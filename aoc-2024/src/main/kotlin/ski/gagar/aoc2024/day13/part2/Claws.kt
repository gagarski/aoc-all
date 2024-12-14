package ski.gagar.aoc2024.day13.part2

import ski.gagar.aoc.util.math.LinearEquation
import ski.gagar.aoc.util.math.LinearEquationSystem
import ski.gagar.aoc.util.math.nf
import java.math.BigInteger

data class Button(val name: String, val deltaX: BigInteger, val deltaY: BigInteger, val price: BigInteger)
data class Prize(val x: BigInteger, val y: BigInteger)

data class ClawMachine(val buttons: List<Button>, val prize: Prize) {
    fun solve(): Map<Button, BigInteger>? {
        val xEq = LinearEquation(buttons.map { nf(it.deltaX) }, nf(prize.x))
        val yEq = LinearEquation(buttons.map { nf(it.deltaY) }, nf(prize.y))

        val sys = LinearEquationSystem(xEq, yEq)
        val solution = sys.solve() ?: return null

        if (solution.any { it.denom != BigInteger.ONE })
            return null

        val intSol = solution.map { it.num }

        return buttons.zip(intSol).toMap()
    }

    companion object {
        private val WELL_KNOWN_PRICES = mapOf(
            "A" to 3.toBigInteger(),
            "B" to 1.toBigInteger()
        )
        private val BUTTON_RE = """Button\s+(.*?)\s*:\s*X\+([0-9]+)\s*,\s*Y\+([0-9]+)""".toRegex()
        private val PRIZE_RE = """Prize\s*:\s*X=([0-9]+)\s*,\s*Y=([0-9]+)""".toRegex()
        private val A_LOT = BigInteger("10000000000000")

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
            val deltaX = match.groupValues[2].toBigInteger()
            val deltaY = match.groupValues[3].toBigInteger()
            val price = WELL_KNOWN_PRICES[name] ?: throw IllegalArgumentException("Unknown button $name")
            return Button(name, deltaX, deltaY, price)
        }

        private fun parsePrize(str: String): Prize? {
            val match = PRIZE_RE.matchEntire(str) ?: return null
            val x = match.groupValues[1].toBigInteger() + A_LOT
            val y = match.groupValues[2].toBigInteger() + A_LOT
            return Prize(x, y)
        }
    }
}

fun Map<Button, BigInteger>.total(): BigInteger = this.entries.sumOf { it.key.price * it.value }

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

fun nTokensOffset(lines: Sequence<String>): BigInteger {
    var sum = BigInteger.ZERO
    for (res in lines.groupedTogether()
        .map { cmLines ->
            ClawMachine.from(cmLines)
        }
        .map { it.solve() }
        .filterNotNull()
        .map { it.total() }) {
        sum += res
    }
    return sum
}