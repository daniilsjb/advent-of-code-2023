package day23

import java.io.File
import kotlin.math.max

fun main() {
    val data = parse("src/day23/Day23.txt")

    println("🎄 Day 23 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private fun parse(path: String): List<List<Char>> =
    File(path)
        .readLines()
        .map(String::toList)

private val directions = mapOf(
    '.' to listOf((1 to 0), (-1 to 0), (0 to 1), (0 to -1)),
    '>' to listOf((1 to 0)),
    '<' to listOf((-1 to 0)),
    'v' to listOf((0 to 1)),
    '^' to listOf((0 to -1)),
)

private data class Vertex(
    val x: Int,
    val y: Int,
)

private fun dfs(
    graph: Map<Vertex,Map<Vertex, Int>>,
    source: Vertex,
    target: Vertex,
    visited: Set<Vertex> = setOf(),
): Int {
    if (source == target) {
        return 0
    }

    var distance = Int.MIN_VALUE
    for ((adjacent, weight) in graph.getValue(source)) {
        if (adjacent !in visited) {
            distance = max(distance, weight + dfs(graph, adjacent, target, visited + adjacent))
        }
    }

    return distance
}

private fun solve(grid: List<List<Char>>): Int {
    val sz = grid.size

    val source = Vertex(1, 0)
    val target = Vertex(sz - 2, sz - 1)

    val vertices = mutableListOf(source, target)
    for ((y, row) in grid.withIndex()) {
        for ((x, col) in row.withIndex()) {
            if (col == '#') {
                continue
            }

            val neighbors = listOfNotNull(
                grid.getOrNull(y)?.getOrNull(x - 1),
                grid.getOrNull(y)?.getOrNull(x + 1),
                grid.getOrNull(y - 1)?.getOrNull(x),
                grid.getOrNull(y + 1)?.getOrNull(x),
            )

            if (neighbors.count { it == '#' } <= 1) {
                vertices += Vertex(x, y)
            }
        }
    }

    val graph = vertices.associateWith { mutableMapOf<Vertex, Int>() }
    for (origin in vertices) {
        val options = ArrayDeque<Pair<Vertex, Int>>().apply { add(origin to 0) }
        val visited = mutableSetOf(origin)

        while (options.isNotEmpty()) {
            val (vertex, n) = options.removeFirst()
            val (vx, vy) = vertex

            if (n != 0 && vertex in vertices) {
                graph.getValue(origin)[vertex] = n
                continue
            }

            for ((dx, dy) in directions.getValue(grid[vy][vx])) {
                val nx = vx + dx
                val ny = vy + dy

                if (nx !in 0..<sz || ny !in 0..<sz) {
                    continue
                }
                if (grid[ny][nx] == '#') {
                    continue
                }
                if (Vertex(nx, ny) in visited) {
                    continue
                }

                options += Vertex(nx, ny) to (n + 1)
                visited += Vertex(nx, ny)
            }
        }
    }

    return dfs(graph, source, target)
}

private fun part1(data: List<List<Char>>): Int =
    solve(data)

private fun part2(data: List<List<Char>>): Int =
    solve(data.map { it.map { c -> if (c in "<^v>") '.' else c } })
