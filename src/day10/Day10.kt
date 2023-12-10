package day10

import java.io.File

fun main() {
    val data = parse("src/day10/Day10.txt")

    println("🎄 Day 10 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private data class Vec2(
    val x: Int,
    val y: Int,
)

private typealias Point = Vec2

private fun List<String>.atOrNull(p: Point): Char? {
    return this.getOrNull(p.y)?.getOrNull(p.x)
}

private fun List<String>.at(p: Point): Char {
    return this.atOrNull(p) ?: error("Point outside the boundaries.")
}

private operator fun Vec2.plus(other: Vec2): Vec2 =
    other.let { (dx, dy) -> Vec2(x + dx, y + dy) }

private fun Point.neighbors(): List<Point> =
    (-1..1).flatMap { dy ->
        (-1..1).map { dx ->
            Point(x + dx, y + dy)
        }
    }

private val offsetN = Vec2(x = +0, y = -1)
private val offsetS = Vec2(x = +0, y = +1)
private val offsetE = Vec2(x = +1, y = +0)
private val offsetW = Vec2(x = -1, y = +0)

private val connectorOffsets = mapOf(
    '|' to listOf(offsetN, offsetS),
    '-' to listOf(offsetE, offsetW),
    'L' to listOf(offsetN, offsetE),
    'J' to listOf(offsetN, offsetW),
    '7' to listOf(offsetS, offsetW),
    'F' to listOf(offsetS, offsetE),
)

private data class Maze(
    val loop: Map<Point, Char>,
    val size: Vec2,
)

private fun parse(path: String): Maze {
    // Load the puzzle input.
    val data = File(path).readLines()

    // Find the position of the starting tile.
    var start = Vec2(0, 0)
    for ((y, row) in data.withIndex()) {
        for ((x, col) in row.withIndex()) {
            if (col == 'S') {
                start = Point(x, y)
            }
        }
    }

    // Find any pipe that is connected to the starting tile.
    var prev = start
    var curr = when {
        data.atOrNull(start + offsetN) in listOf('|', 'F', '7') -> start + offsetN
        data.atOrNull(start + offsetS) in listOf('|', 'L', 'J') -> start + offsetS
        data.atOrNull(start + offsetE) in listOf('-', 'J', '7') -> start + offsetE
        data.atOrNull(start + offsetW) in listOf('-', 'F', 'L') -> start + offsetW
        else -> error("Malformed Puzzle: S is not connected to any pipes.")
    }

    // The starting tile and its connector will be part of the loop.
    val loop = mutableMapOf(
        prev to data.at(prev),
        curr to data.at(curr),
    )

    // Follow the connectors until the starting tile is reached again.
    while (true) {
        // Out of two possible connections, choose the one we did NOT just come from.
        val pipe = data.at(curr)
        val next = connectorOffsets.getValue(pipe)
            .map { offset -> curr + offset }
            .find { it != prev } ?: error("Encountered a dead end.")

        // This connection will necessarily be a part of the loop.
        loop[next] = data.at(next)

        // If we got back to the start, we've found our loop.
        if (data.at(next) == 'S') {
            return Maze(loop, Vec2(data[0].length, data.size))
        }

        // Otherwise, just keep going.
        prev = curr.also { curr = next }
    }
}

private fun part1(data: Maze): Int {
    val (loop, _) = data
    return loop.size / 2
}

private fun part2(data: Maze): Int {
    val (loop, size) = data

    // Scale up the loop by a factor of 2.
    val scaledSize = Vec2(size.x * 2, size.y * 2)
    val scaledLoop = mutableMapOf<Point, Char>()
    for ((point, pipe) in loop) {
        // Each tile will now occupy a 2x2 area.
        val scaledPoint = Point(point.x * 2, point.y * 2)
        scaledLoop[scaledPoint] = pipe

        // We also need to fill in the created gaps.
        connectorOffsets[pipe]?.forEach { (dx, dy) ->
            val gap = Point(scaledPoint.x + dx, scaledPoint.y + dy)
            scaledLoop[gap] = if (dx != 0) '-' else '|'
        }
    }

    // We'll use flood-fill to keep track of enclosed points.
    val enclosed = mutableSetOf<Point>()
    val visited = mutableSetOf<Point>()
    visited.addAll(scaledLoop.keys)

    // Go through each point we've not visited yet.
    for (y in 0..<scaledSize.y) {
        for (x in 0..<scaledSize.x) {
            val point = Point(x, y)
            if (point in visited) {
                continue
            }

            // We will use flood-fill to find the region that this point is a part of.
            val flooded = mutableSetOf<Point>()

            // We will also need to determine if the region is enclosed by the loop.
            var outside = false

            // Scan outwards until no more points are left in this region.
            val frontier = ArrayDeque<Point>().apply { add(point) }
            while (frontier.isNotEmpty()) {
                // Visit the next point in our queue.
                val next = frontier.removeLast()
                    .also { visited.add(it) }
                    .also { flooded.add(it) }

                // See if any neighboring tiles can be flooded as well.
                val neighbors = next
                    .neighbors()
                    .filter { it !in visited }

                // See if any neighboring tiles can be flooded as well.
                for ((nx, ny) in neighbors) {
                    // If the original tile stood on the edge of the map, it must be outside the loop.
                    if (nx < 0 || ny < 0 || nx >= scaledSize.x || ny >= scaledSize.y) {
                        outside = true
                    } else {
                        frontier.add(Point(nx, ny))
                    }
                }
            }

            // Mark flooded tiles as enclosed straight away; we will not be visiting them again.
            if (!outside) {
                enclosed.addAll(flooded)
            }
        }
    }

    // Count all remaining 2x2 areas of enclosed tiles.
    val counted = mutableSetOf<Point>()
    for (point in enclosed) {
        val p1 = point + Vec2(x = 0, y =  0)
        val p2 = point + Vec2(x = 0, y = -1)
        val p3 = point + Vec2(x = 1, y =  0)
        val p4 = point + Vec2(x = 1, y = -1)

        val area = listOf(p1, p2, p3, p4)
        if (enclosed.containsAll(area)) {
            if (!area.any { it in counted }) {
                counted.addAll(area)
            }
        }
    }

    // Scale down the number of enclosed tiles.
    return counted.size / 4
}
