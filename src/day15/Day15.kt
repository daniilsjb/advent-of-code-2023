package day15

import java.io.File

fun main() {
    val data = parse("src/day15/Day15.txt")

    println("🎄 Day 15 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private fun parse(path: String): List<String> =
    File(path)
        .readText()
        .split(",")

private fun String.hash(): Int =
    fold(0) { acc, it -> (acc + it.code) * 17 and 0xFF }

private fun part1(data: List<String>): Int =
    data.sumOf(String::hash)

private data class Lens(
    val label: String,
    val focalLength: Int,
)

private fun part2(data: List<String>): Int {
    val boxes = Array(256) { mutableListOf<Lens>() }
    for (step in data) {
        val (label) = step.split('=', '-')
        val location = label.hash()

        if (step.contains('=')) {
            val focalLength = step.last().digitToInt()

            val index = boxes[location].indexOfFirst { it.label == label }
            if (index >= 0) {
                boxes[location][index] = Lens(label, focalLength)
            } else {
                boxes[location] += Lens(label, focalLength)
            }
        } else {
            boxes[location].removeIf { it.label == label  }
        }
    }

    return boxes.withIndex().sumOf { (boxNumber, box) ->
        box.withIndex().sumOf { (lensNumber, lens) ->
            (boxNumber + 1) * (lensNumber + 1) * lens.focalLength
        }
    }
}