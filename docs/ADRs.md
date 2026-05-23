# Architecture Decision Records

Three decisions that shaped how NoiseGuard is built. The non-obvious ones.

---

## ADR-001: Room over DataStore for noise readings
**Status:** Accepted | **Date:** 2026-04-10

**Decision:** Room with a timestamp-indexed `NoiseLevelEntity` table for readings. DataStore handles scalar preferences only.

**Why not DataStore alone:** DataStore's a key-value store. Storing a list of readings means serialising the whole thing on every write and loading it all back for every query. Range queries — "everything in the last 24 hours", "average since timestamp X" — are impossible without pulling the entire dataset into memory first. At 10 readings/second that's 36,000 entries per hour. Ruled out immediately.

**Outcome:** `getReadingsSince()` returns `Flow<List<NoiseLevel>>` backed by a SQL range query on the timestamp index. The History screen stays current automatically via Flow re-emission. Average and peak are SQL aggregates — no in-memory loops.

---

## ADR-002: Fixed colour palette over Material You
**Status:** Accepted | **Date:** 2026-04-14

**Decision:** Fixed `DarkPremium`/`NeonColors` palette. The app ignores wallpaper extraction entirely.

**Why not Material You:** NoiseGuard uses colour to communicate safety — cyan for safe, magenta through red for harmful. A dynamic palette could generate a colour scheme where the HARMFUL category becomes unreadable against the background. The gauge arc is also a multi-stop gradient from cyan to magenta; that can't be expressed as a single Material semantic token, so a hybrid approach would still require overriding the dynamic system in every place that matters.

**Outcome:** Contrast between category colours is guaranteed regardless of what's on the user's home screen. The app doesn't adapt to system themes, and on Android 12+ it'll feel slightly external to the system aesthetic. That's the right trade-off when the colours are load-bearing.

---

## ADR-003: Bottom navigation bar over NavigationRail
**Status:** Accepted | **Date:** 2026-04-18

**Decision:** `NavigationBar` with three `NavigationBarItem` entries.

**Why not NavigationRail:** NavigationRail is designed for tablets and landscape-primary use. NoiseGuard's main interaction is portrait, one-handed, thumb-driven — you pull out your phone, check the reading, put it away. NavigationRail puts destinations in the thumb's dead zone on a standard phone. A drawer requires an explicit open gesture, which is wrong for three destinations that should always be one tap away.

**Outcome:** All three screens reachable with one thumb tap in the natural thumb zone. Standard Android pattern — no learning curve for the user.

---

## ADR-005: Hilt for Dependency Injection
**Status:** Accepted · **Date:** 2026-05-22

I picked Hilt over Koin and manual DI. The deciding factor wasn't build time or boilerplate — it was the repository singleton bug: three ViewModels each constructing their own `NoiseRepositoryImpl` meant three independent batch buffers. Clearing history while monitoring was broken.  Hilt's `@Singleton` fixed this with one annotation.

**What I considered instead:** Koin (simpler setup, runtime resolution) and manual DI (what we had). Manual DI couldn't guarantee a single repository instance without a god-object Application. Koin would have worked but Hilt is the Google standard and shows up in every Android job spec.

**Result:** Single repository instance, testable ViewModels, -30 lines of factory boilerplate.
