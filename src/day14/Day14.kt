package day14

import java.io.File

fun main() {
    val data = parse("src/day14/Day14.txt")

    println("🎄 Day 14 🎄")

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

private fun part1(data: List<List<Char>>): Int {
    val h = data.size
    val w = data[0].size

    var counter = 0
    for (col in 0..<w) {
        var top = 0
        for (row in 0..<h) {
            when (data[row][col]) {
                'O' -> counter += h - top++
                '#' -> top = row + 1
            }
        }
    }

    return counter
}

private fun part2(data: List<List<Char>>): Int {
    val h = data.size
    val w = data[0].size

    val cycles = mutableMapOf<String, Int>()
    val loads = mutableListOf<Int>()

    val grid = data.map { it.toMutableList() }
    for (i in 1..1_000_000_000) {
        // North
        for (col in 0..<w) {
            var dst = 0
            for (row in 0..<h) {
                when (grid[row][col]) {
                    '#' -> dst = row + 1
                    'O' -> {
                        grid[row][col] = '.'
                        grid[dst][col] = 'O'
                        dst += 1
                    }
                }
            }
        }

        // West
        for (row in 0..<h) {
            var dst = 0
            for (col in 0..<w) {
                when (grid[row][col]) {
                    '#' -> dst = col + 1
                    'O' -> {
                        grid[row][col] = '.'
                        grid[row][dst] = 'O'
                        dst += 1
                    }
                }
            }
        }

        // South
        for (col in w - 1 downTo 0) {
            var dst = h - 1
            for (row in h - 1 downTo 0) {
                when (grid[row][col]) {
                    '#' -> dst = row - 1
                    'O' -> {
                        grid[row][col] = '.'
                        grid[dst][col] = 'O'
                        dst -= 1
                    }
                }
            }
        }

        // East
        for (row in h - 1 downTo 0) {
            var dst = w - 1
            for (col in w - 1 downTo 0) {
                when (grid[row][col]) {
                    '#' -> dst = col - 1
                    'O' -> {
                        grid[row][col] = '.'
                        grid[row][dst] = 'O'
                        dst -= 1
                    }
                }
            }
        }

        val cycle = grid.flatten().toString()
        cycles[cycle]?.let { start ->
            val remainder = (1_000_000_000 - start) % (i - start)
            val loop = cycles.values.filter { it >= start }
            return loads[loop[remainder] - 1]
        }

        cycles[cycle] = i
        loads += grid.withIndex().sumOf { (y, row) ->
            row.count { it == 'O' } * (h - y)
        }
    }

    error("Could not detect a cycle.")
}
