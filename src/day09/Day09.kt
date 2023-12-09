package day09

import java.io.File

fun main() {
    val data = parse("src/day09/Day09.txt")

    println("🎄 Day 09 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private fun parse(path: String): List<List<Int>> =
    File(path)
        .readLines()
        .map { it.split(" ").map(String::toInt) }

private fun part1(data: List<List<Int>>): Int =
    data.sumOf { history ->
        generateSequence(history) { it.zipWithNext { a, b -> b - a } }
            .takeWhile { !it.all { diff -> diff == 0 } }
            .sumOf { it.last() }
    }

private fun part2(data: List<List<Int>>): Int =
    data.sumOf { history ->
        generateSequence(history) { it.zipWithNext { a, b -> b - a } }
            .takeWhile { !it.all { diff -> diff == 0 } }
            .toList()
            .foldRight(0) { it, acc -> it.first() - acc }.toInt()
    }
