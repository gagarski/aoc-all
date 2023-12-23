package ski.gagar.aoc2015.day9.part1

import ski.gagar.aoc.util.StringEdge
import ski.gagar.aoc.util.StringGraph
import ski.gagar.aoc.util.StringGraphBuilder


data class NextPath(
    val edge: StringEdge,
    val visited: Set<String>,
    val path: List<StringEdge> = listOf(),
) {
    val length = path.sumOf { it.weight }
}

val List<StringEdge>.pathLength
    get() = sumOf { it.weight }

fun StringGraph.getShortestPathFrom(vertex: String, longestLengthSoFar: Int? = null): List<StringEdge>? {
    var shortestLengthSoFarLocal = longestLengthSoFar
    var shortestPathLocal: List<StringEdge>? = null
    val stack = ArrayDeque<NextPath>()


    for ((_, edge) in getEdgesFrom(vertex)) {
        stack.addLast(NextPath(edge, linkedSetOf(vertex)))
    }

    while (stack.isNotEmpty()) {
        val current = stack.removeLast()
        if (current.edge.to in current.visited) {
            continue
        }

        val newLength = current.length + current.edge.weight

        if (shortestLengthSoFarLocal != null && newLength > shortestLengthSoFarLocal)
            continue

        val newVisited = current.visited + current.edge.to
        val newPath = current.path + current.edge

        if (newVisited.size == vertices.size) {
            if (shortestLengthSoFarLocal == null || newLength < shortestLengthSoFarLocal) {
                shortestLengthSoFarLocal = newLength
                shortestPathLocal = newPath
            }
            continue
        }

        for ((_, edge) in getEdgesFrom(current.edge.to)) {
            stack.addLast(NextPath(edge, newVisited, newPath))
        }
    }

    return shortestPathLocal
}

fun StringGraph.findShortestPath(): List<StringEdge>? {
    var shortestLengthSoFar: Int? = null
    var shortestPathSoFar: List<StringEdge>? = null

    for (vertx in vertices) {
        val path = getShortestPathFrom(vertx, shortestLengthSoFar)
        val newLength = path?.pathLength
        if (newLength != null && (shortestLengthSoFar == null || newLength < shortestLengthSoFar)) {
            shortestLengthSoFar = newLength
            shortestPathSoFar = path
        }
    }
    return shortestPathSoFar
}

private val EDGE_RE = """(?<first>.*?) to (?<second>.*?) = (?<distance>[0-9]+)""".toRegex()

fun StringGraphBuilder.addString(str: String) {
    val match = EDGE_RE.matchEntire(str)
    require(match != null)
    val groups = match.groups as MatchNamedGroupCollection

    val first = groups["first"]!!.value
    val second = groups["second"]!!.value
    val distance = groups["distance"]!!.value.toInt()

    addVertex(first)
    addVertex(second)
    addNonDirectedEdge(first, second, distance)
}

fun buildGraph(strings: Sequence<String>): StringGraph {
    val bld = StringGraphBuilder()
    for (str in strings) {
        bld.addString(str)
    }
    return bld.build()
}

fun shortestPath(strings: Sequence<String>): Int? =
    buildGraph(strings).findShortestPath()?.pathLength

