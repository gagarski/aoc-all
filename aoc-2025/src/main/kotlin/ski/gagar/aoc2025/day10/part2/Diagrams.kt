package ski.gagar.aoc2025.day10.part2

import ski.gagar.aoc.util.math.LinearEquation
import ski.gagar.aoc.util.math.LinearEquationSystem
import ski.gagar.aoc.util.math.NaturalFraction
import ski.gagar.aoc.util.math.toNaturalFraction
import ski.gagar.aoc2025.day10.part1.Button
import ski.gagar.aoc2025.day10.part1.Diagram
import ski.gagar.aoc2025.day10.part1.Joltages
import java.math.BigInteger
import java.util.ArrayDeque

fun Joltages.isOutOfBounds(target: Joltages): Boolean {
    require(this.levels.size == target.levels.size)
    return levels.withIndex().any { (index, value) -> value > target[index] }
}

fun Joltages.press(button: Button): Joltages {
    val new = levels.toMutableList()

    for (i in new.indices) {
        if (button.toggles(i))
            new[i] = new[i] + 1
    }

    return Joltages(new)
}

private data class QueueItem(val joltages: Joltages, val pathLength: Int, val target: Joltages, val currentButton: Int = 0) {
    val heuristics: Int
        get() {
            require(joltages.levels.size == target.levels.size)
            require(joltages.levels.asSequence().withIndex().all { (ix, lv) -> lv <= target.levels[ix] })
            return joltages.levels.asSequence().withIndex().maxOf { (ix, lv) ->
                val diff = target.levels[ix] - lv
                require(diff >= 0)
                diff
            }
        }
}

private fun Diagram.linearEquationFor(ix: Int): LinearEquation {
    require(ix in 0..<requirements.levels.size)
    val free = requirements.levels[ix].toNaturalFraction()
    val buf = MutableList(buttons.size) { 0 }
    for ((bIx, button) in buttons.withIndex()) {
        if (button.toggles(ix)) buf[bIx] += 1
    }
    return LinearEquation(buf.map { it.toNaturalFraction() }, free)
}

private fun Diagram.equationSystem(): LinearEquationSystem =
    requirements.levels.indices.map { linearEquationFor(it) }.let { LinearEquationSystem(it) }

data class BruteForceItem(val index: Int, val max: Int) {
    val factor
        get() = max + 1
}
data class BruteForceDescriptor(val items: List<BruteForceItem>) {
    val factor
        get() = items.map { it.factor }.fold(1) { a, b -> a * b }
    val size
        get() = items.size
}

fun Diagram.bruteForceDescriptors(size: Int) = sequence {
    val stack = ArrayDeque<BruteForceDescriptor>()
    stack.push(BruteForceDescriptor(listOf()))

    while (stack.isNotEmpty()) {
        val desc = stack.pop()
        if (desc.size >= size) {
            yield(desc)
            continue
        }
        val startIndex = if (desc.size == 0) 0 else desc.items.last().index + 1
        for (nextIndex in startIndex..<buttons.size) {
            val nextItem = buttons[nextIndex].bruteForceItem(nextIndex, this@bruteForceDescriptors)
            stack.push(BruteForceDescriptor(desc.items + nextItem))
        }
    }
}

fun BruteForceDescriptor.extraEquations(size: Int) =
    sequence {
        for (item in items) {
            yield(List(size) { if (it == item.index) 1.toNaturalFraction() else 0.toNaturalFraction() })
        }
    }.toList()

fun Diagram.bruteForceExtraEquations(desc: BruteForceDescriptor): Sequence<List<LinearEquation>> = sequence {
    val stack = ArrayDeque<List<LinearEquation>>()
    stack.push(listOf())

    val base = desc.extraEquations(buttons.size)

    while (stack.isNotEmpty()) {
        val partial = stack.pop()

        if (partial.size >= desc.items.size) {
            yield(partial)
            continue
        }

        val nextIndex = partial.size
        val nextItem = desc.items[nextIndex]

        for (freeValue in 0..nextItem.max) {
            stack.push(partial + LinearEquation(base[nextIndex], freeValue.toNaturalFraction()))
        }
    }
}

private fun Button.bruteForceItem(index: Int, diagram: Diagram): BruteForceItem =
    diagram.requirements.levels.asSequence().withIndex()
        .filter { (index, level) -> toggles(index)}
        .map { (_, level) -> level }
        .min()
        .let {
            BruteForceItem(index, it)
        }

fun Diagram.solveForMinJoltages(): List<NaturalFraction>? {
    val baseSys = equationSystem()


    val red = baseSys.reduce()
    val excessEqCount = red.equations.take(buttons.size).count { it.isUniversal }
    val nExtra = maxOf(buttons.size - requirements.levels.size, 0) + excessEqCount
    val bfDescs = bruteForceDescriptors(nExtra).sortedBy { it.factor }.toList()
    val chosenDesc = bfDescs.first {
        val extraEq = it.extraEquations(buttons.size).map { coeffs -> LinearEquation(coeffs, 0.toNaturalFraction()) }
        val fullSysTest = LinearEquationSystem(baseSys.equations + extraEq)
        fullSysTest.solve() != null
    }
    var minSoFar: NaturalFraction? = null
    var minValue: List<NaturalFraction>? = null

    for (extraEq in bruteForceExtraEquations(chosenDesc).toList()) {
        val sys = LinearEquationSystem(baseSys.equations + extraEq)
        val solution = sys.solve()
        require(solution != null)

        if (solution.any { it < NaturalFraction.ZERO || it.denom != BigInteger.ONE }) {
            continue
        }
        val sum = solution.reduce { a, b -> a + b }

        if (minSoFar == null || sum < minSoFar) {
            minSoFar = sum
            minValue = solution
        }
    }
    return minValue
}



fun sumShortestPathsForJoltages(lines: Sequence<String>) =
    lines.sumOf { line ->
        val d = Diagram.parse(line)
        val sol = d.solveForMinJoltages()!!
        sol.sumOf { it.num }
    }
