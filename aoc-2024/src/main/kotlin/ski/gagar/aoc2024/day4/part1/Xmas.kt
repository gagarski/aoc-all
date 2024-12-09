package ski.gagar.aoc2024.day4.part1


class Field (lines: Sequence<String>) {
    data class Coordinates(val y: Int, val x: Int) {
        operator fun plus(other: Coordinates) = Coordinates(y + other.y, x + other.x)
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

fun countXmas(lines: Sequence<String>): Int = Field(lines).countWord("XMAS")