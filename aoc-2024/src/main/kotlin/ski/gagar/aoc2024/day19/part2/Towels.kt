package ski.gagar.aoc2024.day19.part2

import ski.gagar.aoc2024.day19.part1.PatternTree

fun PatternTree.countCombos(towel: String): Long {
    fun helper(startPos: Int = 0, memo: MutableMap<Int, Long> = mutableMapOf()): Long {
        if (startPos == towel.length) return 1L

        val memoized = memo[startPos]
        if (null != memoized)
            return memoized

        val computed = getPatternsForTowel(towel, startPos).sumOf {
            helper(startPos + it.length, memo)
        }

        memo[startPos] = computed

        return computed
    }
    return helper()
}

fun sumCountCombos(lines: Sequence<String>): Long {
    val itr = lines.iterator()

    require(itr.hasNext())

    val patterns = itr.next().split(", ")
    val tree = PatternTree(patterns.asSequence())

    require(itr.hasNext())
    itr.next() // ignoring empty line

    return itr.asSequence().sumOf { towel ->
        tree.countCombos(towel)
    }
}