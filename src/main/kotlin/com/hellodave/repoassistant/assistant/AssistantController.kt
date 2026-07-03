package com.hellodave.repoassistant.assistant

import com.hellodave.repoassistant.ui.ChatMessage
import com.hellodave.repoassistant.ui.ChatRole
import com.hellodave.repoassistant.ui.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory

class AssistantController(
    private val modelConfig: AiModelConfig,
    private val assistant: RepoAssistant = RepoAssistant(),
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _state = MutableStateFlow(
        UiState(
            isApiKeyConfigured = modelConfig.isConfigured,
            providerName = modelConfig.providerName,
            modelName = modelConfig.modelName,
            apiKeyEnvironmentVariable = modelConfig.apiKeyEnvironmentVariable,
            messages = listOf(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = "Choose a local repository, then ask a guided question about its architecture, entry points, or risks.",
                ),
            ),
        ),
    )

    val state: StateFlow<UiState> = _state.asStateFlow()

    fun updateRepositoryPath(path: String) {
        _state.value = _state.value.copy(repositoryPath = path, error = null)
    }

    fun ask(question: String) {
        val trimmedQuestion = question.trim()
        if (trimmedQuestion.isEmpty() || _state.value.isLoading) return

        if (!modelConfig.isConfigured) {
            showError("Set ${modelConfig.apiKeyEnvironmentVariable} before asking AI-backed repository questions.")
            return
        }

        val repositoryRoot = validateRepositoryRoot() ?: return
        val userMessage = ChatMessage(ChatRole.User, trimmedQuestion)
        _state.value = _state.value.copy(
            messages = _state.value.messages + userMessage,
            isLoading = true,
            error = null,
        )

        scope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    assistant.answer(
                        config = modelConfig,
                        repositoryRoot = repositoryRoot,
                        question = trimmedQuestion,
                    )
                }
            }

            _state.value = result.fold(
                onSuccess = { answer ->
                    _state.value.copy(
                        messages = _state.value.messages + ChatMessage(ChatRole.Assistant, answer),
                        isLoading = false,
                    )
                },
                onFailure = { error ->
                    _state.value.copy(
                        isLoading = false,
                        error = "Assistant failed: ${error.message ?: error::class.simpleName}",
                    )
                },
            )
        }
    }

    private fun validateRepositoryRoot(): Path? {
        val rawPath = _state.value.repositoryPath.trim()
        if (rawPath.isEmpty()) {
            showError("Choose a repository root before asking a repo-specific question.")
            return null
        }

        val path = runCatching { Path.of(rawPath).toAbsolutePath().normalize() }.getOrNull()
        if (path == null || !Files.exists(path) || !path.isDirectory()) {
            showError("Repository path is not a readable directory: $rawPath")
            return null
        }

        return path
    }

    private fun showError(message: String) {
        _state.value = _state.value.copy(error = message)
    }
}
