# Project Guidelines (for Junie)

Last updated: 2025-12-07 (JST)

This file provides a concise, project-specific overview for Junie. For platform-wide defaults and operating rules, see `JUNIE_DEFAULTS.md` at the repository root. In case of conflict, follow Latest-First in task instructions, then this file, then `JUNIE_DEFAULTS.md`.

## 1) Project overview
- App type: Android application (Kotlin)
- Root module: `app`
- Package: `jp.ac.jec.cm0199.aireviewdemo`
- Main entry: `MainActivity`
- UI layout: `app/src/main/res/layout/activity_main.xml`
- ViewModel: `MainViewModel` used by `MainActivity`

### Key files
- `app/src/main/AndroidManifest.xml` – components/permissions/launch activity
- `app/src/main/java/.../MainActivity.kt` – Activity, RecyclerView, adapter and view holder
- `app/src/main/java/.../MainViewModel.kt` – UI state, search logic, LiveData
- `app/src/main/res/values/strings.xml` – Localized strings
- `build.gradle.kts` (root) and `app/build.gradle.kts` – Gradle configuration

## 2) Project structure (high level)
- Root
  - `JUNIE_DEFAULTS.md` – default operating rules for Junie
  - `.junie/guidelines.md` – this file (project-specific guide)
  - `app/` – Android application module
    - `src/main/java/jp/ac/jec/cm0199/aireviewdemo/` – Kotlin sources
    - `src/main/res/` – resources (layouts, drawables, values, xml)
    - `src/test/` – local unit tests (JVM)
    - `src/androidTest/` – instrumented tests (device/emulator)

## 3) Build and run
Preferred: Android Studio (latest stable) with Gradle wrapper included in repo.

- Build (CLI):
  - macOS/Linux: `./gradlew assembleDebug`
  - Windows: `gradlew.bat assembleDebug`
- Install & run on a connected device/emulator:
  - `./gradlew installDebug` then launch from the launcher, or run from Android Studio.

Notes:
- Do not modify signing configs or add keystores.
- Keep Gradle wrapper as-is unless a task explicitly requests upgrades.

## 4) Tests
- Unit tests (JVM): `./gradlew testDebugUnitTest`
- Instrumented tests (device/emulator): `./gradlew connectedDebugAndroidTest`

Junie should run tests when:
- You change production code or tests in `app/` → run related unit tests. For UI/Android-specific changes, also run instrumented tests if they exist.
- Documentation-only changes (like this file) → do NOT build/run unless explicitly requested.

## 5) Submission expectations for typical tasks
- Documentation-only: update files, ensure markdown is valid; no build/tests.
- Code changes in `app/`:
  - Build `assembleDebug` to confirm it compiles.
  - Run unit tests that cover modified areas (`testDebugUnitTest`).
  - If you touched Android-specific behavior (Activity/ViewModel/Resources), smoke-run the app or instrumented tests when requested.

## 6) Code style and conventions
- Follow existing Kotlin style in this repo (indentation, imports, naming, comment frequency).
- Strings/UI text must be in `res/values/strings.xml` (avoid hard-coded strings in code/layouts).
- Resource naming: use descriptive, consistent IDs; keep layout IDs aligned with `activity_*.xml` patterns.
- Android patterns: keep long-running or network work off the main thread; respect Activity/Fragment lifecycle; observe LiveData/StateFlow from lifecycle-aware owners.
- When adding new classes/files, mirror the existing package and file layout. Keep changes minimal and localized.

## 7) Dependencies and configuration
- Do not introduce new libraries or plugins without explicit instruction.
- Keep `gradle.properties`, `settings.gradle.kts`, and Gradle versions unchanged unless the task requires it.

## 8) Communication and defaults
- Be concise; cite file paths/snippets when explaining changes.
- For general operating rules (modes, testing policy, security, naming, etc.), refer to `JUNIE_DEFAULTS.md`.

---

If you need additional project rules (e.g., CI policy, branch strategy, review checklist), ask the user to provide them or request permission to draft them here.
