package ski.gagar.aoc2023.day8.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2023.day8.part1.NetworkMap
import ski.gagar.aoc2023.day8.part1.NetworkMapParser
import java.math.BigInteger

fun NetworkMap.followGhostLenSingle(source: String, destPred: (String) -> Boolean): Int {
    fun runUntilDestination(from: String): Pair<Int, String> {
        var current = source
        var steps = 0
        outer@ while (true) {
            for (instr in instructions) {
                if (destPred(current)) break@outer
                val dests = map[current] ?: throw IllegalStateException("Node $current not found")
                current = dests[instr] ?: throw IllegalStateException("Cannot follow instruction $instr from $current")
                steps++
            }
        }
        return steps to current
    }

    val res1 = runUntilDestination(source)

    if (res1.first % instructions.size != 0) {
        // My only thought on what to do in this case is to fall back to bruteforce algorithm
        // though for my input it would take forever
        throw IllegalStateException("This algorithm won't work for this input, destination is reached " +
                "in number of steps not divisible by instructions size (${instructions.size}")
    }

    val res2 = runUntilDestination(res1.second)

    if (res1 != res2) {
        // Current implementation works only if first and second destination matching destPred
        // This is ok for my implementation, yet we may do better in finding longer patterns
        throw IllegalStateException("This algorithm works only for repeating patterns of reaching the destination")
    }
    return res1.first
}

fun NetworkMap.followGhostLen(sourcePred: (String) -> Boolean, destPred: (String) -> Boolean): BigInteger {
    val lens = map.keys.filter(sourcePred).map { followGhostLenSingle(it, destPred) }

    return lens.asSequence().map {
        BigInteger.valueOf(it.toLong()) }.fold(BigInteger.ONE, ) { a, b ->
            (a / a.gcd(b)) * b
        }
}

fun nSteps(input: String,
           sourcePred: (String) -> Boolean = { it.endsWith("A") },
           destPred: (String) -> Boolean = { it.endsWith("Z") }) =
    NetworkMapParser.parse(input).followGhostLen(sourcePred, destPred)

fun day8Part2() {
    println(
        "day8/part2/networks: ${
            nSteps(
                getResourceAsStream("/ski.gagar.aoc.aoc2023.day8/networks.txt").bufferedReader().readText())
        }"
    )
}