package ski.gagar.aoc2024.day23.part1

import ski.gagar.aoc.util.graphs.Graph
import ski.gagar.aoc.util.graphs.GraphBuilder

fun <V> Graph<V>.interconnectedThrees(predicate: (V) -> Boolean): Set<Set<V>> {
    val result = mutableSetOf<Set<V>>()

    for (v in vertices.asSequence().filter { predicate(it) }) {
        for (edge in edges.values.asSequence()
            .flatMap { it.values }
            .filter { it.from != v && it.to != v }
        ) {
            getEdge(edge.to, edge.from) ?: continue
            getEdge(v, edge.from) ?: continue
            getEdge(edge.from, v) ?: continue
            getEdge(v, edge.to) ?: continue
            getEdge(edge.to, v) ?: continue
            result.add(setOf(v, edge.from, edge.to))
        }
    }
    return result
}

fun buildLan(lines: Sequence<String>): Graph<String> {
    val gb = GraphBuilder<String>()
    for (line in lines) {
        val parts = line.split("-")
        require(parts.size == 2)
        gb.addVertex(parts[0])
        gb.addVertex(parts[1])
        gb.addNonDirectedEdge(parts[0], parts[1])
    }
    return gb.build()
}

fun countInterconnectedThrees(lines: Sequence<String>, predicate: (String) -> Boolean = { it.startsWith("t")} ) =
    buildLan(lines).interconnectedThrees(predicate).size