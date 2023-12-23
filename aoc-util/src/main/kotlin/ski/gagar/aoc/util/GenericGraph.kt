package ski.gagar.aoc.util

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayDeque

data class GenericEdge<V>(val from: V, val to: V, val weight: Int)

class GenericGraphBuilder<V> {
    val vertices: MutableSet<V> = mutableSetOf()
    val edges: MutableMap<V, MutableMap<V, GenericEdge<V>>> = mutableMapOf()
    var built: Boolean = false


    fun addVertex(name: V) {
        require(!built)
        vertices.add(name)
    }

    fun addEdge(from: V, to: V, weight: Int = 1): GenericEdge<V> {
        require(!built)
        require(from in vertices)
        require(to in vertices)
        val newEdge = GenericEdge(from, to, weight)
        edges[from] = (edges[from] ?: mutableMapOf()).apply {
            this[to] = newEdge
        }
        return newEdge
    }

    fun addNonDirectedEdge(first: V, second: V, weight: Int = 1): Pair<GenericEdge<V>, GenericEdge<V>> {
        require(!built)
        val edge1 = addEdge(first, second, weight)
        val edge2 = addEdge(second, first, weight)
        return edge1 to edge2
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
    val edges: Map<V, Map<V, GenericEdge<V>>>
) {
    private val edgesTo = edges
        .asSequence()
        .flatMap { (_, v) -> v.values }
        .groupBy {
            it.to
        }.mapValues {(k, v) ->
            v.groupBy { it.from }
        }

    fun getEdgesFrom(from: V) = edges[from] ?: mapOf()
    fun getEdge(from: V, to: V): GenericEdge<V>? = getEdgesFrom(from)[to]

    fun getEdgesTo(to: V) = edgesTo[to] ?: mapOf()

    private fun tracePath(
        paths: Map<V, GenericEdge<V>>,
        from: V,
        to: V
    ): List<GenericEdge<V>> {
        val res = mutableListOf<GenericEdge<V>>()
        var current: V = to

        do {
            val edge = paths[current]
            check(edge != null)
            res.add(edge)
            current = edge.from
        } while (current != from)

        return res.reversed()
    }

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

        return GenericShortestPaths(from, paths.keys.asSequence().map { it to tracePath(paths, from, it) }.toMap())
    }

    fun negated() = GenericGraph(vertices, edges.mapValues { (_, edgesBySource) ->
        edgesBySource.mapValues { (_, edge) -> GenericEdge(edge.from, edge.to, -edge.weight) }
    })

    fun getFirstCycle(): Cycle<V>? = getCycles().firstOrNull()

    fun getCycles(): Sequence<Cycle<V>> = sequence {
        val visited = mutableSetOf<V>()

        for (v in vertices) {
            if (v in visited)
                continue
            yieldAll(getCycleFrom(v, visited))

        }
    }

    private data class CycleStackItem<V>(val item: V,
                                         val history: PersistentMap<V, V> = persistentMapOf(),
                                         val historyInv: PersistentMap<V, V> = persistentMapOf())

    private fun getCycleFrom(from: V, globalVisited: MutableSet<V>): Sequence<Cycle<V>> = sequence {
        if (vertices.isEmpty()) return@sequence

        val stack = ArrayDeque<CycleStackItem<V>>()

        stack.addFirst(CycleStackItem(from))

        while (stack.isNotEmpty()) {
            val (v, history, historyRev) = stack.removeFirst()

            if (v in globalVisited)
                continue
            globalVisited.add(v)

            for (to in getEdgesFrom(v).keys) {
                if (to == v) {
                     yield(Cycle(listOf(v)))
                }
                if (to in history) {
                    yield(Cycle(backtrack(to, v, historyRev)))
                }

                stack.addFirst(CycleStackItem(to, history.put(v, to), historyRev.put(to, v)))
            }
        }
    }

    fun isDag() = getFirstCycle() == null

    private fun backtrack(from: V, to: V, history: PersistentMap<V, V>): List<V> {
        val res = mutableListOf<V>()
        var cur = to
        res.add(cur)

        while (cur != from) {
            cur = history[cur] ?: error("Cannot backtrack")
            res.add(cur)
        }

        return res.reversed()
    }

    private fun dfsOrders(from: V, visited: MutableSet<V>): DfsOrders<V> {
        val stack = ArrayDeque<OrdersStackItem<V>>()
        val preOrder = mutableListOf<V>()
        val postOrder = mutableListOf<V>()
        stack.add(ProcessVertex(from))
        while (stack.isNotEmpty()) {
            val stackItem = stack.removeFirst()
            when (stackItem) {
                is PushToPostOrder<V> -> postOrder.add(stackItem.vertex)
                is ProcessVertex<V> -> {
                    val v = stackItem.vertex
                    if (v in visited)
                        continue
                    preOrder.add(v)
                    if (v in visited)
                        continue
                    visited.add(v)

                    stack.addFirst(PushToPostOrder(v))
                    for (to in getEdgesFrom(v).keys) {
                        stack.addFirst(ProcessVertex(to))
                    }
                }
            }


        }

        return DfsOrders(preOrder, postOrder)
    }

    private sealed interface OrdersStackItem<V>
    data class ProcessVertex<V>(val vertex: V) : OrdersStackItem<V>
    data class PushToPostOrder<V>(val vertex: V) : OrdersStackItem<V>

    private fun dfsOrders(): DfsOrders<V> {
        val visited = mutableSetOf<V>()

        var orders = DfsOrders<V>(listOf(), listOf())

        for (v in vertices) {
            if (v in visited)
                continue
            orders += dfsOrders(v, visited)

        }
        return orders
    }

    fun topoSort(): List<V> {
        if (!isDag()) throw IllegalArgumentException("This graph is not DAG")

        val orders = dfsOrders()

        return orders.post.reversed()
    }

    fun topoSortShortestPaths(from: V): GenericShortestPaths<V> {
        val sorted = topoSort()

        val distances = mutableMapOf<V, Int>()
        val paths = mutableMapOf<V, GenericEdge<V>>()

        distances[from] = 0


        for (v in sorted) {
            val neighbors = edges[v] ?: mapOf()

            for ((toVertex, toEdge) in neighbors) {
                val currentDistToNeighbor = distances[toVertex]
                val newDistToNeighbor = distances[v]!! + toEdge.weight

                if (null == currentDistToNeighbor || newDistToNeighbor < currentDistToNeighbor) {
                    distances[toVertex] = newDistToNeighbor
                    paths[toVertex] = toEdge
                }
            }
        }

        return GenericShortestPaths(from, paths.keys.asSequence().map { it to tracePath(paths, from, it) }.toMap())
    }

    private data class LongestPathStackItem<V>(
        val current: V,
        val history: PersistentSet<V> = persistentSetOf(),
        val len: Int = 0
    )

    fun longestPath(from: V, to: V): List<GenericEdge<V>>? {
        val stack = ArrayDeque<LongestPathStackItem<V>>()
        stack.addFirst(LongestPathStackItem(from))

        var longestSoFar: PersistentSet<V>? = null
        var maxLengthSoFar: Int? = null
        var i = 0
        while (stack.isNotEmpty()) {
            i++
            val (current, history, len) = stack.removeFirst()

            if (current == to) {
                val currentLength = len

                if (maxLengthSoFar == null || currentLength > maxLengthSoFar) {
                    maxLengthSoFar = currentLength
                    longestSoFar = history
                }
            }

            for ((nextV, nextE) in getEdgesFrom(current)) {
                if (nextV == current || nextV in history) continue
                stack.addFirst(LongestPathStackItem(nextV, history.add(current), len + nextE.weight))
            }
        }

        longestSoFar = longestSoFar?.add(to)

        return longestSoFar?.asSequence()?.zipWithNext()?.map { (from, to) ->
            getEdge(from, to)!!
        }?.toList()
    }

    data class DfsOrders<V>(val pre: List<V>, val post: List<V>) {
        operator fun plus(other: DfsOrders<V>) =
            DfsOrders(pre + other.pre, post + other.post)
    }
    data class Cycle<V>(val elems: List<V>)

}