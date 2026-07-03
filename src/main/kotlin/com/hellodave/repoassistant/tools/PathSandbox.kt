package com.hellodave.repoassistant.tools

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

class PathSandbox(
    root: Path,
    private val ignoredDirectoryNames: Set<String> = DefaultIgnoredDirectories,
) {
    val root: Path = root.toAbsolutePath().normalize()
    private val realRoot: Path

    init {
        require(Files.exists(this.root)) { "Repository root does not exist: ${this.root}" }
        require(Files.isDirectory(this.root)) { "Repository root is not a directory: ${this.root}" }
        realRoot = this.root.toRealPath()
    }

    fun resolve(relativeOrAbsolutePath: String): Path {
        val requested = Path.of(relativeOrAbsolutePath)
        val normalized = if (requested.isAbsolute) {
            requested.normalize()
        } else {
            root.resolve(requested).normalize()
        }

        require(normalized.startsWith(root)) { "Path escapes repository root: $relativeOrAbsolutePath" }
        require(!containsIgnoredDirectory(normalized)) { "Path is in an ignored directory: $relativeOrAbsolutePath" }
        require(realPathStaysInsideRoot(normalized)) {
            "Path escapes repository root through a symbolic link: $relativeOrAbsolutePath"
        }

        return normalized
    }

    fun isAllowed(path: Path): Boolean {
        val normalized = path.toAbsolutePath().normalize()
        return normalized.startsWith(root) &&
            !containsIgnoredDirectory(normalized) &&
            realPathStaysInsideRoot(normalized)
    }

    fun relativize(path: Path): String = root.relativize(path.toAbsolutePath().normalize()).joinToString("/")

    private fun containsIgnoredDirectory(path: Path): Boolean {
        val relative = runCatching { root.relativize(path.toAbsolutePath().normalize()) }.getOrNull() ?: return true
        return relative.any { segment -> segment.name in ignoredDirectoryNames }
    }

    private fun realPathStaysInsideRoot(path: Path): Boolean =
        !Files.exists(path) || runCatching { path.toRealPath().startsWith(realRoot) }.getOrDefault(false)

    companion object {
        val DefaultIgnoredDirectories = setOf(
            ".git",
            ".gradle",
            ".idea",
            "build",
            "out",
            "target",
            "node_modules",
            ".next",
            "dist",
        )
    }
}
