package ski.gagar.aoc2025.day11.part1

import ski.gagar.aoc.util.graphs.Graph
import ski.gagar.aoc.util.graphs.GraphBuilder
import java.math.BigInteger
import java.util.Stack

private val FROM_TO_RE = """\s*:\s*""".toRegex()

fun buildGraph(lines: Sequence<String>): Graph<String> =
    GraphBuilder<String>().apply {
        for (line in lines) {
            val fromTo = line.split(FROM_TO_RE)
            require(fromTo.size == 2)
            val from = fromTo[0]
            val tos = fromTo[1].split(" ")
            addVertex(from)

            for (to in tos) {
                addVertex(to)
                addEdge(from, to)
            }
        }
    }.build()


sealed interface DfsOp<T>

class CompleteVertex<T>(val v: T) : DfsOp<T>
class ProcessVertex<T>(val v: T) : DfsOp<T>

fun <T> Graph<T>.countPaths(from: T, to: T): BigInteger {
    val memo = mutableMapOf<T, BigInteger>()
    memo[to] = BigInteger.ONE
    val stack = Stack<DfsOp<T>>()
    stack.push(ProcessVertex(from))

    while (!stack.empty()) {
        val op = stack.pop()

        when (op) {
            is CompleteVertex -> {
                require(memo[op.v] == null)
                memo[op.v] = getEdgesFrom(op.v).asSequence().sumOf { (dest, ) -> memo[dest] ?: BigInteger.ZERO }
            }
            is ProcessVertex -> {
                if (memo[op.v] != null) {
                    continue
                }
                stack.push(CompleteVertex(op.v))
                for ((next, _) in getEdgesFrom(op.v)) {
                    stack.push(ProcessVertex(next))
                }
            }
        }
    }

    return memo[from]!!
}

fun countPaths(lines: Sequence<String>, from: String = "you", to: String = "out"): BigInteger =
    buildGraph(lines).countPaths(from, to)