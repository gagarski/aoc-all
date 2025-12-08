package ski.gagar.aoc.util.graphs

import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayDeque

data class Edge<V>(val from: V, val to: V, val weight: Int)

class Graph<V>(
    val vertices: Set<V>,
    val edges: Map<V, Map<V, Edge<V>>>
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
    fun getEdge(from: V, to: V): Edge<V>? = getEdgesFrom(from)[to]

    fun getEdgesTo(to: V) = edgesTo[to] ?: mapOf()

    private fun tracePath(
        paths: Map<V, Edge<V>>,
        from: V,
        to: V
    ): List<Edge<V>> {
        val res = mutableListOf<Edge<V>>()
        var current: V = to

        do {
            val edge = paths[current]
            check(edge != null)
            res.add(edge)
            current = edge.from
        } while (current != from)

        return res.reversed()
    }

    fun shortestPaths(from: V): ShortestPaths<V> {
        val distances = mutableMapOf<V, Int>()
        val paths = mutableMapOf<V, Edge<V>>()
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

        return ShortestPaths(from, paths.keys.asSequence().map { it to tracePath(paths, from, it) }.toMap())
    }

    fun negated() = Graph(vertices, edges.mapValues { (_, edgesBySource) ->
        edgesBySource.mapValues { (_, edge) -> Edge(edge.from, edge.to, -edge.weight) }
    })

    fun getFirstCycle(): List<V>? = getCycles().firstOrNull()

    fun getCycles(): Sequence<List<V>> = sequence {
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

    private fun getCycleFrom(from: V, globalVisited: MutableSet<V>): Sequence<List<V>> = sequence {
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

                            yield(backtrack(to, v, p))
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

    fun topoSortShortestPaths(from: V): ShortestPaths<V> {
        val sorted = topoSort()

        val distances = mutableMapOf<V, Int>()
        val paths = mutableMapOf<V, Edge<V>>()

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

        return ShortestPaths(from, paths.keys.asSequence().map { it to tracePath(paths, from, it) }.toMap())
    }

    private sealed interface LongestPathStackItem<V> {
        data class ProcessVertex<V>(
            val vertex: V,
            val len: Int = 0
        ) : LongestPathStackItem<V>
        data class PopVertex<V>(val vertex: V) : LongestPathStackItem<V>
    }

    fun longestPath(from: V, to: V): List<Edge<V>>? {
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

    private data class BiPartQueueItem<V>(val vertex: V, val isOdd: Boolean = false)

    fun bipart(): Parts<V>? {
        val first: V = vertices.first() ?: return null

        val odd = mutableSetOf<V>()
        val even = mutableSetOf<V>()

        val queue = ArrayDeque<BiPartQueueItem<V>>()
        queue.add(BiPartQueueItem(first))

        while(queue.isNotEmpty()) {
            val (v, isOdd) = queue.removeFirst()
            when {
                v in odd && !isOdd -> return null
                v in even && isOdd -> return null
                v in odd || v in even -> continue
                isOdd -> odd.add(v)
                else -> even.add(v)
            }

            for ((neighbor, _) in getEdgesFrom(v)) {
                queue.add(BiPartQueueItem(neighbor, !isOdd))
            }
        }

        if (odd.size + even.size != vertices.size)
            return null

        return Parts(odd, even)
    }

    fun segments(): Set<Set<V>> {
        val visited = mutableSetOf<V>()
        val segments = mutableSetOf<Set<V>>()
        for (v in vertices) {
            if (v in visited) continue
            val segment = mutableSetOf<V>()
            bfs(v) {
                visited.add(it)
                segment.add(it)
            }
            segments += segment
        }
        return segments
    }

    fun bfs(start: V, visitor: (V) -> Unit) {
        val queue = ArrayDeque<V>()
        val visited = mutableSetOf<V>()
        require(start in vertices)

        queue.addLast(start)

        while (queue.isNotEmpty()) {
            val item = queue.removeFirst()
            if (item in visited) {
                continue
            }
            visitor(item)
            visited.add(item)
            for ((n, _) in getEdgesFrom(item)) {
                if (n !in visited) {
                    queue.addLast(n)
                }
            }
        }
    }

    fun copy(): GraphBuilder<V> =
        GraphBuilder(vertices.toMutableSet(),
            edges.mapValues { (k, v) -> v.toMutableMap() }.toMutableMap()
        )

    private data class DfsOrders<V>(val pre: List<V>, val post: List<V>) {
        operator fun plus(other: DfsOrders<V>) =
            DfsOrders(pre + other.pre, post + other.post)
    }

    data class Parts<V>(val odd: Set<V>, val even: Set<V>)

}

data class ShortestPaths<V>(
    val from: V,
    val paths: Map<V, List<Edge<V>>>
) {
    fun to(node: V) = paths[node]
}

class GraphBuilder<V> internal constructor(vs: MutableSet<V>, es: MutableMap<V, MutableMap<V, Edge<V>>>) {
    constructor() : this(mutableSetOf(), mutableMapOf())
    private val vertices_: MutableSet<V> = vs
    private val edges_: MutableMap<V, MutableMap<V, Edge<V>>> = es
    var built: Boolean = false
        private set

    val vertices: Set<V>
        get() = vertices_
    val edges: Map<V, Map<V, Edge<V>>>
        get() = edges_


    fun addVertex(name: V) {
        require(!built)
        vertices_.add(name)
    }

    fun removeVertex(name: V) {
        require(!built)
        edges_.remove(name)
        vertices_.remove(name)
    }

    fun addEdge(from: V, to: V, weight: Int = 1): Edge<V> {
        require(!built)
        require(from in vertices_)
        require(to in vertices_)
        val newEdge = Edge(from, to, weight)
        edges_[from] = (edges_[from] ?: mutableMapOf()).apply {
            this[to] = newEdge
        }
        return newEdge
    }

    fun removeEdge(from: V, to: V) {
        require(!built)
        val srcEdges = edges_[from] ?: mutableMapOf()
        srcEdges.remove(to)
        if (srcEdges.isEmpty())
            edges_.remove(from)
    }

    fun addNonDirectedEdge(first: V, second: V, weight: Int = 1): Pair<Edge<V>, Edge<V>> {
        require(!built)
        val edge1 = addEdge(first, second, weight)
        val edge2 = addEdge(second, first, weight)
        return edge1 to edge2
    }

    fun removeNonDirectedEdge(first: V, second: V) {
        removeEdge(first, second)
        removeEdge(second, first)
    }

    fun getEdgesFrom(from: V) = edges_[from] ?: mapOf()
    fun getEdge(from: V, to: V): Edge<V>? = getEdgesFrom(from)[to]

    fun bfs(start: V, visitor: (V) -> Unit) {
        val queue = ArrayDeque<V>()
        val visited = mutableSetOf<V>()
        require(start in vertices)

        queue.addLast(start)

        while (queue.isNotEmpty()) {
            val item = queue.removeFirst()
            if (item in visited) {
                continue
            }
            visitor(item)
            visited.add(item)
            for ((n, _) in getEdgesFrom(item)) {
                if (n !in visited) {
                    queue.addLast(n)
                }
            }
        }
    }


    fun build(): Graph<V> {
        require(!built)
        return Graph(vertices_, edges_)
    }

    fun buildUnsafe(): Graph<V> {
        return Graph(vertices_, edges_)
    }
}