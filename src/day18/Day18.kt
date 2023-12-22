package day18

import java.io.File

fun main() {
    val data = parse("src/day18/Day18.txt")

    println("🎄 Day 18 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

enum class Direction {
    U, D, L, R
}

private data class Instruction(
    val direction: Direction,
    val distance: Int,
)

private fun parse(path: String): List<String> =
    File(path).readLines()

private fun solve(data: List<Instruction>): Long {
    val x = mutableListOf<Long>()
    val y = mutableListOf<Long>()

    var px = 0L
    var py = 0L

    for ((direction, meters) in data) {
        when (direction) {
            Direction.D -> py += meters
            Direction.U -> py -= meters
            Direction.L -> px -= meters
            Direction.R -> px += meters
        }

        x += px
        y += py
    }

    val area = (x.indices).sumOf { i ->
        val prev = if (i - 1 < 0) x.lastIndex else i - 1
        val next = if (i + 1 > x.lastIndex) 0 else i + 1
        x[i] * (y[next] - y[prev])
    } / 2

    val exterior = data.sumOf { it.distance }
    val interior = (area - (exterior / 2) + 1)

    return interior + exterior
}

private fun part1(data: List<String>): Long =
    solve(data.map { line ->
        val (directionPart, distancePart, _) = line.split(" ")
        val direction = when (directionPart) {
            "R" -> Direction.R
            "D" -> Direction.D
            "L" -> Direction.L
            "U" -> Direction.U
            else -> error("Invalid direction!")
        }

        val distance = distancePart.toInt()
        Instruction(direction, distance)
    })

private fun part2(data: List<String>): Long =
    solve(data.map { line ->
        val (_, _, colorPart) = line.split(" ")
        val color = colorPart.trim('(', ')', '#')

        val distance = color
            .substring(0, 5)
            .toInt(radix = 16)

        val direction = when (color.last()) {
            '0' -> Direction.R
            '1' -> Direction.D
            '2' -> Direction.L
            '3' -> Direction.U
            else -> error("Invalid direction!")
        }

        Instruction(direction, distance)
    })
