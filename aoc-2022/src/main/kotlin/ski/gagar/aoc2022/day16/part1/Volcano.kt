package ski.gagar.aoc2022.day16.part1

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import org.codehaus.jparsec.Terminals
import ski.gagar.aoc.util.getResourceAsStream

data class VolcanoNode(val name: String, val flowRate: Int, val neighbors: List<String>)

sealed interface Move

data class GoTo(val nextNode: String) : Move
object OpenCurrentValve : Move {
    override fun toString(): String = "OpenCurrentValve"
}

data class CourseOfAction(val moves: List<Move>, val result: Int)

private data class State(
    val currentCourse: CourseOfAction,
    val currentNode: VolcanoNode,
    val openedValves: PersistentSet<String>,
    val movesLeft: Int,
    val allNodes: Map<String, VolcanoNode>
)

private data class NextMoveAndState(val move: Move, val state: State)

private fun State.possibleMoves(): Sequence<NextMoveAndState> = sequence {
    if (currentNode.name !in openedValves && currentNode.flowRate != 0) {
        yield(NextMoveAndState(OpenCurrentValve, this@possibleMoves))
    }

    for (neighbor in currentNode.neighbors) {
        yield(NextMoveAndState(GoTo(neighbor), this@possibleMoves))
    }
}

private fun NextMoveAndState.apply(): State {
    val nextMoves = state.currentCourse.moves + move
    val newMovesLeft = state.movesLeft - 1
    return when (move) {
        is OpenCurrentValve -> {
            val newCourse = CourseOfAction(
                nextMoves, state.currentCourse.result + newMovesLeft * state.currentNode.flowRate
            )
            State(
                newCourse,
                state.currentNode,
                state.openedValves.add(state.currentNode.name),
                newMovesLeft,
                state.allNodes
            )
        }
        is GoTo -> {
            val newCourse = CourseOfAction(nextMoves, state.currentCourse.result)
            val nextNode = state.allNodes[move.nextNode]
            check(nextNode != null)
            State(
                newCourse,
                nextNode,
                state.openedValves,
                newMovesLeft,
                state.allNodes
            )
        }
    }
}

fun bestCourseOfAction(nodes: List<VolcanoNode>, startNode: String = "AA", limit: Int = 30): CourseOfAction? {
    val byName = nodes.associateBy { it.name }

    val start = byName[startNode]

    check(start != null)

    val stack = ArrayDeque<NextMoveAndState>()
    val startCourse = CourseOfAction(persistentListOf(), 0)
    val startState = State(startCourse, start, persistentSetOf(), limit, byName)

    for (move in startState.possibleMoves()) {
        stack.addLast(move)
    }

    var bestCourseSoFar: CourseOfAction? = null

    while (stack.isNotEmpty()) {
        val move = stack.removeFirst()

        if (move.state.movesLeft == 0) {
            if ((bestCourseSoFar == null || bestCourseSoFar.result < move.state.currentCourse.result)) {
                bestCourseSoFar = move.state.currentCourse
            }
            continue
        }

        val nextState = move.apply()

        var added = false

        for (nextMove in nextState.possibleMoves()) {
            stack.addLast(nextMove)
            added = true
        }

        if (!added) {
            if ((bestCourseSoFar == null || bestCourseSoFar.result < move.state.currentCourse.result)) {
                bestCourseSoFar = move.state.currentCourse
            }
        }
    }

    return bestCourseSoFar
}

object VolcanoParser {
    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private const val SEMICOLON = ";"
    private const val COMMA = ","
    private const val EQ = "="
    private const val NL = "\n"

    private const val CAPITAL_VALVE = "Valve"
    private const val HAS = "has"
    private const val FLOW = "flow"
    private const val RATE = "rate"
    private const val TUNNELS = "tunnels"
    private const val TUNNEL = "tunnel"
    private const val LEAD = "lead"
    private const val LEADS = "leads"
    private const val TO = "to"
    private const val VALVES = "valves"
    private const val VALVE = "valve"

    private val TERMINALS =
        Terminals.operators(SEMICOLON, COMMA, EQ, NL)
            .words(Scanners.IDENTIFIER)
            .keywords(
                CAPITAL_VALVE,
                HAS,
                FLOW,
                RATE,
                TUNNELS,
                TUNNEL,
                LEAD,
                LEADS,
                TO,
                VALVES,
                VALVE
            ).build()
    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val IDENTIFIER_TOKENIZER = Scanners.IDENTIFIER
    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER,
        IDENTIFIER_TOKENIZER
    )

    private val FLOW_RATE = Terminals.IntegerLiteral.PARSER.map { it.toInt() }

    private val SINGLE_VALVE = Terminals.identifier()
    private val LIST_OF_VALVES = SINGLE_VALVE.sepBy(TERMINALS.token(COMMA))

    private val VALVE_X_HAS_FLOW_RATE = Parsers.sequence(
        TERMINALS.token(CAPITAL_VALVE),
        SINGLE_VALVE,
        TERMINALS.phrase(HAS, FLOW, RATE, EQ),
        FLOW_RATE
    ) { _, valve, _, flowRate ->
        valve to flowRate
    }

    private val TUNNELS_LEAD_TO_VALVES = Parsers.sequence(
        TERMINALS.phrase(TUNNELS, LEAD, TO, VALVES),
        LIST_OF_VALVES
    )

    private val TUNNEL_LEADS_TO_VALVE = Parsers.sequence(
        TERMINALS.phrase(TUNNEL, LEADS, TO, VALVE),
        SINGLE_VALVE
    ) { _, valve ->
        listOf(valve)
    }

    private val VALVES_LIST = Parsers.or(
        TUNNELS_LEAD_TO_VALVES,
        TUNNEL_LEADS_TO_VALVE
    )

    private val VOLCANO_NODE = Parsers.sequence(
        VALVE_X_HAS_FLOW_RATE,
        TERMINALS.token(SEMICOLON),
        VALVES_LIST
    ) { (name, flowRate), _, valves ->
        VolcanoNode(name = name, flowRate = flowRate, neighbors = valves)
    }

    private val VOLCANO_NODES = VOLCANO_NODE.sepBy(TERMINALS.token(NL))

    fun parse(str: String) = VOLCANO_NODES.from(TOKENIZER, WHITESPACES).parse(str)
}

fun day16Part1() {
    println("day16/part1/volcano: ${
        bestCourseOfAction(
            VolcanoParser.parse(
                getResourceAsStream("/ski.gagar.aoc.aoc2022.day16/volcano.txt").bufferedReader().readText()
            )
        )?.result
    }")
}
