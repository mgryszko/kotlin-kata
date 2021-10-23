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
        expect(score(listOf(Spare(9, 1), Spare(2, 8)))).toBe(22)
    }

    @Test
    fun `strike`() {
        expect(score(listOf(Strike, OpenFrame(4, 3)))).toBe(24)
        expect(score(listOf(Strike, Spare(4, 6), OpenFrame(1, 0)))).toBe(32)
        expect(score(listOf(Spare(4, 6), Strike, OpenFrame(1, 0)))).toBe(32)
        expect(score(listOf(Strike, Strike, Strike))).toBe(60)
    }

    @Test
    fun `final frame`() {
        expect(score(listOf(OpenFrame(1, 8), FinalFrame(2, 4)))).toBe(15)
        expect(score(listOf(OpenFrame(1, 8), FinalFrame(7, 3, 10)))).toBe(29)
        expect(score(listOf(OpenFrame(1, 8), FinalFrame(10, 8, 9)))).toBe(36)
        expect(score(listOf(Strike, Strike, Strike, Strike, Strike, Strike, Strike, Strike, Strike, FinalFrame(10, 10, 10)))).toBe(300)
        expect(score(listOf(Spare(1, 9), Spare(2, 8), Spare(3, 7), Spare(4, 6), Spare(5, 5), Spare(6, 4), Spare(7, 3), Spare(8, 2), Spare(9, 1), FinalFrame(0, 10, 10)))).toBe(154)
    }
}

sealed interface Frame {
    fun pins(): Sequence<Int>

    fun ordinaryScore(): Int
}

data class OpenFrame(val roll1: Int, val roll2: Int) : Frame {
    init {
        require(roll1 + roll2 <= 9) { "Sum of pins must be less than or equal to 9" }
    }

    override fun pins(): Sequence<Int> = sequenceOf(roll1, roll2)

    override fun ordinaryScore(): Int = roll1 + roll2
}

data class Spare(val roll1: Int, val roll2: Int) : Frame {
    init {
        require(roll1 + roll2 == 10) { "Sum of pins must be equal to 10" }
    }

    override fun pins(): Sequence<Int> = sequenceOf(roll1, roll2)

    override fun ordinaryScore(): Int = roll1 + roll2
}

object Strike : Frame {
    private const val roll: Int = 10

    override fun pins(): Sequence<Int> = sequenceOf(roll)

    override fun ordinaryScore(): Int = roll
}

data class FinalFrame(val roll1: Int, val roll2: Int, val roll3: Int = 0) : Frame {
    init {
        require(if (roll1 + roll2 <= 9) roll3 == 0 else true) { "Final frame with no strike nor strike cannot have bonus roll" }
    }

    override fun pins(): Sequence<Int> = sequenceOf(roll1, roll2)

    override fun ordinaryScore(): Int = roll1 + roll2 + roll3
}

fun score(frames: List<Frame>): Int = frames.mapIndexed { i, frame ->
    frame.ordinaryScore() + when (frame) {
        is OpenFrame -> 0
        is FinalFrame -> 0
        is Spare -> frames.subList(i + 1).pins().take(1).sum()
        is Strike -> frames.subList(i + 1).pins().take(2).sum()
    }
}.sum()

fun List<Frame>.pins(): Sequence<Int> = asSequence().flatMap(Frame::pins)

fun <T> List<T>.subList(fromIndex: Int): List<T> = subList(fromIndex, size)