package ski.gagar.aoc2025.day5.part1

import java.math.BigInteger

fun String.toBigIntRange(): ClosedRange<BigInteger> {
    val parts = this.split("-")
    require(parts.size == 2)
    return parts[0].toBigInteger()..parts[1].toBigInteger()
}

fun countFreshIngredients(lines: Sequence<String>): Int {
    val good = mutableSetOf<ClosedRange<BigInteger>>()
    val itr = lines.iterator()
    for (line in itr) {
        if (line.isEmpty()) {
            break
        }
        good.add(line.toBigIntRange())
    }

    return itr.asSequence().count {
        good.any { range -> range.contains(it.toBigInteger()) }
    }
}