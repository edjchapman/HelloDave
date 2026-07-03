package com.hellodave.repoassistant

import androidx.compose.ui.window.application
import com.hellodave.repoassistant.assistant.AssistantController
import com.hellodave.repoassistant.ui.App

fun main() = application {
    val controller = AssistantController(geminiApiKey = System.getenv("GEMINI_API_KEY"))
    App(controller = controller, onCloseRequest = ::exitApplication)
}
