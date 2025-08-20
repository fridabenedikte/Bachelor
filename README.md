# **MojiFlow**
The GitHub repository for IT2901 G03, a task issued by **Zedge**.

## 🚀 **Project Overview**
MojiFlow is a **cross-platform game** built with **modular architecture**, ensuring scalability for Android and potential desktop expansion.

---

## 🔧 **Development Environment**
| Tool               | Version            |
|--------------------|--------------------|
| **Android Studio** | 2024.2.2 (Ladybug) |
| **Java JDK**       | 21                 |
| **Android SDK**    | API 35             |
| **Gradle**         | 8.10.2             |

---

## 🛠 **Setup Instructions**
### **1️⃣ Clone the Repository**
```bash
git clone https://github.com/EmojiBachelor/EmojiGame.git
cd EmojiGame
```

### **2️⃣ Set Up Java & Android SDK**
- Open **Android Studio** → **File** → **Settings** (`Preferences` on Mac).
- Go to **Build, Execution, Deployment** → **Build Tools** → **Gradle**.
- Under **Gradle JDK**, select **`21`** (or download it if missing).

### **3️⃣ Configure the Emulator (Optional)**
- Click **Device Manager** (right side in Android Studio).
- If missing, go to **Tools** → **Device Manager**.
- Create a new virtual device (**VanillaIceCream, API 35, Android 15**).
- Launch the emulator.

### **4️⃣ Build the `reslib` Module (Required)**
Before running the app, **build `reslib` first**:
```bash
./gradlew setup
```
if this doesn't create the reslib-release.aar file run
```bash
./gradlew :reslib:build
```

### **5️⃣ Run the Game**
- **From Android Studio**
   1. Open **Run/Debug Configurations** (`Run` → `Edit Configurations`).
   2. Select **`android`** under **Android App**.
   3. Ensure **Deploy** is set to `Default APK`.
   4. Ensure **Launch** is set to `Default Activity`.
   5. Click **Apply** → **OK**.
   6. Click **Run ▶️** to start the game.

- **From the Command Line**
  ```bash
  ./gradlew clean build
  ./gradlew installDebug
  ./gradlew run
  ```
  This compiles and installs the app on your emulator or connected device.

---

## 📂 **Project Structure**
MojiFlow is **modular**, separating core logic from platform-specific implementations.

```
MojiFlow/
├── android/      # Android frontend module
├── core/         # Core game logic (shared across platforms)
├── reslib/       # Shared resources (textures, sounds, utilities)
├── gradle/       # Gradle build system files
├── .github/      # GitHub workflows & CI/CD configurations
├── build.gradle.kts  # Project-wide Gradle build script
├── settings.gradle.kts  # Gradle settings file
├── README.md     # Main project documentation
```

### 📌 **Module Breakdown**
| Module         | Description                                                      |
|----------------|------------------------------------------------------------------|
| **`android/`** | Handles **UI, input, and rendering** for Android devices.        |
| **`core/`**    | Contains **game logic, movement systems, and state management**. |
| **`reslib/`**  | Stores **textures, sound files, shared utilities** for reuse.    |

---

## 🎯 **Contributing**
### **Commit Message Guidelines**
Following a structured commit format helps maintain a **clean Git history**.

**Format:**
```bash
<type>: <short description> (#IssueNumber)
```
**Example:**
```bash
feat: Added exploding fireworks on victory. #21
fix: Fixed music not playing on pause menu. Closes #11, #13
```

### **Commit Types**
| Type       | Description                                       |
|------------|---------------------------------------------------|
| `feat`     | Introduces a new feature                          |
| `fix`      | Fixes a bug                                       |
| `refactor` | Code change that doesn't add features or fix bugs |
| `docs`     | Documentation update                              |
| `test`     | Adding or modifying tests                         |
| `clean`    | Code cleanup, removes debugging                   |

---

## ❌ **Common Issues & Fixes**
### **1. Missing `reslib-release.aar` Error**
If you see:
```log
A problem occurred configuring root project 'EmojiGame'.
File 'reslib-release.aar' not found!
```
Run:
```bash
./gradlew setup
```
This builds the missing AAR file.

### **2. Gradle Build Fails on First Run**
Try **syncing Gradle** manually:
- **Android Studio** → **File** → **Sync Project with Gradle Files**  
  OR run:
```bash
./gradlew clean
./gradlew build
```

### **3. Emulator Not Showing Up**
- Ensure the **Android emulator** is running.
- Run:
  ```bash
  adb devices
  ```
  If no devices appear, restart **ADB**:
  ```bash
  adb kill-server
  adb start-server
  ```

### **4. Reset App State (Clear All Progress)**
If you want to **reset the app completely** (e.g., remove all unlocked levels and progress):

```bash
adb shell pm clear com.emojigame.android
```
---

## 🎮 **Gameplay Overview**

MojiFlow is a **puzzle-solving game** using emoji-based mechanics.

* Players navigate emoji-based levels.
* The game supports **multiple themes and difficulty levels**.
* The UI is built using **XML layout files and View Binding** on Android, with shared **Kotlin Multiplatform** core logic.
# Bachelor
