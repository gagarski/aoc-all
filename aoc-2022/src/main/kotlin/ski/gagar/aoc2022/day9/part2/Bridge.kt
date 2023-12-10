package ski.gagar.aoc2022.day9.part2

import ski.gagar.aoc.util.getResourceAsStream
import kotlin.math.absoluteValue
import kotlin.math.sign

class Bridge(val start: Pair<Int, Int> = 0 to 0, val nKnots: Int = 10) {
    val knots = MutableList(nKnots) { start }

    private val tailHistory = mutableSetOf<Pair<Int, Int>>()

    val head
        get() = knots[0]

    fun knotAt(i: Int) = knots[i]

    init {
        require(nKnots >= 1)
        tailHistory.add(knots[knots.size - 1])
    }

    private fun followUpTail(i: Int, target: Pair<Int, Int>): Boolean {
        val tail = this.knots[i]
        val dx = (target.first - tail.first).absoluteValue
        val dy = (target.second - tail.second).absoluteValue


        if (i != 0 && dx <= 1 && dy <= 1) return false
        if (dx == 0 && dy == 0) return false

        val dirX = (target.first - tail.first).sign
        val dirY = (target.second - tail.second).sign

        knots[i] = tail.first + dirX to tail.second + dirY

        if (i == knots.size - 1) {
            tailHistory.add(knots[i])
        }
        return true
    }

    private fun followUpTails(headTarget: Pair<Int, Int>): Boolean {
        if (head == headTarget)
            return false
        var target = headTarget
        var res = false
        for (i in 0 until knots.size) {
            res = followUpTail(i, target) || res
            target = knots[i]
        }
        return res
    }

    fun moveTo(coords: Pair<Int, Int>) {
        while (true) {
            if (!followUpTails(coords)) break
        }
    }

    fun up() = moveTo(knots[0].first to knots[0].second - 1)
    fun down() = moveTo(knots[0].first to knots[0].second + 1)
    fun right() = moveTo(knots[0].first + 1 to knots[0].second)
    fun left() = moveTo(knots[0].first - 1 to knots[0].second)

    val tailHistorySize
        get() = tailHistory.size
}


enum class Direction(val dx: Int, val dy: Int) {
    U(0, -1), R(1, 0), L(-1, 0), D(0, 1);

    fun Bridge.move(amount: Int) {
        moveTo(head.first + dx * amount to head.second + dy * amount)
    }
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
    val bridge = Bridge(50 to 50)
    for (string in strings) {
        with(parseMove(string)) {
            bridge.move()
        }
    }

    return bridge.tailHistorySize
}

fun Bridge.toString(width: Int, height: Int): String {
    val knots = (0 until  nKnots).map { it to knotAt(it) }.toMap()
    val knotsRev = sequence {
        for ((n, coord) in knots.toList().reversed()) {
            yield(coord to n)
        }
    }.toMap()
    return buildString {
        for (y in 0 until height) {
            for (x in 0 until width) {
                val knot = knotsRev[x to y]
                when {
                    knot == 0 -> append("h")
                    knot != null -> append(knot)
                    start == (x to y) -> append("s")
                    else -> append(".")
                }
            }
            append("\n")
        }
    }
}