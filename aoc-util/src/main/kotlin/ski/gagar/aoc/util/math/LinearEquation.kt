package ski.gagar.aoc.util.math

data class LinearEquation(val coeffs: List<NaturalFraction>, val free: NaturalFraction) {
    init {
        require(coeffs.isNotEmpty()) {
            "$this is empty"
        }
    }

    constructor(vararg coeffs: NaturalFraction, free: NaturalFraction) : this(coeffs.toList(), free)
    constructor(vararg coeffs: Int, free: Int) : this(coeffs.map { nf(it) }.toList(), nf(free))

    private infix fun List<NaturalFraction>.plusEach(other: List<NaturalFraction>): List<NaturalFraction> {
        require(size == other.size)
        return this.asSequence().zip(other.asSequence()).map { (a, b) -> a + b }.toList()
    }

    private infix fun List<NaturalFraction>.minusEach(other: List<NaturalFraction>): List<NaturalFraction> {
        require(size == other.size)
        return this.asSequence().zip(other.asSequence()).map { (a, b) -> a - b }.toList()
    }

    private infix fun List<NaturalFraction>.timesEach(coeff: NaturalFraction): List<NaturalFraction> =
        this.map { it * coeff }
    private fun List<NaturalFraction>.unaryMinusEach(): List<NaturalFraction> = this.map { -it }

    operator fun plus(other: LinearEquation) =
        LinearEquation(this.coeffs plusEach other.coeffs, free + other.free)

    operator fun minus(other: LinearEquation) =
        LinearEquation(this.coeffs minusEach other.coeffs, free - other.free)

    operator fun times(coeff: NaturalFraction) =
        LinearEquation(this.coeffs timesEach coeff, free * coeff)

    val size
        get() = coeffs.size

    val leadingZeros by lazy {
        coeffs.asSequence().takeWhile { it == NaturalFraction.ZERO }.count()
    }

    val zeros by lazy {
        coeffs.count { it == NaturalFraction.ZERO }
    }

    val isSingleton by lazy {
        zeros == size - 1
    }

    val isEmpty by lazy {
        zeros == size
    }

    val isUniversal by lazy {
        free.isZero && coeffs.all { it.isZero }
    }

    val isImpossible by lazy {
        !free.isZero && coeffs.all { it.isZero }
    }

    operator fun get(index: Int) = coeffs[index]

    fun getOrNull(index: Int) =
        if (index in coeffs.indices) coeffs[index]
        else null

    val head
        get() = this[0]
}