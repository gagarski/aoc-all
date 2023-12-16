package ski.gagar.aoc2023.day14.part1

enum class Direction {
    NORTH,
    WEST,
    SOUTH,
    EAST
}

data class Rock(val x: Int, val y: Int)

class ReflectorDish(
    val roundedRocks: Set<Rock>,
    val cubeRocks: Set<Rock>,
    val width: Int,
    val height: Int) {

    init {
        require(roundedRocks.all { it.x in 0 until width })
        require(cubeRocks.all { it.x in 0 until width })
        require(roundedRocks.all { it.y in 0 until height })
        require(cubeRocks.all { it.y in 0 until height })
        require(cubeRocks.intersect(roundedRocks).isEmpty())
    }

    private fun Rock.loadOn(dir: Direction) =
        when (dir) {
            Direction.NORTH -> height - y
            Direction.SOUTH -> y + 1
            Direction.WEST -> width - x
            Direction.EAST -> x + 1
        }

    fun loadOn(dir: Direction) = roundedRocks.sumOf { it.loadOn(dir) }

    private fun Rock.next(dir: Direction) =
        when (dir) {
            Direction.NORTH -> Rock(x, y - 1)
            Direction.SOUTH -> Rock(x, y + 1)
            Direction.WEST -> Rock(x - 1, y)
            Direction.EAST -> Rock(x + 1, y)
        }

    private fun Rock.limit(dir: Direction) =
        when (dir) {
            Direction.NORTH -> Rock(x, -1)
            Direction.SOUTH -> Rock(x, height)
            Direction.WEST -> Rock(-1, y)
            Direction.EAST -> Rock(width, y)
        }

    private fun Rock.roll(dir: Direction, newRoundedRocks: Set<Rock>): Rock {
        var current = this

        val limit = limit(dir)

        while (true) {
            val next = current.next(dir)

            if (next == limit || next in newRoundedRocks || next in cubeRocks)
                return current
            current = next
        }
    }

    private val Direction.yScanRange
        get() = when (this) {
            Direction.NORTH -> 0 until width
            Direction.SOUTH -> width - 1 downTo 0
            Direction.WEST -> 0 until height
            Direction.EAST -> 0 until height
        }

    private val Direction.xScanRange
        get() = when (this) {
            Direction.NORTH -> 0 until width
            Direction.SOUTH -> 0 until width
            Direction.WEST -> 0 until height
            Direction.EAST -> height - 1 downTo 0
        }

    fun tilt(dir: Direction): ReflectorDish {
        val newRoundedRocks = mutableSetOf<Rock>()

        for (y in dir.yScanRange) {
            for (x in dir.xScanRange) {
                val rock = Rock(x, y)
                if (rock !in roundedRocks) continue

                val rolled = rock.roll(dir, newRoundedRocks)
                newRoundedRocks.add(rolled)
            }
        }

        return ReflectorDish(newRoundedRocks, cubeRocks, width, height)
    }

    override fun toString(): String = buildString {
        for (y in 0 until height) {
            for (x in 0 until width) {
                val rock = Rock(x, y)

                when (rock) {
                    in roundedRocks -> append('O')
                    in cubeRocks -> append('#')
                    else -> append('.')
                }
            }
            append('\n')
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReflectorDish

        if (roundedRocks != other.roundedRocks) return false
        if (cubeRocks != other.cubeRocks) return false
        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roundedRocks.hashCode()
        result = 31 * result + cubeRocks.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }


    companion object {
        fun from(lines: Sequence<String>): ReflectorDish {
            var w: Int? = null
            var h = 0
            val roundedRocks = mutableSetOf<Rock>()
            val cubeRocks = mutableSetOf<Rock>()
            for ((y, line) in lines.withIndex()) {
                require(w == null || line.length == w)
                w = line.length
                for ((x, char) in line.withIndex()) {
                    when (char) {
                        '.' -> {}
                        '#' -> cubeRocks.add(Rock(x, y))
                        'O' -> roundedRocks.add(Rock(x, y))
                        else -> throw IllegalArgumentException("Wrong char $char at ($x, $y)")
                    }
                }
                h++
            }
            require(h != 0)
            check(w != null)
            return ReflectorDish(roundedRocks, cubeRocks, w, h)
        }
    }
}

fun loadAfterTilt(lines: Sequence<String>,
                  tiltDir: Direction = Direction.NORTH,
                  loadDir: Direction = tiltDir): Int {
    val dish = ReflectorDish.from(lines)
    val tilted = dish.tilt(tiltDir)
    return tilted.loadOn(loadDir)
}