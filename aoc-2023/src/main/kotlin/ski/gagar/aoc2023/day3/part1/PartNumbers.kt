package ski.gagar.aoc2023.day3.part1

import ski.gagar.aoc.util.getResourceAsStream

data class Coordinates(val x: Int, val y: Int)

class Board(lines: Sequence<String>) {
    val map: Map<Coordinates, Char>
    val width: Int
    val height: Int

    init {
        var y = 0
        var width: Int? = null
        val map = mutableMapOf<Coordinates, Char>()
        for (line in lines) {
            require(width == null || line.length == width)
            width = line.length

            for ((x, char) in line.withIndex()) {
                if (char == '.') continue
                map[Coordinates(x, y)] = char
            }

            y++
        }

        require(width != null)
        this.width = width
        this.map = map
        this.height = y
    }

    private fun isAnchor(ch: Char?) = ch?.isDigit()?.not() ?: false

    private fun hasAnchor(y: Int, x: Int, length: Int): Boolean {
        if (isAnchor(map[Coordinates(x - 1, y)]))
            return true

        if (isAnchor(map[Coordinates(x + length, y)]))
            return true

        for (xx in (x - 1)..(x + length)) {
            if (isAnchor(map[Coordinates(xx, y - 1)]))
                return true
            if (isAnchor(map[Coordinates(xx, y + 1)]))
                return true
        }

        return false
    }

    fun partNumbers() = sequence {
        for (y in 0 until height) {
            var numStartIndex: Int? = null
            var num : Int = 0
            var numLen = 0

            fun reset() {
                numStartIndex = null
                numLen = 0
                num = 0
            }

            suspend fun SequenceScope<Int>.yieldIfWithAnchor() {
                if (hasAnchor(y, numStartIndex!!, numLen)) {
                    yield(num)
                }
            }

            for (x in 0 until width) {

                fun acceptDigit(ch: Char) {
                    numStartIndex = numStartIndex ?: x
                    num = num * 10 + (ch - '0')
                    numLen++
                }


                val ch = map[Coordinates(x, y)]

                when {
                    ch?.isDigit() ?: false -> {
                        acceptDigit(ch!!)
                    }
                    else -> {
                        if (null != numStartIndex) yieldIfWithAnchor()
                        reset()
                    }
                }
            }
            if (null != numStartIndex) yieldIfWithAnchor()
        }
    }
}

private fun partNumbers(lines: Sequence<String>) =
    Board(lines).partNumbers().sum()

fun day3Part1() {
    println(
        "day3/part1/partNumbers: ${
            partNumbers(
                getResourceAsStream("/ski.gagar.aoc.aoc2023.day3/board.txt").bufferedReader().lineSequence()
            )
        }"
    )
}