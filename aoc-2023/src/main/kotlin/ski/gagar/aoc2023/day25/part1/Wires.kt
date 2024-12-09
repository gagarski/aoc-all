package ski.gagar.aoc2023.day25.part1

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import ski.gagar.aoc.util.graphs.*

private val FROM_TO_RE = Regex("""\s*:\s*""")
private val TO_RE = Regex("""\s""")

fun makeGraph(lines: Sequence<String>): Graph<String> {
    val bld = GraphBuilder<String>()
    for (line in lines) {
        val fromAndTos = line.split(FROM_TO_RE)
        require(fromAndTos.size == 2)
        val from = fromAndTos[0]
        val tos = fromAndTos[1].split(TO_RE)

        for (to in tos) {
            bld.addVertex(from)
            bld.addVertex(to)
            bld.addNonDirectedEdge(from, to)
        }
    }

    return bld.build()
}

fun <T> List<T>.combos(n: Int = 3): Sequence<Set<T>> {
    fun helper(index: Int = 0, remaining: Int = n, head: PersistentSet<T> = persistentSetOf()): Sequence<Set<T>> = sequence {
        if (remaining == 0)
            return@sequence

        if (remaining == 1) {
            for (edge in this@combos.asSequence().drop(index)) {
                if (edge !in head) {
                    yield(head.add(edge))
                }
            }
        } else {
            for ((ix, edge) in this@combos.withIndex().asSequence().drop(index)) {
                if (edge !in head) {
                    yieldAll(helper(ix + 1, remaining - 1, head.add(edge)))
                }
            }
        }
    }

    return helper()
}

val expected = setOf(
    "fxr" to "fzb",
    "vgk" to "mqb",
    "nmv" to "thl"
)

fun biPartCount(lines: Sequence<String>, targetSize: Int = 3): Int {
    val graph = makeGraph(lines)
    lateinit var cut: KargerResult<String>
    var minSoFar = Int.MAX_VALUE
    while (true) {
        val parts = graph.minCutKarger()

        if (parts.connectingEdges.size == targetSize * 2) {
            cut = parts
            break
        }
    }
    return cut.first.size * cut.second.size
}