package ski.gagar.aoc.util.math

import java.math.BigDecimal
import java.math.BigInteger
import javax.print.attribute.standard.MediaSize.NA

data class NaturalFraction private constructor(val num: BigInteger, val denom: BigInteger = BigInteger.ONE) :
    Comparable<NaturalFraction> {
    init {
        require(num.gcd(denom) == BigInteger.ONE || num.gcd(denom) == BigInteger.ZERO)
        require(denom >= BigInteger.ZERO)
    }

    val isNaN
        get() = denom == BigInteger.ZERO && num == BigInteger.ZERO

    val isInfinity
        get() = denom == BigInteger.ZERO

    val isPositiveInfinity
        get() = isInfinity && this.num > BigInteger.ZERO

    val isNegativeInfinity
        get() = isInfinity && this.num < BigInteger.ZERO

    val isZero
        get() = num == BigInteger.ZERO

    val isInt
        get() = denom == BigInteger.ONE

    val abs
        get() = normalized(this.num.abs(), this.denom)

    operator fun plus(other: NaturalFraction) =
        normalized(this.num * other.denom + other.num * this.denom, this.denom * other.denom)

    operator fun minus(other: NaturalFraction) =
        normalized(this.num * other.denom - other.num * this.denom, this.denom * other.denom)

    operator fun times(other: NaturalFraction) =
        normalized(this.num * other.num, this.denom * other.denom)

    operator fun div(other: NaturalFraction) =
        normalized(this.num * other.denom, this.denom * other.num)

    operator fun unaryMinus() = normalized(-this.num, this.denom)

    override fun compareTo(other: NaturalFraction): Int =
        (this.num * other.denom).compareTo(other.num * this.denom)

    override fun toString(): String = when {
        isNegativeInfinity -> "-Inf"
        isPositiveInfinity -> "+Inf"
        isInt -> "$num"
        else -> "$num/$denom"
    }

    fun toBigDecimal(scale: Int) =
        BigDecimal(num, scale) / BigDecimal(denom, scale)

    companion object {
        private fun normalized(num: BigInteger, denom: BigInteger): NaturalFraction {
            if (num == denom) return ONE
            if (num == BigInteger.ZERO) return ZERO
            if (denom == BigInteger.ZERO && num == BigInteger.ZERO) return NAN
            if (denom == BigInteger.ZERO && num > BigInteger.ZERO) return POSITIVE_INFINITY
            if (denom == BigInteger.ZERO && num < BigInteger.ZERO) return NEGATIVE_INFINITY

            val gcd = num.gcd(denom)
            val normNum =
                if (denom >= BigInteger.ZERO)
                    num / gcd
                else
                    -num / gcd
            val normDenom =
                if (denom >= BigInteger.ZERO)
                    denom / gcd
                else
                    -denom / gcd
            return NaturalFraction(normNum, normDenom)
        }

        operator fun invoke(num: BigInteger, denom: BigInteger = BigInteger.ONE) =
            normalized(num, denom)

        operator fun invoke(num: Int, denom: Int = 1) = invoke(num.toBigInteger(), denom.toBigInteger())
        operator fun invoke(num: Long, denom: Long = 1) = invoke(num.toBigInteger(), denom.toBigInteger())

        val ZERO = NaturalFraction(BigInteger.ZERO)
        val ONE = NaturalFraction(BigInteger.ONE)
        val POSITIVE_INFINITY = NaturalFraction(BigInteger.ONE, BigInteger.ZERO)
        val NEGATIVE_INFINITY = NaturalFraction(-BigInteger.ONE, BigInteger.ZERO)
        val NAN = NaturalFraction(BigInteger.ZERO, BigInteger.ZERO)
    }
}

fun Int.toNaturalFraction() = NaturalFraction(this)
fun Long.toNaturalFraction() = NaturalFraction(this)
fun nf(num: Int, denom: Int = 1) = NaturalFraction(num, denom)
fun nf(num: Long, denom: Long = 1) = NaturalFraction(num, denom)
fun nf(num: BigInteger, denom: BigInteger = BigInteger.ONE) = NaturalFraction(num, denom)
