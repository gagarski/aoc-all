package ski.gagar.aoc2024.day1.part2

fun freqMap(nums: Sequence<Int>) = nums.groupingBy { it }.eachCount()

fun parseLine(line: String): Pair<Int, Int> {
    val parts = line.split("""\s+""".toRegex())
    require(parts.size == 2)
    return Pair(parts[0].toInt(), parts[1].toInt())
}

fun similarityScore(nums: Sequence<Int>, freqs: Map<Int, Int>) =
    nums.map { it * (freqs[it] ?: 0) }.sum()

fun similarityScore(lines: Sequence<String>): Int {
    val pairs = lines.map { parseLine(it) }.toList()
    val first = pairs.asSequence().map { it.first }.sorted()
    val freqs = freqMap(pairs.asSequence().map { it.second })
    return similarityScore(first, freqs)
}