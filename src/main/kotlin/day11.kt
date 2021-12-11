package aoc.day11

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
  val withIncreasedEnergy = increaseEnergy(levels)
  val glowingPositions = glowingPositions(withIncreasedEnergy)
  return glowNeighbours(withIncreasedEnergy, glowingPositions).let(::depleteGlowingEnergy)
}

fun increaseEnergy(levels: List<Energy>): List<Energy> = levels.map(Energy::inc)

fun glowingPositions(levels: List<Energy>): List<Pos> =
  levels.mapIndexedNotNull { i, energy -> if (energy.isGlowing()) i.toPos() else null }

fun glowNeighbours(levels: List<Energy>, glowingPositions: List<Pos>): List<Energy> =
  levels.toMutableList().also { nextLevels ->
    val stack = ArrayDeque(glowingPositions)
    while (stack.isNotEmpty()) {
      stack.removeFirst().neighbours().forEach { neighbour ->
        if (nextLevels.isNotGlowing(neighbour)) {
          nextLevels[neighbour.toIndex()]++
          if (nextLevels.isGlowing(neighbour)) stack.addLast(neighbour)
        }
      }
    }
  }

fun depleteGlowingEnergy(levels: List<Energy>): List<Energy> = levels.map { if (it.isGlowing()) 0 else it }

fun List<Energy>.isGlowing(pos: Pos): Boolean = get(pos.toIndex()).isGlowing()
fun List<Energy>.isNotGlowing(pos: Pos): Boolean = !isGlowing(pos)
fun List<Energy>.countGlowing(): Int = count(Energy::wasGlowing)
fun List<Energy>.allFlashes() = all(Energy::wasGlowing)

fun Energy.isGlowing(): Boolean = this >= 10
fun Energy.wasGlowing(): Boolean = this == 0

fun Int.toPos(): Pos = Pos(this / rows, this % cols)

fun Pos.toIndex() = row * rows + col

fun Pos.neighbours(): List<Pos> =
  listOfNotNull(up(), up()?.right(), right(), right()?.down(), down(), down()?.left(), left(), left()?.up())

fun Pos.up(): Pos? =
  if (row >= 1) copy(row = row - 1) else null

fun Pos.down(): Pos? =
  if (row < rows - 1) copy(row = row + 1) else null

fun Pos.left(): Pos? =
  if (col < cols - 1) copy(col = col + 1) else null

fun Pos.right(): Pos? =
  if (col >= 1) copy(col = col - 1) else null

fun List<Energy>.to2DString(): String =
  chunked(rows).joinToString("\n") { it.joinToString("") }

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

