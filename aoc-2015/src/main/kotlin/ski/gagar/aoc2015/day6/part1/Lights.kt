package ski.gagar.aoc2015.day6.part1

import ski.gagar.aoc.util.getResourceAsStream
import java.util.*
import kotlin.math.max
import kotlin.math.min

class LightGrid(val width: Int = 1000, val height: Int = 1000) {
    private val lightsOn = BitSet(width * height)

    private fun index(x: Int, y: Int): Int {
        require(x in 0 until width)
        require(y in 0 until height)
        return y * height + x
    }

    private val fullRow = (0 until width)

    private val IntRange.isFullRow
        get() = this == fullRow

    fun turnOn(x: Int, y: Int) {
        lightsOn.set(index(x, y))
    }

    fun turnOff(x: Int, y: Int) {
        lightsOn.clear(index(x, y))
    }

    fun toggle(x: Int, y: Int) {
        lightsOn.flip(index(x, y))
    }

    private fun bulkOp(xRange: IntRange, yRange: IntRange, apply: (first: Int, last: Int) -> Unit) {
        if (xRange.isFullRow) {
            apply(index(xRange.first, yRange.first), index(xRange.last, yRange.last) + 1)
        } else {
            for (row in yRange) {
                apply(index(xRange.first, row), index(xRange.last, row) + 1)
            }
        }
    }

    fun turnOn(xRange: IntRange, yRange: IntRange) {
        bulkOp(xRange, yRange) { first, last ->
            lightsOn.set(first, last)
        }
    }

    fun turnOff(xRange: IntRange, yRange: IntRange) {
        bulkOp(xRange, yRange) { first, last ->
            lightsOn.clear(first, last)
        }
    }

    fun toggle(xRange: IntRange, yRange: IntRange) {
        bulkOp(xRange, yRange) { first, last ->
            lightsOn.flip(first, last)
        }
    }

    fun isOn(x: Int, y: Int) = lightsOn[index(x, y)]

    val onCount
        get() = lightsOn.cardinality()

    override fun toString(): String =
        (0 until height).map { y ->
            (0 until width).map { x ->
                if (isOn(x, y)) "X" else "."
            }.joinToString("")
        }.joinToString("\n")
}

enum class OpCode(val mnemonic: String) {
    TURN_ON("turn on") {
        override fun LightGrid.apply(xRange: IntRange, yRange: IntRange) {
            turnOn(xRange, yRange)
        }

    },
    TURN_OFF("turn off") {
        override fun LightGrid.apply(xRange: IntRange, yRange: IntRange) {
            turnOff(xRange, yRange)
        }
    },
    TOGGLE("toggle") {
        override fun LightGrid.apply(xRange: IntRange, yRange: IntRange) {
            toggle(xRange, yRange)
        }
    };

    abstract fun LightGrid.apply(xRange: IntRange, yRange: IntRange)

    companion object {
        private val byMnemonic = values().associateBy { it.mnemonic }
        fun fromMnemonic(mnemonic: String): OpCode =
            byMnemonic[mnemonic] ?: throw IllegalArgumentException("Unknown mnemonic ${mnemonic}")
    }
}

data class Command(val opCode: OpCode, val xRange: IntRange, val yRange: IntRange) {
    fun exec(grid: LightGrid) {
        with (opCode) {
            grid.apply(xRange, yRange)
        }
    }

    companion object {
        val CMD_RE =
            """(?<opCode>turn on|turn off|toggle) (?<xStart>[0-9]+),(?<yStart>[0-9]+) through (?<xEnd>[0-9]+),(?<yEnd>[0-9]+)""".toRegex()

        private fun MatchNamedGroupCollection.intGroup(name: String): Int =
            this[name]!!.value.toInt()

        fun parseCommand(str: String): Command {
            val match = CMD_RE.matchEntire(str)
            require(match != null) {
                "command $str is invalid"
            }
            val groups = match.groups as MatchNamedGroupCollection

            val opCode = OpCode.fromMnemonic(groups["opCode"]!!.value)
            val xStart = min(groups.intGroup("xStart"), groups.intGroup("xEnd"))
            val xEnd = max(groups.intGroup("xStart"), groups.intGroup("xEnd"))
            val yStart = min(groups.intGroup("yStart"), groups.intGroup("yEnd"))
            val yEnd = max(groups.intGroup("yStart"), groups.intGroup("yEnd"))

            return Command(opCode, xStart..xEnd, yStart..yEnd)
        }
    }
}

fun LightGrid.execProgram(commands: Sequence<String>) {
    for (c in commands) {
        val cmd = Command.parseCommand(c)
        cmd.exec(this)
    }
}

fun nBulbs(commands: Sequence<String>): Int {
    val grid = LightGrid()
    grid.execProgram(commands)
    return grid.onCount
}
