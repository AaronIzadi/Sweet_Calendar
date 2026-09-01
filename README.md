# 🍬 Sweet Calendar

> 🍭 **A sweet little to-do app for your sweetest plans!** 📅✨

**Sweet Calendar** is a candy-themed Android to-do application built around the **Jalali (Persian/Shamsi) calendar**, with optional Gregorian mode, Iranian holidays, task reminders, statistics, and home-screen widgets.

Designed with **Kotlin + Jetpack Compose**, the app combines productivity features with a playful **pixel-art candy aesthetic** 🍬🎀.

---

## 🍭 Features

### 📅 Calendar & Navigation

* 🗓️ **Today / Week / Month** views
* 🇮🇷 **Jalali (Persian/Shamsi) calendar**
* 🌍 Optional **Gregorian calendar** mode
* 🔄 Switch calendar systems from Settings
* 📆 Configurable week start day
* 🧭 Bottom navigation for quick access

### ✅ Tasks & Productivity

Create and manage tasks with:

* 📝 Title
* 📄 Notes
* 🏷️ Category
* 📅 Date
* ⏰ Optional reminders
* ✅ Completion tracking
* 🗄️ Archive functionality
* ↩️ Undo delete with snackbar

### 🇮🇷 Iranian Holidays & Occasions

* 🎉 Iranian holidays and occasions powered by the **time.ir API**
* 🌐 Fetched remotely when needed
* 💾 Cached locally with Room
* 📴 Available offline after the month's data has been fetched

### 📊 Productivity Insights

* 🔥 Completion streaks
* 📈 Statistics screen
* 🎊 Celebration feedback when completing tasks
* 🔎 Task search
* 🗂️ Archived task management

### 🎨 Personalization

* 🌙 Dark mode
* 👤 Custom display name
* 📅 Configurable week start
* 🇮🇷 Persian ↔ 🌍 Gregorian calendar switching
* 🍭 Candy-themed UI throughout the application

### 📱 Home-Screen Widgets

Stay productive without even opening the app! 🚀

* 📅 **Today Summary** widget
* 🍬 **Candy Jar Progress** widget

### 🍬 Pixel-Art UI

The application features custom pixel-art candy illustrations throughout the interface:

🍭 Lollipops · 🍬 Wrapped Candies · 🍫 Chocolate · 🍦 Ice Cream · 🌱 Peppermint

---

## 🛠️ Tech Stack

| Technology               | Purpose                          |
| ------------------------ | -------------------------------- |
| 🟣 **Kotlin**            | Main programming language        |
| 🎨 **Jetpack Compose**   | Modern Android UI                |
| 🧩 **Material 3**        | UI components & theming          |
| 🗄️ **Room**             | Local task & holiday persistence |
| 🌐 **Retrofit + OkHttp** | time.ir API communication        |
| 📅 **Jalali Date Math**  | Persian ↔ Gregorian conversion   |
| ⏰ **AlarmManager**       | Task reminders                   |
| 🔔 **Notifications**     | Reminder notifications           |
| 📱 **App Widgets**       | Home-screen widgets              |
| 🖼️ **Pillow**           | Pixel-art asset processing       |

### 📅 Dependency-Free Calendar Math

Jalali/Gregorian conversion is implemented without an external calendar library:

```text
jalali/
├── JalaliDate.kt
└── GregorianDate.kt
```

This keeps the calendar conversion logic **self-contained and dependency-free**. 🧠✨

---

## 📋 Requirements

> ⚠️ **JDK 17 is required.**

* ☕ **JDK 17**
* 📱 **Android SDK**

    * Platform 34
    * Build-Tools 34
    * Platform-Tools
* 🧑‍💻 **Android Studio Koala or newer** — recommended
* 🌐 Internet connection for the initial dependency download

### ⚠️ Important: Java Version

Use **Java 17**.

❌ Java 8
❌ Java 25
✅ **Java 17**

Using another JDK version can cause the Gradle build to fail.

---

## 🚀 Setup

### 1️⃣ Clone & Open

Clone the repository and open the:

```text
SweetCalendar/
```

folder in **Android Studio**.

### 2️⃣ Gradle Sync

Allow Android Studio to sync Gradle and download the required dependencies.

🌐 An internet connection is required during the initial setup.

### 3️⃣ Configure `local.properties`

Create:

```text
SweetCalendar/local.properties
```

if it doesn't already exist.

Example:

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
TIME_IR_API_KEY=your_api_key_here
```

### 🔑 time.ir API Key

`TIME_IR_API_KEY` is **optional but recommended** for reliable holiday data.

Get an API key from [time.ir](https://time.ir?utm_source=chatgpt.com) and add it to `local.properties`.

> ℹ️ Without an API key, Iranian holidays may not load correctly.

### 🪟 Windows SDK Setup

Windows users can also run:

```powershell
.\setup-sdk.ps1
```

This script scaffolds the SDK folders and creates/updates `local.properties`.

---

## ▶️ Run the App

### 🧑‍💻 Android Studio

1. 📱 Start an emulator or connect an Android phone.
2. 🔌 Make sure USB debugging is enabled if using a physical device.
3. Click **Run ▶**.

That's it! 🍬

---

### 💻 Command Line

From the project directory:

```bash
cd SweetCalendar
./gradlew installDebug
adb shell am start -n com.example.calendartodo/.MainActivity
```

### 🪟 Windows PowerShell

Use:

```powershell
cd SweetCalendar
.\gradlew.bat installDebug
adb shell am start -n com.example.calendartodo/.MainActivity
```

---

## 🟦 BlueStacks

Prefer BlueStacks over the standard Android emulator? No problem! 🍬

### 1️⃣ Enable ADB

In BlueStacks settings, enable:

**Android Debug Bridge (ADB)**

### 2️⃣ Run the Script

From the project root:

```powershell
cd SweetCalendar
.\run-on-bluestacks.ps1
```

The script handles the build and installation on BlueStacks. 🚀

---

## 🏗️ Project Architecture

The project is organized into clear feature and responsibility-based packages:

```text
SweetCalendar/
│
├── app/src/main/java/com/example/calendartodo/
│   │
│   ├── calendar/              📅 Calendar system toggle
│   ├── jalali/                🇮🇷 Date conversion helpers
│   │
│   ├── data/
│   │   ├── local/             🗄️ Room database
│   │   ├── remote/            🌐 time.ir API client
│   │   └── prefs/             ⚙️ User preferences
│   │
│   ├── repository/            📦 Task & event repositories
│   │
│   ├── reminder/              ⏰ Notifications & alarms
│   ├── widget/                📱 Home-screen widgets
│   │
│   ├── ui/
│   │   ├── today/             📅 Today screen
│   │   ├── week/              🗓️ Week screen
│   │   ├── calendar/          📆 Calendar screen
│   │   ├── addtask/           ➕ Task creation
│   │   ├── taskdetail/        📝 Task editing/details
│   │   ├── daydetail/         📅 Day details
│   │   ├── holiday/           🎉 Holiday details
│   │   ├── search/            🔎 Search
│   │   ├── archive/           🗄️ Archived tasks
│   │   ├── stats/              📊 Statistics
│   │   ├── settings/           ⚙️ Settings
│   │   ├── welcome/            👋 Onboarding
│   │   └── components/         🧩 Shared UI components
│   │
│   ├── MainActivity.kt         🚀 Main activity
│   └── CalendarTodoApp.kt      📱 Application class
│
├── app/src/main/res/
│   └── drawable/               🖼️ Processed pixel-art assets
│
├── resources/
│   ├── pixel-art/              🍭 Source JPGs
│   └── icon/                   🎨 App icon source
│
├── scripts/
│   └── process_pixel_art.py    🖼️ Asset processing script
│
├── setup-sdk.ps1               🪟 SDK setup helper
└── run-on-bluestacks.ps1       🟦 BlueStacks runner
```

---

## 🎨 Pixel-Art Assets

The original pixel-art images are stored in:

```text
SweetCalendar/resources/pixel-art/
```

These source images are **not bundled directly into the APK**.

Instead, they are processed into transparent PNG drawables.

### 🖼️ Processing the Assets

Install Pillow:

```bash
pip install pillow
```

Then run:

```bash
python SweetCalendar/scripts/process_pixel_art.py
```

### 🍬 Asset Mapping

| 🖼️ Asset                  | 🎯 Used For                       |
| -------------------------- | --------------------------------- |
| `pixel_wrapped_candy.png`  | 🍬 Calendar FAB & month headers   |
| `pixel_lollipop_swirl.png` | 🍭 Add-task sheet & lollipop icon |
| `pixel_lollipop.png`       | ⏰ Reminder / alarm screen         |
| `pixel_ice_cream.png`      | 🍦 Empty task-list state          |
| `pixel_peppermint.png`     | 🌿 Holiday events                 |
| `pixel_chocolate.png`      | 🍫 Task completion celebration    |

### ➕ Adding New Artwork

To add or replace artwork:

1. 🖼️ Add/replace the JPG in `resources/pixel-art/`
2. 📝 Update the `OUTPUT_NAMES` map in `scripts/process_pixel_art.py`
3. ▶️ Run the processing script again
4. 🍬 The generated PNGs will be placed in the drawable resources

---

## 💾 Offline Support

Sweet Calendar is designed to keep working even when you're offline. 📴✨

### ✅ Tasks

Tasks are stored locally using **Room**, so task management works completely offline.

### 🇮🇷 Holidays

Holiday data requires internet access **once per month**:

```text
🌐 Fetch from time.ir
        ↓
   💾 Cache in Room
        ↓
📴 Available offline
```

Once a month's holidays have been fetched, they are cached locally and can be accessed without an internet connection.

---

## 🌅 Nowruz & Jalali Calendar Notes

The Jalali calendar implementation uses **arithmetic-based date calculations**.

For most dates this works as expected, but:

> ⚠️ In rare years, Jalali date calculations may differ by **one day** from the official Iranian government calendar, which is tied to the **astronomical equinox**.

This is an inherent limitation of purely arithmetic calendar conversion.

---

## ⏰ Reminders & Notifications

Tasks can optionally include reminders.

The application uses:

* ⏰ **AlarmManager** for scheduling
* 🔔 **Android notifications** for reminder delivery

On first launch, the app also guides the user through the required **notification-permission flow**.

---

## 👋 First Launch

New users are guided through a simple onboarding experience:

```text
👋 Welcome
   ↓
🔔 Notification Permission
   ↓
📅 Calendar Setup
   ↓
🍬 Start Planning!
```

---

## ⚠️ Troubleshooting

### ❌ Gradle reports a Java version error

Make sure Android Studio is using **JDK 17**.

In Android Studio:

```text
Settings
   ↓
Build, Execution, Deployment
   ↓
Build Tools
   ↓
Gradle
   ↓
Gradle JDK → Java 17
```

### ❌ Holidays aren't loading

Check that:

* 🌐 You have an internet connection
* 🔑 `TIME_IR_API_KEY` is configured
* 📅 The requested month hasn't failed its initial fetch

Remember: once successfully fetched, holiday data is cached locally.

### ❌ Resources aren't found

Make sure you're working from the correct project root:

```text
SweetCalendar/
```

---

## 🍭 Project Highlights

Sweet Calendar combines **productivity + Persian calendar support + Android development + playful UI design** into one project:

**🇮🇷 Jalali Calendar** · **📱 Android** · **🟣 Kotlin** · **🎨 Jetpack Compose** · **🗄️ Room** · **🌐 Retrofit** · **⏰ Notifications** · **📱 Widgets** · **🎨 Pixel Art** · **📴 Offline Support**

> 🍬 **Plan your day. Track your goals. Keep it sweet.** ✨
