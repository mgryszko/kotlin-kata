package aoc.day12

import aoc.readLines

fun main() {
  val lines = "day12.txt".readLines()
  val graph = undirectedGraph(lines)

  val smallCavesVisitedOnce = distinctPaths(graph, Path::alreadyVisitedSmallCaves)
  println(smallCavesVisitedOnce.size)
  assert(smallCavesVisitedOnce.size == 4011)

  val oneSmallCaveCanBeVisitedTwice = distinctPaths(graph, Path::alreadyVisitedSmallCavesAllOnceButSingleTwice)
  println(oneSmallCaveCanBeVisitedTwice.size)
  assert(oneSmallCaveCanBeVisitedTwice.size == 108035)
}

fun undirectedGraph(lines: List<String>): Graph =
  mutableMapOf<Node, Set<Node>>().also { graph ->
    lines.map {
      it.split('-').let { (start, end) ->
        graph.compute(Node.of(start)) { _, v -> (v ?: emptySet()) + Node.of(end) }
        graph.compute(Node.of(end)) { _, v -> (v ?: emptySet()) + Node.of(start) }
      }
    }
  }

fun distinctPaths(graph: Graph, visitedNodes: (Path) -> Set<Node>): Set<Path> {
  val paths = mutableSetOf<Path>()
  val pathsToExplore = ArrayDeque(listOf(Path.initial))
  while (pathsToExplore.isNotEmpty()) {
    val path = pathsToExplore.removeFirst()
    val extensionNodes = extensionNodes(graph[path.last()], visitedNodes(path))
    val (finishedPaths, unfinishedPaths) = extensionNodes.map(path::extend).partition(Path::isFinished)
    paths.addAll(finishedPaths)
    pathsToExplore.addAll(unfinishedPaths)
  }
  return paths
}

fun extensionNodes(adjacentNodes: Set<Node>?, visitedNodes: Set<Node>): Set<Node> =
  (adjacentNodes?.toMutableSet() ?: mutableSetOf()).also { nodes ->
    nodes -= visitedNodes
    nodes -= Start
  }

typealias Graph = Map<Node, Set<Node>>

sealed class Node {
  companion object {
    fun of(node: String): Node =
      when {
        node == "start" -> Start
        node == "end" -> End
        node.all(Char::isLowerCase) -> SmallCave(node)
        else -> LargeCave(node)
      }
  }
}

data class SmallCave(private val node: String): Node() {
  override fun toString(): String = node
}
data class LargeCave(private val node: String): Node() {
  override fun toString(): String = node
}
object Start: Node() {
  override fun toString(): String = "start"
}
object End: Node() {
  override fun toString(): String = "end"
}

data class Path(val nodes: List<Node>) {
  companion object {
    val initial: Path = Path(listOf(Start))
  }

  fun extend(node: Node) = copy(nodes = nodes + node)

  fun last(): Node = nodes.last()

  fun alreadyVisitedSmallCaves(): Set<Node> = smallCaves().toSet()

  fun alreadyVisitedSmallCavesAllOnceButSingleTwice(): Set<Node> {
    val smallCaves = smallCaves()
    val uniqueSmallCaves = smallCaves.toSet()
    return if (uniqueSmallCaves.size == smallCaves.size) emptySet() else uniqueSmallCaves
  }

  fun isFinished(): Boolean = nodes.last() == End

  private fun smallCaves() = nodes.filterIsInstance<SmallCave>()

  override fun toString(): String = nodes.joinToString(",")
}

val sampleGraph = listOf(
  "start-A",
  "start-b",
  "A-c",
  "A-b",
  "b-d",
  "A-end",
  "b-end",
)
