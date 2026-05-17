# Morning Glory - Minimalist Alarm App

This document outlines the architecture, technology stack, and development guidelines for the
Morning Glory project.

## Architecture Overview

Morning Glory follows a simple, state-driven architecture centered around Android services and
singletons for core logic.

### Core Components

- **`AppPreferences`**: A singleton that manages all persistent state using `SharedPreferences`. It
  uses GSON for complex types like `Uri` and `RingtoneInfo`.
- **`AppAlarmManager`**: A utility singleton for scheduling, snoozing, and cancelling alarms via the
  Android `AlarmManager`. It handles both "Sleep" and "Nap" alarm types.
- **`AlarmService`**: A foreground service triggered when an alarm goes off. It manages sound
  playback (`AppSoundPlayer`) and shows the alarm notification.
- **`AppSoundPlayer`**: A unified class for handling audio playback (alarms and ringtone previews)
  using a single `MediaPlayer` instance.
- **`AlarmActivity`**: A full-screen activity shown when an alarm triggers, providing the UI for
  dismissal (including QR scanning) and snoozing.
- **`MainActivity`**: The primary entry point, handling permissions and hosting the `HomeScreen`.
- **`ScannerActivity`**: A full-screen activity used to scan QR codes.

### Data Flow

1. **Scheduling**: User sets an alarm in `HomeScreen` -> `AppAlarmManager` schedules an exact alarm
   and updates `AppPreferences`.
2. **Triggering**: `AlarmManager` triggers `AlarmService` via a `PendingIntent`.
3. **Action**: `AlarmService` starts in the foreground, plays sound, and launches `AlarmActivity` (
   if the screen is on or via full-screen intent).
4. **Dismissal**: User interacts with `AlarmActivity` -> `AlarmService` is stopped ->
   `AppAlarmManager` may reschedule (for daily sleep alarms).

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Image/Barcode**: ZXing (for QR code scanning)
- **Serialization**: Gson (with custom `UriTypeAdapter`)
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)

## Development Guidelines

### State Management

- Use `AppPreferences` for all persistent settings. Avoid direct `SharedPreferences` access
  elsewhere.
- When adding new settings, add a property to `AppPreferences` with appropriate getters and setters.

### UI & Styling

- All UI should be built using Jetpack Compose.
- Follow the established theme in `ui/theme/`.
- Use the shared components in `shared/components/` for consistency.

### Alarm Logic

- Always use `AppAlarmManager` for scheduling or cancelling alarms to ensure consistent behavior (
  e.g., updating preferences, handling pre-alarm notifications).
- Ensure `AlarmType` is used to distinguish between Sleep and Nap alarms.

### Coding Conventions

- **Naming**: Use descriptive names. Receivers should end in `Receiver`, Services in `Service`,
  Activities in `Activity`, and Compose functions should be UpperCamelCase.
- **Threading**: Use `prepareAsync()` for `MediaPlayer` and handle service starts carefully to avoid
  blocking the main thread.
- **Permissions**: Handle runtime permissions (especially POST_NOTIFICATIONS for Android 13+) in
  `MainActivity`.

## Project Structure

- `app/src/main/java/app/morning/glory/`
    - `core/`: Core logic (audio, notifications, services, utilities).
    - `shared/`: Reusable Compose components.
    - `ui/`: Feature-specific screens and UI logic.
- `app/src/main/res/`:
    - `raw/`: Default alarm sounds.
    - `drawable/`: Vector and bitmap assets.
    - `values/`: Strings, themes, and colors.

## Future Roadmap

- **Random Ringtone**: Support for selecting a random sound from the imported list.
- **Custom Images**: Allowing users to set a custom background for the `AlarmScreen`.
- **NFC Support**: Implementing NFC tag scanning as an alternative dismissal method.
