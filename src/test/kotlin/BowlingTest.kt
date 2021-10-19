import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test

class BowlingTest {
    @Test
    fun `two rolls`() {
        expect(score(0, 0)).toBe(0)
        expect(score(0, 1)).toBe(1)
        expect(score(2, 3)).toBe(5)
        expect(score(4, 5)).toBe(9)
        expect(score(0, 9)).toBe(9)
        expect(score(9, 1)).toBe(10)
        expect(score(0, 10)).toBe(10)
        expect(score(10, 0)).toBe(10)
    }
}

fun score(roll1: Int, roll2: Int): Int = roll1 + roll2