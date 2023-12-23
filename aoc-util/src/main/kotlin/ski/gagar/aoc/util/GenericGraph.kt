package ski.gagar.aoc.util

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

    private sealed interface CycleStackItem<V> {
        data class ProcessVertex<V>(val vertex: V) : CycleStackItem<V>
        data class PopVertex<V>(val vertex: V) : CycleStackItem<V>
    }

    private fun getCycleFrom(from: V, globalVisited: MutableSet<V>): Sequence<Cycle<V>> = sequence {
        if (vertices.isEmpty()) return@sequence

        val stack = ArrayDeque<CycleStackItem<V>>()
        val path = linkedSetOf<V>()
        stack.addFirst(CycleStackItem.ProcessVertex(from))

        while (stack.isNotEmpty()) {
            val op = stack.removeFirst()

            when (op) {
                is CycleStackItem.PopVertex -> path.removeLast()
                is CycleStackItem.ProcessVertex -> {
                    val v = op.vertex

                    if (v in globalVisited)
                        continue
                    globalVisited.add(v)
                    path.addLast(v)
                    stack.addFirst(CycleStackItem.PopVertex(v))

                    for (to in getEdgesFrom(v).keys) {
                        if (to in path) {
                            val p = linkedSetOf<V>()
                            p.addAll(path)

                            yield(Cycle(backtrack(to, v, p)))
                            continue
                        }
                        stack.addFirst(CycleStackItem.ProcessVertex(to))


                    }
                }
            }

        }
    }

    fun isDag() = getFirstCycle() == null

    private fun backtrack(from: V, to: V, history: Set<V>): List<V> {
        val path = history.asSequence().dropWhile { it != from }.toMutableSet()
        path.add(to)

        return path.toList()
    }

    private sealed interface DfsOrdersStackItem<V> {
        data class ProcessVertex<V>(val vertex: V) : DfsOrdersStackItem<V>
        data class PushToPostOrder<V>(val vertex: V) : DfsOrdersStackItem<V>
    }

    private fun dfsOrders(from: V, visited: MutableSet<V>): DfsOrders<V> {
        val stack = ArrayDeque<DfsOrdersStackItem<V>>()
        val preOrder = mutableListOf<V>()
        val postOrder = mutableListOf<V>()
        stack.add(DfsOrdersStackItem.ProcessVertex(from))

        while (stack.isNotEmpty()) {
            val stackItem = stack.removeFirst()
            when (stackItem) {
                is DfsOrdersStackItem.PushToPostOrder<V> -> postOrder.add(stackItem.vertex)
                is DfsOrdersStackItem.ProcessVertex<V> -> {
                    val v = stackItem.vertex
                    if (v in visited)
                        continue
                    preOrder.add(v)
                    if (v in visited)
                        continue
                    visited.add(v)

                    stack.addFirst(DfsOrdersStackItem.PushToPostOrder(v))
                    for (to in getEdgesFrom(v).keys) {
                        stack.addFirst(DfsOrdersStackItem.ProcessVertex(to))
                    }
                }
            }


        }

        return DfsOrders(preOrder, postOrder)
    }

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

    private sealed interface LongestPathStackItem<V> {
        data class ProcessVertex<V>(
            val vertex: V,
            val len: Int = 0
        ) : LongestPathStackItem<V>
        data class PopVertex<V>(val vertex: V) : LongestPathStackItem<V>
    }



    fun longestPath(from: V, to: V): List<GenericEdge<V>>? {
        val stack = ArrayDeque<LongestPathStackItem<V>>()
        stack.addFirst(LongestPathStackItem.ProcessVertex(from))

        var maxLengthSoFar: Int? = null
        var maxPathSoFar: MutableSet<V>? = null
        val path = linkedSetOf<V>()

        while (stack.isNotEmpty()) {
            val op = stack.removeFirst()

            when (op) {
                is LongestPathStackItem.PopVertex -> path.removeLast()
                is LongestPathStackItem.ProcessVertex -> {
                    val (current, len) = op

                    if (current == to) {
                        val currentLength = len

                        if (maxLengthSoFar == null || currentLength > maxLengthSoFar) {
                            maxLengthSoFar = currentLength
                            maxPathSoFar = linkedSetOf()
                            maxPathSoFar.addAll(path)
                        }
                    }
                    path.add(current)
                    stack.addFirst(LongestPathStackItem.PopVertex(current))
                    for ((nextV, nextE) in getEdgesFrom(current)) {
                        if (nextV == current || nextV in path) continue
                        stack.addFirst(LongestPathStackItem.ProcessVertex(nextV, len + nextE.weight))
                    }
                }
            }

        }


        maxPathSoFar?.add(to)

        return maxPathSoFar?.asSequence()?.zipWithNext()?.map { (from, to) ->
            getEdge(from, to)!!
        }?.toList()
    }

    data class DfsOrders<V>(val pre: List<V>, val post: List<V>) {
        operator fun plus(other: DfsOrders<V>) =
            DfsOrders(pre + other.pre, post + other.post)
    }
    data class Cycle<V>(val elems: List<V>)

}

fun main() {
    val b = GenericGraphBuilder<String>()
    b.addVertex("a")
    b.addVertex("b")
    b.addVertex("c")

    b.addEdge("a", "a")
    b.addEdge("a", "b")
    b.addEdge("b", "a")
    b.addEdge("b", "b")
    b.addEdge("b", "c")
    b.addEdge("c", "b")
    b.addEdge("c", "a")

    println(b.build().getCycles().toList())
}