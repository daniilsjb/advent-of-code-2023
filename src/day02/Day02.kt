package day02

import java.io.File

fun main() {
    val data = parse("src/day02/Day02.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 02 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class CubeSet(
    val redCount: Int,
    val blueCount: Int,
    val greenCount: Int,
)

private data class Game(
    val id: Int,
    val sets: List<CubeSet>,
)

private val PATTERN = """(\d+) (red|blue|green)""".toRegex()

private fun String.toCubeSet(): CubeSet {
    val colors = PATTERN.findAll(this)
        .map { it.destructured }
        .associate { (count, color) -> color to count.toInt() }

    return CubeSet(
        colors.getOrDefault("red", 0),
        colors.getOrDefault("blue", 0),
        colors.getOrDefault("green", 0),
    )
}

private fun String.toGame(): Game {
    val parts = this
        .split(";", ":")
        .map(String::trim)

    val id = parts.first()
        .filter(Char::isDigit)
        .toInt()

    val sets = parts
        .drop(1)
        .map(String::toCubeSet)

    return Game(id, sets)
}

private fun parse(path: String): List<Game> =
    File(path)
        .readLines()
        .map(String::toGame)

private fun part1(data: List<Game>): Int =
    data.asSequence()
        .filter { (_, sets) -> sets.all { it.redCount <= 12 } }
        .filter { (_, sets) -> sets.all { it.blueCount <= 14 } }
        .filter { (_, sets) -> sets.all { it.greenCount <= 13 } }
        .sumOf(Game::id)

private fun part2(data: List<Game>): Int =
    data.sumOf { (_, sets) ->
        sets.maxOf(CubeSet::redCount) *
            sets.maxOf(CubeSet::blueCount) *
            sets.maxOf(CubeSet::greenCount)
    }
