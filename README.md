# Luma

An Android app for planning around your energy instead of against it.

## Why
Most to-do apps assume you have the same capacity every day. You don't. Luma starts with a quick
check-in, and then helps you pick tasks that actually match how you're feeling.

No streaks, no guilt-tripping notifications, no emojis demanding your attention. Just a simple way 
to notice your energy and plan accordingly.

## Features

- **Daily check-in**: rate your energy, stress, focus, social battery, and sleep quality on a 1-5 scale
- **Energy-tagged tasks**: every task is labeled as Rest, Focus, Presence, or Movement, so you know 
what it asks of you
- **Active and completed tasks**: with the ability to edit, complete and restore tasks
- **Home overview**: see today's check-in summary and a suggestion based on how you're doing
- **Insights**: track patterns in your energy over time and see what affects your focus and recovery
- **First-time onboarding**: a short walkthrough explaining the concept, shown once and available 
again anytime via the help icon on Home

## Screenshots

<table>
  <tr>
    <td>Home</td>
    <td>Check-in</td>
    <td>Tasks - Active</td>
    <td>Tasks - Completed</td>
    <td>Insights</td>
  </tr>
  <tr>
    <td><img src="screenshots/home.jpg" width="200"/></td>
    <td><img src="screenshots/checkin.jpg" width="200"/></td>
    <td><img src="screenshots/activetasks.jpg" width="200" height="420"/></td>
    <td><img src="screenshots/completedtasks.jpg" width="200" height="420"/></td>
    <td><img src="screenshots/insights.jpg" width="200"/></td>
  </tr>
</table>

## Tech stack

- **Kotlin** with **Jetpack Compose**
- **MVVM** architecture
- **Room** for local persistence
- **DataStore** for onboarding state
- **Navigation Compose** for screen navigation
- Material 3 design system with a custom calm, nature based color palette

## Project structure

```
app/src/main/java/com/example/luma/
├── data/
│   ├── model/          # Room entities (CheckIn, Task) and enums
│   ├── database/        # Room database setup
│   └── repository/      # Data access layer
├── ui/
│   ├── home/             # Home screen and view model
│   ├── checkin/          # Check-in flow
│   ├── tasks/            # Task list and add/edit task
│   ├── insights/         # Energy pattern insights
│   ├── onboarding/        # First-time onboarding flow
│   └── theme/            # Colors, typography, theme
├── Navigation.kt          # App navigation graph
└── LumaApplication.kt     # App entry point, database seeding
```

## Running the app

1. Clone the repository
2. Open the project in [Android Studio](https://developer.android.com/studio) (Hedgehog or newer recommended)
3. Let Gradle sync and download dependencies
4. Run the app on an emulator or physical device (minimum SDK 26 / Android 8.0)

No backend or API keys are required. The app seeds itself with sample data on first launch so there's something to look at right away.

## Status

This is a portfolio/learning project and a work in progress. The focus has been on the core flow: 
check-in, task management, and onboarding. Planned next steps include better insights and suggestion logic.