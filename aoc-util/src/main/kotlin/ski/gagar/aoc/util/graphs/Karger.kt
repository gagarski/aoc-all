package ski.gagar.aoc.util.graphs

private data class MultiVertex<V>(val vertices: Set<V>) {
    infix fun with(other: MultiVertex<V>) = MultiVertex(this.vertices + other.vertices)
}
private data class MultiEdge<V>(val from: MultiVertex<V>, val to: MultiVertex<V>, val edges: Set<Edge<V>>)



private class MultiGraph<V>(graph: Graph<V>) {
    private val vertices: MutableSet<MultiVertex<V>> = mutableSetOf()
    private val edges: MutableMap<MultiVertex<V>, MutableMap<MultiVertex<V>, MultiEdge<V>>> = mutableMapOf()

    init {
        val vToMulti = graph.vertices.associateWith {
            MultiVertex(setOf(it)).also { mv ->addVertex(mv) }
        }

        for ((from, tos) in graph.edges) {
            for ((to, edge) in tos) {
                require(graph.getEdge(to, from) != null)
                val fromM = vToMulti[from]!!
                val toM = vToMulti[to]!!
                addEdge(fromM, toM, setOf(edge))
            }
        }
    }

    private fun addVertex(vertex: MultiVertex<V>) {
        this.vertices.add(vertex)
    }

    private fun removeVertex(vertex: MultiVertex<V>) {
        val toRemove = mutableSetOf<Pair<MultiVertex<V>, MultiVertex<V>>>()
        for ((neighbor, e) in edges[vertex] ?: mapOf()) {
            toRemove.add(vertex to neighbor)
            toRemove.add(neighbor to vertex)
        }
        this.vertices.remove(vertex)
        for ((a, b) in toRemove) {
            removeEdge(a, b)
        }
    }

    private fun addEdge(from: MultiVertex<V>, to: MultiVertex<V>, edges: Set<Edge<V>>) =
        addEdge(MultiEdge(from, to, edges))

    private fun addEdge(edge: MultiEdge<V>) {
        this.edges[edge.from] = (this.edges[edge.from] ?: mutableMapOf()).apply {
            this[edge.to] = edge
        }
    }

    private fun removeEdge(from: MultiVertex<V>, to: MultiVertex<V>) {
        val fromEdges = edges[from]

        fromEdges?.remove(to)

        if (fromEdges?.isEmpty() == true) {
            edges.remove(from)
        }

    }

    private fun contract(edge: MultiEdge<V>) {
        val newV = edge.from with edge.to
        val first = edge.from
        val second = edge.to

        val toAdd = mutableSetOf<MultiEdge<V>>()
        val toRemove = mutableSetOf<MultiEdge<V>>()

        vertices.add(newV)

        toRemove.add(edge)

        val opposite = getEdge(edge.to, edge.from) ?: error("Opposite edge not found")
        toRemove.add(opposite)


        val toSkipForSecond = mutableSetOf<Pair<MultiVertex<V>, MultiVertex<V>>>()

        for ((firstTo, firstEdge) in edges[first] ?: mapOf()) {
            val secondEdge = getEdge(second, firstTo)
            val firstOpposite = getEdge(firstEdge.to, firstEdge.from) ?: error("Opposite edge not found")
            val secondOpposite = secondEdge?.let {
                getEdge(secondEdge.to, secondEdge.from) ?: error("Opposite edge not found")
            }

            toRemove.add(firstEdge)
            toRemove.add(firstOpposite)
            secondEdge?.let { toRemove.add(it) }
            secondOpposite?.let { toRemove.add(it) }

            secondEdge?.let { toSkipForSecond.add(secondEdge.from to secondEdge.to) }

            val combo = firstEdge.edges + (secondEdge?.edges ?: setOf())
            toAdd.add(MultiEdge(newV, firstTo, combo))


            val oppositeCombo = firstOpposite.edges + (secondOpposite?.edges ?: setOf())

            toAdd.add(MultiEdge(firstTo, newV, oppositeCombo))
        }

        for ((secondTo, secondEdge) in edges[second] ?: mapOf()) {
            if (secondEdge.from to secondEdge.to in toSkipForSecond) continue

            val opposite = getEdge(secondEdge.to, secondEdge.from)
                ?: error("Opposite edge not found")

            toAdd.add(MultiEdge(newV, secondTo, secondEdge.edges))
            toAdd.add(MultiEdge(secondTo, newV, opposite.edges))
        }

        for (e in toRemove) {
            removeEdge(e.from, e.to)
        }

        for (e in toAdd) {
            addEdge(e)
        }

        removeVertex(first)
        removeVertex(second)
    }

    private fun randomEdge() = allEdges.toList().random()

    fun karger(): KargerResult<V> {
        while (vertices.size > 2) {
            contract(randomEdge())
        }

        check(vertices.size == 2)

        val vList = vertices.toList()

        val first = vList[0]
        val second = vList[1]
        val edges = getEdge(first, second)?.edges ?: error {
            "Cut not found"
        }
        val oppositeEdges = getEdge(second, first)?.edges ?: error {
            "Cut not found"
        }

        return KargerResult(first.vertices, second.vertices, edges + oppositeEdges)
    }

    private fun getEdgesFrom(from: MultiVertex<V>) = edges[from] ?: mapOf()
    private fun getEdge(from: MultiVertex<V>, to: MultiVertex<V>): MultiEdge<V>? = getEdgesFrom(from)[to]

    private val allEdges: Sequence<MultiEdge<V>> = edges.values.asSequence().flatMap { it.values.asSequence() }
}

data class KargerResult<V>(
    val first: Set<V>,
    val second: Set<V>,
    val connectingEdges: Set<Edge<V>>
)

fun <V>Graph<V>.minCutKarger(): KargerResult<V> =
    MultiGraph(this).karger()
