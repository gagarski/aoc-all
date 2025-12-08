package ski.gagar.aoc2025.day8.part1

import ski.gagar.aoc.util.graphs.Graph
import ski.gagar.aoc.util.graphs.GraphBuilder
import java.math.BigInteger
import java.util.PriorityQueue

private fun BigInteger.square() = this * this

data class Junction(val x: BigInteger, val y: BigInteger, val z: BigInteger) {
    fun distanceToOther3(other: Junction) = (x - other.x).square() + (y - other.y).square() + (z - other.z).square()

    companion object {
        fun parse(string: String): Junction {
            val parts = string.split(",")
            require(parts.size == 3)
            return Junction(parts[0].toBigInteger(), parts[1].toBigInteger(), parts[2].toBigInteger())
        }
    }
}


fun parseJunctions(lines: Sequence<String>): GraphBuilder<Junction> {
    val gb = GraphBuilder<Junction>()
    for (j in lines.map { Junction.parse(it) }) {
        gb.addVertex(j)
    }
    return gb
}

fun Pair<Junction, Junction>.distance3() = first.distanceToOther3(second)

fun GraphBuilder<Junction>.connectClosest(n: Int): GraphBuilder<Junction> = apply {
    val vertices = this.vertices.toList()
    val closest = PriorityQueue(
        Comparator.comparing<Pair<Junction, Junction>, BigInteger> { it.distance3() }.reversed()
    )
    for (i in vertices.indices) {
        for (j in (i + 1) ..< vertices.size) {
            val pair = Pair(vertices[i], vertices[j])
            when {
                closest.size < n -> {
                    closest.add(pair)
                }
                pair.distance3() < closest.peek().distance3() -> {
                    closest.remove()
                    closest.add(pair)
                }
            }
        }
    }

    for (pair in closest) {
        addEdge(pair.first, pair.second)
        addEdge(pair.second, pair.first)
    }
}

private fun <V> Graph<V>.biggestSegmentsSize(n: Int = 3) =
    segments()
        .sortedBy { it.size }
        .reversed()
        .map { it.size }
        .take(n)
        .reduce { a, b -> a * b }


fun connectJunctions(lines: Sequence<String>, connectClosest: Int = 1000, pickSegments: Int = 3): Int =
    parseJunctions(lines)
        .connectClosest(connectClosest)
        .build()
        .biggestSegmentsSize(pickSegments)