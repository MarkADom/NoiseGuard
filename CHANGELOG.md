# Changelog

All notable changes to this project will be documented in this file.
Format: [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## [Unreleased]

## [1.1.0] - 2026-05-25

### Added
- Hilt 2.56.2 DI: `@Singleton` repository, `@HiltViewModel` on all ViewModels, `AppModule`
- 13 instrumented tests: `NoiseLevelDaoTest` (9) + `NavigationTest` (4)
- GitHub Actions CI/CD with green badge — unit tests on every push
- `HiltTestRunner`, `TestAppModule` (in-memory Room), `TestUtils` for test infrastructure

### Fixed
- Multiple-ViewModel buffer bug: three independent `NoiseRepositoryImpl` instances
  caused clearing history while monitoring to silently fail
- `ActivityInvoker` ClassNotFoundException on instrumented test teardown

## [1.0.2] - 2026-05-24

### Fixed
- Permission flow: app now requests microphone and notification permissions
  on first launch via system dialog (previously showed blocked screen immediately)
- Microphone unavailable toast appeared incorrectly when tapping STOP MONITORING
- Version number in Settings was hardcoded as 1.0.0 (now uses BuildConfig.VERSION_NAME)
- Notifications toggle showed ON even when Android notification permission was OFF

## [1.0.1] - 2026-05-23

### Fixed
- License updated from CC BY-NC to BUSL-1.1
- README: Hilt added to tech stack, Accompanist version corrected, Android Studio
  version updated to Meerkat (2025.1.1)
- ARCHITECTURE.md: removed incorrect "No Hilt" section, replaced with actual DI setup
- ADRs renumbered (clean sequence), ADR-004 documents Hilt decision
- Calibration offset corrected to 80 dB throughout docs

## [1.0.0] - 2026-05-19

### Core
- Real-time dB SPL monitoring via `AudioRecord` (RMS formula, 20–120 dB range)
- 4 noise categories: QUIET · MODERATE · LOUD · HARMFUL
- Calibrated dB calculation with 80 dB offset (RMS formula, verified in AudioAnalyzerDecibelsTest)

### UI
- Circular gauge with 270° cyan→magenta neon arc and peak marker
- LED-style digital readout with glow effect
- Neon dark theme (`#0A0E1A` base, fixed palette)
- 3 screens: Monitor, History, Settings

### Data
- Room database with timestamp-indexed `NoiseLevelEntity`
- DataStore for scalar preferences (threshold, toggles, sampling rate)
- 30-day auto-cleanup via scheduled timestamp query
- Hilt (2.56.2) for dependency injection — enforces singleton `NoiseRepository` across all ViewModels

### Alerts
- Push notifications with 30-second cooldown
- Vibration alert with independent toggle
- 3-state `RECORD_AUDIO` permission handling (granted → rationale → denied)
- `POST_NOTIFICATIONS` requested contextually on API 33+


### License
- Released under Business Source License 1.1 (BUSL-1.1)
- Free to study and use personally, commercial use prohibited
- Converts to MIT on 2030-01-01