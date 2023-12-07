package ski.gagar.aoc2015.day14.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import org.jparsec.pattern.CharPredicates
import ski.gagar.aoc.util.getResourceAsStream
import kotlin.math.min

data class DeerSpec(val name: String, val speedKmS: Int, val runDurationSec: Int, val restDurationSec: Int)

data class DeerResult(val name: String, val distancePassed: Int)

class DeerLane(deers: Iterable<DeerSpec>) {
    val specs = deers.associateBy { it.name }
    val states = mutableMapOf<String, DeerState>()

    data class DeerState(var tillSwitchSec: Int, var isResting: Boolean, var distancePassed: Int) {
        companion object {
            fun createDefault() = DeerState(0, true, 0)
        }
    }

    private fun move(spec: DeerSpec, state: DeerState, seconds: Int): Int {
        if (state.isResting) return seconds
        if (seconds == 0) return 0
        val toMoveSec = min(state.tillSwitchSec, seconds)

        state.tillSwitchSec -= toMoveSec

        if (state.tillSwitchSec == 0) {
            state.isResting = true
            state.tillSwitchSec = spec.restDurationSec
        }

        state.distancePassed += toMoveSec * spec.speedKmS

        return seconds - toMoveSec
    }

    private fun rest(spec: DeerSpec, state: DeerState, seconds: Int): Int {
        if (!state.isResting) return seconds
        if (seconds == 0) return 0

        val toRestSec = min(state.tillSwitchSec, seconds)

        state.tillSwitchSec -= toRestSec

        if (state.tillSwitchSec == 0) {
            state.isResting = false
            state.tillSwitchSec = spec.runDurationSec
        }

        return seconds - toRestSec
    }

    private fun oneCycle(spec: DeerSpec, state: DeerState, seconds: Int): Int {
        val rem = move(spec, state, seconds)
        return rest(spec, state, rem)
    }

    private fun runFullCycles(spec: DeerSpec, state: DeerState, seconds: Int): Int {
        val fullCycle = spec.runDurationSec + spec.restDurationSec
        val n = seconds / fullCycle
        state.distancePassed += n * spec.runDurationSec * spec.speedKmS
        return seconds % fullCycle
    }

    private fun advanceTimeForDeer(spec: DeerSpec, seconds: Int) {
        var remaining = seconds
        val state = states.computeIfAbsent(spec.name) {
            DeerState.createDefault()
        }
        remaining = oneCycle(spec, state, remaining)
        remaining = runFullCycles(spec, state, remaining)
        oneCycle(spec, state, remaining)
    }

    fun advanceTime(seconds: Int) {
        for ((_, spec) in specs) {
            advanceTimeForDeer(spec, seconds)
        }
    }

    val results
        get() = states.map { (k, v) -> k to DeerResult(k, v.distancePassed) }.toMap()

    val leaderboard
        get() = states.map { (k, v) -> DeerResult(k, v.distancePassed)}
            .sortedByDescending { it.distancePassed }
}

object DeerSpecParser {
    private val ZERO_OR_MORE_WHITESPACES = Scanners.isChar(CharPredicates.IS_WHITESPACE).many()
    private val NAME = Terminals.Identifier.TOKENIZER.map { it.text() }

    private val NAME_CAN_FLY = Parsers.sequence(
        NAME,
        Scanners.WHITESPACES,
        Scanners.string("can"),
        Scanners.WHITESPACES,
        Scanners.string("fly")
    ) { name, _, _, _, _ ->
        name
    }

    private val POSITIVE_NUMBER = Terminals.DecimalLiteral.TOKENIZER.map {
        it.text().toInt().also { res -> check(res >= 0) }
    }
    private val SPEED = Parsers.sequence(
        POSITIVE_NUMBER,
        Scanners.WHITESPACES,
        Scanners.string("km/s")
    ) { speed, _, _ ->
        speed
    }
    private val SECONDS = Parsers.or(
        Scanners.string("seconds"),
        Scanners.string("second"),
    )
    private val DURATION = Parsers.sequence(
        Scanners.string("for"),
        Scanners.WHITESPACES,
        POSITIVE_NUMBER,
        Scanners.WHITESPACES,
        SECONDS
    ) { _, _, duration, _, _ ->
        duration
    }

    private val BUT_THEN_MUST_REST = Parsers.sequence(
        Scanners.string("but"),
        Scanners.WHITESPACES,
        Scanners.string("then"),
        Scanners.WHITESPACES,
        Scanners.string("must"),
        Scanners.WHITESPACES,
        Scanners.string("rest"))

    data class Intermediate(val name: String, val speedKmS: Int, val runDurationSec: Int)

    private val DEER_SPEC = Parsers.sequence(
        Parsers.sequence(
            NAME_CAN_FLY,
            Scanners.WHITESPACES,
            SPEED,
            Scanners.WHITESPACES,
            DURATION
        ) { name, _, speed, _, duration ->
            Intermediate(name, speed, duration)
        },
        Parsers.sequence(
            ZERO_OR_MORE_WHITESPACES,
            Scanners.isChar(','),
            ZERO_OR_MORE_WHITESPACES,
            BUT_THEN_MUST_REST,
            Scanners.WHITESPACES
        ),
        DURATION,
        ZERO_OR_MORE_WHITESPACES,
        Scanners.isChar('.')
    ) { intermediate, _, restDuration, _, _ ->
        DeerSpec(intermediate.name, intermediate.speedKmS, intermediate.runDurationSec, restDuration)
    }

    fun parse(str: String) = DEER_SPEC.parse(str)
}

fun runDeers(strings: Sequence<String>, time: Int = 2503): Int? {
    val lane = DeerLane(strings.map { DeerSpecParser.parse(it) }.asIterable())
    lane.advanceTime(time)
    return lane.leaderboard.firstOrNull()?.distancePassed
}


fun day14Part1() {
    println("day14/part1/deers: ${
        runDeers(getResourceAsStream("/ski.gagar.aoc.aoc2015.day14/deers.txt").bufferedReader().lineSequence())
    }")
}
