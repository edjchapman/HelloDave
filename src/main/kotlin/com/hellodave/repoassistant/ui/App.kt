package com.hellodave.repoassistant.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import com.hellodave.repoassistant.assistant.AssistantController

@Composable
fun App(controller: AssistantController, onCloseRequest: () -> Unit) {
    val state by controller.state.collectAsState()

    Window(onCloseRequest = onCloseRequest, title = "Repo Explorer Assistant") {
        MaterialTheme {
            ChatScreen(
                state = state,
                onRepositoryPathChange = controller::updateRepositoryPath,
                onSubmit = controller::ask,
            )
        }
    }
}
