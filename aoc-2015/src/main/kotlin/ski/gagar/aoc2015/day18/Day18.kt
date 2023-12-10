package ski.gagar.aoc2015.day18

import ski.gagar.aoc.util.Puzzle
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day18.part2.defaultNextState
import java.io.InputStream

object Day18 : Puzzle {
    override val name: String = "Like a GIF For Your Yard"
    override val inputPath: String = "/ski.gagar.aoc.aoc2015.day18/life.txt"

    override fun part1(input: InputStream) =
        ski.gagar.aoc2015.day18.part1.parseAndRun(input.bufferedReader().lineSequence())


    override fun part2(input: InputStream) =
        ski.gagar.aoc2015.day18.part2.parseAndRun(
            input.bufferedReader().lineSequence(),
            nextState = { state ->
                if (state.x == 0 && state.y == 0) return@parseAndRun true
                if (state.x == 0 && state.y == state.life.height - 1) return@parseAndRun true
                if (state.x == state.life.width - 1 && state.y == 0) return@parseAndRun true
                if (state.x == state.life.width - 1 && state.y == state.life.height - 1) return@parseAndRun true

                defaultNextState(state)
            }
        )
}