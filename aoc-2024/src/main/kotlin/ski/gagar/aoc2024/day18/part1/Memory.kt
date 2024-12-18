package ski.gagar.aoc2024.day18.part1

import ski.gagar.aoc.util.graphs.Edge
import ski.gagar.aoc.util.graphs.GraphBuilder

data class Address(val row: Int, val col: Int) {
    fun neighbors() = sequence {
        yield(Address(row - 1, col))
        yield(Address(row, col + 1))
        yield(Address(row + 1, col))
        yield(Address(row, col - 1))
    }
}

class Memory(lines: Sequence<String>, limit: Int = 1024, val width: Int = 70, val height: Int = 70) {
    val corrupted: Set<Address> =
        lines.take(limit).map {
            val splitted = it.split(",")
            require(splitted.size == 2)
            val address = Address(splitted[0].toInt(), splitted[1].toInt())
            require(address in this)
            address
        }.toSet()

    operator fun contains(address: Address): Boolean =
        address.row in 0 ..< height && address.col in 0 ..< width

    fun shortestPath(): List<Edge<Address>>? {
        val gb = GraphBuilder<Address>()

        for (row in 0 ..< height) {
            for (col in 0 ..< width) {
                val addr = Address(row, col)

                if (addr !in corrupted) {
                    gb.addVertex(addr)
                }
            }
        }

        for (v in gb.vertices) {
            for (neighbor in v.neighbors()) {
                if (neighbor in gb.vertices) {
                    gb.addEdge(v, neighbor)
                }
            }
        }

        val graph = gb.build()

        val start = Address(0, 0)
        val end = Address(height - 1, width - 1)

        require(start in graph.vertices)
        require(end in graph.vertices)

        return graph.shortestPaths(start).paths[end]
    }

    override fun toString(): String = buildString {
        for (row in 0 until height) {
            for (col in 0 until width) {
                when (Address(row, col)) {
                    in corrupted -> append("#")
                    else -> append(".")
                }
            }
            append("\n")
        }
    }
}

fun shortestPathLength(lines: Sequence<String>, limit: Int = 1024, width: Int = 71, height: Int = 71): Int {
    val mem = Memory(lines, limit, width, height)
    return mem.shortestPath()!!.size
}
