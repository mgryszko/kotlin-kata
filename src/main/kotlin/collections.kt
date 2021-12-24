package aoc

import java.math.BigInteger

fun <T> Iterable<T>.frequencies(): Map<T, Int> = groupBy({ it }, { 1 }).mapValues { (_, v) -> v.sum() }

fun <T> Iterable<T>.combinations(): List<Pair<T, T>> = flatMap { first -> map { second -> first to second } }

fun Iterable<BigInteger>.sum(): BigInteger = fold(BigInteger.ZERO) { sum, element -> sum + element }
