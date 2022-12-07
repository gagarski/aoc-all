package ski.gagar.aoc2015.day11.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day11.part1.nextCompliantPassword


fun day11Part2() {
    val first =
        getResourceAsStream("/ski.gagar.aoc.aoc2015.day11/passwords.txt").bufferedReader().lineSequence().first().nextCompliantPassword()
    val second = first.nextCompliantPassword()
    println("day11/part2/passwords: ${second}")
}
