package ski.gagar.aoc2022.day16.part1

import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import org.codehaus.jparsec.Terminals
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

private class VolcanoGraph(nodes: List<VolcanoNode>) {
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

fun bestCourseOfAction(nodes: List<VolcanoNode>, startNode: String = "AA", limit: Int = 30): CourseOfAction? {
    var remainingLimit = limit
    val graph = VolcanoGraph(nodes)
    val withPositiveFlowRate = nodes.filter { it.flowRate > 0 }.toSet()
    val openedValves = mutableSetOf<String>()
    val result = mutableListOf<Move>()

    var currentShortestPaths = graph.shortestPaths(startNode)
    val steps = mutableListOf<Move>()
    var currentGain = 0

    while (remainingLimit != 0) {
        val toConsider =
            withPositiveFlowRate.filter { it.name !in openedValves && currentShortestPaths.lengths[it.name]!! < remainingLimit + 1 }

        var nextNode: String? = null
        var maxGain: Int? = null
        var newShortestPaths: ShortestPaths? = null

        for (nodeWithValve in toConsider) {
            val shortestPathsCandidate = graph.shortestPaths(nodeWithValve.name)

            val losses = toConsider
                .asSequence()
                .map { it.name }
                .filter { it != nodeWithValve.name }
                .map {
                    it to (currentShortestPaths.lengths[nodeWithValve.name]!! + 1 + shortestPathsCandidate.lengths[it]!! - currentShortestPaths.lengths[it]!!) * graph.nodes[it]!!.flowRate
                }
                .toList()

//            val condGains = toConsider
//                .asSequence()
//                .map { it.name }
//                .filter { it != nodeWithValve.name }
//                .map {
//                    it to (remainingLimit - shortestPathsCandidate.lengths[it]!! - 1) * graph.nodes[it]!!.flowRate
//                }
//                .toList()
            val gain = (remainingLimit - currentShortestPaths.lengths[nodeWithValve.name]!! - 1) * (graph.nodes[nodeWithValve.name]!!.flowRate)
            val totalGain = gain - losses.sumOf { it.second }

            if (maxGain == null || totalGain > maxGain) {
                maxGain = totalGain
                nextNode = nodeWithValve.name
                newShortestPaths = shortestPathsCandidate
            }
        }

        if (nextNode == null) {
            return CourseOfAction(result, currentGain)
        }

        val toTravel = currentShortestPaths.to(nextNode)
        currentGain += (remainingLimit - toTravel.length - 1) * graph.nodes[nextNode]!!.flowRate
        steps.addAll(toTravel.path)
        currentShortestPaths = newShortestPaths!!
        openedValves.add(nextNode)
        result.addAll(toTravel.path)
        result.add(OpenCurrentValve)
        remainingLimit -= (toTravel.length + 1)
    }

    return CourseOfAction(steps, currentGain)

}
// Вычислим кол-во пробелов перед выражением "-$subtrahend" ОТНОСИТЕЛЬНО minuend
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
        )
    }")
}
