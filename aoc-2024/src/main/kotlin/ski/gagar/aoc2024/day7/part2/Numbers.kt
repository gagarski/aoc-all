package ski.gagar.aoc2024.day7.part2

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

enum class Operator {
    PLUS {
        override fun perform(op1: Long, op2: Long): Long = op1 + op2
        override fun toString(): String = "+"
    },
    TIMES {
        override fun perform(op1: Long, op2: Long): Long = op1 * op2
        override fun toString(): String = "*"
    },
    CONCAT {
        override fun perform(op1: Long, op2: Long): Long = (op1.toString() + op2.toString()).toLong()
        override fun toString(): String = "||"
    };

    abstract fun perform(op1: Long, op2: Long): Long
}

data class IntermediateResult(
    val operands: List<Long>,
    val target: Long,
    val res: Long = operands[0],
    val ops: PersistentList<Operator> = persistentListOf(),
) {
    fun with(op: Operator): IntermediateResult {
        val ix = ops.size
        return IntermediateResult(operands, target, op.perform(res, operands[ix + 1]), ops.add(op))
    }

    fun isComplete(): Boolean = ops.size == operands.size - 1
    fun isTargetAchievable(): Boolean {
        if (isComplete()) {
            return res == target
        } else {
            return res <= target
        }
    }

    fun isTargetAchieved() = isComplete() && res == target

    fun next() = sequence {
        if (isComplete())
            return@sequence
        if (!isTargetAchievable())
            return@sequence
        for (op in Operator.entries) {
            yield(with(op))
        }
    }

    override fun toString() = buildString {
        append(target)
        if (isTargetAchieved())
            append(" = ")
        else if (isComplete()) {
            append(" != ")
        } else if (!isTargetAchievable()) {
            append(" ?!=")
        } else {
            append(" ?= ")
        }
        append(operands[0])

        for (i in 1 ..< operands.size) {
            append(" ")
            val op = ops.getOrNull(i - 1) ?: "?"
            append(op)
            append(" ")
            append(operands[i])
            append(" ")
        }
    }
}

fun getOperators(target: Long, operands: List<Long>): IntermediateResult? {
    val queue = ArrayDeque<IntermediateResult>()
    queue.addLast(IntermediateResult(operands, target))

    while (queue.isNotEmpty()) {
        val res = queue.removeFirst()
        if (res.isTargetAchieved())
            return res
        for (nxt in res.next()) {
            queue.addLast(nxt)
        }
    }
    return null
}

fun getOperators(str: String): IntermediateResult? {
    val parts = str.split(": ")
    require(parts.size == 2)
    val target = parts[0].toLong()
    val operands = parts[1].split(" ").map { it.toLong() }

    return getOperators(target, operands)
}

fun sumAchievable(lines: Sequence<String>): Long =
    lines
        .map { getOperators(it) }
        .filterNotNull()
        .map { it.res }
        .sum()