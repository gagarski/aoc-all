package ski.gagar.aoc2015.day6.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day3.part1.Coordinates
import kotlin.math.max
import kotlin.math.min

data class Coordinates(val x: Int, val y: Int)

class LightGrid(val width: Int = 1000, val height: Int = 1000) {
    private val lights = mutableMapOf<Coordinates, Int>()

    private fun coordinates(x: Int, y: Int): Coordinates {
        require(x in 0 until width)
        require(y in 0 until height)
        return Coordinates(x, y)
    }

    internal fun brighten(x: Int, y: Int, amount: Int = 1) {
        val brightness = lights[coordinates(x, y)] ?: 0

        val newBrightness = brightness + amount

        lights[coordinates(x, y)] = if (newBrightness <= 0) Int.MAX_VALUE else newBrightness
    }

    fun turnOn(x: Int, y: Int) {
        brighten(x, y)
    }

    internal fun dim(x: Int, y: Int, amount: Int = 1) {
        val newBrightness = (lights[coordinates(x, y)] ?: 0) - amount
        if (newBrightness <= 0) {
            lights.remove(coordinates(x, y))
        } else {
            lights[coordinates(x, y)] = newBrightness
        }
    }

    fun turnOff(x: Int, y: Int) {
        dim(x, y)
    }

    fun toggle(x: Int, y: Int) {
        brighten(x, y, 2)
    }

    internal fun setBrightness(x: Int, y: Int, level: Int) {
        if (level <= 0) {
            lights.remove(coordinates(x, y))
        } else {
            lights[coordinates(x, y)] = level

        }
    }

    fun bulkOp(xRange: IntRange, yRange: IntRange, apply: (x: Int, y: Int) -> Unit) {
        for (y in yRange) {
            for (x in xRange) {
                apply(x, y)
            }
        }
    }

    internal fun brighten(xRange: IntRange, yRange: IntRange, amount: Int) {
        bulkOp(xRange, yRange) { x, y ->
            brighten(x, y, amount)
        }
    }

    fun turnOn(xRange: IntRange, yRange: IntRange) {
        bulkOp(xRange, yRange) { x, y ->
            turnOn(x, y)
        }
    }

    internal fun dim(xRange: IntRange, yRange: IntRange, amount: Int) {
        bulkOp(xRange, yRange) { x, y ->
            dim(x, y, amount)
        }
    }

    fun turnOff(xRange: IntRange, yRange: IntRange) {
        bulkOp(xRange, yRange) { x, y ->
            turnOff(x, y)
        }
    }

    fun toggle(xRange: IntRange, yRange: IntRange) {
        bulkOp(xRange, yRange) { x, y ->
            toggle(x, y)
        }
    }

    internal fun setBrightness(xRange: IntRange, yRange: IntRange, level: Int) {
        bulkOp(xRange, yRange) { x, y ->
            setBrightness(x, y, level)
        }
    }



    fun isOn(x: Int, y: Int) = lights.contains(coordinates(x, y))

    fun getBrightness(x: Int, y: Int) = lights[coordinates(x, y)] ?: 0

    val onCount
        get() = lights.size

    val totalBrightness
        get() = lights.values.sum()

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

fun totalBrightness(commands: Sequence<String>): Int {
    val grid = LightGrid()
    grid.execProgram(commands)
    return grid.totalBrightness
}