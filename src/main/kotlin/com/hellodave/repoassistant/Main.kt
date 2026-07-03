package com.hellodave.repoassistant

import androidx.compose.ui.window.application
import com.hellodave.repoassistant.assistant.AiModelConfig
import com.hellodave.repoassistant.assistant.AssistantController
import com.hellodave.repoassistant.ui.App

fun main() = application {
    val controller = AssistantController(modelConfig = AiModelConfig.fromEnvironment())
    App(controller = controller, onCloseRequest = ::exitApplication)
}
