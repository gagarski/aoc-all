package ski.gagar.aoc2025.day6.part2

import ski.gagar.aoc2025.day6.part1.Operator
import java.math.BigInteger

fun calculateVertical(lines: Sequence<String>): BigInteger {
    var res = BigInteger.ZERO
    val list = lines.toList()
    require(list.isNotEmpty())

    var numbers = mutableListOf<BigInteger>()
    var op: Operator? = null

    fun reset() {
        numbers = mutableListOf()
        op = null
    }

    fun flush() {
        if (numbers.isEmpty() && op != null) {
            reset()
            return
        }
        check(op != null)
        res += numbers.map { it.toString().toBigInteger() }.reduce { a, b -> op!!.apply(a, b) }
        reset()
    }

    for (i in 0..<list.maxOf { it.length }) {
        var allSpaces = true
        val sb = StringBuilder()

        for (line in list.dropLast(1)) {
            val char = line.getOrElse(i) { ' ' }

            when {
                char.isDigit() -> {
                    allSpaces = false
                    sb.append(char)
                }
                char == ' ' -> {}
                else -> error("Illegal character $char at $line:$i")
            }
        }

        if (!allSpaces) {
            numbers += sb.toString().toBigInteger()
        }

        when (val opChar = list.last()[i]) {
            '+' -> {
                check(op == null)
                allSpaces = false
                op = Operator.PLUS
            }
            '*' -> {
                check(op == null)
                allSpaces = false
                op = Operator.TIMES
            }
            ' ' -> {}
            else -> error("Illegal operator $opChar at $i")
        }

        if (allSpaces) {
            flush()
        }
    }
    flush()
    return res
}