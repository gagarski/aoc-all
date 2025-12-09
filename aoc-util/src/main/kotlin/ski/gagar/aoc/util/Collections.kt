package ski.gagar.aoc.util

import java.util.*

fun <K : Comparable<K>, V> Map<out K, V>.toNavigableMap(): NavigableMap<K, V> = TreeMap(this)

fun <T : Comparable<T>> Sequence<T>.toNavigableSet(): NavigableSet<T> =
    toCollection(TreeSet())

fun <T> List<T>.eachPair(): Sequence<Pair<T, T>> =
    this.asSequence().flatMap { first ->
        this.asSequence().map { second ->
            Pair(first, second)
        }
    }

fun <T> List<T>.eachPairNoRepeats(): Sequence<Pair<T, T>> =
    this.asSequence().withIndex().flatMap { (firstIndex, first) ->
        this.asSequence().withIndex().filter { (secondIndex, _) ->
            secondIndex != firstIndex
        }.map { (secondIndex, second) ->
            Pair(first, second)
        }
    }

fun <T> List<T>.eachPairNoSwaps(): Sequence<Pair<T, T>> =
    this.asSequence().withIndex().flatMap { (firstIndex, first) ->
        this.asSequence()
            .drop(firstIndex + 1)
            .map { second ->
                Pair(first, second)
            }
    }
