package aoc.day23

import aoc.assert
import java.util.*
import kotlin.math.absoluteValue

fun main() {
  val winningMove = solve(input)
  println(winningMove.energy)
  assert(winningMove.energy, 16506)

  val extendedWinningMove = solve(extendedInput)
  println(extendedWinningMove.energy)
  assert(extendedWinningMove.energy, 48304)
}

fun solve(initial: Burrow): Move {
  val nextMoves = PriorityQueue<Move>()
  val performedMoves = mutableMapOf<Burrow, Int>()
  nextMoves.add(Move(burrow = initial, energy = 0))
  val winningMoves = TreeSet<Move>()

  while (nextMoves.isNotEmpty()) {
    val move = nextMoves.poll()

    move.burrow.amphipodsAbleToExitSideRooms().forEach { (amphipod, amphipodType) ->
      move.burrow.freeHallwaySpacesReachableFrom(amphipodType).forEach { hallwaySpace ->
        val next = move.burrow.moveToHallway(move, amphipod, amphipodType, hallwaySpace)
        if (performedMoves.getOrDefault(next.burrow, Int.MAX_VALUE) > next.energy) {
          performedMoves += next.burrow to next.energy
          nextMoves.add(next)
        }
      }
    }

    move.burrow.amphipodsAbleToEnterTheirSideRoom().forEach { hallwaySpace ->
      val next = move.burrow.moveToSideRoom(move, hallwaySpace)
      when {
        next.burrow.isWinning() -> winningMoves.add(next)
        performedMoves.getOrDefault(next.burrow, Int.MAX_VALUE) > next.energy -> {
          performedMoves += next.burrow to next.energy
          nextMoves.add(next)
        }
      }
    }
  }

  return winningMoves.first()
}

typealias HallwaySpace = Int

data class Burrow(val hallway: List<Amphipod?> = List(11) { null }, val sideRooms: List<SideRoom>) {
  init {
    require(hallway.size == 11) { "Hallway size must be 11" }
    require(sideRooms.size == 4) { "There must be exactly 4 side rooms" }
  }

  fun amphipodsAbleToExitSideRooms(): List<Pair<Amphipod, Amphipod.Type>> =
    sideRooms.mapNotNull { sideRoom ->
      val amphipod = sideRoom.firstNotNullOrNull()
      if (amphipod != null && !amphipod.wasMovedToHallway) amphipod to sideRoom.home
      else null
    }

  fun amphipodsAbleToEnterTheirSideRoom(): List<HallwaySpace> =
    hallway.mapIndexedNotNull { hallwaySpace, amphipod -> if (amphipod != null) (hallwaySpace to amphipod) else null }
      .filter { (hallwaySpace, amphipod) -> hallwayBetween(hallwaySpace, amphipod.type.opposingHallwaySpace).all { it == null } }
      .filter { (_, amphipod) -> sideRooms[amphipod.type.ordinal].hasSpace() }
      .map(Pair<HallwaySpace, Amphipod>::first)

  private fun hallwayBetween(hallwaySpace: HallwaySpace, to: HallwaySpace): List<Amphipod?> =
    if (hallwaySpace < to) hallway.subList(hallwaySpace + 1, to)
    else hallway.subList(to, hallwaySpace)

  fun freeHallwaySpacesReachableFrom(amphipodType: Amphipod.Type): List<HallwaySpace> =
    hallwaySpacesToLeftOf(amphipodType).takeWhile { hallway[it] == null } +
      hallwaySpacesToRightOf(amphipodType).takeWhile { hallway[it] == null }

  private fun hallwaySpacesToLeftOf(amphipodType: Amphipod.Type): List<HallwaySpace> =
    (amphipodType.opposingHallwaySpace downTo 0).filterNot { it.isOpposingSideRoom() }

  private fun hallwaySpacesToRightOf(amphipodType: Amphipod.Type): List<HallwaySpace> =
    (amphipodType.opposingHallwaySpace until hallway.size).filterNot { it.isOpposingSideRoom() }

  private fun HallwaySpace.isOpposingSideRoom() =
    this == Amphipod.Type.A.opposingHallwaySpace
      || this == Amphipod.Type.B.opposingHallwaySpace
      || this == Amphipod.Type.C.opposingHallwaySpace
      || this == Amphipod.Type.D.opposingHallwaySpace

  fun moveToHallway(move: Move, amphipod: Amphipod, amphipodType: Amphipod.Type, hallwaySpace: HallwaySpace): Move {
    return Move(
      burrow = Burrow(
        hallway = hallway.toMutableList().also { it[hallwaySpace] = amphipod.copy(wasMovedToHallway = true) },
        sideRooms = sideRooms.map { sideRoom -> if (sideRoom.home == amphipodType) sideRoom - amphipod else sideRoom }
      ),
      energy = move.energy
        + sideRooms[amphipodType.ordinal].energyOfMovingToHallway(amphipod)
        + energyOfMovingInHallway(amphipod, amphipodType, hallwaySpace)
    )
  }

  fun moveToSideRoom(move: Move, hallwaySpace: HallwaySpace): Move {
    val amphipod = hallway[hallwaySpace]!!
    return Move(
      burrow = Burrow(
        hallway = hallway.toMutableList().also { it[hallwaySpace] = null },
        sideRooms = sideRooms.map { sideRoom ->  if (sideRoom.home == amphipod.type) sideRoom + amphipod else sideRoom }
      ),
      energy = move.energy
        + energyOfMovingInHallway(amphipod, amphipod.type, hallwaySpace)
        + sideRooms[amphipod.type.ordinal].energyOfMovingFromHallway(amphipod)
    )
  }

  private fun energyOfMovingInHallway(amphipod: Amphipod, amphipodType: Amphipod.Type, hallwaySpace: HallwaySpace): Int =
    (amphipodType.opposingHallwaySpace - hallwaySpace).absoluteValue * amphipod.type.energy

  fun isWinning(): Boolean = sideRooms.all(SideRoom::allAmphipodsHome)

  fun draw(): String = """
    |#############
    |#${hallway.joinToString("") { it?.type?.name ?: "." }}#
    |###${sideRooms.joinToString("#") { it.amphipods[0]?.toString() ?: "." }}###
    |  #${sideRooms.joinToString("#") { it.amphipods[1]?.toString() ?: "." }}#
    |  #########""".trimMargin()
}

data class Amphipod(val type: Type, val wasMovedToHallway: Boolean = false) {
  enum class Type(val energy: Int, val opposingHallwaySpace: HallwaySpace) {
    A(1, 2), B(10, 4), C(100, 6), D(1000, 8);
  }

  override fun toString(): String = if (wasMovedToHallway) type.name.lowercase() else type.name
}

data class SideRoom(val amphipods: List<Amphipod?>, val home: Amphipod.Type) {
  fun firstNotNullOrNull(): Amphipod? = amphipods.firstNotNullOfOrNull { it }

  fun hasSpace(): Boolean = amphipods.any { it == null }

  operator fun minus(amphipod: Amphipod): SideRoom {
    val i = amphipods.indexOf(amphipod)
    check(i != -1) { "No amphipod $amphipod found in the side room $this" }
    val updatedAmphipods = amphipods.toMutableList()
    updatedAmphipods[i] = null
    return copy(amphipods = updatedAmphipods)
  }

  operator fun plus(amphipod: Amphipod): SideRoom {
    val i = amphipods.indexOfLast { it == null }
    check(i != -1) { "Amphipod $amphipod cannot enter the side room $this because full" }
    val updatedAmphipods = amphipods.toMutableList()
    updatedAmphipods[i] = amphipod
    return copy(amphipods = updatedAmphipods)
  }

  fun energyOfMovingToHallway(amphipod: Amphipod): Int {
    val i = amphipods.indexOf(amphipod)
    check(i != -1) { "Amphipod $amphipod not found in the side room $this" }
    return (i + 1) * amphipod.type.energy
  }

  fun energyOfMovingFromHallway(amphipod: Amphipod): Int {
    val i = amphipods.indexOfLast { it == null }
    check(i != -1) { "Amphipod $amphipod cannot enter the side room $this because full" }
    return (i + 1) * amphipod.type.energy
  }

  fun allAmphipodsHome(): Boolean = amphipods.all { it?.type == home }
}

data class Move(val burrow: Burrow, val energy: Int) : Comparable<Move> {
  override fun compareTo(other: Move): Int = energy.compareTo(other.energy)

  fun draw(): String {
    val burrowLines = burrow.draw().lines()
    return "${burrowLines.first()} @ $energy\n" + burrowLines.drop(1).joinToString("\n")
  }
}

val sample = Burrow(
  sideRooms = listOf(
    SideRoom(listOf(Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.A)), Amphipod.Type.A),
    SideRoom(listOf(Amphipod(Amphipod.Type.C), Amphipod(Amphipod.Type.D)), Amphipod.Type.B),
    SideRoom(listOf(Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.C)), Amphipod.Type.C),
    SideRoom(listOf(Amphipod(Amphipod.Type.D), Amphipod(Amphipod.Type.A)), Amphipod.Type.D),
  )
)

val extendedSample = Burrow(
  sideRooms = listOf(
    SideRoom(
      listOf(Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.D), Amphipod(Amphipod.Type.D), Amphipod(Amphipod.Type.A)),
      Amphipod.Type.A
    ),
    SideRoom(
      listOf(Amphipod(Amphipod.Type.C), Amphipod(Amphipod.Type.C), Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.D)),
      Amphipod.Type.B
    ),
    SideRoom(
      listOf(Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.A), Amphipod(Amphipod.Type.C)),
      Amphipod.Type.C
    ),
    SideRoom(
      listOf(Amphipod(Amphipod.Type.D), Amphipod(Amphipod.Type.A), Amphipod(Amphipod.Type.C), Amphipod(Amphipod.Type.A)),
      Amphipod.Type.D
    ),
  )
)

val input = Burrow(
  sideRooms = listOf(
    SideRoom(listOf(Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.D)), Amphipod.Type.A),
    SideRoom(listOf(Amphipod(Amphipod.Type.A), Amphipod(Amphipod.Type.A)), Amphipod.Type.B),
    SideRoom(listOf(Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.D)), Amphipod.Type.C),
    SideRoom(listOf(Amphipod(Amphipod.Type.C), Amphipod(Amphipod.Type.C)), Amphipod.Type.D),
  )
)

val extendedInput = Burrow(
  sideRooms = listOf(
    SideRoom(
      listOf(Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.D), Amphipod(Amphipod.Type.D), Amphipod(Amphipod.Type.D)),
      Amphipod.Type.A
    ),
    SideRoom(
      listOf(Amphipod(Amphipod.Type.A), Amphipod(Amphipod.Type.C), Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.A)),
      Amphipod.Type.B
    ),
    SideRoom(
      listOf(Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.B), Amphipod(Amphipod.Type.A), Amphipod(Amphipod.Type.D)),
      Amphipod.Type.C
    ),
    SideRoom(
      listOf(Amphipod(Amphipod.Type.C), Amphipod(Amphipod.Type.A), Amphipod(Amphipod.Type.C), Amphipod(Amphipod.Type.C)),
      Amphipod.Type.D
    ),
  )
)
