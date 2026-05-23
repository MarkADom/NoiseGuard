# Changelog

All notable changes to this project will be documented in this file.
Format: [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## [Unreleased]

## [1.0.0] - 2026-05-19

### Core
- Real-time dB SPL monitoring via `AudioRecord` (RMS formula, 20–120 dB range)
- 4 noise categories: QUIET · MODERATE · LOUD · HARMFUL
- Calibrated dB calculation with 82 dB hardware offset

### UI
- Circular gauge with 270° cyan→magenta neon arc and peak marker
- LED-style digital readout with glow effect
- Neon dark theme (`#0A0E1A` base, fixed palette)
- 3 screens: Monitor, History, Settings

### Data
- Room database with timestamp-indexed `NoiseLevelEntity`
- DataStore for scalar preferences (threshold, toggles, sampling rate)
- 30-day auto-cleanup via scheduled timestamp query

### Alerts
- Push notifications with 30-second cooldown
- Vibration alert with independent toggle
- 3-state `RECORD_AUDIO` permission handling (granted → rationale → denied)
- `POST_NOTIFICATIONS` requested contextually on API 33+


### License
- Released under CC BY-NC 4.0 (non-commercial use only)