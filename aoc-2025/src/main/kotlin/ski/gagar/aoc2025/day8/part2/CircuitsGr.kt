package ski.gagar.aoc2025.day8.part2

import ski.gagar.aoc.util.graphs.GraphBuilder
import ski.gagar.aoc2025.day8.part1.Junction
import ski.gagar.aoc2025.day8.part1.distance3
import ski.gagar.aoc2025.day8.part1.parseJunctions
import java.math.BigInteger
import java.util.PriorityQueue
import kotlin.collections.indices

fun <V> GraphBuilder<V>.isSingleSegment(): Boolean {
    var visited = 0
    bfs(vertices.first()) {
        visited++
    }
    return visited == vertices.size
}

fun GraphBuilder<Junction>.connectTillSingle(): BigInteger {
    val vList = vertices.toList()
    val possibleConnections =
        sequence {
            for (i in vList.indices) {
                for (j in (i + 1)..<vList.size) {
                    yield(PossibleConnection(vList[i], vList[j]))
                }
            }
        }.toList().let { list ->
            PriorityQueue(list)
        }

    while (possibleConnections.isNotEmpty()) {
        val conn = possibleConnections.poll()
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