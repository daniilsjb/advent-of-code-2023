package day12

import java.io.File

fun main() {
    val data = parse("src/day12/Day12.txt")

    println("🎄 Day 12 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private data class Row(
    val springs: String,
    val numbers: List<Int>,
)

private fun String.toRow(): Row {
    val (springs, numbers) = this.split(" ")
    return Row(springs, numbers.split(",").map(String::toInt))
}

private fun parse(path: String): List<Row> =
    File(path)
        .readLines()
        .map(String::toRow)

private typealias Cache = MutableMap<Pair<String, List<Int>>, Long>

private fun solve(springs: String, numbers: List<Int>, cache: Cache = mutableMapOf()): Long {
    if (cache.containsKey(springs to numbers)) {
        return cache.getValue(springs to numbers)
    }

    if (springs.isEmpty()) {
        return if (numbers.isEmpty()) 1L else 0L
    }
    if (numbers.isEmpty()) {
        return if (springs.contains('#')) 0L else 1L
    }

    var count = 0L
    if (springs.first() in ".?") {
        count += solve(springs.substring(1), numbers, cache)
    }
    if (springs.first() in "#?") {
        val n = numbers.first()
        if (springs.length > n) {
            val group = springs.substring(0, n)
            if (!group.contains('.') && springs[n] != '#') {
                count += solve(springs.substring(n + 1), numbers.drop(1), cache)
            }
        }

        if (springs.length == n) {
            if (!springs.contains('.')) {
                count += solve("", numbers.drop(1), cache)
            }
        }
    }

    cache[springs to numbers] = count
    return count
}

private fun part1(data: List<Row>): Long =
    data.sumOf { (springs, numbers) -> solve(springs, numbers) }

private fun part2(data: List<Row>): Long =
    data.sumOf { (springs, numbers) -> solve(
        generateSequence { springs }.take(5).toList().joinToString("?"),
        generateSequence { numbers }.take(5).toList().flatten())
    }
