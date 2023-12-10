package ski.gagar.aoc2023.day5.part2

import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import org.jparsec.pattern.CharPredicate
import org.jparsec.pattern.Patterns
import ski.gagar.aoc.util.getResourceAsStream
import java.util.*

data class MappingItem(val source: Long, val dest: Long, val size: Long) {
    private operator fun contains(src: Long) = src in (source until source + size)
    fun getNonNull(src: Long) = if (src in this) (dest + src - source) else src
}

operator fun MappingItem?.get(src: Long) = this?.getNonNull(src) ?: src

class Mapping(items: Sequence<MappingItem>) {
    private val map = TreeMap<Long, MappingItem>()

    init {
        for (item in items) {
            map[item.source] = item
        }
    }

    operator fun get(src: Long): Long = map.floorEntry(src)?.value[src]
}

val EMPTY = Mapping(emptySequence())

class AllMappings(
    val seedToSoil: Mapping,
    val soilToFertilizer: Mapping,
    val fertilizerToWater: Mapping,
    val waterToLight: Mapping,
    val lightToTemperature: Mapping,
    val temperatureToHumidity: Mapping,
    val humidityToLocation: Mapping
) {
    operator fun get(seed: Long): Long {
        val soil = seedToSoil[seed]
        val fertilizer = soilToFertilizer[soil]
        val water = fertilizerToWater[fertilizer]
        val light = waterToLight[water]
        val temperature = lightToTemperature[light]
        val humidity = temperatureToHumidity[temperature]
        return humidityToLocation[humidity]
    }
}

data class SeedRange(val from: Long, val size: Long)

class Input(
    val ranges: Set<SeedRange>,
    val mappings: AllMappings
) {
    fun getLowestLocation(): Long? {
        var min: Long? = null
        for (range in ranges) {
            for (i in 0 until range.size) {
                val seed = range.from + i
                val loc = mappings[seed]

                if (null == min || loc < min) {
                    min = loc
                }
            }
        }
        return min
    }
}

object LocationsParser {
    private val IS_KEBAB_ALPHA = object : CharPredicate {
        override fun isChar(c: Char): Boolean =
            c == '-' || c == '_' || c in 'a'..'z' || c in 'A'..'Z'
    }

    private val IS_KEBAB = object : CharPredicate {
        override fun isChar(c: Char): Boolean =
            c == '-' || c == '_' || c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9'
    }

    private val KEBAB_WORD = Patterns.isChar(IS_KEBAB_ALPHA).next(Patterns.isChar(IS_KEBAB).many())
    private val KEBAB_IDENTIFIER = KEBAB_WORD.toScanner("word").source()

    private const val NL = "\n"
    private const val NL_WIN = "\r\n"
    private const val COLON = ":"

    private const val SEEDS = "seeds"
    private const val MAP = "map"

    private const val SEED_TO_SOIL = "seed-to-soil"
    private const val SOIL_TO_FERTILIZER = "soil-to-fertilizer"
    private const val FERTILIZER_TO_WATER = "fertilizer-to-water"
    private const val WATER_TO_LIGHT = "water-to-light"
    private const val LIGHT_TO_TEMPERATURE = "light-to-temperature"
    private const val TEMPERATURE_TO_HUMIDITY = "temperature-to-humidity"
    private const val HUMIDITY_TO_LOCATION = "humidity-to-location"

    private val TERMINALS = Terminals.operators(COLON, NL, NL_WIN)
        .words(KEBAB_IDENTIFIER)
        .keywords(
            SEEDS,
            MAP,
            SEED_TO_SOIL,
            SOIL_TO_FERTILIZER,
            FERTILIZER_TO_WATER,
            WATER_TO_LIGHT,
            LIGHT_TO_TEMPERATURE,
            TEMPERATURE_TO_HUMIDITY,
            HUMIDITY_TO_LOCATION
        )
        .build()

    private val NON_BR_WHITESPACES = setOf(' ', '\t')

    private val NEWLINE = Parsers.or(
        TERMINALS.token(NL),
        TERMINALS.token(NL_WIN)
    )

    private val INT_TOKENIZER = Terminals.IntegerLiteral.TOKENIZER
    private val WHITESPACES = Scanners.isChar { it in NON_BR_WHITESPACES }.skipMany()

    private val TOKENIZER = Parsers.or(
        TERMINALS.tokenizer(),
        INT_TOKENIZER
    )

    private val INTEGER = Terminals.IntegerLiteral.PARSER.map { int ->
        int.toLong().also {
            check(it >= 0)
        }
    }

    private val INT_SET = INTEGER.many().map { it.toSet() }

    private val SEED_RANGE = Parsers.sequence(INTEGER, INTEGER) { from, size ->
        SeedRange(from, size)
    }

    private val SEED_RANGES = SEED_RANGE.many().map { it.toSet() }

    private val SEED_RANGES_HEADER = Parsers.sequence(
        TERMINALS.token(SEEDS), TERMINALS.token(COLON), SEED_RANGES
    ) { _, _, ranges -> ranges}

    private val MAPPING_ITEM = Parsers.sequence(
        INTEGER, INTEGER, INTEGER
    ) { dest, src, size ->
        MappingItem(src, dest, size)
    }

    private val MAPPING_BODY = MAPPING_ITEM.sepBy(NEWLINE.many1()).map {
        Mapping(it.asSequence())
    }

    private val SEED_TO_SOIL_MT =
        TERMINALS.token(SEED_TO_SOIL).map { MappingType.SEED_TO_SOIL }
    private val SOIL_TO_FERTILIZER_MT =
        TERMINALS.token(SOIL_TO_FERTILIZER).map { MappingType.SOIL_TO_FERTILIZER }
    private val FERTILIZER_TO_WATER_MT =
        TERMINALS.token(FERTILIZER_TO_WATER).map { MappingType.FERTILIZER_TO_WATER }
    private val WATER_TO_LIGHT_MT =
        TERMINALS.token(WATER_TO_LIGHT).map { MappingType.WATER_TO_LIGHT }
    private val LIGHT_TO_TEMPERATURE_MT =
        TERMINALS.token(LIGHT_TO_TEMPERATURE).map { MappingType.LIGHT_TO_TEMPERATURE }
    private val TEMPERATURE_TO_HUMIDITY_MT =
        TERMINALS.token(TEMPERATURE_TO_HUMIDITY).map { MappingType.TEMPERATURE_TO_HUMIDITY }
    private val HUMIDITY_TO_LOCATION_MT =
        TERMINALS.token(HUMIDITY_TO_LOCATION).map { MappingType.HUMIDITY_TO_LOCATION }
    private val MAPPING_TYPE = Parsers.or(
        SEED_TO_SOIL_MT,
        SOIL_TO_FERTILIZER_MT,
        FERTILIZER_TO_WATER_MT,
        WATER_TO_LIGHT_MT,
        LIGHT_TO_TEMPERATURE_MT,
        TEMPERATURE_TO_HUMIDITY_MT,
        HUMIDITY_TO_LOCATION_MT
    )

    private val MAPPING_HEADER = Parsers.sequence(
        MAPPING_TYPE, TERMINALS.token(MAP), TERMINALS.token(COLON)
    ) { type, _, _ -> type }

    private val MAPPING = Parsers.sequence(
        MAPPING_HEADER,
        NEWLINE.many1(),
        MAPPING_BODY
    ) { type, _, mapping -> type to mapping }

    private val MAPPINGS = MAPPING.sepBy(
        NEWLINE.many1()
    ).map {
        val mappings = it.toMap()
        AllMappings(
            seedToSoil = mappings[MappingType.SEED_TO_SOIL] ?: EMPTY,
            soilToFertilizer = mappings[MappingType.SOIL_TO_FERTILIZER] ?: EMPTY,
            fertilizerToWater = mappings[MappingType.FERTILIZER_TO_WATER] ?: EMPTY,
            waterToLight = mappings[MappingType.WATER_TO_LIGHT] ?: EMPTY,
            lightToTemperature = mappings[MappingType.LIGHT_TO_TEMPERATURE] ?: EMPTY,
            temperatureToHumidity = mappings[MappingType.TEMPERATURE_TO_HUMIDITY] ?: EMPTY,
            humidityToLocation = mappings[MappingType.HUMIDITY_TO_LOCATION] ?: EMPTY
        )
    }

    private val INPUT = Parsers.sequence(
        SEED_RANGES_HEADER,
        NEWLINE.many1(),
        MAPPINGS,
        NEWLINE.many()
    ) { seeds, _, mappings, _ ->
        Input(seeds, mappings)
    }

    fun parse(str: String) = INPUT.from(TOKENIZER, WHITESPACES).parse(str)

    private enum class MappingType {
        SEED_TO_SOIL,
        SOIL_TO_FERTILIZER,
        FERTILIZER_TO_WATER,
        WATER_TO_LIGHT,
        LIGHT_TO_TEMPERATURE,
        TEMPERATURE_TO_HUMIDITY,
        HUMIDITY_TO_LOCATION
    }
}


fun lowestLocation(input: String) = LocationsParser.parse(input).getLowestLocation()
