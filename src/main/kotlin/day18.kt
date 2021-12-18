package aoc.day18

import aoc.readLines

fun main() {
  val sum = input.map(String::parse).reduce { sn1, sn2 ->
    (sn1 + sn2).let { sum ->
      sum.reduce()
      sum
    }
  }
  val magnitude = sum.magnitude()
  println(magnitude)
  assert(magnitude == 3675)

  val combinations = input.flatMap { sn1 -> input.mapNotNull { sn2 -> if (sn1 != sn2) sn1 to sn2 else null } }
    .map { it.first.parse() to it.second.parse() }
  val maxMagnitude = combinations.map {
    (it.first + it.second).let { sum ->
      sum.reduce()
      sum
    }
  }.maxOfOrNull { it.magnitude() }
  println(maxMagnitude)
  assert(maxMagnitude == 4650)
}

val sample1 = listOf(
  "[[[[4,3],4],4],[7,[[8,4],9]]]",
  "[1,1]",
)
val sample2 = listOf(
  "[[[[1,1],[2,2]],[3,3]],[4,4]]",
  "[5,5]",
)
val sample3 = listOf(
  "[[[[1,1],[2,2]],[3,3]],[4,4]]",
  "[5,5]",
  "[6,6]",
)
val sample4 = listOf(
  "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
  "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
  "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
  "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
  "[7,[5,[[3,8],[1,4]]]]",
  "[[2,[2,2]],[8,[8,1]]]",
  "[2,9]",
  "[1,[[[9,3],9],[[9,0],[0,7]]]]",
  "[[[5,[7,4]],7],1]",
  "[[[[4,2],2],6],[8,7]]",
)
val sample5 = listOf(
  "[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
  "[[[5,[2,8]],4],[5,[[9,9],0]]]",
  "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
  "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
  "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
  "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
  "[[[[5,4],[7,7]],8],[[8,3],8]]",
  "[[9,3],[[9,9],[6,[4,9]]]]",
  "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
  "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]] ",
)
val input = "day18.txt".readLines()

typealias Level = Int

sealed class SnailNumber(open var p: SnailPair?) {
  var parent: SnailPair
    get() = p!!
    set(value) { p = value }

  abstract fun magnitude(magnitude: Int = 0): Int
}

data class SnailPair(var l: SnailNumber?, var r: SnailNumber?, override var p: SnailPair?) : SnailNumber(p) {
  var left: SnailNumber
    get() = l!!
    set(value) { l = value }
  var right: SnailNumber
    get() = r!!
    set(value) { r = value }
  private val leftAsPair: SnailPair
    get() = left as SnailPair
  private val rightAsPair: SnailPair
    get() = right as SnailPair
  private val leftAsRegularNumber: SnailRegularNumber
    get() = left as SnailRegularNumber
  private val rightAsRegularNumber: SnailRegularNumber
    get() = right as SnailRegularNumber

  operator fun plus(addend: SnailPair): SnailPair = SnailPair(this, addend, null).also {
    this.parent = it
    addend.parent = it
  }

  fun reduce() {
    while (explode() || split()) {
    }
  }

  private fun explode(): Boolean {
    val postOrder = postOrder()

    // find leftmost pair nested inside four pairs
    val toExplodeIndex = postOrder.indexOfFirst { (n, level) -> n is SnailPair && level >= 4 }
    if (toExplodeIndex == -1) return false
    val toExplode = postOrder[toExplodeIndex].first as SnailPair

    // the pair's right value is added to the first regular number to the right of the exploding pair (if any)
    postOrder.subList(0, toExplodeIndex).onlySnailNumbers().regularNumbers().lastOrNull()?.let { regNumber ->
      regNumber.n += toExplode.leftAsRegularNumber.n
    }
    // the pair's left value is added to the first regular number to the left of the exploding pair (if any)
    postOrder.subList(toExplodeIndex + 1, postOrder.size).onlySnailNumbers().regularNumbers().firstOrNull()?.let { regNumber ->
      regNumber.n += toExplode.rightAsRegularNumber.n
    }

    // replace the exploding pair with 0
    val parent = toExplode.parent
    if (parent.left === toExplode) parent.left = SnailRegularNumber(0, parent)
    if (parent.right === toExplode) parent.right = SnailRegularNumber(0, parent)
    return true
  }

  private fun split(): Boolean {
    val postOrder = postOrder()

    // find numbers greater that 9
    val toSplit = postOrder.onlySnailNumbers().regularNumbers().firstOrNull { it.n > 9 } ?: return false
    val parent = toSplit.parent

    // replace it with pair
    val splitted = SnailPair(null, null, parent)
    // left element of the pair should be the regular number divided by two and rounded down
    splitted.left = SnailRegularNumber(toSplit.n.halfRoundedDown(), splitted)
    // right element of the pair should be the regular number divided by two and rounded up
    splitted.right = SnailRegularNumber(toSplit.n.halfRoundedUp(), splitted)
    if (parent.left === toSplit) parent.left = splitted
    if (parent.right === toSplit) parent.right = splitted
    return true
  }

  private fun postOrder(order: List<Pair<SnailNumber, Level>> = emptyList(), level: Int = 0): List<Pair<SnailNumber, Level>> {
    var nextOrder = order.toMutableList()

    if (bothAreRegularNumbers()) nextOrder.add(this to level)
    else {
      if (leftIsPair()) nextOrder = leftAsPair.postOrder(nextOrder, level + 1).toMutableList()
      else nextOrder.add(left to level)
      if (rightIsPair()) nextOrder = rightAsPair.postOrder(nextOrder, level + 1).toMutableList()
      else nextOrder.add(right to level)
    }

    return nextOrder
  }

  private fun bothAreRegularNumbers(): Boolean = left is SnailRegularNumber && right is SnailRegularNumber

  private fun rightIsPair() = right is SnailPair

  private fun leftIsPair(): Boolean = left is SnailPair

  private fun List<Pair<SnailNumber, Level>>.onlySnailNumbers() = map(Pair<SnailNumber, Level>::first)

  private fun List<SnailNumber>.regularNumbers(): List<SnailRegularNumber> = flatMap {
    when (it) {
      is SnailPair -> listOf(it.leftAsRegularNumber, it.rightAsRegularNumber)
      is SnailRegularNumber -> listOf(it)
    }
  }

  private fun Int.halfRoundedDown() = this / 2

  private fun Int.halfRoundedUp() = if (this % 2 == 0) this / 2 else this / 2 + 1

  override fun magnitude(magnitude: Int): Int =
    magnitude + 3 * left.magnitude(magnitude) + 2 * right.magnitude(magnitude)

  override fun toString(): String = "[$l,$r]"
}

data class SnailRegularNumber(var n: Int, override var p: SnailPair?) : SnailNumber(p) {
  override fun magnitude(magnitude: Int): Int = magnitude + n

  override fun toString(): String = "$n"
}

fun String.parse(): SnailPair {
  val stack = ArrayDeque<SnailNumber>()
  forEach { c ->
    if (c == '[') stack.addLast(SnailPair(null, null, null))
    if (c.isDigit()) stack.addLast(SnailRegularNumber(c.digitToInt(), null))
    if (c == ']') {
      val right = stack.removeLast()
      val left = stack.removeLast()
      val pair = stack.removeLast() as SnailPair
      pair.left = left
      left.parent = pair
      pair.right = right
      right.parent = pair
      stack.addLast(pair)
    }
  }
  return stack.first() as SnailPair
}

