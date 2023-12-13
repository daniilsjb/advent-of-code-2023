package day13

import java.io.File
import kotlin.math.min

fun main() {
    val data = parse("src/day13/Day13.txt")

    println("🎄 Day 13 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private typealias Pattern = List<List<Char>>

private fun parse(path: String): List<Pattern> =
    File(path)
        .readText()
        .split("\n\n", "\r\n\r\n")
        .map { it.lines().map(String::toList) }

private fun Pattern.row(index: Int): List<Char> =
    this[index]

private fun Pattern.col(index: Int): List<Char> =
    this.indices.map { this[it][index] }

private fun verticalDifference(pattern: Pattern, y: Int): Int {
    val distance = min(y, pattern.size - y)
    return (0..<distance).sumOf { dy ->
        val lineTop = pattern.row(y - dy - 1)
        val lineBottom = pattern.row(y + dy)
        lineTop.zip(lineBottom).count { (a, b) -> a != b }
    }
}

private fun horizontalDifference(pattern: Pattern, x: Int): Int {
    val distance = min(x, pattern[0].size - x)
    return (0..<distance).sumOf { dx ->
        val lineLeft = pattern.col(x - dx - 1)
        val lineRight = pattern.col(x + dx)
        lineLeft.zip(lineRight).count { (a, b) -> a != b }
    }
}

private fun solve(data: List<Pattern>, tolerance: Int): Int {
    return data.sumOf { pattern ->
        val height = pattern.size
        for (y in 1..<height) {
            if (verticalDifference(pattern, y) == tolerance) {
                return@sumOf 100 * y
            }
        }

        val width = pattern[0].size
        for (x in 1..<width) {
            if (horizontalDifference(pattern, x) == tolerance) {
                return@sumOf x
            }
        }

        error("Reflection line could not be found.")
    }
}

private fun part1(data: List<Pattern>): Int =
    solve(data, tolerance = 0)

private fun part2(data: List<Pattern>): Int =
    solve(data, tolerance = 1)
