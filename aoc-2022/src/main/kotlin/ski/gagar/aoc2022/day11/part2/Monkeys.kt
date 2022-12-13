package ski.gagar.aoc2022.day11.part2

import org.codehaus.jparsec.OperatorTable
import org.codehaus.jparsec.Parser
import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import org.codehaus.jparsec.Terminals
import org.codehaus.jparsec.Tokens
import org.codehaus.jparsec.functors.Map2
import ski.gagar.aoc.util.getResourceAsStream
import java.math.BigInteger

class Monkey(
    val index: Int = 0,
    val initialItems: List<BigInteger>,
    val op: (BigInteger) -> BigInteger,
    val divisor: BigInteger,
    val ifTrueThrowTo: Int,
    val ifFalseThrowTo: Int
)

class MonkeyBoat(
    monkeySpecs: List<Monkey>,
) {
    private val specs = monkeySpecs.associateBy { it.index }
    private var items = monkeySpecs.map { it.index to it.initialItems.toMutableList() }.toMap().toMutableMap()
    private val scores = monkeySpecs.map { it.index to 0L }.toMap().toMutableMap()
    private val commonDivisor = monkeySpecs.asSequence().map { it.divisor }.fold(BigInteger.ONE) { a, b -> a * b }

    fun round() {
        for ((k, v) in items) {
            val spec = specs[k]
            check(spec != null)
            
            for (item in v) {
                val worryLevel = spec.op(item) % commonDivisor
                
                val throwTo = if (worryLevel % spec.divisor == BigInteger.ZERO) spec.ifTrueThrowTo else spec.ifFalseThrowTo

                items[throwTo] = (items[throwTo] ?: mutableListOf()).apply {
                    add(worryLevel)
                }
                
                scores[k] = (scores[k] ?: 0) + 1
            }

            v.clear()
        }
    }
    
    val result: Map<Int, Long>
        get() = scores
    
}


object MonkeyParser {
    fun interface MonkeyExpr {
        fun evaluate(old: BigInteger): BigInteger
    }

    data class Const(val value: BigInteger) : MonkeyExpr {
        override fun evaluate(old: BigInteger): BigInteger = value
    }

    object Old : MonkeyExpr {
        override fun evaluate(old: BigInteger): BigInteger = old
    }

    data class Times(val lhs: MonkeyExpr, val rhs: MonkeyExpr) : MonkeyExpr {
        override fun evaluate(old: BigInteger): BigInteger = lhs.evaluate(old) * rhs.evaluate(old)
    }

    data class Plus(val lhs: MonkeyExpr, val rhs: MonkeyExpr) : MonkeyExpr {
        override fun evaluate(old: BigInteger): BigInteger = lhs.evaluate(old) + rhs.evaluate(old)
    }

    data class MonkeyFirstHalf(
        val index: Int = 0,
        val initialItems: List<BigInteger>,
        val op: (BigInteger) -> BigInteger,
    )

    data class ThrowSpec(
        val divisor: BigInteger,
        val ifTrueThrowTo: Int,
        val ifFalseThrowTo: Int
    )

    private const val COMMA = ","
    private const val MUL = "*"
    private const val PLUS = "+"
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"
    private const val ASSIGN = "="
    private const val COLON = ":"
    private const val LPAREN = "("
    private const val RPAREN = ")"

    private const val CAPITAL_MONKEY = "Monkey"
    private const val CAPITAL_STARTING = "Starting"
    private const val ITEMS = "items"
    private const val CAPITAL_OPERATION = "Operation"
    private const val NEW = "new"
    private const val OLD = "old"
    private const val CAPITAL_TEST = "Test"
    private const val DIVISIBLE = "divisible"
    private const val BY = "by"
    private const val CAPITAL_IF = "If"
    private const val TRUE = "true"
    private const val FALSE = "false"
    private const val THROW = "throw"
    private const val TO = "to"
    private const val MONKEY = "monkey"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val TERMINALS =
        Terminals.operators(COMMA, MUL, PLUS, NL, NL_WIN, ASSIGN, COLON, LPAREN, RPAREN)
            .words(Scanners.IDENTIFIER)
            .keywords(
                CAPITAL_MONKEY,
                CAPITAL_STARTING,
                ITEMS,
                CAPITAL_OPERATION,
                NEW,
                OLD,
                CAPITAL_TEST,
                DIVISIBLE,
                BY,
                CAPITAL_IF,
                TRUE,
                FALSE,
                THROW,
                TO,
                MONKEY
            ).build()

    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { int ->
        int.toInt().also {
            check(it >= 0)
        }
    }

    private val BIG_INT = Terminals.IntegerLiteral.PARSER.map { int ->
        int.toBigInteger().also {
            check(it >= BigInteger.ZERO)
        }
    }

    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()
    private val TOKENIZER = TERMINALS.tokenizer().cast<Tokens.Fragment>().or(INT_TOKENIZER)

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val MONKEY_HEADER = Parsers.sequence(
        TERMINALS.token(CAPITAL_MONKEY),
        INTEGER,
        TERMINALS.token(COLON)
    ) { _, int, _ ->
        int
    }

    private val LIST_BIG_INT = BIG_INT.sepBy(TERMINALS.token(COMMA))

    private val STARTING_ITEMS = Parsers.sequence(
        TERMINALS.phrase(CAPITAL_STARTING, ITEMS, COLON),
        LIST_BIG_INT
    ) { _, list -> list }

    private val CONST_EXPR = BIG_INT.map { Const(it) }
    private val OLD_EXPR = TERMINALS.token(OLD).map { Old }
    private val EXPR_LAZY = Parser.newReference<MonkeyExpr>()
    private val EXPR = Parsers.or(
        EXPR_LAZY.lazy().between(TERMINALS.token(LPAREN), TERMINALS.token(RPAREN)),
        CONST_EXPR,
        OLD_EXPR
    )
    private val EXPR_FULL = OperatorTable<MonkeyExpr>()
        .infixl(TERMINALS.token(PLUS).retn(
            Map2 { l, r ->
                Plus(l, r)
            }), 10)
        .infixl(TERMINALS.token(MUL).retn(
            Map2 { l, r ->
                Times(l, r)
            }), 10)
        .build(EXPR)

    private val OPERATION = Parsers.sequence(
        TERMINALS.phrase(CAPITAL_OPERATION, COLON, NEW, ASSIGN),
        EXPR_FULL
    )

    private val TEST = Parsers.sequence(
        TERMINALS.phrase(CAPITAL_TEST, COLON, DIVISIBLE, BY),
        BIG_INT
    ).map { bigInt ->
        bigInt
    }

    private val TRUE_THROW = Parsers.sequence(
        TERMINALS.phrase(CAPITAL_IF, TRUE, COLON, THROW, TO, MONKEY),
        INTEGER
    )

    private val FALSE_THROW = Parsers.sequence(
        TERMINALS.phrase(CAPITAL_IF, FALSE, COLON, THROW, TO, MONKEY),
        INTEGER
    )

    private val FIRST_HALF = Parsers.sequence(
        MONKEY_HEADER,
        NEWLINE.many1(),
        STARTING_ITEMS,
        NEWLINE.many1(),
        OPERATION
    ) { ix, _, startingItems, _, op ->
        MonkeyFirstHalf(ix, startingItems) { op.evaluate(it) }
    }

    private val THROW_SPEC = Parsers.sequence(
        TEST,
        NEWLINE.many1(),
        TRUE_THROW,
        NEWLINE.many1(),
        FALSE_THROW
    ) { pred, _, trueMonkey, _, falseMonkey ->
        ThrowSpec(pred, trueMonkey, falseMonkey)
    }

    private val MONKEY_SPEC = Parsers.sequence(
        FIRST_HALF,
        NEWLINE.many1(),
        THROW_SPEC
    ) { first, _, throwSpec ->
        Monkey(
            index = first.index,
            initialItems = first.initialItems,
            op = first.op,
            divisor = throwSpec.divisor,
            ifTrueThrowTo = throwSpec.ifTrueThrowTo,
            ifFalseThrowTo = throwSpec.ifFalseThrowTo
        )
    }

    private val LIST_OF_MONKEYS = MONKEY_SPEC.sepBy(
        NEWLINE.many1()
    )

    private val LIST_OF_MONKEYS_WITH_NL = Parsers.sequence(
        NEWLINE.many(),
        LIST_OF_MONKEYS,
        NEWLINE.many()
    ) { _, list, _ -> list }

    fun parse(str: String) = LIST_OF_MONKEYS_WITH_NL.from(TOKENIZER, WHITESPACES).parse(str, Parser.Mode.DEBUG)

    init {
        EXPR_LAZY.set(EXPR_FULL)
    }
}

fun run(specs: String, n: Int = 10000, top: Int = 2): Long {
    val boat = MonkeyBoat(MonkeyParser.parse(specs))

    for (i in 0 until n) {
        boat.round()
    }
    
    return boat.result.asSequence().sortedByDescending { it.value }.map { it.value }.take(top).fold(1) { a, b -> a * b }
}

val Int.big
    get() = BigInteger(this.toString())

fun day11Part2() {
    println("day11/part2/monkeys: \n${
        run(getResourceAsStream("/ski.gagar.aoc.aoc2022.day11/monkeys.txt").bufferedReader().readText())
    }")
}
