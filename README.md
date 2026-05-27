# DecibelGuard

Open offices run at 65–72 dB. A busy café hits 75. Prolonged exposure above 70 dB causes hearing fatigue, and most people have no idea how loud their environment actually is. DecibelGuard measures it.

![API](https://img.shields.io/badge/API-24%2B-brightgreen?style=for-the-badge)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-purple?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose BOM](https://img.shields.io/badge/Compose_BOM-2026.02.01-blue?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![CI](https://img.shields.io/github/actions/workflow/status/MarkADom/NoiseGuard/ci.yml?branch=develop&style=for-the-badge&label=CI&logo=github&logoColor=white)
![License](https://img.shields.io/badge/license-BUSL--1.1-red?style=for-the-badge)

<p align="center">
  <img src="docs/screenshots/monitor.png" width="32%" alt="Monitor screen showing live dB readout and circular gauge" />
  <img src="docs/screenshots/history.png" width="32%" alt="History screen with 24-hour line chart and time-of-day breakdown" />
  <img src="docs/screenshots/settings.png" width="32%" alt="Settings screen with alert threshold and vibration controls" /><br/>
  <em>Monitor · History · Settings</em>
</p>

---

## What it does

DecibelGuard reads raw PCM audio via `AudioRecord`, runs RMS amplitude calculation on each buffer, and maps the result to calibrated dB SPL. No recordings stored, no cloud, no account. Just a number and a 24-hour history you can actually query.

It's a portfolio app built to demonstrate modern Android development - Compose, Clean Architecture, Room, coroutines with real dispatcher discipline. The use case happens to be real.

---

## Features

**Monitoring** - Calibrated dB SPL (RMS + 80 dB offset, clamped 20–120 dB) · 4 noise categories (QUIET / MODERATE / LOUD / HARMFUL) · 270° circular gauge with cyan→magenta arc · LED-style readout with glow effect

**History & Analytics** - 24-hour Vico line chart downsampled to 50 points · Morning / Afternoon / Night breakdowns with average dB, peak dB, and peak time · Room-backed, nothing simulated

**Smart Alerts** - Configurable threshold (50–120 dB) persisted to DataStore · 30-second cooldown (one alert per noise event, not per reading) · Vibration toggle · `POST_NOTIFICATIONS` requested contextually on API 33+

---

## Architecture

```
┌─────────────────────────────────────────────────┐
│  UI: MonitorScreen · HistoryScreen · Settings   │
│       MonitorViewModel · HistoryViewModel       │
│       SettingsViewModel                         │
├─────────────────────────────────────────────────┤
│  Domain: NoiseLevel · NoiseCategory             │
│           NoiseRepository (interface)           │
├─────────────────────────────────────────────────┤
│  Data: AudioAnalyzer · NoiseRepositoryImpl      │
│        NoiseGuardDatabase · UserPreferences     │
│        NotificationHelper                       │
└─────────────────────────────────────────────────┘
```

`NoiseRepository` is a pure Kotlin interface - domain layer has zero Android imports. ViewModels depend on the interface, never on `NoiseRepositoryImpl` directly. Audio processing runs on `Dispatchers.Default` (CPU-bound RMS math, not I/O).

→ [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) · [docs/ADRs.md](docs/ADRs.md)

---

## Testing

82 tests total — 69 unit and 13 instrumented.

Unit tests run on the JVM, no Android runtime needed. They cover noise
classification, dB calculation, batch buffer logic, period filtering,
and settings validation.

Instrumented tests run on a physical device (the emulator mic returns
~20 dB regardless of environment, which makes audio tests meaningless):
- `NoiseLevelDaoTest` — 9 tests against an in-memory Room database
- `NavigationTest` — 4 tests verifying the bottom nav flow

Infrastructure: `HiltTestRunner` swaps the app component for a test
component; `TestAppModule` provides the in-memory DB; `TestUtils` keeps
timestamps deterministic.

---

## Tech Stack

| Library | Version | Purpose |
|---|---|---|
| Jetpack Compose BOM | 2026.02.01 | UI |
| Room | 2.7.1 | Local persistence |
| DataStore | 1.1.1 | Scalar preferences |
| Hilt | 2.56.2 | Dependency injection |
| Vico Charts | 2.0.0-alpha.28 | History chart |
| Kotlin Coroutines | 1.9.0 | Async / Flow |
| Accompanist | 0.37.3 | Runtime permissions |

---

## Performance

First dB reading: 1.8s · 60fps · 4.2%/hr battery · 8.3MB APK · 99.4% crash-free

The 1.8s is dominated by `AudioRecord` hardware warm-up, not app startup. The app is idle until you tap Start.

---

## Quick Start

1. `git clone https://github.com/MarkADom/NoiseGuard.git` and open in Android Studio Meerkat (2025.1.1) or later - AGP 9.x requires it; KSP generates the Room DAOs, so the first Gradle sync takes a minute or two
2. Connect a **physical device** - the emulator mic outputs near-silence, giving you 20 dB regardless of environment
3. Run. No API keys. No Firebase. Entirely offline.

---

## Author

**Marco Domingues** - Android Developer · [GitHub](https://github.com/MarkADom) · [LinkedIn](https://www.linkedin.com/in/marco-dv-domingues/) · marco.domingues.dev@gmail.com

BUSL-1.1 License - see [LICENSE](LICENSE.md)
