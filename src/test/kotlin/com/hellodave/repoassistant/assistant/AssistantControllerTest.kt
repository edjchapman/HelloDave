package com.hellodave.repoassistant.assistant

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AssistantControllerTest {
    @Test
    fun `initial state exposes configured provider and model metadata`() {
        val controller = AssistantController(
            modelConfig = AiModelConfig.fromEnvironment(
                mapOf(
                    "AI_PROVIDER" to "openai",
                    "AI_MODEL" to "gpt-4o-mini",
                    "OPENAI_API_KEY" to "openai-key",
                ),
            ),
        )

        val state = controller.state.value

        assertTrue(state.isApiKeyConfigured)
        assertEquals("OpenAI", state.providerName)
        assertEquals("GPT-4o mini", state.modelName)
        assertEquals("OPENAI_API_KEY", state.apiKeyEnvironmentVariable)
    }

    @Test
    fun `missing key error uses selected provider key requirement`() {
        val controller = AssistantController(
            modelConfig = AiModelConfig.fromEnvironment(
                mapOf("AI_PROVIDER" to "anthropic"),
            ),
        )

        controller.ask("Summarize this repository")

        val state = controller.state.value
        assertFalse(state.isApiKeyConfigured)
        assertEquals("Anthropic Claude", state.providerName)
        assertEquals("AI_API_KEY", state.apiKeyEnvironmentVariable)
        assertEquals("Set AI_API_KEY before asking AI-backed repository questions.", state.error)
    }
}