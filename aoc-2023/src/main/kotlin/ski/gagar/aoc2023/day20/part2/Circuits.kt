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

fun Circuit.tryCaptureLast(c: CapturedWithSource, initPulse: Pulse = Pulse()): Boolean {
    val queue = ArrayDeque<Pulse>()
    queue.add(initPulse)

    var captured = false

    while (queue.isNotEmpty()) {
        val processed = queue.removeFirst()
        if (processed.matchesSource(c)) {
            captured = true
        }

        val elem = gates[processed.destination]
            ?: continue
        val pulse = elem.pulse(processed.source, processed.polarity) ?: continue

        val newSource = processed.destination

        for (newDest in wires[newSource] ?: setOf()) {
            queue.add(Pulse(newSource, newDest, pulse))
        }
    }
    return captured
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

    return conj.inputs.fold(BigInteger.ONE) { acc, input ->
        acc.lcm(BigInteger.valueOf(findPositivePulsePattern(input, conj.name, initPulse = initPulse).toLong()))
    }
}

fun BigInteger.lcm(other: BigInteger) =
    this / this.gcd(other) * other

fun Circuit.findPositivePulsePattern(source: String, dest: String, limit: Int = 1000000, initPulse: Pulse = Pulse()): Int {
    reset()
    val iters = mutableListOf<Int>()
    var i = 0

    while (true) {
        val captured = tryCaptureLast(CapturedWithSource(source, dest, true), initPulse)
        i++
        if (captured) {
            iters.add(i)
            if (iters.size == 3)
                break
        }

        if (i >= limit) {
            throw IllegalStateException("failed to find positive pulse pattern for $source")
        }
    }

    check(iters[1] - iters[0] == iters[2] - iters[1]) {
        "failed to find positive pulse pattern for $source"
    }

    return iters[1] - iters[0]
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