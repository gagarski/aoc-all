package ski.gagar.aoc2023.day7.part1

import ski.gagar.aoc.util.getResourceAsStream
import java.lang.IllegalArgumentException
import java.util.TreeMap

enum class Card(val char: Char) {
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    TEN('T'),
    JACK('J'),
    QUEEN('Q'),
    KING('K'),
    ACE('A');

    companion object {
        private val fromChar = values().associateBy { it.char }
        fun from(char: Char) = fromChar[char] ?: throw IllegalArgumentException("Unknown card $char")
    }
}


class ListByElementComparator<T : Comparable<T>> : Comparator<List<T>> {
    override fun compare(first: List<T>, second: List<T>): Int {
        for ((firstEl, secondEl) in first.asSequence().zip(second.asSequence())) {
            if (firstEl < secondEl) return -1
            if (firstEl > secondEl) return 1
        }

        return when {
            first.size > second.size -> 1
            first.size < second.size -> -1
            else -> 0
        }
    }

}

class Hand(val cards: List<Card>) : Comparable<Hand> {
    private val byCount: Map<Int, List<Card>>

    init {
        require(cards.size == 5)
        byCount = cards.groupingBy { it }
            .eachCount()
            .asSequence()
            .groupBy {  (card, count) -> count }
            .map { (k, listEntries) -> k to listEntries.map { it.key }}
            .toMap()
    }

    val combo: Combo
        get() = Combo.values().reversed().first { it.containedInHand(this) }

    override fun compareTo(other: Hand): Int =
        compareBy<Hand> { it.combo }
            .thenComparing({ it.cards }, ListByElementComparator())
            .compare(this, other)

    override fun toString(): String {
        return "${cards.map { it.char }.joinToString("")}(${combo})"
    }


    companion object {
        fun from(string: String) = Hand(string.map { Card.from(it) })
    }

    enum class Combo {
        NONE {
            override fun containedInHand(hand: Hand) = true
        },
        PAIR {
            override fun containedInHand(hand: Hand) =
                (hand.byCount[2] ?: listOf()).isNotEmpty()
        },
        TWO_PAIRS {
            override fun containedInHand(hand: Hand) =
                (hand.byCount[2] ?: listOf()).size >= 2
        },
        THREE_OF_A_KIND {
            override fun containedInHand(hand: Hand): Boolean =
                (hand.byCount[3] ?: listOf()).isNotEmpty()
        },
        FULL_HOUSE {
            override fun containedInHand(hand: Hand): Boolean =
                (hand.byCount[3] ?: listOf()).isNotEmpty() &&
                        (hand.byCount[2] ?: listOf()).isNotEmpty()
        },
        FOUR_OF_A_KIND {
            override fun containedInHand(hand: Hand): Boolean =
                (hand.byCount[4] ?: listOf()).isNotEmpty()
        },
        FIVE_OF_A_KIND {
            override fun containedInHand(hand: Hand): Boolean =
                (hand.byCount[5] ?: listOf()).isNotEmpty()
        };

        abstract fun containedInHand(hand: Hand): Boolean
    }
}

class HandWithStake(val hand: Hand, val stake: Long) : Comparable<HandWithStake> {
    override fun compareTo(other: HandWithStake): Int = hand.compareTo(other.hand)
    override fun toString(): String {
        return "HandWithStake(hand=$hand, stake=$stake)"
    }


    companion object {
        fun from(string: String): HandWithStake {
            val parts = string.split(" ")
            require(parts.size == 2)

            return HandWithStake(Hand.from(parts[0]), parts[1].toLong())
        }
    }
}

fun stakesSummary(lines: Sequence<String>) =
    lines.map {
        HandWithStake.from(it)
    }
        .sorted()
        .withIndex()
        .map { (ix, hws) ->
            (ix + 1) * hws.stake
        }.sum()

