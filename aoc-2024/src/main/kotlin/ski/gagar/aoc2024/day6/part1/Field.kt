package ski.gagar.aoc2024.day6.part1

import ski.gagar.aoc.util.Puzzle

data class Coordinates(val row: Int, val col: Int) {
    operator fun plus(other: Coordinates) = Coordinates(row + other.row, col + other.col)
    operator fun minus(other: Coordinates) = Coordinates(row - other.row, col - other.col)
}

enum class Direction {
    LEFT {
        override val delta = Coordinates(0, -1)
        override val cw
            get() = UP
    },
    RIGHT {
        override val delta = Coordinates(0, 1)
        override val cw
            get() = DOWN
    },
    UP {
        override val delta = Coordinates(-1, 0)
        override val cw
            get() = RIGHT
    },
    DOWN {
        override val delta = Coordinates(1, 0)
        override val cw
            get() = LEFT
    };

    abstract val delta: Coordinates
    abstract val cw: Direction
}

class Field(lines: Sequence<String>) {
    val height: Int
    val width: Int
    private val obstacles: Set<Coordinates>
    var direction: Direction
        private set
    var position: Coordinates
        private set

    init {
        var w: Int? = null
        var h = 0
        val obstacles = mutableSetOf<Coordinates>()
        var d: Direction? = null
        var p: Coordinates? = null

        for ((row, line) in lines.withIndex()) {
            if (null != w) {
                require(line.length == w)
            }
            w = line.length
            h++
            for ((column, char) in line.withIndex()) {
                when (char) {
                    '#' -> obstacles.add(Coordinates(row, column))
                    '^' -> {
                        require(d == null && p == null) {
                            "Repeating start position"
                        }
                        p = Coordinates(row, column)
                        d = Direction.UP
                    }
                    '>' -> {
                        require(d == null && p == null) {
                            "Repeating start position"
                        }
                        p = Coordinates(row, column)
                        d = Direction.RIGHT
                    }
                    '<' -> {
                        require(d == null && p == null) {
                            "Repeating start position"
                        }
                        p = Coordinates(row, column)
                        d = Direction.LEFT
                    }
                    'v' -> {
                        require(d == null && p == null) {
                            "Repeating start position"
                        }
                        p = Coordinates(row, column)
                        d = Direction.DOWN
                    }
                    '.' -> {}
                    else -> throw IllegalArgumentException("Illegal char $char")
                }
            }
        }
        require(w != null)
        require(p != null)
        require(d != null)
        width = w
        height = h
        direction = d
        position = p
        this.obstacles = obstacles
    }

    fun isInside() = position.row in 0..<width  && position.col in 0..<height

    private fun canGoTo(direction: Direction) = position + direction.delta !in obstacles


    fun step(): Coordinates {
        val origDir = direction

        while (!canGoTo(direction)) {
            direction = direction.cw

            if (direction == origDir) {
                throw IllegalStateException("Stuck")
            }
        }
        position += direction.delta
        return position
    }

    fun walkWhileInside(): Set<Coordinates> {
        val visited = mutableSetOf<Coordinates>()

        while (isInside()) {
            visited += position
            step()
        }
        return visited
    }
}

fun nVisitedCells(lines: Sequence<String>): Int {
    val field = Field(lines)
    return field.walkWhileInside().size
}