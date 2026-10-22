# 🍬 Sweet Calendar

A candy-themed Android to-do app built around the **Jalali (Persian/Shamsi) calendar** 📅, with optional Gregorian mode, Iranian holidays 🇮🇷, reminders 🔔, stats 📊, home-screen widgets 📱, and a playful pixel-art UI 🎮.

**🛠️ Stack:** Kotlin · Jetpack Compose · Material 3 · Room · Retrofit

**📦 Application ID:** `com.sweetcalendar`

---

## ✨ Features

### 📅 Calendar and navigation
- 🏠 **Today**, **Week**, and **Month** views with bottom navigation
- 🇮🇷 **Jalali (Persian/Shamsi)** calendar with optional **Gregorian** mode
- 🔄 Switch calendar system and **week start day** in Settings
- 🗓️ **Day detail** and **holiday** screens

### ✅ Tasks
- 📝 Title, notes, category, date, optional time, and **reminders**
- ✔️ Complete, edit, archive, **search**, and **undo delete** (snackbar)
- 🍭 Custom **Sweet** time and date pickers aligned with the mockup UI

### 🇮🇷 Holidays (Iran)
- 🎉 Iranian holidays and occasions via the **time.ir** API
- 💾 Fetched when needed, **cached in Room**, and usable offline after a month is loaded

### 📊 Stats and delight
- 🔥 **Streaks**, jar progress, and a **stats** screen
- 🎊 Light motion and celebration feedback (respects system **reduced motion** when animator scale is off)

### 🎨 Personalization
- 🌙 **Dark mode**
- 🪐 **App theme:** Bubblegum (candy) or Orbit (space)
- 👤 Custom **display name** (profile card on Settings)
- 🎊 Show/hide **local holidays**

### 📤 Data
- 💾 **Backup and export** (CSV, Excel, PDF) from Settings

### 📱 Home-screen widgets
- 📋 **Today tasks** widget
- 🍯 **Jar progress** widget

### 🖼️ UI
- 🎨 Mockup-driven layout with **Quicksand** body type and **pixel** labels
- ✨ Shared motion in `ui/motion/Motion.kt`
- 🌈 Color system documented in [`SweetCalendar/docs/color-palette.md`](SweetCalendar/docs/color-palette.md) (`PinkText`, `onPink`, nav inactive tokens, field outlines)

---

## 📋 Requirements

- ☕ **JDK 17** (required; other versions often break Gradle)
- 🤖 **Android SDK** — Platform 34, Build-Tools 34, Platform-Tools
- 💻 **Android Studio** Koala or newer (recommended)
- 🌐 Network for first-time dependency download

---

## 🚀 Setup

### 1️⃣ Open the project

Open **either** the git repo root (`Sweet_Calendar/`) **or** the Gradle root (`SweetCalendar/`). Both are wired for an **app** run configuration after Gradle sync.

### 2️⃣ Sync Gradle

Allow Gradle to download dependencies.

### 3️⃣ ⚙️ `local.properties`

Create `SweetCalendar/local.properties` if needed:

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
TIME_IR_API_KEY=your_api_key_here
```

**🔑 time.ir API key** — optional but recommended for reliable holiday data. Get a key from [time.ir](https://time.ir) and add `TIME_IR_API_KEY`. Without it, holidays may not load.

**🪟 Windows:** Run `.\setup-sdk.ps1` from `SweetCalendar/` to scaffold SDK paths and `local.properties`.

---

## ▶️ Run the app

### 💻 Android Studio

1. Start an emulator or connect a device (USB debugging on).
2. Run the **app** configuration.

### ⌨️ Command line

From `SweetCalendar/`:

```bash
./gradlew installDebug
adb shell am start -n com.sweetcalendar/.MainActivity
```

**🪟 Windows PowerShell (emulator — starts AVD if needed):**

```powershell
cd SweetCalendar
.\run-on-emulator.ps1
```

**Manual install:**

```powershell
cd SweetCalendar
.\gradlew.bat installDebug
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start -n com.sweetcalendar/.MainActivity
```

### 🖥️ BlueStacks

Enable **Android Debug Bridge (ADB)** in BlueStacks, then from `SweetCalendar/`:

```powershell
.\run-on-bluestacks.ps1
```

---

## 🗂️ Project layout

```text
SweetCalendar/
├── app/src/main/java/com/sweetcalendar/
│   ├── calendar/           Calendar system (Jalali / Gregorian)
│   ├── jalali/             Date conversion (no external calendar lib)
│   ├── data/
│   │   ├── local/          Room (tasks, event cache)
│   │   ├── remote/         time.ir client
│   │   └── prefs/           User preferences
│   ├── repository/
│   ├── reminder/            Alarms and notifications
│   ├── export/              CSV, Excel, PDF export
│   ├── widget/              Home-screen widgets
│   ├── ui/
│   │   ├── today/ week/ calendar/ daydetail/
│   │   ├── addtask/ taskdetail/ tasklist/
│   │   ├── holiday/ search/ archive/ stats/
│   │   ├── settings/ welcome/ permission/ offline/
│   │   ├── motion/          Shared animations
│   │   ├── navigation/
│   │   ├── components/      Shared Compose UI
│   │   └── theme/           Colors, type, mockup scale
│   ├── MainActivity.kt
│   └── CalendarTodoApp.kt   Application class
├── docs/color-palette.md
├── resources/pixel-art/     Source art (processed into drawables)
└── scripts/process_pixel_art.py
```

### 🧮 Jalali math

```text
jalali/
├── JalaliDate.kt
└── GregorianDate.kt
```

Arithmetic Jalali/Gregorian conversion with no third-party calendar dependency. Rare years can differ by one day from the official equinox-based Iranian calendar.

---

## 🎮 Pixel-art assets

Sources live in `SweetCalendar/resources/pixel-art/`. They are processed into transparent PNGs for the APK:

```bash
pip install pillow
python SweetCalendar/scripts/process_pixel_art.py
```

See `scripts/process_pixel_art.py` (`OUTPUT_NAMES`) when adding or replacing art.

---

## 📴 Offline behavior

| Data | Behavior |
| --- | --- |
| 📝 Tasks | Fully local (Room) |
| 🎉 Holidays | Online fetch once per month, then Room cache |

---

## 🌱 First launch

1. 👋 **Welcome** carousel (skippable)
2. 🔔 **Notification permission** when reminders are used
3. 🏠 Main app: **Today** tab by default

---

## ♿ Accessibility and contrast

- 💗 **`pinkText`** — small pink labels and icons on cream/paper (not bright `Pink` / `PinkDeep`)
- 🖋️ **`onPink`** — ink-colored labels on pink button fills
- 📝 **`fieldOutline`** — visible input borders where `line` is too subtle
- 🎞️ **Reduced motion:** when system animator duration is `0`, the app shortens or disables many animations (`rememberReducedMotion()`)

📖 Details: [`SweetCalendar/docs/color-palette.md`](SweetCalendar/docs/color-palette.md)

---

## 🛠️ Troubleshooting

| Issue | What to check |
| --- | --- |
| ☕ Java / Gradle errors | Gradle JDK = **17** (Android Studio → Settings → Build Tools → Gradle) |
| 📱 Run does nothing / no device | Start an AVD in **Device Manager**; device dropdown must show `emulator-…` **device** (not offline). Or run `.\run-on-emulator.ps1` |
| 💥 Emulator “crashes” / app vanishes | Often the **API 35 emulator** (Bluetooth system bug), not Sweet Calendar. Close AVD → `.\fix-emulator.ps1 -ColdBoot` → `.\run-on-emulator.ps1 -ColdBoot`. Save logs with `.\capture-crash-log.ps1` |
| 📱 “Module not specified” | Use run config **app** (not “Sweet Calendar”). **File → Sync Project with Gradle Files** |
| 🎉 Holidays missing | Internet, `TIME_IR_API_KEY`, retry after failed month fetch |
| 📂 Wrong project root | Repo root or `SweetCalendar/` both work after Gradle sync |

---

## 🧰 Tech stack (summary)

| Piece | Use |
| --- | --- |
| 🟣 Kotlin | Language |
| 🎨 Compose | UI |
| 💾 Room | Tasks and holiday cache |
| 🌐 Retrofit | time.ir |
| ⏰ AlarmManager | Reminders |
| 📱 App Widgets | Today list and jar progress |

---

🍬 **Plan your day, track your goals, keep it sweet.** 💖
