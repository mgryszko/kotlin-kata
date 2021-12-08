package aoc.day8

import aoc.readLines

fun main() {
  val sampleWires = listOf("acedgfb", "cdfbe", "gcdfa", "fbcad", "dab", "cefabd", "cdfgeb", "eafb", "cagedb", "ab").map(String::toWires)
  val sampleDigits = listOf("cdfeb", "fcadb", "cdfeb", "cdbaf").map(String::toWires)
  val sampleDecodedDigits = decode(sampleWires, sampleDigits)
  println(sampleDecodedDigits)
  assert(sampleDecodedDigits == 5353)

  val lines = "day8.txt".readLines().map { it.split(" | ").let { (wires, digits) ->
    wires.split(' ').map(String::toWires) to digits.split(' ').map(String::toWires) }
  }

  val count = lines.sumOf { line -> line.second.count { it.is1() || it.is4() || it.is7() || it.is8() } }
  println(count)
  assert(count == 381)

  val digits = lines.map { (wires, digits) -> decode(wires, digits) }
  println(digits.sum())
  assert(digits.sum() == 1023686)
}

typealias Wire = Char
typealias Segment = Char

fun String.toWires(): Set<Wire> = toCharArray().toSet()

fun decode(wires: List<Set<Wire>>, digits: List<Set<Wire>>): Int =
  segmentsByWire(wires).let { segmentsByWire -> digits
    .map { digit -> decode(digit, segmentsByWire).toDigit() }
    .joinToString("").toInt()
  }

fun decode(digit: Set<Wire>, segmentsByWires: Map<Wire, Segment>): Set<Segment> =
  digit.map { wire -> segmentsByWires[wire] ?: error("Wire to segment mapping not found for wires: $digit") }.toSet()

fun segmentsByWire(wires: List<Set<Char>>): Map<Wire, Segment> {
  val aSegment = wires.find7() - wires.find1()
  val gSegment = wires.find069().map { it - wires.find4() - wires.find7() }.firstSingleton()
  val eSegment = wires.find069().map { it - wires.find4() - wires.find7() - gSegment }.firstSingleton()
  val bSegment = wires.find069().map { it - wires.find7() - eSegment - gSegment }.firstSingleton()
  val dSegment = wires.find8() - wires.find7() - bSegment - eSegment - gSegment
  val fSegment = wires.find069().map { it - aSegment - bSegment - dSegment - eSegment - gSegment }.firstSingleton()
  val cSegment = wires.find1() - fSegment

  return mapOf(
    aSegment.first() to 'a',
    bSegment.first() to 'b',
    cSegment.first() to 'c',
    dSegment.first() to 'd',
    eSegment.first() to 'e',
    fSegment.first() to 'f',
    gSegment.first() to 'g',
  )
}

fun List<Set<Wire>>.find069(): List<Set<Wire>> = filter { it.size == 6 }
fun List<Set<Wire>>.find1(): Set<Wire> = first(Set<Wire>::is1)
fun List<Set<Wire>>.find4(): Set<Wire> = first(Set<Wire>::is4)
fun List<Set<Wire>>.find7(): Set<Wire> = first(Set<Wire>::is7)
fun List<Set<Wire>>.find8(): Set<Wire> = first(Set<Wire>::is8)

fun Set<Wire>.is1(): Boolean = size == 2
fun Set<Wire>.is4(): Boolean = size == 4
fun Set<Wire>.is7(): Boolean = size == 3
fun Set<Wire>.is8(): Boolean = size == 7

fun List<Set<Wire>>.firstSingleton(): Set<Wire> = first { it.size == 1 }

fun Set<Wire>.toDigit(): Int = when(this) {
  setOf('a', 'b', 'c', 'e', 'f', 'g') -> 0
  setOf('c', 'f') -> 1
  setOf('a', 'c', 'd', 'e', 'g') -> 2
  setOf('a', 'c', 'd', 'f', 'g') -> 3
  setOf('b', 'c', 'd', 'f') -> 4
  setOf('a', 'b', 'd', 'f', 'g') -> 5
  setOf('a', 'b', 'd', 'e', 'f', 'g') -> 6
  setOf('a', 'c', 'f') -> 7
  setOf('a', 'b', 'c', 'd', 'e', 'f', 'g') -> 8
  setOf('a', 'b', 'c', 'd', 'f', 'g') -> 9
  else -> error("Illegal segments: $this")
}
