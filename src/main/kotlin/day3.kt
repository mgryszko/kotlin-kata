package aoc.day3

import aoc.readLines

fun main() {
  val numbers = "day3.txt".readLines()

  val (gammaRate, epsilonRate) = gammaEpsilonRates(numbers)
  println("$gammaRate $epsilonRate ${gammaRate * epsilonRate}")

  val (oxygenGeneratorRating, co2ScrubberRating) = oxygenGeneratorCO2ScrubberRatings(numbers)
  println("$oxygenGeneratorRating $co2ScrubberRating ${oxygenGeneratorRating * co2ScrubberRating}")
}

fun oxygenGeneratorCO2ScrubberRatings(numbers: List<String>): Pair<Int, Int> {
  fun bitCountOnIndex(numbers: List<String>, i: Int) =
    numbers.groupBy { it[i] }.mapKeys { it.key.toString().toInt() }

  val bitNumbersComparator = Comparator.comparing<Pair<Int, List<String>>, Int> { (_, numbers) -> numbers.size }
    .thenComparing { (bit, _) -> bit }

  fun rating(numbers: List<String>, comparator: Comparator<Pair<Int, List<String>>>) =
    (0 until numbers.bits()).fold(numbers) { reduced, i ->
      if (reduced.size == 1) reduced
      else {
        val bitCounts = bitCountOnIndex(reduced, i)
        bitCounts.toList().sortedWith(comparator).first().second
      }
  }.first().toInt(2)

  return rating(numbers, bitNumbersComparator.reversed()) to rating(numbers, bitNumbersComparator)
}

fun gammaEpsilonRates(numbers: List<String>): Pair<Int, Int> {
  fun countToNumber(bitCounts: List<Int>, countToBit: (Int) -> Boolean) =
    bitCounts.map { if (countToBit(it)) 1 else 0 }.joinToString("").toInt(2)

  val bitCounts = MutableList(numbers.bits()) { 0 }
  numbers.forEach { number ->
    number.forEachIndexed { i, bit ->
      bitCounts[i] += bit.toString().toInt()
    }
  }
  val gammaBit = { count: Int -> count >= numbers.size / 2 }
  val epsilonBit = { count: Int -> count < numbers.size / 2 }
  return countToNumber(bitCounts, gammaBit) to countToNumber(bitCounts, epsilonBit)
}

fun List<String>.bits() = first().length
