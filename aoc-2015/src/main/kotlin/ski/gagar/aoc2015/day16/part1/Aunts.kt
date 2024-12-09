package ski.gagar.aoc2015.day16.part1

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import org.jparsec.pattern.CharPredicates
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

fun findFirstMatching(descs: Sequence<String>,
                      unknownAunt: UnknownAunt = UnknownAunt(
                          mapOf(
                              AuntCharacteristic.CHILDREN to 3,
                              AuntCharacteristic.CATS to 7,
                              AuntCharacteristic.SAMOYEDS to 2,
                              AuntCharacteristic.POMERANIANS to 3,
                              AuntCharacteristic.AKITAS to 0,
                              AuntCharacteristic.VIZSLAS to 0,
                              AuntCharacteristic.GOLDFISH to 5,
                              AuntCharacteristic.TREES to 3,
                              AuntCharacteristic.CARS to 2,
                              AuntCharacteristic.PERFUMES to 1
                          )
                      )
) =
    findFirstMatching(unknownAunt, descs.map { AuntParser.parseDescription(it) })?.index
