package crdt


class LWWElementSet<E, T : Comparable<T>> {
  val addSet: MutableSet<Pair<E, T>> = mutableSetOf()
  val removeSet: MutableSet<Pair<E, T>> = mutableSetOf()

  fun add(e: E, t: T) {
    val added = addSet.firstOrNull { it.first == e }
    added?.let { addSet -= it }
    addSet += Pair(e, maxOf(added?.second ?: t, t))
  }

  fun remove(e: E, t: T) {
    if (!contains(e)) {
      return
    }
    val removed = removeSet.firstOrNull { it.first == e }
    removed?.let { removeSet -= it }
    removeSet += Pair(e, maxOf(removed?.second ?: t, t))
  }

  fun contains(e: E): Boolean {
    val added = addSet.firstOrNull { it.first == e }
    val removed = removeSet.firstOrNull { it.first == e }
    return added != null && (removed == null || added.second > removed.second)
  }

  fun merge(other: LWWElementSet<E, T>): LWWElementSet<E, T> {
    val merged = LWWElementSet<E, T>()
    this.addSet.forEach { (e, t) -> merged.add(e, t) }
    other.addSet.forEach { (e, t) -> merged.add(e, t) }
    this.removeSet.forEach { (e, t) -> merged.remove(e, t) }
    other.removeSet.forEach { (e, t) -> merged.remove(e, t) }
    return merged
  }

  override fun toString(): String = "LWWElementSet(addSet=$addSet, removeSet=$removeSet)"
}
