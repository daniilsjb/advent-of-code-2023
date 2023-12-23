package day21

import java.io.File

fun main() {
    val data = parse("src/day21/Day21.txt")

    println("🎄 Day 21 🎄")

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

private fun solve(data: List<List<Char>>, start: Pair<Int, Int>, n: Int): Long {
    var plots = setOf(start)
    repeat(n) {
        val next = mutableSetOf<Pair<Int, Int>>()
        for ((px, py) in plots) {
            for ((dx, dy) in listOf((-1 to 0), (1 to 0), (0 to -1), (0 to 1))) {
                val nx = px + dx
                val ny = py + dy
                if (nx in data.indices && ny in data.indices) {
                    if (data[ny][nx] != '#') {
                        next += nx to ny
                    }
                }
            }
        }
        plots = next
    }

    return plots.size.toLong()
}

private fun part1(data: List<List<Char>>): Long {
    for ((y, row) in data.withIndex()) {
        for ((x, col) in row.withIndex()) {
            if (col == 'S') {
                return solve(data, Pair(x, y), 64)
            }
        }
    }

    error("Starting point not found.")
}

private fun part2(data: List<List<Char>>): Long {
    val size = data.size
    val steps = 26_501_365

    val sx = size / 2
    val sy = size / 2

    val n = (steps / size - 1).toLong()

    val evenGrids = n * n
    val evenCount = evenGrids * solve(data, start = Pair(sx, sy), n = 2 * size + 1)

    val oddGrids = (n + 1) * (n + 1)
    val oddCount =  oddGrids * solve(data, start = Pair(sx, sy), n = 2 * size)

    val corners =
        solve(data, start = Pair(sx, size - 1), n = size - 1) +  // Top
        solve(data, start = Pair(size - 1, sy), n = size - 1) +  // Left
        solve(data, start = Pair(sx, 0), n = size - 1) +         // Bottom
        solve(data, start = Pair(0, sy), n = size - 1)           // Right

    val smallSections =
        solve(data, start = Pair(size - 1, size - 1), n = size / 2 - 1) + // Top-left
        solve(data, start = Pair(size - 1, 0), n = size / 2 - 1) +        // Bottom-left
        solve(data, start = Pair(0, 0), n = size / 2 - 1) +               // Bottom-right
        solve(data, start = Pair(0, size - 1), n = size / 2 - 1)          // Top-right

    val largeSections =
        solve(data, start = Pair(size - 1, size - 1), n = size * 3 / 2 - 1) + // Top-left
        solve(data, start = Pair(size - 1, 0), n = size * 3 / 2 - 1) +        // Bottom-left
        solve(data, start = Pair(0, 0), n = size * 3 / 2 - 1) +               // Bottom-right
        solve(data, start = Pair(0, size - 1), n = size * 3 / 2 - 1)          // Top-right

    return evenCount + oddCount + corners + (n + 1) * smallSections + n * largeSections
}
