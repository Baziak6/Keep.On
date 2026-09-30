# Cooking Mode for Android

A tiny Android app that adds a **Cooking Mode** Quick Settings tile.

## What it does
- One-tap ON/OFF tile in Android Quick Settings.
- While ON, keeps the current recipe/app available without the phone locking.
- After 30 seconds, displays a full black cover (especially power-efficient on OLED screens).
- Touch or swipe anywhere on the black screen to reveal the recipe immediately.
- Automatically turns Cooking Mode OFF after 1 hour.
- Turning it OFF restores normal phone behavior; it never changes your PIN/password/biometrics or Android lock-screen configuration.

## Why it uses a black cover instead of disabling Android's lock screen
Normal third-party Android apps are not allowed to temporarily disable a secure PIN/password/pattern keyguard. Cooking Mode therefore keeps the device awake and uses a black overlay to mimic screen-off while preserving instant access to the recipe.

## First-time setup
1. Install and open **Cooking Mode**.
2. Tap **Allow display over other apps** and enable the permission.
3. Tap **Add Quick Settings tile** (Android 13+) or pull down Quick Settings > Edit and add **Cooking Mode** manually.
4. Open your recipe and tap the Cooking Mode tile.

## Build
Open this repository in a current Android Studio, allow Gradle sync, then Build > Build APK(s).

- Min Android: 8.0 (API 26)
- Target/compile SDK: 35
- Package: `com.example.cookingmode`

## GitHub upload
Upload the complete repository contents. `.gitignore` is included; generated build files should not be committed.
