import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test

class BowlingTest {
    @Test
    fun `one frame`() {
        expect(score(OpenFrame(0, 0))).toBe(0)
        expect(score(OpenFrame(0, 1))).toBe(1)
        expect(score(OpenFrame(2, 3))).toBe(5)
        expect(score(OpenFrame(4, 5))).toBe(9)
        expect(score(OpenFrame(0, 9))).toBe(9)
        expect(score(OpenFrame(9, 1))).toBe(10)
        expect(score(OpenFrame(0, 10))).toBe(10)
        expect(score(OpenFrame(10, 0))).toBe(10)
    }
}

data class OpenFrame(val roll1: Int, val roll2: Int)

fun score(frame: OpenFrame): Int = frame.roll1 + frame.roll2