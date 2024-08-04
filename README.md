## Timber log Viewer

### Features
- Logs filtering
- Floating window

### Usage

1. add dependency to your project

```kotlin
implementation("com.github.devapro.logcat.timber:android-client:0.1.0")
```
2. Add Timber log tree

```kotlin

Timber.plant(TimberViewerTree())

```

3. Start Timber log viewer in first activity

```kotlin

startLogViewer(activity)

```

TODO
- column settings
- library publish
- clear logs
- store settings in SP