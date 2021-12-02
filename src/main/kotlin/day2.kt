package aoc.day2

import aoc.readLines

fun main() {
  val moves = "day2.txt".readLines().map { it.split(' ').let { (dir, steps) -> Move(dir, steps.toInt()) } }

  val positionWithoutAim = movesWithoutAim(moves)
  println(positionWithoutAim)
  println(positionWithoutAim.horizontal * positionWithoutAim.depth)

  val positionWithAim = movesWithAim(moves)
  println(positionWithAim)
  println(positionWithAim.horizontal * positionWithAim.depth)
}

fun movesWithoutAim(moves: List<Move>): Position =
  moves.fold(Position(0, 0, 0)) { position, (dir, steps) ->
    when (dir) {
      "forward" -> position.copy(horizontal = position.horizontal + steps)
      "down" -> position.copy(depth = position.depth + steps)
      "up" -> position.copy(depth = position.depth - steps)
      else -> throw IllegalArgumentException("Direction $dir not supported")
    }
  }

fun movesWithAim(moves: List<Move>): Position =
  moves.fold(Position(0, 0, 0)) { position, (dir, steps) ->
    when (dir) {
      "forward" -> position.copy(horizontal = position.horizontal + steps, depth = position.depth + position.aim * steps)
      "down" -> position.copy(aim = position.aim + steps)
      "up" -> position.copy(aim = position.aim - steps)
      else -> throw IllegalArgumentException("Direction $dir not supported")
    }
  }

data class Move(val direction: String, val positions: Int)

data class Position(val horizontal: Int, val depth: Int, val aim: Int)
