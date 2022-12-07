package ski.gagar.aoc2015.day2.part1

import ski.gagar.aoc.util.getResourceAsStream

data class Box(val l: Int, val h: Int, val w: Int) {
    init {
        require(l > 0)
        require(h > 0)
        require(w > 0)
    }

    private val hw
        get() = h * w

    private val lw
        get() = l * w

    private val lh
        get() = l * h

    val surfaceArea
        get() = 2 * hw + 2 * lw + 2 * lh

    val extraArea
        get() = minOf(hw, lw, lh)

    val wrapArea = surfaceArea + extraArea

    companion object {
        fun parse(repr: String): Box {
            val parts = repr.split("x")
            require(parts.size == 3)
            return Box(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }
    }
}

fun getAreaForLine(string: String): Int =
    Box.parse(string).wrapArea

fun getTotalArea(lines: Sequence<String>): Int =
    lines.sumOf { getAreaForLine(it) }


fun day2Part1() {
    println(
        "day2/part1/paper: ${
            getTotalArea(getResourceAsStream("/ski.gagar.aoc.aoc2015.day2/paper.txt").bufferedReader().lineSequence())
        }"
    )
}
