package aoc.day3

import aoc.println
import aoc.readLines

fun main() {
  val ranges = "day4.txt".readLines()
    .map { line ->
      val (first, second) = line.split(',')
      toRange(first) to toRange(second)
    }

  ranges
    .map { (first, second) -> first.fullyOverlaps(second) || second.fullyOverlaps(first) }
    .count { it }
    .println()
  ranges
    .map { (first, second) -> first.partiallyOverlaps(second) || first.fullyOverlaps(second) || second.fullyOverlaps(first) }
    .count { it }
    .println()
}

fun toRange(str: String): IntRange {
  val (first, last) = str.split('-')
  return first.toInt()..last.toInt()
}

fun IntRange.fullyOverlaps(other: IntRange): Boolean =
  this.first >= other.first && this.last <= other.last

fun IntRange.partiallyOverlaps(other: IntRange): Boolean =
  this.first >= other.first && this.first <= other.last
    || other.first >= this.first && other.first <= this.last
