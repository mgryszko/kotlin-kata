package aoc.day13

import aoc.Point
import aoc.draw
import aoc.readLines
import aoc.toPoint

fun main() {
  val lines = "day13.txt".readLines()
  val separatorDotsFolds = lines.indexOfFirst(String::isBlank)
  val sheet = lines.take(separatorDotsFolds).map(String::toPoint).toSet()
  val folds = lines.drop(separatorDotsFolds + 1).toFolds()

  val afterFirstFold = folds.take(1).fold(sheet)
  println(afterFirstFold.size)
  assert(afterFirstFold.size == 745)

  val foldedSheet = folds.fold(sheet)
  val printedSheet = foldedSheet.draw()

  println(printedSheet.draw())
  // ABKJFBGC
}

fun Set<Point>.draw(): List<List<String>> {
  val min = Point(minOf(Point::x), minOf(Point::y))
  val max = Point(maxOf(Point::x), maxOf(Point::y))
  val rows = max.y - min.y
  val cols = max.x - min.x

  return MutableList(rows + 1) { MutableList(cols + 1) { "." } }.also { printedSheet ->
    this.forEach { (x, y) -> printedSheet[y - min.y][x - min.x] = "#" }
  }
}

fun List<String>.toFolds(): List<Fold> = map { line ->
  line.split("=").let { (foldAxis, coord) ->
    when (foldAxis) {
      "fold along x" -> XFold(coord.toInt())
      "fold along y" -> YFold(coord.toInt())
      else -> error("Unsupported fold: $line")
    }
  }
}

fun List<Fold>.fold(initialSheet: Set<Point>) = fold(initialSheet) { sheet, fold -> fold.fold(sheet) }

sealed class Fold(val coord: Int) {
  abstract fun fold(sheet: Set<Point>): Set<Point>
}

class XFold(coord: Int) : Fold(coord) {
  override fun fold(sheet: Set<Point>): Set<Point> = sheet.map { point ->
    if (point.x <= coord) point
    else point.copy(x = coord - (point.x - coord))
  }.toSet()
}

class YFold(coord: Int) : Fold(coord) {
  override fun fold(sheet: Set<Point>): Set<Point> = sheet.map { point ->
    if (point.y <= coord) point
    else point.copy(y = coord - (point.y - coord))
  }.toSet()
}

val sampleSheet = setOf(
  Point(6, 10),
  Point(0, 14),
  Point(9, 10),
  Point(0, 3),
  Point(10, 4),
  Point(4, 11),
  Point(6, 0),
  Point(6, 12),
  Point(4, 1),
  Point(0, 13),
  Point(10, 12),
  Point(3, 4),
  Point(3, 0),
  Point(8, 4),
  Point(1, 10),
  Point(2, 14),
  Point(8, 10),
  Point(9, 0),
)

val sampleFolds = listOf(YFold(7), XFold(5))