package com.hellodave.repoassistant.assistant

import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.clients.anthropic.AnthropicLLMClient
import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.llm.LLModel

data class AiModelConfig(
    val providerName: String,
    val apiKey: String?,
    val apiKeyEnvironmentVariable: String,
    val modelName: String,
    val model: LLModel,
    val createClient: (String) -> LLMClient,
) {
    val isConfigured: Boolean = !apiKey.isNullOrBlank()

    companion object {
        private const val GenericApiKeyEnv = "AI_API_KEY"

        fun fromEnvironment(environment: Map<String, String> = System.getenv()): AiModelConfig {
            val provider = AiProvider.from(environment["AI_PROVIDER"])
            val genericApiKey = environment[GenericApiKeyEnv]
            val providerApiKey = provider.apiKeyEnvironmentVariables.firstNotNullOfOrNull { env ->
                environment[env]?.takeIf { it.isNotBlank() }?.let { env to it }
            }
            val selectedApiKey = genericApiKey?.takeIf { it.isNotBlank() }?.let { GenericApiKeyEnv to it } ?: providerApiKey
            val model = provider.modelFor(environment["AI_MODEL"])

            return AiModelConfig(
                providerName = provider.displayName,
                apiKey = selectedApiKey?.second,
                apiKeyEnvironmentVariable = selectedApiKey?.first ?: GenericApiKeyEnv,
                modelName = model.displayName,
                model = model.model,
                createClient = provider.createClient,
            )
        }
    }
}

private enum class AiProvider(
    val displayName: String,
    val aliases: Set<String>,
    val apiKeyEnvironmentVariables: List<String>,
    val models: List<ConfiguredModel>,
    val createClient: (String) -> LLMClient,
) {
    Google(
        displayName = "Google Gemini",
        aliases = setOf("google", "gemini"),
        apiKeyEnvironmentVariables = listOf("GOOGLE_API_KEY", "GEMINI_API_KEY"),
        models = listOf(
            ConfiguredModel("gemini-2.5-pro", "Gemini 2.5 Pro", GoogleModels.Gemini2_5Pro),
        ),
        createClient = ::GoogleLLMClient,
    ),
    OpenAI(
        displayName = "OpenAI",
        aliases = setOf("openai"),
        apiKeyEnvironmentVariables = listOf("OPENAI_API_KEY"),
        models = listOf(
            ConfiguredModel("gpt-4.1", "GPT-4.1", OpenAIModels.Chat.GPT4_1),
            ConfiguredModel("gpt-4o", "GPT-4o", OpenAIModels.Chat.GPT4o),
            ConfiguredModel("gpt-4o-mini", "GPT-4o mini", OpenAIModels.Chat.GPT4oMini),
        ),
        createClient = { apiKey -> OpenAILLMClient(apiKey) },
    ),
    Anthropic(
        displayName = "Anthropic Claude",
        aliases = setOf("anthropic", "claude"),
        apiKeyEnvironmentVariables = listOf("ANTHROPIC_API_KEY"),
        models = listOf(
            ConfiguredModel("sonnet-4.5", "Claude Sonnet 4.5", AnthropicModels.Sonnet_4_5),
            ConfiguredModel("sonnet-4", "Claude Sonnet 4", AnthropicModels.Sonnet_4),
            ConfiguredModel("opus-4.1", "Claude Opus 4.1", AnthropicModels.Opus_4_1),
        ),
        createClient = { apiKey -> AnthropicLLMClient(apiKey) },
    ),
    ;

    fun modelFor(modelName: String?): ConfiguredModel {
        val normalizedModelName = modelName?.normalizeKey()
        return models.firstOrNull { model ->
            normalizedModelName != null && model.aliases.any { it.normalizeKey() == normalizedModelName }
        } ?: models.first()
    }

    companion object {
        fun from(providerName: String?): AiProvider {
            val normalizedProviderName = providerName?.normalizeKey()
            return entries.firstOrNull { provider ->
                normalizedProviderName != null && provider.aliases.any { it.normalizeKey() == normalizedProviderName }
            } ?: Google
        }
    }
}

private data class ConfiguredModel(
    val id: String,
    val displayName: String,
    val model: LLModel,
    val aliases: Set<String> = setOf(id, displayName),
)

private fun String.normalizeKey(): String = lowercase().replace("_", "-").replace(" ", "-")