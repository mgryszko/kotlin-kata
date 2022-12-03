package crdt

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LWWElementSetSpec : FunSpec({
  isolationMode = IsolationMode.InstancePerLeaf

  context("add and remove") {
    val set = LWWElementSet<String, Int>()

    test("empty set") {
      set.contains("a") shouldBe false
    }

    test("add") {
      set.apply {
        add("a", 1)
        contains("a") shouldBe true
      }
    }

    test("add, remove") {
      set.apply {
        add("a", 1)
        remove("a", 2)
        contains("a") shouldBe false
      }
    }

    test("remove, add") {
      set.apply {
        remove("a", 1)
        add("a", 2)
        contains("a") shouldBe true
      }
    }

    test("add, remove, add") {
      set.apply {
        add("a", 1)
        remove("a", 2)
        add("a", 3)
        contains("a") shouldBe true
      }
    }

    test("add, remove, remove, add") {
      set.apply {
        add("a", 1)
        remove("a", 2)
        remove("a", 4)
        add("a", 2)
        contains("a") shouldBe false
      }
    }
  }

  context("merge") {
    test("only added elements") {
      val set1 = LWWElementSet<String, Int>().apply {
        add("a", 1)
        add("b", 2)
      }
      val set2 = LWWElementSet<String, Int>().apply {
        add("c", 2)
        add("a", 2)
      }
      set1.merge(set2).apply {
        contains("a") shouldBe true
        contains("b") shouldBe true
        contains("c") shouldBe true
      }
    }

    // 1 has added element, later removed. 2 adds it later
    // properties of merge
  }
})
