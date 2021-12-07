package aoc.day7

import aoc.readText
import kotlin.math.absoluteValue

fun main() {
  val positions = "day7.txt".readText().trim().split(',').map(String::toInt).sorted()

  val part1 = ((positions.first() + 1)..positions.last()).runningFold(positions.constantCostOfMovementTo(positions.first())) { cost, pos ->
    val p = positions.lowerBoundBinarySearch(pos)
    val (positionsLt, positionsGeq) = p to positions.size - p
    cost - positionsGeq + positionsLt
  }
  println(part1.minOrNull()!!)
  assert(part1.minOrNull() == 336120)

  val part2 = (positions.minOrNull()!!..positions.maxOrNull()!!)
    .map { it to positions.linearCostOfMovementTo(it) }
  println(part2.minByOrNull(Pair<Int, Int>::second))
  assert(part2.minByOrNull(Pair<Int, Int>::second) == (462 to 96864235))
}

val samplePositions = listOf(0, 1, 1, 2, 2, 2, 4, 7, 14, 16)

fun List<Int>.lowerBoundBinarySearch(element: Int): Int {
  val i = binarySearch(element)
  return if (i < 0) -i - 1
  else {
    var prevI = i
    while(prevI > 0) {
      if (this[prevI - 1] == element) prevI--
      else return prevI
    }
    return 0
  }
}

fun List<Int>.constantCostOfMovementTo(pos: Int): Int = sumOf { (pos - it).absoluteValue }

fun List<Int>.linearCostOfMovementTo(pos: Int): Int = sumOf {
  val distance = (pos - it).absoluteValue
  distance * (distance + 1) / 2
}
