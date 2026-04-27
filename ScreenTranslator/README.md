# Screen Translator / مترجم الشاشة

## وصف التطبيق | Application Description

### العربية
تطبيق أندرويد يتيح لك ترجمة أي نص على الشاشة بسهولة. مثالي لترجمة الترجمات المدمجة في الفيديوهات أو أي نص على الشاشة.

**المميزات:**
- زر عائم يعمل فوق جميع التطبيقات
- التعرف التلقائي على لغة النص (OCR)
- الترجمة الفورية إلى 4 لغات: العربية، الإنجليزية، التركية، والفارسية
- تحديد منطقة النص بسهولة
- نسخ النص المترجم مباشرة

### English
An Android app that allows you to translate any text on your screen easily. Perfect for translating embedded subtitles in videos or any on-screen text.

**Features:**
- Floating button that works over all apps
- Automatic text language recognition (OCR)
- Instant translation to 4 languages: Arabic, English, Turkish, and Persian
- Easy text area selection
- Direct copy of translated text

---

## كيفية البناء والتشغيل | Build and Run Instructions

### المتطلبات | Requirements
- Android Studio Arctic Fox or newer
- Android SDK 24 or higher
- Kotlin 1.9.0 or higher
- Gradle 8.1.0 or higher

### خطوات البناء | Build Steps

1. **افتح المشروع في Android Studio | Open the project in Android Studio**
   ```bash
   File > Open > Select ScreenTranslator folder
   ```

2. **مزامنة Gradle | Sync Gradle**
   - انتظر حتى يتم تحميل جميع التبعيات
   - Wait for all dependencies to download

3. **قم بالبناء | Build**
   ```
   Build > Make Project
   ```

4. **قم بالتشغيل | Run**
   - صل جهاز Android أو استخدم المحاكي
   - Connect an Android device or use an emulator
   ```
   Run > Run 'app'
   ```

---

## كيفية الاستخدام | How to Use

### العربية
1. افتح التطبيق واختر لغة الترجمة المطلوبة
2. اضغط على "تشغيل الزر العائم"
3. امنح التطبيق صلاحية العرض فوق التطبيقات الأخرى
4. سيظهر زر عائم أزرق على الشاشة
5. افتح الفيديو أو التطبيق الذي تريد الترجمة منه
6. اضغط على الزر العائم
7. حدد المنطقة التي تحتوي على النص
8. ستظهر الترجمة في نافذة منبثقة
9. يمكنك نسخ النص المترجم

### English
1. Open the app and select your desired translation language
2. Tap "Start Floating Button"
3. Grant the app permission to display over other apps
4. A blue floating button will appear on screen
5. Open the video or app you want to translate from
6. Tap the floating button
7. Select the area containing the text
8. Translation will appear in a popup window
9. You can copy the translated text

---

## الصلاحيات المطلوبة | Required Permissions

- `SYSTEM_ALERT_WINDOW`: لعرض الزر العائم فوق التطبيقات الأخرى
- `FOREGROUND_SERVICE`: لإبقاء الخدمة نشطة
- `INTERNET`: لتحميل نماذج الترجمة

- `SYSTEM_ALERT_WINDOW`: To display floating button over other apps
- `FOREGROUND_SERVICE`: To keep the service running
- `INTERNET`: To download translation models

---

## التقنيات المستخدمة | Technologies Used

- **Kotlin**: لغة البرمجة الأساسية
- **ML Kit**: للتعرف على النص (OCR) والترجمة
- **AndroidX**: للمكتبات الحديثة
- **Coroutines**: للعمليات غير المتزامنة
- **Material Design**: للواجهة الحديثة

---

## ملاحظات مهمة | Important Notes

### العربية
- التطبيق يحتاج إلى اتصال بالإنترنت لأول مرة لتحميل نماذج الترجمة
- بعد التحميل، يمكن استخدام التطبيق دون اتصال بالإنترنت
- للحصول على أفضل النتائج، حدد منطقة واضحة تحتوي على النص فقط
- قد لا يعمل التطبيق بشكل صحيح مع بعض التطبيقات المحمية

### English
- The app needs internet connection for the first time to download translation models
- After downloading, the app can work offline
- For best results, select a clear area containing only text
- The app may not work properly with some protected apps

---

## الترخيص | License

هذا المشروع مفتوح المصدر ومتاح للاستخدام الشخصي والتعليمي.

This project is open source and available for personal and educational use.

---

## المساهمة | Contributing

نرحب بالمساهمات! يرجى فتح issue أو pull request.

Contributions are welcome! Please open an issue or pull request.
