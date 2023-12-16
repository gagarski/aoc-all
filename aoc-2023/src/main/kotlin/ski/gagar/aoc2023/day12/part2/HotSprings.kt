package ski.gagar.aoc2023.day12.part2

import ski.gagar.aoc2023.day12.part1.*

fun Counts.Companion.fromFolded(string: String, nFold: Int = 5) =
    from(
        sequence {
            repeat(nFold) {
                yield(string)
            }
        }.joinToString(",")
    )

fun Pattern.Companion.fromFolded(string: String, nFold: Int = 5) =
    from(
        sequence {
            repeat(nFold) {
                yield(string)
            }
        }.joinToString("?")
    )

data class MemoKey(val fieldHeadSize: Int, val countHeadSize: Int, val lastIsBroken: Boolean)

private fun Pattern.nCombos(targetCounts: Counts,
                            possibleRanges: List<IntRange>,
                            mustBeBroken: List<IntRange>,
                            fieldHeadSize: Int = 0,
                            countHeadSize: Int = 0,
                            headPattern: Pattern = Pattern(listOf()),
                            rangesHead: List<IntRange> = listOf(),
                            memo: MutableMap<MemoKey, Long> = mutableMapOf(),
                            depth: Int = 0
): Long {
    val lastIsBroken = headPattern.states.lastOrNull() == PatternState.BROKEN

    fun isHeadViable(): Boolean {
        if (rangesHead.isEmpty())
            return true

        val countsHead = targetCounts.counts.take(countHeadSize)
        val headLast = rangesHead.last().last
        val allInRanges = headPattern.counts().counts == countsHead
        val mbbInOrNearHead = mustBeBroken.filter { it.start <= headLast + 1 }
        val allMbbSatisfied = mbbInOrNearHead.all { mbb -> rangesHead.any { h -> mbb in h } }

        return allInRanges && allMbbSatisfied
    }

    if (!isHeadViable()) {
        return 0
    }

    val memoized = memo[MemoKey(fieldHeadSize, countHeadSize, lastIsBroken)]

    if (null != memoized) {
        return memoized
    }

    val fieldTail = states.drop(fieldHeadSize)

    if (fieldHeadSize == states.size && countHeadSize != targetCounts.counts.size) {
        return 0
    }

    if (countHeadSize == targetCounts.counts.size) {
        if (fieldTail.all { it != PatternState.BROKEN }) {
            return 1
        } else {
            return 0
        }
    }


    var count = 0L

    val head = targetCounts.counts[countHeadSize]

    for (i in fieldHeadSize ..  states.size - head) {
        val newRange = i until i + head
        val newHead: List<IntRange> = rangesHead + listOf(newRange)

        val chs = countHeadSize + 1
        val fhs = newRange.last + 1
        val newHeadPattern = Pattern(states.take(fhs)).applyGuess(Pattern.Guess(newHead))


        count +=
            nCombos(
                targetCounts,
                possibleRanges,
                mustBeBroken,
                fhs, chs, newHeadPattern, newHead, memo, depth + 1
            )

    }
    memo[MemoKey(fieldHeadSize, countHeadSize, lastIsBroken)] = count
    return count
}

private fun Pattern.nCombos(targetCounts: Counts) = nCombos(targetCounts, possibleRanges(), mustBeBroken())

private val SPACE_RE = Regex("""\s+""")

fun sumPatternCountFolded(lines: Sequence<String>) =
    lines.withIndex().map { (ix, it) ->
        val splitted = it.split(SPACE_RE)
        require(splitted.size == 2)
        val pattern = Pattern.fromFolded(splitted[0], 5)
        val target = Counts.fromFolded(splitted[1], 5)

        pattern.nCombos(target)
    }.sum()