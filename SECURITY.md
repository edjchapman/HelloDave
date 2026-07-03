# Security Policy

Repo Explorer Assistant is a local developer tool. Its core safety goal is to let an AI model inspect a selected repository only through bounded, read-only tools.

## Supported Versions

Security updates are handled on the default branch. This repository does not currently maintain multiple release lines.

## Reporting a Vulnerability

If you find a security issue, do not include secrets or private repository contents in a public issue. Open a private advisory or contact the maintainer directly with:

- A short summary of the issue.
- Reproduction steps.
- The expected and actual trust-boundary behavior.
- Any relevant file paths, with secrets redacted.

## Scope

Security-sensitive areas include:

- Path traversal or symlink behavior that escapes the selected repository root.
- Tool output that reads ignored directories such as `.git`, `.gradle`, `build`, or dependency caches.
- Excessive file reads, search results, or snippet output that bypass configured caps.
- Accidental logging or persistence of API keys.

Secrets must be provided through environment variables such as `AI_API_KEY`, `GOOGLE_API_KEY`, `GEMINI_API_KEY`, `OPENAI_API_KEY`, or `ANTHROPIC_API_KEY`. Do not commit API keys, model credentials, or private repository contents.
