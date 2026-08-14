<div align="center">

# 🎓 KAMPUS

**A modern campus social network built for students**

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android)](https://android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?style=flat-square&logo=firebase)](https://firebase.google.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208)-green?style=flat-square)]()
[![Version](https://img.shields.io/badge/Version-1.0-blue?style=flat-square)]()

</div>

---

## 📖 What is KAMPUS?

**KAMPUS** is a full-featured campus social media Android app designed for students. It lets you connect with classmates, share posts, join study groups, discover events, and chat — all in one place.

Think of it as a social platform built specifically for campus life.

---

## ✨ Features at a Glance

| Feature | Description |
|---|---|
| 🔐 **Authentication** | Sign up / log in with Email, Password, Google, or OTP |
| 📰 **Social Feed** | Create posts with text, images & videos. Like, comment, share |
| 💬 **Real-time Chat** | Direct messages + group chats with voice/video call support |
| 👥 **Groups** | Join or create public/private groups by interest or subject |
| 📅 **Events** | Browse and RSVP to campus events. Create your own |
| 👤 **Profiles** | Personal profile, followers/following, activity feed |
| 🔔 **Notifications** | Push notifications via Firebase Cloud Messaging (FCM) |
| 🔍 **Search** | Search for people, groups, events, and posts |
| 📖 **Stories** | Short-lived story posts with camera capture |
| 🌙 **Dark / Light Mode** | Toggle theme + choose accent color |
| 🌐 **Localization** | Multi-language UI string support |
| 🛡️ **Admin Dashboard** | Manage users, review reported content |

---

## 📱 Screenshots & Screens

| Screen | Purpose |
|---|---|
| Splash | Launch screen |
| Onboarding | First-time app intro |
| Login / Register | User authentication |
| OTP / Forgot Password | Password recovery flow |
| Home (Feed) | Main social feed |
| Create Post | Post composer with media |
| Post Detail | Full post with comments |
| Groups List | Browse / join groups |
| Group Detail | Group page & posts |
| Create Group | Group creation form |
| Event List | Campus events feed |
| Event Detail | Event info + RSVP |
| Create Event | Event creation form |
| Chat List | All conversations |
| Chat Screen | 1-on-1 or group chat |
| Notifications | All app notifications |
| Profile | Own user profile |
| Public Profile | View another user's profile |
| Edit Profile | Update name, bio, avatar |
| Settings | App preferences |
| Appearance | Theme / accent / font size |
| Admin Dashboard | Admin-only management view |

---

## 🏗️ Architecture

KAMPUS follows **Clean Architecture + MVVM** — a widely used industry-standard pattern that keeps code organized and easy to maintain.

```
┌────────────────────────────────────────┐
│          UI Layer (Screens)            │  ← What the user sees (Jetpack Compose)
│         ViewModels                     │  ← Holds state, handles logic
├────────────────────────────────────────┤
│         Domain Layer                   │  ← Business rules
│   Models · Repository Interfaces       │
│         Use Cases                      │
├────────────────────────────────────────┤
│          Data Layer                    │  ← Talks to the internet
│  Firebase · Supabase · Repository Impl │
└────────────────────────────────────────┘
```

> **Simple explanation:** The app is split into 3 clean layers. The UI layer only knows about ViewModels. ViewModels only know about Use Cases. Use Cases talk to the Data layer. Nothing is mixed up — easy to debug and extend.

---

## 🔧 Tech Stack

### Core
| Library | What it does |
|---|---|
| **Kotlin** | Main programming language |
| **Jetpack Compose** | Declarative UI framework (no XML layouts) |
| **Material 3** | Google's modern design system |
| **Navigation Compose** | Screen-to-screen navigation |
| **Kotlin Coroutines** | Async tasks without callbacks |
| **StateFlow** | Reactive state management in ViewModels |

### Backend & Database
| Library | What it does |
|---|---|
| **Firebase Auth** | Email/Password + Google Sign-In |
| **Firebase Firestore** | Real-time NoSQL database for posts, chats, users |
| **Firebase Storage** | Cloud file storage |
| **Firebase Messaging (FCM)** | Push notifications |
| **Supabase Storage** | Image/media uploads |

### Media & UI
| Library | What it does |
|---|---|
| **Coil** | Fast async image loading |
| **UCrop** | Image cropping editor |
| **CameraX** | Camera capture for stories |
| **Media3 / ExoPlayer** | Video playback in posts |
| **WebRTC** | Real-time voice & video calls |

### Other
| Library | What it does |
|---|---|
| **DataStore** | Saves user preferences locally |
| **WorkManager** | Background file uploads |
| **OkHttp** | WebSocket signaling for calls |
| **Security Crypto** | Encrypted local storage (E2EE support) |

---

## 📁 Project Structure

```
KAMPUS-APP/
│
├── app/src/main/java/com/example/kampus/
│   │
│   ├── MainActivity.kt          # Entry point of the app
│   ├── KampusApplication.kt     # App-level setup (Hilt/DI init)
│   │
│   ├── navigation/
│   │   ├── NavGraph.kt          # All screen routes & navigation logic
│   │   └── Screen.kt            # Route name constants
│   │
│   ├── domain/                  # 🧠 Business logic (pure Kotlin, no Android)
│   │   ├── model/               # Data models: User, Post, Group, Event…
│   │   ├── repository/          # Repository interfaces (contracts)
│   │   └── usecase/             # Use cases: LoginUseCase, CreatePostUseCase…
│   │
│   ├── data/                    # 🌐 Data sources
│   │   ├── remote/              # Firebase & Supabase data sources
│   │   └── repository/          # Repository implementations
│   │
│   ├── di/                      # 💉 Dependency Injection modules (Hilt)
│   │
│   ├── ui/                      # 🎨 All screens & components
│   │   ├── theme/               # Colors, Typography, ThemeController
│   │   ├── components/          # Reusable UI pieces (buttons, avatars…)
│   │   ├── auth/                # Login, Register, OTP screens
│   │   ├── feed/                # Home feed, post creation
│   │   ├── chat/                # Chat list, chat screen, message bubbles
│   │   ├── events/              # Event list, detail, create
│   │   ├── groups/              # Group list, detail, create
│   │   ├── post/                # Post detail, comments
│   │   ├── profile/             # Profile, settings, friends
│   │   ├── notifications/       # Notification screen
│   │   ├── search/              # Search screen
│   │   ├── story/               # Story viewer/camera
│   │   ├── onboarding/          # Onboarding slides
│   │   ├── splash/              # Splash screen
│   │   ├── admin/               # Admin tools
│   │   └── localization/        # UI strings (multi-language)
│   │
│   ├── viewmodel/               # Shared ViewModels (e.g. GroupsViewModel)
│   └── utils/                   # Helper functions: DateUtils, ImageUtils…
│
├── firestore.rules              # Firestore database security rules
├── firebase.json                # Firebase project configuration
├── gradle/libs.versions.toml    # All dependency versions in one place
└── app/build.gradle.kts         # App build configuration
```

---

## ⚙️ Build Configuration

```
Application ID : com.example.kampus
Min SDK        : 26  (Android 8.0 Oreo and above)
Target SDK     : 36  (Android 16)
Compile SDK    : 36
Version        : 1.0 (versionCode 1)
Java Version   : 11
Language       : Kotlin
Build System   : Gradle with Kotlin DSL
```

---

## 🎨 Theme System

KAMPUS has a custom **`ThemeController`** singleton that drives the entire app's look:

```kotlin
ThemeController.isDark    // true = dark mode, false = light mode
ThemeController.accent    // Accent color: Blue | Purple | Pink | Red | Orange | Green | Teal
ThemeController.fontScale // Global font size scale
```

Colors, borders, and backgrounds across all screens react to these values in real time — no app restart needed.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio **Hedgehog** or later
- JDK 11+
- A `google-services.json` file from your Firebase project (place in `app/`)
- A Supabase project URL + anon key (configured in `di/SupabaseModule.kt`)

### Run the App

```bash
# Clone the repo
git clone <your-repo-url>
cd KAMPUS-APP

# Open in Android Studio and sync Gradle, then run on emulator or device
./gradlew assembleDebug
```

Or just press ▶️ **Run** in Android Studio.

### Build Commands

```bash
./gradlew build            # Full build
./gradlew assembleDebug    # Debug APK  →  app/build/outputs/apk/debug/
./gradlew assembleRelease  # Release APK → app/build/outputs/apk/release/
./gradlew test             # Unit tests
```

---

## 🗺️ Navigation Flow

```
Splash
  ├── First launch?  → Onboarding → Login/Register → Home
  └── Returning user → Home
         ├── Feed (Posts, Stories)
         ├── Groups
         ├── Events
         ├── Chat
         └── Profile → Settings → Appearance / Account / Privacy …
```

Deep links are supported for:
- `kampus://profile/{userId}`
- `https://kampus.app/profile/{userId}`

---

## 🔐 Security & Privacy

- Firebase Security Rules protect all Firestore reads/writes (`firestore.rules`)
- E2EE (End-to-End Encryption) architecture for chat — see [`E2EE_ARCHITECTURE.md`](./E2EE_ARCHITECTURE.md)
- Encrypted local preferences via `EncryptedSharedPreferences`
- Admin role required to access the Admin Dashboard

---

## 📦 Key Domain Models

| Model | Key Fields |
|---|---|
| `User` | id, name, email, avatar, role, bio |
| `Post` | id, authorId, text, mediaUrls, likes, comments |
| `Comment` | id, postId, authorId, text, timestamp |
| `Message` | id, chatId, senderId, text, mediaUrl, type |
| `Group` | id, name, category, privacy, members, coverEmoji |
| `Event` | id, title, date, location, coverUrl, attendees |
| `Notification` | id, type, actorId, targetId, timestamp |

---

## 👥 Team

> **KAMPUS** — Year Three Project  
> Package: `com.example.kampus`  
> Repo: `RETH-CHANRITH/KAMPUS`

---

<div align="center">
  <sub>Built with ❤️ using Kotlin + Jetpack Compose</sub>
</div>
