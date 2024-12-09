package ski.gagar.aoc2024.day8.part1


data class Coordinates(val row: Int, val col: Int) {
    operator fun plus(other: Coordinates) = Coordinates(row + other.row, col + other.col)
    operator fun minus(other: Coordinates) = Coordinates(row - other.row, col - other.col)

    fun oppositeTo(other: Coordinates): Coordinates {
        val delta = other - this
        return this - delta
    }
}

class Field(lines: Sequence<String>) {
    val height: Int
    val width: Int
    val antennas: Map<Coordinates, Char>

    init {
        var w: Int? = null
        var h = 0
        val antennas = mutableMapOf<Coordinates, Char>()

        for ((row, line) in lines.withIndex()) {
            if (null != w) {
                require(line.length == w)
            }
            w = line.length
            h++
            for ((column, char) in line.withIndex()) {
                when (char) {
                    '.' -> {}
                    else -> antennas[Coordinates(row, column)] = char
                }
            }
        }
        require(w != null)
        width = w
        height = h
        this.antennas = antennas
    }

    fun getAntinodes(): Set<Coordinates> {
        val byFrequency = antennas.asSequence().groupBy {
            it.value
        }.map {
            it.key to it.value.asSequence().map { it.key }.toSet()
        }.toMap()
        val antinodes = mutableSetOf<Coordinates>()
        for ((_, antennas) in byFrequency) {
            for (a1 in antennas) {
                for (a2 in antennas) {
                    if (a1 == a2)
                        continue
                    val antinode = a1.oppositeTo(a2)
                    if (isInside(antinode)) {
                        antinodes.add(antinode)
                    }
                }
            }
        }
        return antinodes
    }

    private fun isInside(position: Coordinates) = position.row in 0..<width  && position.col in 0..<height

}

fun countAntinodes(lines: Sequence<String>): Int =
    Field(lines).getAntinodes().size