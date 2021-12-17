package aoc

fun <T> T.println() = println(this)

fun <E> Iterable<Iterable<E>>.draw(separator: CharSequence = ""): String = joinToString("\n") { it.joinToString(separator) }