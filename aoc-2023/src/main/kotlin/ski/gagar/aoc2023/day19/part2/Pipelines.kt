package ski.gagar.aoc2023.day19.part2

import ski.gagar.aoc2023.day19.part1.*
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayDeque

class BranchingResult(val ifTrue: Ranges, val ifFalse: Ranges)

class Ranges(traitRanges: Map<Trait, IntRange> = mapOf(), default: IntRange = 1..4000) {
    private val ranges: Map<Trait, IntRange>

    init {
        val toSet = EnumMap<Trait, IntRange>(Trait::class.java)

        for (trait in Trait.entries) {
            toSet[trait] = traitRanges[trait] ?: default
        }

        ranges = toSet
    }

    val nCombos: BigInteger by lazy {
        ranges.values.fold(BigInteger.ONE) { acc, range ->
            acc * BigInteger.valueOf((range.last - range.first + 1).toLong())
        }
    }

    private fun andGt(trait: Trait, value: Int, inclusive: Boolean = false): IntRange {
        val range = ranges[trait]!!

        val newFirst = if (inclusive) value else value + 1

        if (range.first >= newFirst) return range
        else return newFirst..range.last
    }

    private fun andLt(trait: Trait, value: Int, inclusive: Boolean = false): IntRange {
        val range = ranges[trait]!!

        val newLast = if (inclusive) value else value - 1

        if (range.last <= newLast) return range
        else return range.first..newLast
    }

    private fun updatedWith(trait: Trait, newRange: IntRange): Ranges {
        val oldRange = ranges[trait]

        if (oldRange == newRange) return this

        val rangesCopy = EnumMap(ranges)
        rangesCopy[trait] = newRange
        return Ranges(rangesCopy)
    }

    fun and(branch: PipelineBranch) = when (branch) {
        is Unconditional -> this
        is GtConditional -> updatedWith(branch.trait, andGt(branch.trait, branch.value))
        is LtConditional -> updatedWith(branch.trait, andLt(branch.trait, branch.value))
    }

    fun andNot(branch: PipelineBranch) = when (branch) {
        is Unconditional -> EMPTY
        is GtConditional -> updatedWith(branch.trait, andLt(branch.trait, branch.value, true))
        is LtConditional -> updatedWith(branch.trait, andGt(branch.trait, branch.value, true))
    }

    fun branch(branch: PipelineBranch) = BranchingResult(and(branch), andNot(branch))

    companion object {
        @Suppress("EmptyRange")
        val EMPTY = Ranges(default = 0..-1)
    }
}

data class RangeAnalysisResult(
    val accepted: List<Ranges>,
    val rejected: List<Ranges>
)

private data class QueueItem(
    val ranges: Ranges,
    val dest: String
)

fun Pipelines.analyzeRanges(startRanges: Ranges = Ranges(), startPipeline: String = "in"): RangeAnalysisResult {
    val accepted = mutableListOf<Ranges>()
    val rejected = mutableListOf<Ranges>()

    val queue = ArrayDeque<QueueItem>()
    queue.add(QueueItem(startRanges, startPipeline))

    while (queue.isNotEmpty()) {
        val (ranges, plName) = queue.removeFirst()
        val pipeline = pipelines[plName] ?: throw IllegalArgumentException("Pipeline $plName not found")

        var notPrev = ranges

        for (branch in pipeline.branches) {
            val branched = notPrev.branch(branch)
            when (val dst = branch.dest) {
                Accept -> accepted.add(branched.ifTrue)
                Reject -> rejected.add(branched.ifTrue)
                is JumpToPipeline -> queue.add(QueueItem(branched.ifTrue, dst.name))
            }
            notPrev = branched.ifFalse
        }
    }

    return RangeAnalysisResult(accepted, rejected)
}

fun nCombos(input: String) =
    PipelinesParser.parse(input).pipelines.analyzeRanges().accepted.sumOf { it.nCombos }
