package ski.gagar.aoc2015.day9.part2

import ski.gagar.aoc.util.graphs.StringEdge
import ski.gagar.aoc.util.graphs.StringGraph
import ski.gagar.aoc.util.graphs.StringGraphBuilder

data class NextPath(
    val edge: StringEdge,
    val visited: Set<String>,
    val path: List<StringEdge> = listOf(),
) {
    val length = path.sumOf { it.weight }
}

val List<StringEdge>.pathLength
    get() = sumOf { it.weight }

fun StringGraph.getLongestPathFrom(vertex: String, shortestLengthSoFar: Int? = null): List<StringEdge>? {
    var longestLengthSoFarLocal = shortestLengthSoFar
    var longestPathLocal: List<StringEdge>? = null
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

        val newVisited = current.visited + current.edge.to
        val newPath = current.path + current.edge

        if (newVisited.size == vertices.size) {
            if (longestLengthSoFarLocal == null || newLength > longestLengthSoFarLocal) {
                longestLengthSoFarLocal = newLength
                longestPathLocal = newPath
            }
            continue
        }

        for ((_, edge) in getEdgesFrom(current.edge.to)) {
            stack.addLast(NextPath(edge, newVisited, newPath))
        }
    }

    return longestPathLocal
}

fun StringGraph.findLongestPath(): List<StringEdge>? {
    var longestLengthSoFar: Int? = null
    var longestpathSoFar: List<StringEdge>? = null

    for (vertx in vertices) {
        val path = getLongestPathFrom(vertx, longestLengthSoFar)
        val newLength = path?.pathLength
        if (newLength != null && (longestLengthSoFar == null || newLength > longestLengthSoFar)) {
            longestLengthSoFar = newLength
            longestpathSoFar = path
        }
    }
    return longestpathSoFar
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

fun longestPath(strings: Sequence<String>): Int? =
    buildGraph(strings).findLongestPath()?.pathLength?.let { it }
