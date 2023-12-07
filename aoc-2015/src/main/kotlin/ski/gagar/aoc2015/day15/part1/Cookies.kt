package ski.gagar.aoc2015.day15.part1

import kotlinx.collections.immutable.persistentMapOf
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals
import org.jparsec.pattern.CharPredicates
import ski.gagar.aoc.util.getResourceAsStream

interface Additive<T> {
    operator fun plus(other: T): T
    operator fun minus(other: T): T
    operator fun times(int: Int): T
}


data class IngredientValue(
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int
) : Additive<IngredientValue>, Comparable<IngredientValue> {
    override operator fun plus(other: IngredientValue) = IngredientValue(
        this.capacity + other.capacity,
        this.durability + other.durability,
        this.flavor + other.flavor,
        this.texture + other.texture
    )

    override fun minus(other: IngredientValue): IngredientValue = IngredientValue(
        this.capacity - other.capacity,
        this.durability - other.durability,
        this.flavor - other.flavor,
        this.texture - other.texture
    )

    override fun times(int: Int) = IngredientValue(
        this.capacity * int,
        this.durability * int,
        this.flavor * int,
        this.texture * int
    )

    val numeric: Int
        get() = capacity.coerceAtLeast(0) * durability.coerceAtLeast(0) *
                flavor.coerceAtLeast(0) * texture.coerceAtLeast(0)

    override fun compareTo(other: IngredientValue): Int = compareValuesBy(this, other) { it.numeric }

}

data class Ingredient(
    val name: String,
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int
)

data class Compound<I, V>(val ingredients: Map<I, Int>, val value: V?)

fun <I, V> getOptimalCompoundBruteForce(
    ingredients: List<I>,
    costF: (I) -> V,
    maxWeight: Int = 100,
    weightF: (I) -> Int = { 1 },
): Compound<I, V>? where V : Additive<V>, V : Comparable<V> =
    getOptimalCompoundHelper(
        ingredients = ingredients,
        costF = costF,
        maxWeight = maxWeight,
        currentCompound = Compound(persistentMapOf(), null),
        weightF = weightF
    )

private fun <I, V> getOptimalCompoundHelper(
    ingredients: List<I>,
    costF: (I) -> V,
    maxWeight: Int = 100,
    currentCompound: Compound<I, V>,
    weightF: (I) -> Int = { 1 },
): Compound<I, V>? where V : Additive<V>, V : Comparable<V> {
    assert(ingredients.isNotEmpty())

    if (ingredients.size == 1) {
        // A little optimisation, we can avoid brute force here
        return getOptimalCompoundLast(
            ingredient = ingredients.first(),
            costF = costF,
            maxWeight = maxWeight,
            currentCompound = currentCompound,
            weightF = weightF
        )
    }

    val head = ingredients.first()
    val headWeight = weightF(head)
    val tail = ingredients.drop(1)

    val steps = maxWeight / headWeight

    var maxSoFar: Compound<I, V>? = null

    for (i in 0 until steps) {
        val compound =
            if (i == 0)
                currentCompound
            else {
                val iCost = costF(head) * i
                Compound(currentCompound.ingredients + (head to i), currentCompound.value?.let {
                    it + iCost
                } ?: iCost)
            }

        val fromIteration = getOptimalCompoundHelper(
            ingredients = tail,
            costF = costF,
            maxWeight = maxWeight - headWeight * i,
            currentCompound = compound,
            weightF = weightF
        )

        val valueFromIt = fromIteration?.value
        val maxSoFarValue = maxSoFar?.value

        maxSoFar = when {
            maxSoFarValue == null -> fromIteration
            valueFromIt as? Comparable<*> != null && valueFromIt > maxSoFarValue -> fromIteration
            else -> maxSoFar
        }
    }

    return maxSoFar
}

fun <I, V> getOptimalCompoundLast(
    ingredient: I,
    costF: (I) -> V,
    maxWeight: Int,
    currentCompound: Compound<I, V>,
    weightF: (I) -> Int = { 1 },
): Compound<I, V>? where V : Additive<V>, V : Comparable<V> {
    val weight = weightF(ingredient)
    val rem = maxWeight % weight
    if (rem != 0) return null

    val n = maxWeight / weight
    val nCost = costF(ingredient) * n

    return Compound(currentCompound.ingredients + (ingredient to n), currentCompound.value?.let {
        it + nCost
    } ?: nCost)
}

object IngredientParser {
    private enum class CharacteristicType {
        CAPACITY, DURABILITY, FLAVOR, TEXTURE, CALORIES
    }

    private data class Characteristic(val type: CharacteristicType, val amount: Int)

    private val ZERO_OR_MORE_WHITESPACES = Scanners.isChar(CharPredicates.IS_WHITESPACE).skipMany()

    private val CAPACITY = Scanners.string("capacity").map {
        CharacteristicType.CAPACITY
    }

    private val DURABILITY = Scanners.string("durability").map {
        CharacteristicType.DURABILITY
    }

    private val FLAVOR = Scanners.string("flavor").map {
        CharacteristicType.FLAVOR
    }

    private val TEXTURE = Scanners.string("texture").map {
        CharacteristicType.TEXTURE
    }

    private val CALORIES = Scanners.string("calories").map {
        CharacteristicType.CALORIES
    }

    private val CHARACTERISTIC_TYPE_PARSER = Parsers.or(
        CAPACITY,
        DURABILITY,
        FLAVOR,
        TEXTURE,
        CALORIES
    )

    private val POSITIVE_NUMBER = Terminals.IntegerLiteral.TOKENIZER.map {
        it.text().toInt()
    }

    private val NEGATIVE_INTEGER = Parsers.sequence(
        Scanners.isChar('-'),
        Terminals.IntegerLiteral.TOKENIZER
    ) { _, int ->
        -int.text().toInt()
    }

    private val NUMBER = Parsers.or(
        POSITIVE_NUMBER,
        NEGATIVE_INTEGER
    )

    private val CHARACTERISTIC = Parsers.sequence(
        CHARACTERISTIC_TYPE_PARSER,
        Scanners.WHITESPACES,
        NUMBER
    ) { type, _, amount ->
        Characteristic(type, amount)
    }

    private val ALL_CHARACTERISTICS_PARSER = CHARACTERISTIC.sepBy(
        Parsers.sequence(
            ZERO_OR_MORE_WHITESPACES,
            Scanners.isChar(','),
            ZERO_OR_MORE_WHITESPACES,
        )
    ).map { list ->
        list.associateBy { it.type }
    }

    private val INGREDIENT = Parsers.sequence(
        Terminals.Identifier.TOKENIZER,
        ZERO_OR_MORE_WHITESPACES,
        Scanners.isChar(':'),
        ZERO_OR_MORE_WHITESPACES,
        ALL_CHARACTERISTICS_PARSER
    ) { name, _, _, _, chars ->
        Ingredient(
            name = name.text(),
            capacity = chars[CharacteristicType.CAPACITY]?.amount ?: 0,
            durability = chars[CharacteristicType.DURABILITY]?.amount ?: 0,
            flavor = chars[CharacteristicType.FLAVOR]?.amount ?: 0,
            texture = chars[CharacteristicType.TEXTURE]?.amount ?: 0,
            calories = chars[CharacteristicType.CALORIES]?.amount ?: 0,
        )
    }

    fun parse(str: String) = INGREDIENT.parse(str)
}

fun getOptimalCompoundValue(strings: Sequence<String>) =
    getOptimalCompoundBruteForce(
        strings.map { IngredientParser.parse(it) }.toList(),
        { IngredientValue(it.capacity, it.durability, it.flavor, it.texture) }
    )?.value?.numeric

fun day15Part1() {
    println("day15/part1/cookies: ${
        getOptimalCompoundValue(getResourceAsStream("/ski.gagar.aoc.aoc2015.day15/cookies.txt").bufferedReader().lineSequence())
    }")
}
