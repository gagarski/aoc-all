package ski.gagar.aoc2022.day2.part1

import ski.gagar.aoc.util.getResourceAsStream
import java.lang.IllegalArgumentException
import java.util.StringJoiner

enum class Result(val points: Int) {
    WIN(6),
    DRAW(3),
    LOSS(0)
}

enum class Turn(val opponents: String, val mine: String, val points: Int) {
    ROCK("A", "X", 1) {
        override fun play(opponentsTurn: Turn): Result = when (opponentsTurn) {
            PAPER -> Result.LOSS
            ROCK -> Result.DRAW
            SCISSORS -> Result.WIN
        }
    },
    PAPER("B", "Y", 2) {
        override fun play(opponentsTurn: Turn): Result = when (opponentsTurn) {
            SCISSORS -> Result.LOSS
            PAPER -> Result.DRAW
            ROCK -> Result.WIN
        }
    },
    SCISSORS("C", "Z", 3) {
        override fun play(opponentsTurn: Turn): Result = when (opponentsTurn) {
            ROCK -> Result.LOSS
            SCISSORS -> Result.DRAW
            PAPER -> Result.WIN
        }
    };


    abstract fun play(opponentsTurn: Turn): Result

    companion object {
        private val byOpponents = values().associateBy { it.opponents }
        private val byMine = values().associateBy { it.mine }

        fun fromOpponents(opponents: String) =
            byOpponents[opponents] ?: throw IllegalArgumentException("Unknown value $opponents")
        fun fromMine(mine: String) =
            byMine[mine] ?: throw IllegalArgumentException("Unknown value $mine")
    }
}


fun playRound(string: String): Int {
    val parts = string.split(" ")
    require(parts.size == 2)

    val opp = Turn.fromOpponents(parts[0])
    val mine = Turn.fromMine(parts[1])
    val res = mine.play(opp)

    return res.points + mine.points
}

fun playAllRounds(string: Sequence<String>) = string.sumOf { playRound(it) }
