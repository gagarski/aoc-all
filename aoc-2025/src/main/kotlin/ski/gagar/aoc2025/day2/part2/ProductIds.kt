package ski.gagar.aoc2025.day2.part2

import ski.gagar.aoc2025.day2.part1.nDigits
import ski.gagar.aoc2025.day2.part1.parseProductIdRange
import java.math.BigInteger

private fun invalidId(base: BigInteger, repeats: Int) = base.toString().repeat(repeats).toBigInteger()
private fun BigInteger.take(n: Int) = toString().take(n).toBigInteger()

private fun BigInteger.isInvalid(): Boolean {
    for (nd in 1..this.nDigits / 2) {
        if (nDigits % nd != 0) continue
        val chunks = this.toString().chunked(nd).toSet()
        if (chunks.size == 1) {
            return true
        }
    }
    return false
}

private fun invalidIds(nDigits: Int,
                       repeats: Int,
                       range: ClosedRange<BigInteger>) = sequence<BigInteger> {
    val minBase = BigInteger.TEN.pow(nDigits - 1)
    val maxBase = BigInteger.TEN.pow(nDigits)

    var current = minBase

    while (true) {
        if (current >= maxBase) break
        if (current.isInvalid()) {
            current += BigInteger.ONE
            continue
        }
        val invalid = invalidId(current, repeats)
        if (invalid in range) {
            yield(invalid)
        }
        current += BigInteger.ONE
    }
}

private fun invalidIds(range: ClosedRange<BigInteger>) = sequence {
    for (nDigits in 1..range.endInclusive.nDigits / 2) {
        val minNRepeats = maxOf(range.start.nDigits / nDigits, 2)
        val maxNRepeats = range.endInclusive.nDigits / nDigits

        for (nRepeats in minNRepeats..maxNRepeats) {
            yieldAll(invalidIds(nDigits, nRepeats, range))
        }
    }
}

fun sumInvalidIds(line: String) =
    line.split(Regex("""\s*,\s*""")).map {
        parseProductIdRange(it)
    }.flatMap {
        invalidIds(it)
    }.fold(BigInteger.ZERO) { sum, it -> sum + it }

