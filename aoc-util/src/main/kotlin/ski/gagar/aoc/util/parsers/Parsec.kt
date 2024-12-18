package ski.gagar.aoc.util.parsers

import org.jparsec.Parser
import org.jparsec.Parsers

fun <T> Parser<T>.sepOrSurroundedBy(separator: Parser<*>) = Parsers.sequence(
    separator.many(),
    this.sepBy(separator.many1()),
    separator.many()
) { _, p, _ -> p }