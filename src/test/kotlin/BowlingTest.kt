import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test

class BowlingTest {
    @Test
    fun `two frames`() {
        expect(score(listOf(OpenFrame(0, 0), OpenFrame(0, 1)))).toBe(1)
        expect(score(listOf(OpenFrame(0, 1), OpenFrame(2, 3)))).toBe(6)
        expect(score(listOf(OpenFrame(9, 1), OpenFrame(4, 3)))).toBe(17)
        expect(score(listOf(OpenFrame(10, 0), OpenFrame(4, 3)))).toBe(17)
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
}

sealed interface Frame {
    val roll1: Int
}

data class OpenFrame(override val roll1: Int, val roll2: Int) : Frame

data class Spare(override val roll1: Int, val roll2: Int) : Frame

fun score(frames: List<Frame>): Int = frames.windowed(2, partialWindows = true).map { frames ->
    when (val current = frames[0]) {
        is OpenFrame -> current.roll1 + current.roll2
        is Spare -> current.roll1 + current.roll2 + frames[1].roll1
    }
}.sum()