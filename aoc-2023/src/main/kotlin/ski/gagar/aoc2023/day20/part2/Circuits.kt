package ski.gagar.aoc2023.day20.part2

import ski.gagar.aoc2023.day20.part1.Circuit
import ski.gagar.aoc2023.day20.part1.CircuitsParser
import ski.gagar.aoc2023.day20.part1.Conjunctor
import ski.gagar.aoc2023.day20.part1.Pulse
import java.math.BigInteger

data class Captured(val destination: String = "rx", val polarity: Boolean = false)
data class CapturedWithSource(val source: String, val destination: String, val polarity: Boolean)

fun Pulse.matches(c: Captured) = destination == c.destination && polarity == c.polarity
fun Pulse.matchesSource(c: CapturedWithSource) =
    source == c.source && destination == c.destination && polarity == c.polarity
fun Pulse.unmatchesSource(c: CapturedWithSource) =
    source == c.source && destination == c.destination && polarity != c.polarity


fun Circuit.tryCapture(initPulse: Pulse = Pulse(), c: Captured = Captured()): Boolean {
    val queue = ArrayDeque<Pulse>()
    queue.add(initPulse)

    while (queue.isNotEmpty()) {
        val processed = queue.removeFirst()
        if (processed.matches(c)) {
            return true
        }

        val elem = gates[processed.destination]
            ?: continue
        val pulse = elem.pulse(processed.source, processed.polarity) ?: continue

        val newSource = processed.destination

        for (newDest in wires[newSource] ?: setOf()) {
            queue.add(Pulse(newSource, newDest, pulse))
        }
    }
    return false
}

private data class QueueItem(val pulse: Pulse, val time: Int = 0)

fun Circuit.tryCaptureLast(c: CapturedWithSource, initPulse: Pulse = Pulse()): IntRange? {
    val queue = ArrayDeque<QueueItem>()
    queue.add(QueueItem(initPulse))

    var lastTime = -1
    var capturedStart = -1
    var capturedEnd = -1

    while (queue.isNotEmpty()) {
        val (processed, layer) = queue.removeFirst()
        lastTime = layer
        if (processed.matchesSource(c)) {
            if (capturedStart != -1) {
                throw IllegalStateException("Should have only one spike of ${c.source}")
            }
            capturedStart = layer
            capturedEnd = -1
        } else if (processed.unmatchesSource(c) && capturedEnd == -1) {
            capturedEnd = layer
        }

        val elem = gates[processed.destination]
            ?: continue
        val pulse = elem.pulse(processed.source, processed.polarity) ?: continue

        val newSource = processed.destination

        for (newDest in wires[newSource] ?: setOf()) {
            queue.add(QueueItem(Pulse(newSource, newDest, pulse), layer + 1))
        }
    }

    if (capturedStart != -1 && capturedEnd == -1) {
        capturedEnd = lastTime
    }

    return if (capturedStart != -1) capturedStart..capturedEnd else null
}

fun Map<String, Set<String>>.collectInputs(dest: String, start: String = "broadcaster"): Set<String> {
    val queue = ArrayDeque<String>()
    queue.add(dest)

    val res = mutableSetOf<String>()

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current == start) continue

        if (current in res) continue

        res.add(current)

        val currentInputs = this[current] ?: setOf()

        for (input in currentInputs) {
            queue.add(input)
        }
    }

    return res
}

fun Circuit.nPressesLastConjNeg(
    target: String = "rx",
    start: String = "broadcaster",
    initPulse: Pulse = Pulse()
): BigInteger {
    require(gates[target] == null)
    val leadingToTarget = wires.filter { (k, v) -> target in v }
    require(leadingToTarget.size == 1)
    val conj = gates[leadingToTarget.keys.first()]

    require(conj is Conjunctor)

    val inputs = wires
        .flatMap {(source, dests) ->
            dests.map { it to source }
        }.groupBy {
            it.second
        }.mapValues { (k, v) ->
            v.asSequence().map { it.second }.toSet()
        }

    var prevInputs = mutableSetOf<String>()

    for (conjInput in conj.inputs) {
        val current = inputs.collectInputs(conjInput, start)

        if (current.any { it in prevInputs }) {
            throw IllegalStateException("Input $conjInput has a source from multiple parts of the" +
                    " scheme, this algorithm won't work")
        }
        prevInputs.addAll(current)
    }

    val patterns = conj.inputs.map {
        findPositivePulsePattern(it, conj.name, initPulse = initPulse)
    }

    val lastStart = patterns.maxOf { it.posRange.first }
    val firstEnd = patterns.minOf { it.posRange.last }

    if (firstEnd < lastStart) {
        throw IllegalStateException("Even though positive patterns found, " +
                "the ranges for them inside iteration are not intersecting")
    }

    return patterns.fold(BigInteger.ONE) { acc, input ->
        acc.lcm(BigInteger.valueOf(input.iters.toLong()))
    }
}

fun BigInteger.lcm(other: BigInteger) =
    this / this.gcd(other) * other

data class LoopSummary(val iters: Int, val posRange: IntRange)

fun Circuit.findPositivePulsePattern(source: String, dest: String, limit: Int = 1000000, initPulse: Pulse = Pulse()): LoopSummary {
    reset()
    val mem = mutableListOf<LoopSummary>()
    var i = 0

    while (true) {
        val captured = tryCaptureLast(CapturedWithSource(source, dest, true), initPulse)
        i++
        if (captured != null) {
            mem.add(LoopSummary(i, captured))
            if (mem.size == 3)
                break
        }

        if (i >= limit) {
            throw IllegalStateException("failed to find positive pulse pattern for $source")
        }
    }

    check(mem[1].iters - mem[0].iters == mem[2].iters - mem[1].iters) {
        "failed to find positive pulse pattern for $source"
    }

    check(mem.all { it.posRange == mem.first().posRange }) {
        "failed to find positive pulse pattern for $source"
    }

    return mem[0]
}

fun Circuit.nPresses(initPulse: Pulse = Pulse(), c: Captured = Captured()): Int {
    var i = 0

    while (true) {
        val t = tryCapture(initPulse, c)
        i++
        if (t && i == 4000) return i
    }
}

fun nPresses(input: String): BigInteger {
    val circuit = Circuit(CircuitsParser.parse(input))

    return circuit.nPressesLastConjNeg()
}