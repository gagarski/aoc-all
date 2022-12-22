package ski.gagar.aoc2022.day21.part1

import org.codehaus.jparsec.*
import org.codehaus.jparsec.functors.Map2
import ski.gagar.aoc.util.getResourceAsStream

sealed interface MonkeyExpression {
    fun evaluate(context: MonkeyCalculator.Context): Long
}

data class LongExpression(val long: Long) : MonkeyExpression {
    override fun evaluate(context: MonkeyCalculator.Context): Long = long
    override fun toString(): String = "$long"
}

data class VarExpression(val name: String) : MonkeyExpression {
    override fun evaluate(context: MonkeyCalculator.Context): Long {
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

    override fun toString(): String = name
}

abstract class BinaryOperator(val lhs: MonkeyExpression, val rhs: MonkeyExpression) : MonkeyExpression {
    abstract fun apply(lhs: Long, rhs: Long): Long
    abstract val infix: String
    override fun evaluate(context: MonkeyCalculator.Context): Long = apply(lhs.evaluate(context), rhs.evaluate(context))

    override fun toString(): String = "($lhs $infix $rhs)"
}

class Plus(lhs: MonkeyExpression, rhs: MonkeyExpression) : BinaryOperator(lhs, rhs) {
    override fun apply(lhs: Long, rhs: Long): Long = lhs + rhs
    override val infix: String = "+"
}

class Minus(lhs: MonkeyExpression, rhs: MonkeyExpression) : BinaryOperator(lhs, rhs) {
    override fun apply(lhs: Long, rhs: Long): Long = lhs - rhs
    override val infix: String = "-"
}

class Times(lhs: MonkeyExpression, rhs: MonkeyExpression) : BinaryOperator(lhs, rhs) {
    override fun apply(lhs: Long, rhs: Long): Long = lhs * rhs
    override val infix: String = "*"
}

class DividedBy(lhs: MonkeyExpression, rhs: MonkeyExpression) : BinaryOperator(lhs, rhs) {
    override fun apply(lhs: Long, rhs: Long): Long = lhs / rhs
    override val infix: String = "/"
}

class MonkeyCalculator(val expressions: Map<String, MonkeyExpression>) {
    private val cache: MutableMap<String, Long> = mutableMapOf()

    fun evaluate(name: String) = Context().evaluate(name)

    inner class Context {
        val path: MutableSet<String> = mutableSetOf()
        fun push(name: String) = path.add(name)
        fun pop(name: String) = path.remove(name)

        fun cache(name: String, value: Long) {
            cache[name] = value
        }

        fun getCached(name: String) = cache[name]
        fun getExpression(name: String) = expressions[name] ?: throw IllegalArgumentException()

        fun evaluate(name: String): Long {
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
}

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

    private val LONG = Terminals.IntegerLiteral.PARSER.map { it.toLong() }
    private val IDENTIFIER = Terminals.Identifier.PARSER

    private val LONG_EXPR = LONG.map { LongExpression(it) }
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

fun evaluate(input: String, variable: String = "root"): Long {
    val calc = MonkeyCalculator(ExpressionParser.parse(input))
    return calc.evaluate(variable)
}

fun day21Part1() {
    println("day21/part1/monkeys: \n${
        evaluate(
            getResourceAsStream("/ski.gagar.aoc.aoc2022.day21/monkeys.txt").bufferedReader().readText()
        )
    }")
}