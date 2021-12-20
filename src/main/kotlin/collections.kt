package aoc

fun <T> Iterable<T>.frequencies(): Map<T, Int> = groupBy({ it }, { 1 }).mapValues { (_, v) -> v.sum() }
