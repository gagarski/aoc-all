package ski.gagar.aoc2015.day3.part2

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

class Elf(nSantas: Int) {
    init {
        require(nSantas > 0)
    }

    var currentSanta = 0
        private set
    private val santas = MutableList(nSantas) { Coordinates.start() }

    fun execute(block: Coordinates.() -> Coordinates) {
        santas[currentSanta] = santas[currentSanta].block()
    }

    fun roundRobin() {
        currentSanta++
        if (currentSanta >= santas.size) {
            currentSanta = 0
        }
    }

    fun executeAndRoundRobin(block: Coordinates.() -> Coordinates) {
        execute(block)
        roundRobin()
    }

    fun forEachSanta(block: Coordinates.() -> Coordinates) {
        val startingSanta = currentSanta

        do {
            executeAndRoundRobin(block)
        } while (currentSanta != startingSanta)
    }
}

fun drive(instructions: String, nSantas: Int = 2): Int {
    val elf = Elf(nSantas)
    val nGifts = mutableMapOf<Coordinates, Int>()

    fun giveGift(coordinates: Coordinates) {
        nGifts[coordinates] = (nGifts[coordinates] ?: 0) + 1
    }

    elf.forEachSanta {
        giveGift(this)
        this
    }

    for (instruction in instructions) {
        elf.executeAndRoundRobin {
            val next = when (instruction) {
                '<' -> left()
                '>' -> right()
                'v' -> down()
                '^' -> up()
                else -> this
            }
            giveGift(next)
            next
        }
    }

    return nGifts.size
}

fun day3Part2() {
    println(
        "day3/part2/gifts: ${
            drive(getResourceAsStream("/ski.gagar.aoc.aoc2015.day3/gifts.txt").bufferedReader().lineSequence().first())
        }"
    )
}
