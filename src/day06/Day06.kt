package day06

import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    val data = parse("src/day06/Day06.txt")

    println("🎄 Day 06 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private data class Race(
    val time: Long,
    val distance: Long,
)

private fun parse(path: String): Pair<List<String>, List<String>> =
    File(path)
        .readLines()
        .map { it.split("\\s+".toRegex()).drop(1).map(String::trim) }
        .let { (ts, ds) -> ts to ds }

private fun solve(data: List<Race>): Long =
    data.fold(1) { acc, (t, d) ->
        val x1 = floor((t - sqrt(t * t - 4.0 * d)) / 2.0 + 1.0).toInt()
        val x2 = ceil((t + sqrt(t * t - 4.0 * d)) / 2.0 - 1.0).toInt()
        acc * (x2 - x1 + 1)
    }

private fun part1(data: Pair<List<String>, List<String>>): Long =
    data.let { (ts, ds) -> ts.map(String::toLong) to ds.map(String::toLong) }
        .let { (ts, ds) -> ts.zip(ds) { t, d -> Race(t, d) } }
        .let { solve(it) }

private fun part2(data: Pair<List<String>, List<String>>): Long =
    data.let { (ts, ds) -> ts.joinToString("") to ds.joinToString("") }
        .let { (ts, ds) -> Race(ts.toLong(), ds.toLong()) }
        .let { solve(listOf(it)) }
