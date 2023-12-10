package ski.gagar.aoc2022.day2.part2

import ski.gagar.aoc.util.getResourceAsStream
import java.lang.AssertionError
import java.lang.IllegalArgumentException

enum class Result(val repr: String, val points: Int) {
    WIN("Z", 6),
    DRAW("Y", 3),
    LOSS("X", 0);

    companion object {
        private val byRepr = values().associateBy { it.repr }

        fun fromRepr(repr: String) =
            byRepr[repr] ?: throw IllegalArgumentException("Unknown value $repr")
    }
}

enum class Turn(val opponents: String, val points: Int) {
    ROCK("A", 1) {
        override val better: Turn
            get() = PAPER
        override val worse: Turn
            get() = SCISSORS
    },
    PAPER("B", 2) {
        override val better: Turn
            get() = SCISSORS
        override val worse: Turn
            get() = ROCK
    },
    SCISSORS("C", 3) {
        override val better: Turn
            get() = ROCK
        override val worse: Turn
            get() = PAPER
    };


    fun play(opponentsTurn: Turn): Result = when (opponentsTurn) {
        better -> Result.LOSS
        same -> Result.DRAW
        worse -> Result.WIN
        else -> throw AssertionError("Should not happen")
    }

    abstract val better: Turn
    abstract val worse: Turn
    val same: Turn
        get() = this

    companion object {
        private val byOpponents = values().associateBy { it.opponents }

        fun fromOpponents(opponents: String) =
            byOpponents[opponents] ?: throw IllegalArgumentException("Unknown value $opponents")
    }
}

fun myTurn(opponents: Turn, expectedResult: Result) =
    when (expectedResult) {
        Result.WIN -> opponents.better
        Result.DRAW -> opponents.same
        Result.LOSS -> opponents.worse
    }

fun playRound(string: String): Int {
    val parts = string.split(" ")
    require(parts.size == 2)

    val opp = Turn.fromOpponents(parts[0])
    val res = Result.fromRepr(parts[1])
    val mine = myTurn(opp, res)

    return res.points + mine.points
}

fun playAllRounds(string: Sequence<String>) = string.sumOf { playRound(it) }
