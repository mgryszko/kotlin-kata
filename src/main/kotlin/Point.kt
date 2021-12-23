package aoc

data class Point(val x: Int, val y: Int) {
  override fun toString(): String = "($x,$y)"
}

fun String.toPoint(): Point =
  split(',').let { (x, y) -> Point(x.toInt(), y.toInt()) }
