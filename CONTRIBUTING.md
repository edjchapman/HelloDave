# Contributing

Repo Explorer Assistant is a compact portfolio project. Contributions should keep the app easy to understand, easy to run, and explicit about the trust boundary between the AI model and the local file system.

## Local Setup

Requirements:

- JDK 21
- The Gradle wrapper checked into this repository
- Optional `AI_API_KEY` or provider-specific key for live assistant answers

Run the validation suite:

```bash
./gradlew test
```

Run the desktop app:

```bash
./gradlew run
```

Install local Git hooks:

```bash
./scripts/install-hooks.sh
```

## Development Guidelines

- Keep repository access read-only unless a feature explicitly changes that project direction.
- Put all path-safety decisions in `PathSandbox`.
- Put file-walking and result-size limits in `RepoFileTools`.
- Expose LLM capabilities only through annotated Koog tools in `RepoToolRegistry`.
- Add or update tests when changing provider selection, path handling, file search, or snippet reads.
- Keep public documentation aligned with actual environment variables and supported models.

## Pull Requests

Before opening a pull request:

1. Run `./gradlew test`.
2. Update `README.md`, `docs/demo.md`, or the architecture/configuration docs if behavior changes.
3. Describe user-facing impact and test coverage in the PR body.
4. Avoid committing local IDE, cache, generated build, or secret files.
