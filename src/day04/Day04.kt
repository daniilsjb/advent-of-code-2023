package day04

import java.io.File
import kotlin.math.pow

fun main() {
    val data = parse("src/day04/Day04.txt")

    println("🎄 Day 04 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private fun String.toMatches(): Int {
    val (_, winningPart, playerPart) = split(": ", " | ")

    val winningNumbers = winningPart
        .split(" ")
        .mapNotNull(String::toIntOrNull)
        .toSet()

    val playerNumbers = playerPart
        .split(" ")
        .mapNotNull(String::toIntOrNull)

    return playerNumbers.count { it in winningNumbers }
}

private fun parse(path: String): List<Int> =
    File(path)
        .readLines()
        .map(String::toMatches)

private fun part1(data: List<Int>): Int =
    data.sumOf { 2.0.pow(it).toInt() / 2 }

private fun part2(data: List<Int>): Int {
    val pile = MutableList(data.size) { 1 }
    for ((i, matches) in data.withIndex()) {
        for (offset in 1..matches) {
            pile[i + offset] += pile[i]
        }
    }
    return pile.sum()
}
