# KAMPUS

A campus social networking Android app for university students — built with Kotlin, Jetpack Compose, and Firebase.

![Platform](https://img.shields.io/badge/Platform-Android%208.0%2B-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Language-Kotlin%202.2.0-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Database](https://img.shields.io/badge/Database-Firebase%20Firestore-FFCA28?logo=firebase&logoColor=black)
![Storage](https://img.shields.io/badge/Storage-Supabase-3ECF8E?logo=supabase&logoColor=white)
![Version](https://img.shields.io/badge/Version-1.0-lightgrey)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## Screenshots

| Groups | Events | Profile |
|:---:|:---:|:---:|
| ![Groups](screenshots/groups_light_fixed.png) | ![Events](screenshots/events_light_fixed.png) | ![Profile](screenshots/profile_light_fixed.png) |

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Data Models](#data-models)
- [Navigation](#navigation)
- [Theme System](#theme-system)
- [Security](#security)
- [Getting Started](#getting-started)
- [Build](#build)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

KAMPUS is a full-featured social media platform designed specifically for university life. It allows students to share posts, join interest-based groups, discover and RSVP to campus events, message classmates in real time, and manage their academic profile — all within a single native Android app.

The project is built from the ground up using modern Android development best practices: Clean Architecture, MVVM, 100% Jetpack Compose UI, and Firebase as the primary backend.

> Year Three University Project — Package `com.example.kampus`

---

## Features

### Authentication
- Email and password sign-up / login
- Google Sign-In via OAuth
- OTP verification (email or phone)
- Forgot password with reset flow
- Persistent session — no re-login on app restart

### Social Feed
- Create posts with text, images, and videos
- Like, comment, and share posts
- Post visibility controls (public / friends / private)
- Tag people and add location or feeling
- Stories — short-lived posts with camera or gallery media
- Real-time feed updates

### Chat
- One-on-one direct messages
- Group chat rooms
- Voice calls (WebRTC)
- Video calls (WebRTC)
- Message reactions, reply threads
- Seen indicators and online status
- Call history log

### Groups
- Browse public groups and request to join private ones
- Create groups with custom emoji, cover color, category, and privacy
- My Groups tab and Discover tab with live search
- Join request management for private groups
- Group posts and activity

### Events
- Campus event feed with cover image, date, location
- RSVP / mark interested
- Create events with type, capacity, speaker, tags, deadline, and certificate info
- Online and in-person event support
- Paid event flag
- Event comments

### Profile
- Personal profile: display name, handle, bio, faculty, year, avatar
- Cover image and profile photo upload
- Followers / following counts
- Friend request system (send, accept, reject, block)
- Activity feed (recent posts, events, group actions)
- Public profile view for other users
- Discover People screen

### Settings
- Edit profile
- Account settings
- Notification preferences
- Privacy and security controls
- Appearance (theme, accent color, font scale)
- Language and region
- Blocked users management
- Help and support
- About screen

### Notifications
- Real-time push notifications via Firebase Cloud Messaging
- In-app notification screen
- Deep links from notification tap (open chat, post, or notification list)

### Search
- Global search across people, posts, groups, and events simultaneously

### Admin
- Admin-only dashboard
- User management
- Reported content review and moderation

---

## Architecture

KAMPUS follows **Clean Architecture** with an **MVVM** presentation pattern.

```
┌─────────────────────────────────────────────────────┐
│                  Presentation Layer                  │
│                                                      │
│   Jetpack Compose Screens  ←→  ViewModels            │
│   (StateFlow / collectAsState)                       │
├─────────────────────────────────────────────────────┤
│                    Domain Layer                      │
│                                                      │
│   Data Models  ·  Repository Interfaces              │
│   Use Cases (business logic lives here)             │
├─────────────────────────────────────────────────────┤
│                     Data Layer                       │
│                                                      │
│   Repository Implementations                         │
│   Firebase Firestore  ·  Firebase Auth               │
│   Firebase Storage    ·  Supabase Storage            │
└─────────────────────────────────────────────────────┘
```

**Key principles:**
- The UI layer never communicates directly with the database.
- ViewModels call Use Cases; Use Cases call Repository interfaces; only the Data layer touches Firebase or Supabase.
- The Domain layer is pure Kotlin — zero Android dependencies — making it easy to test.
- State is managed with `MutableStateFlow` in ViewModels and observed in Compose via `collectAsState`.

---

## Tech Stack

### Core

| Library | Version | Purpose |
|---|---|---|
| Kotlin | 2.2.0 | Primary language |
| Jetpack Compose | BOM 2024.x | Declarative UI framework |
| Material Design 3 | — | Component library and design system |
| Navigation Compose | 2.7.7 | Type-safe screen navigation |
| Kotlin Coroutines | 1.7.3 | Async and reactive programming |
| Lifecycle / StateFlow | 2.10.0 | Observable state in ViewModels |
| Hilt / Koin | — | Dependency injection |

### Backend and Database

| Library | Version | Purpose |
|---|---|---|
| Firebase BoM | 33.12.0 | Firebase version management |
| Firebase Auth | — | Authentication (email, Google) |
| Firebase Firestore | — | Real-time NoSQL cloud database |
| Firebase Storage | — | File and media storage |
| Firebase Messaging | — | Push notifications (FCM) |
| Supabase-kt | 2.2.2 | Image and media uploads |
| Ktor OkHttp | 2.3.0 | HTTP client for Supabase |

### Media

| Library | Version | Purpose |
|---|---|---|
| Coil Compose | 2.7.0 | Async image loading from URL |
| UCrop | 2.2.10 | In-app image cropping and editing |
| CameraX | 1.4.1 | Camera capture for stories |
| Media3 / ExoPlayer | 1.3.1 | In-feed video playback |

### Communication

| Library | Version | Purpose |
|---|---|---|
| WebRTC SDK | 125.6422.07 | Peer-to-peer voice and video calls |
| OkHttp | 4.11.0 | WebSocket signaling for call setup |

### Storage and Security

| Library | Version | Purpose |
|---|---|---|
| DataStore Preferences | 1.1.7 | Persistent user settings |
| WorkManager | 2.8.1 | Background file uploads with retry |
| Security Crypto | 1.1.0-alpha06 | EncryptedSharedPreferences for E2EE |

---

## Project Structure

```
app/src/main/java/com/example/kampus/
│
├── MainActivity.kt                   # App entry point, Compose host
├── KampusApplication.kt              # Application class
│
├── navigation/
│   ├── NavGraph.kt                   # Complete navigation graph, all routes and arguments
│   └── Screen.kt                     # Route name constants
│
├── domain/                           # Pure Kotlin — no Android or Firebase imports
│   ├── model/
│   │   ├── User.kt                   # User, UserStats, Friend, FriendRequest
│   │   ├── Post.kt                   # Post model
│   │   ├── Comment.kt                # Comment model
│   │   ├── Message.kt                # Chat message model
│   │   ├── Group.kt                  # Group model
│   │   ├── Event.kt                  # Event model
│   │   └── Notification.kt           # Notification model
│   ├── repository/
│   │   ├── IAuthRepository.kt
│   │   ├── IUserRepository.kt
│   │   ├── IPostRepository.kt
│   │   ├── IChatRepository.kt
│   │   ├── IEventRepository.kt
│   │   └── IGroupRepository.kt
│   └── usecase/
│       ├── LoginUseCase.kt
│       ├── RegisterUseCase.kt
│       ├── CreatePostUseCase.kt
│       ├── LikePostUseCase.kt
│       ├── GetFeedPostsUseCase.kt
│       ├── SendMessageUseCase.kt
│       ├── JoinGroupUseCase.kt
│       ├── GetGroupsUseCase.kt
│       ├── RsvpEventUseCase.kt
│       ├── GetEventsUseCase.kt
│       ├── UploadImageUseCase.kt
│       └── ProfileUseCases.kt
│
├── data/
│   ├── remote/
│   │   ├── FirebaseDataSource.kt     # Firestore operations
│   │   ├── FirebaseAuthSource.kt     # Firebase Auth operations
│   │   └── SupabaseDataSource.kt     # Supabase storage operations
│   └── repository/
│       ├── AuthRepositoryImpl.kt
│       ├── UserRepositoryImpl.kt
│       ├── PostRepositoryImpl.kt
│       ├── ChatRepositoryImpl.kt
│       ├── EventRepositoryImpl.kt
│       └── GroupRepositoryImpl.kt
│
├── di/
│   ├── AppModule.kt
│   ├── FirebaseModule.kt
│   ├── SupabaseModule.kt
│   └── RepositoryModule.kt
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt                  # App color palette
│   │   ├── Type.kt                   # Typography definitions
│   │   ├── Theme.kt                  # MaterialTheme setup
│   │   ├── KampusTheme.kt            # Custom theme wrapper
│   │   ├── ThemeController.kt        # Global dark/light/accent/fontScale state
│   │   └── AppSettingsStore.kt       # DataStore-backed settings persistence
│   │
│   ├── components/                   # Shared reusable composables
│   │   ├── BottomNavBar.kt
│   │   ├── TopAppBar.kt
│   │   ├── InputField.kt
│   │   ├── GradientButton.kt
│   │   ├── AvatarImage.kt
│   │   ├── LoadingIndicator.kt
│   │   ├── ConfirmDialog.kt
│   │   └── EmptyState.kt
│   │
│   ├── splash/
│   │   └── SplashScreen.kt
│   │
│   ├── onboarding/
│   │   ├── OnboardingScreen.kt
│   │   ├── OnboardingViewModel.kt
│   │   ├── Onboardingpage.kt
│   │   └── OnboardingIllustration.kt
│   │
│   ├── auth/
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── OtpScreen.kt
│   │   ├── ForgotPasswordScreen.kt
│   │   ├── ResetPasswordScreen.kt
│   │   ├── AuthViewModel.kt
│   │   ├── AuthState.kt
│   │   └── AuthColors.kt
│   │
│   ├── feed/
│   │   ├── FeedScreen.kt             # Main home/feed screen (87 KB)
│   │   ├── FeedViewModel.kt          # Feed state, post CRUD, reactions (67 KB)
│   │   ├── CreatePostScreen.kt       # Post composer with media picker
│   │   ├── CreatePostBar.kt
│   │   ├── PostItem.kt
│   │   ├── ShareComposerDialog.kt
│   │   ├── MediaCropper.kt
│   │   └── PickerModals.kt           # Gallery + camera picker sheets
│   │
│   ├── post/
│   │   ├── PostDetailScreen.kt
│   │   ├── PostViewModel.kt
│   │   └── CommentItem.kt
│   │
│   ├── chat/
│   │   ├── ChatListScreen.kt         # All conversations (83 KB)
│   │   ├── Chatscreen.kt             # Chat UI with reply and reactions (86 KB)
│   │   ├── ChatViewModel.kt          # Real-time messaging, calls (85 KB)
│   │   ├── CallScreen.kt             # Voice / Video call UI (40 KB)
│   │   ├── MessageBubble.kt          # Message rendering (59 KB)
│   │   ├── ChatItem.kt
│   │   ├── CallExtrasControls.kt
│   │   ├── CallHistory.kt
│   │   ├── CreateStoryScreen.kt      # Story camera capture (70 KB)
│   │   └── DraggablePreview.kt
│   │
│   ├── groups/
│   │   ├── GroupListScreen.kt        # Browse, search, join groups
│   │   ├── GroupDetailScreen.kt
│   │   ├── CreateGroupScreen.kt
│   │   ├── GroupViewModel.kt         # Join, request, create, filter
│   │   ├── GroupItem.kt
│   │   ├── Groupdata.kt              # GroupData model and GroupColors
│   │   └── GroupColors.kt
│   │
│   ├── events/
│   │   ├── EventListScreen.kt
│   │   ├── EventDetailScreen.kt
│   │   ├── CreateEventScreen.kt
│   │   ├── CreateEventPostScreen.kt
│   │   ├── EventViewModel.kt
│   │   ├── EventItem.kt
│   │   └── MediaPickerHelper.kt
│   │
│   ├── profile/
│   │   ├── ProfileScreen.kt          # Own profile (57 KB)
│   │   ├── ProfileViewModel.kt       # Profile data and actions (62 KB)
│   │   ├── PublicProfileScreen.kt    # Other user's profile (47 KB)
│   │   ├── PublicProfileViewModel.kt
│   │   ├── EditProfileScreen.kt
│   │   ├── FriendsScreen.kt
│   │   ├── FriendsViewModel.kt
│   │   ├── FriendRequestsScreen.kt
│   │   ├── DiscoverPeopleScreen.kt
│   │   ├── PublicFriendsScreen.kt
│   │   ├── SettingsScreen.kt
│   │   ├── AccountSettingsScreen.kt
│   │   ├── AppearanceSettingsScreen.kt
│   │   ├── NotificationSettingsScreen.kt
│   │   ├── PrivacySecurityScreen.kt
│   │   ├── LanguageRegionScreen.kt
│   │   ├── BlockedUsersScreen.kt
│   │   ├── HelpSupportScreen.kt
│   │   └── AboutScreen.kt
│   │
│   ├── notifications/
│   │   ├── NotificationScreen.kt
│   │   ├── NotificationViewModel.kt
│   │   └── NotificationItem.kt
│   │
│   ├── search/
│   │   └── SearchScreen.kt
│   │
│   ├── story/                        # Story viewer
│   ├── activity/                     # Activity log screens
│   ├── admin/
│   │   ├── AdminDashboardScreen.kt
│   │   ├── ManageUsersScreen.kt
│   │   ├── ReportedContentScreen.kt
│   │   └── AdminViewModel.kt
│   └── localization/
│       └── UiStrings.kt              # Multi-language string table
│
├── viewmodel/
│   └── GroupsViewModel.kt            # Shared ViewModel for groups nav graph
│
└── utils/
    ├── Constants.kt
    ├── Resource.kt                   # Sealed Result wrapper
    ├── Extensions.kt
    ├── DateUtils.kt
    ├── ImageUtils.kt
    ├── RoleUtils.kt
    └── ActivityLogger.kt
```

---

## Data Models

### User

```kotlin
data class User(
    val id               : String,
    val displayName      : String,
    val handle           : String,        // @username
    val bio              : String,
    val email            : String,
    val phone            : String,
    val faculty          : String,
    val year             : String,
    val location         : String,
    val avatarEmoji      : String,
    val profileImageUrl  : String,
    val coverImageUrl    : String,
    val stats            : UserStats,     // posts, followers, following
    val isVerified       : Boolean,
    val isOnline         : Boolean,
    val lastActive       : LocalDateTime?,
    val createdAt        : LocalDateTime?,
)
```

### Event

```kotlin
data class Event(
    val id                   : String?,
    val title                : String,
    val description          : String?,
    val location             : String?,
    val imageUrl             : String?,
    val ownerId              : String,
    val startDate            : Long?,
    val endDate              : Long?,
    val eventType            : String?,
    val capacity             : Int?,
    val registrationDeadline : String?,
    val website              : String?,
    val onlineEvent          : Boolean,
    val certificateAvailable : Boolean,
    val paidEvent            : Boolean,
    val speaker              : String?,
    val tags                 : List<String>?,
    val allowGuest           : Boolean,
)
```

### Group (GroupData)

```kotlin
data class GroupData(
    val id           : Int,
    val name         : String,
    val category     : String,
    val coverColor1  : Color,
    val coverColor2  : Color,
    val coverEmoji   : String,
    val description  : String,
    val members      : Int,
    val posts        : Int,
    val privacy      : String,        // "public" or "private"
    val ownerId      : String,
)
```

### Other Models

| Model | Key Fields |
|---|---|
| `Comment` | id · postId · authorId · text · timestamp |
| `Message` | id · chatId · senderId · text · mediaUrl · type · timestamp |
| `Notification` | id · type · actorId · targetId · timestamp · isRead |
| `FriendRequest` | id · fromUserId · toUserId · status (PENDING / ACCEPTED / REJECTED / BLOCKED) |

---

## Navigation

All routes are defined in [`NavGraph.kt`](app/src/main/java/com/example/kampus/navigation/NavGraph.kt) and route names in `Routes` object.

```
Splash
├── (first launch)  →  Onboarding  →  Login / Register
└── (returning)     →  Home (Feed)
                          ├── Post Detail (postId)
                          ├── Create Post
                          ├── Notifications
                          ├── Search
                          │
                          ├── Groups Graph
                          │     ├── Group List
                          │     ├── Group Detail (groupId)
                          │     └── Create Group
                          │
                          ├── Event List
                          │     ├── Event Detail (eventId)
                          │     └── Create Event
                          │
                          ├── Chat List
                          │     └── Chat Screen (chatId)
                          │           └── Call Screen (voice / video)
                          │
                          └── Profile
                                ├── Edit Profile
                                ├── Friends
                                ├── Friend Requests
                                ├── Discover People
                                ├── Public Profile (userId)
                                └── Settings
                                      ├── Account
                                      ├── Notifications
                                      ├── Privacy & Security
                                      ├── Appearance
                                      ├── Language & Region
                                      ├── Blocked Users
                                      ├── Help & Support
                                      └── About
```

**Deep links supported:**
```
kampus://profile/{userId}
https://kampus.app/profile/{userId}
```

**Notification deep links** (from FCM payload extras):
```
openChatId      → navigates to ChatScreen
openPostId      → navigates to PostDetail
openNotifications → navigates to NotificationScreen
```

---

## Theme System

All app theming is controlled by `ThemeController` — a global Compose-observable singleton.

```kotlin
object ThemeController {
    var isDark     : Boolean    // true = dark mode
    var fontScale  : Float      // e.g. 0.85, 1.0, 1.15, 1.3
    var accent     : AppAccent  // chosen accent color
    fun toggle()                // switch dark/light
}
```

**Available accent colors:**

| Key | Color |
|---|---|
| `blue` | `#0D7FFF` (default) |
| `purple` | `#9C27B0` |
| `pink` | `#E91E63` |
| `red` | `#F44336` |
| `orange` | `#FF9800` |
| `green` | `#4CAF50` |
| `teal` | `#009688` |

Theme changes apply immediately across every screen — no restart required. Settings are persisted via `AppSettingsStore` (DataStore).

Each feature area has its own color object (e.g. `GroupColors`, `EventColors`, `ProfileColors`) that resolves light/dark variants based on `ThemeController.isDark`.

---

## Security

| Area | Detail |
|---|---|
| **Firestore Security Rules** | Full server-side rules in `firestore.rules` — read/write access is role and ownership checked per collection |
| **End-to-End Encryption** | E2EE chat architecture documented in `E2EE_ARCHITECTURE.md` |
| **Encrypted Local Storage** | `EncryptedSharedPreferences` via AndroidX Security Crypto for sensitive data |
| **Admin Role Gating** | Admin screens are only accessible when the authenticated user has an admin role |
| **FCM Token Management** | Device tokens are updated per session and removed on logout |

---

## Getting Started

### Requirements

- Android Studio **Hedgehog** (2023.1.1) or later
- JDK 11+
- A Firebase project with **Authentication**, **Firestore**, **Storage**, and **Messaging** enabled
- `google-services.json` — download from [Firebase Console](https://console.firebase.google.com) and place in `app/`
- A Supabase project — add your project URL and anon key in `di/SupabaseModule.kt`

### Clone and Open

```bash
git clone https://github.com/RETH-CHANRITH/KAMPUS.git
cd KAMPUS
```

Open the project in Android Studio, wait for Gradle to sync, then press **Run**.

---

## Build

```bash
# Debug APK (for development/testing)
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Release APK
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk

# Run unit tests
./gradlew test

# Full build check
./gradlew build
```

**Build configuration:**

```
applicationId   com.example.kampus
minSdk          26   (Android 8.0 Oreo and above)
targetSdk       36   (Android 16)
compileSdk      36
versionName     1.0
versionCode     1
jvmTarget       11
```

---

## Roadmap

- [x] Email / Password authentication
- [x] Google Sign-In
- [x] OTP verification
- [x] Social feed with text, image, and video posts
- [x] Like, comment, and share
- [x] Stories
- [x] Real-time direct messaging
- [x] Group chat
- [x] Voice calls (WebRTC)
- [x] Video calls (WebRTC)
- [x] Groups — public and private with join requests
- [x] Events with RSVP, capacity, and detailed info
- [x] User profiles with followers / following
- [x] Friend request system
- [x] Discover People
- [x] Push notifications (FCM) with deep link support
- [x] Global search
- [x] Dark / Light mode
- [x] 7 accent color options
- [x] Font scale setting
- [x] Admin dashboard and content moderation
- [x] Multi-language localization support
- [ ] Story reactions
- [ ] Scheduled events with calendar sync
- [ ] Live streaming
- [ ] Student marketplace

---

## Contributing

1. Fork the repository
2. Create your feature branch

```bash
git checkout -b feature/your-feature-name
```

3. Commit your changes using conventional commits

```bash
git commit -m "feat: add your feature description"
git commit -m "fix: describe the bug you fixed"
git commit -m "docs: update README section"
```

4. Push and open a Pull Request

```bash
git push origin feature/your-feature-name
```

For major changes, please open an issue first to discuss the approach.

---

## License

This project is licensed under the **MIT License**.
See the [LICENSE](./LICENSE) file for full details.

---

*KAMPUS — Year Three University Project*  
*Package: `com.example.kampus` · Repository: [RETH-CHANRITH/KAMPUS](https://github.com/RETH-CHANRITH/KAMPUS)*
