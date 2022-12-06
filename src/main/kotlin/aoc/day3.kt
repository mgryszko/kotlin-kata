package aoc.day3

import aoc.println
import aoc.readLines

fun main() {
  "day3.txt".readLines()
    .map { line -> line.subSequence(0, line.length / 2).toSet() to line.subSequence(line.length / 2, line.length).toSet() }
    .map { (left, right) -> left.intersect(right).first() }
    .sumOf(::priority)
    .println()

  "day3.txt".readLines()
    .windowed(3, 3)
    .map { lines -> lines.map(String::toSet).reduce(Set<Char>::intersect).first() }
    .sumOf(::priority)
    .println()
}

fun priority(item: Char): Int =
  if (item.isUpperCase()) item.code - 38 else item.code - 96

