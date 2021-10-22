import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test

class BowlingTest {
    @Test
    fun `two frames`() {
        expect(score(listOf(OpenFrame(0, 0), OpenFrame(0, 1)))).toBe(1)
        expect(score(listOf(OpenFrame(0, 1), OpenFrame(2, 3)))).toBe(6)
    }

    @Test
    fun `three frames`() {
        expect(score(listOf(OpenFrame(0, 0), OpenFrame(0, 1), OpenFrame(2, 0)))).toBe(3)
        expect(score(listOf(OpenFrame(0, 1), OpenFrame(9, 0), OpenFrame(2, 7)))).toBe(19)
    }

    @Test
    fun `spare`() {
        expect(score(listOf(Spare(9, 1), OpenFrame(4, 3)))).toBe(21)
    }

    @Test
    fun `strike`() {
        expect(score(listOf(Strike, OpenFrame(4, 3)))).toBe(24)
        expect(score(listOf(Strike, Spare(4, 6), OpenFrame(1, 0)))).toBe(32)
    }
}

sealed interface Frame {
    val roll1: Int

    fun pins(): Sequence<Int>
}

data class OpenFrame(override val roll1: Int, val roll2: Int) : Frame {
    init {
        require(roll1 + roll2 <= 9) { "Sum of pins must be less than or equal to 9" }
    }

    override fun pins(): Sequence<Int> = sequenceOf(roll1, roll2)
}

data class Spare(override val roll1: Int, val roll2: Int) : Frame {
    init {
        require(roll1 + roll2 == 10) { "Sum of pins must be equal to 10" }
    }

    override fun pins(): Sequence<Int> = sequenceOf(roll1, roll2)
}

object Strike : Frame {
    override val roll1: Int = 10

    override fun pins(): Sequence<Int> = sequenceOf(roll1)
}

fun score(frames: List<Frame>): Int = frames.mapIndexed { i, frame ->
    when (frame) {
        is OpenFrame -> frame.roll1 + frame.roll2
        is Spare -> frame.roll1 + frame.roll2 + frames.subList(i + 1).pins().take(1).sum()
        is Strike -> frame.roll1 + frames.subList(i + 1).pins().take(2).sum()
    }
}.sum()

fun List<Frame>.pins(): Sequence<Int> = asSequence().flatMap(Frame::pins)

fun <T> List<T>.subList(fromIndex: Int): List<T> =  subList(fromIndex, size)