package ski.gagar.aoc2015.day15.part2

import kotlinx.collections.immutable.persistentMapOf
import ski.gagar.aoc.util.getResourceAsStream
import ski.gagar.aoc2015.day15.part1.Additive
import ski.gagar.aoc2015.day15.part1.Compound
import ski.gagar.aoc2015.day15.part1.IngredientParser
import ski.gagar.aoc2015.day15.part1.IngredientValue

fun <I, V> getOptimalCompoundBruteForce(
    ingredients: List<I>,
    costF: (I) -> V,
    maxWeight: Int = 100,
    weightF: (I) -> Int = { 1 },
    compoundPredicate: (Compound<I, V>) -> Boolean = { true }
): Compound<I, V>? where V : Additive<V>, V : Comparable<V> =
    getOptimalCompoundHelper(
        ingredients = ingredients,
        costF = costF,
        maxWeight = maxWeight,
        currentCompound = Compound(persistentMapOf(), null),
        weightF = weightF,
        compoundPredicate = compoundPredicate
    )

private fun <I, V> getOptimalCompoundHelper(
    ingredients: List<I>,
    costF: (I) -> V,
    maxWeight: Int = 100,
    currentCompound: Compound<I, V>,
    weightF: (I) -> Int = { 1 },
    compoundPredicate: (Compound<I, V>) -> Boolean = { true }
): Compound<I, V>? where V : Additive<V>, V : Comparable<V> {
    assert(ingredients.isNotEmpty())

    if (ingredients.size == 1) {
        // A little optimisation, we can avoid brute force here
        return getOptimalCompoundLast(
            ingredient = ingredients.first(),
            costF = costF,
            maxWeight = maxWeight,
            currentCompound = currentCompound,
            weightF = weightF,
            compoundPredicate = compoundPredicate
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
            weightF = weightF,
            compoundPredicate
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
    compoundPredicate: (Compound<I, V>) -> Boolean = { true }
): Compound<I, V>? where V : Additive<V>, V : Comparable<V> {
    val weight = weightF(ingredient)
    val rem = maxWeight % weight
    if (rem != 0) return null

    val n = maxWeight / weight
    val nCost = costF(ingredient) * n

    val toRet = Compound(currentCompound.ingredients + (ingredient to n), currentCompound.value?.let {
        it + nCost
    } ?: nCost)

    return if (compoundPredicate(toRet)) toRet else null
}

fun getOptimalCompoundValue(strings: Sequence<String>) =
    getOptimalCompoundBruteForce(
        strings.map { IngredientParser.parse(it) }.toList(),
        { IngredientValue(it.capacity, it.durability, it.flavor, it.texture) },
        compoundPredicate = { it.ingredients.map { (k, v) -> k.calories * v }.sum() == 500 }
    )?.value?.numeric

fun day15Part2() {
    println("day15/part2/cookies: ${
        getOptimalCompoundValue(getResourceAsStream("/ski.gagar.aoc.aoc2015.day15/cookies.txt").bufferedReader().lineSequence())
    }")
}
