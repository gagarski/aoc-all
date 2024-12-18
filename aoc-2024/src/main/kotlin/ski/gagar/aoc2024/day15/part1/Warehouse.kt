package ski.gagar.aoc2024.day15.part1

import kotlin.math.abs

data class Coordinates(val row: Int, val column: Int) {
    operator fun plus(coordinate: Coordinates): Coordinates =
        Coordinates(row + coordinate.row, column + coordinate.column)
    operator fun minus(coordinate: Coordinates) =
        Coordinates(row - coordinate.row, column - coordinate.column)

    val gps: Int
        get() = GPS_ROW * row + column

    companion object {
        const val GPS_ROW = 100
    }

    object Deltas {
        val TOP = Coordinates(-1, 0)
        val RIGHT = Coordinates(0, 1)
        val BOTTOM = Coordinates(1, 0)
        val LEFT = Coordinates(0, -1)
    }
}

class Warehouse(lines: Sequence<String>) {
    val width: Int
    val height: Int
    val walls: Set<Coordinates>
    val boxes_: MutableSet<Coordinates>
    var lanternfish: Coordinates
        private set

    val boxes: Set<Coordinates>
        get() = boxes_

    init {
        var w: Int? = null
        var h = 0
        val walls = mutableSetOf<Coordinates>()
        val boxes = mutableSetOf<Coordinates>()
        var lanternfish: Coordinates? = null

        for ((row, line) in lines.withIndex()) {
            if (line.isEmpty())
                break
            h++
            for ((column, char) in line.withIndex()) {
                if (null != w) {
                    require(line.length == w)
                }
                w = line.length
                when (char) {
                    '#' -> walls.add(Coordinates(row, column))
                    'O' -> boxes.add(Coordinates(row, column))
                    '@' -> {
                        require(lanternfish == null)
                        lanternfish = Coordinates(row, column)
                    }
                    '.' -> {}
                    else -> throw IllegalArgumentException("Illegal cell $char")
                }
            }
        }

        require(w != null)
        require(h != 0)
        require(lanternfish != null)
        this.width = w
        this.height = h
        this.walls = walls
        this.boxes_ = boxes
        this.lanternfish = lanternfish
    }

    operator fun contains(coordinates: Coordinates): Boolean =
        coordinates.row in 0 ..< height && coordinates.column in 0 ..< width

    private fun move(delta: Coordinates): Boolean {
        require(abs(delta.row + delta.column) == 1)
        var toCheck = lanternfish + delta
        val boxesToRemove = mutableSetOf<Coordinates>()
        val boxesToAdd = mutableSetOf<Coordinates>()

        fun commit() {
            lanternfish += delta
            boxes_.removeAll(boxesToRemove)
            boxes_.addAll(boxesToAdd)
        }

        while (true) {
            when (toCheck) {
                !in this, in walls -> return false
                in boxes_ -> {
                    val next = toCheck + delta
                    boxesToRemove.add(toCheck)
                    boxesToAdd.add(next)
                    toCheck = next
                }
                else -> {
                    commit()
                    return true
                }
            }
        }
    }

    fun left() = move(Coordinates.Deltas.LEFT)

    fun right() = move(Coordinates.Deltas.RIGHT)

    fun up() = move(Coordinates.Deltas.TOP)

    fun down() = move(Coordinates.Deltas.BOTTOM)

    override fun toString() = buildString {
        for (row in 0 until height) {
            for (col in 0 until width) {
                when (Coordinates(row, col)) {
                    lanternfish -> append('@')
                    in boxes_ -> append('O')
                    in walls -> append('#')
                    else -> append('.')
                }
            }
            append('\n')
        }
    }

}

enum class Move {
    UP {
        override fun perform(warehouse: Warehouse) = warehouse.up()
    }, LEFT {
        override fun perform(warehouse: Warehouse) = warehouse.left()
    }, DOWN {
        override fun perform(warehouse: Warehouse) = warehouse.down()
    }, RIGHT {
        override fun perform(warehouse: Warehouse) = warehouse.right()
    };

    abstract fun perform(warehouse: Warehouse): Boolean
}

fun Warehouse.performMove(move: Move) {
    move.perform(this)
}

fun parseMoves(lines: Sequence<String>): List<Move> =
    lines.flatMap { it.asSequence().map {
        when (it) {
            '^' -> Move.UP
            '>' -> Move.RIGHT
            'v' -> Move.DOWN
            '<' -> Move.LEFT
            else -> throw IllegalArgumentException("Illegal move $it")
        }
    } }.toList()


fun sumBoxGps(lines: Sequence<String>): Int {
    val itr = lines.iterator()
    val warehouse = Warehouse(itr.asSequence())
    val moves = parseMoves(itr.asSequence())
    for (move in moves) {
        warehouse.performMove(move)
    }


    return warehouse.boxes.sumOf { it.gps }
}