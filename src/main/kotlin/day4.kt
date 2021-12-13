package aoc.day4

import aoc.draw
import aoc.readLines

fun main() {
  val lines = "day4.txt".readLines()

  val numbers = lines.first().split(',').map(String::toInt)
  val boards = toRawBoards(lines.drop(2)).map(::toBoard)

  val (firstNumber, firstBoard) = firstBingo(numbers, boards)
  println("$firstNumber ${firstBoard.remainingNumbers.sum()} ${firstNumber * firstBoard.remainingNumbers.sum()}")

  val (lastNumber, lastBoard) = lastBingo(numbers, boards, emptyList())
  println("$lastNumber ${lastBoard.remainingNumbers.sum()} ${lastNumber * lastBoard.remainingNumbers.sum()}")
}

fun toRawBoards(lines: List<String>): List<List<String>> =
  lines.chunked(BOARD_SIZE + 1).map { it.take(BOARD_SIZE) }

fun toBoard(rawBoard: List<String>) =
  Board(rawBoard.joinToString(" ").trim().split("\\s+".toRegex()).map(String::toInt))

tailrec fun firstBingo(numbers: List<Int>, boards: List<Board>): Pair<Int, Board> {
  val number = numbers.first()
  val (bingoBoards, nonBingoBoards) = boards.mark(number)
  return if (bingoBoards.isNotEmpty()) number to bingoBoards.first() else firstBingo(numbers.drop(1), nonBingoBoards)
}

tailrec fun lastBingo(numbers: List<Int>, boards: List<Board>, prevBingoBoards: List<Board>): Pair<Int, Board> {
  val number = numbers.first()
  val (bingoBoards, nonBingoBoards) = boards.mark(number)
  return if (numbers.isSingle() || nonBingoBoards.isEmpty()) number to (bingoBoards.ifNotEmptyOr(prevBingoBoards)).first()
  else lastBingo(numbers.drop(1), nonBingoBoards, bingoBoards.ifNotEmptyOr(prevBingoBoards))
}

const val BOARD_SIZE = 5

fun List<Board>.mark(number: Int): Pair<List<Board>, List<Board>> =
  map { it.mark(number) }.partition(Board::isBingo)

class Board(private val board: Map<Int, Int>, private val remNumbersInRows: List<Int>, private val remNumbersInCols: List<Int>) {
  val remainingNumbers: Set<Int>
    get() = board.keys

  constructor(numbers: List<Int>) : this(
    numbers.mapIndexed { i, n -> n to i }.toMap(),
    List(BOARD_SIZE) { BOARD_SIZE },
    List(BOARD_SIZE) { BOARD_SIZE },
  )

  fun mark(number: Int): Board = board[number]?.let(::toCoord)?.let { (row, col) ->
      Board(
        board - number,
        remNumbersInRows.toMutableList().apply { set(row, get(row) - 1) },
        remNumbersInCols.toMutableList().apply { set(col, get(col) - 1) }
      )
    } ?: this

  private fun toCoord(i: Int): Pair<Int, Int> = i / BOARD_SIZE to i % BOARD_SIZE

  fun isBingo(): Boolean = remNumbersInRows.any { it == 0 } || remNumbersInCols.any { it == 0 }

  override fun toString(): String = board.keys.chunked(BOARD_SIZE).draw(" ")
}

fun <T> List<T>.isSingle() = size == 1

fun <T> List<T>.ifNotEmptyOr(other: List<T>) = if (isNotEmpty()) this else other
