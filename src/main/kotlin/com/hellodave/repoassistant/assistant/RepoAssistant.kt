package com.hellodave.repoassistant.assistant

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import com.hellodave.repoassistant.tools.RepoToolRegistry
import java.nio.file.Path

class RepoAssistant {
    suspend fun answer(config: AiModelConfig, repositoryRoot: Path, question: String): String {
        val apiKey = requireNotNull(config.apiKey?.takeIf { it.isNotBlank() })

        val agent = AIAgent(
            promptExecutor = MultiLLMPromptExecutor(config.createClient(apiKey)),
            llmModel = config.model,
            systemPrompt = AssistantPrompts.systemPrompt,
            toolRegistry = RepoToolRegistry.create(repositoryRoot),
            maxIterations = 8,
        )

        return agent.run(question)
    }
}
