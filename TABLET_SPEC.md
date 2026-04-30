# Tablet / large screen support — implementation spec

This document describes the adaptive layout changes agreed during design review.
It is intended as a handover to Claude Code for implementation.

---

## Approach

Use `WindowSizeClass` from `androidx.compose.material3.adaptive` to branch between
phone and tablet layouts. The breakpoint to use is `WindowWidthSizeClass.Expanded`
(≥ 840 dp), which covers tablets in landscape and large tablets in portrait.

Add the dependency if it is not already present:

```toml
# gradle/libs.versions.toml
material3-adaptive = "1.1.0"
androidx-material3-adaptive = { group = "androidx.compose.material3.adaptive", name = "adaptive", version.ref = "material3-adaptive" }
```

```kotlin
// app/build.gradle.kts
implementation(libs.androidx.material3.adaptive)
```

Compute the size class once at the top-level `Activity` and pass it down via a
`CompositionLocal` or as a plain parameter:

```kotlin
val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
val isExpanded = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
```

---

## Screen 1 — Library (MangaListScreen)

### Phone (unchanged)
- `BottomAppBar` / `NavigationBar` at the bottom
- Single-column or 2-column `LazyVerticalGrid`

### Tablet
- Replace `NavigationBar` with a `NavigationRail` on the left edge
- Split the screen into two panes side by side using a `Row`:
  - **Left pane** (~40% width): search bar + filter chips + `LazyVerticalGrid` with 3 columns
  - **Right pane** (remaining width): persistent detail panel showing the selected manga's cover, metadata, description, and chapter list
- When no manga is selected, the right pane shows an empty-state prompt
- Selecting a manga updates the right pane in place (no navigation)

```kotlin
@Composable
fun MangaListScreen(isExpanded: Boolean, ...) {
    if (isExpanded) {
        Row {
            NavigationRail { /* Library, Explore, Profile items */ }
            MangaListPane(modifier = Modifier.fillMaxHeight().weight(0.4f), ...)
            MangaDetailPane(modifier = Modifier.fillMaxHeight().weight(0.6f), ...)
        }
    } else {
        // existing phone layout
    }
}
```

---

## Screen 2 — Manga detail (MangaChapterListScreen)

### Phone (unchanged)
- Full-screen scrollable layout: cover + info at the top, chapter list below

### Tablet
- Two-pane `Row`:
  - **Left pane** (~40% width, non-scrolling): cover art, title, author, stat cards (chapters, status, progress, unread), description, "Continue" button
  - **Right pane** (scrollable): full chapter list with sort/filter controls
- Stat cards use a 2×2 `LazyVerticalGrid` (or simple `FlowRow`)

```kotlin
@Composable
fun MangaChapterListScreen(isExpanded: Boolean, ...) {
    if (isExpanded) {
        Row {
            MangaInfoPane(modifier = Modifier.fillMaxHeight().weight(0.4f), ...)
            ChapterListPane(modifier = Modifier.fillMaxHeight().weight(0.6f), ...)
        }
    } else {
        // existing phone layout
    }
}
```

---

## Screen 3 — Reader (MangaChapterReaderScreen)

This is the most significant change.

### Phone (unchanged)
- Full-screen vertical `LazyColumn` of image strips
- Controls hidden by default, revealed on tap

### Tablet

#### Layout
Use `PermanentNavigationDrawer` with the drawer on the **left** side:

```kotlin
@Composable
fun MangaChapterReaderScreen(isExpanded: Boolean, ...) {
    if (isExpanded) {
        PermanentNavigationDrawer(
            drawerContent = { ChapterDrawerContent(...) }
        ) {
            ReaderContent(...)
        }
    } else {
        // existing phone layout
    }
}
```

#### Drawer (`ChapterDrawerContent`)
Width: `240.dp` (Material 3 default for `PermanentNavigationDrawer`)

Contents top to bottom:
1. Manga title + current chapter name
2. Chapter scroll progress bar (`LinearProgressIndicator`)
3. Scrollable chapter list (`LazyColumn`) — current chapter highlighted, unread chapters marked with a dot badge
4. Prev / Next chapter buttons (`OutlinedButton` + `FilledButton`) pinned at the bottom

The drawer should be **collapsible**: a toggle in the `TopAppBar` sets a `drawerVisible` state, and when `false` the drawer collapses to a `36.dp` rail showing only the progress indicator.

#### Reader content (`ReaderContent`)
- `LazyColumn` of full-width image strips (existing `LazyLayoutCacheWindow` logic unchanged)
- Strip width is constrained to `min(contentWidth * 0.8f, 480.dp)` and centred horizontally, so the art is not stretched on very wide screens
- A "currently reading" pill overlay appears on the visible strip

#### TopAppBar
Standard `TopAppBar` with:
- `navigationIcon`: back chevron (`Icons.AutoMirrored.Filled.ArrowBack`)
- `title`: two-line block — manga name (headline) + chapter name (subtitle)
- `actions`:
  1. Brightness icon button (opens a `ModalBottomSheet` slider)
  2. Fit-width toggle icon button (`Icons.Filled.FitScreen` / custom)
  3. Drawer toggle icon button (`Icons.Filled.Menu`) — highlighted when drawer is open

The `TopAppBar` uses `enterAlwaysScrollBehavior` so it hides when the user scrolls
down and reappears on scroll up, keeping the reading area clean.

---

## Summary of new Compose APIs used

| API | Usage |
|---|---|
| `WindowSizeClass` / `currentWindowAdaptiveInfo()` | Breakpoint detection |
| `NavigationRail` | Main nav on tablet (Library screen) |
| `PermanentNavigationDrawer` | Chapter list on tablet (Reader screen) |
| `TopAppBar` with `enterAlwaysScrollBehavior` | Reader screen app bar |
| `Row` with `weight()` | Two-pane layouts (Library, Detail) |

---

## What is NOT changing

- The vertical-scroll `LazyColumn` reader mechanic — no pagination, no dual-page
- All existing phone layouts — these are additive `if (isExpanded)` branches only
- ViewModel / use case / data layer — purely a presentation-layer change
