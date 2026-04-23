#!/bin/bash

# Script to check if BuildConfig has been generated with correct values

echo "🔍 Checking BuildConfig status..."
echo ""

BUILDCONFIG_PATH="app/build/generated/source/buildConfig/debug/com/eduspecial/BuildConfig.java"

if [ ! -f "$BUILDCONFIG_PATH" ]; then
    echo "❌ BuildConfig.java NOT FOUND"
    echo ""
    echo "This means the app has not been built yet."
    echo ""
    echo "📝 To fix:"
    echo "1. Open project in Android Studio"
    echo "2. Build → Clean Project"
    echo "3. Build → Rebuild Project"
    echo "4. Run the app"
    echo ""
    exit 1
fi

echo "✅ BuildConfig.java found"
echo ""
echo "📋 Checking Cloudinary configuration..."
echo ""

# Check for CLOUDINARY_CLOUD_NAME
if grep -q "CLOUDINARY_CLOUD_NAME.*=.*\"ddh0htmsd\"" "$BUILDCONFIG_PATH"; then
    echo "✅ CLOUDINARY_CLOUD_NAME = ddh0htmsd (correct)"
elif grep -q "CLOUDINARY_CLOUD_NAME.*=.*\"your_cloud_name\"" "$BUILDCONFIG_PATH"; then
    echo "❌ CLOUDINARY_CLOUD_NAME = your_cloud_name (default value - NOT configured)"
    echo ""
    echo "📝 To fix:"
    echo "1. Make sure local.properties has: CLOUDINARY_CLOUD_NAME=ddh0htmsd"
    echo "2. In Android Studio: File → Sync Project with Gradle Files"
    echo "3. Build → Rebuild Project"
    echo ""
    exit 1
else
    echo "⚠️  CLOUDINARY_CLOUD_NAME found but value unclear"
    grep "CLOUDINARY_CLOUD_NAME" "$BUILDCONFIG_PATH"
fi

# Check for CLOUDINARY_UPLOAD_PRESET
if grep -q "CLOUDINARY_UPLOAD_PRESET.*=.*\"eduspecial_preset\"" "$BUILDCONFIG_PATH"; then
    echo "✅ CLOUDINARY_UPLOAD_PRESET = eduspecial_preset (correct)"
else
    echo "⚠️  CLOUDINARY_UPLOAD_PRESET value unclear"
    grep "CLOUDINARY_UPLOAD_PRESET" "$BUILDCONFIG_PATH"
fi

echo ""
echo "🎉 BuildConfig looks good!"
echo ""
echo "If you still get 'No Cloudinary accounts configured' error:"
echo "1. Uninstall the app from your device/emulator"
echo "2. In Android Studio: Build → Clean Project"
echo "3. Build → Rebuild Project"
echo "4. Run → Run 'app'"
echo ""
