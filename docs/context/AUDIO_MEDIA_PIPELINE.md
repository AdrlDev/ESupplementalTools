# Audio and media pipeline

## Sources

- Songs generally use Google Drive sharing URLs from `FinalData`; `DriveAudioManager` converts `/file/d/{id}/...` or `id={id}` links to `uc?export=download&id=...` for Media3.
- Stories use an asset Markdown transcript and may have a legacy Drive URL, but the active player calls the audio-story API for the full transcript and uses the generated URL.
- Game audio is generated from question, event, sentence, word, quote, or direction text.

## Generated audio flow

```text
ViewModel / prefetch use case
 → MediaRepository.generateAudioForText(id, title, text)
 → Room audio_stories lookup by mediaId
 → POST /api/v1/audio/stories with X-App-Key
 → if PROCESSING, GET /api/v1/audio/stories/{mediaId} every 3 seconds, max 60 attempts
 → READY URL
 → MediaMetadataRetriever on Dispatchers.IO reads duration
 → insert audio_stories and update media_items.durationSeconds
 → return AudioStory
 → MediaPlaybackManager.prepare(...)
 → Media3 ExoPlayer.prepare/play
```

The repository uses a per-ID in-process `Mutex` to prevent concurrent generation in one repository instance. A 409 checks current remote status. HTTP/network/playback failures are mapped by `ErrorMapper` and surfaced as `Result`/UI errors in most flows.

## Local cache and offline behavior

The Room `audio_stories` table stores metadata and remote URL, word timings, duration, and remote IDs. It does not store downloaded MP3 bytes or a local file path. Therefore the cache avoids regeneration but is not a true offline audio cache; playback still depends on the remote URL being reachable. Song playback also requires network access.

## Playback

`MediaPlaybackManagerImpl` owns a Media3 `ExoPlayer`, a main-thread ticker, and optional Android `Visualizer`. It publishes playing, buffering, errors, position, duration, sentence index, word index, waveform amplitudes, and completion through `StateFlow`. Story highlighting uses returned word timings; if timings are absent, sentence position is estimated linearly by word count.

Each game/player ViewModel receives a playback manager factory instance and releases it in `onCleared`. `PlayerViewModel` also prepares story transcript chunks/pages; Story Order uses a summary narration and its own caption chunks.

## Error behavior

- `ErrorMapper` maps common network, HTTP, and Media3 errors.
- Some game audio failures intentionally unlock the answer UI or fall back to the next phase.
- Full story-player generation failure enters a visible error state.
- Visualizer setup failure is swallowed as non-fatal and the ticker emits random fallback amplitudes.

