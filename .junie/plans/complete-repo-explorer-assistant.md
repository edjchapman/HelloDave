---
sessionId: session-260703-004259-lbej
isActive: true
---

# Current Status

### What Has Already Been Started
The project has been partially migrated from the original IntelliJ-only `HelloDave.iml` scaffold into a Gradle/Compose Desktop Kotlin app:

- `settings.gradle.kts`, `build.gradle.kts`, Gradle wrapper files, and a basic `.gitignore` exist.
- `src/Main.kt` has been removed and replaced by `src/main/kotlin/com/hellodave/repoassistant/Main.kt`.
- UI files exist under `src/main/kotlin/com/hellodave/repoassistant/ui`:
  - `App.kt`
  - `ChatScreen.kt`
  - `UiModels.kt`
- Assistant files exist under `src/main/kotlin/com/hellodave/repoassistant/assistant`:
  - `AssistantController.kt`
  - `AssistantPrompts.kt`
  - `RepoAssistant.kt`
- Repository tool files exist under `src/main/kotlin/com/hellodave/repoassistant/tools`:
  - `PathSandbox.kt`
  - `RepoFileTools.kt`
  - `RepoToolRegistry.kt`
- Deterministic tests exist in `src/test/kotlin/com/hellodave/repoassistant/tools/PathSandboxTest.kt`.

### Known Blockers
- `./gradlew test` currently fails before compilation because `build.gradle.kts` declares an unavailable dependency: `ai.koog:prompt-executor-google-client:1.0.0`.
- Local dependency investigation confirms `ai.koog:koog-agents:1.0.0` resolves, but the currently imported `GoogleLLMClient` class is not present on the compile classpath with the declared artifacts.
- `RepoAssistant.kt` currently imports `GoogleLLMClient` and constructs `MultiLLMPromptExecutor(GoogleLLMClient(apiKey))`; this must be replaced with a Koog 1.0.0 Gemini executor path that is actually published and compileable.
- The README is only a short placeholder and does not yet satisfy the portfolio/documentation requirements.
- No durable AI handoff/progress entry point is visible yet; `AGENTS.md` has not been added, and the implementation changes still need conventional commits.

### Immediate Next Implementation Focus
- Do not restart the scaffold migration; continue from the existing `ui`, `assistant`, and `tools` packages.
- Resolve the Koog/Gemini API mismatch first, because it blocks all compilation and test feedback.
- Prefer removing the nonexistent `prompt-executor-google-client` artifact and using the simplest Koog 1.0.0 Google/Gemini executor API available from published artifacts, keeping that choice isolated inside `RepoAssistant.kt`.
- If Koog’s published Google executor cannot be made compileable in the current version, add a narrow `RepoAssistant` fallback boundary rather than leaking provider-specific code into UI or tools.

### Completion Goal
Finish the existing implementation rather than restart it: fix the build, harden the Koog/tool integration, complete the documentation and AI handoff trail, validate the desktop app, and commit sensible milestones using conventional commit messages.

# Technical Design

### Build and Dependency Strategy
- Keep the current Gradle Kotlin DSL structure in `settings.gradle.kts` and `build.gradle.kts`.
- Remove the unavailable `ai.koog:prompt-executor-google-client:1.0.0` dependency before attempting further compilation.
- Align `RepoAssistant.kt` with the Koog 1.0.0 API exposed by published artifacts, using the documented Gemini executor/client path that compiles locally.
- Keep Koog/provider-specific imports limited to `RepoAssistant.kt` and `RepoToolRegistry.kt`; the UI and deterministic file tools should remain independent of LLM dependency churn.
- Keep `GEMINI_API_KEY` as the app-facing environment variable, even if Koog documentation examples refer to `GOOGLE_API_KEY`, because this is part of the project requirements.

### Existing Architecture to Preserve
The current package split is appropriate and should be completed rather than replaced:

```text
com.hellodave.repoassistant
├── Main.kt                         # Compose app entry point
├── ui                              # Compose UI and UI state models
├── assistant                       # Controller, prompts, Koog agent boundary
└── tools                           # Read-only repository tools and sandboxing
```

### Assistant and Tool Integration
- Keep UI code free of Koog/tool logic; `ChatScreen.kt` should continue dispatching to `AssistantController`.
- Keep `AssistantController.kt` responsible for:
  - API-key status,
  - repository path validation,
  - loading/error state,
  - background execution via coroutines.
- Keep `RepoAssistant.kt` as the only class that constructs the Koog `AIAgent`.
- Keep `RepoToolRegistry.kt` as the Koog adapter around deterministic `RepoFileTools` methods.
- Ensure `AssistantPrompts.systemPrompt` requires repository tool use, citations, “What I checked”, and honesty about uncertainty.

### Repository Safety
- Continue routing filesystem access through `PathSandbox.kt`.
- Preserve ignored directories such as `.git`, `.gradle`, `.idea`, `build`, `out`, `target`, `node_modules`, `.next`, and `dist`.
- Keep output caps in `RepoToolLimits` for file listings, search matches, snippet lines, file bytes, and project-tree depth.
- Add/adjust tests around the current `RepoFileTools.kt` behaviors as needed, especially skipped directories and capped output.

### AI Handoff and Progress Tracking
- Add a root-level `AGENTS.md` as the clear entry point for future AI agents.
- Use it to document:
  - project purpose,
  - current architecture,
  - run/test commands,
  - safety constraints,
  - known build/tooling notes,
  - completion checklist/status.
- Keep this file updated at each milestone so compressed future sessions can resume quickly.

### Documentation and Portfolio Finish
- Expand `README.md` from its current placeholder into a GitHub-ready portfolio README covering:
  - purpose and feature overview,
  - setup and `GEMINI_API_KEY`,
  - `./gradlew run` and `./gradlew test`,
  - screenshot/GIF placeholders,
  - demo script,
  - architecture diagram,
  - safety model,
  - limitations and future enhancements.

### Commit Strategy
Because the original request explicitly asked for regular commits, each implementation milestone should end with a conventional commit such as:

- `chore: configure gradle compose desktop app`
- `feat: add sandboxed repository tools`
- `feat: wire koog gemini repo assistant`
- `docs: document portfolio demo and agent handoff`
- `test: cover repository sandbox behavior`

IDE metadata changes should be reviewed before committing; generated or personal files should be excluded unless intentionally required for the project.

# Testing

### Automated Validation
- Run `./gradlew test` after fixing Koog/Gradle dependencies.
- Ensure `PathSandboxTest.kt` covers:
  - paths inside the repository root,
  - `..` traversal rejection,
  - absolute paths outside the root,
  - ignored directory rejection,
  - snippet/search caps and ignored directory skipping.
- Add targeted `RepoFileTools` tests if the existing combined test becomes too broad.

### Manual Validation
- Run `./gradlew run` and confirm the Compose Desktop app launches.
- Launch without `GEMINI_API_KEY` and verify the UI shows a clear missing-key state instead of crashing.
- Launch with `GEMINI_API_KEY` and ask a guided prompt against a small local repo.
- Verify the answer cites repository files and includes a short “What I checked” section.
- Try invalid/out-of-root paths through tool-accessible questions and verify they are blocked.

### Acceptance Criteria
- `./gradlew test` passes.
- `./gradlew run` launches the app.
- The app can complete at least one Gemini-backed repository question with file citations.
- Repository tools remain read-only and sandboxed.
- `README.md` and `AGENTS.md` make the project understandable and resumable for GitHub visitors and future AI agents.
- Work is committed in sensible conventional-commit checkpoints.

# Delivery Steps

###   Step 1: Stabilize Gradle and Koog dependencies
The project builds far enough to compile Kotlin sources and run deterministic tests.

- Update `build.gradle.kts` to remove the unavailable `ai.koog:prompt-executor-google-client:1.0.0` dependency.
- Replace the direct `GoogleLLMClient` import in `RepoAssistant.kt` with a Koog 1.0.0 Google/Gemini executor API that is present in the resolved published artifacts.
- Keep `GEMINI_API_KEY` as the environment variable consumed by `Main.kt` and `AssistantController.kt`, adapting only inside `RepoAssistant.kt` if Koog uses a differently named API-key concept internally.
- Run `./gradlew test` immediately after the dependency/import fix to expose any remaining compile errors in `RepoToolRegistry.kt`, Compose UI code, or tests.
- Commit the build-stabilization milestone with a conventional commit message such as `chore: stabilize koog gradle dependencies`.

###   Step 2: Harden sandboxed repository tools and tests
The read-only repository tool layer is deterministic, bounded, and covered by focused tests.

- Review `PathSandbox.kt` containment checks and ignored-directory handling for normalized relative and absolute paths.
- Review `RepoFileTools.kt` for stream handling, output caps, UTF-8 failure behavior, and generated-directory skipping.
- Expand or split `PathSandboxTest.kt` so sandbox behavior and repo-tool limits are easy to diagnose.
- Verify `./gradlew test` passes after tool-layer changes.
- Commit the safety/tooling milestone with a conventional commit message.

###   Step 3: Complete Koog Gemini assistant wiring
Repository questions flow from the UI controller to a Koog Gemini agent using the sandboxed tool registry.

- Finalize `RepoToolRegistry.kt` tool annotations/signatures so Koog can call `listProjectFiles`, `searchFileContents`, `readFileSnippet`, and `summarizeProjectStructure` with the current Koog reflection/tool API.
- Finalize `RepoAssistant.kt` agent creation with the selected Gemini model, `AssistantPrompts.systemPrompt`, `RepoToolRegistry.create(repositoryRoot)`, and bounded `maxIterations`.
- Keep all provider/API compatibility work inside `RepoAssistant.kt` so `AssistantController.kt`, `ChatScreen.kt`, and `RepoFileTools.kt` do not depend on Koog implementation details.
- Verify `AssistantController.kt` handles missing API key, invalid repo path, in-flight requests, agent failures, and successful assistant messages.
- Manually run one Gemini-backed repository question and confirm cited files appear in the answer.
- Commit the assistant-integration milestone with a conventional commit message such as `feat: wire koog gemini repo assistant`.

###   Step 4: Polish the Compose Desktop chat experience
The desktop UI feels like a guided portfolio demo rather than a raw sample app.

- Refine `ChatScreen.kt` layout for repository path entry, API key status, error card, transcript, loading text, prompt input, and suggested chips.
- Ensure suggested chips submit useful demo prompts without leaving confusing stale input state.
- Keep `App.kt` and `UiModels.kt` simple and aligned with the controller-owned state model.
- Run `./gradlew run` and manually validate missing-key, invalid-path, loading, and successful-response states.
- Commit the UI-polish milestone with a conventional commit message.

###   Step 5: Finish portfolio documentation and AI handoff
GitHub visitors and future AI agents have clear documentation, commands, and project status.

- Expand `README.md` with purpose, features, screenshots/GIF placeholders, setup, run/test commands, demo script, architecture, safety notes, limitations, and future enhancements.
- Add root-level `AGENTS.md` as the durable AI handoff entry point with architecture notes, current status, commands, constraints, and a completion checklist.
- Add `LICENSE` if absent and ensure `.gitignore` excludes generated/personal files while keeping required Gradle wrapper files.
- Run final `./gradlew test` and `./gradlew run` validation.
- Commit the documentation/finalization milestone with a conventional commit message.