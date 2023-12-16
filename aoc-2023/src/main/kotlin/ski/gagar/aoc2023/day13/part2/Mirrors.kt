package ski.gagar.aoc2023.day13.part2

enum class SymmetryType {
    VERTICAL,
    HORIZONTAL
}

data class Symmetry(val type: SymmetryType, val index: Int)

class Pattern(val lines: List<String>) {
    val height = lines.size
    val width = lines[0].length

    init {
        require(lines.all { it.length == width })
    }

    private fun String.nSmudges(index: Int, maxSmudges: Int = 1): Int {
        require(index in indices)

        var i = 0
        var nSmudges = 0

        while (true) {
            val left = this.getOrNull(index - 1 - i)
            val right = this.getOrNull(index + i)


            if (null == left || null == right) {
                return nSmudges
            }

            if (left != right) {
                nSmudges++
            }

            if (nSmudges > maxSmudges) {
                return nSmudges
            }
            i++
        }
    }

    private fun Iterable<Int>.lazySum(max: Int = 1): Int {
        var sum = 0
        for (i in this) {
            sum += i
            if (sum > max) {
                return sum
            }
        }

        return sum
    }

    private fun nSmudgesForCol(col: Int, index: Int, maxSmudges: Int = 1): Int {
        require(col in 0 until width)
        require(index in 0 until height)

        var i = 0
        var nSmudges = 0

        while (true) {
            val top = this.lines.getOrNull(index - 1 - i)?.get(col)
            val bottom = this.lines.getOrNull(index + i)?.get(col)


            if (null == top || null == bottom) {
                return nSmudges
            }

            if (top != bottom) {
                nSmudges++
            }

            if (nSmudges > maxSmudges) {
                return nSmudges
            }

            i++
        }
    }

    fun verticalSymmetryLine(maxSmudges: Int = 1): Int? {
        for (line in 1 until width) {
            if (lines.asSequence().map { it.nSmudges(line, maxSmudges) }.asIterable().lazySum(maxSmudges) == maxSmudges)
                return line
        }

        return null
    }

    fun horizontalSymmetryLine(maxSmudges: Int = 1): Int? {
        for (line in 1 until height) {
            if ((0 until width).map { nSmudgesForCol(it, line, maxSmudges) }.asIterable().lazySum(maxSmudges) == maxSmudges)
                return line
        }

        return null
    }

    fun getNonEmptySymmetry(): Symmetry? {
        val ver = verticalSymmetryLine()

        if (ver != null) return Symmetry(SymmetryType.VERTICAL, ver)

        val hor = horizontalSymmetryLine()

        if (hor != null) return Symmetry(SymmetryType.HORIZONTAL, hor)

        return null
    }

}

fun Symmetry?.price(): Int = this?.let {
    when (it.type) {
        SymmetryType.VERTICAL -> index
        SymmetryType.HORIZONTAL -> index * 100
    }

} ?: 0

fun calculateSymmetry(lines: Sequence<String>): Int {
    var buf = mutableListOf<String>()
    var sym = 0
    for (line in lines) {
        if (line.isEmpty()) {
            val pattern = Pattern(buf)
            buf = mutableListOf()
            sym += pattern.getNonEmptySymmetry().price()
            continue
        }
        buf += line
    }

    if (buf.isNotEmpty()) {
        val pattern = Pattern(buf)
        sym += pattern.getNonEmptySymmetry().price()
    }
    return sym
}