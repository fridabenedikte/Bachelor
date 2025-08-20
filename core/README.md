# 🧠 Core Module – MojiFlow

This module serves as the central hub for all game logic in MojiFlow. It orchestrates game state transitions, entity behavior, level progression, and other core systems that define how the game works. By isolating logic here, the module ensures a consistent and maintainable game loop, regardless of the platform.

> 💡 **Note:** This module is UI-agnostic and does **not** depend on any platform-specific frameworks like Android Views or Jetpack Compose.

---

## 📂 Directory Structure

```
core/
├── data/           # Static data models (e.g., tutorial data, level data)
├── definitions/    # Definitions for levels, tutorials, emoji types, etc.
├── entities/       # Core models for moveable/interactable objects
├── states/         # Game state management system
├── ui/             # Abstract UI interfaces for cross-platform behavior
├── util/           # Shared utility functions (math, collision, logging)
├── CoreManager.kt  # Entry point for managing core game logic
```

---

## 📁 Folder Overview

### `data/` – Static Data
Holds static models and game configuration files.

**Examples:**
- `Level.kt` – level properties and variables
- `Platform.kt` – platform properties and variables

---

### `definitions/` – Game Definitions
Contains structured data that defines game content and behaviors.

**Examples:**
- `LevelDefinitions.kt` – holds level metadata and structure
- `TutorialDefinitions.kt` – tutorial configuration
- `EmojiTypes.kt` – emoji behaviors and categories

---

### `entities/` – Game Entities
Includes data classes for objects that appear or interact in the game world.

**Examples:**
- `Player.kt`, `Entity.kt`, `Emoji.kt`
- Encapsulates position, movement, and collision

---

### `states/` – Game State System
Implements the core state machine for transitioning between gameplay, menus, tutorials, and other game phases.

**Examples:**
- `GameState.kt`, `PlayState.kt`, `TutorialState.kt`
- `GameStateManager.kt` – manages transitions

---

### `ui/` – Platform-Independent UI Contracts
Defines interfaces for rendering and input to be implemented separately on Android and Desktop.

**Examples:**
- `GameStateFlow.kt`, `TutorialState.kt`  – holds passable state information for the android module to handle

---

### `util/` – Utilities
General helper classes and core logic tools.

**Examples:**
- `Vector2f.kt`, `AABB.kt` – vector math and collision
- `Logger.kt` – simple structured logging

---

## 🧭 Principles

- ✅ **No Android/Compose dependencies**
- 🔁 **Reusable Kotlin logic** for full code sharing
- 🧪 **Easily testable** outside any UI context
- 🧩 **Modular and scalable** architecture for level and feature expansion
