#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────────
# EduSpecial — pre-build connection & integrity verification
# Run this BEFORE `./gradlew assembleDebug` to make sure every integration
# (Firebase, Cloudinary, Algolia, Flask backend, embedded keys) is wired up.
# ─────────────────────────────────────────────────────────────────────────────
set -u
PASS="✅"; FAIL="❌"; WARN="⚠️ "
errors=0; warnings=0

echo "🔍 EduSpecial — Pre-Build Verification"
echo "======================================="

check_file() {
    if [ -f "$1" ]; then echo "$PASS $1"
    else echo "$FAIL  MISSING: $1"; errors=$((errors+1)); fi
}

grep_in() {
    # grep_in <file> <pattern> <label>
    if grep -q "$2" "$1" 2>/dev/null; then echo "$PASS $3"
    else echo "$FAIL  $3 (pattern not found in $1)"; errors=$((errors+1)); fi
}

echo
echo "1) Required files"
check_file app/google-services.json
check_file app/build.gradle.kts
check_file myproject/app.py
check_file myproject/serviceAccountKey.json
check_file app/src/main/java/com/eduspecial/data/remote/config/RemoteConfigManager.kt
check_file app/src/main/java/com/eduspecial/di/NetworkModule.kt

echo
echo "2) Firebase project consistency"
if [ -f app/google-services.json ]; then
    pkg=$(grep -o '"package_name":[^,}]*' app/google-services.json | head -1)
    proj=$(grep -o '"project_id":[^,}]*' app/google-services.json | head -1)
    echo "   $pkg"
    echo "   $proj"
    grep_in app/google-services.json '"project_id": "eduspecial"' "Firebase project_id = eduspecial"
    grep_in app/google-services.json '"package_name": "com.eduspecial.app"' "Android package = com.eduspecial.app"
fi

echo
echo "3) Embedded keys (no build-time secrets needed)"
RC=app/src/main/java/com/eduspecial/data/remote/config/RemoteConfigManager.kt
for i in 1 2 3 4 5 6; do
    grep_in "$RC" "cloudinary_cloud_name_$i" "Cloudinary account #$i embedded"
done
grep_in "$RC" 'algolia_app_id' 'Algolia app_id embedded'
grep_in "$RC" 'algolia_search_key' 'Algolia search key embedded'
grep_in "$RC" 'backend_base_url' 'Backend base URL embedded'

echo
echo "4) HuggingFace decoupling"
if grep -rq "hf.space\|huggingface" app/src/main 2>/dev/null; then
    echo "$FAIL  Found HuggingFace references in app/src/main"
    errors=$((errors+1))
else
    echo "$PASS No HuggingFace references in app sources"
fi
if grep -q "BASE_API_URL\|APP_API_KEY" app/build.gradle.kts 2>/dev/null; then
    echo "$WARN build.gradle.kts still references HF build vars"
    warnings=$((warnings+1))
else
    echo "$PASS build.gradle.kts clean of HF build vars"
fi

echo
echo "5) Server-side admin key safety"
if grep -rq "BEGIN PRIVATE KEY" app/src 2>/dev/null; then
    echo "$FAIL  Service-account private key found inside Android sources!"
    errors=$((errors+1))
else
    echo "$PASS No admin private keys leaked into app/ sources"
fi
check_file myproject/serviceAccountKey.json

echo
echo "6) Flask backend health (optional — only if running)"
HEALTH_URL="${REPLIT_DEV_DOMAIN:+https://$REPLIT_DEV_DOMAIN}/login"
if [ -n "${REPLIT_DEV_DOMAIN:-}" ]; then
    if curl -fsS -o /dev/null -m 5 "$HEALTH_URL"; then
        echo "$PASS Flask backend reachable at $HEALTH_URL"
    else
        echo "$WARN Flask backend not responding (workflow may be stopped)"
        warnings=$((warnings+1))
    fi
else
    echo "$WARN REPLIT_DEV_DOMAIN not set — skipping live health check"
fi

echo
echo "======================================="
if [ $errors -eq 0 ]; then
    echo "$PASS All critical checks passed ($warnings warnings)."
    echo "    You can now run:  ./gradlew assembleDebug"
    exit 0
else
    echo "$FAIL  $errors critical issue(s), $warnings warning(s). Fix before building."
    exit 1
fi
