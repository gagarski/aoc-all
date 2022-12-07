package ski.gagar.aoc.util

data class Edge(val from: String, val to: String, val weight: Int)

class GraphBuilder {
    val vertices: MutableSet<String> = mutableSetOf()
    val edges: MutableMap<String, MutableMap<String, Edge>> = mutableMapOf()
    var built: Boolean = false


    fun addVertex(name: String) {
        require(!built)
        vertices.add(name)
    }

    fun addEdge(from: String, to: String, weight: Int) {
        require(!built)
        require(from in vertices)
        require(to in vertices)

        edges[from] = (edges[from] ?: mutableMapOf()).apply {
            this[to] = Edge(from, to, weight)
        }
    }

    fun addNonDirectedEdge(first: String, second: String, weight: Int) {
        require(!built)
        addEdge(first, second, weight)
        addEdge(second, first, weight)
    }

    fun getEdgesFrom(from: String) = edges[from] ?: mapOf()
    fun getEdge(from: String, to: String): Edge? = getEdgesFrom(from)[to]

    fun build(): Graph {
        require(!built)
        return Graph(vertices, edges)
    }
}

class Graph(val vertices: Set<String>, val edges: MutableMap<String, MutableMap<String, Edge>>) {
    fun getEdgesFrom(from: String) = edges[from] ?: mapOf()
    fun getEdge(from: String, to: String): Edge? = getEdgesFrom(from)[to]
}
