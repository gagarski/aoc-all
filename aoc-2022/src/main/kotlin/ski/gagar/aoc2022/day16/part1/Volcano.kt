package ski.gagar.aoc2022.day16.part1

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import org.codehaus.jparsec.Terminals
import ski.gagar.aoc.util.Graph
import ski.gagar.aoc.util.GraphBuilder
import ski.gagar.aoc.util.getResourceAsStream
import java.util.*

data class VolcanoNode(val name: String, val flowRate: Int, val neighbors: List<String>)

sealed interface Move

data class GoTo(val nextNode: String) : Move
object OpenCurrentValve : Move {
    override fun toString(): String = "OpenCurrentValve"
}

data class CourseOfAction(val moves: List<Move>, val result: Int)

data class ShortestPath(val path: List<Move>, val length: Int)

data class ShortestPaths(val from: String, val lengths: Map<String, Int>, val predecessors: Map<String, String>) {
    fun to(to: String): ShortestPath {
        val length = lengths[to]!!
        val path = mutableListOf<Move>()
        var current: String = to
        while (current != from) {
            path.add(GoTo(current))
            current = predecessors[current]!!
        }

        return ShortestPath(path.reversed(), length)
    }
}

class VolcanoGraph(nodes: List<VolcanoNode>) {
    val nodes = nodes.associateBy { it.name }

    fun shortestPaths(from: String): ShortestPaths {
        val lengths = mutableMapOf<String, Int>()
        val predecessors = mutableMapOf<String, String>()
        val visited = mutableSetOf<String>()
        val queue = PriorityQueue<Pair<String, Int>>(Comparator.comparing { it.second })

        lengths[from] = 0

        queue.add(from to lengths[from]!!)

        while (queue.isNotEmpty()) {
            val (current, weight) = queue.remove()

            if (current in visited) {
                continue
            }

            visited.add(current)

            val currentNode = nodes[current]!!

            for (neighbor in currentNode.neighbors) {
                val currentLengthNeighour = lengths[neighbor]
                val newLength = lengths[current]!! + 1

                if (currentLengthNeighour == null || newLength < currentLengthNeighour) {
                    lengths[neighbor] = newLength
                    predecessors[neighbor] = current
                }
                queue.add(neighbor to newLength)
            }
        }

        return ShortestPaths(from, lengths, predecessors)
    }
}

private data class ReducedMove(val to: String)

private data class State(
    val moves: PersistentList<Move>,
    val result: Int,
    val currentNode: String,
    val visitedNodes: PersistentSet<String>,
    val openedValves: PersistentSet<String>,
    val movesLeft: Int,
    val allNodes: Map<String, VolcanoNode>,
    val reducedGraph: Graph,
    val expansions: Map<Pair<String, String>, ShortestPath>
)

private data class NextMoveAndState(val move: ReducedMove, val state: State)

private fun State.possibleMoves(): Sequence<NextMoveAndState> = sequence {
    for ((to, edge) in reducedGraph.getEdgesFrom(currentNode)) {
        val neighborNode = allNodes[to]
        check(neighborNode != null)
        if (to in visitedNodes) continue

        if (edge.weight + 1 > movesLeft) continue
        yield(NextMoveAndState(ReducedMove(to), this@possibleMoves))
    }
}


private fun NextMoveAndState.apply(): State {
    val path = state.expansions[state.currentNode to move.to]!!
    val toNode = state.allNodes[move.to]!!
    val nextMoves = state.moves.addAll(path.path).add(OpenCurrentValve)

    val newMovesLeft = state.movesLeft - path.length - 1
    return State(
        nextMoves,
        state.result + newMovesLeft * toNode.flowRate,
        move.to,
        state.visitedNodes.add(state.currentNode),
        state.openedValves.add(move.to),
        newMovesLeft,
        state.allNodes,
        state.reducedGraph,
        state.expansions
    )
}

fun bestCourseOfAction(nodes: List<VolcanoNode>, startNode: String = "AA", limit: Int = 30): CourseOfAction? {
    val graph = VolcanoGraph(nodes)
    val meaningfulNodes = nodes.asSequence().filter { it.flowRate > 0 || it.name == startNode }.toSet()

    val expansions = mutableMapOf<Pair<String, String>, ShortestPath>()

    for (node in meaningfulNodes) {
        val paths = graph.shortestPaths(node.name)

        for (node2 in meaningfulNodes) {
            if (node2.name == node.name)
                continue

            val path = paths.to(node2.name)

            expansions[node.name to node2.name] = path
        }
    }

    val bld = GraphBuilder()

    for (node in meaningfulNodes) {
        bld.addVertex(node.name)
    }

    for ((k, path) in expansions) {
        val (from, to) = k
        bld.addEdge(from, to, path.length)
    }

    val reducedGraph = bld.build()

    val stack = ArrayDeque<NextMoveAndState>()
    val startState = State(persistentListOf(), 0, startNode, persistentSetOf(), persistentSetOf(),
        limit, graph.nodes, reducedGraph, expansions)

    for (move in startState.possibleMoves()) {
        stack.addLast(move)
    }
    var bestStateSoFar: State? = null

    var i = 0

    while (stack.isNotEmpty()) {
        i++
        val move = stack.removeFirst()

        if (move.state.movesLeft == 0) {
            if ((bestStateSoFar == null || bestStateSoFar.result < move.state.result)) {
                bestStateSoFar = move.state
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
            if ((bestStateSoFar == null || bestStateSoFar.result < nextState.result)) {
                bestStateSoFar = nextState
            }
        }
    }

    return bestStateSoFar?.let {
        CourseOfAction(it.moves, it.result)
    }

}

object VolcanoParser {
    private val NON_BR_WHITESPACES = setOf(' ', '\t')
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private const val SEMICOLON = ";"
    private const val COMMA = ","
    private const val EQ = "="
    private const val NL = "\n"
    private const val NL_WIN = "\r\n"

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
        Terminals.operators(SEMICOLON, COMMA, EQ, NL, NL_WIN)
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

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

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

    private val VOLCANO_NODES = VOLCANO_NODE.sepBy(NEWLINE)

    private val VOLCANO_NODES_WITH_NL = Parsers.sequence(
        NEWLINE.many(),
        VOLCANO_NODES,
        NEWLINE.many()
    ) { _, nodes, _ ->
        nodes
    }

    fun parse(str: String) = VOLCANO_NODES_WITH_NL.from(TOKENIZER, WHITESPACES).parse(str)
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
