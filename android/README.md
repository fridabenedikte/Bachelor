# Android Module

This module contains Android-specific UI implementations, utilities, and platform-specific functionality for the **Android version** of the game. It relies on the **core** module for shared logic and models.

## 📁 Directory Structure
```
android/
├── ui/              # UI components (activities, fragments, views)
│   ├── fragments/   # Fragments used throughout the app
│   │   └── dialogs/ # Dialog-specific fragments
│   └── views/       # Custom views or view-related classes
├── util/            # Android-specific utilities (e.g., permissions, SharedPreferences)
```

---

## 🚀 Setup & Build Instructions
### **1️⃣ First-Time Setup**
Before building, ensure dependencies are installed:
```sh
gradle setup
```
This task:
- Runs `npm i` for necessary packages.
- Builds the **reslib** module to generate `reslib-release.aar`.

### **2️⃣ Building the Android Module**
Once setup is complete, build the project with:
```sh
gradle build
```
If you face issues, ensure **reslib** is correctly built:
```sh
gradle :reslib:assembleRelease
```

---

## 📌 **Directory Breakdown & Guidelines**

### **1️⃣ `ui/` – UI Components for Android**
Holds **Android-specific UI elements**, including:
- **Fragments & Activities** (e.g., `PuzzleFragment.kt` for gameplay UI).
- **Custom Views** (e.g., `PuzzleCanvasView.kt` for optimized drawing).
- **Adapters** for lists or dynamic UI components.
- **Layouts & Themes** (XML styles, colors, and bindings).

#### 🔹 **Performance Optimization: `PuzzleCanvasView`**
Instead of using multiple `ImageView`s, the player is drawn directly onto a **Canvas**, reducing UI lag.

---

### **2️⃣ `util/` – Android-Specific Utilities**
This directory contains **helper functions** for:
- **SharedPreferences management**.
- **Permissions handling**.
- **Android lifecycle utilities**.
- **Networking helpers** (if needed).

---

## ✅ Guidelines
- **Do NOT duplicate core logic** – Keep all shared code inside the `core` module.
- **Optimize UI performance** – Use Canvas drawing instead of excessive ImageViews.
- **Keep Android-specific code in this module** – No platform-independent logic here.

This module ensures **Android compatibility** while leveraging the shared logic from **core**.

