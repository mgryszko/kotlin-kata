package aoc.day1

fun main() {
  val depths = "day1.txt".readLines().map(String::toInt)
  val increments = depths.increments()
  println(increments)
  val slidedIncrements = depths.windowed(3).map(List<Int>::sum).increments()
  println(slidedIncrements)
}

fun List<Int>.increments() = this.windowed(2).map { (first, second) -> if (second > first) 1 else 0  }.sum()

fun String.readLines(): List<String> {
  val resource = Thread.currentThread().contextClassLoader.getResourceAsStream(this)
  requireNotNull(resource) { "Resource $this not found" }
  return resource.reader().readLines()
}