package ski.gagar.aoc.util.math


data class LinearEquationSystem(val equations: List<LinearEquation>) {
    val width: Int
    val height: Int = equations.size

    init {
        require(equations.isNotEmpty()) {
            "At least one equation is required"
        }

        require(equations.all { it.size == equations.first().size })
        width = equations.first().size
    }

    constructor(vararg equations: LinearEquation) : this(equations.toList())

    fun combine(other: LinearEquationSystem) =
        LinearEquationSystem(this.equations + other.equations)

    fun reduce(): LinearEquationSystem {
        val buf = mutableListOf<LinearEquation>()
        buf.addAll(equations)
        buf.forward()
        buf.backward()
        return LinearEquationSystem(buf)
    }

    private fun doSolve(): List<NaturalFraction>? {
        val possibleRes = (0 until width).map {ix ->
            val eq = equations.getOrNull(ix) ?: return null
            val coeff = eq.getOrNull(ix) ?: return null
            if (coeff.isZero) return null
            equations[ix].free / coeff
        }

        val rest = equations.drop(width)

        if (rest.all { it.isUniversal })
            return possibleRes
        else {
            check(rest.all { isImpossible })
            return null
        }
    }

    fun solve(): List<NaturalFraction>? {
        val reduced = reduce()
        return reduced.doSolve()
    }

    val isImpossible by lazy {
        equations.any { it.isImpossible }
    }

    private fun MutableList<LinearEquation>.forward() {
        for (i in 0 until width) {
            val maxIndex =
                this.asSequence().withIndex().drop(i).maxByOrNull { (_, eq) -> eq[i].abs }?.index ?: continue

            val tmp = this[i]
            this[i] = this[maxIndex]
            this[maxIndex] = tmp

            for (j in i + 1 .. lastIndex) {
                val current = this.getOrNull(i) ?: continue
                val reduced = this[j]
                if (reduced[i] == NaturalFraction.ZERO) continue
                if (current[i] == NaturalFraction.ZERO) continue

                val coeff = reduced[i] / current[i]
                this[j] = reduced - current * coeff
            }
        }
    }

    private fun MutableList<LinearEquation>.backward() {
        for (i in width - 1  downTo 0) {
            for (j in i - 1 downTo 0) {
                val current = this.getOrNull(i) ?: continue
                val reduced = this[j]
                if (reduced[i] == NaturalFraction.ZERO) continue
                if (current[i] == NaturalFraction.ZERO) continue
                val coeff = reduced[i] / current[i]
                this[j] = reduced - current * coeff

            }
        }
    }

    private fun MutableList<LinearEquation>.result(): List<NaturalFraction>? =
        this.indices.map {
            if (!this[it].isSingleton)
                return null
            this[it].free / this[it][it]
        }.let { result ->
            if (result.any { it.isNaN })
                return null
            return result
        }
}