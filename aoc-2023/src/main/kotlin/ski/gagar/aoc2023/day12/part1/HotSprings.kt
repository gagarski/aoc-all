package ski.gagar.aoc2023.day12.part1

data class Counts(val counts: List<Int>) {
    companion object {
        private val COMMA_RE = Regex(""",""")
        fun from(string: String) = Counts(string.split(COMMA_RE).map { it.toInt() })
    }
}

enum class PatternState {
    WORKING,
    BROKEN,
    UNKNOWN;

    companion object {
        fun from(char: Char) =
            when (char) {
                '.' -> WORKING
                '#' -> BROKEN
                '?' -> UNKNOWN
                else -> throw IllegalArgumentException("Cannot parse $char")
            }
    }
}

fun IntRange.intersectsOrTouches(other: IntRange) =
    !(this.last <= other.first - 1 || other.last <= this.first - 1)

fun IntRange.shr() = IntRange(first + 1, last + 1)
fun IntRange.shl() = IntRange(first - 1, last - 1)

fun IntRange.distance(): Int {
    require(step == 1)
    return this.last - this.first + 1
}

operator fun IntRange.contains(other: IntRange) =
    first <= other.first && last >= other.last

data class Pattern(val states: List<PatternState>) {
    fun counts(): Counts {
        val res = mutableListOf<Int>()
        var cnt = 0

        fun push() {
            if (cnt != 0) res.add(cnt)
            cnt = 0
        }

        for (state in states) {
            when (state) {
                PatternState.WORKING -> push()
                PatternState.BROKEN -> cnt++
                else -> throw IllegalStateException("Not supported for patterns with unknown state")
            }
        }

        push()
        return Counts(res)
    }

    fun possibleRanges() : List<IntRange> {
        val res = mutableListOf<IntRange>()
        var start = -1

        fun end(end: Int) {
            if (start != -1) res.add(start..<end)
            start = -1
        }

        fun start(ix: Int) {
            if (start == -1) start = ix
        }

        for ((i, state) in states.withIndex()) {
            when (state) {
                PatternState.WORKING -> end(i)
                else -> start(i)
            }
        }

        end(states.size)

        return res
    }

    data class Guess(val ranges: List<IntRange>)

    fun Pattern.mustBeBroken(): List<IntRange> {
        val res = mutableListOf<IntRange>()
        var start = -1

        fun end(end: Int) {
            if (start != -1) res.add(start..<end)
            start = -1
        }

        fun start(ix: Int) {
            if (start == -1) start = ix
        }

        for ((i, state) in states.withIndex()) {
            when (state) {
                PatternState.BROKEN -> start(i)
                else -> end(i)
            }
        }

        end(states.size)

        return res
    }

    private fun firstGuess(targetCount: Counts,
                           ranges: List<IntRange>,
                           mustBeBroken: List<IntRange>
    ) : Guess {
        var startIndex = 0

        val res = mutableListOf<IntRange>()


        for (cnt in targetCount.counts) {
            var fittedRange = startIndex ..< (startIndex + cnt)

            while (true) {
                val fits = ranges
                    .withIndex()
                    .any { (_, range) -> fittedRange in range }

                if (fits) {
                    res.add(fittedRange)
                    startIndex = fittedRange.last + 2
                    break
                }

                fittedRange = fittedRange.shr()
            }
        }

        val rightmostIndex = ranges.last().last
//        return Guess(res)
        fun pushRight(i: Int, shiftedRes: MutableList<IntRange>): Boolean {
            val current = shiftedRes[i]
            var shifted = current

            while (true) {
                shifted = shifted.shr()
                var next = shiftedRes.getOrNull(i + 1)
                if (shifted.last > rightmostIndex) {
                    return false
                }

                if (next != null && shifted.last >= next.first - 1) {
                    val pushNextRes = pushRight(i + 1, shiftedRes)
                    if (!pushNextRes) {
                        return false
                    }
                }

                // We've pushed and have some free space (probably)

                if (!ranges.any { shifted in it })
                    continue

                val mbbInt = mustBeBroken.filter { shifted.intersectsOrTouches(it) }

                if (!mbbInt.all { it in shifted })
                    continue

                // We're all go do go here!
                shiftedRes[i] = shifted
                break
            }
            return true
        }


        fun pushRight(i: Int, towardsIndex: Int): Boolean {
            val pushRes = res.toMutableList()

            while (pushRes[i].last < towardsIndex) {
                val ok = pushRight(i, pushRes)
                if (!ok) {
                    return false
                }
            }

            for (j in res.indices) {
                res[j] = pushRes[j]
            }

            return true
        }

        fun coverBrokenIter(): Boolean {
            for (broken in mustBeBroken.reversed()) {
                if (res.any { broken in it }) continue
                val ix = res.indexOfLast { it.first <= broken.first && it.distance() >= broken.distance() }
                if (ix == -1)
                    return false
                val r = pushRight(ix, broken.last)
                if (!r) return false
            }
            return true
        }

        fun coverBroken() {
            while(applyGuess(Guess(res)).counts() != targetCount) {
                if (!coverBrokenIter())
                    throw IllegalArgumentException("Cannot cover broken")

            }
        }

        coverBroken()
        return Guess(res)
    }



    private fun Guess.nextGuesses(ranges: List<IntRange>,
                                  processed: MutableSet<Guess>,
                                  mustBeBroken: List<IntRange>) = sequence {
        if (this@nextGuesses in processed)
            return@sequence
        val endOfLastRange = ranges.last().last
        for ((shiftedRangeIx, shiftedRange) in this@nextGuesses.ranges.asSequence().withIndex()) {
            val nextRange = this@nextGuesses.ranges.getOrNull(shiftedRangeIx + 1)
            var current = shiftedRange

            while (true) {
                current = current.shr()

                if (current.last > endOfLastRange || nextRange?.intersectsOrTouches(current) == true)
                    break

                val mbbInt = mustBeBroken.filter { current.intersectsOrTouches(it) }

                if (!ranges.any { current in it })
                    continue

                if (mbbInt.any { it !in current })
                    continue

                val leftOf = this@nextGuesses.ranges.take(shiftedRangeIx)
                val mbbLeftOf = mustBeBroken.filter { it.last < current.first - 1 }
                if (mbbLeftOf.sumOf { it.last - it.first + 1 } > leftOf.sumOf { it.last - it.first + 1 })
                    break

                val copy = this@nextGuesses.ranges.toMutableList()
                copy[shiftedRangeIx] = current
                val newGuess = Guess(copy)
                if (newGuess !in processed) {
                    yield(newGuess)
                }
                break
            }
        }
        processed.add(this@nextGuesses)
    }

    fun applyGuess(guess: Guess): Pattern {
        val res = states.toMutableList()

        for ((ix, state) in res.withIndex()) {
            if (state == PatternState.UNKNOWN) {
                res[ix] = if (guess.ranges.any { ix in it }) PatternState.BROKEN else PatternState.WORKING
            }
        }

        return Pattern(res)
    }

    private fun traversePatterns(seed: Guess, ranges: List<IntRange>,
                                 processed: MutableSet<Guess>,
                                 mustBeBroken: List<IntRange>): Sequence<Guess> = sequence {
        for (next in seed.nextGuesses(ranges, processed, mustBeBroken)) {
            yieldAll(traversePatterns(next, ranges, processed, mustBeBroken))
        }
        yield(seed)
    }

    fun matchingPatterns(targetCounts: Counts) : Sequence<Pattern> {
        val possibleRanges = possibleRanges()
        val mbb = mustBeBroken()
        val guess = firstGuess(targetCounts, possibleRanges, mustBeBroken())
        var i = 0
        return traversePatterns(guess, possibleRanges, mutableSetOf(), mbb).map {
            this@Pattern.applyGuess(it)
        }.filter {
            it.counts() == targetCounts
        }
    }

    override fun toString(): String = states.map {
        when (it) {
            PatternState.WORKING -> '.'
            PatternState.BROKEN -> '#'
            PatternState.UNKNOWN -> '?'
        }
    }.joinToString("")


    companion object {
        fun from(string: String) = Pattern(string.map { PatternState.from(it) })
    }
}

private val SPACE_RE = Regex("""\s+""")

fun sumPatternCount(lines: Sequence<String>) =
    lines.withIndex().map { (ix, it) ->
        val splitted = it.split(SPACE_RE)
        require(splitted.size == 2)
        val pattern = Pattern.from(splitted[0])
        val target = Counts.from(splitted[1])

        pattern.matchingPatterns(target).toSet().size
    }.sum()