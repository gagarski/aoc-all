package ski.gagar.aoc2025.day12.part1

import java.util.BitSet

private val ENFORCE_OPTIMIZE_CONDITIONS = true

@ConsistentCopyVisibility
data class Gift private constructor(private val rows: List<BitSet>, val width: Int, val id: Int) {
    val height: Int = rows.size
    val char: Char
    init {
        require(id in 0..36) // Can be lifted but requires smarter one-char representation
        char = id.toString(36)[0]
    }

    val realArea: Int =
        rows.sumOf { it.cardinality() }
    val rectArea: Int =
        width * height

    operator fun get(row: Int, col: Int): Boolean {
        require(row in 0..<width)
        require(col in 0..<height)
        return rows[row][col]
    }

    fun rotate(): Gift {
        val buf = List(width) { BitSet(height) }
        for (i in 0..<height) {
            for (j in 0..<width) {
                buf[j][height - i - 1] = this[i, j]
            }
        }
        return Gift(buf, height, id)
    }

    override fun toString() = buildString {
        for (row in 0..<height) {
            for (col in 0..<width) {
                when {
                    this@Gift[row, col] -> append(id)
                    else -> append(".")
                }
            }
            append("\n")
        }
    }

    companion object {
        val ID_RE = """([0-9]+):""".toRegex()
        fun parse(lines: Sequence<String>): Gift {
            val itr = lines.iterator()
            require(itr.hasNext()) {
                "Gift should have an id"
            }
            val idStr = itr.next()
            val idMatch = ID_RE.matchEntire(idStr)
            require(null != idMatch) {
                "$idStr is not a valid header"
            }
            val id = idMatch.groupValues[1].toInt()
            val lines = mutableListOf<BitSet>()
            var width: Int? = null
            for ((row, line) in itr.withIndex()) {
                val bitSet = BitSet(line.length)
                require(null == width || line.length == width) {
                    // actually no biggie to rectangulize it
                    "Gift $id should have a rectangular shape"
                }
                width = line.length
                for ((col, char) in line.withIndex()) {
                    when (char) {
                        '#' -> bitSet.set(col)
                        '.' -> {}
                        else -> require(false) {
                            "Illegal cell $char at ($row, $col) for gift $id"
                        }
                    }
                }
                lines.add(bitSet)
            }
            require(width != null) {
                "Gift $idStr should have at leat one line"
            }
            return Gift(lines, width, id)
        }
    }
}

data class GiftBag(val gifts: Map<Int, Gift>, val amounts: Map<Int, Int>) {
    init {
        for ((k, v) in amounts) {
            require(k in gifts.keys) {
                "Gift $ (amount $v) is missing a description"
            }
        }
        require(gifts.isNotEmpty())
    }

    // normalized in a way so that width >= height
    private val normalizedGifts by lazy {
        gifts.values.map { if (it.width < it.height) it.rotate() else it }
    }

    private val allGiftsAreSameSize by lazy {
        val w = normalizedGifts.all { it.width == normalizedGifts.first().width }
        val h = normalizedGifts.all { it.height == normalizedGifts.first().height }

        val res = w && h
        require(!ENFORCE_OPTIMIZE_CONDITIONS || res)
        res
    }

    fun willNeverFit(fieldSize: FieldSize) =
        amounts.map { (gift, amount) -> gifts[gift]!!.realArea * amount }.sum() > fieldSize.area

    private fun nDefinitelyFit(fieldSize: FieldSize): Int {
        check(allGiftsAreSameSize)
        val giftW = normalizedGifts.first().width
        val giftH = normalizedGifts.first().height

        val usefulWidth = (fieldSize.width - fieldSize.width % giftW)
        val usefulHeight = (fieldSize.height - fieldSize.height % giftH)

        return (usefulWidth / giftW) * usefulHeight / giftH
    }

    fun willDefinitelyFit(rectangles: List<FieldSize>): Boolean {
        if (!allGiftsAreSameSize) {
            return false
        }
        var rem = normalizedGifts.sumOf { amounts[it.id] ?: 0 }
        for (rect in rectangles) {
            val isSquare = rect.width == rect.height
            rem -=
                if (isSquare)
                    nDefinitelyFit(rect)
                else
                    maxOf(nDefinitelyFit(rect), nDefinitelyFit(rect.rotate()))
        }
        return rem <= 0
    }

    fun willDefinitelyFit(rectangle: FieldSize): Boolean = willDefinitelyFit(listOf(rectangle))


    companion object {
        fun parse(gifts: Map<Int, Gift>, amounts: String): GiftBag {
            val buf = mutableMapOf<Int, Int>()
            for ((ix, amount) in amounts.split(" ").withIndex()) {
                buf[ix] = amount.toInt()
            }
            return GiftBag(gifts, buf)
        }
    }
}

data class GiftWrappingTask(val size: FieldSize, val giftBag: GiftBag) {
    fun willNeverFit(): Boolean  = giftBag.willNeverFit(size)
    fun willDefinitelyFit(): Boolean = giftBag.willDefinitelyFit(size)

    companion object {
        val SPLIT_RE = """\s*:\s*""".toRegex()
        val SPACES = """\s+""".toRegex()
        fun parse(gifts: Map<Int, Gift>, taskLine: String): GiftWrappingTask {
            val sizeAndAmounts = taskLine.split(SPLIT_RE)
            require(sizeAndAmounts.size == 2)
            val size = FieldSize.parse(sizeAndAmounts[0])
            val amounts = sizeAndAmounts[1].split(SPACES).map { it.toInt() }.withIndex()
                .associate { it.index to it.value }
            return GiftWrappingTask(size, GiftBag(gifts, amounts))
        }
    }
}

private class Parser {
    sealed interface State {
        fun consume(line: String): State
    }

    data object WaitingForGiftOrTask : State {
        override fun consume(line: String): State {
            TODO("Not yet implemented")
        }
    }

    inner class ParsingGift(val id: Int) : State {
        override fun consume(line: String): State {
            TODO("Not yet implemented")
        }
    }

    data object WaitingForTask : State {
        override fun consume(line: String): State {
            TODO()
        }
    }

    private val gifts = mutableListOf<Gift>()
    private val tasks = mutableListOf<GiftWrappingTask>()
}


data class FieldSize(val width: Int, val height: Int) {
    val area = width * height

    fun rotate() = FieldSize(height, width)


    companion object {
        fun parse(string: String): FieldSize {
            val parts = string.split("x")
            require(parts.size == 2)
            return FieldSize(parts[0].toInt(), parts[1].toInt())
        }
    }
}

fun parseTaskList(lines: Sequence<String>) = sequence {
    val gifts = mutableListOf<Gift>()
    val itr = lines.iterator()
    var giftsRead = false
    var giftLines: MutableList<String>? = null

    fun flushGift() {
        require(giftLines != null)
        gifts.add(Gift.parse(giftLines!!.asSequence()))
    }

    while (itr.hasNext()) {
        val line = itr.next()

        when {
            line.isEmpty() -> {
                if (giftLines != null) {
                    flushGift()
                    giftLines = null
                }
            }
            Gift.ID_RE.matchEntire(line) != null -> {
                require(!giftsRead)
                if (giftLines != null) {
                    flushGift()
                }
                giftLines = mutableListOf(line)
            }
            giftLines != null -> {
                giftLines.add(line)
            }
            else -> {
                giftsRead = true
                yield(GiftWrappingTask.parse(gifts.associateBy { it.id }, line))
            }
        }
    }

    if (!giftsRead && giftLines != null) {
        flushGift()
    }
}

fun countWrappable(lines: Sequence<String>): Int? {
    val tasks = parseTaskList(lines).toList()
    val neverFit = tasks.count { it.willNeverFit() }
    val defFit = tasks.count { it.willDefinitelyFit() }
    
    if (neverFit + defFit != tasks.size) {
        return null
    }
    return defFit
}