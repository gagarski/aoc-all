package ski.gagar.aoc2024.day10.part2

import ski.gagar.aoc.util.Puzzle

data class Coordinates(val row: Int, val col: Int) {
    fun neighbors(): Sequence<Coordinates> = sequence {
        yield(Coordinates(row - 1, col))
        yield(Coordinates(row, col + 1))
        yield(Coordinates(row + 1, col))
        yield(Coordinates(row, col - 1))
    }
}

class TrailField(lines: Sequence<String>) {
    val field: List<List<Int>>
    val trailHeads: Set<Coordinates>
    val height: Int
        get() = this.field.size
    val width: Int
        get() = this.field[0].size

    init {
        var w: Int? = null
        var h = 0
        val field = mutableListOf<List<Int>>()
        val trailHeads = mutableSetOf<Coordinates>()

        for ((row, line) in lines.withIndex()) {

            if (null != w) {
                require(line.length == w)
            }
            w = line.length
            h++

            val r = line.map {
                when {
                    it in '0'..'9' -> it - '0'
                    else -> Int.MIN_VALUE
                }
            }

            for ((col, height) in r.withIndex()) {
                if (height == 0)
                    trailHeads.add(Coordinates(row, col))
            }

            field.add(r)
        }
        require(w != null)
        this.field = field
        this.trailHeads = trailHeads
    }

    operator fun contains(coordinates: Coordinates): Boolean =
        coordinates.row in 0 ..< height && coordinates.col in 0 ..< width

    operator fun get(coordinates: Coordinates): Int {
        require(coordinates in this)
        return field[coordinates.row][coordinates.col]
    }

    operator fun get(row: Int, col: Int): Int = get(Coordinates(row, col))

    fun rating(start: Coordinates, targetHeight: Int = 9): Int {
        val queue = ArrayDeque<Coordinates>()
        // val visited = No need to track visited!
        // The way nest step is chosen we cannot backtrack and cannot produce a trail which we've already had walked
        queue.addLast(start)
        var score = 0

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            if (this[current] == targetHeight) {
                score++
                continue
            }

            for (neighbor in current.neighbors().filter {
                it in this && this[it] == this[current] + 1
            }) {
                queue.addLast(neighbor)
            }
        }

        return score
    }

    fun sumRatings(targetHeight: Int = 9): Int =
        trailHeads.sumOf { rating(it, targetHeight) }
}

fun sumTrailHeadRatings(lines: Sequence<String>, targetHeight: Int = 9): Int =
    TrailField(lines).sumRatings(targetHeight)