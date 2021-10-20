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
    }
}

sealed interface Frame {
    val roll1: Int
}

data class OpenFrame(override val roll1: Int, val roll2: Int) : Frame {
    init {
        require(roll1 + roll2 <= 9) { "Sum of pins must be less than or equal to 9" }
    }
}

data class Spare(override val roll1: Int, val roll2: Int) : Frame {
    init {
        require(roll1 + roll2 == 10) { "Sum of pins must be equal to 10" }
    }
}

object Strike : Frame {
    override val roll1: Int = 10
}

fun score(frames: List<Frame>): Int = frames.windowed(2, partialWindows = true).map { frames ->
    when (val current = frames[0]) {
        is OpenFrame -> current.roll1 + current.roll2
        is Spare -> current.roll1 + current.roll2 + frames[1].roll1
        is Strike -> current.roll1  + when(val next = frames[1]) {
            is OpenFrame -> next.roll1 + next.roll2
            else -> TODO()
        }
    }
}.sum()