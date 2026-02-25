Since the Drag-N-Drop Logic must be handled specific to each platform
We have to use a "Split UI" code structure.

# Android Drag-N-Drop
The drag-n-drop logic is implemented in `GameScreen.kt`, specifically inside the
`LetterGroup` composable. Some important objects/functions related to handling drag-n-drop:
* `ddTarget` (of type `DragAndDropTarget`): this is the object that simultaneously handles
  both drag-n-drop and pointer motion events (enter, exit, leave)

# iOS Drag-N-Drop
The "drag-n-drop" logic is implemented in `ContentView.swift`, specifically inside the
`LetterGroup` view. Unfortunately, the iOS built-in drag-n-drop using (`.draggable` and `.dropDestination`)
does not allow simultaneous handling of drag gestures (like in Android).
Hence, I was not able to use these function to replicate the visual UI feedback implemented in Android.
Without a true drag-and-drop, we can't drag on object from one LetterGroup and drop it to the other.
We use double tap to move a letter object between the two groups.

Both the Android and iOS implementations keep a similar `@Composable`/`View` structure:
`LetterGroup` and `BigLetter`.