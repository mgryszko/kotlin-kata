import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test

class BowlingTest {
    @Test
    fun `one roll without the frame`() {
        expect(score(0)).toBe(0)
        expect(score(5)).toBe(5)
        expect(score(10)).toBe(10)
    }
}

fun score(pins: Int): Int = pins