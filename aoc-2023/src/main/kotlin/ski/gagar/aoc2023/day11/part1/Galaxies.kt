package ski.gagar.aoc2023.day11.part1

import java.util.TreeSet
import kotlin.math.abs

data class Galaxy(val x: Long, val y: Long)

class Universe(map: Sequence<String>, val xExpansion: Long = 2, val yExpansion: Long = 2) {
    val galaxies: Set<Galaxy>
    private val emptyRows: TreeSet<Long> = TreeSet()
    private val emptyCols: TreeSet<Long> = TreeSet()

    init {
        val galaxies = mutableSetOf<Galaxy>()
        val occupiedRows = mutableSetOf<Long>()
        val occupiedCols = mutableSetOf<Long>()

        for ((y, line) in map.withIndex()) {
            for ((x, char) in line.withIndex()) {
                if (char == '#') {
                    galaxies.add(Galaxy(x.toLong(), y.toLong()))
                    occupiedRows.add(y.toLong())
                    occupiedCols.add(x.toLong())
                }
            }
        }

        this.galaxies = galaxies

        val minX = galaxies.minOfOrNull { it.x }
        val maxX = galaxies.maxOfOrNull { it.x }
        val minY = galaxies.minOfOrNull { it.y }
        val maxY = galaxies.maxOfOrNull { it.y }

        if (minX != null && maxX != null && minY != null && maxY != null) {
            for (x in minX..maxX) {
                if (x !in occupiedCols) {
                    emptyCols.add(x)
                }
            }

            for (y in minY..maxY) {
                if (y !in occupiedRows) {
                    emptyRows.add(y)
                }
            }
        }
    }

    fun manhattanDistanceBetween(first: Galaxy, second: Galaxy): Long {
        val origManhattan = abs(first.x - second.x) + abs(first.y - second.y)

        val minX = minOf(first.x, second.x)
        val maxX = maxOf(first.x, second.x)
        val minY = minOf(first.y, second.y)
        val maxY = maxOf(first.y, second.y)

        val xExpanded = emptyCols.subSet(minX, true, maxX, true).size
        val yExpanded = emptyRows.subSet(minY, true, maxY, true).size

        return origManhattan + xExpanded * (xExpansion - 1) + yExpanded * (yExpansion - 1)
    }

    fun sumManhattan(): Long {
        val galaxiesList = galaxies.toList()
        var sum = 0L
        for (i in galaxiesList.indices) {
            for (j in i + 1..galaxiesList.lastIndex) {
                sum += manhattanDistanceBetween(galaxiesList[i], galaxiesList[j])
            }
        }
        return sum
    }
}

fun sumManhattan(map: Sequence<String>, xExpansion: Long = 2, yExpansion: Long = 2) =
    Universe(map, xExpansion, yExpansion).sumManhattan()