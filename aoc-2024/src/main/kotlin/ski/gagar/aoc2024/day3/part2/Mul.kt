package ski.gagar.aoc2024.day3.part2

private val MUL_RE = """mul\((\d+),(\d+)\)""".toRegex()
private val DO = """do\(\)""".toRegex()
private val DONT = """don't\(\)""".toRegex()

fun processMatchResult(matchResult: MatchResult): Long {
    val op1 = matchResult.groups[1]?.value?.toLong() ?: throw IllegalArgumentException("Invalid input")
    val op2 = matchResult.groups[2]?.value?.toLong() ?: throw IllegalArgumentException("Invalid input")
    return op1 * op2
}

enum class InstructionType {
    DO, DONT
}

data class Instruction(val type: InstructionType, val range: IntRange)

operator fun IntRange.contains(other: IntRange): Boolean =
    other.first in this && other.last in this


fun processText(lines: String): Long {
    val conditionals = mutableListOf<Instruction>()
    DO.findAll(lines).mapTo(conditionals) { Instruction(InstructionType.DO, it.range) }
    DONT.findAll(lines).mapTo(conditionals) { Instruction(InstructionType.DONT, it.range) }

    conditionals.sortBy { it.range.first }

    val enabledRanges = mutableListOf<IntRange>()
    var currentStart = 0
    var currentType = InstructionType.DO
    var lastCond: Instruction? = null

    for (conditional in conditionals) {
        if (conditional.type != currentType) {
            val length = conditional.range.first - currentStart

            if (length != 0 && currentType == InstructionType.DO) {
                enabledRanges.add(IntRange(currentStart, conditional.range.first))
            }
            currentStart = conditional.range.last + 1
            currentType = conditional.type
        }
        lastCond = conditional
    }

    when {
        lastCond == null -> enabledRanges.add(IntRange(0, lines.length))
        lastCond.type == InstructionType.DO -> enabledRanges.add(IntRange(lastCond.range.last, lines.length))
    }


    return MUL_RE.findAll(lines).filter { cmd -> enabledRanges.any { cmd.range in it } }.map { processMatchResult(it) }.sum()
}