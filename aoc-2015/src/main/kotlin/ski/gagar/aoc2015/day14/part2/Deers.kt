package ski.gagar.aoc2015.day14.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day14.part1.DeerLane
import ski.gagar.aoc2015.day14.part1.DeerSpecParser

data class LeaderBoardItem(val name: String, val points: Int)

class LeaderBoard {
    private val pointsW = mutableMapOf<String, Int>()

    fun count(lane: DeerLane) {
        val results = lane.leaderboard
        val top = results.firstOrNull()?.distancePassed ?: return

        for (result in results) {
            if (result.distancePassed == top) {
                pointsW[result.name] = (pointsW[result.name] ?: 0) + 1
            }
        }
    }

    val points: Map<String, LeaderBoardItem>
        get() = pointsW.map { (k, v) -> k to LeaderBoardItem(k, v) }.toMap()

    val results
        get() = pointsW.map { (k, v) -> LeaderBoardItem(k, v) }.sortedByDescending { it.points }
}

fun runDeers(strings: Sequence<String>, time: Int = 2503): Int? {
    val lane = DeerLane(strings.map { DeerSpecParser.parse(it) }.asIterable())
    val lb = LeaderBoard()

    for (sec in 0 until 2503) {
        lane.advanceTime(1)
        lb.count(lane)
    }

    return lb.results.firstOrNull()?.points
}
