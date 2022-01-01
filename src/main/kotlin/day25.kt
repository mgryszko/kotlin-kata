package aoc.day25

import aoc.assert
import aoc.draw
import aoc.readLines

fun main() {
  val board = "day25.txt".readLines()
  val (eastCucumbers, southCucumbers) = parse(board)

  val turns = generateSequence(Turn(eastCucumbers, southCucumbers)) { (east, south) ->
    val nextEast = moveEastCucumbers(east, south)
    val nextSouth = moveSouthCucumbers(nextEast, south)
    Turn(nextEast, nextSouth, nextEast != east || nextSouth != south)
  }
  val steps = turns.takeWhile { (_, _, moved) -> moved }.toList().size
  println(steps)
  assert(steps, 300)
}

data class Turn(val eastCucumbers: List<Set<Int>>, val southCucumbers: List<Set<Int>>, val moved: Boolean = true)

fun moveEastCucumbers(eastCucumbers: List<Set<Int>>, southCucumbers: List<Set<Int>>): List<Set<Int>> {
  val cols = southCucumbers.size
  return eastCucumbers.mapIndexed { row, cucumbers ->
    cucumbers.map { col ->
      if (col.nextPos(cols) !in cucumbers && row !in southCucumbers[col.nextPos(cols)]) col.nextPos(cols)
      else col
    }.toSet()
  }
}

fun moveSouthCucumbers(eastCucumbers: List<Set<Int>>, southCucumbers: List<Set<Int>>): List<Set<Int>> {
  val rows = eastCucumbers.size
  return southCucumbers.mapIndexed { col, cucumbers ->
    cucumbers.map { row ->
      if (row.nextPos(rows) !in cucumbers && col !in eastCucumbers[row.nextPos(rows)]) row.nextPos(rows)
      else row
    }.toSet()
  }
}

fun Int.nextPos(wrapAt: Int): Int =
  if (this + 1 < wrapAt) this + 1 else 0

fun draw(eastCucumbers: List<Set<Int>>, southCucumbers: List<Set<Int>>) {
  val rows = eastCucumbers.size
  val cols = southCucumbers.size
  val board = MutableList(rows) { MutableList(cols) { '.' } }
  eastCucumbers.forEachIndexed { row, cucumbers ->
    cucumbers.forEach { col -> board[row][col] = '>' }
  }
  southCucumbers.forEachIndexed { col, cucumbers ->
    cucumbers.forEach { row -> board[row][col] = 'v' }
  }
  println(board.draw())
}

fun parse(board: List<String>): Pair<List<Set<Int>>, List<Set<Int>>> {
  val eastCucumbers = List(board.size) { mutableSetOf<Int>() }
  val southCucumbers = List(board.first().length) { mutableSetOf<Int>() }
  board.forEachIndexed { row, cucumbers ->
    cucumbers.forEachIndexed { col, c ->
      when (c) {
        '>' -> eastCucumbers[row] += col
        'v' -> southCucumbers[col] += row
      }
    }
  }
  return eastCucumbers to southCucumbers
}

val sample = """
v...>>.vv>
.vv>>.vv..
>>.>v>...v
>>v>>.>.v.
v>v.vv.v..
>.>>..v...
.vv..>.>v.
v.v..>>v.v
....v..v.>
""".trimIndent()

