package com.hellodave.repoassistant.ui

enum class ChatRole {
    User,
    Assistant,
}

data class ChatMessage(
    val role: ChatRole,
    val content: String,
)

data class UiState(
    val repositoryPath: String = "",
    val isApiKeyConfigured: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
