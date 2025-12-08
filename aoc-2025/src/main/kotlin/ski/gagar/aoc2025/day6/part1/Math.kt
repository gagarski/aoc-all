package ski.gagar.aoc2025.day6.part1

import java.math.BigInteger

enum class Operator {
    PLUS {
        override fun apply(a: BigInteger, b: BigInteger): BigInteger {
            return a + b
        }

    },
    TIMES {
        override fun apply(a: BigInteger, b: BigInteger): BigInteger {
            return a * b
        }
    };

    abstract fun apply(a: BigInteger, b: BigInteger): BigInteger
}

sealed interface Line {
    data class Numbers(val numbers: List<BigInteger>) : Line {
        fun sum(): BigInteger = numbers.fold(BigInteger.ZERO) { a, b -> a + b }
    }
    data class Operators(val operators: List<Operator>) : Line {
        fun apply(nums: List<Numbers>): Numbers {
            require(nums.isNotEmpty())

            val buf = nums.first().numbers.toMutableList()

            for (line in nums.asSequence().drop(1)) {
                require(line.numbers.size == buf.size)
                for ((index, item) in line.numbers.withIndex()) {
                    buf[index] = operators[index].apply(buf[index], item)
                }
            }

            return Numbers(buf)
        }
    }

    companion object {
        fun parse(line: String): Line {
            val items = line.trim().split("""\s+""".toRegex())
            val bigInts = items.map { it.toBigIntegerOrNull() }
            if (bigInts.all { it != null }) {
                @Suppress("UNCHECKED_CAST")
                return Numbers(bigInts as List<BigInteger>)
            }
            return items.map {
                when (it) {
                    "+" -> Operator.PLUS
                    "*" -> Operator.TIMES
                    else -> error("Parse error")
                }
            }.let { Operators(it) }

        }
    }
}

fun calculate(lines: Sequence<String>): BigInteger {
    val numbers = mutableListOf<Line.Numbers>()
    for (line in lines) {
        when (val parsed = Line.parse(line)) {
            is Line.Numbers -> numbers.add(parsed)
            is Line.Operators -> return parsed.apply(numbers).sum()
        }
    }
    error("Empty input")
}