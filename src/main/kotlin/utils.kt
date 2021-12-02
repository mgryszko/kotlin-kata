package aoc

fun String.readLines(): List<String> {
    val resource = Thread.currentThread().contextClassLoader.getResourceAsStream(this)
    requireNotNull(resource) { "Resource $this not found" }
    return resource.reader().readLines()
}
