package day01

import java.io.File

fun main() {
    val data = parse("src/day01/Day01.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 01 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private fun parse(path: String): List<String> =
    File(path).readLines()

private fun part1(data: List<String>): Int =
    data.map { it.mapNotNull(Char::digitToIntOrNull) }
        .sumOf { it.first() * 10 + it.last() }

val spelledDigits = mapOf(
    "one"   to 1,
    "two"   to 2,
    "three" to 3,
    "four"  to 4,
    "five"  to 5,
    "six"   to 6,
    "seven" to 7,
    "eight" to 8,
    "nine"  to 9,
)

private fun String.parseDigits(): List<Int> {
    val digits = mutableListOf<Int>()

    scan@ for ((i, c) in this.withIndex()) {
        // Ordinary digits may be parsed directly.
        if (c.isDigit()) {
            digits.add(c.digitToInt())
            continue@scan
        }

        // Spelled out digits must be matched individually.
        for ((spelling, value) in spelledDigits) {
            val endIndex = i + spelling.length
            if (this.length >= endIndex) {
                if (this.substring(i, endIndex) == spelling) {
                    digits.add(value)
                    continue@scan
                }
            }
        }
    }

    return digits
}


private fun part2(data: List<String>): Int =
    data.map { it.parseDigits() }
        .sumOf { it.first() * 10 + it.last() }
