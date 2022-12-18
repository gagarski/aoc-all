package ski.gagar.aoc2022.day16.part2

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import ski.gagar.aoc.util.Graph
import ski.gagar.aoc.util.GraphBuilder
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2022.day16.part1.Move
import ski.gagar.aoc2022.day16.part1.OpenCurrentValve
import ski.gagar.aoc2022.day16.part1.ShortestPath
import ski.gagar.aoc2022.day16.part1.VolcanoGraph
import ski.gagar.aoc2022.day16.part1.VolcanoNode
import ski.gagar.aoc2022.day16.part1.VolcanoParser
import java.util.*

data class CourseOfAction(
    val myMoves: List<Move>,
    val elephantMoves: List<Move>,
    val result: Int
)


sealed interface ReducedMove {
    val to: String
}
private data class MyMove(override val to: String) : ReducedMove
private data class ElephantMove(override val to: String) : ReducedMove

private data class State(
    val myMoves: PersistentList<Move>,
    val elephantMoves: PersistentList<Move>,
    val result: Int,
    val myCurrentNode: String,
    val elephantCurrentNode: String,
    val visitedNodes: PersistentSet<String>,
    val openedValves: PersistentSet<String>,
    val myMovesLeft: Int,
    val elephantMovesLeft: Int,
    val allNodes: Map<String, VolcanoNode>,
    val reducedGraph: Graph,
    val expansions: Map<Pair<String, String>, ShortestPath>
)
private data class NextMoveAndState(val move: ReducedMove, val state: State)

private fun State.possibleMoves(): Sequence<NextMoveAndState> = sequence {
    val myNeighbors = reducedGraph.getEdgesFrom(myCurrentNode)
    val elephantNeighbors = reducedGraph.getEdgesFrom(elephantCurrentNode)


    for ((to, edge) in elephantNeighbors) {
        if (to in visitedNodes) continue
        if (elephantMovesLeft < edge.weight) continue
        yield(NextMoveAndState(ElephantMove(to), this@possibleMoves))
    }

    for ((to, edge) in myNeighbors) {
        if (to in visitedNodes) continue
        if (myMovesLeft < edge.weight) continue
        yield(NextMoveAndState(MyMove(to), this@possibleMoves))
    }
}

private fun NextMoveAndState.apply(): State = when (move) {
    is ElephantMove -> {
        val path = state.expansions[state.elephantCurrentNode to move.to]!!
        val toNode = state.allNodes[move.to]!!
        val nextElephantMoves = state.elephantMoves.addAll(path.path).add(OpenCurrentValve)
        val newElephantMovesLeft = state.elephantMovesLeft - path.length - 1

        State(
            myMoves = state.myMoves,
            elephantMoves = nextElephantMoves,
            result = state.result + newElephantMovesLeft * toNode.flowRate,
            myCurrentNode = state.myCurrentNode,
            elephantCurrentNode = move.to,
            visitedNodes = state.visitedNodes.add(move.to),
            openedValves = state.openedValves.add(move.to),
            myMovesLeft = state.myMovesLeft,
            elephantMovesLeft = newElephantMovesLeft,
            allNodes = state.allNodes,
            reducedGraph = state.reducedGraph,
            expansions = state.expansions
        )
    }
    is MyMove -> {
        val path = state.expansions[state.myCurrentNode to move.to]!!
        val toNode = state.allNodes[move.to]!!
        val nextMyMoves = state.myMoves.addAll(path.path).add(OpenCurrentValve)
        val newMyMovesLeft = state.myMovesLeft - path.length - 1

        State(
            myMoves = nextMyMoves,
            elephantMoves = state.elephantMoves,
            result = state.result + newMyMovesLeft * toNode.flowRate,
            myCurrentNode = move.to,
            elephantCurrentNode = state.elephantCurrentNode,
            visitedNodes = state.visitedNodes.add(move.to),
            openedValves = state.openedValves.add(move.to),
            myMovesLeft = newMyMovesLeft,
            elephantMovesLeft = state.elephantMovesLeft,
            allNodes = state.allNodes,
            reducedGraph = state.reducedGraph,
            expansions = state.expansions
        )
    }
}

fun bestCourseOfAction(nodes: List<VolcanoNode>, startNode: String = "AA", limit: Int = 26): CourseOfAction? {
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
    val startState = State(
        myMoves = persistentListOf(),
        elephantMoves = persistentListOf(),
        result = 0,
        myCurrentNode = startNode,
        elephantCurrentNode = startNode,
        visitedNodes = persistentSetOf(startNode),
        openedValves = persistentSetOf(),
        myMovesLeft = limit,
        elephantMovesLeft = limit,
        allNodes = graph.nodes,
        reducedGraph = reducedGraph,
        expansions = expansions
    )

    for (move in startState.possibleMoves()) {
        stack.addLast(move)
    }
    var bestStateSoFar: State? = null

    while (stack.isNotEmpty()) {
        val move = stack.removeFirst()

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
        CourseOfAction(it.myMoves, it.elephantMoves, it.result)
    }

}


fun day16Part2() {
    println("day16/part2/volcano: ${
        bestCourseOfAction(
            VolcanoParser.parse(
                getResourceAsStream("/ski.gagar.aoc.aoc2022.day16/volcano.txt").bufferedReader().readText()
            )
        )
    }")
}
