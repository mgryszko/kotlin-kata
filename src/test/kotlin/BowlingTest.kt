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
}

data class OpenFrame(val roll1: Int, val roll2: Int)

fun score(frames: List<OpenFrame>): Int = frames.map { it.roll1 + it.roll2 }.sum()