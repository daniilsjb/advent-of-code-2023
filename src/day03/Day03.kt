package day03

import java.io.File

fun main() {
    val data = parse("src/day03/Day03.txt")

    println("🎄 Day 03 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private data class Part(
    val rx: IntRange,
    val ry: IntRange,
    val value: Int,
)

private data class Symbol(
    val x: Int,
    val y: Int,
    val value: Char,
)

private data class Schematic(
    val parts: List<Part>,
    val symbols: List<Symbol>,
)

private val PATTERN = """(\d+)|[^.]""".toRegex()

private fun parse(path: String): Schematic {
    val parts = mutableListOf<Part>()
    val symbols = mutableListOf<Symbol>()

    val data = File(path).readLines()
    for ((y, line) in data.withIndex()) {
        val (matchedParts, matchedSymbols) = PATTERN.findAll(line)
            .partition { it.value[0].isDigit() }

        parts.addAll(matchedParts.map {
            val a = it.range.first
            val b = it.range.last

            val rx = (a - 1)..(b + 1)
            val ry = (y - 1)..(y + 1)

            Part(rx, ry, it.value.toInt())
        })

        symbols.addAll(matchedSymbols.map {
            Symbol(x = it.range.first, y, it.value[0])
        })
    }

    return Schematic(parts, symbols)
}

private fun part1(data: Schematic): Int {
    val (parts, symbols) = data
    return parts.sumOf { (rx, ry, value) ->
        if (symbols.any { (sx, sy) -> (sx in rx) && (sy in ry) }) {
            value
        } else {
            0
        }
    }
}

private fun part2(data: Schematic): Int {
    val (parts, symbols) = data
    val gears = symbols.filter { it.value == '*' }
    return gears.sumOf { (sx, sy) ->
        val adjacentNumbers = parts.mapNotNull { (rx, ry, value) ->
            if ((sx in rx) && (sy in ry)) value else null
        }

        if (adjacentNumbers.count() == 2) {
            adjacentNumbers.reduce(Int::times)
        } else {
            0
        }
    }
}
