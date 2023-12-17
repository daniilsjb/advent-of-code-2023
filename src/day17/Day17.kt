package day17

import java.io.File
import java.util.PriorityQueue

fun main() {
    val data = parse("src/day17/Day17.txt")

    println("🎄 Day 17 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private typealias Graph = List<List<Int>>

private fun parse(path: String): Graph =
    File(path)
        .readLines()
        .map { it.map(Char::digitToInt) }

private data class Vec2(
    val x: Int,
    val y: Int,
)

private typealias Vertex = Vec2

private data class Endpoint(
    val vertex: Vertex,
    val streakDirection: Vec2,
    val streakLength: Int,
)

private data class Path(
    val endpoint: Endpoint,
    val distance: Int,
) : Comparable<Path> {

    override fun compareTo(other: Path): Int =
        distance.compareTo(other.distance)
}

private val Graph.h get() = this.size
private val Graph.w get() = this[0].size

private fun Graph.neighborsOf(vertex: Vertex): List<Vertex> =
    listOfNotNull(
        if (vertex.x - 1 >= 0) Vertex(vertex.x - 1, vertex.y) else null,
        if (vertex.x + 1 <  w) Vertex(vertex.x + 1, vertex.y) else null,
        if (vertex.y - 1 >= 0) Vertex(vertex.x, vertex.y - 1) else null,
        if (vertex.y + 1 <  h) Vertex(vertex.x, vertex.y + 1) else null,
    )

private fun part1(graph: Graph): Int {
    val visited = mutableSetOf<Endpoint>()

    val (xs, xt) = (0 to graph.w - 1)
    val (ys, yt) = (0 to graph.h - 1)

    val source = Vertex(xs, ys)
    val target = Vertex(xt, yt)

    val sourceEndpoint = Endpoint(source, Vec2(0, 0), streakLength = 0)
    val queue = PriorityQueue<Path>()
        .apply { add(Path(sourceEndpoint, distance = 0)) }

    while (queue.isNotEmpty()) {
        val (endpoint, distanceToVertex) = queue.poll()
        if (endpoint in visited) {
            continue
        } else {
            visited += endpoint
        }

        val (vertex, streakDirection, streakLength) = endpoint
        if (vertex == target) {
            return distanceToVertex
        }

        for (neighbor in graph.neighborsOf(vertex)) {
            val dx = neighbor.x - vertex.x
            val dy = neighbor.y - vertex.y

            val nextDirection = Vec2(dx, dy)
            if (nextDirection.x == -streakDirection.x && nextDirection.y == -streakDirection.y) {
                continue
            }

            val nextLength = if (nextDirection == streakDirection) streakLength + 1 else 1
            if (nextLength == 4) {
                continue
            }

            val endpointToNeighbor = Endpoint(neighbor, nextDirection, nextLength)
            val distanceToNeighbor = distanceToVertex + graph[neighbor.y][neighbor.x]
            queue += Path(endpointToNeighbor, distanceToNeighbor)
        }
    }

    error("Could not find any path from source to target.")
}

private fun part2(graph: Graph): Int {
    val visited = mutableSetOf<Endpoint>()

    val (xs, xt) = (0 to graph.w - 1)
    val (ys, yt) = (0 to graph.h - 1)

    val source = Vertex(xs, ys)
    val target = Vertex(xt, yt)

    val sourceEndpoint = Endpoint(source, Vec2(0, 0), streakLength = 0)
    val queue = PriorityQueue<Path>()
        .apply { add(Path(sourceEndpoint, distance = 0)) }

    while (queue.isNotEmpty()) {
        val (endpoint, distanceToVertex) = queue.poll()
        if (endpoint in visited) {
            continue
        } else {
            visited += endpoint
        }

        val (vertex, streakDirection, streakLength) = endpoint
        if (vertex == target && streakLength >= 4) {
            return distanceToVertex
        }

        for (neighbor in graph.neighborsOf(vertex)) {
            val dx = neighbor.x - vertex.x
            val dy = neighbor.y - vertex.y

            val nextDirection = Vec2(dx, dy)
            if (nextDirection.x == -streakDirection.x && nextDirection.y == -streakDirection.y) {
                continue
            }

            val nextLength = if (nextDirection == streakDirection) streakLength + 1 else 1
            if (nextLength > 10) {
                continue
            }
            if (streakDirection != Vec2(0, 0) && nextDirection != streakDirection && streakLength < 4) {
                continue
            }

            val endpointToNeighbor = Endpoint(neighbor, nextDirection, nextLength)
            val distanceToNeighbor = distanceToVertex + graph[neighbor.y][neighbor.x]
            queue += Path(endpointToNeighbor, distanceToNeighbor)
        }
    }

    error("Could not find any path from source to target.")
}
