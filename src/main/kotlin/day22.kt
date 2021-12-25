package aoc.day22

import aoc.assert
import aoc.readLines
import java.math.BigInteger

fun main() {
  val steps = "day22.txt".readLines().map { line ->
    val (toggle, cuboid) = line.split(' ')
    Toggle.valueOf(toggle) to cuboid.toCuboid()
  }

  val activated = activate(steps)

  val initializationArea = Cuboid(Range(-50, 50), Range(-50, 50), Range(-50, 50))
  val countOnInitalizationArea = activated.mapNotNull { it.intersect(initializationArea) }.sum()
  println(countOnInitalizationArea)
  assert(countOnInitalizationArea, 644257.toBigInteger())

  val countOn = activated.sum()
  println(countOn)
  assert(countOn, 1235484513229032.toBigInteger())
}

fun activate(steps: List<Pair<Toggle, Cuboid>>): Set<Cuboid> {
  val activatedWithOverlaps = steps.drop(1).fold(setOf(steps.first().second)) { acc, (toggle, cuboid) ->
    when (toggle) {
      Toggle.on -> acc + cuboid
      Toggle.off -> acc - cuboid
    }
  }
  val overlapping = activatedWithOverlaps.overlapping()
  val nonOverlapping = overlapping.drop(1).fold(setOf(overlapping.first())) { acc, cuboid -> acc + cuboid }.toSet()
  return activatedWithOverlaps - overlapping + nonOverlapping
}

operator fun Iterable<Cuboid>.plus(other: Cuboid): Set<Cuboid> =
  flatMap { other + it }.toSet()

operator fun Iterable<Cuboid>.minus(other: Cuboid): Set<Cuboid> =
  flatMap { it - other }.toSet()

fun Collection<Cuboid>.overlapping(): Set<Cuboid> {
  val overlapping = mutableSetOf<Cuboid>()
  val thisAsList = toList()
  thisAsList.forEachIndexed { i, c1 ->
    thisAsList.subList(i + 1, size).forEach { c2 ->
      if(!c1.disjoint(c2)) {
        overlapping += c1
        overlapping += c2
      }
    }
  }
  return overlapping
}

enum class Toggle { on, off }

data class Cuboid(val x: Range, val y: Range, val z: Range) {
  val size: BigInteger
    get() = x.size.toBigInteger() * y.size.toBigInteger() * z.size.toBigInteger()

  fun intersect(other: Cuboid): Cuboid? =
    if (disjoint(other)) null
    else Cuboid(x.intersect(other.x), y.intersect(other.y), z.intersect(other.z))

  operator fun plus(other: Cuboid): List<Cuboid> {
    if (disjoint(other))
      return listOf(this, other)
    if (contains(other))
      return listOf(this)
    if (other.contains(this))
      return listOf(other)

    val otherDecomposed = other.x.decompose(x).flatMap { xRange ->
      other.y.decompose(y).flatMap { yRange ->
        other.z.decompose(z).map { zRange ->
          Cuboid(xRange, yRange, zRange)
        }
      }
    }
    return listOf(this) + otherDecomposed.mapNotNull { if (this.disjoint(it)) it else null }
  }

  operator fun minus(other: Cuboid): List<Cuboid> {
    if (disjoint(other))
      return listOf(this)
    if (other.contains(this))
      return emptyList()

    val thisDecomposed = x.decompose(other.x).flatMap { xRange ->
      y.decompose(other.y).flatMap { yRange ->
        z.decompose(other.z).map { zRange ->
          Cuboid(xRange, yRange, zRange)
        }
      }
    }
    return thisDecomposed.mapNotNull { if (it.disjoint(other)) it else null }
  }

  fun disjoint(other: Cuboid): Boolean =
    x.disjoint(other.x) || y.disjoint(other.y) || z.disjoint(other.z)

  private fun contains(other: Cuboid): Boolean =
    x.contains(other.x) && y.contains(other.y) && z.contains(other.z)

  override fun toString(): String = "x=$x,y=$y,z=$z"
}

data class Range(val from: Int, val to: Int) {
  init {
    require(to >= from) { "To $to must >= from $from" }
  }

  val size: Int
    get() = to - from + 1

  fun disjoint(other: Range): Boolean =
    other.to < from || to < other.from

  fun contains(other: Range): Boolean =
    from <= other.from && other.to <= to

  fun decompose(other: Range): List<Range> = when {
    // overlapping other to the left of this
    other.from < from && from <= other.to && other.to < to -> listOf(Range(from, other.to), Range(other.to + 1, to))
    // overlapping other to the right of this
    from < other.from && other.from <= to && to < other.to -> listOf(Range(from, other.from - 1), Range(other.from, to))
    // other completely contained in this
    from < other.from && other.to < to -> listOf(Range(from, other.from - 1), other, Range(other.to + 1, to))
    from == other.from && other.to < to -> listOf(other, Range(other.to + 1, to))
    from < other.from && other.to == to -> listOf(Range(from, other.from - 1), other)
    // this completely contained in other
    other.from < from && to < other.to -> listOf(this)
    // this and other equal
    else -> listOf(this)
  }

  fun intersect(other: Range): Range =
    Range(if (from < other.from) other.from else from, if (to < other.to) to else other.to)

  override fun toString(): String = "$from..$to"
}

fun Iterable<Cuboid>.sum(): BigInteger = sumOf(Cuboid::size)

fun String.toCuboid(): Cuboid {
  val (x, y, z) = split(',')
  return Cuboid(x.toRange(), y.toRange(), z.toRange())
}

fun String.toRange(): Range {
  val (_, range) = split('=')
  val (from, to) = range.split("..")
  return Range(from.toInt(), to.toInt())
}

val sample1 = listOf(
  Toggle.on to Cuboid(x = Range(10, 12), y = Range(10, 12), z = Range(10, 12)),
  Toggle.on to Cuboid(x = Range(11, 13), y = Range(11, 13), z = Range(11, 13)),
  Toggle.off to Cuboid(x = Range(9, 11), y = Range(9, 11), z = Range(9, 11)),
  Toggle.on to Cuboid(x = Range(10, 10), y = Range(10, 10), z = Range(10, 10)),
)

val sample2 = listOf(
  Toggle.on to Cuboid(x = Range(-20, 26), y = Range(-36, 17), z = Range(-47, 7)),
  Toggle.on to Cuboid(x = Range(-20, 33), y = Range(-21, 23), z = Range(-26, 28)),
  Toggle.on to Cuboid(x = Range(-22, 28), y = Range(-29, 23), z = Range(-38, 16)),
  Toggle.on to Cuboid(x = Range(-46, 7), y = Range(-6, 46), z = Range(-50, -1)),
  Toggle.on to Cuboid(x = Range(-49, 1), y = Range(-3, 46), z = Range(-24, 28)),
  Toggle.on to Cuboid(x = Range(2, 47), y = Range(-22, 22), z = Range(-23, 27)),
  Toggle.on to Cuboid(x = Range(-27, 23), y = Range(-28, 26), z = Range(-21, 29)),
  Toggle.on to Cuboid(x = Range(-39, 5), y = Range(-6, 47), z = Range(-3, 44)),
  Toggle.on to Cuboid(x = Range(-30, 21), y = Range(-8, 43), z = Range(-13, 34)),
  Toggle.on to Cuboid(x = Range(-22, 26), y = Range(-27, 20), z = Range(-29, 19)),
  Toggle.off to Cuboid(x = Range(-48, -32), y = Range(26, 41), z = Range(-47, -37)),
  Toggle.on to Cuboid(x = Range(-12, 35), y = Range(6, 50), z = Range(-50, -2)),
  Toggle.off to Cuboid(x = Range(-48, -32), y = Range(-32, -16), z = Range(-15, -5)),
  Toggle.on to Cuboid(x = Range(-18, 26), y = Range(-33, 15), z = Range(-7, 46)),
  Toggle.off to Cuboid(x = Range(-40, -22), y = Range(-38, -28), z = Range(23, 41)),
  Toggle.on to Cuboid(x = Range(-16, 35), y = Range(-41, 10), z = Range(-47, 6)),
  Toggle.off to Cuboid(x = Range(-32, -23), y = Range(11, 30), z = Range(-14, 3)),
  Toggle.on to Cuboid(x = Range(-49, -5), y = Range(-3, 45), z = Range(-29, 18)),
  Toggle.off to Cuboid(x = Range(18, 30), y = Range(-20, -8), z = Range(-3, 13)),
  Toggle.on to Cuboid(x = Range(-41, 9), y = Range(-7, 43), z = Range(-33, 15)),
  Toggle.on to Cuboid(x = Range(-54112, -39298), y = Range(-85059, -49293), z = Range(-27449, 7877)),
  Toggle.on to Cuboid(x = Range(967, 23432), y = Range(45373, 81175), z = Range(27513, 53682)),
)