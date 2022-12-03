package aoc.day1

import aoc.println
import aoc.readText
import kotlin.math.max

fun main() {
  val calories = "day1.txt".readText().split("\n\n").map { cals ->
    cals.split("\n").filter(String::isNotBlank).map(String::toInt).sum()
  }
  val maxCalories = calories.fold(0, ::max)
  maxCalories.println()
  val topThree = calories.sortedDescending().take(3).sum()
  topThree.println()
}