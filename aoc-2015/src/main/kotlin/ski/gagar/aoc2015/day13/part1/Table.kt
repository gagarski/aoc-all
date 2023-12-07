package ski.gagar.aoc2015.day13.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import org.jparsec.pattern.CharPredicates
import ski.gagar.aoc.util.Edge
import ski.gagar.aoc.util.Graph
import ski.gagar.aoc.util.GraphBuilder
import ski.gagar.aoc.util.getResourceAsStream


data class NextPath(
    val edge: Edge,
    val visited: Set<String>,
    val path: List<Edge> = listOf(),
) {
    val length = path.sumOf { it.weight }
}

val List<Edge>.pathLength
    get() = sumOf { it.weight }

fun Graph.getLongestPathFrom(vertex: String, longestLengthSoFar: Int? = null): List<Edge>? {
    var longestPathSoFar = longestLengthSoFar
    var longestPathLocal: List<Edge>? = null
    val stack = ArrayDeque<NextPath>()


    for ((_, edge) in getEdgesFrom(vertex)) {
        stack.addLast(NextPath(edge, linkedSetOf(vertex)))
    }

    while (stack.isNotEmpty()) {
        val current = stack.removeLast()
        if (current.edge.to in current.visited && !(current.visited.size == vertices.size && current.edge.to == vertex)) {
            continue
        }

        val newLength = current.length + current.edge.weight

        val newVisited = current.visited + current.edge.to
        val newPath = current.path + current.edge

        if (current.visited.size == vertices.size && current.edge.to == vertex) {
            if (longestPathSoFar == null || newLength > longestPathSoFar) {
                longestPathSoFar = newLength
                longestPathLocal = newPath
            }
            continue
        }

        for ((_, edge) in getEdgesFrom(current.edge.to)) {
            stack.addLast(NextPath(edge, newVisited, newPath))
        }
    }

    return longestPathLocal
}

fun Graph.findLongestPath(): List<Edge>? = getLongestPathFrom(vertices.first(), null)

data class SittingCondition(val first: String, val second: String, val weight: Int)

object TableParser {
    enum class GainLose(val coeff: Int) {
        GAIN(1),
        LOSE(-1)
    }

    private val NAME = Terminals.Identifier.TOKENIZER
    private val WOULD = Scanners.string("would")
    private val GAIN = Scanners.string("gain"). map {
        GainLose.GAIN
    }
    private val LOSE = Scanners.string("lose"). map {
        GainLose.LOSE
    }
    private val AMOUNT = Terminals.IntegerLiteral.TOKENIZER.map {
        it.text().toInt()
    }
    private val HAPPINESS = Scanners.string("happiness")
    private val UNIT = Scanners.string("unit")
    private val UNITS = Scanners.string("units")
    private val BY = Scanners.string("by")
    private val SITTING = Scanners.string("sitting")
    private val NEXT = Scanners.string("next")
    private val TO = Scanners.string("to")

    private val GAIN_OR_LOSE = Parsers.or(
        GAIN, LOSE
    )

    private val HAPPINESS_UNITS = Parsers.sequence(
        HAPPINESS,
        Scanners.WHITESPACES,
        Parsers.or(UNITS, UNIT)
    )

    private val BY_SITTING_NEXT_TO = Parsers.sequence(
        BY,
        Scanners.WHITESPACES,
        SITTING,
        Scanners.WHITESPACES,
        NEXT,
        Scanners.WHITESPACES,
        TO,
    )

    private val ZERO_OR_MORE_WHITESPACES = Scanners.isChar(CharPredicates.IS_WHITESPACE).many()

    private val FULL_STOP = Scanners.isChar('.')

    private data class IntermediateResult(val name: String, val amount: Int)

    private val SEAT = Parsers.sequence(
        Parsers.sequence(
            NAME,
            Parsers.sequence(Scanners.WHITESPACES, WOULD, Scanners.WHITESPACES),
            GAIN_OR_LOSE,
            Scanners.WHITESPACES,
            AMOUNT) { name, _, gOrL, _, amount ->
            IntermediateResult(name.text(), amount * gOrL.coeff)
        },
        Parsers.sequence(
            Scanners.WHITESPACES,
            HAPPINESS_UNITS,
            Scanners.WHITESPACES,
            BY_SITTING_NEXT_TO,
            Scanners.WHITESPACES
        ),
        Parsers.sequence(
            NAME,
            ZERO_OR_MORE_WHITESPACES,
            FULL_STOP
        ) { name, _, _ ->
            name.text()
        }
    ) { intermediate, _, second ->
        SittingCondition(intermediate.name, second, intermediate.amount)
    }

    fun parse(text: String) = SEAT.parse(text)
}

fun sittingGraph(strings: Sequence<String>): Graph {
    val builder = GraphBuilder()
    for (string in strings) {
        val cond = TableParser.parse(string)
        val currentWeightDirect = builder.getEdge(cond.first, cond.second)?.weight ?: 0
        builder.addVertex(cond.first)
        builder.addVertex(cond.second)
        builder.addEdge(cond.first, cond.second, currentWeightDirect + cond.weight)
        val currentWeightReverse = builder.getEdge(cond.second, cond.first)?.weight ?: 0
        builder.addEdge(cond.second, cond.first, currentWeightReverse + cond.weight)
    }

    return builder.build()
}

fun optimalHappiness(strings: Sequence<String>) = sittingGraph(strings).findLongestPath()?.pathLength


fun day13Part1() {
    println("day13/part1/table: ${
        optimalHappiness(getResourceAsStream("/ski.gagar.aoc.aoc2015.day13/table.txt").bufferedReader().lineSequence())
    }")
}
