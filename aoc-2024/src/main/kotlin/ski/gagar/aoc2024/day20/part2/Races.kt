package ski.gagar.aoc2024.day20.part2

import ski.gagar.aoc2024.day20.part1.Coordinates
import ski.gagar.aoc2024.day20.part1.Racetrack
import kotlin.math.abs

fun Coordinates.manhattanDistance(other: Coordinates): Int =
    abs(row - other.row) + abs(col - other.col)

data class Cheat(val from: Coordinates, val to: Coordinates, val savedTime: Int)

fun Racetrack.cheatsBetterThan(minCheat: Int = 100, maxCheatDuration: Int = 20) = sequence {
    val path = findSinglePath()

    for (first in path.steps) {
        for (second in path.steps) {
            val manhattan = first.manhattanDistance(second)
            if (manhattan > maxCheatDuration)
                continue

            val cheatedTime = path.distance(first, second) - manhattan
            if (cheatedTime < minCheat)
                continue

            yield(Cheat(first, second, cheatedTime))
        }
    }
}

fun countCheatsLongerThan(lines: Sequence<String>, minCount: Int = 100, maxCheatDuration: Int = 20): Int {
    val track = Racetrack(lines)
    return track.cheatsBetterThan(minCount, maxCheatDuration).count()
}