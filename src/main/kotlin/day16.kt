package aoc.day16

import aoc.readText

fun main() {
  val input = "day16.txt".readText()

  val packet = parsePacket(input.toBits())
  println(packet.sumVersions())
  println(packet.expr())
}

fun Packet.sumVersions(): Int =
  when (this) {
    is Literal -> version
    is Operator -> version + operands.sumOf(Packet::sumVersions)
  }

fun Packet.expr(): Long =
  when (this) {
    is Literal -> value
    is Sum -> operands.sumOf(Packet::expr)
    is Product -> operands.map(Packet::expr).reduce(Long::times)
    is Minimum -> operands.minOf(Packet::expr)
    is Maximum -> operands.maxOf(Packet::expr)
    is GreaterThan -> operands.map(Packet::expr).let { (first, second) -> if (first > second) 1 else 0 }
    is LessThan -> operands.map(Packet::expr).let { (first, second) -> if (first < second) 1 else 0 }
    is Equal -> operands.map(Packet::expr).let { (first, second) -> if (first == second) 1 else 0 }
  }

typealias Index = Int

sealed class Packet(open val version: Int)
data class Literal(override val version: Int, val value: Long) : Packet(version)

sealed class Operator(version: Int, open val operands: List<Packet>) : Packet(version)
data class Sum(override val version: Int, override val operands: List<Packet>) : Operator(version, operands)
data class Product(override val version: Int, override val operands: List<Packet>) : Operator(version, operands)
data class Minimum(override val version: Int, override val operands: List<Packet>) : Operator(version, operands)
data class Maximum(override val version: Int, override val operands: List<Packet>) : Operator(version, operands)
data class GreaterThan(override val version: Int, override val operands: List<Packet>) : Operator(version, operands)
data class LessThan(override val version: Int, override val operands: List<Packet>) : Operator(version, operands)
data class Equal(override val version: Int, override val operands: List<Packet>) : Operator(version, operands)

fun parsePacket(bits: List<String>): Packet = parsePacket(bits, 0).first

fun parsePacket(bits: List<String>, i: Index): Pair<Packet, Index> {
  val packetType = parsePacketType(bits, i)
  return when (packetType) {
    0 -> parseOperator(bits, i, ::Sum)
    1 -> parseOperator(bits, i, ::Product)
    2 -> parseOperator(bits, i, ::Minimum)
    3 -> parseOperator(bits, i, ::Maximum)
    4 -> parseLiteral(bits, i)
    5 -> parseOperator(bits, i, ::GreaterThan)
    6 -> parseOperator(bits, i, ::LessThan)
    7 -> parseOperator(bits, i, ::Equal)
    else -> error("Packet type $packetType not supported")
  }
}

private const val PACKET_TYPE_OFFSET = 3
private const val PAYLOAD_OFFSET = 6

fun parsePacketType(bits: List<String>, i: Index): Int = bits.subList(i + PACKET_TYPE_OFFSET, i + PAYLOAD_OFFSET).bitsToInt()

fun parseVersion(bits: List<String>, i: Index): Int = bits.subList(i, i + PACKET_TYPE_OFFSET).bitsToInt()

fun parseLiteral(bits: List<String>, i: Index): Pair<Packet, Index> {
  val (value, nextI) = parseLiteralValue(bits, i + PAYLOAD_OFFSET)
  return Literal(parseVersion(bits, i), value) to nextI
}

fun parseLiteralValue(bits: List<String>, i: Index): Pair<Long, Index> {
  val digitChunkLength = 5
  val digitBits = mutableListOf<String>()
  var nextI = i
  do {
    digitBits += bits.subList(nextI + 1, nextI + digitChunkLength)
    nextI += digitChunkLength
  } while (bits[nextI - digitChunkLength] == "1")
  return digitBits.bitsToLong() to nextI
}

sealed interface LengthType
data class Length(val length: Int) : LengthType
data class Size(val size: Int) : LengthType

fun <T : Operator> parseOperator(bits: List<String>, i: Index, newOperator: (Int, List<Packet>) -> T): Pair<Operator, Index> {
  val version = parseVersion(bits, i)
  val (lengthType, subpacketsOffset) = parseOperatorSubpacketsBoundary(bits, i)
  val (subpackets, nextI) = when (lengthType) {
    is Length -> parsePacketsUntilLength(bits, subpacketsOffset, lengthType)
    is Size -> parsePacketsUntilSize(bits, subpacketsOffset, lengthType)
  }
  return newOperator(version, subpackets) to nextI
}

fun parsePacketsUntilLength(bits: List<String>, i: Index, until: Length): Pair<List<Packet>, Index> {
  val packets = mutableListOf<Packet>()
  var nextI = i
  while (nextI < i + until.length) {
    parsePacket(bits, nextI).let {
      packets += it.first
      nextI = it.second
    }
  }
  return packets to nextI
}

fun parsePacketsUntilSize(bits: List<String>, i: Index, until: Size): Pair<List<Packet>, Index> {
  val packets = mutableListOf<Packet>()
  var nextI = i
  (0 until until.size).forEach {
    parsePacket(bits, nextI).let {
      packets += it.first
      nextI = it.second
    }
  }

  return packets to nextI
}

fun parseOperatorSubpacketsBoundary(bits: List<String>, i: Index): Pair<LengthType, Index> {
  val lengthTypeOffset = i + PAYLOAD_OFFSET
  val lengthOffset = lengthTypeOffset + 1
  return if (bits[lengthTypeOffset] == "0")
    Length(bits.subList(lengthOffset, lengthOffset + 15).bitsToInt()) to (lengthOffset + 15)
  else
    Size(bits.subList(lengthOffset, lengthOffset + 11).bitsToInt()) to (lengthOffset + 11)
}

fun String.toBits(): List<String> = toList().map { it.digitToInt(16).toString(2).padStart(4, '0') }
  .flatMap { it.toList().map(Char::toString) }

fun List<String>.bitsToInt(): Int = joinToString("").toInt(2)

fun List<String>.bitsToLong(): Long = joinToString("").toLong(2)
