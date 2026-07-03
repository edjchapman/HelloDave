# Repo Explorer Assistant

Repo Explorer Assistant is a Kotlin and Compose Desktop portfolio project that turns a basic `HelloDave` scaffold into a guided local-codebase assistant. It combines a desktop chat UI, Koog agents, Google Gemini, and bounded read-only repository tools so a developer can ask questions about a selected local project without giving the model unrestricted file-system access.

## Portfolio highlights

- **Kotlin + Compose Desktop:** a native desktop UI with unidirectional state flow and coroutine-backed assistant calls.
- **Agentic tooling:** Koog exposes explicit repository tools for listing files, searching text, reading snippets, and summarizing structure.
- **Safety boundaries:** path sandboxing blocks traversal outside the selected repository and skips generated or sensitive directories such as `.git`, `.gradle`, and `build`.
- **Graceful setup:** the app opens without a Gemini key and explains the missing configuration instead of crashing.
- **Automated validation:** the core sandbox and tool limits are covered by Kotlin tests and a GitHub Actions CI workflow.

## Demo

Use the app to point Gemini at this repository and ask questions that produce cited, file-grounded answers.

```bash
GEMINI_API_KEY=your_key_here ./gradlew run
```

Suggested demo flow:

1. Start the app with `GEMINI_API_KEY` set.
2. Enter the absolute path to this repository.
3. Click **Summarize this repository's architecture and entry points.**
4. Ask a follow-up such as `What safety checks prevent the assistant from reading outside the selected repository?`
5. Point out the cited files in the response, especially the sandbox and read-only tool implementation.

For a fuller script, see [`docs/demo.md`](docs/demo.md).

## Requirements

- JDK 21
- A Google Gemini API key exported as `GEMINI_API_KEY` for live assistant answers

## Run locally

```bash
./gradlew run
```

Without `GEMINI_API_KEY`, the app still launches and shows a clear setup message when a question is submitted.

## Test

```bash
./gradlew test
```

## Build desktop packages

```bash
./gradlew packageDistributionForCurrentOS
```

The Compose Desktop configuration can produce native packages for macOS, Windows, and Debian-based Linux.

## Project structure

```text
src/main/kotlin/com/hellodave/repoassistant
├── Main.kt                         # Compose Desktop entry point
├── assistant/                      # Gemini/Koog orchestration and prompt rules
├── tools/                          # Read-only repository tools and path sandbox
└── ui/                             # Compose UI and state models
```

## GitHub metadata

Recommended repository description:

> Kotlin Compose Desktop app that uses Koog and Gemini to explore local codebases through safe read-only tools.

Recommended topics:

`kotlin`, `compose-desktop`, `koog`, `gemini`, `ai-agent`, `developer-tools`, `portfolio`
