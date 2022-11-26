import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SmokeTest : FunSpec({
    test("read") {
        true shouldBe false
    }
})
