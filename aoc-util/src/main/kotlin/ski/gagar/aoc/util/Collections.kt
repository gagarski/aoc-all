package ski.gagar.aoc.util

import java.util.*

fun <K : Comparable<K>, V> Map<out K, V>.toNavigableMap(): NavigableMap<K, V> = TreeMap(this)

fun <T : Comparable<T>> Sequence<T>.toNavigableSet(): NavigableSet<T> =
    toCollection(TreeSet())