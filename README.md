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
- Pixel-art candy illustrations (processed from source JPGs in
  `resources/pixel art/`)

## How to open and run it

1. Install **Android Studio** (Koala or newer) if you don't have it:
   https://developer.android.com/studio
2. Open Android Studio -> **Open** -> select the `SweetCalendar` folder.
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

From the command line you can also install and launch with:

```bash
cd SweetCalendar
./gradlew installDebug
adb shell am start -n com.example.calendartodo/.MainActivity
```

## Project layout

```
SweetCalendar/
  app/src/main/java/com/example/calendartodo/
    jalali/JalaliDate.kt          Jalali <-> Gregorian date math
    data/local/                   Room entities + DAOs (tasks, cached events)
    data/remote/                  Retrofit service for holidayapi.ir
    repository/                   TaskRepository, EventRepository
    ui/calendar/                  CalendarScreen + CalendarViewModel
    ui/addtask/                   Add/edit task bottom sheet
    ui/components/CandyArt.kt     Pixel-art image composables
    MainActivity.kt               Entry point
    CalendarTodoApp.kt            Application class (holds shared repositories)
  app/src/main/res/drawable/      Processed transparent PNG assets
  resources/pixel art/            Source pixel-art JPGs (not bundled in APK)
  scripts/process_pixel_art.py    Removes backgrounds and exports PNGs
```

## Pixel art assets

Source images live in `SweetCalendar/resources/pixel art/`. They are not
packaged into the APK directly — run the processing script to regenerate
the transparent PNG drawables used by the app:

```bash
pip install pillow
python SweetCalendar/scripts/process_pixel_art.py
```

This writes updated files to `app/src/main/res/drawable/`:

| Output | Used for |
|---|---|
| `pixel_wrapped_candy.png` | Calendar FAB, month headers |
| `pixel_lollipop_swirl.png` | Add-task sheet, general lollipop icon |
| `pixel_lollipop.png` | Reminder/alarm screen |
| `pixel_ice_cream.png` | Empty task list state |
| `pixel_peppermint.png` | Holiday events on calendar |
| `pixel_chocolate.png` | Task completion celebration |

Add or replace JPGs in `resources/pixel art/`, update the `OUTPUT_NAMES`
map in `scripts/process_pixel_art.py` if filenames change, then rerun the
script.

## A couple of honest caveats

- **Nowruz edge cases**: the Jalali date math is a well-tested arithmetic
  algorithm, but Iran's real calendar is pinned to the actual astronomical
  equinox, so in rare years the pure-math version can land a day off from
  the government's official declaration. Not worth over-engineering for a
  personal app, but worth knowing.
- **holidayapi.ir** is a community-run mirror of time.ir, not an official
  government API — it's been reliable in testing, but if it ever goes down,
  the app will just show no occasions until it's back (your personal tasks
  are unaffected either way).
