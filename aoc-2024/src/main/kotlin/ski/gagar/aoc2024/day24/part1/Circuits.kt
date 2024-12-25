package ski.gagar.aoc2024.day24.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import java.util.*

interface Expression {
    fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): Boolean
}

interface ConstExpression: Expression {
    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): Boolean = const
    val const: Boolean
}

data object TrueExpression : ConstExpression {
    override val const: Boolean = true

    const val MNEMONIC = "1"
}

data object FalseExpression : ConstExpression {
    override val const: Boolean = false

    const val MNEMONIC = "0"
}

data class SignalExpression(val name: String) : Expression {
    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): Boolean {
        if (name in state.path)
            throw IllegalStateException("Feedback is not supported ($name)")
        state.push(name)
        val res = state.getCached(name) ?: circuit.getGate(name).expression.evaluate(circuit, state)
        state.cache(name, res)
        state.pop(name)
        return res
    }
}

data class Assignment(val expression: Expression, val name: String) {
    companion object {
        const val MNEMONIC = "->"
        const val MNEMONIC_CONST = ":"
    }
}

interface BinaryOperatorExpression : Expression {
    val lhs: Expression
    val rhs: Expression

    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): Boolean =
        apply(lhs.evaluate(circuit, state), rhs.evaluate(circuit, state))


    fun apply(lhs: Boolean, rhs: Boolean): Boolean
}

data class AndExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: Boolean, rhs: Boolean): Boolean = lhs and rhs

    companion object {
        const val MNEMONIC = "AND"
    }
}

data class OrExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: Boolean, rhs: Boolean): Boolean = lhs or rhs

    companion object {
        const val MNEMONIC = "OR"
    }
}

data class XorExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: Boolean, rhs: Boolean): Boolean = lhs xor rhs

    companion object {
        const val MNEMONIC = "XOR"
    }
}

class LogicalCircuit(assignments: List<Assignment>) {
    val wires = assignments.associateBy { it.name }
    val cache: MutableMap<String, Boolean> = mutableMapOf()


    fun getGate(name: String) = wires[name] ?: throw IllegalArgumentException("Unknown signal $name")

    fun evaluate(name: String) = getGate(name).expression.evaluate(this, Context().apply { push(name) })

    inner class Context {
        val path: MutableSet<String> = mutableSetOf()
        fun push(name: String) = path.add(name)
        fun pop(name: String) = path.remove(name)

        fun cache(name: String, value: Boolean) {
            cache[name] = value
        }

        fun getCached(name: String) = cache[name]
    }
}

object CircuitParser {
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private val TERMINALS = Terminals
        .operators(
            Assignment.MNEMONIC,
            Assignment.MNEMONIC_CONST,
            AndExpression.MNEMONIC,
            OrExpression.MNEMONIC,
            XorExpression.MNEMONIC,
            NL,
            NL_WIN
        )
        .words(Scanners.IDENTIFIER)
        .build()

    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER


    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER
    )

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { int ->
        int.toInt()
    }

    private val CONST_EXPR = INTEGER.map { it ->
        when (it) {
            1 -> TrueExpression
            0 -> FalseExpression
            else -> error("Illegal int literal $it")
        }
    }

    private val ASSIGNMENT_TARGET = Terminals.Identifier.PARSER
    private val CONST_ASSIGNMNENT = Parsers.sequence(
        ASSIGNMENT_TARGET,
        TERMINALS.token(Assignment.MNEMONIC_CONST),
        CONST_EXPR
    ) { tgt, _, const ->
        Assignment(const, tgt)
    }
    private val SIGNAL_EXPR = Terminals.Identifier.PARSER.map { SignalExpression(it) }
    private val AND_EXPR = Parsers.sequence(
        SIGNAL_EXPR,
        TERMINALS.token(AndExpression.MNEMONIC),
        SIGNAL_EXPR
    ) { a, _, b -> AndExpression(a, b) }

    private val OR_EXPR = Parsers.sequence(
        SIGNAL_EXPR,
        TERMINALS.token(OrExpression.MNEMONIC),
        SIGNAL_EXPR
    ) { a, _, b -> OrExpression(a, b) }

    private val XOR_EXPR = Parsers.sequence(
        SIGNAL_EXPR,
        TERMINALS.token(XorExpression.MNEMONIC),
        SIGNAL_EXPR
    ) { a, _, b -> XorExpression(a, b) }

    private val BINARY_OP = Parsers.or(AND_EXPR, OR_EXPR, XOR_EXPR)
    private val BINARY_OP_ASSIGNMENT = Parsers.sequence(
        BINARY_OP,
        TERMINALS.token(Assignment.MNEMONIC),
        ASSIGNMENT_TARGET
    ) { op, _, tgt ->
        Assignment(op, tgt)
    }

    private val ASSIGNMENT = Parsers.or(CONST_ASSIGNMNENT, BINARY_OP_ASSIGNMENT)
    private val CIRCUIT = ASSIGNMENT.sepBy(NEWLINE.many1())

    fun parse(text: String) = CIRCUIT.from(TOKENIZER, WHITESPACES).parse(text)
}

private val Z_RE = """z([0-9]+)""".toRegex()

fun getNumberFromZ(text: String): Long {
    val circuit = LogicalCircuit(CircuitParser.parse(text))

    val bits = circuit.wires.keys.filter { it.matches(Z_RE) }.map { name ->
        val n = Z_RE.matchEntire(name)?.groups?.get(1)?.value?.toInt() ?: error("Cannot parse z signal")
        val value = circuit.evaluate(name)
        n to value
    }.toMap(TreeMap())

    var i = bits.lastKey() + 1
    var number = 0L
    for ((k, v) in bits.reversed()) {
        if (k != i - 1)
            error("Missing bit ${i - 1}")
        i = k
        number = number shl 1

        if (v) number = number or 1L
    }
    return number
}

