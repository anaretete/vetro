# Vetro

Vetro is a free and open-source manga, comic, and webtoon reader for Android. Built as a fork of Mihon and TachiyomiSY, it features an updated glass-inspired interface, smooth gestures, and flexible reading controls.

[![Latest Release](https://img.shields.io/badge/Release-v1.14.72-blue?style=flat-square)](https://github.com/anaretete/vetro/releases/latest)
[![License](https://img.shields.io/badge/License-Apache_2.0-purple?style=flat-square)](LICENSE)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-green?style=flat-square)](https://github.com/anaretete/vetro/releases)

---

## Features

- **Custom UI & Styling:** Glass-style navigation and toolbars designed to stay readable over artwork without getting in the way.
- **Reading Modes:** Webtoon (vertical scroll), left-to-right, right-to-left, and double-page view.
- **Reading Filters:** Color filters, brightness reduction, grayscale, and invert mode.
- **Library Management:** Custom categories, progress tracking, updates feed, and automatic chapter downloads.
- **Tracker Integration:** Sync reading progress with AniList, MyAnimeList, Kitsu, Shikimori, MangaUpdates, and Bangumi.
- **Backup & Restore:** Compatible with Mihon/Tachiyomi backup files.

---

## Download

Get the latest APKs from the [Releases page](https://github.com/anaretete/vetro/releases/latest):

| Architecture | Description |
|:---|:---|
| **ARM64-v8a** | For modern 64-bit phones and tablets *(recommended)* |
| **Universal** | Works on all supported Android devices |

*Requires Android 8.0 or newer.*

---

## Building

### Requirements
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17+
- Android SDK 35

### Build Commands
```bash
git clone https://github.com/anaretete/vetro.git
cd vetro

# Build release APK
./gradlew assembleStandardRelease
```

Compiled APKs will be in `app/build/outputs/apk/standard/release/`.

---

## Credits

Vetro builds upon the work of several open-source projects:
- [Mihon](https://github.com/mihonapp/mihon) & [Tachiyomi](https://github.com/tachiyomiorg/tachiyomi)
- [TachiyomiSY](https://github.com/jobobby04/TachiyomiSY)
- [Komikku](https://github.com/komikku-app/komikku)
- [Lucide Icons](https://lucide.dev)

---

## License

Vetro is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.
