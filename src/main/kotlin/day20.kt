package aoc.day20

import aoc.Pos
import aoc.assert
import aoc.readLines

fun main() {
  val lines = "day20.txt".readLines()

  val enhancementVector = lines.first().toEnhancementVector()
  val pixels = Image(lines.toLitPixels(), 0)

  val enhancedImages = generateSequence(pixels) { it.enhance(enhancementVector) }.drop(1)

  val litPixelsAfterEnhancing2Times = enhancedImages.take(2).last().litPixels.size
  println(litPixelsAfterEnhancing2Times)
  assert(litPixelsAfterEnhancing2Times, 5432)

  val litPixelsAfterEnhancing50Times = enhancedImages.take(50).last().litPixels.size
  println(litPixelsAfterEnhancing50Times)
  assert(litPixelsAfterEnhancing50Times, 16016)
}

data class Image(val litPixels: Set<Pos>, val infinityPixel: Int) {
  private val minRow = litPixels.minOf(Pos::row)
  private val maxRow = litPixels.maxOf(Pos::row)
  private val minCol = litPixels.minOf(Pos::col)
  private val maxCol = litPixels.maxOf(Pos::col)

  fun enhance(enhancementVector: List<Int>): Image = Image(
    litPixels = toEnhance().mapNotNull { pixel -> if (enhancementVector[pixel.enhancementIndex()] == 1) pixel else null }.toSet(),
    infinityPixel = if (infinityPixel == 1) enhancementVector.last() else enhancementVector.first(),
  )

  private fun toEnhance() =
    ((minRow - 1)..(maxRow + 1)).flatMap { row -> ((minCol - 1)..(maxCol + 1)).map { col -> Pos(row, col) } }.toSet()

  private fun Pos.enhancementIndex() =
    neighbours().map { pos ->
      when {
        pos in litPixels -> 1
        pos.row < minRow -> infinityPixel
        pos.row > maxRow -> infinityPixel
        pos.col < minCol -> infinityPixel
        pos.col > maxCol -> infinityPixel
        else -> 0
      }
    }.joinToString("").toInt(2)

  private fun Pos.neighbours(): List<Pos> =
    listOf(
      Pos(row - 1, col - 1), Pos(row - 1, col), Pos(row - 1, col + 1),
      Pos(row, col - 1), Pos(row, col), Pos(row, col + 1),
      Pos(row + 1, col - 1), Pos(row + 1, col), Pos(row + 1, col + 1),
    )
}

fun String.toEnhancementVector(): List<Int> = map { if (it.isLit()) 1 else 0 }

fun Char.isLit(): Boolean = this == '#'

fun List<String>.toLitPixels() =
  drop(2).flatMapIndexed { row, line -> line.mapIndexedNotNull { col, c -> if (c.isLit()) Pos(row, col) else null } }.toSet()
