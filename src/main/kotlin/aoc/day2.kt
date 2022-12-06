package aoc.day2

import aoc.println
import aoc.readLines

fun main() {
  val pairs = "day2.txt".readLines()
    .map { it.split(' ') }
  pairs
    .map { (opponent, yours) -> toShape(opponent) to toShape(yours) }
    .sumOf { (opponent, yours) -> yours.outcome(opponent).score + yours.score }
    .println()

  pairs
    .map { (opponent, yours) -> toShape(opponent) to toOutcome(yours) }
    .map { (opponent, yours) -> opponent to yours.shape(opponent) }
    .sumOf { (opponent, yours) -> yours.outcome(opponent).score + yours.score }
    .println()

}

fun toShape(move: String): Shape = when (move) {
  "A", "X" -> Shape.ROCK
  "B", "Y" -> Shape.PAPER
  "C", "Z" -> Shape.SCISSORS
  else -> throw IllegalArgumentException("Move symbol $move not recognized")
}

fun toOutcome(outcome: String): Outcome = when (outcome) {
  "X" -> Outcome.LOST
  "Y" -> Outcome.DRAW
  "Z" -> Outcome.WON
  else -> throw IllegalArgumentException("Outcome symbol $outcome not recognized")
}

enum class Shape(val score: Int) {
  PAPER(2),
  ROCK(1),
  SCISSORS(3);

  fun outcome(opponent: Shape): Outcome =
    when (this to opponent) {
      ROCK to ROCK -> Outcome.DRAW
      ROCK to PAPER -> Outcome.LOST
      ROCK to SCISSORS -> Outcome.WON
      PAPER to ROCK -> Outcome.WON
      PAPER to SCISSORS -> Outcome.LOST
      PAPER to PAPER -> Outcome.DRAW
      SCISSORS to ROCK -> Outcome.LOST
      SCISSORS to PAPER -> Outcome.WON
      SCISSORS to SCISSORS -> Outcome.DRAW
      else -> throw IllegalArgumentException("Outcome of $this to $opponent unknown")
    }
}

enum class Outcome(val score: Int) {
  LOST(0),
  DRAW(3),
  WON(6);

  fun shape(opponent: Shape): Shape =
    when (this to opponent) {
      LOST to Shape.ROCK -> Shape.SCISSORS
      LOST to Shape.PAPER -> Shape.ROCK
      LOST to Shape.SCISSORS -> Shape.PAPER
      DRAW to Shape.ROCK -> Shape.ROCK
      DRAW to Shape.PAPER -> Shape.PAPER
      DRAW to Shape.SCISSORS -> Shape.SCISSORS
      WON to Shape.ROCK -> Shape.PAPER
      WON to Shape.PAPER -> Shape.SCISSORS
      WON to Shape.SCISSORS -> Shape.ROCK
      else -> throw IllegalArgumentException("Move to $this given opponent shape $opponent unknown")
    }
}
