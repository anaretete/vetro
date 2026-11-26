# Contributing to Vetro

Thanks for considering contributing to Vetro!

## Code Contributions

Pull requests are always welcome.

If you are planning to work on an issue or build a new feature, feel free to open or comment on an issue first to discuss the approach.

### Requirements

- Familiarity with Android development and Kotlin
- Android Studio Ladybug or newer
- An Android device or emulator running Android 8.0+

### Development Workflow

1. Fork and clone the repository:
   ```bash
   git clone https://github.com/anaretete/vetro.git
   cd vetro
   ```
2. Create a feature branch:
   ```bash
   git checkout -b feature/my-feature
   ```
3. Test your changes locally on an emulator or test device:
   ```bash
   ./gradlew assembleStandardDebug
   ```
4. Push your branch and open a pull request.

---

## Translations

Translations are managed alongside upstream sources via Weblate. Feel free to submit translations or fixes for string resources in `i18n`.

---

## Forks & License

Vetro is licensed under the Apache 2.0 license. Forks are welcome under the terms of the license. Please remember to update the app name, icon, and application ID if distributing your own builds.
