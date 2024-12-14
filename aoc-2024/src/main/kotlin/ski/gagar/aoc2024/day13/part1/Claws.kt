package ski.gagar.aoc2024.day13.part1

import ski.gagar.aoc.util.math.LinearEquation
import ski.gagar.aoc.util.math.LinearEquationSystem
import ski.gagar.aoc.util.math.nf
import java.math.BigInteger

data class Button(val name: String, val deltaX: Int, val deltaY: Int, val price: Int)
data class Prize(val x: Int, val y: Int)

data class ClawMachine(val buttons: List<Button>, val prize: Prize) {
    fun solve(): Map<Button, Int>? {
        val xEq = LinearEquation(buttons.map { nf(it.deltaX) }, nf(prize.x))
        val yEq = LinearEquation(buttons.map { nf(it.deltaY) }, nf(prize.y))

        val sys = LinearEquationSystem(xEq, yEq)
        val solution = sys.solve() ?: return null

        if (solution.any { it.denom != BigInteger.ONE })
            return null

        val intSol = solution.map { it.num.toInt() }

        return buttons.zip(intSol).toMap()
    }

    companion object {
        private val WELL_KNOWN_PRICES = mapOf(
            "A" to 3,
            "B" to 1
        )
        private val BUTTON_RE = """Button\s+(.*?)\s*:\s*X\+([0-9]+)\s*,\s*Y\+([0-9]+)""".toRegex()
        private val PRIZE_RE = """Prize\s*:\s*X=([0-9]+)\s*,\s*Y=([0-9]+)""".toRegex()

        fun from(lines: List<String>): ClawMachine {
            val buttons = mutableListOf<Button>()
            var prize: Prize? = null

            for (line in lines) {
                val btn = parseButton(line)
                if (btn != null) {
                    buttons.add(btn)
                    continue
                }
                val prz = parsePrize(line)

                if (prz != null) {
                    require(prize == null) { "Prize $prize already present, yet $prz occured in the input"}
                    prize = prz
                }
            }
            require(prize != null) { "Prize not found" }
            require(buttons.size == 2)
            return ClawMachine(buttons, prize)
        }

        private fun parseButton(str: String): Button? {
            val match = BUTTON_RE.matchEntire(str) ?: return null
            val name = match.groupValues[1]
            val deltaX = match.groupValues[2].toInt()
            val deltaY = match.groupValues[3].toInt()
            val price = WELL_KNOWN_PRICES[name] ?: throw IllegalArgumentException("Unknown button $name")
            return Button(name, deltaX, deltaY, price)
        }

        private fun parsePrize(str: String): Prize? {
            val match = PRIZE_RE.matchEntire(str) ?: return null
            val x = match.groupValues[1].toInt()
            val y = match.groupValues[2].toInt()
            return Prize(x, y)
        }
    }
}

fun Map<Button, Int>.total() = this.entries.sumOf { it.key.price * it.value }

fun Sequence<String>.groupedTogether(): Sequence<List<String>> = sequence {
    var acc = mutableListOf<String>()

    for (line in this@groupedTogether) {
        if (line.isBlank() && acc.isNotEmpty()) {
            yield(acc)
            acc = mutableListOf()
        }
        acc += line
    }

    if (acc.isNotEmpty())
        yield(acc)
}

fun nTokens(lines: Sequence<String>) =
    lines.groupedTogether()
        .map { cmLines ->
            ClawMachine.from(cmLines)
        }
        .map { it.solve() }
        .filterNotNull()
        .map { it.total() }
        .sum()