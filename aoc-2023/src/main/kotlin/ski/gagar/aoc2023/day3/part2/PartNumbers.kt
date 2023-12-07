package ski.gagar.aoc2023.day3.part2

import ski.gagar.aoc.util.getResourceAsStream

data class Coordinates(val x: Int, val y: Int)

class Board(lines: Sequence<String>) {
    val numbers: Map<Coordinates, Int>
    val gears: Set<Coordinates>
    val width: Int
    val height: Int

    init {
        var y = 0
        var width: Int? = null
        val numbers = mutableMapOf<Coordinates, Int>()
        val gears = mutableSetOf<Coordinates>()
        for (line in lines) {

            require(width == null || line.length == width)
            width = line.length

            var numStartIndex: Int? = null
            var num : Int = 0
            var numLen = 0

            fun reset() {
                numStartIndex = null
                numLen = 0
                num = 0
            }

            fun flushNumber() {
                if (null == numStartIndex) return

                numbers[Coordinates(numStartIndex!!, y)] = num
            }

            for ((x, ch) in line.withIndex()) {
                fun acceptDigit() {
                    numStartIndex = numStartIndex ?: x
                    num = num * 10 + (ch - '0')
                    numLen++
                }

                when {
                    ch.isDigit() -> {
                        acceptDigit()
                    }
                    ch == '*' -> {
                        flushNumber()
                        reset()
                        gears.add(Coordinates(x, y))
                    }
                    else -> {
                        flushNumber()
                        reset()
                    }
                }
            }

            flushNumber()
            reset()

            y++
        }

        require(width != null)
        this.width = width
        this.numbers = numbers
        this.gears = gears
        this.height = y
    }


    private fun getAdjacentGears(coordinates: Coordinates, number: Int) = sequence {
        val numLength = number.toString().length

        val left = Coordinates(coordinates.x - 1, coordinates.y)
        if (left in gears) yield(left)

        val right = Coordinates(coordinates.x + numLength, coordinates.y)
        if (right in gears) yield(right)

        for (xx in (coordinates.x - 1)..(coordinates.x + numLength)) {
            val up = Coordinates(xx, coordinates.y - 1)
            if (up in gears) yield(up)

            val down = Coordinates(xx, coordinates.y + 1)
            if (down in gears) yield(down)
        }
    }

    fun getGearedPairs(): List<Pair<Int, Int>> {
        val gearToNums = mutableMapOf<Coordinates, MutableSet<Pair<Coordinates, Int>>>()
        for ((numXy, num) in numbers) {
            for (gearXy in getAdjacentGears(numXy, num)) {
                val nums = gearToNums[gearXy] ?: mutableSetOf()
                nums.add(numXy to num)
                gearToNums[gearXy] = nums
            }
        }

        return gearToNums.values.filter {
            require(it.size <= 2)
            it.size == 2
        }.map {
            val list = it.toList()
            list[0].second to list[1].second
        }.toList()
    }
}

private fun gearRatio(lines: Sequence<String>) =
    Board(lines).getGearedPairs().asSequence().map {
        it.first.toLong() * it.second.toLong()
    }.sum()

fun day3Part2() {
    println(
        "day3/part2/partNumbers: ${
            gearRatio(
                getResourceAsStream("/ski.gagar.aoc.aoc2023.day3/board.txt").bufferedReader().lineSequence()
            )
        }"
    )
}