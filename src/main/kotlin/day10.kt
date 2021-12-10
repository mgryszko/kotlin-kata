package aoc.day10

import aoc.readLines
import java.math.BigInteger
import java.math.BigInteger.ZERO

fun main() {
  val lines = "day10.txt".readLines()

  val wrongCharsScore = lines.mapNotNull(::wrongClosingChar).map(Char::wrongScore).sum()
  println(wrongCharsScore)

  val completions = lines.filter { wrongClosingChar(it) == null }.map(::completeChars)
  val completionCharsScore = completions.map(List<Char>::completionScore)
  println(completionCharsScore.sorted()[completionCharsScore.size / 2])
}

val sampleLines = listOf(
  "[({(<(())[]>[[{[]{<()<>>",
  "[(()[<>])]({[<{<<[]>>(",
  "{([(<{}[<>[]}>{[]{[(<()>",
  "(((({<>}<{<{<>}{[]{[]{}",
  "[[<[([]))<([[{}[[()]]]",
  "[{[{({}]{}}([{[{{{}}([]",
  "{<[[]]>}<{[{[{[]{()[[[]",
  "[<(<(<(<{}))><([]([]()",
  "<{([([[(<>()){}]>(<<{{",
  "<{([{{}}[<[[[<>{}]]]>[]]",
)

fun completeChars(line: String): List<Char> {
  val chars = ArrayDeque<Char>()
  for (c in line.toCharArray()) {
    if (c.openingChar()) chars.addFirst(c)
    else chars.removeFirst()
  }
  return chars.map(Char::closingChar)
}

fun wrongClosingChar(line: String): Char? {
  val wrongChars = ArrayDeque<Char>()
  for (c in line.toCharArray()) {
    if (c.openingChar()) {
      wrongChars.addFirst(c)
    } else if (c != wrongChars.removeFirst().closingChar()) {
      return c
    }
  }
  return null
}

fun Char.openingChar(): Boolean =
  this == '(' || this == '[' || this == '{' || this == '<'

fun Char.closingChar(): Char = when (this) {
  '(' -> ')'
  '[' -> ']'
  '{' -> '}'
  '<' -> '>'
  else -> error("Unsupported char: $this")
}

fun Char.wrongScore(): Int = when (this) {
  ')' -> 3
  ']' -> 57
  '}' -> 1197
  '>' -> 25137
  else -> error("Unsupported char: $this")
}

fun List<Char>.completionScore(): BigInteger =
  fold(ZERO) { score, c -> score * 5.toBigInteger() + c.completionScore().toBigInteger() }

fun Char.completionScore(): Int = when (this) {
  ')' -> 1
  ']' -> 2
  '}' -> 3
  '>' -> 4
  else -> error("Unsupported char: $this")
}

