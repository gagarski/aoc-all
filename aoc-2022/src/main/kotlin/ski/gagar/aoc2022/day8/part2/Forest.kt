package ski.gagar.aoc2022.day8.part2

import ski.gagar.aoc.util.getResourceAsStream
import java.util.BitSet

class Forest(private val trees: List<List<Int>>) {
    val height: Int
    val width: Int
    private val visibility: BitSet
    private val scenicScores: List<List<Int>>

    init {
        require(trees.asSequence().map { it.size }.toSet().size == 1)
        require(trees.first().isNotEmpty())
        height = trees.size
        width = trees.first().size
        visibility = BitSet(trees.size * trees.first().size)
        initVisibility()
        scenicScores = initScenicScores()
    }

    private fun processTreeVisibility(x: Int, y: Int, tallestSoFar: Int): Int {
        val current = getHeight(x, y)
        if (current > tallestSoFar) {
            visibility[index(x, y)] = true
            return current
        }
        return tallestSoFar
    }

    private fun initVisibility() {
        for (y in 0 until height) {
            var tallestSoFar = Int.MIN_VALUE
            for (x in 0 until width) {
                tallestSoFar = processTreeVisibility(x, y, tallestSoFar)
            }
            tallestSoFar = Int.MIN_VALUE
            for (x in width - 1 downTo  0) {
                tallestSoFar = processTreeVisibility(x, y, tallestSoFar)
            }

        }
        for (x in 0 until width) {
            var tallestSoFar = Int.MIN_VALUE
            for (y in 0 until height) {
                tallestSoFar = processTreeVisibility(x, y, tallestSoFar)
            }
            tallestSoFar = Int.MIN_VALUE
            for (y in height - 1 downTo  0) {
                tallestSoFar = processTreeVisibility(x, y, tallestSoFar)
            }
        }
    }

    private fun Int.correct(int: Int): Int {
        if (this == int) return this

        return this + 1
    }

    private fun calcScenicScore(x: Int, y: Int): Int {
        val me = trees[y][x]
        val l = (x - 1 downTo  0).map { trees[y][it] }.takeWhile { it < me }.count().correct(x)
        val r = (x + 1 until width).map { trees[y][it] }.takeWhile { it < me }.count().correct(width - 1 - x)
        val t = (y - 1 downTo  0).map { trees[it][x] }.takeWhile { it < me }.count().correct(y)
        val b = (y + 1 until height).map { trees[it][x] }.takeWhile { it < me }.count().correct(height - 1 - y)

        return l * r * t * b
    }

    private fun initScenicScores() = sequence {
        for (y in 0 until height) {
            yield(
                sequence {
                    for (x in 0 until width) {
                        yield(calcScenicScore(x, y))
                    }
                }.toList()
            )
        }
    }.toList()

    private fun checkBounds(x: Int, y: Int) {
        require(x in 0 until width)
        require(y in 0 until height)
    }

    private fun index(x: Int, y: Int): Int {
        checkBounds(x, y)
        return y * width + x
    }

    fun getHeight(x: Int, y: Int): Int {
        checkBounds(x, y)
        return trees[y][x]
    }

    fun isVisible(x: Int, y: Int): Boolean {
        checkBounds(x, y)
        return visibility[index(x, y)]
    }

    fun getScenicScore(x: Int, y: Int): Int {
        checkBounds(x, y)
        return scenicScores[y][x]
    }

    fun nVisible() = visibility.cardinality()

}


fun maxScenicScore(strings: Sequence<String>): Int {
    val forest = Forest(strings.map { it.map { c -> c - '0' } }.toList())

    var maxSoFar = Int.MIN_VALUE

    for (y in 0 until forest.height) {
        for (x in 0 until forest.width) {
            val current = forest.getScenicScore(x, y)
            if (current > maxSoFar) {
                maxSoFar = current
            }
        }
    }

    return maxSoFar
}
