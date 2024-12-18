package ski.gagar.aoc2024.day17.part2

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import ski.gagar.aoc2024.day17.part1.CpuDescription
import ski.gagar.aoc2024.day17.part1.CpuDescriptionParser
import ski.gagar.aoc2024.day17.part1.Processor
import java.util.*

enum class Command(val opCode: Int) {
    ADV(0), BXL(1), BST(2), JNZ(3), BXC(4), OUT(5), BDV(6), CDV(7);

    init {
        require(opCode in 0..7)
    }

    companion object {
        private val byOpCode = entries.associateBy { it.opCode }
        fun from(opCode: Int) = byOpCode[opCode] ?: error("Unknown opcode $opCode")
    }
}

data class PreparsedCommand(val command: Command, val operand: Int)

fun getChunkSize(program: List<Int>): Int {
    require(program.size % 2 == 0)
    val parsed = program.asSequence().chunked(2).map {
        PreparsedCommand(Command.from(it[0]), it[1])
    }.toList()
    check(parsed.last() == PreparsedCommand(Command.JNZ, 0))
    check(parsed[parsed.size - 2] == PreparsedCommand(Command.OUT, 5))
    val aWrites = parsed.filter { it.command == Command.ADV }
    check(aWrites.size == 1)
    check(parsed.indexOf(aWrites.first()) == parsed.size - 3)
    check(parsed.first() == PreparsedCommand(Command.BST, 4))
    return aWrites.first().operand
}

fun List<Int>.isPartialQuine(program: List<Int>): Boolean =
    isNotEmpty() && program.take(size) == this

sealed interface QuineResult {
    val depth: Int
}
data object Quine: QuineResult {
    override val depth = 0
}

data object NotAQuine: QuineResult {
    override val depth = 0
}

data class TreeResult(val output: NavigableMap<Int, QuineResult>, override val depth: Int): QuineResult {
    fun quines(prefix: PersistentList<Int> = persistentListOf()): Sequence<List<Int>> = sequence {
        for ((k, v) in output.entries) {
            when (v) {
                is Quine -> yield(prefix.add(k))
                is NotAQuine -> {}
                is TreeResult -> yieldAll(v.quines(prefix.add(k)))
            }
        }
    }
}

fun List<Int>.toLong(chunkSize: Int): Long {
    var mul = 1L
    var res = 0L
    for (item in this.reversed()) {
        res += item * mul
        mul = mul shl chunkSize
    }
    return res
}

fun getQuinesTree(b: Long,
                  c: Long,
                  program: List<Int>,
                  chunkSize: Int,
                  quinePosition: Int,
                  depth: Int,
                  chunks: PersistentList<Int> = persistentListOf()
): QuineResult {

    if (depth == 0) {
        val chunks = chunks.toPersistentList().addAll(List(quinePosition) { 0 })
        // HACK: based on pour knowledge about the program,
        //       ensuring that program will run for enough number of steps
        val a = chunks.toLong(chunkSize).or(1L shl 62)
        val out = mutableListOf<Int>()
        val cpu = Processor(
            a, b, c, program
        ) {
            out.add(it)
        }
        cpu.run()

        if (out.getOrNull(quinePosition) == program[quinePosition]) {
            return Quine
        } else {
            return NotAQuine
        }
    }

    val tree = TreeMap<Int, QuineResult>()
    (0 until (1 shl chunkSize)).mapNotNull { guess ->
        guess to getQuinesTree(
            b = b,
            c = c,
            program = program,
            chunkSize = chunkSize,
            quinePosition = quinePosition,
            chunks = chunks.add(guess),
            depth = depth - 1
        )


    }.toMap(tree)


    val depth = tree.firstEntry().value.depth

    require(tree.values.all { it.depth == depth })

    return TreeResult(tree, depth + 1)
}

fun QuineResult.isReliable(): Boolean {
    if (this !is TreeResult) return false

    val first = output.firstEntry().value

    return output.values.all { it == first }

}


fun getSmallestReliableTree(cpuD: CpuDescription, position: Int, maxDepth: Int = 8): QuineResult {
    // HACK: 2 is here for a reason
    //       for depth of 1 we're getting reliable tree of non-quines
    //       which should not be the case
    //       Fix might be to not treat fully non-quine tree as "reliable".
    //       A proper fix would be to reimplement CPU with tri-state logic: 1/0/U
    //       having leftmost bits as U in getQuineTrees and treading outputs with U bits accordingly
    for (depth in 2..maxDepth) {
        val tree = getQuinesTree(
            b = cpuD.registers["B"]!!,
            c = cpuD.registers["C"]!!,
            program = cpuD.program!!,
            chunkSize = getChunkSize(program = cpuD.program!!),
            quinePosition = position,
            depth = depth
        )
        if (tree.isReliable())
            return tree
    }

    error("Not able to find a reliable tree for $position")
}

typealias ReliableTrees = NavigableMap<Int, QuineResult>

fun getSmallestReliableTrees(cpuD: CpuDescription, maxDepth: Int = 8): ReliableTrees =
    cpuD.program!!.indices.associateWithTo(TreeMap()) { index ->
        getSmallestReliableTree(cpuD, index, maxDepth)
    }

fun List<Int>.combinePath(other: List<Int>): List<Int>? {
    require(this.isNotEmpty())
    require(this.size >= other.size)
    val res = MutableList(this.size + 1) { 0 }

    val copyThis = this.size - other.size + 1

    for (i in 0 ..< copyThis) {
        res[i] = this[i]
    }

    for (i in 0 ..< other.size - 1) {
        val fromThis = this[copyThis + i]
        val fromOther = other[i]

        if (fromThis != fromOther)
            return null
        res[copyThis + i] = fromThis
    }

    res[this.size] = other.last()

    return res
}

fun ReliableTrees.combine(): List<Int> {
    val (highestIndex, highestResult) = this.lastEntry()
    require(highestResult is TreeResult)
    for (path in highestResult.quines()) {
        val res = combine(path, highestIndex - 1)

        if (res != null)
            return res
    }
    error("Failed to combine")
}

fun ReliableTrees.combine(prevPath: List<Int>, index: Int): List<Int>? {
    if (index == -1)
        return prevPath

    val tree = this[index]

    require(tree is TreeResult)

    for (path in tree.quines()) {
        val combinedPath = prevPath.combinePath(path) ?: continue

        val res = combine(combinedPath, index - 1)

        if (res != null)
            return res
    }

    return null
}

fun quineAValue(desc: String): Long {
    val cpuD = CpuDescriptionParser.parse(desc)
    val trees = getSmallestReliableTrees(cpuD)

    return trees.combine().toLong(getChunkSize(cpuD.program!!))
}