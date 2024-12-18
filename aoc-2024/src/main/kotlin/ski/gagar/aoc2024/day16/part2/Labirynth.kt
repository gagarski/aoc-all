package ski.gagar.aoc2024.day16.part2

import ski.gagar.aoc.util.graphs.Edge
import ski.gagar.aoc2024.day16.part1.Coordinates
import ski.gagar.aoc2024.day16.part1.Direction
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayDeque


class Labyrinth(lines: Sequence<String>) {
    val width: Int
    val height: Int
    val walls: Set<Coordinates>
    val start: Coordinates
    val end: Coordinates

    init {
        var w: Int? = null
        var h = 0
        val walls = mutableSetOf<Coordinates>()
        var start: Coordinates? = null
        var end: Coordinates? = null

        for ((row, line) in lines.withIndex()) {
            h++
            for ((column, char) in line.withIndex()) {
                if (null != w) {
                    require(line.length == w)
                }
                w = line.length
                when (char) {
                    '#' -> walls.add(Coordinates(row, column))
                    'S' -> {
                        require(start == null)
                        start = Coordinates(row, column)
                    }
                    'E' -> {
                        require(end == null)
                        end = Coordinates(row, column)
                    }
                    '.' -> {}
                    else -> throw IllegalArgumentException("Illegal cell $char")
                }
            }
        }

        require(w != null)
        require(h != 0)
        require(start != null)
        require(end != null)
        this.width = w
        this.height = h
        this.walls = walls
        this.start = start
        this.end = end
    }

    private inner class Vertex(val coordinates: Coordinates, val direction: Direction) {
        fun edgesFrom() = sequence {
            yield(Edge(this@Vertex, Vertex(coordinates, direction.cw()), 1000))
            yield(Edge(this@Vertex, Vertex(coordinates, direction.ccw()), 1000))
            val next = coordinates.walkTowards(direction)

            if (next in this@Labyrinth && next !in walls)
                yield(Edge(this@Vertex, Vertex(next, direction), 1))
        }



        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Vertex

            if (coordinates != other.coordinates) return false
            if (direction != other.direction) return false

            return true
        }

        override fun hashCode(): Int {
            var result = coordinates.hashCode()
            result = 31 * result + direction.hashCode()
            return result
        }

        override fun toString(): String {
            return "Vertex(coordinates=$coordinates, direction=$direction)"
        }


    }

    private inner class Edge(val from: Vertex, val to: Vertex, val weight: Int)

    operator fun contains(coordinates: Coordinates): Boolean =
        coordinates.row in 0 ..< height && coordinates.col in 0 ..< width

    fun tilesOnBestPaths(): Set<Coordinates> {
        val distances = mutableMapOf<Vertex, Int>()
        val visited = mutableSetOf<Vertex>()
        val paths = mutableMapOf<Vertex, MutableSet<Edge>>()

        val startVertex = Vertex(start, Direction.RIGHT)
        val queue = PriorityQueue<Pair<Vertex, Int>>(Comparator.comparing { it.second })

        distances[startVertex] = 0
        queue.add(startVertex to distances[startVertex]!!)

        while (!queue.isEmpty()) {
            val (current, _) = queue.remove()
            if (current in visited) {
                continue
            }

            visited.add(current)

            for (edge in current.edgesFrom()) {
                val currentDistToNeighbor = distances[edge.to]
                val newDistToNeighbor = distances[current]!! + edge.weight

                if (null == currentDistToNeighbor || newDistToNeighbor < currentDistToNeighbor) {
                    distances[edge.to] = newDistToNeighbor
                    paths[edge.to] = mutableSetOf(edge)
                } else if (currentDistToNeighbor == newDistToNeighbor) {
                    paths[edge.to]!!.add(edge)
                }
                queue.add(edge.to to newDistToNeighbor)
            }
        }

        val btVisited = mutableSetOf<Vertex>()

        fun backtrack(target: Vertex) {
            val btQueue = ArrayDeque<Vertex>()
            btQueue.addLast(target)

            while (!btQueue.isEmpty()) {
                val v = btQueue.removeFirst()
                if (v in btVisited) continue
                btVisited.add(v)

                for (path in paths[v] ?: emptySet()) {
                    btQueue.addLast(path.from)
                }
            }
        }

        backtrack(Vertex(end, Direction.UP))
        backtrack(Vertex(end, Direction.UP))
        backtrack(Vertex(end, Direction.UP))
        backtrack(Vertex(end, Direction.UP))

        return btVisited.asSequence().map { it.coordinates }.toSet()
    }
}

fun labyrinthBestSpots(lines: Sequence<String>): Int = Labyrinth(lines).tilesOnBestPaths().size