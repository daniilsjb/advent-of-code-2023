package day11

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
    val data = parse("src/day11/Day11.txt")

    println("🎄 Day 11 🎄")

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

private fun solve(data: List<List<Char>>, scale: Long): Long {
    val emptyRows = data.indices
        .filter { row -> data[row].all { it == '.' } }

    val emptyCols = data[0].indices
        .filter { col -> data.all { it[col] == '.' } }

    val galaxies = mutableListOf<Pair<Int, Int>>()
    for ((y, row) in data.withIndex()) {
        for ((x, col) in row.withIndex()) {
            if (col == '#') {
                galaxies.add(x to y)
            }
        }
    }

    var accumulator = 0L
    for (a in 0..galaxies.lastIndex) {
        for (b in (a + 1)..galaxies.lastIndex) {
            val (ax, ay) = galaxies[a]
            val (bx, by) = galaxies[b]

            val x0 = min(ax, bx)
            val x1 = max(ax, bx)

            val y0 = min(ay, by)
            val y1 = max(ay, by)

            val nx = emptyCols.count { it in x0..x1 }
            val ny = emptyRows.count { it in y0..y1 }

            accumulator += (x1 - x0) + nx * (scale - 1)
            accumulator += (y1 - y0) + ny * (scale - 1)
        }
    }

    return accumulator
}

private fun part1(data: List<List<Char>>): Long =
    solve(data, scale = 2L)

private fun part2(data: List<List<Char>>): Long =
    solve(data, scale = 1_000_000L)
