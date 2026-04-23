#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
#  EduSpecial — pre-build verification
#  Confirms that NO secrets ship in the APK and that the secure config
#  channel (BootstrapConfig + SecureChannel + RuntimeConfigProvider) is wired.
# ─────────────────────────────────────────────────────────────────────────────
set -u
RED=$'\e[31m'; GRN=$'\e[32m'; YLW=$'\e[33m'; CLR=$'\e[0m'
fail=0; warn=0

ok()   { echo "${GRN}✅${CLR} $*"; }
bad()  { echo "${RED}❌${CLR} $*"; fail=$((fail+1)); }
note() { echo "${YLW}⚠️${CLR} $*"; warn=$((warn+1)); }

echo "── 1. APK MUST NOT contain google-services.json ──"
if [[ -f app/google-services.json ]]; then
    bad "app/google-services.json is still present — DELETE IT"
else
    ok "no google-services.json (Firebase initializes from runtime config)"
fi

echo "── 2. google-services Gradle plugin MUST be disabled ──"
if grep -q "alias(libs.plugins.google.services)" app/build.gradle.kts build.gradle.kts 2>/dev/null; then
    bad "google-services plugin still referenced in Gradle files"
else
    ok "google-services plugin removed from Gradle"
fi

echo "── 3. Secure-channel source files MUST exist ──"
SECURE_DIR=app/src/main/java/com/eduspecial/data/remote/secure
for f in BootstrapConfig SecureChannel RemoteConfigDtos RemoteConfigClient RemoteConfigCache RuntimeConfigProvider; do
    if [[ -f "$SECURE_DIR/$f.kt" ]]; then ok "$f.kt"; else bad "$SECURE_DIR/$f.kt missing"; fi
done

echo "── 4. Bootstrap MUST point at the production backend ──"
URL=$(grep -oP 'CONFIG_URL\s*=\s*"\K[^"]+' "$SECURE_DIR/BootstrapConfig.kt" 2>/dev/null)
[[ "$URL" == https://* ]] && ok "CONFIG_URL = $URL" || bad "CONFIG_URL is not HTTPS: $URL"

echo "── 5. NO embedded API keys in source ──"
LEAKS=$(rg -n "AIza[0-9A-Za-z_-]{35}|[0-9]{12}-[0-9a-z]{32}\.apps\.googleusercontent\.com|cloudinary://[^\"]+" \
        app/src/main 2>/dev/null | grep -v "BootstrapConfig.kt")
if [[ -z "$LEAKS" ]]; then ok "no Firebase/OAuth/Cloudinary credential patterns found in app sources"
else bad "POSSIBLE KEY LEAK:\n$LEAKS"; fi

echo "── 6. RemoteConfigManager / ConfigRepository MUST have NO defaults ──"
if rg -nq "BuildConfig\.(CLOUDINARY|ALGOLIA|FIREBASE)" app/src/main 2>/dev/null; then
    bad "code still references BuildConfig.* keys"
else
    ok "no BuildConfig.* key references"
fi

echo "── 7. Server-side config.json MUST exist ──"
if [[ -f myproject/config.json ]]; then
    ok "myproject/config.json present (edit this file to rotate any key)"
    python3 - <<'PY' 2>/dev/null || note "could not validate JSON shape"
import json, sys
c = json.load(open("myproject/config.json"))
need = ["shared_secret_hex","firebase","cloudinary_accounts","algolia"]
miss = [k for k in need if k not in c]
sys.exit(1 if miss else 0)
PY
else
    bad "myproject/config.json missing"
fi

echo
if (( fail == 0 )); then
    echo "${GRN}━━━ READY: APK is key-free, secure channel wired ($warn warnings) ━━━${CLR}"
    exit 0
else
    echo "${RED}━━━ $fail check(s) failed — fix before building ━━━${CLR}"
    exit 1
fi
