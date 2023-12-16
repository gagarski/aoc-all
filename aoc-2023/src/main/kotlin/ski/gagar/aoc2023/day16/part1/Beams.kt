package ski.gagar.aoc2023.day16.part1

data class Coordinates(val x: Int, val y: Int) {
    val left
        get() = Coordinates(x - 1, y)
    val right
        get() = Coordinates(x + 1, y)
    val up
        get() = Coordinates(x, y - 1)
    val down
        get() = Coordinates(x, y + 1)
}

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT
}

data class BeamPosition(val coordinates: Coordinates, val direction: Direction) {
    val straightAhead
        get() = when (direction) {
            Direction.UP -> BeamPosition(coordinates.up, Direction.UP)
            Direction.LEFT -> BeamPosition(coordinates.left, Direction.LEFT)
            Direction.DOWN -> BeamPosition(coordinates.down, Direction.DOWN)
            Direction.RIGHT -> BeamPosition(coordinates.right, Direction.RIGHT)
        }

    val turnRight
        get() = when (direction) {
            Direction.UP -> BeamPosition(coordinates.right, Direction.RIGHT)
            Direction.LEFT -> BeamPosition(coordinates.up, Direction.UP)
            Direction.DOWN -> BeamPosition(coordinates.left, Direction.LEFT)
            Direction.RIGHT -> BeamPosition(coordinates.down, Direction.DOWN)
        }

    val turnLeft
        get() = when (direction) {
            Direction.UP -> BeamPosition(coordinates.left, Direction.LEFT)
            Direction.LEFT -> BeamPosition(coordinates.down, Direction.DOWN)
            Direction.DOWN -> BeamPosition(coordinates.right, Direction.RIGHT)
            Direction.RIGHT -> BeamPosition(coordinates.up, Direction.UP)
        }
}

enum class BeamItem(private val char: Char) {
    MIRROR_BL_TR('/') {
        override fun pass(beamPosition: BeamPosition) = sequence {
            yield(when (beamPosition.direction) {
                Direction.UP, Direction.DOWN -> beamPosition.turnRight
                Direction.LEFT, Direction.RIGHT -> beamPosition.turnLeft
            })
        }
    },
    MIRROR_TL_BR('\\') {
        override fun pass(beamPosition: BeamPosition) = sequence {
            yield(when (beamPosition.direction) {
                Direction.UP, Direction.DOWN -> beamPosition.turnLeft
                Direction.LEFT, Direction.RIGHT -> beamPosition.turnRight
            })
        }
    },
    SPLITTER_H('-') {
        override fun pass(beamPosition: BeamPosition): Sequence<BeamPosition> = sequence {
            when (beamPosition.direction) {
                Direction.UP, Direction.DOWN -> {
                    yield(beamPosition.turnLeft)
                    yield(beamPosition.turnRight)
                }
                Direction.LEFT, Direction.RIGHT -> yield(beamPosition.straightAhead)
            }
        }
    },
    SPLITTER_V('|') {
        override fun pass(beamPosition: BeamPosition): Sequence<BeamPosition> = sequence {
            when (beamPosition.direction) {
                Direction.UP, Direction.DOWN -> yield(beamPosition.straightAhead)
                Direction.LEFT, Direction.RIGHT -> {
                    yield(beamPosition.turnLeft)
                    yield(beamPosition.turnRight)
                }
            }
        }
    },
    NOTHING('.') {
        override fun pass(beamPosition: BeamPosition): Sequence<BeamPosition> =
            sequenceOf(beamPosition.straightAhead)
    };

    abstract fun pass(beamPosition: BeamPosition): Sequence<BeamPosition>

    companion object {
        private val byChar = entries.associateBy { it.char }
        fun from(char: Char) =
            byChar[char] ?: throw IllegalArgumentException("Unknown beam item $char")
    }
}

class BeamField(items: Map<Coordinates, BeamItem>, val width: Int, val height: Int) {
    val items = items.filter { it.value != BeamItem.NOTHING }


    private fun BeamPosition.isInside() =
        coordinates.x in 0 until width && coordinates.y in 0 until height

    private fun Set<BeamPosition>.beamsStep(visited: MutableSet<BeamPosition>): Set<BeamPosition> {
        visited.addAll(this)

        val newPositions = mutableSetOf<BeamPosition>()

        for (beam in this) {
            val itemUnder = items[beam.coordinates] ?: BeamItem.NOTHING

            newPositions.addAll(
                itemUnder.pass(beam).filter {
                    it.isInside() && it !in visited
                }
            )
        }

        return newPositions
    }

    private fun beamPositions(start: BeamPosition = BeamPosition(Coordinates(0, 0), Direction.RIGHT)): Set<BeamPosition> {
        var positions = setOf(start)
        val visited = mutableSetOf<BeamPosition>()

        while (positions.isNotEmpty()) {
            positions = positions.beamsStep(visited)
        }

        return visited
    }

    fun getEnergized(start: BeamPosition = BeamPosition(Coordinates(0, 0), Direction.RIGHT)) =
        beamPositions(start).asSequence().map { it.coordinates }.toSet()

    companion object {
        fun from(sequence: Sequence<String>): BeamField {
            var w: Int? = null
            var h: Int = 0
            val items = mutableMapOf<Coordinates, BeamItem>()

            for ((y, line) in sequence.withIndex()) {
                require(w == null || w == line.length)
                w = line.length

                for ((x, char) in line.withIndex()) {
                    val item = BeamItem.from(char)
                    if (BeamItem.NOTHING == item) continue
                    items[Coordinates(x, y)] = item
                }
                h++
            }

            require(h != 0)
            check(w != null)

            return BeamField(items, w, h)
        }
    }
}

fun getEnergized(lines: Sequence<String>) =
    BeamField.from(lines).getEnergized().size