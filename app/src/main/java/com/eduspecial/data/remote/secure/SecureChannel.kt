package com.eduspecial.data.remote.secure

import android.util.Base64
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Crypto primitives shared with myproject/secure_channel.py.
 *
 *   sign(method, path, ts, nonce) → hex( HMAC_SHA256(secret, "method\npath\nts\nnonce") )
 *
 *   key  = HKDF-SHA256(secret, salt = 8-byte BE(server_ts_ms), info = "eduspecial-config-v1", L = 32)
 *   plaintext = AES-GCM(key, iv).decrypt(ct || tag)
 */
object SecureChannel {

    private val rng = SecureRandom()

    // ── Hex helpers ─────────────────────────────────────────────────────────
    fun hexToBytes(hex: String): ByteArray {
        require(hex.length % 2 == 0) { "invalid hex length" }
        val out = ByteArray(hex.length / 2)
        for (i in out.indices) {
            out[i] = ((Character.digit(hex[2 * i], 16) shl 4)
                    or Character.digit(hex[2 * i + 1], 16)).toByte()
        }
        return out
    }

    fun bytesToHex(b: ByteArray): String {
        val sb = StringBuilder(b.size * 2)
        for (x in b) sb.append(String.format("%02x", x))
        return sb.toString()
    }

    // ── Random nonce ────────────────────────────────────────────────────────
    fun randomNonceHex(bytes: Int = 16): String {
        val n = ByteArray(bytes)
        rng.nextBytes(n)
        return bytesToHex(n)
    }

    // ── HMAC-SHA256 request signing ─────────────────────────────────────────
    fun sign(method: String, path: String, tsMs: Long, nonce: String, secretHex: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(hexToBytes(secretHex), "HmacSHA256"))
        val msg = "$method\n$path\n$tsMs\n$nonce".toByteArray(Charsets.UTF_8)
        return bytesToHex(mac.doFinal(msg))
    }

    // ── HKDF-SHA256 ─────────────────────────────────────────────────────────
    private fun hkdf(secret: ByteArray, salt: ByteArray, info: ByteArray, length: Int): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        // Extract
        mac.init(SecretKeySpec(salt, "HmacSHA256"))
        val prk = mac.doFinal(secret)
        // Expand
        mac.init(SecretKeySpec(prk, "HmacSHA256"))
        val out = ByteArray(length)
        var t = ByteArray(0)
        var pos = 0
        var counter: Byte = 1
        while (pos < length) {
            mac.reset()
            mac.update(t)
            mac.update(info)
            mac.update(byteArrayOf(counter))
            t = mac.doFinal()
            val take = minOf(t.size, length - pos)
            System.arraycopy(t, 0, out, pos, take)
            pos += take
            counter = (counter + 1).toByte()
        }
        return out
    }

    // ── AES-GCM decrypt for the config response ─────────────────────────────
    fun decryptConfigResponse(
        ivB64: String,
        ctB64: String,
        tagB64: String,
        serverTsMs: Long,
        secretHex: String
    ): ByteArray {
        val iv  = Base64.decode(ivB64,  Base64.DEFAULT)
        val ct  = Base64.decode(ctB64,  Base64.DEFAULT)
        val tag = Base64.decode(tagB64, Base64.DEFAULT)

        val salt = ByteBuffer.allocate(8).putLong(serverTsMs).array()
        val key  = hkdf(
            secret = hexToBytes(secretHex),
            salt   = salt,
            info   = BootstrapConfig.HKDF_INFO.toByteArray(Charsets.UTF_8),
            length = 32
        )

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(key, "AES"),
            GCMParameterSpec(128, iv)
        )
        // Java AES-GCM expects ciphertext || tag concatenated
        val combined = ByteArray(ct.size + tag.size).also {
            System.arraycopy(ct,  0, it, 0,         ct.size)
            System.arraycopy(tag, 0, it, ct.size,   tag.size)
        }
        return cipher.doFinal(combined)
    }
}
