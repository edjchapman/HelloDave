package com.hellodave.repoassistant.assistant

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import com.hellodave.repoassistant.tools.RepoToolRegistry
import java.nio.file.Path

class RepoAssistant {
    suspend fun answer(apiKey: String, repositoryRoot: Path, question: String): String {
        require(apiKey.isNotBlank())

        val agent = AIAgent(
            promptExecutor = MultiLLMPromptExecutor(GoogleLLMClient(apiKey)),
            llmModel = GoogleModels.Gemini2_5Pro,
            systemPrompt = AssistantPrompts.systemPrompt,
            toolRegistry = RepoToolRegistry.create(repositoryRoot),
            maxIterations = 8,
        )

        return agent.run(question)
    }
}
