package ski.gagar.aoc2022.day24.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day24.part1.NextMoveAndState
import ski.gagar.aoc2022.day24.part1.BlizzardPositions
import ski.gagar.aoc2022.day24.part1.Coordinates
import ski.gagar.aoc2022.day24.part1.Direction
import ski.gagar.aoc2022.day24.part1.Move
import ski.gagar.aoc2022.day24.part1.State
import ski.gagar.aoc2022.day24.part1.StateCacheItem
import ski.gagar.aoc2022.day24.part1.cacheItem

data class CourseOfAction(val moves: List<Move>, val blizzards: BlizzardPositions)


private fun quickestCourseOfAction(
    blizzards: BlizzardPositions,
    width: Int,
    height: Int,
    startPosition: Coordinates = Coordinates(0, 0),
    endPosition: Coordinates = Coordinates(width - 1, height - 1),
    ff: Boolean = true
): CourseOfAction? {
    val queue = ArrayDeque<NextMoveAndState>()
    val preStartState = State(
        blizzards = blizzards,
        width = width,
        height = height,
        startPosition = startPosition,
        endPosition = endPosition,
        blizzardsCache = mutableMapOf(0 to blizzards)
    )

    val nextBlizzards = preStartState.nextBlizzards()

    val startState =
        if (ff) {
            preStartState.copy(
                blizzards = nextBlizzards,
                blizzardsCache = mutableMapOf(0 to nextBlizzards)
            )
        } else {
            preStartState
        }

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
            return CourseOfAction(nextState.moves, nextState.blizzardsCache[nextState.moves.size]!!)
        }

        for (nextMove in nextState.possibleMoves()) {
            queue.add(NextMoveAndState(nextMove, nextState))
        }
    }



    return null
}

private val LINE_RE = """^#[.^v<>]*#$""".toRegex()

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

    val forward = quickestCourseOfAction(
        blizzards = positions,
        width = width,
        height = height,
        startPosition = Coordinates(startX, startY),
        endPosition = Coordinates(endX, endY)
    )!!
    val back = quickestCourseOfAction(
        blizzards = forward.blizzards,
        width = width,
        height = height,
        startPosition = Coordinates(endX, endY),
        endPosition = Coordinates(startX, startY),
        ff = false
    )!!
    val forwardAgain = quickestCourseOfAction(
        blizzards = back.blizzards,
        width = width,
        height = height,
        startPosition = Coordinates(startX, startY),
        endPosition =  Coordinates(endX, endY),
        ff = false
    )!!

    return forward.moves.size + back.moves.size + forwardAgain.moves.size
}

fun day24Part2() {
    println("day24/part2/plants: \n${
        findQuickestLength(
            getResourceAsStream("/ski.gagar.aoc.aoc2022.day24/blizzards.txt").bufferedReader().lineSequence()
        )
    }")
}
