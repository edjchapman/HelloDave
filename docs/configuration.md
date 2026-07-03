# Configuration

Repo Explorer Assistant reads model configuration from environment variables at startup. The app can launch without credentials, but live AI-backed answers require a supported key.

## Environment Variables

| Variable | Purpose | Required |
| --- | --- | --- |
| `AI_API_KEY` | Preferred API key for any supported provider | Yes for live answers |
| `AI_PROVIDER` | Selects provider: `google`, `gemini`, `openai`, `anthropic`, or `claude` | No |
| `AI_MODEL` | Selects a provider-specific model alias | No |
| `GOOGLE_API_KEY` | Google fallback key | No |
| `GEMINI_API_KEY` | Legacy Google Gemini fallback key | No |
| `OPENAI_API_KEY` | OpenAI fallback key | No |
| `ANTHROPIC_API_KEY` | Anthropic fallback key | No |

`AI_API_KEY` takes precedence over provider-specific fallback keys. Blank values are ignored.

## Providers And Models

| Provider | Default model | Supported aliases |
| --- | --- | --- |
| Google Gemini | `gemini-2.5-pro` | `gemini-2.5-pro`, `Gemini 2.5 Pro` |
| OpenAI | `gpt-4.1` | `gpt-4.1`, `gpt-4o`, `gpt-4o-mini` |
| Anthropic Claude | `sonnet-4.5` | `sonnet-4.5`, `sonnet-4`, `opus-4.1` |

Unknown providers fall back to Google Gemini. Unknown models fall back to the selected provider's default model.

## Examples

Google Gemini:

```bash
AI_PROVIDER=google AI_MODEL=gemini-2.5-pro AI_API_KEY=... ./gradlew run
```

OpenAI:

```bash
AI_PROVIDER=openai AI_MODEL=gpt-4.1 AI_API_KEY=... ./gradlew run
```

Anthropic Claude:

```bash
AI_PROVIDER=anthropic AI_MODEL=sonnet-4.5 AI_API_KEY=... ./gradlew run
```

Missing-key demo:

```bash
./gradlew run
```

The app still opens and reports which key is needed when the first question is submitted.
