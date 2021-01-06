import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test

class SmokeTest {
    @Test
    fun read() {
        expect(true).toBe(false)
    }
}
