package com.hellodave.repoassistant.assistant

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AiModelConfigTest {
    @Test
    fun `defaults to Google Gemini and generic AI API key`() {
        val config = AiModelConfig.fromEnvironment(
            mapOf("AI_API_KEY" to "generic-key"),
        )

        assertTrue(config.isConfigured)
        assertEquals("Google Gemini", config.providerName)
        assertEquals("Gemini 2.5 Pro", config.modelName)
        assertEquals("AI_API_KEY", config.apiKeyEnvironmentVariable)
        assertEquals("generic-key", config.apiKey)
    }

    @Test
    fun `uses provider-specific fallback key when generic key is missing`() {
        val config = AiModelConfig.fromEnvironment(
            mapOf(
                "AI_PROVIDER" to "openai",
                "AI_MODEL" to "gpt_4o_mini",
                "OPENAI_API_KEY" to "openai-key",
            ),
        )

        assertTrue(config.isConfigured)
        assertEquals("OpenAI", config.providerName)
        assertEquals("GPT-4o mini", config.modelName)
        assertEquals("OPENAI_API_KEY", config.apiKeyEnvironmentVariable)
        assertEquals("openai-key", config.apiKey)
    }

    @Test
    fun `uses Google provider-specific key before legacy Gemini key`() {
        val config = AiModelConfig.fromEnvironment(
            mapOf(
                "AI_PROVIDER" to "gemini",
                "GOOGLE_API_KEY" to "google-key",
                "GEMINI_API_KEY" to "gemini-key",
            ),
        )

        assertTrue(config.isConfigured)
        assertEquals("Google Gemini", config.providerName)
        assertEquals("GOOGLE_API_KEY", config.apiKeyEnvironmentVariable)
        assertEquals("google-key", config.apiKey)
    }

    @Test
    fun `keeps legacy Gemini key support for default provider`() {
        val config = AiModelConfig.fromEnvironment(
            mapOf("GEMINI_API_KEY" to "gemini-key"),
        )

        assertTrue(config.isConfigured)
        assertEquals("Google Gemini", config.providerName)
        assertEquals("GEMINI_API_KEY", config.apiKeyEnvironmentVariable)
        assertEquals("gemini-key", config.apiKey)
    }

    @Test
    fun `generic key takes precedence over provider-specific key`() {
        val config = AiModelConfig.fromEnvironment(
            mapOf(
                "AI_PROVIDER" to "anthropic",
                "AI_MODEL" to "Claude Sonnet 4",
                "AI_API_KEY" to "generic-key",
                "ANTHROPIC_API_KEY" to "anthropic-key",
            ),
        )

        assertTrue(config.isConfigured)
        assertEquals("Anthropic Claude", config.providerName)
        assertEquals("Claude Sonnet 4", config.modelName)
        assertEquals("AI_API_KEY", config.apiKeyEnvironmentVariable)
        assertEquals("generic-key", config.apiKey)
    }

    @Test
    fun `normalizes Claude provider alias and model id`() {
        val config = AiModelConfig.fromEnvironment(
            mapOf(
                "AI_PROVIDER" to "CLAUDE",
                "AI_MODEL" to "opus-4.1",
                "ANTHROPIC_API_KEY" to "anthropic-key",
            ),
        )

        assertTrue(config.isConfigured)
        assertEquals("Anthropic Claude", config.providerName)
        assertEquals("Claude Opus 4.1", config.modelName)
        assertEquals("ANTHROPIC_API_KEY", config.apiKeyEnvironmentVariable)
        assertEquals("anthropic-key", config.apiKey)
    }

    @Test
    fun `falls back to default provider and model for unknown values`() {
        val config = AiModelConfig.fromEnvironment(
            mapOf(
                "AI_PROVIDER" to "unknown-provider",
                "AI_MODEL" to "unknown-model",
                "AI_API_KEY" to "generic-key",
            ),
        )

        assertTrue(config.isConfigured)
        assertEquals("Google Gemini", config.providerName)
        assertEquals("Gemini 2.5 Pro", config.modelName)
        assertEquals("AI_API_KEY", config.apiKeyEnvironmentVariable)
        assertEquals("generic-key", config.apiKey)
    }

    @Test
    fun `blank generic key falls back to provider-specific key`() {
        val config = AiModelConfig.fromEnvironment(
            mapOf(
                "AI_PROVIDER" to "openai",
                "AI_API_KEY" to " ",
                "OPENAI_API_KEY" to "openai-key",
            ),
        )

        assertTrue(config.isConfigured)
        assertEquals("OpenAI", config.providerName)
        assertEquals("GPT-4.1", config.modelName)
        assertEquals("OPENAI_API_KEY", config.apiKeyEnvironmentVariable)
        assertEquals("openai-key", config.apiKey)
    }

    @Test
    fun `reports generic key requirement when no configured key exists`() {
        val config = AiModelConfig.fromEnvironment(
            mapOf(
                "AI_PROVIDER" to "openai",
                "OPENAI_API_KEY" to " ",
            ),
        )

        assertFalse(config.isConfigured)
        assertEquals("OpenAI", config.providerName)
        assertEquals("AI_API_KEY", config.apiKeyEnvironmentVariable)
    }
}