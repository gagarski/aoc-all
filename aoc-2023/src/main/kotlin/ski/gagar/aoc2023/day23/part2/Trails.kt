package ski.gagar.aoc2023.day23.part2

import ski.gagar.aoc.util.graphs.Graph
import ski.gagar.aoc.util.graphs.GraphBuilder
import ski.gagar.aoc2023.day23.part1.Coordinates
import ski.gagar.aoc2023.day23.part1.Direction

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
    };

    open val canStepIn: Boolean = true
    abstract fun possibleNeighbors(coordinates: Coordinates): Sequence<Coordinates>

    companion object {
        private val byChar = entries.associateBy { it.char }
        private val subst = mapOf(
            '^' to '.',
            '>' to '.',
            'v' to '.',
            '<' to '.'
        )
        fun from(char: Char) = byChar[subst[char] ?: char]
            ?: throw IllegalArgumentException("Unknown cell type $char")
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

    private data class WalkStraightAheadResult(
        val coordinates: Coordinates,
        val directions: Set<Direction>,
        val distance: Int
    )

    private fun Coordinates.walkStraightAhead(direction: Direction): WalkStraightAheadResult {
        var current = this

        var steps = 0

        var dir = direction

        while (true) {
            val next = current.toThe(dir)
            val nextCell = this@TrailField[next]
            if (!nextCell.canStepIn) {
                break
            }

            steps++

            val neighbors = next.getNeighbors(current).toSet()
            current = next

            if (neighbors.size != 1) {
                return WalkStraightAheadResult(current, neighbors.asSequence().map {
                    it.relationTo(current) ?: throw IllegalStateException("Wrong neighbor")
                }.toSet(), steps)
            }

            val soleNeighbor = neighbors.first()
            dir = soleNeighbor.relationTo(current)
                ?: throw IllegalStateException("Wrong neighbor")

        }
        throw IllegalStateException("cannot walk")
    }

    fun getGraph(from: Coordinates): Graph<Coordinates> {
        val bld = GraphBuilder<Coordinates>()

        val firstNeighbors = from.getNeighbors().toList()
        require(firstNeighbors.size == 1)
        val firstNeighbor = firstNeighbors.first()
        val firstDir = firstNeighbor.relationTo(from) ?: error("Wrong neighbor")
        val visited = mutableSetOf<StackItem>()
        val stack = ArrayDeque<StackItem>()

        bld.addVertex(from)
        stack.add(StackItem(from, firstDir))

        while (stack.isNotEmpty()) {
            val stackItem = stack.removeFirst()
            val (v, dir) = stackItem
            if (stackItem in visited) continue
            if (StackItem(v, dir.opposite) in visited) continue
            visited.add(stackItem)
            val (to, dirs, distance) = v.walkStraightAhead(dir)

            if (to !in bld.vertices) {
                bld.addVertex(to)
            }

            bld.addEdge(v, to, distance)
            bld.addEdge(to, v, distance)

            for (d in dirs) {
                stack.add(StackItem(to, d))
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

fun trailsLongestPathNoSlopes(lines: Sequence<String>): Int? {
    val field = TrailField.from(lines)
    val graph = field.getGraph(Coordinates(1, 0))

    return graph.longestPath(Coordinates(1, 0), Coordinates(field.width - 2, field.height - 1))
        ?.sumOf { it.weight }
}
