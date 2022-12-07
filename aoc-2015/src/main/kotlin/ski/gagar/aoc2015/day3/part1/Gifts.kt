package ski.gagar.aoc2015.day3.part1

import ski.gagar.aoc.util.getResourceAsStream

data class Coordinates(val x: Int, val y: Int) {
    fun left() = Coordinates(x - 1, y)
    fun right() = Coordinates(x + 1, y)
    fun down() = Coordinates(x, y - 1)
    fun up() = Coordinates(x, y + 1)

    companion object {
        fun start() = Coordinates(0, 0)
    }
}

fun drive(instructions: String): Int {
    var current = Coordinates.start()
    val nGifts = mutableMapOf<Coordinates, Int>()

    fun giveGift() {
        nGifts[current] = (nGifts[current] ?: 0) + 1
    }

    giveGift()

    for (instruction in instructions) {
        current = when (instruction) {
            '<' -> current.left()
            '>' -> current.right()
            'v' -> current.down()
            '^' -> current.up()
            else -> current
        }
        giveGift()
    }

    return nGifts.size
}

fun day3Part1() {
    println(
        "day3/part1/gifts: ${
            drive(getResourceAsStream("/ski.gagar.aoc.aoc2015.day3/gifts.txt").bufferedReader().lineSequence().first())
        }"
    )
}
