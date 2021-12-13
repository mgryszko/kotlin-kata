package aoc

data class Point(val x: Int, val y: Int)

fun String.toPoint(): Point =
  split(',').let { (x, y) -> Point(x.toInt(), y.toInt()) }
