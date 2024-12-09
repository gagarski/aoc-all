package ski.gagar.aoc.util

import java.nio.file.Path

private const val PADDING = 10
private const val SPACES = 2


interface Contest {
    open val name: String
        get() = this::class.simpleName!!
    val puzzles: List<Puzzle>
        get() = listOf()

    fun run(basePath: Path) {
        val name = this.javaClass.simpleName
        println("=".repeat(PADDING * 2 + name.length))
        println(
            "*".repeat(PADDING - SPACES) +
                    " ".repeat(SPACES) +
                    name +
                    " ".repeat(SPACES) +
                    "*".repeat(PADDING - SPACES)
        )
        println("=".repeat(PADDING * 2 + name.length))

        for (puzzle in puzzles) {
            puzzle.run(basePath, this)
        }
    }
}