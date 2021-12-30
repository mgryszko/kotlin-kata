package aoc.day24

import aoc.readText
import java.math.BigInteger
import java.math.BigInteger.ZERO

fun main() {
  val monadProgram = Parser.toInstructions("day24.txt".readText())

  val initialInputs = factors.reversed().fold(listOf(Input(0, listOf()))) { inputs, (xAddend, yAddend, zDivisor)  ->
    backtrack(inputs, xAddend, yAddend, zDivisor)
  }
  val serials = initialInputs.filter { (z, _) -> z == 0 }
    .map { (_, digits) -> digits.reversed().joinToString("") }
    .sorted()
  val largestSerial = serials.last()
  println(largestSerial)
  println(Interpreter.execute(monadProgram, initialBindings, Parser.toSerial(largestSerial)))
  aoc.assert(largestSerial, "91699394894995")
  
  val smallestSerial = serials.first()
  println(smallestSerial)
  println(Interpreter.execute(monadProgram, initialBindings, Parser.toSerial(smallestSerial)))
  aoc.assert(smallestSerial, "51147191161261")
}

data class Input(val expectedZ: Int, val digits: List<Int>)

fun backtrack(inputs: List<Input>, xAddend: Int, yAddend: Int, zDivisor: Int): List<Input> =
  mutableListOf<Input>().also { nextInputs ->
    inputs.forEach { (expectedZ, digits) ->
      val inputBacktrack = if (zDivisor == 26) ::inputZDiv26 else ::inputZDiv1
      val backtrackedInputs = inputBacktrack(expectedZ, xAddend, yAddend).map { (w, z) ->
        check(z(w, z, xAddend, yAddend, zDivisor) == expectedZ) { "Cannot compute z=$expectedZ given: w=$w, z=$z, xAddend=$xAddend, yAddend=$yAddend, zDivisor=$zDivisor" }
        Input(z, digits + w)
      }
      nextInputs += backtrackedInputs
    }
  }

fun inputZDiv26(expectedZ: Int, xAddend: Int, yAddend: Int): List<Pair<Int, Int>> {
  val inputs = mutableListOf<Pair<Int, Int>>()
  for (w in 1..9) {
    // assume that x = 0
    inputs += w to expectedZ * 26 + (w - xAddend)

    // assume that x = 1
    if ((expectedZ - w - yAddend) % 26 == 0) {
      val quot = (expectedZ - w - yAddend) / 26
      val rems = (if (quot >= 0) 0..25 else -25..0)
        .filter { it != w - xAddend }
      val zs = rems.map { quot * 26 + it }
      zs.forEach { inputs += w to it }
    }
  }
  return inputs
}

fun inputZDiv1(expectedZ: Int, xAddend: Int, yAddend: Int): List<Pair<Int, Int>> {
  val inputs = mutableListOf<Pair<Int, Int>>()
  for (w in 1..9) {
    // assume that x = 0
    expectedZ.let { z ->
      if (z % 26 == w - xAddend) inputs += w to z
    }

    // assume that x = 1
    if ((expectedZ - w - yAddend) % 26 == 0) {
      ((expectedZ - w - yAddend) / 26).let { z ->
        if (z % 26 != w - xAddend) inputs += w to z
      }
    }
  }
  return inputs
}

fun z(w: Int, z: Int, xAddend: Int, yAddend: Int, zDivisor: Int): Int {
  val x = if (z % 26 + xAddend != w) 1 else 0
  return (z / zDivisor) * (25 * x + 1) + (w + yAddend) * x
}

sealed interface Instruction
data class Inp(val a: Var) : Instruction {
  override fun toString(): String = "$a <-"
}

data class Add(val a: Var, val b: Symbol) : Instruction {
  override fun toString(): String = "$a + $b"
}

data class Mul(val a: Var, val b: Symbol) : Instruction {
  override fun toString(): String = "$a * $b"
}

data class Div(val a: Var, val b: Symbol) : Instruction {
  override fun toString(): String = "$a / $b"
}

data class Mod(val a: Var, val b: Symbol) : Instruction {
  override fun toString(): String = "$a % $b"
}

data class Eql(val a: Var, val b: Symbol) : Instruction {
  override fun toString(): String = "$a == $b"
}

sealed interface Symbol

typealias Value = BigInteger

@JvmInline
value class Literal(val value: Value) : Symbol {
  override fun toString(): String = value.toString()
}

@JvmInline
value class Var(val name: String) : Symbol {
  override fun toString(): String = name
}

typealias Bindings = Map<Var, Value>

object Interpreter {
  fun execute(program: List<Instruction>, bindings: Bindings, inputs: List<Int>): Bindings =
    program.fold(bindings to inputs) { (b, i), instr ->
      execute(instr, b, i.firstOrNull()) to if (instr is Inp) i.drop(1) else i
    }.first

  private fun execute(instr: Instruction, bindings: Bindings, input: Int?): Bindings =
    bindings + when (instr) {
      is Inp -> instr.a to (input?.toBigInteger() ?: error("Input required for ${instr.a}"))
      is Add -> instr.a to instr.add(bindings)
      is Mul -> instr.a to instr.mul(bindings)
      is Div -> instr.a to instr.div(bindings)
      is Mod -> instr.a to instr.mod(bindings)
      is Eql -> instr.a to instr.eql(bindings)
    }

  private fun Add.add(bindings: Bindings): Value =
    a.value(bindings) + b.value(bindings)

  private fun Mul.mul(bindings: Bindings): Value =
    a.value(bindings) * b.value(bindings)

  private fun Div.div(bindings: Bindings): Value =
    a.value(bindings) / b.value(bindings)

  private fun Mod.mod(bindings: Bindings): Value =
    a.value(bindings) % b.value(bindings)

  private fun Eql.eql(bindings: Bindings): Value =
    if (a.value(bindings) == b.value(bindings)) BigInteger.ONE else ZERO

  private fun Symbol.value(bindings: Bindings): Value = when (this) {
    is Var -> bindings[this] ?: error("Variable $this not bound to any value: $bindings")
    is Literal -> value
  }
}

object Parser {
  fun toSerial(str: String): List<Int> = str.toCharArray().map(Char::digitToInt)

  fun toInstructions(program: String): List<Instruction> =
    program.lines().map { it.toInstruction() }

  private fun String.toInstruction(): Instruction =
    split(' ').let { tokens ->
      when (tokens[0]) {
        "inp" -> Inp(tokens[1].toVar())
        "add" -> Add(tokens[1].toVar(), tokens[2].toSymbol())
        "mul" -> Mul(tokens[1].toVar(), tokens[2].toSymbol())
        "div" -> Div(tokens[1].toVar(), tokens[2].toSymbol())
        "mod" -> Mod(tokens[1].toVar(), tokens[2].toSymbol())
        "eql" -> Eql(tokens[1].toVar(), tokens[2].toSymbol())
        else -> error("Instruction $this not supported")
      }
    }

  private fun String.toVar(): Var = Var(this)

  private fun String.toSymbol(): Symbol =
    runCatching(String::toBigInteger).map(::Literal).getOrElse { Var(this) }
}

val factors = listOf(
  Triple(13, 3, 1),
  Triple(11, 12, 1),
  Triple(15, 9, 1),
  Triple(-6, 12, 26),
  Triple(15, 2, 1),
  Triple(-8, 1, 26),
  Triple(-4, 1, 26),
  Triple(15, 13, 1),
  Triple(10, 1, 1),
  Triple(11, 6, 1),
  Triple(-11, 2, 26),
  Triple(0, 11, 26),
  Triple(-8, 10, 26),
  Triple(-7, 3, 26),
)

val initialBindings = mapOf(Var("w") to ZERO, Var("x") to ZERO, Var("y") to ZERO, Var("z") to ZERO)

val threeTimes = """
inp z
inp x
mul z 3
eql z x 
""".trimIndent()

val toBinary = """
inp w
add z w
mod z 2
div w 2
add y w
mod y 2
div w 2
add x w
mod x 2
div w 2
mod w 2 
""".trimIndent()