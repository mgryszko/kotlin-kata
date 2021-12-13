package aoc

interface Board<out E> {
  val rows: Int
  val cols: Int

  operator fun get(pos: Pos): E

  fun positions(): Iterable<Pos> =
    (0 until rows).flatMap { r -> (0 until cols).map { c -> Pos(r, c) } }

  fun adjacent(pos: Pos): Iterable<Pos> =
    listOfNotNull(
      pos.up(),
      pos.right(),
      pos.down(),
      pos.left(),
    )

  fun adjacentAndDiagonal(pos: Pos): Iterable<Pos> =
    listOfNotNull(
      pos.up(),
      pos.up()?.right(),
      pos.right(),
      pos.right()?.down(),
      pos.down(),
      pos.down()?.left(),
      pos.left(),
      pos.left()?.up(),
    )

  fun toList(): List<E>

  private fun Pos.up(): Pos? =
    if (row >= 1) copy(row = row - 1) else null

  private fun Pos.down(): Pos? =
    if (row < rows - 1) copy(row = row + 1) else null

  private fun Pos.left(): Pos? =
    if (col < cols - 1) copy(col = col + 1) else null

  private fun Pos.right(): Pos? =
    if (col >= 1) copy(col = col - 1) else null
}

interface MutableBoard<E> : Board<E> {
  operator fun set(neighbour: Pos, value: E)
}

class OneDimensionalBoard<E>(elements: List<E>, override val rows: Int, override val cols: Int) : MutableBoard<E> {
  private val elements: MutableList<E>

  init {
    this.elements = elements.toMutableList()
  }

  override fun get(pos: Pos): E = elements[pos.toIndex()]

  override fun set(neighbour: Pos, value: E) {
    elements[neighbour.toIndex()] = value
  }

  private fun Pos.toIndex() = row * rows + col

  override fun toList(): List<E> = elements

  override fun toString(): String = elements.chunked(rows).draw()
}


