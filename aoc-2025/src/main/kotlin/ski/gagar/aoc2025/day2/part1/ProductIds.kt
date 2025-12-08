package ski.gagar.aoc2025.day2.part1

import java.math.BigInteger


fun parseProductIdRange(ids: String): ClosedRange<BigInteger> {
    val parts = ids.split("-")
    require(parts.size == 2)
    return parts[0].toBigInteger()..parts[1].toBigInteger()
}

val BigInteger.nDigits: Int
    get() {
        require(this.signum() == 1)
        return this.toString().length
    }

private val BigInteger.halfIdLower: BigInteger
    get() {
        val log10 = this.nDigits
        return if (log10 % 2 == 0) {
            this / BigInteger.TEN.pow(log10 / 2)
        } else {
            BigInteger.TEN.pow(log10 - (log10 + 1) / 2)
        }
    }

private val BigInteger.halfIdHigher: BigInteger
    get() {
        val log10 = this.nDigits
        return if (log10 % 2 == 0) {
            this / BigInteger.TEN.pow(log10 / 2)
        } else {
            BigInteger.TEN.pow(log10 - (log10 + 1) / 2) - BigInteger.ONE
        }
    }

private val BigInteger.repeat
    get() = this * BigInteger.TEN.pow(this.nDigits) + this

private val ClosedRange<BigInteger>.halfIdRange: ClosedRange<BigInteger>
    get() = start.halfIdLower..endInclusive.halfIdHigher

private fun invalidIds(range: ClosedRange<BigInteger>): Sequence<BigInteger> = sequence {
    val half = range.halfIdRange
    var h = half.start
    while (true) {
        if (h > half.endInclusive) break
        val rep = h.repeat
        if (rep in range) {
            yield(rep)
        }
        h += BigInteger.ONE
    }
}

fun sumInvalidIds(line: String) =
    line.split(Regex("""\s*,\s*""")).map {
        parseProductIdRange(it)
    }.flatMap {
        invalidIds(it)
    }.fold(BigInteger.ZERO) { sum, it -> sum + it }
