package ski.gagar.aoc2024.day15.part2

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

data class Box(val left: Coordinates, val right: Coordinates) {
    val gps: Int
        get() = left.gps

    operator fun plus(coordinate: Coordinates) = Box(left + coordinate, right + coordinate)
}

class Warehouse(lines: Sequence<String>) {
    val width: Int
    val height: Int
    val walls: Set<Coordinates>
    val boxes_: MutableSet<Box>
    var lanternfish: Coordinates
        private set

    val boxes: Set<Box>
        get() = boxes_

    init {
        var w: Int? = null
        var h = 0
        val walls = mutableSetOf<Coordinates>()
        val boxes = mutableSetOf<Box>()
        var lanternfish: Coordinates? = null

        for ((row, line) in lines.withIndex()) {
            if (line.isEmpty())
                break
            h++
            for ((column, char) in line.withIndex()) {
                if (null != w) {
                    require(line.length * 2 == w)
                }
                w = line.length * 2
                when (char) {
                    '#' ->{
                        walls.add(Coordinates(row, column * 2))
                        walls.add(Coordinates(row, column * 2 + 1))
                    }
                    'O' -> {
                        val l = Coordinates(row, column * 2)
                        val r = Coordinates(row, column * 2 + 1)
                        val box = Box(l, r)
                        boxes.add(box)
                    }
                    '@' -> {
                        require(lanternfish == null)
                        lanternfish = Coordinates(row, column * 2)
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
        var toCheck = listOf(lanternfish + delta)
        val boxesToRemove = mutableSetOf<Box>()
        val boxesToAdd = mutableSetOf<Box>()
        val boxesByCell = boxesByCell()

        fun commit() {
            lanternfish += delta
            boxes_.removeAll(boxesToRemove)
            boxes_.addAll(boxesToAdd)
        }

        while (true) {
            when {
                toCheck.any { it !in this || it in walls } -> return false
                toCheck.all { it !in boxesByCell } -> {
                    commit()
                    return true
                }
                else -> {
                    val boxesOnWay = toCheck.mapNotNull { boxesByCell[it] }.toSet()
                    boxesToRemove.addAll(boxesOnWay)
                    boxesToAdd.addAll(boxesOnWay.map { it + delta })
                    if (delta.row != 0) {
                        toCheck =
                            (boxesOnWay.asSequence().map { it.left + delta } +
                                    boxesOnWay.asSequence().map { it.right + delta }).toList()
                    } else if (delta.column == -1) {
                        toCheck =
                            boxesOnWay.asSequence().map { it.left + delta }.toList()
                    } else {
                        toCheck =
                            boxesOnWay.asSequence().map { it.right + delta }.toList()
                    }
                }
            }
        }
    }

    fun left() = move(Coordinates.Deltas.LEFT)

    fun right() = move(Coordinates.Deltas.RIGHT)

    fun up() = move(Coordinates.Deltas.TOP)

    fun down() = move(Coordinates.Deltas.BOTTOM)

    override fun toString() = buildString {
        val boxesByCell = boxesByCell()
        for (row in 0 until height) {
            for (col in 0 until width) {
                when (val c = Coordinates(row, col)) {
                    lanternfish -> append('@')
                    in boxesByCell -> {
                        val box = boxesByCell[c]!!
                        if (c == box.left)
                            append('[')
                        else
                            append(']')
                    }
                    in walls -> append('#')
                    else -> append('.')
                }
            }
            append('\n')
        }
    }


    private fun boxesByCell() =
        (boxes.asSequence().map { it.left to it} + boxes.asSequence().map { it.right to it }).toMap()
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


fun sumBoxGpsWide(lines: Sequence<String>): Int {
    val itr = lines.iterator()
    val warehouse = Warehouse(itr.asSequence())
    val moves = parseMoves(itr.asSequence())
    for (move in moves) {
        warehouse.performMove(move)
    }

    return warehouse.boxes.sumOf { it.gps }
}