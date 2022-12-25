package ski.gagar.aoc2022.day24.part1

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day23.part2.getNSteps
import java.lang.IllegalArgumentException
import java.util.PriorityQueue
import kotlin.math.absoluteValue

data class Coordinates(val x: Int, val y: Int) {
    fun manhattanTo(other: Coordinates) =
        (x - other.x).absoluteValue + (y - other.y).absoluteValue
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}


sealed interface Move

data class GoTowards(val dir: Direction) : Move
object Wait : Move {
    override fun toString(): String = "Wait"
}

object Enter : Move {
    override fun toString(): String = "Enter"
}

object Extract : Move {
    override fun toString(): String = "Extract"
}



typealias BlizzardPositions = Map<Coordinates, List<Direction>>

fun BlizzardPositions.draw(width: Int, height: Int) = buildString {
    for (y in 0 until height) {
        for (x in 0 until width) {
            val blz = get(Coordinates(x, y))
            append(
                when {
                    blz == null -> '.'
                    blz.size == 1 -> when (blz.first()) {
                        Direction.UP -> "^"
                        Direction.DOWN -> "v"
                        Direction.LEFT -> "<"
                        Direction.RIGHT -> ">"
                    }
                    blz.size in 2..9 -> "${blz.size}"
                    else -> "*"
                }
            )
        }
        append("\n")
    }
}

data class State(
    // "Next" position, the one under each we'll take the decision, this can cover our current position
    val blizzards: BlizzardPositions,
    val width: Int,
    val height: Int,
    val startPosition: Coordinates = Coordinates(0, 0),
    val endPosition: Coordinates = Coordinates(width - 1, height - 1),
    val extracted: Boolean = false,
    val myPosition: Coordinates? = null,
    val moves: PersistentList<Move> = persistentListOf(),
    val blizzardsCache: MutableMap<Int, BlizzardPositions> = mutableMapOf(moves.size to blizzards)
) {
    fun nextBlizzards(): BlizzardPositions {
        val cached = blizzardsCache[moves.size + 1]

        if (cached != null) return cached

        val new = mutableMapOf<Coordinates, MutableList<Direction>>()

        for ((pos, directions) in blizzards) {
            for (dir in directions) {
                val nextPos = pos.goTo(dir)
                new[nextPos] = (new[nextPos] ?: mutableListOf()).apply {
                    add(dir)
                }
            }
        }

        blizzardsCache[moves.size + 1] = new

        return new
    }

    fun possibleMoves() = sequence {
        when {
            myPosition == null -> {
                if (!extracted) {
                    if (startPosition !in blizzards) {
                        yield(Enter)
                    }
                    yield(Wait)
                }
            }
            myPosition == endPosition -> {
                yield(Extract)
            }
            else -> {
                for (dir in Direction.values()) {
                    val nextCoords = myPosition.goTo(dir, false)

                    if (nextCoords.areInside && nextCoords !in blizzards) {
                        yield(GoTowards(dir))
                    }
                }
                if (myPosition !in blizzards) {
                    yield(Wait)
                }
            }
        }
    }

    private val Coordinates.areInside: Boolean
        get() = x in (0 until width) && y in (0 until  height)

    fun Coordinates.goTo(dir: Direction, wrap: Boolean = true): Coordinates {
        val x = when (dir) {
            Direction.LEFT -> this.x - 1
            Direction.RIGHT -> this.x + 1
            else -> this.x
        }
        val y = when (dir) {
            Direction.UP -> this.y - 1
            Direction.DOWN -> this.y + 1
            else -> this.y
        }

        val xWrapped = when {
            wrap -> (x + width) % width
            else -> x
        }

        val yWrapped = when {
            wrap -> (y + height) % height
            else -> y
        }

        return Coordinates(xWrapped, yWrapped)
    }
}

data class NextMoveAndState(val nextMove: Move, val state: State) {
    fun apply() = State(
        blizzards = state.nextBlizzards(),
        width = state.width,
        height = state.height,
        startPosition = state.startPosition,
        endPosition = state.endPosition,
        extracted = nextMove == Extract,
        myPosition = when (nextMove) {
            is Wait -> state.myPosition
            is Enter -> state.startPosition
            is Extract -> null
            is GoTowards -> with (state) {
                state.myPosition!!.goTo(nextMove.dir)
            }
        },
        moves = state.moves.add(nextMove),
        blizzardsCache = state.blizzardsCache
    )
}

data class CourseOfAction(val moves: List<Move>)

data class StateCacheItem(val coordinates: Coordinates?, val pathLength: Int, val extracted: Boolean)

val State.cacheItem
    get() = StateCacheItem(myPosition, moves.size, extracted)

fun quickestCourseOfAction(
    blizzards: BlizzardPositions,
    width: Int,
    height: Int,
    startPosition: Coordinates = Coordinates(0, 0),
    endPosition: Coordinates = Coordinates(width - 1, height - 1)
): CourseOfAction? {
//    val queue = PriorityQueue<NextMoveAndState>(Comparator.comparing {
//        when (it.state.myPosition) {
//            null -> if (it.state.extracted) Int.MIN_VALUE else Int.MAX_VALUE
//            else -> it.state.myPosition.manhattanTo(it.state.endPosition)
//        }
//    })
    val queue = ArrayDeque<NextMoveAndState>()
    val preStartState = State(
        blizzards = blizzards,
        width = width,
        height = height,
        startPosition = startPosition,
        endPosition = endPosition,
        blizzardsCache = mutableMapOf()
    )

    val nextBlizzards = preStartState.nextBlizzards()

    val startState = preStartState.copy(
        blizzards = nextBlizzards,
        blizzardsCache = mutableMapOf(0 to nextBlizzards)
    )

    for (move in startState.possibleMoves()) {
        queue.add(NextMoveAndState(move, startState))
    }

    val seen = mutableSetOf<StateCacheItem>()

    while (queue.isNotEmpty()) {
        val move = queue.removeFirst()
        val nextState = move.apply()

        val cacheItem = nextState.cacheItem

        if (cacheItem in seen)
            continue

        seen.add(cacheItem)

        if (nextState.extracted) {
            return CourseOfAction(nextState.moves)
        }

        for (nextMove in nextState.possibleMoves()) {
            queue.add(NextMoveAndState(nextMove, nextState))
        }
    }



    return null
}

val LINE_RE = """^#[.^v<>]*#$""".toRegex()

fun findQuickestLength(
    linesSeq: Sequence<String>
): Int {
    val lines = linesSeq.toList()

    require(lines.size >= 3) // Top, bottom and at least one as a height of the room

    val firstLine = lines[0]
    val startY = 0
    val startX = firstLine.indexOf('.') - 1 // [0] is a corner of top and left wall

    require(startX >= 0 && startX != firstLine.length - 1)
    require(firstLine.withIndex().all { (ix, it) -> it == '#' || ix == startX + 1 })

    val lastLine = lines.last()
    val endY = lines.size - 3 // size - 1 - top line - bottom line (we want an extraction point to be inside grid)
    val endX = lastLine.indexOf('.') - 1 // [0] is a corner of bottom and left wall

    require(endX >= 0 && endX != lastLine.length - 1)
    require(lastLine.withIndex().all { (ix, it) -> it == '#' || ix == endX + 1 })

    val width = firstLine.length - 2
    val height = lines.size - 2

    val positions = sequence {
        for (y in 1 until lines.size - 1) { // First and last line are already handled
            val line = lines[y]
            require(line.matches(LINE_RE))
            require(line.length == width + 2)
            for (x in 1 until line.length - 1) {
                val char = line[x]

                val dir = when (char) {
                    '.' -> null
                    '^' -> Direction.UP
                    'v' -> Direction.DOWN
                    '>' -> Direction.RIGHT
                    '<' -> Direction.LEFT
                    else -> throw IllegalArgumentException("oops")
                }

                dir?.let {
                    yield(Coordinates(x - 1, y - 1) to dir)
                }
            }
        }
    }.groupBy { it.first }.mapValues { it.value.map { it.second } }

    return quickestCourseOfAction(
        blizzards = positions,
        width = width,
        height = height,
        startPosition = Coordinates(startX, startY),
        endPosition = Coordinates(endX, endY)
    )!!.moves.size
}

fun day24Part1() {
    println("day24/part1/plants: \n${
        findQuickestLength(
            getResourceAsStream("/ski.gagar.aoc.aoc2022.day24/blizzards.txt").bufferedReader().lineSequence()
        )
    }")
}
