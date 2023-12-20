package ski.gagar.aoc.util

import java.util.*
import kotlin.Comparator

data class GenericEdge<V>(val from: V, val to: V, val weight: Int)

class GenericGraphBuilder<V> {
    val vertices: MutableSet<V> = mutableSetOf()
    val edges: MutableMap<V, MutableMap<V, GenericEdge<V>>> = mutableMapOf()
    var built: Boolean = false


    fun addVertex(name: V) {
        require(!built)
        vertices.add(name)
    }

    fun addEdge(from: V, to: V, weight: Int) {
        require(!built)
        require(from in vertices)
        require(to in vertices)

        edges[from] = (edges[from] ?: mutableMapOf()).apply {
            this[to] = GenericEdge(from, to, weight)
        }
    }

    fun addNonDirectedEdge(first: V, second: V, weight: Int) {
        require(!built)
        addEdge(first, second, weight)
        addEdge(second, first, weight)
    }

    fun getEdgesFrom(from: V) = edges[from] ?: mapOf()
    fun getEdge(from: V, to: V): GenericEdge<V>? = getEdgesFrom(from)[to]

    fun build(): GenericGraph<V> {
        require(!built)
        return GenericGraph(vertices, edges)
    }
}

data class GenericShortestPaths<V>(
    val from: V,
    val paths: Map<V, List<GenericEdge<V>>>
) {
    fun to(node: V) = paths[node]
}



class GenericGraph<V>(
    val vertices: Set<V>,
    val edges: MutableMap<V, MutableMap<V, GenericEdge<V>>>
) {
    fun getEdgesFrom(from: V) = edges[from] ?: mapOf()
    fun getEdge(from: V, to: V): GenericEdge<V>? = getEdgesFrom(from)[to]

    fun shortestPaths(from: V): GenericShortestPaths<V> {
        val distances = mutableMapOf<V, Int>()
        val paths = mutableMapOf<V, GenericEdge<V>>()
        val visited = mutableSetOf<V>()

        distances[from] = 0

        val queue = PriorityQueue<Pair<V, Int>>(Comparator.comparing { it.second })

        queue.add(from to distances[from]!!)

        while (!queue.isEmpty()) {
            val (current, _) = queue.remove()
            if (current in visited) {
                continue
            }

            visited.add(current)

            val neighbors = edges[current] ?: mapOf()

            for ((toVertex, toEdge) in neighbors) {
                val currentDistToNeighbor = distances[toVertex]
                val newDistToNeighbor = distances[current]!! + toEdge.weight

                if (null == currentDistToNeighbor || newDistToNeighbor < currentDistToNeighbor) {
                    distances[toVertex] = newDistToNeighbor
                    paths[toVertex] = toEdge
                }
                queue.add(toVertex to newDistToNeighbor)
            }
        }

        fun tracePathTo(node: V): List<GenericEdge<V>> {
            val res = mutableListOf<GenericEdge<V>>()
            var current: V = node

            do {
                val edge = paths[current]
                check(edge != null)
                res.add(edge)
                current = edge.from
            } while (current != from)

            return res.reversed()
        }

        return GenericShortestPaths(from, paths.keys.asSequence().map { it to tracePathTo(it) }.toMap())
    }
}
