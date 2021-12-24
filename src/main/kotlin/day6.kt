package aoc.day6

import aoc.readText
import aoc.sum
import java.math.BigInteger
import java.math.BigInteger.ZERO

fun main() {
  val population = "day6.txt".readText().trim().split(',').map(String::toInt)
    .groupBy { it }.mapValues { (_, v) -> v.size.toBigInteger() }

  println(laternfishPopulationAfterDays(population, 80).values.sum())
  println(laternfishPopulationAfterDays(population, 256).values.sum())
}

fun laternfishPopulationAfterDays(population: Map<Int, BigInteger>, days: Int): Map<Int, BigInteger> =
  (0 until days).fold(population) { popByAge, _ ->
    val nextDayPopulation = popByAge.mapKeys { (age, _) -> age - 1 }
    val reproducingFishes = nextDayPopulation[-1] ?: ZERO
    (nextDayPopulation - -1) + mapOf(
      6 to ((nextDayPopulation[6] ?: ZERO) + reproducingFishes),
      8 to reproducingFishes
    )
  }
