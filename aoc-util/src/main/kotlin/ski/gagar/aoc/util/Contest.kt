package ski.gagar.aoc.util

interface Contest {
    val puzzles: List<Puzzle>
        get() = listOf()

    fun run() {
        println("======================")
        println(this.javaClass.simpleName)
        println("======================")

        for (puzzle in puzzles) {
            puzzle.run()
        }
    }
}