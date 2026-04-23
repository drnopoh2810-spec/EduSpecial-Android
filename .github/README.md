# GitHub Actions - EduSpecial

## 📋 نظرة عامة

يحتوي هذا المجلد على إعدادات GitHub Actions للبناء التلقائي ونشر التطبيق.

## 🔄 Workflows

### [`build.yml`](workflows/build.yml) - البناء والنشر التلقائي

يتم تشغيله عند:
- Push إلى `main` branch
- Pull Request إلى `main` branch

#### الخطوات:

1. **Checkout code** - تحميل الكود
2. **Setup JDK 17** - تثبيت Java
3. **Setup Android SDK** - تثبيت Android SDK
4. **Grant execute permission** - إعطاء صلاحيات لـ gradlew
5. **✨ Verify Required Secrets** - التحقق من المفاتيح المطلوبة
   - يتحقق من وجود جميع المفاتيح الإلزامية
   - يعرض تحذيرات للمفاتيح الاختيارية المفقودة
   - يفشل البناء إذا كانت المفاتيح الإلزامية مفقودة
6. **Create local.properties** - إنشاء ملف المفاتيح
7. **Generate version number** - توليد رقم الإصدار تلقائياً
8. **Build Release APK** - بناء ملف APK
9. **Rename APK** - إعادة تسمية الملف بالإصدار
10. **Create GitHub Release** - نشر الإصدار (فقط على `main`)

#### الإصدارات التلقائية

يتم توليد رقم الإصدار تلقائياً بناءً على عدد الـ commits:

```
Version: 1.0.<commit_count>
Tag: v1.0.<commit_count>
```

**مثال:**
- Commit #1 → `v1.0.1`
- Commit #50 → `v1.0.50`
- Commit #100 → `v1.0.100`

كل push إلى `main` ينشئ إصدار جديد تلقائياً!

## 🔐 المفاتيح المطلوبة (Secrets)

راجع [`SECRETS_CHECKLIST.md`](SECRETS_CHECKLIST.md) للحصول على قائمة كاملة.

### الحد الأدنى (إلزامي)
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_UPLOAD_PRESET`

### اختياري (موصى به)
- `CLOUDINARY_CLOUD_NAME_2` + `CLOUDINARY_UPLOAD_PRESET_2`
- `CLOUDINARY_CLOUD_NAME_3` + `CLOUDINARY_UPLOAD_PRESET_3`
- `CLOUDINARY_CLOUD_NAME_4` + `CLOUDINARY_UPLOAD_PRESET_4`
- `ALGOLIA_APP_ID`
- `ALGOLIA_SEARCH_KEY`

## 📖 الأدلة

- [`SECRETS_CHECKLIST.md`](SECRETS_CHECKLIST.md) - قائمة تحقق سريعة
- [`../SERVICES_SETUP.md`](../SERVICES_SETUP.md) - دليل شامل لإعداد الخدمات

## 🎯 الاستخدام

### للمطورين

1. **Fork المشروع**
2. **أضف Secrets المطلوبة** (راجع SECRETS_CHECKLIST.md)
3. **Push إلى main**
4. **تحقق من Actions** للتأكد من نجاح البناء
5. **حمّل APK** من Releases

### للمساهمين

عند إنشاء Pull Request:
- يتم بناء التطبيق تلقائياً
- لا يتم نشر إصدار (فقط اختبار البناء)
- تحقق من نجاح البناء قبل الدمج

## 🔍 استكشاف الأخطاء

### البناء فشل في "Verify Required Secrets"

**السبب:** مفاتيح إلزامية مفقودة

**الحل:**
1. اقرأ رسالة الخطأ في Actions log
2. أضف المفاتيح المفقودة إلى Repository Secrets
3. أعد تشغيل الـ workflow

### البناء نجح لكن التطبيق لا يرفع الصور

**السبب:** مفاتيح Cloudinary غير صحيحة

**الحل:**
1. تحقق من صحة `CLOUDINARY_CLOUD_NAME` و `CLOUDINARY_UPLOAD_PRESET`
2. تأكد من أن Upload Preset موجود في Cloudinary Dashboard
3. تأكد من أن Signing Mode = `Unsigned`

### البحث لا يعمل

**السبب:** مفاتيح Algolia مفقودة أو غير صحيحة

**الحل:**
- لا تقلق! التطبيق يستخدم البحث المحلي تلقائياً
- إذا أردت تفعيل Algolia، أضف `ALGOLIA_APP_ID` و `ALGOLIA_SEARCH_KEY`

## 📊 الحالة

| المكون | الحالة | ملاحظات |
|--------|--------|---------|
| Auto Build | ✅ يعمل | على كل push |
| Auto Release | ✅ يعمل | فقط على main |
| Secrets Verification | ✅ يعمل | يتحقق من المفاتيح |
| Version Auto-increment | ✅ يعمل | بناءً على commits |

## 🚀 التحسينات المستقبلية

- [ ] إضافة اختبارات تلقائية قبل البناء
- [ ] إضافة تحليل الكود (lint)
- [ ] إضافة اختبار الاتصال بالخدمات
- [ ] دعم multiple build variants (debug, staging, release)
- [ ] إضافة changelog تلقائي

---

**للمزيد من المعلومات:** راجع [`SERVICES_SETUP.md`](../SERVICES_SETUP.md)
