# Repo Explorer Assistant

Repo Explorer Assistant is a Kotlin and Compose Desktop portfolio project that turns a basic `HelloDave` scaffold into a guided local-codebase assistant. It combines a desktop chat UI, Koog agents, configurable AI model settings, and bounded read-only repository tools so a developer can ask questions about a selected local project without giving the model unrestricted file-system access.

## Portfolio highlights

- **Kotlin + Compose Desktop:** a native desktop UI with unidirectional state flow and coroutine-backed assistant calls.
- **Agentic tooling:** Koog exposes explicit repository tools for listing files, searching text, reading snippets, and summarizing structure.
- **Safety boundaries:** path sandboxing blocks traversal outside the selected repository and skips generated or sensitive directories such as `.git`, `.gradle`, and `build`.
- **Graceful setup:** the app opens without an AI provider key and explains the missing configuration instead of crashing.
- **Automated validation:** the core sandbox and tool limits are covered by Kotlin tests and a GitHub Actions CI workflow.

## Demo

Use the app to point an AI model at this repository and ask questions that produce cited, file-grounded answers.

```bash
AI_API_KEY=your_key_here ./gradlew run
```

Suggested demo flow:

1. Start the app with `AI_API_KEY` set.
2. Enter the absolute path to this repository.
3. Click **Summarize this repository's architecture and entry points.**
4. Ask a follow-up such as `What safety checks prevent the assistant from reading outside the selected repository?`
5. Point out the cited files in the response, especially the sandbox and read-only tool implementation.

For a fuller script, see [`docs/demo.md`](docs/demo.md).

## Requirements

- JDK 21
- An AI provider API key exported as `AI_API_KEY` for live assistant answers

The current default provider is Google Gemini (`Gemini 2.5 Pro`). Existing `GEMINI_API_KEY` setups continue to work as a fallback.

`AI_API_KEY` is preferred for all providers. If it is not set, provider-specific fallback keys are also supported: `GOOGLE_API_KEY` or `GEMINI_API_KEY` for Google Gemini, `OPENAI_API_KEY` for OpenAI, and `ANTHROPIC_API_KEY` for Anthropic Claude.

Set `AI_PROVIDER` and `AI_MODEL` to switch supported Koog clients, for example:

- `AI_PROVIDER=google AI_MODEL=gemini-2.5-pro AI_API_KEY=... ./gradlew run`
- `AI_PROVIDER=openai AI_MODEL=gpt-4.1 AI_API_KEY=... ./gradlew run`
- `AI_PROVIDER=anthropic AI_MODEL=sonnet-4.5 AI_API_KEY=... ./gradlew run`

## Run locally

```bash
./gradlew run
```

Without `AI_API_KEY`, the app still launches and shows a clear setup message when a question is submitted.

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
├── assistant/                      # AI model/Koog orchestration and prompt rules
├── tools/                          # Read-only repository tools and path sandbox
└── ui/                             # Compose UI and state models
```

## GitHub metadata

Recommended repository description:

> Kotlin Compose Desktop app that uses Koog and configurable AI models to explore local codebases through safe read-only tools.

Recommended topics:

`kotlin`, `compose-desktop`, `koog`, `ai-agent`, `developer-tools`, `portfolio`
