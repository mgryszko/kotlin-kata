package aoc.day5

import aoc.Point
import aoc.readLines
import aoc.toPoint
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun main() {
  val lines = "day5.txt".readLines().map(::toLine)

  println(pointsWithAtLeastTwoIntersections(lines.filter(Line::isStraight)))
  println(pointsWithAtLeastTwoIntersections(lines.filter(Line::isStraightOrDiagonal)))
}

fun toLine(line: String): Line =
  line.split(" -> ").let { (p1, p2) -> Line(p1.toPoint(), p2.toPoint()) }

fun pointsWithAtLeastTwoIntersections(lines: List<Line>): Int {
  val intersectionsByPoints = mutableMapOf<Point, Int>()
  val pointsByIntersections = mutableMapOf<Int, MutableSet<Point>>()
  lines.map(Line::points).forEach { points ->
    points.forEach { point ->
      intersectionsByPoints.compute(point) { _, intersections ->
        val prevIntersections = intersections ?: 0
        val nextIntersections = prevIntersections + 1
        pointsByIntersections.remove(prevIntersections, point)
        pointsByIntersections.add(nextIntersections, point)
        nextIntersections
      }
    }
  }
  return pointsByIntersections.filterKeys { intersections -> intersections >= 2 }
    .values.sumOf(MutableSet<Point>::size)
}

fun MutableMap<Int, MutableSet<Point>>.remove(intersections: Int, point: Point) =
  get(intersections)?.remove(point)

fun MutableMap<Int, MutableSet<Point>>.add(intersections: Int, point: Point) {
  val points = (get(intersections) ?: mutableSetOf()).apply { add(point) }
  set(intersections, points)
}

data class Line(val p1: Point, val p2: Point) {
  fun isStraight(): Boolean = isHorizontal() || isVertical()

  fun isStraightOrDiagonal(): Boolean =
    isHorizontal() || isVertical() || isDiagonal()

  fun points(): List<Point> =
    if (isHorizontal())
      (min(p1.y, p2.y)..max(p1.y, p2.y)).map { Point(p1.x, it) }
    else if (isVertical())
      (min(p1.x, p2.x)..max(p1.x, p2.x)).map { Point(it, p1.y) }
    else if (isDiagonal())
      p1.x.upOrDownTo(p2.x).zip(p1.y.upOrDownTo(p2.y)).map { (x, y) -> Point(x, y)}
    else
      throw IllegalArgumentException("No straight or diagonal line: $this")

  private fun isVertical() = p1.y == p2.y

  private fun isHorizontal() = p1.x == p2.x

  private fun isDiagonal() = (p2.x - p1.x).absoluteValue == (p2.y - p1.y).absoluteValue

  private fun Int.upOrDownTo(to: Int): IntProgression = if (this < to) this..to else this.downTo(to)
}
