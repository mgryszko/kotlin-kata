package aoc.day9

import aoc.Board
import aoc.OneDimensionalBoard
import aoc.Pos
import aoc.readLines

fun main() {
  val lines = "day9.txt".readLines().map { it.toCharArray().map(Char::digitToInt) }
  val heightMap = OneDimensionalBoard(lines.flatten(), lines.size, lines.first().size)

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

fun Board<Height>.lowPoints(): List<Pos> =
  positions().filter { pos -> adjacent(pos).all { get(pos) < get(it) } }

fun Board<Height>.neighboursExceptHighest(pos: Pos): List<Pos> = adjacent(pos).filter { get(it) != 9 }

fun basin(map: Board<Height>, initialPos: Pos): Set<Pos> {
  val posToExplore = ArrayDeque(listOf(initialPos))
  val basin = mutableSetOf<Pos>()
  while (posToExplore.isNotEmpty()) {
    val pos = posToExplore.removeFirst()
    basin += pos
    posToExplore += map.neighboursExceptHighest(pos).filterNot(basin::contains)
  }
  return basin
}
