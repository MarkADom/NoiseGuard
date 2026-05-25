# Architecture

NoiseGuard uses Clean Architecture with three layers and one hard rule: dependencies point inward. The domain layer knows nothing about Android. The UI layer knows nothing about Room or DataStore.

## Layers

```
┌──────────────────────────────────────────────────────────┐
│  UI Layer                                                │
│  MonitorScreen · HistoryScreen · SettingsScreen          │
│  MonitorViewModel · HistoryViewModel · SettingsViewModel │
├──────────────────────────────────────────────────────────┤
│  Domain Layer                                            │
│  NoiseLevel · NoiseCategory                              │
│  NoiseRepository (interface)                             │
├──────────────────────────────────────────────────────────┤
│  Data Layer                                              │
│  AudioAnalyzer · NoiseRepositoryImpl                     │
│  NoiseGuardDatabase · UserPreferences · NotificationHelper│
└──────────────────────────────────────────────────────────┘
```

`NoiseRepository` is a pure Kotlin interface — zero Android imports in the domain package. `MonitorViewModel` takes it as a constructor parameter and never touches `NoiseRepositoryImpl` directly.

## Data Flow

```
AudioRecord → AudioAnalyzer (Flow) → MonitorViewModel (StateFlow) → Compose UI
                                   ↓
                           NoiseRepositoryImpl → Room Database
```

Each buffer read goes through RMS → dB conversion in `AudioAnalyzer`, emitted as a `Flow<Double>`. The ViewModel collects on Main, updates state, persists via the repository, and fires an alert if the threshold is crossed and the cooldown has elapsed.

## Threading

| Work | Dispatcher | Reason |
|---|---|---|
| AudioAnalyzer audio loop | Default | CPU-bound RMS calc — not blocking I/O |
| Room queries | IO | Blocking disk reads |
| Period avg/peak calculations | Default | Pure computation over in-memory lists |

`AudioRecord.read()` fills a buffer from an internal ring buffer and returns quickly — it doesn't block on slow I/O. The dominant cost is the RMS math, which belongs on Default, not IO.

## Patterns

- **MVVM** — each screen has one `StateFlow<UiState>`; Compose collects with `collectAsStateWithLifecycle`
- **Repository** — data source decisions are invisible above the data layer
- **Unidirectional Data Flow** — UI fires events, ViewModel updates state, Compose reacts
- **Single Source of Truth** — Room is the only persistent store; ViewModels don't cache

## Dependency Injection

Hilt (`hilt-android 2.56.2`) wires the dependency graph. `AppModule` provides `@Singleton` instances of `NoiseRepository`, `UserPreferences`, `NotificationHelper`, and `NoiseGuardDatabase`. All three ViewModels are `@HiltViewModel` with `@Inject` constructors — no manual factory boilerplate.

The singleton scope matters: without it, each ViewModel constructs its own `NoiseRepositoryImpl`, which means three independent write buffers. Clearing history while monitoring was broken until this was fixed. See [ADR-004](ADRs.md#adr-004-hilt-for-dependency-injection) for the full reasoning.

No multi-module, no WorkManager — neither is warranted at this scale.

## Testing

**Unit tests (69)** run on the JVM — no Android runtime, no emulator needed.
`AudioAnalyzer`, `NoiseRepository`, and ViewModel logic are covered.

**Instrumented tests (13)** run on a physical device:
- `NoiseLevelDaoTest` — Room DAO queries against an in-memory database
- `NavigationTest` — Compose navigation via `createAndroidComposeRule<MainActivity>()`

Test infrastructure:
- `HiltTestRunner` — replaces the default runner, swaps Hilt component for test component
- `TestAppModule` — provides in-memory Room DB, isolates tests from on-device data
- `TestUtils` — fixed `BASE_TIMESTAMP` and helpers for deterministic test data
