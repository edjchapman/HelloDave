# CLAUDE.md

This file gives Claude Code guidance for working in this repository.

## Project

Repo Explorer Assistant is a Kotlin and Compose Desktop app that lets a developer point a configurable AI model at a local repository and ask questions about it. The model only reaches the codebase through bounded, read-only Koog tools; it does not receive unrestricted file-system access.

The Gradle root name is still `HelloDave` from the original scaffold. The application code lives under `com.hellodave.repoassistant`.

## Commands

```bash
./gradlew run
./gradlew test
./gradlew build
./gradlew test --tests "*PathSandboxTest"
./gradlew packageDistributionForCurrentOS
```

JDK 21 is required through `jvmToolchain(21)`.

The app launches without a key and shows a setup error on the first AI-backed question. Live answers require `AI_API_KEY` or a provider-specific fallback key.

## Runtime Configuration

`AiModelConfig.fromEnvironment()` reads:

- `AI_PROVIDER`: `google`/`gemini` by default, plus `openai`, `anthropic`, or `claude`.
- `AI_MODEL`: provider-specific model alias, falling back to the provider default when unset or unknown.
- `AI_API_KEY`: preferred key for every provider.
- Provider fallbacks: `GOOGLE_API_KEY`, `GEMINI_API_KEY`, `OPENAI_API_KEY`, `ANTHROPIC_API_KEY`.

To add or change a provider/model, edit the private `AiProvider` enum in `AiModelConfig.kt`.

## Architecture

The main flow is:

```text
Main.kt -> AssistantController -> RepoAssistant -> Koog AIAgent -> RepoToolRegistry -> RepoFileTools -> PathSandbox
```

Packages:

- `assistant/`: AI provider configuration, controller state, Koog agent construction, and prompt rules.
- `tools/`: repository file operations, Koog tool registry, output caps, and path sandboxing.
- `ui/`: Compose Desktop views and immutable UI models.

## Trust Boundary

`PathSandbox` is the security-critical chokepoint. Keep filesystem containment logic there.

Safety invariants:

- Repository tools are read-only.
- All model-facing repository access goes through `RepoToolRegistry`.
- All filesystem resolution goes through `PathSandbox`.
- Paths must remain inside the selected repository root.
- Symlinks must resolve inside the selected repository root.
- Ignored directories such as `.git`, `.gradle`, `.idea`, `build`, `out`, `target`, `node_modules`, `.next`, and `dist` stay inaccessible.
- `RepoFileTools` caps listed files, search matches, snippet lines, file bytes, and tree depth.

Any path-safety change should update `PathSandboxTest`.

## Tests And CI

Tests use `kotlin.test` on JUnit Platform. Prefer descriptive backtick test names.

Important checks:

- Local: `./gradlew test`
- Full local/CI check: `./gradlew build` (or `make check`, the single gate)
- GitHub Actions CI: `.github/workflows/check.yml` (runs `make check` on push/PR to `main`)
- Qodana JVM analysis: `.github/workflows/qodana_code_quality.yml` and `qodana.yaml`
- Releases: `release-please` (`.github/workflows/release-please.yml`) bumps the version + `CHANGELOG.md` and, on merge of its release PR, tags `vX.Y.Z` and attaches the Dmg/Msi/Deb installers.

## Contribution Notes

- Keep documentation aligned with supported environment variables and model aliases.
- Do not commit API keys or private repository contents.
- Keep generated outputs and IDE-local state out of commits.
- Keep the app small and reviewable; this is a portfolio project, not a full IDE replacement.
