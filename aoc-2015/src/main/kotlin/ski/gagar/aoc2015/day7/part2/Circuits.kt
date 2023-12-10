package ski.gagar.aoc2015.day7.part2

import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream
import java.util.StringJoiner

interface Expression {
    fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): UShort
}

data class NumberExpression(val num: UShort) : Expression {
    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context) = num
}

data class SignalExpression(val name: String) : Expression {
    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): UShort {
        if (name in state.path)
            throw IllegalStateException("Feedback is not supported ($name)")
        state.push(name)
        val res = state.getCached(name) ?: circuit.getGate(name).expression.evaluate(circuit, state)
        state.cache(name, res)
        state.pop(name)
        return res
    }
}

interface UnaryOperatorExpression : Expression {
    val expr: Expression
    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): UShort =
        apply(expr.evaluate(circuit, state))

    fun apply(evaluated: UShort): UShort

}

data class NotExpression(override val expr: Expression): UnaryOperatorExpression {
    override fun apply(evaluated: UShort): UShort = evaluated.inv()


    companion object {
        const val MNEMONIC = "NOT"
    }
}

interface BinaryOperatorExpression : Expression {
    val lhs: Expression
    val rhs: Expression

    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): UShort =
        apply(lhs.evaluate(circuit, state), rhs.evaluate(circuit, state))


    fun apply(lhs: UShort, rhs: UShort): UShort
}

data class AndExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: UShort, rhs: UShort): UShort = lhs and rhs


    companion object {
        const val MNEMONIC = "AND"
    }
}

data class OrExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: UShort, rhs: UShort): UShort = lhs or rhs


    companion object {
        const val MNEMONIC = "OR"
    }
}

data class RShiftExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: UShort, rhs: UShort): UShort = (lhs.toUInt() shr rhs.toInt()).toUShort()

    companion object {
        const val MNEMONIC = "RSHIFT"
    }

}

data class LShiftExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: UShort, rhs: UShort): UShort = (lhs.toUInt() shl rhs.toInt()).toUShort()

    companion object {
        const val MNEMONIC = "LSHIFT"
    }
}

data class Assignment(val name: String, val expression: Expression) {
    companion object {
        const val MNEMONIC = "->"
    }
}

class LogicalCircuit(assignments: Iterable<Assignment>) {
    val wires = assignments.associateBy { it.name }.toMutableMap()
    val cache: MutableMap<String, UShort> = mutableMapOf()


    fun getGate(name: String) = wires[name] ?: throw IllegalArgumentException("Unknown signal $name")

    fun evaluate(name: String) = getGate(name).expression.evaluate(this, Context().apply { push(name) })

    fun assign(assignment: Assignment) {
        cache.clear()
        wires[assignment.name] = assignment
    }

    inner class Context {
        val path: MutableSet<String> = mutableSetOf()
        fun push(name: String) = path.add(name)
        fun pop(name: String) = path.remove(name)

        fun cache(name: String, value: UShort) {
            cache[name] = value
        }

        fun getCached(name: String) = cache[name]
    }
}

object CircuitParser {
    private val OPERATORS = Terminals.operators(
        Assignment.MNEMONIC,
        NotExpression.MNEMONIC,
        AndExpression.MNEMONIC,
        OrExpression.MNEMONIC,
        RShiftExpression.MNEMONIC,
        LShiftExpression.MNEMONIC,
    )

    private val IGNORED = Scanners.WHITESPACES.skipMany()

    private val TOKENIZER =
        Parsers.or(OPERATORS.tokenizer(), Terminals.DecimalLiteral.TOKENIZER, Terminals.Identifier.TOKENIZER)

    private val USHORT: Parser<Expression> = Terminals.DecimalLiteral.PARSER.map {
        NumberExpression(it.toUShortOrNull() ?: throw IllegalArgumentException("$it - not a UShort interval"))
    }

    private val ASSIGNMENT_RHS = Terminals.Identifier.PARSER

    private val SIGNAL_NAME_EXPR: Parser<Expression> = Terminals.Identifier.PARSER.map {
        SignalExpression(it)
    }

    private val OPERAND: Parser<Expression> = USHORT.or(SIGNAL_NAME_EXPR)

    private val SHIFT_RIGHT_OPERAND: Parser<Expression> = USHORT

    private val NOT_OPERATOR =
        Parsers.sequence(OPERATORS.token(NotExpression.MNEMONIC), OPERAND) { _, operand ->
            NotExpression(operand)
        }

    private val AND_OPERATOR =
        Parsers.sequence(OPERAND, OPERATORS.token(AndExpression.MNEMONIC), OPERAND) { lhs, _, rhs ->
            AndExpression(lhs, rhs)
        }

    private val OR_OPERATOR =
        Parsers.sequence(OPERAND, OPERATORS.token(OrExpression.MNEMONIC), OPERAND) { lhs, _, rhs ->
            OrExpression(lhs, rhs)
        }

    private val RSHIFT_OPERATOR =
        Parsers.sequence(OPERAND, OPERATORS.token(RShiftExpression.MNEMONIC), SHIFT_RIGHT_OPERAND) { lhs, _, rhs ->
            RShiftExpression(lhs, rhs)
        }

    private val LSHIFT_OPERATOR =
        Parsers.sequence(OPERAND, OPERATORS.token(LShiftExpression.MNEMONIC), SHIFT_RIGHT_OPERAND) { lhs, _, rhs ->
            LShiftExpression(lhs, rhs)
        }

    private val EXPRESSION = Parsers.or(
        NOT_OPERATOR,
        AND_OPERATOR,
        OR_OPERATOR,
        AND_OPERATOR,
        RSHIFT_OPERATOR,
        LSHIFT_OPERATOR,
        USHORT,
        SIGNAL_NAME_EXPR
    )

    private val ASSIGNMENT =
        Parsers.sequence(EXPRESSION, OPERATORS.token(Assignment.MNEMONIC), ASSIGNMENT_RHS) { lhs, _, rhs ->
            Assignment(rhs, lhs)
        }

    private val PARSER = ASSIGNMENT.from(TOKENIZER, IGNORED)

    fun parse(string: String) = PARSER.parse(string)
}

fun makeCircuitAndEvaluateThenReassign(strings: Sequence<String>, signal: String = "a", toReassign: String = "b"): UShort {
    val circuit = LogicalCircuit(strings.map { CircuitParser.parse(it) }.asIterable())
    val value = circuit.evaluate(signal)
    circuit.assign(Assignment(toReassign, NumberExpression(value)))
    return circuit.evaluate(signal)
}
