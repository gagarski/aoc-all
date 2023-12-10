package ski.gagar.aoc2023.day10.part1

import java.util.PriorityQueue

enum class Direction {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM;

    companion object {
        fun from(char: Char): Set<Direction> =
            when (char) {
                '|' -> setOf(TOP, BOTTOM)
                '-' -> setOf(LEFT, RIGHT)
                'L' -> setOf(TOP, RIGHT)
                'J' -> setOf(TOP, LEFT)
                'F' -> setOf(BOTTOM, RIGHT)
                '7' -> setOf(BOTTOM, LEFT)
                else -> setOf()
            }
    }
}


data class ShortestPathsResult(
    val from: Maze.Node,
    val paths: Map<Maze.Node, List<Maze.Node>>
) {
    fun to(node: Maze.Node) = paths[node]
}

class Maze(description: Sequence<String>) {
    val width: Int
    val height: Int
    val animal: Node
    val verticles: Set<Node>
    val edges: Map<Node, Set<Node>>

    init {
        var animal: Node? = null
        val outgoing = mutableMapOf<Node, MutableSet<Node>>()
        val verticles = mutableSetOf<Node>()
        val edges = mutableMapOf<Node, MutableSet<Node>>()
        var height = 0
        var width: Int? = null
        for ((y, line) in description.withIndex()) {
            require(width == null || line.length == width)
            width = line.length
            for ((x, char) in line.withIndex()) {
                val node = Node(x, y)
                if (char == 'S') {
                    require(animal == null)
                    animal = node
                    continue
                }

                val directions = Direction.from(char)

                if (directions.isNotEmpty())
                    verticles.add(node)

                for (neighbor in node.getAdjacent(directions)) {
                    val og = outgoing[node] ?: mutableSetOf()
                    og.add(neighbor)
                    outgoing[node] = og

                    if (node in (outgoing[neighbor] ?: setOf())) {
                        val og = edges[node] ?: mutableSetOf()
                        og.add(neighbor)
                        edges[node] = og

                        val ig = edges[neighbor] ?: mutableSetOf()
                        ig.add(node)
                        edges[neighbor] = ig
                    }
                }
            }
            height++
        }

        require(animal != null)
        var animalConnections = 0

        for (neighbor in animal.getAdjacent()) {
            if (animal in (outgoing[neighbor] ?: setOf())) {
                val og = edges[animal] ?: mutableSetOf()
                og.add(neighbor)
                edges[animal] = og

                val ig = edges[neighbor] ?: mutableSetOf()
                ig.add(animal)
                edges[neighbor] = ig
                animalConnections++
            }
        }

        require(animalConnections == 2)
        verticles.add(animal)

        this.width = width!!
        this.height = height
        this.animal = animal
        this.edges = edges
        this.verticles = verticles
    }

    fun shortestPaths(from: Node): ShortestPathsResult {
        val distances = mutableMapOf<Node, Int>()
        val paths = mutableMapOf<Node, Node>()
        val visited = mutableSetOf<Node>()

        distances[from] = 0

        val queue = PriorityQueue<Pair<Node, Int>>(Comparator.comparing { it.second })

        queue.add(from to distances[from]!!)

        while (!queue.isEmpty()) {
            val (current, _) = queue.remove()
            if (current in visited) {
                continue
            }

            visited.add(current)

            val neighbors = edges[current] ?: setOf()

            for (neighbor in neighbors) {
                val currentDistToNeighbor = distances[neighbor]
                val newDistToNeighbor = distances[current]!! + 1

                if (null == currentDistToNeighbor || newDistToNeighbor < currentDistToNeighbor) {
                    distances[neighbor] = newDistToNeighbor
                    paths[neighbor] = current
                }
                queue.add(neighbor to newDistToNeighbor)
            }
        }

        fun tracePathTo(node: Node): List<Node> {
            val res = mutableListOf<Node>()
            var current: Node? = node

            do {
                require(current != null)
                res.add(current)
                current = paths[current]
            } while (current != from)

            return res.reversed()
        }

        return ShortestPathsResult(from, paths.keys.asSequence().map { it to tracePathTo(it) }.toMap())
    }

    fun furthestFrom(from: Node) =
        shortestPaths(from).paths.maxOf { (_, v) -> v.size }

    inner class Node(val x: Int, val y: Int) {
        private val maze = this@Maze

        fun getAdjacent() = sequence {
            yield(getAdjacent(Direction.LEFT))
            yield(getAdjacent(Direction.RIGHT))
            yield(getAdjacent(Direction.TOP))
            yield(getAdjacent(Direction.BOTTOM))
        }

        fun getAdjacent(directions: Set<Direction>) =
            directions.asSequence().map { getAdjacent(it) }.filterNotNull()

        fun getAdjacent(direction: Direction) = when (direction) {
            Direction.LEFT -> Node(x - 1, y)
            Direction.RIGHT -> Node(x + 1, y)
            Direction.TOP -> Node(x, y - 1)
            Direction.BOTTOM -> Node(x, y + 1)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Node

            if (x != other.x) return false
            if (y != other.y) return false
            if (maze != other.maze) return false

            return true
        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            result = 31 * result + maze.hashCode()
            return result
        }

        override fun toString(): String {
            return "Node(x=$x, y=$y)"
        }


    }
}

fun findFurthestFromAnimal(lines: Sequence<String>): Int {
    val maze = Maze(lines)
    return maze.furthestFrom(maze.animal)
}