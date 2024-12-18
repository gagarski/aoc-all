package ski.gagar.aoc2024.day16.part1

import java.util.*
import kotlin.Comparator

data class Coordinates(val row: Int, val col: Int) {
    fun walkTowards(direction: Direction) = when (direction) {
        Direction.UP -> Coordinates(row - 1, col)
        Direction.RIGHT -> Coordinates(row, col + 1)
        Direction.DOWN -> Coordinates(row + 1, col)
        Direction.LEFT -> Coordinates(row, col - 1)
    }
}

enum class Direction {
    UP {
        override fun cw(): Direction = RIGHT
        override fun ccw(): Direction = LEFT
    },
    RIGHT {
        override fun cw(): Direction = DOWN
        override fun ccw(): Direction = UP
    },
    DOWN {
        override fun cw(): Direction = LEFT
        override fun ccw(): Direction = RIGHT
    },
    LEFT {
        override fun cw(): Direction = UP
        override fun ccw(): Direction = DOWN
    };

    abstract fun cw(): Direction
    abstract fun ccw(): Direction
}

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

    fun lowestScore(): Int {
        val distances = mutableMapOf<Vertex, Int>()
        val visited = mutableSetOf<Vertex>()

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
                }
                queue.add(edge.to to newDistToNeighbor)
            }
        }

        return sequenceOf(
            Vertex(end, Direction.UP),
            Vertex(end, Direction.RIGHT),
            Vertex(end, Direction.DOWN),
            Vertex(end, Direction.LEFT)
        ).mapNotNull {
            distances[it]
        }.min()
    }
}

fun labyrinthLowestScore(lines: Sequence<String>): Int = Labyrinth(lines).lowestScore()