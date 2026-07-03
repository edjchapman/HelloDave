package com.hellodave.repoassistant.tools

import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import java.nio.file.Path

@LLMDescription("Read-only tools for safely exploring the selected local repository")
class RepoToolRegistry(repositoryRoot: Path) : ToolSet {
    private val tools = RepoFileTools(repositoryRoot)

    @Tool
    @LLMDescription("List repository files, optionally filtering by a path substring or extension. Generated and heavy directories are skipped.")
    fun listProjectFiles(
        @LLMDescription("Optional file path substring or extension, such as `.kt`, `README`, or `src/main`") filter: String? = null,
    ): String = tools.listFiles(filter)

    @Tool
    @LLMDescription("Search UTF-8 text files in the repository and return capped matches with file and line citations.")
    fun searchFileContents(
        @LLMDescription("Text query to search for") query: String,
    ): String = tools.searchText(query)

    @Tool
    @LLMDescription("Read a bounded snippet from a repository file. The path must stay inside the selected repository root.")
    fun readFileSnippet(
        @LLMDescription("Repository-relative file path") path: String,
        @LLMDescription("One-based starting line") startLine: Int = 1,
        @LLMDescription("Maximum number of lines to read") maxLines: Int = 60,
    ): String = tools.readFileSnippet(path = path, startLine = startLine, maxLines = maxLines)

    @Tool
    @LLMDescription("Summarize the discovered project structure with a bounded tree and representative source files.")
    fun summarizeProjectStructure(): String = tools.summarizeStructure()

    companion object {
        fun create(repositoryRoot: Path): ToolRegistry = ToolRegistry {
            tools(RepoToolRegistry(repositoryRoot))
        }
    }
}
