# reslib

`reslib` is a module designed to store shared resources (currently images) used across different 
parts of the project. It provides drawable resources, specifically emoji images, that can be used 
in both the Android and Desktop modules.

## How to Interact with `reslib`

This module includes drawable resources (images as emojis) that can be accessed by other modules, 
like the Android app. The resources are packaged into an AAR file and are shared across the project.

### How to Add New Emojis to the Spritesheet

All emojis in the app are loaded from the spritesheet [`emojis.png`](src/main/res/drawable/emojis.png) 
located in `src/main/res/drawable`. If you want to add new emojis, the spritesheet must be updated. 
Follow these steps:

1. **Find and download emojis**

Visit [emojigraph.org](https://emojigraph.org/) to find and download the emojis you want to use. 
The site has a wide selection and makes it easy to save emojis as PNG files.

2. **Create a new spritesheet**

Use a tool like the [codeshack.io Sprite Sheet Generator](https://codeshack.io/images-sprite-sheet-generator/) 
to create an updated spritesheet:

    - Upload the existing `emojis.png`
    - Add the new emoji images you downloaded
    - Generate and download the new spritesheet
    - Replace the old `emojis.png` in the `drawable` folder with the new one

3. **Update the code**

   After updating the spritesheet, the following files need to be updated:

    - `EmojiDefinitions.kt`  
      Add the new emojis with the correct tag and description.

    - `AndroidTextureMapper.kt`  
      Update the `spriteMap` list with the new emojis and their correct tag/index in the spritesheet.

      - The `loadSpriteSheet()` function in `LoadingActivity.kt`  
        Adjust the dimensions if the new spritesheet has changed size 
        (e.g., sprite width/height or number of sprites per row/column).

### Accessing Resources

Once your emoji spritesheet are added to `src/main/res/drawable`, it will be automatically included in 
the AAR package when the `reslib` module is built. In order for Android Studio to recognize these, 
rebuild the library module using ./gradlew :reslib:build, then sync gradle files 
("File" -> "Sync Project with Gradle files")

NB: Android studio might not automatically recognize the files that you want to use. When you want 
to use R.drawable.MyEmojj, you might have to write com.emojigame.reslib.R.drawable.MyEmoji. Android
Studio may or may not recognize this in the suggestions you get.

- In Android, you can access them like any other resource, for example:

```kotlin
val drawable = ContextCompat.getDrawable(context, R.drawable.emojis)
imageView.setImageDrawable(drawable)
```

This assumes that `emojis.png` is placed in the `drawable` directory.

### How to Build the Library

To update or build the `reslib` library, follow this step:

Run the following command to assemble the AAR file:

```bash
./gradlew :reslib:build
```

The `reslib-release.aar` file will be generated in the project root, which is where it is read 
by the android module (and other modules in the future).

### How to Use the Library

To use the resources from `reslib` in the Android module, make sure to include the following in the 
`build.gradle` of the `android` module:

```gradle

dependencies {
    implementation(files("$rootDir/reslib-release.aar"))
}
```

This allows you to access all the drawable resources in the `reslib` module within the Android app.