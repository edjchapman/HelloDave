# Repo Explorer Assistant Demo

This script is designed for a short portfolio walkthrough or screen recording.

## Goal

Show that Repo Explorer Assistant can inspect a local repository with explicit, bounded, read-only tools and return practical engineering guidance with file citations.

## Setup

```bash
export GEMINI_API_KEY=your_key_here
./gradlew run
```

If you do not set `GEMINI_API_KEY`, the app still opens. Submit a question to show the friendly setup error, then restart with the key configured for the live demo.

## Walkthrough

1. Launch the app and enter the absolute path to this repository.
2. Click the suggested prompt: `Summarize this repository's architecture and entry points.`
3. Highlight that the answer cites concrete files instead of giving a generic response.
4. Ask: `What safety checks prevent the assistant from reading outside the selected repository?`
5. Highlight the sandbox behavior in `PathSandbox.kt` and the capped file access in `RepoFileTools.kt`.
6. Ask: `What would you improve next if this were a production developer tool?`
7. Close by showing the automated tests and CI workflow that validate the safety-critical path handling.

## What to emphasize

- The UI is intentionally small and focused so the agent/tool architecture is easy to review.
- The model receives only the tool outputs it requests, not unrestricted file-system access.
- Repository traversal, text search, and snippet reading are capped to keep responses bounded and demo-friendly.
- The missing-key path is handled gracefully, which makes the app easy for reviewers to run.

## Backup demo without a live key

If API access is unavailable during the demo, show:

```bash
./gradlew test
```

Then walk through the key source files manually:

- `src/main/kotlin/com/hellodave/repoassistant/ui/ChatScreen.kt`
- `src/main/kotlin/com/hellodave/repoassistant/assistant/RepoAssistant.kt`
- `src/main/kotlin/com/hellodave/repoassistant/tools/PathSandbox.kt`
- `src/main/kotlin/com/hellodave/repoassistant/tools/RepoFileTools.kt`
- `src/test/kotlin/com/hellodave/repoassistant/tools/PathSandboxTest.kt`