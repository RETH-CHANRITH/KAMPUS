<div align="center">

<br />

<img src="https://img.shields.io/badge/KAMPUS-Campus%20Social%20Network-0D7FFF?style=for-the-badge&labelColor=0A0A0A" height="40" />

<br /><br />

**The all-in-one social platform built for campus life.**  
Connect · Share · Chat · Discover

<br />

[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black)](https://firebase.google.com)
[![Material 3](https://img.shields.io/badge/Material%203-757575?style=flat-square&logo=materialdesign&logoColor=white)](https://m3.material.io)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-brightgreen?style=flat-square)](https://developer.android.com/about/versions/oreo)
[![Version](https://img.shields.io/badge/Version-1.0-blue?style=flat-square)]()
[![License](https://img.shields.io/badge/License-MIT-red?style=flat-square)]()

<br />

</div>

---

## 📸 Screenshots

<div align="center">

| Groups (Light) | Events (Light) | Profile (Light) |
|:-:|:-:|:-:|
| <img src="screenshots/groups_light_fixed.png" width="220"/> | <img src="screenshots/events_light_fixed.png" width="220"/> | <img src="screenshots/profile_light_fixed.png" width="220"/> |

</div>

---

## 📖 About KAMPUS

**KAMPUS** is a full-featured Android social networking app made for university students. It brings together everything students need — a social feed, group communities, campus events, real-time chat, and friend discovery — all in a single beautiful app.

> 🎓 Built as a **Year Three** university project using modern Android development best practices.

---

## ✨ Features

<table>
  <tr>
    <td>🔐 <b>Authentication</b></td>
    <td>Email & Password · Google Sign-In · OTP verification · Forgot password flow</td>
  </tr>
  <tr>
    <td>📰 <b>Social Feed</b></td>
    <td>Create posts with text, images & videos · Like · Comment · Share · Stories</td>
  </tr>
  <tr>
    <td>💬 <b>Real-time Chat</b></td>
    <td>Direct messages · Group chats · Voice & Video calls (WebRTC)</td>
  </tr>
  <tr>
    <td>👥 <b>Groups</b></td>
    <td>Create or join public/private groups · Request to join · Discover new groups</td>
  </tr>
  <tr>
    <td>📅 <b>Events</b></td>
    <td>Browse campus events · RSVP · Create and manage your own events</td>
  </tr>
  <tr>
    <td>👤 <b>Profiles</b></td>
    <td>Personal profile · Bio · Faculty & Year · Followers / Following · Activity feed</td>
  </tr>
  <tr>
    <td>🔍 <b>Search</b></td>
    <td>Search across people, posts, groups, and events all at once</td>
  </tr>
  <tr>
    <td>🔔 <b>Notifications</b></td>
    <td>Real-time push notifications via Firebase Cloud Messaging</td>
  </tr>
  <tr>
    <td>🌙 <b>Themes</b></td>
    <td>Dark & Light mode · 7 accent colors · Adjustable font scale</td>
  </tr>
  <tr>
    <td>🌐 <b>Localization</b></td>
    <td>Multi-language UI string support</td>
  </tr>
  <tr>
    <td>🛡️ <b>Admin Panel</b></td>
    <td>Manage users · Review & moderate reported content</td>
  </tr>
</table>

---

## 🏗️ Architecture

KAMPUS is built with **Clean Architecture + MVVM** — the industry-standard pattern for scalable Android apps.

```
┌──────────────────────────────────────────────┐
│         🎨  Presentation Layer               │
│     Jetpack Compose Screens + ViewModels     │
├──────────────────────────────────────────────┤
│           🧠  Domain Layer                   │
│   Models  ·  Use Cases  ·  Repo Interfaces   │
├──────────────────────────────────────────────┤
│            🌐  Data Layer                    │
│   Firebase · Supabase · Repository Impls     │
└──────────────────────────────────────────────┘
```

**Why Clean Architecture?**
- Each layer has one job — easy to understand and test
- Adding a new feature doesn't break existing ones
- Swapping the backend (e.g. Firebase → Supabase) only touches the Data layer

---

## 🔧 Tech Stack

### 🧱 Core
| | Library | Purpose |
|---|---|---|
| 🟣 | **Kotlin** | Primary language |
| 🎨 | **Jetpack Compose** | 100% declarative UI — no XML |
| 📐 | **Material Design 3** | Modern Google UI design system |
| 🧭 | **Navigation Compose** | Type-safe screen routing |
| ⚡ | **Kotlin Coroutines + Flow** | Async, reactive programming |
| 💡 | **StateFlow** | Observable state in ViewModels |

### 🔥 Backend & Database
| | Library | Purpose |
|---|---|---|
| 🔐 | **Firebase Auth** | Login, Register, Google Sign-In |
| 🗄️ | **Firebase Firestore** | Real-time NoSQL cloud database |
| 📁 | **Firebase Storage** | File & media storage |
| 📣 | **Firebase Messaging (FCM)** | Push notifications |
| 🗃️ | **Supabase Storage** | Image & media uploads |

### 🎞️ Media & UI
| | Library | Purpose |
|---|---|---|
| 🖼️ | **Coil** | Fast async image loading from URL |
| ✂️ | **UCrop** | In-app image crop & edit |
| 📷 | **CameraX** | Camera capture for stories |
| 🎬 | **Media3 / ExoPlayer** | Smooth in-feed video playback |
| 📞 | **WebRTC** | Real-time voice & video calls |

### 🛠️ Other
| | Library | Purpose |
|---|---|---|
| 💾 | **DataStore** | Persist user settings locally |
| 📤 | **WorkManager** | Reliable background file uploads |
| 🔒 | **Security Crypto** | Encrypted local storage (E2EE) |
| 🌐 | **OkHttp** | WebSocket signaling for calls |

---

## 📁 Project Structure

```
KAMPUS-APP/
│
├── 📱 app/src/main/java/com/example/kampus/
│   │
│   ├── MainActivity.kt                 # App entry point
│   ├── KampusApplication.kt            # Application class
│   │
│   ├── 🧭 navigation/
│   │   ├── NavGraph.kt                 # All routes & navigation logic
│   │   └── Screen.kt                   # Route name constants
│   │
│   ├── 🧠 domain/                      # Pure Kotlin — zero Android dependencies
│   │   ├── model/                      # User, Post, Group, Event, Message…
│   │   ├── repository/                 # Repository interfaces (contracts only)
│   │   └── usecase/                    # Business logic: Login, CreatePost…
│   │
│   ├── 🌐 data/                        # Talks to the network
│   │   ├── remote/                     # Firebase & Supabase data sources
│   │   └── repository/                 # Implementations of domain interfaces
│   │
│   ├── 💉 di/                          # Hilt dependency injection modules
│   │
│   ├── 🎨 ui/
│   │   ├── theme/                      # Colors, Typography, ThemeController
│   │   ├── components/                 # Shared UI: Button, Avatar, Dialog…
│   │   │
│   │   ├── splash/                     # Splash screen
│   │   ├── onboarding/                 # First-launch walkthrough
│   │   ├── auth/                       # Login · Register · OTP · Forgot PW
│   │   ├── feed/                       # Home feed · Create post · Stories
│   │   ├── post/                       # Post detail · Comments
│   │   ├── chat/                       # Chat list · Chat screen · Calls
│   │   ├── groups/                     # Group list · Detail · Create
│   │   ├── events/                     # Event list · Detail · Create
│   │   ├── profile/                    # Profile · Edit · Friends · Settings
│   │   ├── notifications/              # Notifications screen
│   │   ├── search/                     # Global search
│   │   ├── story/                      # Story camera & viewer
│   │   ├── admin/                      # Admin dashboard
│   │   └── localization/               # Multi-language string tables
│   │
│   ├── viewmodel/                      # Shared ViewModels (Groups, etc.)
│   └── utils/                          # DateUtils · ImageUtils · Extensions
│
├── 🔒 firestore.rules                  # Firestore security rules
├── 🔑 firebase.json                    # Firebase project config
├── 📦 gradle/libs.versions.toml        # All dependency versions in one file
└── ⚙️  app/build.gradle.kts            # App build configuration
```

---

## 🗺️ Navigation Flow

```
         ┌─────────┐
         │  Splash  │
         └────┬─────┘
              │
     ┌────────┴────────┐
  First visit?      Returning?
     │                  │
┌────▼─────┐       ┌────▼─────┐
│Onboarding│       │   Home   │◄──────────────────────────┐
└────┬─────┘       │  (Feed)  │                           │
     │             └────┬─────┘                           │
┌────▼─────┐            │                                 │
│  Login / │     ┌──────┼──────┬──────────┬───────┐       │
│ Register │   Groups Events  Chat    Profile  Search     │
└────┬─────┘     │      │      │        │                 │
     │           │      │      │        ├── Settings ─────┘
     └──────────►│      │      │        ├── Friends
              Detail  Detail  Chat     └── Edit Profile
              Create  Create  Screen
```

---

## ⚙️ Build Configuration

```kotlin
applicationId  = "com.example.kampus"
minSdk         = 26    // Android 8.0+
targetSdk      = 36    // Android 16
compileSdk     = 36
versionName    = "1.0"
jvmTarget      = "11"
```

---

## 🚀 Getting Started

### Prerequisites

- ✅ [Android Studio](https://developer.android.com/studio) **Hedgehog** or newer
- ✅ JDK 11+
- ✅ `google-services.json` from your [Firebase Console](https://console.firebase.google.com) → place in `app/`
- ✅ Supabase project URL + anon key → configure in `di/SupabaseModule.kt`

### Clone & Run

```bash
# 1. Clone the repository
git clone https://github.com/RETH-CHANRITH/KAMPUS.git
cd KAMPUS

# 2. Open in Android Studio and let Gradle sync automatically

# 3. Add google-services.json to the app/ folder

# 4. Run on emulator or physical device
./gradlew assembleDebug
```

> Or simply press ▶️ **Run** in Android Studio after syncing.

### Build Commands

```bash
./gradlew assembleDebug      # → app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease    # → app/build/outputs/apk/release/app-release.apk
./gradlew test               # Run unit tests
./gradlew build              # Full project build check
```

---

## 🎨 Theme System

KAMPUS uses a global **`ThemeController`** singleton that updates the entire UI in real time — no restart needed.

```kotlin
ThemeController.isDark     // true = Dark Mode  |  false = Light Mode
ThemeController.fontScale  // 0.85f … 1.3f — global font size
ThemeController.accent     // Accent color enum
```

### Available Accent Colors

| Accent | Color |
|---|---|
| 🔵 Blue | `#0D7FFF` (default) |
| 🟣 Purple | `#9C27B0` |
| 🩷 Pink | `#E91E63` |
| 🔴 Red | `#F44336` |
| 🟠 Orange | `#FF9800` |
| 🟢 Green | `#4CAF50` |
| 🩵 Teal | `#009688` |

---

## 🔐 Security

| Feature | Detail |
|---|---|
| **Firestore Rules** | Strict read/write rules per collection (`firestore.rules`) |
| **E2EE Chat** | End-to-end encryption architecture (see [`E2EE_ARCHITECTURE.md`](./E2EE_ARCHITECTURE.md)) |
| **Encrypted Prefs** | `EncryptedSharedPreferences` for sensitive local data |
| **Admin Role** | Admin Dashboard only accessible to verified admin users |
| **FCM Token** | Device token managed securely per user session |

---

## 📦 Domain Models

```kotlin
User          → id · displayName · handle · bio · faculty · year · avatarEmoji
               profileImageUrl · isVerified · isOnline · stats (posts/followers/following)

Post          → id · authorId · text · mediaUrls · likes · comments · visibility

Comment       → id · postId · authorId · text · timestamp

Message       → id · chatId · senderId · text · mediaUrl · type · timestamp

Group         → id · name · category · privacy · members · coverEmoji · posts

Event         → id · title · date · location · coverUrl · attendees · description

Notification  → id · type · actorId · targetId · timestamp · isRead
```

---

## 📋 Roadmap

- [x] Authentication (Email, Google, OTP)
- [x] Social Feed with media support
- [x] Real-time Chat (1-on-1 & group)
- [x] Voice & Video Calls (WebRTC)
- [x] Groups (public & private)
- [x] Events with RSVP
- [x] User Profiles & Friends system
- [x] Push Notifications (FCM)
- [x] Dark / Light theme + accent colors
- [x] Admin Dashboard
- [ ] Stories with reactions
- [ ] Live streaming
- [ ] In-app marketplace for students

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you'd like to change.

```bash
# Create a feature branch
git checkout -b feature/your-feature-name

# Commit your changes
git commit -m "feat: add your feature"

# Push and open a PR
git push origin feature/your-feature-name
```

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](./LICENSE) file for details.

---

<div align="center">

<br />

**KAMPUS** — Year Three Project &nbsp;|&nbsp; Package `com.example.kampus`

<br />

Built with ❤️ using **Kotlin** + **Jetpack Compose** + **Firebase**

<br />

⭐ If you like this project, give it a star on GitHub!

</div>
