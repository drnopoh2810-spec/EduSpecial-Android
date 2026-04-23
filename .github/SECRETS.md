# GitHub Actions — Configuration Guide

**🎉 NEW: Firebase Remote Config Integration**

This project now uses **Firebase Remote Config** for all API keys and configuration. No more GitHub Secrets needed (except for google-services.json)!

---

## 🔧 Configuration Method

### Firebase Remote Config (Current)
All API keys are now managed in Firebase Console:
- **Cloudinary accounts** (all 6 accounts)
- **Algolia search keys**
- **Other configuration values**

**Benefits:**
- ✅ No GitHub Secrets management
- ✅ Real-time configuration updates
- ✅ No CI/CD configuration needed
- ✅ Secure and encrypted storage

### Setup Instructions
1. Go to [Firebase Console → Remote Config](https://console.firebase.google.com/u/0/project/eduspecial/config)
2. Add the required keys (see `FIREBASE_REMOTE_CONFIG_SETUP.md`)
3. Publish changes
4. Done! 🎉

---

## 📁 Required Files

### Firebase Configuration
| File | Location | Description |
|------|----------|-------------|
| `google-services.json` | `app/google-services.json` | Firebase project configuration (committed to repo) |

**Note:** This is the ONLY file needed for builds. All API keys come from Firebase Remote Config.

---

## 🔑 Legacy Secrets (No longer needed)

~~The following secrets are no longer required thanks to Firebase Remote Config:~~

| ~~Secret Name~~ | ~~Description~~ | **New Location** |
|-------------|-------------|------------------|
| ~~`CLOUDINARY_CLOUD_NAME`~~ | ~~Cloudinary cloud name~~ | **Firebase Remote Config** |
| ~~`CLOUDINARY_UPLOAD_PRESET`~~ | ~~Cloudinary upload preset~~ | **Firebase Remote Config** |
| ~~`CLOUDINARY_CLOUD_NAME_2-6`~~ | ~~Secondary accounts~~ | **Firebase Remote Config** |
| ~~`ALGOLIA_APP_ID`~~ | ~~Algolia Application ID~~ | **Firebase Remote Config** |
| ~~`ALGOLIA_SEARCH_KEY`~~ | ~~Algolia Search key~~ | **Firebase Remote Config** |

---

## 🚀 Build Process

### GitHub Actions Workflow
1. **Checkout code** from repository
2. **Verify Firebase** configuration (google-services.json)
3. **Create minimal local.properties** (SDK path only)
4. **Build APK** - all configuration fetched from Firebase at runtime
5. **Create release** with auto-versioning

### Local Development
1. Clone repository
2. Open in Android Studio
3. Build and run - configuration loaded from Firebase automatically

**No setup required!** 🎉

---

## 🌿 Branch Strategy

```
main          ← stable, production releases (auto-versioned)
develop       ← integration branch (nightly builds)
feature/xxx   ← feature branches (CI runs on push)
fix/xxx       ← bugfix branches (CI runs on push)
```

## 🏷 Auto-Versioning

Version format: `1.0.<commit_count>`
- Every commit to `main` automatically increments version
- No manual tagging needed
- Automatic GitHub releases with APK

---

## 📊 Workflow Summary

| Workflow | Trigger | Purpose | Configuration |
|----------|---------|---------|---------------|
| `build.yml` | push to main/PR | Build APK + Auto Release | Firebase Remote Config |

**Simple and clean!** 🧹
