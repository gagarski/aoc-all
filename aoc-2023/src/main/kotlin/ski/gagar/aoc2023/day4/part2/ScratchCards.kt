package ski.gagar.aoc2023.day4.part2

import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2023.day4.part1.ScratchCard
import ski.gagar.aoc2023.day4.part1.ScratchCardParser
import java.util.stream.Stream

fun processPile(lines: Sequence<String>): Int {
    val copies = mutableMapOf<Int, Int>()

    fun processOneCopy(card: ScratchCard) {
        val win = card.intersectSize

        for (i in card.id + 1 .. card.id + win) {
            copies[i] = (copies[i] ?: 0) + 1
        }
    }

    fun processCard(card: ScratchCard) {
        val amount = (copies[card.id] ?: 0) + 1
        copies[card.id] = amount

        for (i in 0 until amount) {
            processOneCopy(card)
        }
    }

    for (line in lines) {
        processCard(ScratchCardParser.parse(line))
    }

    return copies.values.sum()
}
