# UI design system

The active Compose design system is in `presentation/ui/theme` and shared components.

## Foundations

- Brand palette: navy, teal, aqua, pale background, semantic error/success/warning/reward tones.
- Theme supports light/dark modes and six persisted primary-color presets: navy, teal, purple, emerald, rose, gold.
- `Quicksand` is the primary readable font; `SharkBit` is reserved by convention for decorative display/headline text.
- `Spacing` is a centralized 4/8/16/24/32/48 dp scale with 20 dp screen padding.
- Material 3 `AppShapes` ranges from 4 dp to 32 dp; playful game cards use 20 dp corners.
- `AppBackground`, `AppLogo`, `ActivityCard`, `MediaListItem`, progress components, submit buttons, dividers, and search components are reusable app-level pieces.

## Game UI

`GameUiKit.kt` provides shared top bars, progress bars, timers, primary/secondary buttons, answer options, feedback, star ratings, result, loading, and locked states. `GamePlayfulStyle.kt` and `GameDifficultyColors.kt` provide game surfaces/chips and theme-aware tier colors.

Individual games still own large feature screens and custom content components for their interaction model: draggable event cards, grid controls, disappearing text, equalizers, character cards, and typing UI.

## Interaction conventions

- Screens use animated content/visibility and feedback delays for phase changes.
- Audio states distinguish loading, buffering, playing, error, and completion where the feature needs them.
- Primary actions are generally full-width Material buttons; game-specific components expose semantic roles for answer controls.
- Preview composables are common across UI and game files.

## Consistency opportunities (not automatically refactored)

- Some older screens use plain `collectAsState()`.
- Submit buttons and game headers have overlapping custom styling.
- Game screens repeat loading/error/start/result scaffolding despite the shared kit.
- Some UI text and comments still refer to TTS even though playback is Media3/API audio.

