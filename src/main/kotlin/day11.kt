package aoc.day11

import aoc.Board
import aoc.MutableBoard
import aoc.OneDimensionalBoard
import aoc.Pos

fun main() {
  val levels = octopuses.flatMap { it.toCharArray().map(Char::digitToInt) }

  val glowCycles = generateSequence(levels, ::glowCycle)

  val flashes = glowCycles.drop(1).take(100).sumOf(List<Energy>::countGlowing)
  println(flashes)
  assert(flashes == 1594)

  val synchronizedFlashes = glowCycles.indexOfFirst(List<Energy>::allFlashes)
  println(synchronizedFlashes)
  assert(synchronizedFlashes == 437)
}

typealias Energy = Int

const val rows = 10
const val cols = 10

fun glowCycle(levels: List<Energy>): List<Energy> {
  val withIncreasedEnergy = OneDimensionalBoard(increaseEnergy(levels), rows, cols)
  val glowingPositions = glowingPositions(withIncreasedEnergy)
  return glowNeighbours(withIncreasedEnergy, glowingPositions).let(::depleteGlowingEnergy)
}

fun increaseEnergy(levels: List<Energy>): List<Energy> = levels.map(Energy::inc)

fun glowingPositions(levels: Board<Energy>): List<Pos> =
  levels.positions().mapNotNull { pos -> if (levels[pos].isGlowing()) pos else null }

fun glowNeighbours(levels: MutableBoard<Energy>, glowingPositions: List<Pos>): List<Energy> {
  val stack = ArrayDeque(glowingPositions)
  while (stack.isNotEmpty()) {
    levels.adjacentAndDiagonal(stack.removeFirst()).forEach { neighbour ->
      if (levels.isNotGlowing(neighbour)) {
        levels[neighbour]++
        if (levels.isGlowing(neighbour)) stack.addLast(neighbour)
      }
    }
  }
  return levels.toList()
}

fun depleteGlowingEnergy(levels: List<Energy>): List<Energy> = levels.map { if (it.isGlowing()) 0 else it }

fun Board<Energy>.isGlowing(pos: Pos): Boolean = get(pos).isGlowing()
fun Board<Energy>.isNotGlowing(pos: Pos): Boolean = !isGlowing(pos)
fun List<Energy>.countGlowing(): Int = count(Energy::wasGlowing)
fun List<Energy>.allFlashes() = all(Energy::wasGlowing)

fun Energy.isGlowing(): Boolean = this >= 10
fun Energy.wasGlowing(): Boolean = this == 0

val sampleOctopuses = listOf(
  "5483143223",
  "2745854711",
  "5264556173",
  "6141336146",
  "6357385478",
  "4167524645",
  "2176841721",
  "6882881134",
  "4846848554",
  "5283751526",
)

val octopuses = listOf(
  "2682551651",
  "3223134263",
  "5848471412",
  "7438334862",
  "8731321573",
  "6415233574",
  "5564726843",
  "6683456445",
  "8582346112",
  "4617588236",
)

