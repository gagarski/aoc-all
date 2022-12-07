package ski.gagar.aoc2015.day16.part1

import org.codehaus.jparsec.Parsers
import org.codehaus.jparsec.Scanners
import org.codehaus.jparsec.Terminals
import org.codehaus.jparsec.pattern.CharPredicates
import ski.gagar.aoc.util.getResourceAsStream

enum class AuntCharacteristic {
    CHILDREN, CATS, SAMOYEDS, POMERANIANS, AKITAS, VIZSLAS, GOLDFISH, TREES, CARS, PERFUMES
}

data class AuntDescription(val index: Int, val chars: Map<AuntCharacteristic, Int>)

data class UnknownAunt(val chars: Map<AuntCharacteristic, Int>) {
    fun matches(desc: AuntDescription) =
        desc.chars.all { (k, v) -> chars[k] == v }

}

fun findFirstMatching(aunt: UnknownAunt, descs: Sequence<AuntDescription>) =
    descs.find { aunt.matches(it) }

object AuntParser {
    private val ZERO_OR_MORE_WHITESPACES = Scanners.isChar(CharPredicates.IS_WHITESPACE).many()
    private val CHARACTERISTIC_KEY = Parsers.or(
        AuntCharacteristic.values().asSequence().map { Scanners.string(it.name.lowercase()).source() }.asIterable()
    ).map {
        AuntCharacteristic.valueOf(it.uppercase())
    }

    private val CHARACTERISTIC = Parsers.sequence(
        CHARACTERISTIC_KEY,
        ZERO_OR_MORE_WHITESPACES,
        Scanners.isChar(':'),
        ZERO_OR_MORE_WHITESPACES,
        Terminals.IntegerLiteral.TOKENIZER
    ) { key, _, _, _, amount ->
        key to amount.text().toInt()
    }

    private val CHARACTERISTICS = CHARACTERISTIC.sepBy(
        Parsers.sequence(
            ZERO_OR_MORE_WHITESPACES,
            Scanners.isChar(','),
            ZERO_OR_MORE_WHITESPACES,
        )
    ).map {
        it.toMap()
    }

    private val AUNT_INDEX = Parsers.sequence(
        Scanners.string("Sue"),
        Scanners.WHITESPACES,
        Terminals.IntegerLiteral.TOKENIZER,
    ) { _, _, ix ->
        ix.text().toInt()
    }

    private val AUNT_DESCRIPTION = Parsers.sequence(
        AUNT_INDEX,
        ZERO_OR_MORE_WHITESPACES,
        Scanners.isChar(':'),
        ZERO_OR_MORE_WHITESPACES,
        CHARACTERISTICS
    ) { ix, _, _, _, chars ->
        AuntDescription(ix, chars)
    }

    fun parseUnknownAunt(strings: Sequence<String>) =
        UnknownAunt(
            strings.map { CHARACTERISTIC.parse(it) }.toMap()
        )

    fun parseDescription(string: String) = AUNT_DESCRIPTION.parse(string)
}

fun findFirstMatching(auntStrings: Sequence<String>, descs: Sequence<String>) =
    findFirstMatching(AuntParser.parseUnknownAunt(auntStrings), descs.map { AuntParser.parseDescription(it) })?.index

fun day16Part1() {
    println("day16/part1/aunts: ${
        findFirstMatching(
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day16/unknown.txt").bufferedReader().lineSequence(),
            getResourceAsStream("/ski.gagar.aoc.aoc2015.day16/descriptions.txt").bufferedReader().lineSequence()
        )
    }")
}
