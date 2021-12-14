package aoc.day14

import aoc.readLines
import java.math.BigInteger
import java.math.BigInteger.ZERO

fun main() {
  val lines = "day14.txt".readLines()
  val polymer = Polymer(
    chunks = lines.first().windowed(2).groupingBy(String::toChunk).eachCount().mapValues { (_, f) -> f.toBigInteger() },
    particles = lines.first().groupingBy(Char::toString).eachCount().mapValues { (_, f) -> f.toBigInteger() },
  )
  val rules = lines.drop(2).toInsertionRules()

  val polymers = generateSequence(polymer) { p -> p.extend(rules) }.drop(1)

  val polymerAfter10Extensions = polymers.take(10).last()
  println(polymerAfter10Extensions.maxParticleFrequency - polymerAfter10Extensions.minParticleFrequency)
  assert(polymerAfter10Extensions.maxParticleFrequency - polymerAfter10Extensions.minParticleFrequency == 3555.toBigInteger())

  val polymerAfter40Extensions = polymers.take(40).last()
  println(polymerAfter40Extensions.maxParticleFrequency - polymerAfter40Extensions.minParticleFrequency)
  assert(polymerAfter40Extensions.maxParticleFrequency - polymerAfter40Extensions.minParticleFrequency == 4439442043739.toBigInteger())
}

typealias Particle = String
typealias Frequency = BigInteger

data class Chunk(val first: Particle, val second: Particle) {
  override fun toString(): String = "${first}${second}"
}

data class Polymer(val chunks: Map<Chunk, Frequency>, val particles: Map<Particle, Frequency>) {
  val minParticleFrequency: Frequency
    get() = particles.minByOrNull { (_, f) -> f }?.value ?: ZERO

  val maxParticleFrequency: Frequency
    get() = particles.maxByOrNull { (_, f) -> f }?.value ?: ZERO

  fun extend(rules: Map<Chunk, Particle>): Polymer {
    val nextChunks = mutableMapOf<Chunk, Frequency>()
    val nextParticles = particles.toMutableMap()

    chunks.forEach { (chunk, chunkFreq) ->
      val insertedParticle = rules[chunk] ?: error("Particle for chunk $chunk not found")
      val extendedChunk1 = Chunk(chunk.first, insertedParticle)
      val extendedChunk2 = Chunk(insertedParticle, chunk.second)

      nextChunks.compute(extendedChunk1) { _, freq -> (freq ?: ZERO) + chunkFreq }
      nextChunks.compute(extendedChunk2) { _, freq -> (freq ?: ZERO) + chunkFreq }
      nextParticles.compute(insertedParticle) { _, freq -> (freq ?: ZERO) + chunkFreq }
    }

    return Polymer(nextChunks, nextParticles)
  }
}

fun List<String>.toInsertionRules(): Map<Chunk, Particle> = associate(String::toRule)

fun String.toRule(): Pair<Chunk, String> =
  split(" -> ").let { (from, to) -> from.toChunk() to to }

fun String.toChunk(): Chunk = Chunk(this[0].toString(), this[1].toString())

val sampleLines = """NNCB
  
CH -> B
HH -> N
CB -> H
NH -> C
HB -> C
HC -> B
HN -> C
NN -> C
BH -> H
NC -> B
NB -> B
BN -> B
BB -> N
BC -> B
CC -> N
CN -> C"""