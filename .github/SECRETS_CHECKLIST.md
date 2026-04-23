# ✅ قائمة التحقق من GitHub Secrets

استخدم هذه القائمة للتأكد من إضافة جميع المفاتيح المطلوبة قبل البناء.

## 🔴 إلزامي (يجب إضافتها)

- [ ] `CLOUDINARY_CLOUD_NAME` - اسم حساب Cloudinary الأساسي
- [ ] `CLOUDINARY_UPLOAD_PRESET` - Upload preset للحساب الأساسي

## 🟡 اختياري (موصى به)

### Cloudinary Failover (للتبديل التلقائي عند الوصول للحد الأقصى)
- [ ] `CLOUDINARY_CLOUD_NAME_2` + `CLOUDINARY_UPLOAD_PRESET_2`
- [ ] `CLOUDINARY_CLOUD_NAME_3` + `CLOUDINARY_UPLOAD_PRESET_3`
- [ ] `CLOUDINARY_CLOUD_NAME_4` + `CLOUDINARY_UPLOAD_PRESET_4`

### Algolia Search (يوجد fallback إلى البحث المحلي)
- [ ] `ALGOLIA_APP_ID`
- [ ] `ALGOLIA_SEARCH_KEY`

## 🟢 تلقائي (لا يحتاج إعداد)

- ✅ Firebase - يستخدم `app/google-services.json` الموجود في المشروع

---

## 📖 للحصول على الدليل الكامل

راجع ملف [`SERVICES_SETUP.md`](../SERVICES_SETUP.md) للحصول على:
- شرح تفصيلي لكل خدمة
- كيفية الحصول على المفاتيح
- آليات Fallback
- استكشاف الأخطاء
- التحقق من صحة الإعداد

---

## 🚀 كيفية إضافة Secret

1. اذهب إلى: `Repository → Settings → Secrets and variables → Actions`
2. اضغط: `New repository secret`
3. أدخل:
   - **Name**: اسم المفتاح (مثال: `CLOUDINARY_CLOUD_NAME`)
   - **Value**: القيمة الفعلية (مثال: `dxyz123abc`)
4. اضغط: `Add secret`

---

## ✅ التحقق من الإعداد

بعد إضافة الـ Secrets:

1. ادفع أي تغيير إلى `main`:
   ```bash
   git commit --allow-empty -m "test: verify secrets"
   git push origin main
   ```

2. اذهب إلى: `Actions → أحدث workflow run`

3. تحقق من خطوة **"Verify Required Secrets"**:
   - ✅ أخضر = جميع المفاتيح الإلزامية موجودة
   - ⚠️ أصفر = بعض المفاتيح الاختيارية مفقودة (التطبيق سيعمل)
   - ❌ أحمر = مفاتيح إلزامية مفقودة (البناء سيفشل)

---

## 🎯 الحد الأدنى للعمل

التطبيق يحتاج **فقط** هذين المفتاحين للعمل:
- `CLOUDINARY_CLOUD_NAME`
- `CLOUDINARY_UPLOAD_PRESET`

باقي المفاتيح اختيارية وتحسن الأداء والموثوقية.
