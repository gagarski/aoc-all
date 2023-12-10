package ski.gagar.aoc2022.day21.part2

import org.jparsec.*
import org.jparsec.functors.Map2
import ski.gagar.aoc.util.getResourceAsStream
import java.math.BigInteger
import java.util.TreeMap

sealed interface MonkeyExpression {
    fun evaluate(context: MonkeyCalculator.Context): BigInteger
    fun asPolynomFraction(context: MonkeyCalculator.FractionContext): PolynomFraction
}

data class BigIntegerExpression(val bigInt: BigInteger) : MonkeyExpression {
    override fun evaluate(context: MonkeyCalculator.Context): BigInteger = bigInt
    override fun asPolynomFraction(context: MonkeyCalculator.FractionContext) =
        BigIntPolynom.of(0 to bigInt).toFraction()

    override fun toString(): String = "$bigInt"
}

data class VarExpression(val name: String) : MonkeyExpression {
    override fun evaluate(context: MonkeyCalculator.Context): BigInteger {
        if (name in context.path) {
            throw IllegalArgumentException("Recursion not supported")
        }

        val cached = context.getCached(name)

        if (null != cached) {
            return cached
        }
        context.push(name)
        val res = context.getExpression(name).evaluate(context)
        context.pop(name)
        context.cache(name, res)
        return res
    }

    override fun asPolynomFraction(context: MonkeyCalculator.FractionContext): PolynomFraction {
        if (name in context.path) {
            throw IllegalArgumentException("Recursion not supported")
        }
        if (context.varName == name)
            return BigIntPolynom.of(1 to BigInteger.ONE).toFraction()

        val cached = context.getCached(name)

        if (null != cached) {
            return cached
        }
        context.push(name)

        val res = context.asPolynomFraction(name)

        context.pop(name)
        context.cache(name, res)
        return res
    }


    override fun toString(): String = name
}

abstract class BinaryOperator(val lhs: MonkeyExpression, val rhs: MonkeyExpression) : MonkeyExpression {
    abstract fun apply(lhs: BigInteger, rhs: BigInteger): BigInteger
    abstract val infix: String
    override fun evaluate(context: MonkeyCalculator.Context): BigInteger = apply(lhs.evaluate(context), rhs.evaluate(context))

    override fun toString(): String = "($lhs $infix $rhs)"
}

class Plus(lhs: MonkeyExpression, rhs: MonkeyExpression) : BinaryOperator(lhs, rhs) {
    override fun apply(lhs: BigInteger, rhs: BigInteger): BigInteger = lhs + rhs
    override val infix: String = "+"
    override fun asPolynomFraction(context: MonkeyCalculator.FractionContext): PolynomFraction =
        lhs.asPolynomFraction(context) + rhs.asPolynomFraction(context)
}

class Minus(lhs: MonkeyExpression, rhs: MonkeyExpression) : BinaryOperator(lhs, rhs) {
    override fun apply(lhs: BigInteger, rhs: BigInteger): BigInteger = lhs - rhs
    override val infix: String = "-"
    override fun asPolynomFraction(context: MonkeyCalculator.FractionContext): PolynomFraction =
        lhs.asPolynomFraction(context) - rhs.asPolynomFraction(context)
}

class Times(lhs: MonkeyExpression, rhs: MonkeyExpression) : BinaryOperator(lhs, rhs) {
    override fun apply(lhs: BigInteger, rhs: BigInteger): BigInteger = lhs * rhs
    override val infix: String = "*"
    override fun asPolynomFraction(context: MonkeyCalculator.FractionContext): PolynomFraction =
        lhs.asPolynomFraction(context) * rhs.asPolynomFraction(context)
}

class DividedBy(lhs: MonkeyExpression, rhs: MonkeyExpression) : BinaryOperator(lhs, rhs) {
    override fun apply(lhs: BigInteger, rhs: BigInteger): BigInteger = lhs / rhs
    override val infix: String = "/"

    override fun asPolynomFraction(context: MonkeyCalculator.FractionContext): PolynomFraction =
        lhs.asPolynomFraction(context) / rhs.asPolynomFraction(context)
}

class MonkeyCalculator(val expressions: Map<String, MonkeyExpression>) {
    private val cache: MutableMap<String, BigInteger> = mutableMapOf()

    fun evaluate(name: String) = Context().evaluate(name)

    fun polynomFractionForName(forVar: String, ofVar: String): PolynomFraction {
        val expr = expressions[forVar]

        require(expr is BinaryOperator)

        val cxt = FractionContext(ofVar)
        return expr.lhs.asPolynomFraction(cxt) - expr.rhs.asPolynomFraction(cxt)
    }

    inner class Context {
        val path: MutableSet<String> = mutableSetOf()
        fun push(name: String) = path.add(name)
        fun pop(name: String) = path.remove(name)

        fun cache(name: String, value: BigInteger) {
            cache[name] = value
        }

        fun getCached(name: String) = cache[name]
        fun getExpression(name: String) = expressions[name] ?: throw IllegalArgumentException()

        fun evaluate(name: String): BigInteger {
            val cached = getCached(name)

            if (null != cached) {
                return cached
            }
            push(name)
            val res = getExpression(name).evaluate(this)
            pop(name)
            cache(name, res)
            return res
        }
    }

    inner class FractionContext(val varName: String) {
        private val cache: MutableMap<String, PolynomFraction> = mutableMapOf()

        val path: MutableSet<String> = mutableSetOf()
        fun push(name: String) = path.add(name)
        fun pop(name: String) = path.remove(name)

        fun cache(name: String, value: PolynomFraction) {
            cache[name] = value
        }

        fun getCached(name: String) = cache[name]
        fun getExpression(name: String) = expressions[name] ?: throw IllegalArgumentException()

        fun asPolynomFraction(name: String): PolynomFraction {
            val cached = getCached(name)

            if (null != cached) {
                return cached
            }
            push(name)
            val res = getExpression(name).asPolynomFraction(this)
            pop(name)
            cache(name, res)
            return res
        }
    }
}


private typealias Storage = TreeMap<UInt, BigInteger>

class BigIntPolynom private constructor(
    private val coeffs: Storage
) {
    operator fun get(pow: UInt) = coeffs[pow] ?: BigInteger.ZERO
    operator fun get(pow: Int): BigInteger {
        require(pow >= 0)
        return get(pow.toUInt())
    }

    val power: UInt
        get() = coeffs.lastKey()

    val map: Map<UInt, BigInteger>
        get() = coeffs

    operator fun plus(other: BigIntPolynom): BigIntPolynom {
        val buf = TreeMap(this.coeffs)

        for ((pow, coeff) in other.coeffs) {
            buf[pow] = (buf[pow] ?: BigInteger.ZERO) + coeff
        }
        buf.cleanupZeros()

        return BigIntPolynom(buf)
    }

    operator fun minus(other: BigIntPolynom): BigIntPolynom {
        val buf = TreeMap(this.coeffs)

        for ((pow, coeff) in other.coeffs) {
            buf[pow] = (buf[pow] ?: BigInteger.ZERO) - coeff
        }
        buf.cleanupZeros()
        return BigIntPolynom(buf)
    }

    operator fun unaryMinus(): BigIntPolynom {
        val buf = Storage()

        for ((pow, coeff) in coeffs) {
            buf[pow] = -coeff
        }

        return BigIntPolynom(buf)
    }

    operator fun unaryPlus(): BigIntPolynom = this

    operator fun times(other: BigIntPolynom): BigIntPolynom {
        val buf = Storage()

        for ((pow1, coeff1) in coeffs) {
            for ((pow2, coeff2) in other.coeffs) {
                val pow = pow1 + pow2
                buf[pow] = (buf[pow] ?: BigInteger.ZERO) + coeff1 * coeff2
            }
        }

        buf.cleanupZeros()
        return BigIntPolynom(buf)
    }


    fun divRem(other: BigIntPolynom): DivRem {
        val div = Storage()
        val rem = Storage()

        require(other != ZERO)

        rem.putAll(this.coeffs)

        if (rem.isEmpty())
            return DivRem(BigIntPolynom(div), BigIntPolynom(rem))

        var curPow: UInt = power

        while (curPow >= other.power) {
            val toDivide = rem.tailMap(curPow, true).descendingMap()

            if (toDivide.isEmpty()) {
                break
            }

            val powDiff = curPow - other.power
            val curDivCoeff = (rem[curPow] ?: BigInteger.ZERO) / other[other.power]

            div[powDiff] = curDivCoeff

            for ((otherPow, coeff) in other.coeffs.descendingMap()) {
                rem[otherPow + powDiff] =
                    (rem[otherPow + powDiff] ?: BigInteger.ZERO) - curDivCoeff * coeff
            }

            rem.cleanupZeros()

            curPow = rem.lowerKey(curPow) ?: 0.toUInt()
        }

        div.cleanupZeros()

        return DivRem(BigIntPolynom(div), BigIntPolynom(rem))
    }

    operator fun div(other: BigIntPolynom) = divRem(other).div

    operator fun rem(other: BigIntPolynom) = divRem(other).rem

    fun eval(value: BigInteger): BigInteger {
        var prevPow = 0.toUInt()
        var prevMonom = BigInteger.ONE

        var res = BigInteger.ZERO
        for ((pow, coeff) in coeffs.descendingMap()) {
            val monom = prevMonom * value.pow((pow - prevPow).toInt())
            res += coeff * monom

            prevPow = pow
            prevMonom = monom
        }

        return res
    }

    fun isRoot(value: BigInteger): Boolean = eval(value) == BigInteger.ZERO

    private fun linearRoot(): BigInteger {
        require(power == 1.toUInt())
        return -this[0] / this[1]
    }

    private fun squareRoots() = sequence {
        require(power == 2.toUInt())
        val a = this@BigIntPolynom[2]
        val b = this@BigIntPolynom[1]
        val c = this@BigIntPolynom[0]

        val d = b * b - 4.toBigInteger() * a * c

        if (d < BigInteger.ZERO) {
            return@sequence
        }

        val sqDAndRem = d.sqrtAndRemainder()

        if (sqDAndRem[1] != BigInteger.ZERO) {
            return@sequence
        }

        val sqD = sqDAndRem[0]

        if (sqD == BigInteger.ZERO) {
            yield(-b / (BigInteger.TWO * a))
            return@sequence
        }

        val root1 = (-b + sqD) / (BigInteger.TWO * a)
        val root1Rem = (-b + sqD) % (BigInteger.TWO * a)

        if (root1Rem == BigInteger.ZERO) {
            yield(root1)
        }

        val root2 = (-b - sqD) / (BigInteger.TWO * a)
        val root2Rem = (-b - sqD) % (BigInteger.TWO * a)

        if (root2Rem == BigInteger.ZERO) {
            yield(root2)
        }
    }

    fun rootSequence() = sequence<BigInteger> {
        if (coeffs.isEmpty()) {
            return@sequence
        }

        for (i in 0.toUInt() until coeffs.firstEntry().key) {
            yield(BigInteger.ZERO)
        }

        var current = this@BigIntPolynom / of(coeffs.firstKey() to BigInteger.ONE)

        val free = current[0]

        var div = BigInteger.ONE
        val limit = free.abs().sqrt()

        while (div <= limit) {
            if (current.power <= 2.toUInt())
                break
            var noneYielded = true
            if (free % div == BigInteger.ZERO) {
                val (d, rem) = current.divRem(of(1 to BigInteger.ONE, 0 to -div))

                if (rem == ZERO) {
                    yield(div)
                    noneYielded = false
                    current = d
                }

                val (dMinus, remMinus) = current.divRem(of(1 to BigInteger.ONE, 0 to div))

                if (remMinus == ZERO) {
                    yield(-div)
                    noneYielded = false
                    current = dMinus
                }

                val complement = free / div

                if (complement == div) {
                    continue
                }

                val (dComplement, remComplement) = current.divRem(of(1 to BigInteger.ONE, 0 to -complement))

                if (remComplement == ZERO) {
                    yield(complement)
                    noneYielded = false
                    current = dComplement
                }

                val (dComplementMinus, remComplementMinus) = current.divRem(of(1 to BigInteger.ONE, 0 to complement))

                if (remComplementMinus == ZERO) {
                    yield(-complement)
                    noneYielded = false
                    current = dComplementMinus
                }
            }
            if (noneYielded) {
                div++
            }

        }

        when (current.power) {
            1.toUInt() -> yield(current.linearRoot())
            2.toUInt() -> yieldAll(current.squareRoots())
        }
    }

    fun roots() = rootSequence().groupingBy { it }.eachCount()

    fun uniqueRoots() = roots().keys

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BigIntPolynom

        if (coeffs != other.coeffs) return false

        return true
    }

    override fun hashCode(): Int {
        return coeffs.hashCode()
    }

    private fun formatCoeff(pow: UInt, coeff: BigInteger) = when {
        coeff == BigInteger.ONE && pow != 0.toUInt() -> ""
        else -> "$coeff"
    }

    private fun formatMono(pow: UInt, coeff: BigInteger) =
        when (pow) {
            0.toUInt() -> formatCoeff(pow, coeff)
            1.toUInt() -> "${formatCoeff(pow, coeff)}x"
            else -> "${formatCoeff(pow, coeff)}x^$pow"
        }

    override fun toString(): String = buildString {
        val map = coeffs.descendingMap()

        if (map.isEmpty()) {
            append("0")
            return@buildString
        }
        val first = map.asSequence().first()
        append(formatMono(first.key, first.value))

        for ((pow, coeff) in map.asSequence().drop(1)) {
            if (coeff < BigInteger.ZERO)
                append(" - ")
            else
                append(" + ")

            append(formatMono(pow, coeff.abs()))
        }
    }


    data class DivRem(val div: BigIntPolynom, val rem: BigIntPolynom)

    companion object {
        private fun MutableMap<UInt, BigInteger>.cleanupZeros() {
            val iter = iterator()

            while (iter.hasNext()) {
                val (_, v) = iter.next()

                if (v == BigInteger.ZERO) iter.remove()
            }
        }

        @JvmName("ofUInt")
        fun of(seq: Sequence<Pair<UInt, BigInteger>>) =
            BigIntPolynom(TreeMap<UInt, BigInteger>().apply {
                for ((pow, coeff) in seq) {
                    if (coeff == BigInteger.ZERO)
                        continue
                    this[pow] = coeff
                }
            })

        @JvmName("ofInt")
        fun of(seq: Sequence<Pair<Int, BigInteger>>) =
            of(seq.map { (k, v) ->
                require(k >= 0)
                k.toUInt() to v
            })

        @JvmName("ofUInt")
        fun of(map: Map<UInt, BigInteger>): BigIntPolynom =
            of(map.asSequence().map { it.key to it.value })

        @JvmName("ofInt")
        fun of(map: Map<Int, BigInteger>): BigIntPolynom =
            of(map.asSequence().map { it.key to it.value })

        @JvmName("ofUInt")
        fun of(vararg entries: Pair<UInt, BigInteger>): BigIntPolynom =
            of(entries.asSequence())

        @JvmName("ofInt")
        fun of(vararg entries: Pair<Int, BigInteger>): BigIntPolynom =
            of(entries.asSequence())

        fun of(value: BigInteger) = of(sequenceOf(0 to value))

        fun ofXMinus(value: BigInteger) = of(sequenceOf(0 to -value, 1 to BigInteger.ONE))
        fun ofXPlus(value: BigInteger) = of(sequenceOf(0 to value, 1 to BigInteger.ONE))

        val ZERO = of(sequenceOf<Pair<UInt, BigInteger>>())
        val ONE = of(0 to BigInteger.ONE)
        val X = of(1 to BigInteger.ONE)

    }
}

operator fun BigIntPolynom.plus(num: BigInteger) = this + BigIntPolynom.of(num)
operator fun BigIntPolynom.minus(num: BigInteger) = this - BigIntPolynom.of(num)
operator fun BigIntPolynom.times(num: BigInteger) = this * BigIntPolynom.of(num)
fun BigIntPolynom.divRem(num: BigInteger) = this.divRem(BigIntPolynom.of(num))
operator fun BigIntPolynom.div(num: BigInteger) = this / BigIntPolynom.of(num)
operator fun BigIntPolynom.rem(num: BigInteger) = this % BigIntPolynom.of(num)

operator fun BigInteger.plus(poly: BigIntPolynom) = BigIntPolynom.of(this) + poly
operator fun BigInteger.minus(poly: BigIntPolynom) = BigIntPolynom.of(this) - poly
operator fun BigInteger.times(poly: BigIntPolynom) = BigIntPolynom.of(this) * poly
fun BigInteger.divRem(poly: BigIntPolynom) = BigIntPolynom.of(this).divRem(poly)
operator fun BigInteger.div(poly: BigIntPolynom) = BigIntPolynom.of(this) / poly
operator fun BigInteger.rem(poly: BigIntPolynom) = BigIntPolynom.of(this) % poly


data class PolynomFraction(val num: BigIntPolynom, val denom: BigIntPolynom = BigIntPolynom.ONE) {
    init {
        require(denom != BigIntPolynom.ZERO)
    }

    operator fun plus(other: PolynomFraction) =
        PolynomFraction(num * other.denom + other.num * denom, denom * other.denom)

    operator fun minus(other: PolynomFraction) =
        PolynomFraction(num * other.denom - other.num * denom, denom * other.denom)

    operator fun times(other: PolynomFraction) =
        PolynomFraction(num * other.num, denom * other.denom)

    operator fun div(other: PolynomFraction) =
        PolynomFraction(num * other.denom, denom * other.num)

    fun rootSequence() = sequence {
        for (root in num.rootSequence()) {
            if (!denom.isRoot(root)) {
                yield(root)
            }
        }
    }

    fun roots() = rootSequence().groupingBy { it }.eachCount()
    fun uniqueRoots() = roots().keys

}

fun BigIntPolynom.toFraction() = PolynomFraction(this)

object ExpressionParser {
    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private const val COLON = ":"
    private const val PLUS = "+"
    private const val MINUS = "-"
    private const val MUL = "*"
    private const val DIV = "/"
    private const val LPAREN = "("
    private const val RPAREN = ")"

    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val TERMINALS =
        Terminals.operators(COLON, PLUS, MINUS, MUL, DIV, LPAREN, RPAREN, NL, NL_WIN)

    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val IDENTIFIER_TOKENIZER = Terminals.Identifier.TOKENIZER

    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER,
        IDENTIFIER_TOKENIZER
    )

    private val LONG = Terminals.IntegerLiteral.PARSER.map { it.toBigInteger() }
    private val IDENTIFIER = Terminals.Identifier.PARSER

    private val LONG_EXPR = LONG.map { BigIntegerExpression(it) }
    private val VAR_EXPR = IDENTIFIER.map { VarExpression(it) }
    private val EXPR_LAZY = Parser.newReference<MonkeyExpression>()

    private val EXPR = Parsers.or(
        EXPR_LAZY.lazy().between(
            TERMINALS.token(LPAREN), TERMINALS.token(RPAREN)),
        LONG_EXPR,
        VAR_EXPR
    )

    private val EXPR_FULL = OperatorTable<MonkeyExpression>()
        .infixl(TERMINALS.token(PLUS).retn(
            Map2 { lhs, rhs ->
                Plus(lhs, rhs)
            }
        ), 10)
        .infixl(TERMINALS.token(MINUS).retn(
            Map2 { lhs, rhs ->
                Minus(lhs, rhs)
            }
        ), 10)
        .infixl(TERMINALS.token(MUL).retn(
            Map2 { lhs, rhs ->
                Times(lhs, rhs)
            }
        ), 20)
        .infixl(TERMINALS.token(DIV).retn(
            Map2 { lhs, rhs ->
                DividedBy(lhs, rhs)
            }
        ), 20)
        .build(EXPR)

    init {
        EXPR_LAZY.set(EXPR_FULL)
    }

    private val ASSIGNMENT = Parsers.sequence(
        IDENTIFIER,
        TERMINALS.token(COLON),
        EXPR_FULL
    ) { name, _, expr ->
        name to expr
    }

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val ASSIGNMENTS = Parsers.sequence(
        NEWLINE.many(),
        ASSIGNMENT.sepBy(NEWLINE.many1()),
        NEWLINE.many()
    ) { _, assignments, _ ->
        assignments.toMap()
    }

    fun parse(input: String) = ASSIGNMENTS.from(TOKENIZER, WHITESPACES).parse(input)
}

fun evaluate(input: String, variable: String = "root", eqVariable: String = "humn"): BigInteger {
    val calc = MonkeyCalculator(ExpressionParser.parse(input))
    val frac = calc.polynomFractionForName(variable, eqVariable)
    return frac.rootSequence().first()
}