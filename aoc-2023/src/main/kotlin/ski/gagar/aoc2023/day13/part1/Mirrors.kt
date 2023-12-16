package ski.gagar.aoc2023.day13.part1

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

    private fun String.isSymmetricAround(index: Int): Boolean {
        require(index in indices)

        var i = 0

        while (true) {
            val left = this.getOrNull(index - 1 - i)
            val right = this.getOrNull(index + i)


            if (null == left || null == right) {
                return true
            }

            if (left != right) {
                return false
            }
            i++
        }
    }

    private fun isColumnSymmetricalAround(col: Int, index: Int): Boolean {
        require(col in 0 until width)
        require(index in 0 until height)

        var i = 0

        while (true) {
            val top = this.lines.getOrNull(index - 1 - i)?.get(col)
            val bottom = this.lines.getOrNull(index + i)?.get(col)


            if (null == top || null == bottom) {
                return true
            }

            if (top != bottom) {
                return false
            }
            i++
        }
    }

    fun verticalSymmetryLine(): Int? {
        for (line in 1 until width) {
            if (lines.all { it.isSymmetricAround(line) })
                return line
        }

        return null
    }

    fun horizontalSymmetryLine(): Int? {

        for (line in 1 until height) {
            if ((0 until width).all { isColumnSymmetricalAround(it, line) })
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