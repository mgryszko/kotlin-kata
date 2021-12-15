package aoc.day15

import aoc.Board
import aoc.OneDimensionalBoard
import aoc.Pos
import aoc.readLines
import java.util.*


fun main() {
  val lines = "day15.txt".readLines()
  val rows = lines.size
  val cols = lines.first().length
  val cave = OneDimensionalBoard(lines.flatMap { it.toCharArray().map(Char::digitToInt) }, rows, cols)

  val sp = DijkstraSP(cave, Pos(0, 0))
  val spToBottomRight = sp.distTo(Pos(rows - 1, cols - 1))
  println(spToBottomRight)
  assert(spToBottomRight == 626)

  val tiles = 5
  val spLargerCave = DijkstraSP(BoardOfBoards(cave, tiles), Pos(0, 0))
  val spLargerCaveToBottomRight = spLargerCave.distTo(Pos(rows * tiles - 1, cols * tiles - 1))
  println(spLargerCaveToBottomRight)
  assert(spLargerCaveToBottomRight == 2966)
}

typealias Risk = Int

class DijkstraSP(private val riskBoard: Board<Risk>, start: Pos) {
  private val edgeTo = mutableMapOf<Pos, Pos>()
  private val riskTo = mutableMapOf(start to 0)
  private val positionsByRiskAsc = PriorityQueue<Pair<Pos, Risk>> { (_, risk1), (_, risk2) -> risk1.compareTo(risk2) }

  init {
    positionsByRiskAsc += start to 0
    while (!positionsByRiskAsc.isEmpty()) {
      relax(positionsByRiskAsc.poll().first)
    }
  }

  private fun relax(v: Pos) {
    riskBoard.adjacent(v).forEach { w ->
      if (risk(w) > risk(v) + riskBoard[w]) {
        riskTo[w] = risk(v) + riskBoard[w]
        edgeTo[w] = v

        positionsByRiskAsc.find { (pos, _) -> pos == w }?.also { positionsByRiskAsc -= it }
        positionsByRiskAsc += w to risk(w)
      }
    }
  }

  private fun risk(p: Pos) = riskTo[p] ?: Int.MAX_VALUE

  fun distTo(v: Pos): Risk? = riskTo[v]
}

const val MAX_RISK = 9

class BoardOfBoards(private val board: Board<Risk>, private val times: Int): Board<Risk> {
  override val rows: Int
    get() = board.rows * times

  override val cols: Int
    get() = board.cols * times

  override fun get(pos: Pos): Risk {
    val rowInc = pos.row / board.rows
    val colInc = pos.col / board.cols

    val increasedRisk = (board[pos.sourcePos()] + rowInc + colInc) % MAX_RISK
    return if (increasedRisk > 0) increasedRisk else MAX_RISK
  }

  private fun Pos.sourcePos() = copy(row = row % board.rows, col = col % board.cols)
}

val sampleCave = """1163751742
1381373672
2136511328
3694931569
7463417111
1319128137
1359912421
3125421639
1293138521
2311944581"""