package aoc.day17

import kotlin.math.absoluteValue

fun main() {
//  val targetX = 20 to 30
//  val targetY = -10 to -5
  val targetX = 56 to 76
  val targetY = -162 to -134

  val launches = launches(targetX, targetY)
  val maxVSpeed = launches.maxOf(Pair<Speed, Speed>::second)
  val maxY = arithmeticProgression(maxVSpeed, maxVSpeed, -1)
  println("$maxVSpeed $maxY")
  assert(maxY == 13041)
  println(launches.size)
  assert(launches.size == 1031)
}

typealias Speed = Int
typealias X = Int
typealias Y = Int
typealias Moves = Int

fun launches(targetX: Pair<Int, Int>, targetY: Pair<Int, Int>): Set<Pair<Speed, Speed>> =
  (0..targetX.second).flatMap { hSpeed ->
    horizontalMovementsWithinTargetRange(hSpeed, targetX).takeTargetsReachingVerticalRange(targetY).flatMap { moves ->
      verticalSpeedsWithinTargetRange(moves, targetY).map { vSpeed -> hSpeed to vSpeed }
    }
  }.toSet()

fun horizontalMovementsWithinTargetRange(initialSpeed: Speed, target: Pair<X, X>): Sequence<Moves> =
  when {
    initialSpeed.horizontalDistanceUntilHalted() < target.first -> emptySequence()
    initialSpeed > target.second -> emptySequence()
    else -> generateSequence(initialSpeed to 0) { (speed, x) ->
      when {
        x + speed > target.second -> null
        speed > 0 -> (speed - 1) to (x + speed)
        else -> (0 to x)
      }
    }
      .mapIndexed { i, (_, x) -> (i to x) }
      .filter { (_, x) -> x >= target.first && x <= target.second }
      .map(Pair<Moves, X>::first)
  }

fun Sequence<Moves>.takeTargetsReachingVerticalRange(targetY: Pair<Y, Y>) =
// go up the number of moves corresponding to the max trench depth
// 1 move for the zero velocity
// go down to reach the zero depth
  // 1 move to reach the trench bottom
  take(targetY.first.absoluteValue * 2 + 2)

fun verticalSpeedsWithinTargetRange(moves: Moves, targetY: Pair<Int, Int>): List<Speed> =
  targetY.toRange().mapNotNull { y -> initialVSpeedToReach(y, moves) }

fun initialVSpeedToReach(y: Y, moves: Moves): Speed? = (2 * y + moves * (moves - 1)).divOrNull(2 * moves)

fun Int.divOrNull(divisor: Int): Int? = if (this % divisor == 0) this / divisor else null

fun Speed.horizontalDistanceUntilHalted(): X = arithmeticProgression(this, this, -1)

fun arithmeticProgression(a: Int, n: Int, d: Int): Int = (2 * a + (n - 1) * d) * n / 2

fun Pair<Int, Int>.toRange(): IntRange = first..second

