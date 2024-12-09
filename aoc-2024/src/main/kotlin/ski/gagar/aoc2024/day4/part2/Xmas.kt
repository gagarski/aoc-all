package ski.gagar.aoc2024.day4.part2


class Field (lines: Sequence<String>) {
    data class Coordinates(val y: Int, val x: Int) {
        operator fun plus(other: Coordinates) = Coordinates(y + other.y, x + other.x)
        operator fun minus(other: Coordinates) = Coordinates(y - other.y, x - other.x)
    }

    val width: Int
    val height: Int
    private val field: List<String>

    init {
        var w: Int? = null
        var h = 0
        val f = mutableListOf<String>()
        for (line in lines) {
            if (null != w) {
                require(line.length == w)
            }
            w = line.length
            h++
            f.add(line)
        }
        require(w != null)
        width = w
        height = h
        field = f
    }

    operator fun get(y: Int, x: Int) = field[y][x]
    operator fun get(xy: Coordinates) = field[xy.y][xy.x]
    operator fun contains(xy: Coordinates) = xy.x in 0..<width && xy.y in 0..<height

    fun hasWord(start: Coordinates, direction: Coordinates, word: String): Boolean {
        var xy = start
        for (c in word) {
            if (xy !in this) return false
            if (this[xy] != c) return false
            xy += direction
        }
        return true
    }

    fun countCrossWord(start: Coordinates, word: String): Int {
        var count = 0
        if (hasWord(start, Coordinates(1, 1), word)) {
            if (hasWord(start + Coordinates(word.length - 1, 0), Coordinates(-1, 1), word) ||
                hasWord(start + Coordinates(0, word.length - 1), Coordinates(1, -1), word)) {
                count++
            }
        }

        if (hasWord(start, Coordinates(-1, -1), word)) {
            if (hasWord(start - Coordinates(word.length - 1, 0), Coordinates(1, -1), word) ||
                hasWord(start - Coordinates(0, word.length - 1), Coordinates(-1, 1), word)) {
                count++
            }
        }

        return count
    }

    fun countWord(word: String): Int {
        var count = 0
        for (y in 0..<height) {
            for (x in 0..<width) {
                for (d in DIRECTIONS) {
                    if (hasWord(Coordinates(y, x), d, word)) count++
                }
            }
        }
        return count
    }

    fun countCrossWord(word: String): Int {
        var count = 0
        for (y in 0..<height) {
            for (x in 0..<width) {
                count += countCrossWord(Coordinates(y, x), word)
            }
        }
        return count
    }

    companion object {
        val DIRECTIONS = listOf(
            Coordinates(1, 0),
            Coordinates(1, 1),
            Coordinates(0, 1),
            Coordinates(-1, 1),
            Coordinates(-1, 0),
            Coordinates(-1, -1),
            Coordinates(0, -1),
            Coordinates(1, -1),
        )
    }
}

fun countXMas(lines: Sequence<String>): Int = Field(lines).countCrossWord("MAS")