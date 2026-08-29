# Sweet Calendar (Persian calendar)

A simple Android app: a Jalali (Persian/Shamsi) month calendar that shows
official occasions/holidays pulled from time.ir (via the `holidayapi.ir`
JSON mirror) side by side with your own personal to-do tasks.

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- Room for local storage of your tasks (works fully offline)
- Retrofit + OkHttp to fetch time.ir occasions from `holidayapi.ir`,
  cached in Room so it's only fetched once per month
- A small dependency-free Jalali <-> Gregorian date converter
  (`jalali/JalaliDate.kt`)

## How to open and run it

1. Install **Android Studio** (Koala or newer) if you don't have it:
   https://developer.android.com/studio
2. Open Android Studio -> **Open** -> select the `CalendarTodoApp` folder.
3. If Android Studio prompts you about the Gradle wrapper, let it
   download/regenerate it (or use **File > Sync Project with Gradle Files**).
   This project needs internet access the first time so Gradle can pull
   down the Android/Compose/Room/Retrofit libraries.
4. Create or start an emulator (**Device Manager** in Android Studio),
   or plug in an Android phone with USB debugging enabled.
5. Click **Run ▶**.

The app needs internet access at runtime to fetch time.ir occasions the
first time you view a given month — after that, they're cached locally.
Your personal tasks always work offline.

## Project layout

```
app/src/main/java/com/example/calendartodo/
  jalali/JalaliDate.kt          Jalali <-> Gregorian date math
  data/local/                   Room entities + DAOs (tasks, cached events)
  data/remote/                  Retrofit service for holidayapi.ir
  repository/                   TaskRepository, EventRepository
  ui/calendar/                  CalendarScreen + CalendarViewModel (main screen)
  ui/addtask/                   Add-task dialog
  MainActivity.kt               Entry point
  CalendarTodoApp.kt            Application class (holds shared repositories)
```

## Where to take it from here

Some easy next steps if you and your friend want to extend it:
- Reminders/notifications for tasks due today
- Task categories or colors
- Swipe-to-delete instead of a trash icon
- A simple month/year picker instead of only prev/next arrows
- Editing an existing task (currently: delete + re-add)

## A couple of honest caveats

- **Nowruz edge cases**: the Jalali date math is a well-tested arithmetic
  algorithm, but Iran's real calendar is pinned to the actual astronomical
  equinox, so in rare years the pure-math version can land a day off from
  the government's official declaration. Not worth over-engineering for a
  personal app, but worth knowing.
- **holidayapi.ir** is a community-run mirror of time.ir, not an official
  Anthropic-vetted or government API — it's been reliable in testing, but
  if it ever goes down, the app will just show no occasions until it's
  back (your personal tasks are unaffected either way).
- This project hasn't been compiled in a real Android build environment
  (this sandbox has no Android SDK/network access to Google's Maven repo),
  so it's been written and manually reviewed carefully but you may hit a
  small build hiccup on first sync — very fixable, just flag it if so.
