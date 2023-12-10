package ski.gagar.aoc2023.day10.part2

import ski.gagar.aoc2023.day10.part1.Direction
import ski.gagar.aoc2023.day10.part1.Maze


fun Maze.lhs(node: Maze.Node, dir: Direction): Maze.Node {
    val x = node.x
    val y = node.y

    return when (dir) {
        Direction.TOP -> Node(x - 1, y)
        Direction.BOTTOM -> Node(x + 1, y)
        Direction.LEFT -> Node(x, y + 1)
        Direction.RIGHT -> Node(x, y - 1)
    }
}

fun Maze.rhs(node: Maze.Node, dir: Direction): Maze.Node {
    val x = node.x
    val y = node.y

    return when (dir) {
        Direction.TOP -> Node(x + 1, y)
        Direction.BOTTOM -> Node(x - 1, y)
        Direction.LEFT -> Node(x, y - 1)
        Direction.RIGHT -> Node(x, y + 1)
    }
}

fun Maze.isNodeFree(node: Maze.Node, mainLoop: Set<Maze.Node>): Boolean {
    if (node.x < 0) return false
    if (node.x >= width) return false
    if (node.y < 0) return false
    if (node.y >= height) return false

    return node !in mainLoop
}

fun Maze.fillFreeNodes(start: Maze.Node, mainLoop: Set<Maze.Node>, result: MutableSet<Maze.Node>) {
    val queue = ArrayDeque<Maze.Node>()
    val visited = mutableSetOf<Maze.Node>()

    if (isNodeFree(start, mainLoop) && start !in result) {
        queue.add(start)
        visited.add(start)
    }

    while (queue.isNotEmpty()) {
        val node = queue.removeFirst()
        result.add(node)

        for (neighbor in node.getAdjacent()) {
            if (isNodeFree(neighbor, mainLoop) && neighbor !in visited) {
                queue.add(neighbor)
                visited.add(neighbor)
            }
        }
    }

    return
}

fun Maze.fillLhs(position: Maze.Node, direction: Direction, mainLoop: Set<Maze.Node>, res: MutableSet<Maze.Node>, ) {
    fillFreeNodes(lhs(position, direction), mainLoop, res)
}

fun Maze.fillRhs(position: Maze.Node, direction: Direction, mainLoop: Set<Maze.Node>, res: MutableSet<Maze.Node>) {
    fillFreeNodes(rhs(position, direction), mainLoop, res)
}

fun Maze.isBorderNode(node: Maze.Node) =
    node.x == 0 || node.x == width - 1 || node.y == 0 || node.y == height - 1

fun Maze.Node.directionTo(other: Maze.Node) = when {
    other.x - x == 1 && other.y == y -> Direction.RIGHT
    other.x - x == -1 && other.y == y -> Direction.LEFT
    other.x == x && other.y - y == 1 -> Direction.BOTTOM
    other.x == x && other.y - y == -1 -> Direction.TOP
    else -> throw IllegalArgumentException("Can't get there")
}

fun Maze.mainLoopNodes() = sequence {
    val first = animal
    var current = first
    val firstNeighbors = edges[first]

    require(firstNeighbors != null && firstNeighbors.size <= 2)

    yield(current)

    var prev = current

    current = firstNeighbors.first() // The only time we have a choice in fact

    yield(current)

    while (current != first) {
        val neighbors = edges[current]?.filter { it != prev } ?: throw IllegalStateException("Neighbors not found")
        require(neighbors.size == 1)
        val next = neighbors.first()
        prev = current
        current = next
        yield(current)
    }
}

fun Maze.nEnclosedCells(): Int {
    val mainLoopNodes = mainLoopNodes().toSet()
    val first = animal
    var current = first
    val firstNeighbors = edges[first]

    val rhs = mutableSetOf<Maze.Node>()
    val lhs = mutableSetOf<Maze.Node>()

    require(firstNeighbors != null && firstNeighbors.size <= 2)

    var prev = current

    current = firstNeighbors.first() // The only time we have a choice in fact

    var direction = prev.directionTo(current)

    fillLhs(prev, direction, mainLoopNodes, lhs)
    fillRhs(prev, direction, mainLoopNodes, rhs)

    while (current != first) {
        fillLhs(current, direction, mainLoopNodes, lhs)
        fillRhs(current, direction, mainLoopNodes, rhs)
        val neighbors = edges[current]?.filter { it != prev } ?: throw IllegalStateException("Neighbors not found")
        require(neighbors.size == 1)
        val next = neighbors.first()

        val nextDir = current.directionTo(next)

        if (nextDir != direction) {
            direction = nextDir
            fillLhs(current, direction, mainLoopNodes, lhs)
            fillRhs(current, direction, mainLoopNodes, rhs)
        }

        prev = current
        current = next
    }

    if (lhs.isEmpty())
        return rhs.size

    if (rhs.isEmpty())
        return lhs.size

    if (rhs.any { isBorderNode(it) })
        return lhs.size

    if (lhs.any { isBorderNode(it) })
        return rhs.size

    throw IllegalStateException("Should not happen")
}

fun areaInsideMainLoop(lines: Sequence<String>): Int {
    val maze = Maze(lines)
    return maze.nEnclosedCells()
}