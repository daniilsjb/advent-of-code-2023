package day03

import java.io.File

fun main() {
    val data = parse("src/day03/Day03.txt")

    val answer1 = part1(data)
    val answer2 = part2(data)

    println("🎄 Day 03 🎄")

    println()

    println("[Part 1]")
    println("Answer: $answer1")

    println()

    println("[Part 2]")
    println("Answer: $answer2")
}

private data class Part(
    val position: Pair<Int, Int>,
    val length: Int,
    val value: Int,
)

private data class Symbol(
    val position: Pair<Int, Int>,
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
            val x = it.range.first
            val end = it.range.last
            Part(x to y, end - x + 1, it.value.toInt())
        })

        symbols.addAll(matchedSymbols.map {
            val x = it.range.first
            Symbol(x to y, it.value[0])
        })
    }

    return Schematic(parts, symbols)
}

private fun Symbol.within(rx: IntRange, ry: IntRange): Boolean =
    position.let { (sx, sy) -> (sx in rx) && (sy in ry) }

private fun part1(data: Schematic): Int {
    val (parts, symbols) = data
    return parts.sumOf { part ->
        val (px, py) = part.position
        val rx = (px - 1)..(px + part.length)
        val ry = (py - 1)..(py + 1)

        if (symbols.any { it.within(rx, ry) }) {
            part.value
        } else {
            0
        }
    }
}

private fun part2(data: Schematic): Int {
    val (parts, symbols) = data
    val gears = symbols.filter { it.value == '*' }
    return gears.sumOf { symbol ->
        val adjacentParts = parts.filter { part ->
            val (px, py) = part.position
            val rx = (px - 1)..(px + part.length)
            val ry = (py - 1)..(py + 1)
            symbol.within(rx, ry)
        }

        if (adjacentParts.count() == 2) {
            adjacentParts
                .map(Part::value)
                .reduce(Int::times)
        } else {
            0
        }
    }
}
