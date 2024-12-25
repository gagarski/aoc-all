package ski.gagar.aoc2024.day24.part2

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals

sealed interface Expression {
    fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): Boolean?
}

sealed interface ConstExpression: Expression {
    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): Boolean? = const
    val const: Boolean?
}

data object TrueExpression : ConstExpression {
    override val const: Boolean = true

}

data object FalseExpression : ConstExpression {
    override val const: Boolean = false
}

data object NullExpression : ConstExpression {
    override val const = null
}

data class SignalExpression(val name: String) : Expression {
    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): Boolean? {
        if (name in state.path)
            return null
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

sealed interface BinaryOperatorExpression : Expression {
    val lhs: Expression
    val rhs: Expression

    override fun evaluate(circuit: LogicalCircuit, state: LogicalCircuit.Context): Boolean? =
        apply(lhs.evaluate(circuit, state), rhs.evaluate(circuit, state))


    fun apply(lhs: Boolean?, rhs: Boolean?): Boolean?
}

data class AndExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: Boolean?, rhs: Boolean?): Boolean? = when {
        lhs == false || rhs == false -> false
        lhs == true && rhs == true -> true
        else -> null
    }

    companion object {
        const val MNEMONIC = "AND"
    }
}

data class OrExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: Boolean?, rhs: Boolean?): Boolean? = when {
        lhs == true || rhs == true -> true
        lhs == false && rhs == false -> false
        else -> null
    }

    companion object {
        const val MNEMONIC = "OR"
    }
}

data class XorExpression(override val lhs: Expression, override val rhs: Expression) : BinaryOperatorExpression {
    override fun apply(lhs: Boolean?, rhs: Boolean?): Boolean? = when {
        lhs == null || rhs == null -> null
        else -> lhs xor rhs
    }

    companion object {
        const val MNEMONIC = "XOR"
    }
}

class LogicalCircuit(assignments: List<Assignment>) {
    val wires = assignments.associateBy { it.name }
    val cache: MutableMap<String, Boolean?> = mutableMapOf()


    fun getGate(name: String) = wires[name] ?: throw IllegalArgumentException("Unknown signal $name")

    fun evaluate(name: String): Boolean? {
        val cached = cache[name] ?: getGate(name).expression.evaluate(this, Context().apply { push(name) })
        cache[name] = cached
        return cached
    }

    fun evaluateAll() {
        for (wire in wires.keys) {
            evaluate(wire)
        }
    }

    inner class Context {
        val path: MutableSet<String> = mutableSetOf()
        fun push(name: String) = path.add(name)
        fun pop(name: String) = path.remove(name)

        fun cache(name: String, value: Boolean?) {
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

private val XY_RE = """([xy])([0-9]+)""".toRegex()
private val Z_RE = """z([0-9]+)""".toRegex()

fun LogicalCircuit.debugForBit(n: Int, x: ConstExpression, y: ConstExpression,
                               prevX: ConstExpression, prevY: ConstExpression): LogicalCircuit {
    val newWires = wires.toMutableMap()
    for (k in newWires.keys) {
        val match = XY_RE.matchEntire(k) ?: continue
        val isX = match.groupValues[1] == "x"
        val isY = match.groupValues[1] == "y"
        check(isX || isY)
        val num = match.groupValues[2].toInt()
        when {
            num < n - 1 -> newWires[k] = Assignment(FalseExpression, k)
            num == n && isX -> newWires[k] = Assignment(x, k)
            num == n - 1 && isX -> newWires[k] = Assignment(prevX, k)
            num == n && isY -> newWires[k] = Assignment(y, k)
            num == n - 1 && isY -> newWires[k] = Assignment(prevY, k)
            else -> newWires[k] = Assignment(NullExpression, k)
        }
    }
    return LogicalCircuit(newWires.values.toList())
}

fun Boolean?.expr() = when (this) {
    true -> TrueExpression
    false -> FalseExpression
    null -> NullExpression
}

class Replacement(from: String, to: String) {
    val first: String
    val second: String

    init {
        require(from != to)

        if (from < to) {
            first = from
            second = to
        } else {
            first = to
            second = from
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Replacement

        if (first != other.first) return false
        if (second != other.second) return false

        return true
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        return result
    }

    override fun toString(): String {
        return "Replacement(first='$first', second='$second')"
    }


}

fun Assignment.withReplacement(replacement: Replacement): Assignment = when (name) {
    replacement.first -> Assignment(expression, replacement.second)
    replacement.second -> Assignment(expression, replacement.first)
    else -> this
}

fun LogicalCircuit.withReplacement(replacement: Replacement): LogicalCircuit {
    val newWires = wires.values.map {
        it.withReplacement(replacement)
    }
    return LogicalCircuit(newWires)
}

data class ValidationResult(val isValid: Boolean, val cascade: Set<String>)

fun LogicalCircuit.validateBit(name: String): ValidationResult {
    val match = Z_RE.matchEntire(name) ?: error("Cannot validate $name")
    val num = match.groupValues[1].toInt()
    val prevNum = (num - 1).toString().padStart(2, '0')
    val curNum = num.toString().padStart(2, '0')
    val prevX = wires["x$prevNum"]
    val prevY = wires["y$prevNum"]
    val curX = wires["x$curNum"]
    val curY = wires["y$curNum"]

    require((prevX != null) == (prevY != null))
    require((curX != null) == (curY != null))

    var cascade: MutableSet<String>? = null

    val isValid = testCasesFiltered(curX != null, prevX != null).all { case ->
        val debugCircuit = this.debugForBit(num, case.x.expr(), case.y.expr(), case.prevX.expr(), case.prevY.expr())
        val eval = debugCircuit.evaluate(name)
        debugCircuit.evaluateAll()

        val caseCascade = debugCircuit.cache.asSequence().filter { (k, v) -> v != null }.map { it.key }.toMutableSet()

        if (cascade == null) {
            cascade = caseCascade
        } else {
            cascade!!.retainAll(caseCascade)
        }

        check(eval != null)
        eval == case.expectedZ
    }

    return ValidationResult(isValid, cascade!!)
}


data class TestCase(val x: Boolean, val y: Boolean, val prevX: Boolean, val prevY: Boolean, val expectedZ: Boolean)

fun testCases() = sequence {
    yield(TestCase(false, false, false, false, false))
    yield(TestCase(false, true, false, false, true))
    yield(TestCase(true, false, false, false, true))
    yield(TestCase(true, true, false, false, false))

    yield(TestCase(false, false, false, true, false))
    yield(TestCase(false, true, false, true, true))
    yield(TestCase(true, false, false, true, true))
    yield(TestCase(true, true, false, true, false))

    yield(TestCase(false, false, true, false, false))
    yield(TestCase(false, true, true, false, true))
    yield(TestCase(true, false, true, false, true))
    yield(TestCase(true, true, true, false, false))

    yield(TestCase(false, false, true, true, true))
    yield(TestCase(false, true, true, true, false))
    yield(TestCase(true, false, true, true, false))
    yield(TestCase(true, true, true, true, true))
}

fun testCasesFiltered(hasCurrent: Boolean, hasPrev: Boolean): Sequence<TestCase> {
    require(hasCurrent || hasPrev)
    return testCases().filter {
        if (!hasCurrent)
            it.x == false && it.y == false
        else if (!hasPrev)
            it.prevX == false && it.prevY == false
        else
            true
    }
}

fun getFaultyGates(circuit: LogicalCircuit): Set<Replacement>? {
    val zs = circuit.wires.keys.filter { it matches Z_RE }.sorted()
    var c = circuit
    var prevCascadeFull: Set<String>? = null
    val replacements: MutableSet<Replacement> = mutableSetOf()
    val protected: MutableSet<String> = mutableSetOf()
    val cascades = mutableMapOf<String, Set<String>>()
    var prev: String? = null
    z@for (z in zs) {
        val valRes = c.validateBit(z)
        val curCascadeFull = valRes.cascade
        val curCascade =
            if (prevCascadeFull == null)
                curCascadeFull
            else
                curCascadeFull.filter { it !in prevCascadeFull!! }.toSet()
        cascades[z] = curCascade
        val prevCascade = if (prev != null) cascades[prev]!! else emptySet()

        if (!valRes.isValid) {
            val toCheck = (curCascade + prevCascade - protected).toList()

            for (i in toCheck.indices) {
                for (j in i + 1 ..< toCheck.size) {
                    val first = toCheck[i]
                    val second = toCheck[j]
                    val repl = Replacement(first, second)
                    val fixed = c.withReplacement(repl)
                    val newValRes = fixed.validateBit(z)
                    if (newValRes.isValid) {
                        replacements.add(repl)
                        protected.add(first)
                        protected.add(second)
                        c = fixed
                        continue@z
                    }
                }
            }
            error("Failed to find fix for $z")
        }

        prevCascadeFull = curCascadeFull
        prev = z
    }

    return replacements

}


fun getFaultyGates(text: String): String {
    val repl = getFaultyGates(LogicalCircuit(CircuitParser.parse(text)))
    return (repl!!.map { it.first } + repl.map { it.second }).sorted().joinToString(",")
}
