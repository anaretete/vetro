<div align="center">

  <h1>Vetro</h1>
  <p><strong>The Liquid Glass Manga & Comic Reader for Android</strong></p>

  <p>
    <a href="https://github.com/anaretete/komikku-liquid-glass/releases/latest">
      <img src="https://img.shields.io/badge/Latest_Release-v1.14.67-0A84FF?style=for-the-badge&logo=android&logoColor=white" alt="Latest Release" />
    </a>
    <a href="https://github.com/anaretete/komikku-liquid-glass/actions">
      <img src="https://img.shields.io/badge/Build-Passing-30D158?style=for-the-badge&logo=githubactions&logoColor=white" alt="Build Status" />
    </a>
    <a href="LICENSE">
      <img src="https://img.shields.io/badge/License-Apache_2.0-BF5AF2?style=for-the-badge" alt="License" />
    </a>
    <img src="https://img.shields.io/badge/Android-8.0%2B-FF9F0A?style=for-the-badge&logo=android" alt="Android 8.0+" />
  </p>

</div>

---

## Overview

**Vetro** is a high-performance, open-source manga, comic, and webtoon reader for Android. Built upon the robust foundation of Tachiyomi, Mihon, and TachiyomiSY, Vetro introduces a state-of-the-art **Liquid Glass UI engine** that brings dynamic optical refraction, caustic reflections, chromatic aberration, and tactile spring physics to every corner of the interface.

Whether browsing expansive libraries, tracking reading progress, or immersing yourself in high-resolution chapters, Vetro delivers an authentic, fluid experience designed with continuous-curvature Apple HIG aesthetics and cutting-edge Android Graphics Shading Language (AGSL) shaders.

---

## Key Features

### 🔮 Liquid Glass Optical Engine
- **AGSL Refraction Shaders:** Authentic signed distance field (SDF) optical simulation with real-time caustics and edge light refraction.
- **Chromatic Aberration:** Physically modeled RGB light dispersion along curved glass borders.
- **LayerBackdrop Architecture:** Hardware-accelerated offscreen sampling allows smooth 120 FPS glass rendering without UI thread hitching or recursive canvas captures.
- **Viscoelastic Spring Physics:** Tactile button and slider interactions with natural velocity squish, stretch, and spring release.

### 📖 Superior Reader Experience
- **Reading Modes:** Seamless Webtoon reader with automatic detection, Continuous Vertical, Left-to-Right, Right-to-Left, and Double Page viewers.
- **Refractive HUD:** Frosted liquid glass top and bottom bars that keep the manga artwork visible underneath with minimal obstruction.
- **True Color Filter Refraction:** Reading filters (color overlay, brightness reduction, grayscale, and inversion) cleanly refract through glass overlays without hue clipping.
- **Smooth Scrubbing Slider:** 1:1 proportional page tracking with continuous tactile engagement.

### 🎨 Cupertino & Apple HIG Design System
- **Floating Pill Navigation:** Ergonomic floating navigation capsules with continuous squircle corners (`RoundedRectangle` with continuous styling).
- **Segmented Selectors:** Multi-section selectors featuring a refractive glass track and a solid theme-matched sliding indicator.
- **Accessible Contrast:** Rigorous dynamic color contrast ensures no light-on-light or dark-on-dark text across both dark and light modes.
- **Lucide Iconography:** Clean, modern vector icon set across all menus and preference pages.

---

## Downloads

Download the latest APK releases directly from [GitHub Releases](https://github.com/anaretete/komikku-liquid-glass/releases/latest):

| Architecture | Description | Download |
|:---|:---|:---:|
| **ARM64-v8a** | Optimized for modern 64-bit Android smartphones & tablets *(Recommended)* | [Download APK](https://github.com/anaretete/komikku-liquid-glass/releases/latest) |
| **Universal** | Contains binaries for all CPU architectures (ARM64, ARMv7, x86_64, x86) | [Download APK](https://github.com/anaretete/komikku-liquid-glass/releases/latest) |

*System Requirements: Android 8.0 (Oreo) or higher. Android 13+ recommended for full AGSL hardware shader acceleration.*

---

## Building from Source

### Prerequisites
- Android Studio Ladybug (2024.2.1+) or newer
- JDK 17 or higher
- Android SDK 35

### Clone & Compile
```bash
# Clone the repository
git clone https://github.com/anaretete/komikku-liquid-glass.git
cd komikku-liquid-glass

# Build the release APK
./gradlew assembleStandardRelease
```

The output APKs will be located in:
```
app/build/outputs/apk/standard/release/
```

---

## Credits & Acknowledgments

Vetro is made possible thanks to the incredible open-source community:
- [Mihon](https://github.com/mihonapp/mihon) & [Tachiyomi](https://github.com/tachiyomiorg/tachiyomi) for the foundational manga reader architecture.
- [TachiyomiSY](https://github.com/jobobby04/TachiyomiSY) for rich feed discovery and extension capabilities.
- [Komikku](https://github.com/komikku-app/komikku) for cover-based theming and multi-source features.
- [Kyant0](https://github.com/Kyant0) for pioneering Android AGSL Liquid Glass concepts.
- [Lucide](https://lucide.dev) for the clean vector icon designs.

---

## License

```
Copyright (C) 2026 Vetro Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
