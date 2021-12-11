package aoc

import java.io.InputStreamReader

fun String.readLines(): List<String> = toResourceReader().readLines()

fun String.readText(): String = toResourceReader().readText()

private fun String.toResourceReader(): InputStreamReader {
    val resource = Thread.currentThread().contextClassLoader.getResourceAsStream(this)
    requireNotNull(resource) { "Resource $this not found" }
    return resource.reader()
}

data class Pos(val row: Int, val col: Int) {
    override fun toString(): String = "($row,$col)"
}
