package com.hellodave.repoassistant.tools

import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.nio.file.Files

class PathSandboxTest {
    @Test
    fun `accepts paths inside repository root`() {
        val root = Files.createTempDirectory("repo-sandbox")
        val file = root.resolve("src/Main.kt")
        file.parent.createDirectories()
        file.createFile()

        val sandbox = PathSandbox(root)

        assertEquals(file.normalize(), sandbox.resolve("src/Main.kt"))
        assertTrue(sandbox.isAllowed(file))
    }

    @Test
    fun `rejects traversal outside repository root`() {
        val root = Files.createTempDirectory("repo-sandbox")
        val sandbox = PathSandbox(root)

        assertFailsWith<IllegalArgumentException> {
            sandbox.resolve("../outside.txt")
        }
    }

    @Test
    fun `rejects absolute paths outside repository root`() {
        val root = Files.createTempDirectory("repo-sandbox")
        val outside = Files.createTempFile("outside", ".txt")
        val sandbox = PathSandbox(root)

        assertFailsWith<IllegalArgumentException> {
            sandbox.resolve(outside.toString())
        }
    }

    @Test
    fun `rejects ignored directories`() {
        val root = Files.createTempDirectory("repo-sandbox")
        val ignoredFile = root.resolve(".git/config")
        ignoredFile.parent.createDirectories()
        ignoredFile.createFile()
        val sandbox = PathSandbox(root)

        assertFailsWith<IllegalArgumentException> {
            sandbox.resolve(".git/config")
        }
        assertFalse(sandbox.isAllowed(ignoredFile))
    }

    @Test
    fun `repo tools cap snippets and search results while skipping ignored directories`() {
        val root = Files.createTempDirectory("repo-tools")
        root.resolve("src").createDirectories()
        root.resolve("src/App.kt").writeText((1..100).joinToString("\n") { "line $it query" })
        root.resolve(".gradle").createDirectories()
        root.resolve(".gradle/cache.txt").writeText("query should be ignored")

        val tools = RepoFileTools(root, RepoToolLimits(maxSearchMatches = 3, maxSnippetLines = 5))

        val snippet = tools.readFileSnippet("src/App.kt", startLine = 10, maxLines = 50)
        assertEquals(5, snippet.lines().size)
        assertContains(snippet, "10: line 10 query")

        val search = tools.searchText("query")
        assertEquals(3, search.lines().size)
        assertFalse(search.contains(".gradle"))
    }
}
