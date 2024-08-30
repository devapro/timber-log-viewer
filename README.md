## Timber log Viewer

### Features
- Logs filtering
- Floating window

<table>
  <tr>
    <td>
        <img src="https://github.com/devapro/timber-log-viewer/blob/main/screenshots/floating_window.png" width="250" style="max-width:100%;">
    </td>
    <td>
        <img src="https://github.com/devapro/timber-log-viewer/blob/main/screenshots/activity.png" width="250" style="max-width:100%;">
    </td>
  </tr>
</table>

### Usage

1. add dependency to your project

```kotlin
debugImplementation("io.github.devapro:timber-viewer:0.1.1")
releaseImplementation("io.github.devapro:timber-viewer-no-op:0.1.1")
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
- library publish
- store settings in SP