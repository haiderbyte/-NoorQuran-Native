# مصحف النور — NoorQuran Native

تطبيق Android أصلي بـ Kotlin وJetpack Compose لقراءة القرآن الكريم والتفسير الميسر. يتضمن قاعدة بيانات محلية للاستخدام دون اتصال.

## المتطلبات

- Android Studio.
- JDK 17.
- Android SDK 35 (minSdk 24).
- Gradle Wrapper 8.9 المرفق.

## التثبيت والتشغيل

```bash
git clone https://github.com/haiderbyte/-NoorQuran-Native.git
cd -NoorQuran-Native
./gradlew assembleDebug
```

أو افتح مجلد المشروع في Android Studio، ثبّت Android SDK 35 من SDK Manager، ثم نفّذ **Sync Project with Gradle Files** واضغط **Run** على محاكي أو جهاز Android.

بعد نجاح بناء debug ستجد الملف في `app/build/outputs/apk/debug/app-debug.apk`. لا يحتاج التطبيق إلى Node.js أو مفتاح Gemini؛ خطوات AI Studio القديمة لم تكن تخص مشروع Android هذا. لم يُتحقق من بناء APK في بيئة الفحص لغياب Android SDK.
