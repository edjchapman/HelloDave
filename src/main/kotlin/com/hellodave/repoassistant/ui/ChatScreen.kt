package com.hellodave.repoassistant.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val suggestedPrompts = listOf(
    "Summarize this repository's architecture and entry points.",
    "Find the main user-facing workflows and explain how they connect.",
    "Identify the highest-risk areas to refactor first.",
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatScreen(
    state: UiState,
    onRepositoryPathChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
) {
    var prompt by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Repo Explorer Assistant", style = MaterialTheme.typography.h4)
            Text("Ask Gemini-backed Koog tools about a local repository with bounded, read-only file access.")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.repositoryPath,
                    onValueChange = onRepositoryPathChange,
                    label = { Text("Repository root path") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                Text(
                    text = if (state.isApiKeyConfigured) "Gemini key configured" else "Missing GEMINI_API_KEY",
                    color = if (state.isApiKeyConfigured) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                    modifier = Modifier.padding(top = 20.dp),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                suggestedPrompts.forEach { suggestion ->
                    Chip(onClick = {
                        prompt = suggestion
                        onSubmit(suggestion)
                    }) {
                        Text(suggestion.substringBefore(" and ").take(32))
                    }
                }
            }

            state.error?.let { error ->
                Card(backgroundColor = Color(0xFFFFEBEE), modifier = Modifier.fillMaxWidth()) {
                    Text(error, color = Color(0xFFB71C1C), modifier = Modifier.padding(12.dp))
                }
            }

            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.messages) { message ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(message.role.name, style = MaterialTheme.typography.subtitle2)
                            Spacer(Modifier.height(4.dp))
                            Text(message.content)
                        }
                    }
                }
                if (state.isLoading) {
                    item { Text("Thinking with repository tools...") }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Ask about the selected repository") },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading,
                )
                Button(
                    onClick = {
                        onSubmit(prompt)
                        prompt = ""
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text("Ask")
                }
            }
        }
    }
}
