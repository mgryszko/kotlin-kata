package aoc.day9

import aoc.Pos
import aoc.readLines

fun main() {
  val lines = "day9.txt".readLines()
  val heightMap = HeightMap(lines.map { it.toCharArray().map(Char::digitToInt) })

  val lowPos = heightMap.lowPoints()

  val riskLevels = lowPos.sumOf { p -> heightMap[p] } + lowPos.size
  println(riskLevels)
  assert(riskLevels == 570)

  val basins = lowPos.map { basin(heightMap, it) }
  val threeLargestBasins = basins.map(Set<Pos>::size).sortedDescending().take(3)
  println(threeLargestBasins.reduce(Int::times))
  assert(threeLargestBasins.reduce(Int::times) == 899392)
}

val sampleLines = listOf(
  "2199943210",
  "3987894921",
  "9856789892",
  "8767896789",
  "9899965678",
)

typealias Height = Int

fun basin(map: HeightMap, initialPos: Pos): Set<Pos> {
  val posToExplore = ArrayDeque(listOf(initialPos))
  val basin = mutableSetOf<Pos>()
  while (posToExplore.isNotEmpty()) {
    val pos = posToExplore.removeFirst()
    basin += pos
    posToExplore += map.neighboursExceptHighest(pos).filterNot(basin::contains)
  }
  return basin
}

data class HeightMap(val map: List<List<Height>>) {
  private val rows = map.size
  private val cols = map.first().size

  operator fun get(p: Pos): Height = map[p.row][p.col]

  fun lowPoints(): List<Pos> =
    (0 until rows).flatMap { r -> (0 until cols).map { c -> Pos(r, c) } }.filter { pos ->
      neighbours(pos).all { get(pos) < get(it) }
    }

  fun neighboursExceptHighest(pos: Pos): List<Pos> = neighbours(pos).filter { get(it) != 9 }

  private fun neighbours(pos: Pos): List<Pos> =
    listOfNotNull(up(pos), right(pos), down(pos), left(pos))

  private fun up(pos: Pos): Pos? =
    if (pos.row >= 1) pos.copy(row = pos.row - 1) else null

  private fun down(pos: Pos): Pos? =
    if (pos.row < rows - 1) pos.copy(row = pos.row + 1) else null

  private fun left(pos: Pos): Pos? =
    if (pos.col < cols - 1) pos.copy(col = pos.col + 1) else null

  private fun right(pos: Pos): Pos? =
    if (pos.col >= 1) pos.copy(col = pos.col - 1) else null

  override fun toString(): String =
    map.joinToString(System.lineSeparator()) { it.joinToString("") }
}
