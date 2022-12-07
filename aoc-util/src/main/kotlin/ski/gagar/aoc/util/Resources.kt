package ski.gagar.aoc.util

private class Resources

fun getResourceAsStream(path: String) = Resources::class.java.getResourceAsStream(path)!!
