package ski.gagar.aoc2022.day9.part1

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day8.part2.maxScenicScore
import kotlin.math.absoluteValue
import kotlin.math.sign

class Bridge(val start: Pair<Int, Int> = 0 to 0) {
    var head = start
        private set
    var tail = start
        private set

    private val tailHistory = mutableSetOf<Pair<Int, Int>>()

    init {
        tailHistory.add(tail)
    }

    private fun followUpTail(): Boolean {
        val dx = (head.first - tail.first).absoluteValue
        val dy = (head.second - tail.second).absoluteValue


        if (dx <= 1 && dy <= 1) return false

        val dirX = (head.first - tail.first).sign
        val dirY = (head.second - tail.second).sign

        this.tail = tail.first + dirX to tail.second + dirY
        tailHistory.add(tail)
        return true
    }

    fun moveTo(coords: Pair<Int, Int>) {
        head = coords
        while (true) {
            if (!followUpTail()) break
        }
    }

    fun up(amount: Int) = moveTo(head.first to head.second - amount)
    fun down(amount: Int) = moveTo(head.first to head.second + amount)
    fun right(amount: Int) = moveTo(head.first + amount to head.second)
    fun left(amount: Int) = moveTo(head.first - amount to head.second)

    val tailHistorySize
        get() = tailHistory.size
}


enum class Direction(val dx: Int, val dy: Int) {
    U(0, -1), R(1, 0), L(-1, 0), D(0, 1);

    fun Bridge.move(amount: Int) = moveTo(head.first + dx * amount to head.second + dy * amount)
}

data class Move(val dir: Direction, val amount: Int) {
    fun Bridge.move() {
        with (dir) {
            move(amount)
        }
    }
}

private val MOVE_RE = """([LRDU])\s+([0-9]+)""".toRegex()

fun parseMove(str: String): Move {
    val match = MOVE_RE.matchEntire(str)
    require(match != null)
    val dir = Direction.valueOf(match.groups[1]!!.value)
    val amount = match.groups[2]!!.value.toInt()

    return Move(dir, amount)
}

fun doMoves(strings: Sequence<String>): Int {
    val bridge = Bridge()

    for (string in strings) {
        with (parseMove(string)) {
            bridge.move()
        }
    }

    return bridge.tailHistorySize
}

