package ski.gagar.aoc2022.day17.part1

import ski.gagar.aoc.util.getResourceAsStream
import java.util.BitSet


private fun String.isEmptyLine() = all { it == '.' }

class TetrisShape(string: String) {
    val width: Int
    val height: Int
    val pattern: BitSet = BitSet()

    init {
        val lines = string.lines()
        val trimUp = lines.asSequence().takeWhile { it.isEmptyLine() }.count()
        val trimDown = lines.asSequence().drop(trimUp).dropWhile { !it.isEmptyLine() }.count()

        height = lines.size - trimUp - trimDown

        val trimLeft = lines.asSequence().map { it.indexOf('#') }.min()
        val rightMostRock = lines.asSequence().map { it.lastIndexOf('#') }.max()

        width = rightMostRock + 1 - trimLeft

        for ((y, line) in lines.drop(trimUp).take(height).withIndex()) {
            for (x in 0 until width) {
                val char = line.getOrNull(trimLeft + x) ?: '.'

                pattern.set(index(x, y), char == '#')
            }
        }
    }

    private fun index(x: Int, y: Int) = y * width + x

    fun isSet(x: Int, y: Int) = pattern[index(x, y)]

    override fun toString(): String = (0 until height).asSequence().map { y ->
        (0 until width).map { x -> if (pattern[index(x, y)]) '#' else '.' }.joinToString("")
    }.joinToString("\n")

}

val DASH = TetrisShape("####")
val PLUS = TetrisShape("""
    .#.
    ###
    .#.
""".trimIndent())
val J = TetrisShape("""
    ..#
    ..#
    ###
""".trimIndent())

val I = TetrisShape("""
    #
    #
    #
    #
""".trimIndent())

val SQUARE = TetrisShape("""
    ##
    ##
""".trimIndent())

val FIGURES_ROUND_ROBIN = listOf(
    DASH, PLUS, J, I, SQUARE
)

enum class Move {
    LEFT, RIGHT
}

data class Coordinates(val x: Int, val y: Int)

class TetrisShaft(val moves: List<Move>, val width: Int = 7, val shapes: List<TetrisShape> = FIGURES_ROUND_ROBIN,
                  val initialX: Int = 2, val initialY: Int = 3) {
    private var moveIndex = 0
    private var shapeIndex = 0

    private val content = BitSet()
    val maxHeight: Int
        get() {
            val prevBitSet = content.previousSetBit(content.size())

            if (prevBitSet < 0) return 0

            return prevBitSet / width + 1
        }
    var shapeTopLeft: Coordinates
        private set

    init {
        require(moves.isNotEmpty())
        require(shapes.isNotEmpty())
        shapeTopLeft = initialShapePosition()
    }

    private fun initialShapePosition() = Coordinates(initialX, (initialY + currentShape.height - 1 + maxHeight))

    private val currentShape
        get() = shapes[shapeIndex]

    private val currentMove
        get() = moves[moveIndex]

    private fun index(x: Int, y: Int) = y * width + x

    private val shapeBottomSurface
        get() = sequence {
            x@for (x in 0 until currentShape.width) {
                y@for (y in currentShape.height - 1 downTo 0) {
                    if (currentShape.isSet(x, y)) {
                        yield(shapeTopLeft.y - y)
                        continue@x
                    }
                }
                throw IllegalStateException("Should not happen")
            }
        }.toList()


    private val canMoveDown: Boolean
        get() = shapeBottomSurface.asSequence().withIndex().all { (index, y) ->
            y - 1 >= 0 && !content[index(shapeTopLeft.x + index, y - 1)]
        }

    private fun moveDown() {
        shapeTopLeft = Coordinates(shapeTopLeft.x, shapeTopLeft.y - 1)
    }

    private fun moveDownIfPossible(): Boolean {
        if (!canMoveDown) return false
        moveDown()
        return true
    }

    private val shapeRightSurface
        get() = sequence {
            y@for (y in currentShape.height - 1 downTo 0) {
                x@for (x in currentShape.width - 1 downTo  0) {
                    if (currentShape.isSet(x, y)) {
                        yield(shapeTopLeft.x + x)
                        continue@y
                    }
                }
                throw IllegalStateException("Should not happen")
            }
        }.toList()


    private val canMoveRight: Boolean
        get() = shapeRightSurface.asSequence().withIndex().all { (index, x) ->
            x + 1 < width && !content[index(x + 1, shapeTopLeft.y - currentShape.height + 1 + index)]
        }

    private fun moveRight() {
        shapeTopLeft = Coordinates(shapeTopLeft.x + 1, shapeTopLeft.y)
    }

    private fun moveRightIfPossible(): Boolean {
        if (!canMoveRight) return false
        moveRight()
        return true
    }

    private val shapeLeftSurface
        get() = sequence {
            y@for (y in currentShape.height - 1 downTo 0) {
                x@for (x in 0 until  width) {
                    if (currentShape.isSet(x, y)) {
                        yield(shapeTopLeft.x + x)
                        continue@y
                    }
                }
                throw IllegalStateException("Should not happen")
            }
        }.toList()

    private val canMoveLeft: Boolean
        get() = shapeLeftSurface.asSequence().withIndex().all { (index, x) ->
            x - 1 >= 0 && !content[index(x - 1, shapeTopLeft.y - currentShape.height + 1 + index)]
        }

    private fun moveLeft() {
        shapeTopLeft = Coordinates(shapeTopLeft.x - 1, shapeTopLeft.y)
    }

    private fun moveLeftIfPossible(): Boolean {
        if (!canMoveLeft) return false
        moveLeft()
        return true
    }

    private fun doMoveIfPossible(): Boolean {
        when (currentMove) {
            Move.LEFT -> moveLeftIfPossible()
            Move.RIGHT -> moveRightIfPossible()
        }
        moveIndex = (moveIndex + 1) % (moves.size)
        return moveDownIfPossible()
    }

    private fun persistShapeAndSwitchToNext() {
        for (y in 0 until currentShape.height) {
            for (x in 0 until currentShape.width) {
                if (currentShape.isSet(x, y)) {
                    content.set(index(shapeTopLeft.x + x, shapeTopLeft.y - y))
                }
            }
        }
        shapeIndex = (shapeIndex + 1) % (shapes.size)
        shapeTopLeft = initialShapePosition()
    }

    fun runCurrentShape() {
        while (true) {
            if (!doMoveIfPossible()) {
                break
            }
        }
        persistShapeAndSwitchToNext()
    }


    private fun belongsToShape(x: Int, y: Int): Boolean {
        if (x !in shapeTopLeft.x until shapeTopLeft.x + currentShape.width) return false
        if (y !in shapeTopLeft.y downTo  shapeTopLeft.y - currentShape.height + 1) return false

        return currentShape.isSet(x - shapeTopLeft.x, shapeTopLeft.y - y)
    }

    override fun toString(): String {
        val height = maxOf(maxHeight, shapeTopLeft.y + 1)

        return buildString {
            for (y in height - 1 downTo 0) {
                append('|')
                for (x in 0 until width) {
                    when {
                        content[index(x, y)] -> append('#')
                        belongsToShape(x, y) -> append('@')
                        else -> append('.')
                    }
                }
                append('|')
                append('\n')
            }
            append('+')
            for (x in 0 until width) {
                append('-')
            }
            append('+')
        }
    }
}

fun String.toMoves() = map {
    when (it) {
        '>' -> Move.RIGHT
        '<' -> Move.LEFT
        else -> throw IllegalStateException("Unknown move")
    }
}

fun getMaxHeight(input: String, nMoves: Int = 2022): Int {
    val tetris = TetrisShaft(input.toMoves())

    for (i in 0 until nMoves) {
        tetris.runCurrentShape()
    }
    return tetris.maxHeight
}

fun day17Part1() {
    println("day17/part1/tetris: ${
        getMaxHeight(
            getResourceAsStream("/ski.gagar.aoc.aoc2022.day17/tetris.txt").bufferedReader().lineSequence().first()
        )
    }")
}
