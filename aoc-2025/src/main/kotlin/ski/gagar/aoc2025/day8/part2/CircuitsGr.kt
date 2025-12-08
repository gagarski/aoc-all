package ski.gagar.aoc2025.day8.part2

import ski.gagar.aoc.util.graphs.GraphBuilder
import ski.gagar.aoc2025.day8.part1.Junction
import ski.gagar.aoc2025.day8.part1.distance3
import ski.gagar.aoc2025.day8.part1.parseJunctions
import java.math.BigInteger

fun <V> GraphBuilder<V>.isSingleSegment(): Boolean {
    var visited = 0
    bfs(vertices.first()) {
        visited++
    }
    return visited == vertices.size
}

fun GraphBuilder<Junction>.connectTillSingle(): BigInteger {
    val possibleConnections = vertices
        .asSequence()
        .flatMap { first ->
            vertices
                .asSequence()
                .map {
                        second -> first to second
                }
                .filter { (first, second) -> first != second }
        }
        .sortedBy { it.distance3() }

    for (conn in possibleConnections) {
        addEdge(conn.first, conn.second)
        addEdge(conn.second, conn.first)

        if (isSingleSegment()) {
            return conn.first.x * conn.second.x
        }
    }
    return -BigInteger.ONE
}

fun connectTillSingleGraph(lines: Sequence<String>): BigInteger =
    parseJunctions(lines)
        .connectTillSingle()