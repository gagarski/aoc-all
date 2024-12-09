package ski.gagar.aoc2024.day3.part1

private val MUL_RE = """mul\((\d+),(\d+)\)""".toRegex()

fun processMatchResult(matchResult: MatchResult): Long {
    val op1 = matchResult.groups[1]?.value?.toLong() ?: throw IllegalArgumentException("Invalid input")
    val op2 = matchResult.groups[2]?.value?.toLong() ?: throw IllegalArgumentException("Invalid input")
    return op1 * op2
}

fun processLine(line: String): Long =
    MUL_RE.findAll(line).map { processMatchResult(it) }.sum()

fun processLines(lines: Sequence<String>): Long = lines.map { processLine(it) }.sum()