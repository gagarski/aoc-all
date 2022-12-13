package ski.gagar.aoc2022.day13.part1

import org.codehaus.jparsec.Parser
import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import org.codehaus.jparsec.Terminals
import org.codehaus.jparsec.Terminals.IntegerLiteral
import org.codehaus.jparsec.Tokens.Fragment
import ski.gagar.aoc.util.getResourceAsStream
import java.lang.IllegalStateException

sealed class Signal : Comparable<Signal> {
    override fun compareTo(other: Signal): Int = when {
        this is IntSignal && other is IntSignal -> doCompare(other)
        this is ListSignal && other is ListSignal -> doCompare(other)
        this is IntSignal && other is ListSignal -> this.asListSignal().doCompare(other)
        this is ListSignal && other is IntSignal -> doCompare(other.asListSignal())
        else -> throw IllegalStateException("Should not happen")
    }
}

data class IntSignal(val int: Int) : Signal() {
    fun doCompare(other: IntSignal) = compareValues(int, other.int)
    fun asListSignal() = ListSignal(listOf(this))
    override fun toString(): String = int.toString()
}

data class ListSignal(val list: List<Signal>) : Signal() {
    fun doCompare(other: ListSignal): Int {
        for (i in 0 until maxOf(list.size, other.list.size)) {
            val thisI = this.list.getOrNull(i)
            val otherI = other.list.getOrNull(i)

            when {
                thisI == null -> return -1
                otherI == null -> return 1
                thisI < otherI -> return -1
                thisI > otherI -> return 1
            }
        }
        return 0
    }

    override fun toString(): String = list.toString()
}

fun Pair<Signal, Signal>.isCorrectOrder() = first < second

object SignalParser {
    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val TERMINALS =
        Terminals.operators("[", "]", ",", "\n")
    private val INT_TOKENIZER = IntegerLiteral.TOKENIZER

    private val WHITESPACES = Scanners.isChar { it in  NON_BR_WHITESPACES }.optional()
    private val TOKENIZER = TERMINALS.tokenizer().cast<Fragment>().or(INT_TOKENIZER)

    private val INT: Parser<IntSignal> = IntegerLiteral.PARSER.map { IntSignal(it.toInt()) }
    private val SIGNALS_REF = Parser.newReference<ListSignal>()
    private val LIST: Parser<ListSignal> = SIGNALS_REF.lazy().between(TERMINALS.token("["), TERMINALS.token("]"))

    private val SIGNAL: Parser<Signal> = Parsers.or(INT, LIST)

    init {
        SIGNALS_REF.set(SIGNAL.sepBy(TERMINALS.token(",")).map {
            ListSignal(it)
        })
    }

    fun parse(str: String) = SIGNAL.from(TOKENIZER, WHITESPACES).parse(str)

}

fun parseListOfPairs(seq: Sequence<String>) =
    seq.filter { it.isNotBlank() }
        .map { SignalParser.parse(it) }
        .chunked(2)
        .map {
            check(it.size == 2)
            it[0] to it[1]
        }

fun getSumOfIndices(seq: Sequence<String>) =
    parseListOfPairs(seq).withIndex()
        .map { (ix, v) -> ix + 1 to v }
        .filter { (_, v) -> v.isCorrectOrder() }
        .sumOf { (ix, _) -> ix }

fun day13Part1() {
    println("day13/part1/signals: ${
        getSumOfIndices(getResourceAsStream("/ski.gagar.aoc.aoc2022.day13/signals.txt").bufferedReader().lineSequence())
    }")
}
