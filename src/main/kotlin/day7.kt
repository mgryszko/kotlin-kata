package aoc.day7

import aoc.readText
import kotlin.math.absoluteValue
import kotlin.math.floor

fun main() {
  val positions = "day7.txt".readText().trim().split(',').map(String::toInt).sorted()

  val part1 = (positions.minOrNull()!!..positions.maxOrNull()!!)
    .map { it to positions.constantCostOfMovementTo(it) }
  println(part1.minByOrNull(Pair<Int, Int>::second))
  println(positions.middleValue())
  println(positions.constantCostOfMovementTo(positions.middleValue()))

  val part2 = (positions.minOrNull()!!..positions.maxOrNull()!!)
    .map { it to positions.linearCostOfMovementTo(it) }
  println(part2.minByOrNull(Pair<Int, Int>::second))
  println(floor(positions.average()))
  println(positions.linearCostOfMovementTo(floor(positions.average()).toInt()))
  println(positions.linearCostOfMovementTo(floor(positions.average()).toInt() + 1))
}

fun List<Int>.constantCostOfMovementTo(pos: Int): Int = sumOf { (pos - it).absoluteValue }

fun List<Int>.linearCostOfMovementTo(pos: Int): Int = sumOf {
  val distance = (pos - it).absoluteValue
  distance * (distance + 1) / 2
}

fun List<Int>.middleValue(): Int = get(size / 2)


