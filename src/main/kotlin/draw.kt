package aoc

fun <E> Iterable<Iterable<E>>.draw(separator: CharSequence = ""): String = joinToString("\n") { it.joinToString(separator) }