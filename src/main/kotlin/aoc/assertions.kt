package aoc

fun <T> assert(actual: T, expected: T): Unit =
  check(actual == expected) { "Expected $expected, got $actual" }