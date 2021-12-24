package aoc.day21_2

import aoc.println
import aoc.sum
import java.math.BigInteger

fun main() {
  val p1Pos = 1
  val p2Pos = 2
  val initialUniverses = mapOf((Board(0, p1Pos) to Board(0, p2Pos)) to BigInteger.ONE)

  val universes = generateSequence(initialUniverses to Turn.Player1) { (universes, turn) -> universes.nextUniverses(turn) }
  universes.drop(1).dropWhile { (u, _) -> u.anyUniverseNotWinning() }.first().first.winningUniverses().println()
}

fun Map<Pair<Board, Board>, BigInteger>.nextUniverses(turn: Turn): Pair<Map<Pair<Board, Board>, BigInteger>, Turn> =
  mutableMapOf<Pair<Board, Board>, BigInteger>().also { nextUniverses ->
    forEach { (boards, count) ->
      val (board1, board2) = boards
      if (board1.wins() || board2.wins()) {
        nextUniverses.merge(boards, count, BigInteger::plus)
      } else {
        rolls().forEach { roll ->
          if (turn == Turn.Player1) {
            val nextBoard = board1.nextTurn(roll)
            nextUniverses.merge(nextBoard to board2, count, BigInteger::plus)
          } else {
            val nextBoard = board2.nextTurn(roll)
            nextUniverses.merge(board1 to nextBoard, count, BigInteger::plus)
          }
        }
      }
    }
  } to turn.next()

fun rolls(): List<Int> =
  (1..3).flatMap { r1 -> (1..3).flatMap { r2 -> (1..3).map { r3 -> r1 + r2 + r3 } } }

fun Map<Pair<Board, Board>, BigInteger>.anyUniverseNotWinning(): Boolean =
  any { (boards, _) -> !(boards.first.wins() || boards.second.wins()) }

fun Map<Pair<Board, Board>, BigInteger>.winningUniverses(): Pair<BigInteger, BigInteger> =
  Pair(
    filterKeys { (board1, _) -> board1.wins() }.values.sum(),
    filterKeys { (_, board2) -> board2.wins() }.values.sum(),
  )

enum class Turn {
  Player1, Player2;

  fun next(): Turn = if (this == Player1) Player2 else Player1
}

data class Board(val score: Int, val pos: Int) {
  fun nextTurn(rolls: Int): Board =
    pos.move(rolls).let { nextPos -> Board(score + nextPos, nextPos) }

  fun wins(): Boolean = score >= 21

  private fun Int.move(rolls: Int) = ((this + rolls) % 10).let { if (it == 0) 10 else it }
}
