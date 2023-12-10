package ski.gagar.aoc.util

private const val PADDING = 10
private const val SPACES = 2


interface Contest {
    val puzzles: List<Puzzle>
        get() = listOf()

    fun run() {
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
            puzzle.run()
        }
    }
}