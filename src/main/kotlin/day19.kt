package aoc.day19

import aoc.assert
import aoc.readLines
import kotlin.math.absoluteValue

fun main() {
  val scansByScannerIds = "day19.txt".readLines().parse()

  val scanners = position(scansByScannerIds).positionedScanners()

  val beacons = scanners.flatMap(PositionedScanner::scansRelativeOrigin).toSet()
  println(beacons.size)
  assert(beacons.size, 465)

  val maxManhattanDistance = scanners.flatMap { (pos1) -> scanners.map { (pos2) -> Delta(pos1, pos2).manhattan } }.maxOrNull()
  println(maxManhattanDistance)
  assert(maxManhattanDistance, 12149)
}

@Suppress("UNCHECKED_CAST")
fun position(scansByScannerIds: List<List<Point>>): List<Scanner> {
  val scanners = mutableListOf<Scanner>(PositionedScanner(Point(0, 0, 0), scansByScannerIds[0]))
  scanners += scansByScannerIds.drop(1).map(::UnpositionedScanner)

  val references = ArrayDeque(listOf(scanners[0] as PositionedScanner))
  while (references.isNotEmpty()) {
    val reference = references.removeFirst()
    scanners.unpositionedScanners().forEach { (id, unpositioned) ->
      reference.position(unpositioned)?.let { positioned ->
        scanners[id] = positioned
        references.addLast(positioned)
      }
    }
  }

  return scanners
}

fun List<Scanner>.unpositionedScanners(): List<Pair<Int, UnpositionedScanner>> =
  mapIndexedNotNull { id, scanner -> if (scanner is UnpositionedScanner) id to scanner else null }

fun List<Scanner>.positionedScanners(): List<PositionedScanner> =
  mapNotNull { scanner -> if (scanner is PositionedScanner) scanner else null }

typealias Distance = Int

sealed interface Scanner

data class PositionedScanner(val pos: Point, val scans: List<Point>) : Scanner {
  val scansRelativeOrigin: List<Point>
    get() = scans.map(pos::plus)

  fun position(other: UnpositionedScanner): PositionedScanner? {
    val distancesBetweenPoints = distancesBetweenAllBeacons()

    val commonBeacons = other.commonBeacons(distancesBetweenPoints) ?: return null

    val sourceReference = commonBeacons.keys.flatMap(UndirectedLine::toList).findMostCommon()!!
    val targetReference = commonBeacons.values.flatMap(UndirectedLine::toList).findMostCommon()!!
    val rotation = commonBeacons.firstNotNullOfOrNull { (ul1, ul2) ->
      val l1 = ul1.toDirectedLine(sourceReference)
      val l2 = ul2.toDirectedLine(targetReference)
      val l1Delta = l1.delta()
      Rotation.all.firstOrNull { l1Delta == l2.rotate(it).delta() }
    }

    return rotation?.let { other.position(pos + Delta(targetReference.rotate(it), sourceReference), it) }
  }

  private fun distancesBetweenAllBeacons(): Map<Distance, UndirectedLine> =
    scans.flatMapIndexed { i, p1 ->
      scans.drop(i + 1).map { p2 -> p1.squaredDistanceTo(p2) to UndirectedLine(p1, p2) }
    }.toMap()

  private fun <T> List<T>.findMostCommon(): T? = groupBy { it }.maxByOrNull { it.value.size }?.key
}

data class UnpositionedScanner(val scans: List<Point>) : Scanner {
  fun commonBeacons(sourceDistances: Map<Distance, UndirectedLine>): Map<UndirectedLine, UndirectedLine>? =
    scans.asSequence().map { p1 ->
      val targetDistances = scans.associate { p2 -> p1.squaredDistanceTo(p2) to UndirectedLine(p1, p2) }
      check(targetDistances.size == scans.size) { "There is non-unique distance between $p1 and some other points " }
      sourceDistances.intersect(targetDistances)
    }.firstOrNull { it.size >= 11 }

  private fun Map<Distance, UndirectedLine>.intersect(other: Map<Distance, UndirectedLine>): Map<UndirectedLine, UndirectedLine> =
    mutableMapOf<UndirectedLine, UndirectedLine>().also { sourcePoints ->
      entries.forEach { (d, l1) ->
        other[d]?.also { l2 -> sourcePoints[l1] = l2 }
      }
    }

  fun position(pos: Point, rotation: Rotation): PositionedScanner =
    PositionedScanner(pos, scans.map { it.rotate(rotation) })
}

data class Point(val x: Int, val y: Int, val z: Int) {
  fun squaredDistanceTo(other: Point): Distance =
    (other.x - x) * (other.x - x) + (other.y - y) * (other.y - y) + (other.z - z) * (other.z - z)

  fun rotate(rotation: Rotation): Point =
    Point(x = rotateAxis(rotation.x), y = rotateAxis(rotation.y), z = rotateAxis(rotation.z))

  private fun rotateAxis(rotation: Pair<Axis, Flip>): Int =
    when (rotation.first) {
      Axis.X -> x
      Axis.Y -> y
      Axis.Z -> z
    } * rotation.second

  operator fun plus(delta: Delta): Point = Point(x = x + delta.dx, y = y + delta.dy, z = z + delta.dz)

  operator fun plus(point: Point): Point = Point(x = x + point.x, y = y + point.y, z = z + point.z)

  override fun toString(): String = "($x,$y,$z)"
}

enum class Axis { X, Y, Z }
typealias Flip = Int

data class Rotation(val x: Pair<Axis, Flip>, val y: Pair<Axis, Flip>, val z: Pair<Axis, Flip>) {
  companion object {
    val all: List<Rotation> = listOf(
      Triple(Axis.X, Axis.Y, Axis.Z),
      Triple(Axis.X, Axis.Z, Axis.Y),
      Triple(Axis.Y, Axis.X, Axis.Z),
      Triple(Axis.Y, Axis.Z, Axis.X),
      Triple(Axis.Z, Axis.Y, Axis.X),
      Triple(Axis.Z, Axis.X, Axis.Y),
    ).flatMap { (x, y, z) ->
      listOf(
        Triple(1, 1, 1),
        Triple(1, 1, -1),
        Triple(1, -1, 1),
        Triple(-1, 1, 1),
        Triple(1, -1, -1),
        Triple(-1, -1, 1),
        Triple(-1, 1, -1),
        Triple(-1, -1, -1),
      ).map { (xFlip, yFlip, zFlip) -> Rotation(x = x to xFlip, y = y to yFlip, z = z to zFlip) }
    }
  }
}

data class Delta(val dx: Int, val dy: Int, val dz: Int) {
  constructor(p1: Point, p2: Point) : this(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z)

  val manhattan: Distance = dx.absoluteValue + dy.absoluteValue + dz.absoluteValue

  override fun toString(): String = "|$dx,$dy,$dz|"
}

data class UndirectedLine(val p1: Point, val p2: Point) {
  fun toList(): List<Point> = listOf(p1, p2)

  fun toDirectedLine(start: Point): DirectedLine =
    if (p1 == start) DirectedLine(p1, p2)
    else if (p2 == start) DirectedLine(p2, p1)
    else error("Start point $start not found in $this")

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as UndirectedLine

    return (p1 == other.p1 && p2 == other.p2) || (p1 == other.p2 && p2 == other.p1)
  }

  override fun hashCode(): Int = p1.hashCode() + p2.hashCode()

  override fun toString(): String = "$p1<->$p2"
}

data class DirectedLine(val p1: Point, val p2: Point) {
  fun delta(): Delta = Delta(p1, p2)

  fun rotate(rotation: Rotation): DirectedLine =
    DirectedLine(p1 = p1.rotate(rotation), p2 = p2.rotate(rotation))

  override fun toString(): String = "$p1->$p2"
}

fun List<String>.parse(): List<List<Point>> =
  splitByEmptyString().map { lines -> lines.drop(1).toPoints() }

fun List<String>.toPoints(): List<Point> = map(String::toPoint)

fun String.toPoint(): Point =
  split(',').let { (x, y, z) -> Point(x.toInt(), y.toInt(), z.toInt()) }

fun List<String>.splitByEmptyString(): List<List<String>> =
  flatMapIndexed { index, line ->
    when {
      index == 0 || index == this.lastIndex -> listOf(index)
      line.isEmpty() -> listOf(index - 1, index + 1)
      else -> emptyList()
    }
  }.windowed(size = 2, step = 2) { (from, to) -> slice(from..to) }
