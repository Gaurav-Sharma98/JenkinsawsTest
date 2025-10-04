# VehicleAlert Android App

Features:
- Register owner and vehicle (plate)
- Capture image, OCR plate via ML Kit
- Match plate in Room DB, notify owner via SMS/WhatsApp and call fallback
- Request runtime permissions for camera, location, SMS, and call

Build:
- Open `android-app/` in Android Studio Ladybug+ and sync Gradle.
- Run on device with Google Play Services and camera.

Notes:
- SMS and CALL require runtime permissions; on Android 11+ you may need to use default SMS app or explicit user action for sending messages.
- WhatsApp share uses `wa.me` deep link.
