package com.hellodave.repoassistant.tools

import java.nio.charset.MalformedInputException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

data class RepoToolLimits(
    val maxFiles: Int = 120,
    val maxSearchMatches: Int = 40,
    val maxSnippetLines: Int = 80,
    val maxFileBytes: Long = 256_000,
    val maxTreeDepth: Int = 4,
)

class RepoFileTools(
    repositoryRoot: Path,
    private val limits: RepoToolLimits = RepoToolLimits(),
) {
    private val sandbox = PathSandbox(repositoryRoot)

    fun listFiles(filter: String? = null): String {
        val normalizedFilter = filter?.trim()?.takeIf { it.isNotEmpty() }?.lowercase()
        val files = walkRepository()
            .filter { it.isRegularFile() }
            .map { sandbox.relativize(it) }
            .filter { relative -> normalizedFilter == null || relative.lowercase().contains(normalizedFilter) }
            .sorted()
            .take(limits.maxFiles)
            .toList()

        return if (files.isEmpty()) {
            "No files matched."
        } else {
            files.joinToString(separator = "\n")
        }
    }

    fun searchText(query: String): String {
        val normalizedQuery = query.trim()
        require(normalizedQuery.isNotEmpty()) { "Search query must not be blank." }

        val matches = mutableListOf<String>()
        walkRepository()
            .filter { it.isRegularFile() && Files.size(it) <= limits.maxFileBytes }
            .forEach { file ->
                if (matches.size >= limits.maxSearchMatches) return@forEach
                val lines = readLinesSafely(file) ?: return@forEach
                lines.forEachIndexed { index, line ->
                    if (matches.size < limits.maxSearchMatches && line.contains(normalizedQuery, ignoreCase = true)) {
                        matches += "${sandbox.relativize(file)}:${index + 1}: ${line.trim().take(220)}"
                    }
                }
            }

        return if (matches.isEmpty()) {
            "No matches for `$normalizedQuery`."
        } else {
            matches.joinToString(separator = "\n")
        }
    }

    fun readFileSnippet(path: String, startLine: Int = 1, maxLines: Int = limits.maxSnippetLines): String {
        require(startLine >= 1) { "startLine must be 1 or greater." }
        val cappedMaxLines = maxLines.coerceIn(1, limits.maxSnippetLines)
        val file = sandbox.resolve(path)
        require(file.isRegularFile()) { "Path is not a regular file: $path" }
        require(Files.size(file) <= limits.maxFileBytes) { "File is too large to read safely: $path" }

        val lines = readLinesSafely(file) ?: return "File could not be read as UTF-8 text: $path"
        val selected = lines.drop(startLine - 1).take(cappedMaxLines)
        if (selected.isEmpty()) return "No lines available from ${sandbox.relativize(file)}:$startLine."

        return selected.mapIndexed { offset, line ->
            "${startLine + offset}: $line"
        }.joinToString(separator = "\n")
    }

    fun projectTree(maxDepth: Int = limits.maxTreeDepth): String {
        val cappedDepth = maxDepth.coerceIn(1, limits.maxTreeDepth)
        val entries = walkRepository()
            .filter { it != sandbox.root }
            .filter { sandbox.root.relativize(it).nameCount <= cappedDepth }
            .map { path ->
                val relative = sandbox.relativize(path)
                val depth = sandbox.root.relativize(path).nameCount - 1
                "${"  ".repeat(depth)}${path.name}${if (path.isDirectory()) "/" else ""}"
                    .let { if (relative.isBlank()) path.name else it }
            }
            .distinct()
            .take(limits.maxFiles)
            .toList()

        return if (entries.isEmpty()) "Repository appears empty." else entries.joinToString("\n")
    }

    fun summarizeStructure(): String = buildString {
        appendLine("Repository root: ${sandbox.root.fileName}")
        appendLine("Top-level tree:")
        appendLine(projectTree(maxDepth = 2))
        appendLine()
        appendLine("Representative Kotlin/Gradle files:")
        appendLine(listFiles(".kt").lineSequence().take(25).joinToString("\n"))
    }

    private fun walkRepository(): Sequence<Path> {
        val stream: Stream<Path> = Files.walk(sandbox.root)
        return stream.use { paths ->
            paths.filter { sandbox.isAllowed(it) }.toList().asSequence()
        }
    }

    private fun readLinesSafely(file: Path): List<String>? = try {
        Files.readAllLines(file, StandardCharsets.UTF_8)
    } catch (_: MalformedInputException) {
        null
    }
}
