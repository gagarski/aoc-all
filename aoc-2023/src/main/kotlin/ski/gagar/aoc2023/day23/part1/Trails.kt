package ski.gagar.aoc2023.day23.part1

import ski.gagar.aoc.util.Graph
import ski.gagar.aoc.util.GraphBuilder
import kotlin.math.absoluteValue

data class Coordinates(val x: Int, val y: Int) {
    fun up(steps: Int = 1) = Coordinates(x, y - steps)
    fun right(steps: Int = 1) = Coordinates(x + steps, y)
    fun down(steps: Int = 1) = Coordinates(x, y + steps)
    fun left(steps: Int = 1) = Coordinates(x - steps, y)

    fun toThe(direction: Direction, steps: Int = 1) = when (direction) {
        Direction.UP -> up(steps)
        Direction.RIGHT -> right(steps)
        Direction.DOWN -> down(steps)
        Direction.LEFT -> left(steps)
    }

    fun relationTo(other: Coordinates): Direction? = when {
        this.x == other.x && this.y < other.y -> Direction.UP
        this.y == other.y && this.x > other.x -> Direction.RIGHT
        this.x == other.x && this.y > other.y -> Direction.DOWN
        this.y == other.y && this.x < other.x -> Direction.LEFT
        else -> null
    }

    fun manhattanDistanceTo(other: Coordinates) =
        (this.x - other.x).absoluteValue + (this.y - other.y).absoluteValue
}

enum class Direction {
    UP {
        override val opposite: Direction
            get() = DOWN
    }, RIGHT {
        override val opposite: Direction
            get() = LEFT
    }, DOWN {
        override val opposite: Direction
            get() = UP
    }, LEFT {
        override val opposite: Direction
            get() = RIGHT
    };

    abstract val opposite: Direction
}

enum class CellType(val char: Char) {
    TRAIL('.') {
        override fun possibleNeighbors(coordinates: Coordinates) = sequence {
            yield(coordinates.up())
            yield(coordinates.right())
            yield(coordinates.down())
            yield(coordinates.left())
        }
    },
    FOREST('#') {
        override val canStepIn: Boolean = false
        override fun possibleNeighbors(coordinates: Coordinates): Sequence<Coordinates> = sequence {  }
    },
    SLOPE_U('^') {
        override fun possibleNeighbors(coordinates: Coordinates): Sequence<Coordinates> = sequence {
            yield(coordinates.up())
        }
    },
    SLOPE_R('<') {
        override fun possibleNeighbors(coordinates: Coordinates): Sequence<Coordinates> = sequence {
            yield(coordinates.left())
        }
    },
    SLOPE_D('v') {
        override fun possibleNeighbors(coordinates: Coordinates): Sequence<Coordinates> = sequence {
            yield(coordinates.down())
        }
    },
    SLOPE_L('>') {
        override fun possibleNeighbors(coordinates: Coordinates): Sequence<Coordinates> = sequence {
            yield(coordinates.right())
        }
    };

    open val canStepIn: Boolean = true
    abstract fun possibleNeighbors(coordinates: Coordinates): Sequence<Coordinates>

    companion object {
        private val byChar = entries.associateBy { it.char }
        fun from(char: Char) = byChar[char] ?: throw IllegalArgumentException("Unknown cell type $char")
    }
}

class TrailField(map: Map<Coordinates, CellType>, val width: Int, val height: Int) {
    val map = map.filter { (_, v) -> v != CellType.FOREST }

    operator fun get(coordinates: Coordinates) = map[coordinates] ?: CellType.FOREST
    operator fun contains(coordinates: Coordinates) =
        coordinates.x in 0 until width && coordinates.y in 0 until height

    private fun Coordinates.getNeighbors(excluding: Coordinates? = null) : Sequence<Coordinates> {
        val me = this@TrailField[this]
        require(me != CellType.FOREST) {
            "Cannot step out of the forest"
        }
        return me.possibleNeighbors(this).filter { nXy ->
            if (nXy == excluding)
                return@filter false
            val (x, y) = nXy
            val neighbor = this@TrailField[Coordinates(x, y)]
            return@filter neighbor.canStepIn
        }
    }

    private fun Coordinates.walkStraightAhead(direction: Direction): Pair<Coordinates, Set<Direction>> {
        var current = this

        while (true) {
            val next = current.toThe(direction)
            val nextCell = this@TrailField[next]
            if (!nextCell.canStepIn) {
                break
            }

            val neighbors = next.getNeighbors(current).toSet()
            current = next

            if (neighbors.size != 1) {
                return current to neighbors.asSequence().map {
                    it.relationTo(current) ?: throw IllegalStateException("Wrong neighbor")
                }.toSet()
            }

            val soleNeighbor = neighbors.first()
            val soleNeighborDir = soleNeighbor.relationTo(current)
                ?: throw IllegalStateException("Wrong neighbor")
            if (soleNeighborDir != direction) {
                return current to setOf(soleNeighborDir)
            }

        }
        throw IllegalStateException("cannot walk")
    }

    fun getGraph(from: Coordinates): Graph<Coordinates> {
        val bld = GraphBuilder<Coordinates>()

        val firstNeighbors = from.getNeighbors().toList()
        require(firstNeighbors.size == 1)
        val firstNeighbor = firstNeighbors.first()
        val firstDir = firstNeighbor.relationTo(from) ?: error("Wrong neighbor")
        val queue = ArrayDeque<StackItem>()

        bld.addVertex(from)
        queue.addFirst(StackItem(from, firstDir))

        while (queue.isNotEmpty()) {
            val queueItem = queue.removeFirst()

            val (v, dir) = queueItem
            val (to, dirs) = v.walkStraightAhead(dir)

            if (to !in bld.vertices) {
                bld.addVertex(to)
            }

            bld.addEdge(v, to, v.manhattanDistanceTo(to))

            for (d in dirs) {
                queue.add(StackItem(to, d))
            }
        }

        return bld.build()
    }

    private data class StackItem(val point: Coordinates, val direction: Direction)

    companion object {
        fun from(lines: Sequence<String>): TrailField {
            var w: Int? = null
            var h = 0
            val map = mutableMapOf<Coordinates, CellType>()

            for ((y, line) in lines.withIndex()) {
                require(w == null || w == line.length)
                w = line.length

                for ((x, char) in line.withIndex()) {
                    map[Coordinates(x, y)] = CellType.from(char)
                }

                h++
            }

            require(h != 0)
            check(w != null)

            return TrailField(map, w, h)
        }
    }
}

fun trailsLongestPath(lines: Sequence<String>): Int? {
    val field = TrailField.from(lines)
    val graph = field.getGraph(Coordinates(1, 0)).negated()

    val sp = graph.topoSortShortestPaths(Coordinates(1, 0))

    return sp
        .paths[Coordinates(field.width - 2, field.height - 1)]
        ?.let {
            -it.sumOf { it.weight }
        }
}
