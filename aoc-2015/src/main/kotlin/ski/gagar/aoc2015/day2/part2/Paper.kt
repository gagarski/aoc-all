package ski.gagar.aoc2015.day2.part2

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

    val minSectionPerimeter =
        2 * minOf(h + w, l + w, l + h)

    val volume
        get() = l * h * w

    val ribbonLength = minSectionPerimeter + volume

    companion object {
        fun parse(repr: String): Box {
            val parts = repr.split("x")
            require(parts.size == 3)
            return Box(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }
    }
}

fun getRibbonLengthForLine(string: String): Int =
    Box.parse(string).ribbonLength

fun getTotalRibbonLength(lines: Sequence<String>): Int =
    lines.sumOf { getRibbonLengthForLine(it) }

