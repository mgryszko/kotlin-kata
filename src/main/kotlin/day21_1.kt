package aoc.day21_1

fun main() {
  val p1Pos = 1
  val p2Pos = 2

  val p1Rolls = rolls().player1Rolls()
  val p2Rolls = rolls().player2Rolls()

  val boardsAfterWin = p1Rolls.game(p1Pos).zip(p2Rolls.game(p2Pos)).first { (p1Board, p2Board) ->
    p1Board.score >= 1000 || p2Board.score >= 1000
  }
  val totalRolls = if (boardsAfterWin.first.score > boardsAfterWin.second.score)
    boardsAfterWin.first.move * 3 + (boardsAfterWin.second.move - 1) * 3
  else
    boardsAfterWin.first.move * 3 + boardsAfterWin.second.move * 3
  val loosingBoard = if (boardsAfterWin.first.score > boardsAfterWin.second.score) boardsAfterWin.second else boardsAfterWin.first
  println("$totalRolls ${loosingBoard.score}")
}

private fun Sequence<Int>.game(startingPos: Int) =
  runningFold(Board(startingPos, 0, 0)) { (pos, score, move), sum ->
    pos.move(sum).let { nextPos -> Board(nextPos, score + nextPos, move + 1) }
  }

data class Board(val pos: Int, val score: Int, val move: Int)

fun rolls(): Sequence<Int> = generateSequence(1, Int::inc).chunked(3).map(List<Int>::sum)

fun Sequence<Int>.player1Rolls() = filterIndexed { i, _ -> i % 2 == 0 }
fun Sequence<Int>.player2Rolls() = filterIndexed { i, _ -> i % 2 == 1 }

fun Int.move(rolls: Int) = ((this + rolls) % 10).let { if (it == 0) 10 else it}


