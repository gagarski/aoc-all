package ski.gagar.aoc2015.day9.part2

import ski.gagar.aoc.util.Edge
import ski.gagar.aoc.util.Graph
import ski.gagar.aoc.util.GraphBuilder
import ski.gagar.aoc.util.getResourceAsStream

data class NextPath(
    val edge: Edge,
    val visited: Set<String>,
    val path: List<Edge> = listOf(),
) {
    val length = path.sumOf { it.weight }
}

val List<Edge>.pathLength
    get() = sumOf { it.weight }

fun Graph.getLongestPathFrom(vertex: String, shortestLengthSoFar: Int? = null): List<Edge>? {
    var longestLengthSoFarLocal = shortestLengthSoFar
    var longestPathLocal: List<Edge>? = null
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

fun Graph.findLongestPath(): List<Edge>? {
    var longestLengthSoFar: Int? = null
    var longestpathSoFar: List<Edge>? = null

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

fun GraphBuilder.addString(str: String) {
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

fun buildGraph(strings: Sequence<String>): Graph {
    val bld = GraphBuilder()
    for (str in strings) {
        bld.addString(str)
    }
    return bld.build()
}

fun longestPath(strings: Sequence<String>): Int? =
    buildGraph(strings).findLongestPath()?.pathLength?.let { it }

fun day9Part2() {
    println("day9/part2/escaping: ${
        longestPath(getResourceAsStream("/ski.gagar.aoc.aoc2015.day9/travel.txt").bufferedReader().lineSequence())
    }")
}
