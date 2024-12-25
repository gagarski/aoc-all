package ski.gagar.aoc2024.day22.part1

infix fun Long.shlr(bits: Int) = if (bits >= 0) shl(bits) else shr(-bits)

fun Long.shiftMixPrune(shiftBits: Int, pruneBits: Int = 24) =
    (this xor (this shlr shiftBits)) and ((1L shl pruneBits) - 1)

fun Long.nextRandom() =
    shiftMixPrune(6)
        .shiftMixPrune(-5)
        .shiftMixPrune(11)

fun Long.randomSeq() = generateSequence(this) { it.nextRandom() }

fun Long.nThRandom(n: Int) = randomSeq().drop(n).first()

fun sumNthRandom(lines: Sequence<String>, n: Int = 2000): Long =
    lines.map { it.toLong().nThRandom(n) }.sum()