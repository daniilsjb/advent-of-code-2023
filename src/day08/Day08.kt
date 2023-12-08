package day08

import java.io.File

fun main() {
    val data = parse("src/day08/Day08.txt")

    println("🎄 Day 08 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private data class Instructions(
    val directions: String,
    val network: Map<String, Pair<String, String>>,
)

private fun parse(path: String): Instructions {
    val lines = File(path).readLines()

    val directions = lines.first()
    val network = lines.drop(2)
        .mapNotNull { """(\w+) = \((\w+), (\w+)\)""".toRegex().find(it)?.destructured }
        .associate { (key, left, right) -> key to (left to right) }

    return Instructions(directions, network)
}

private fun lcm(a: Long, b: Long): Long {
    var n1 = a
    var n2 = b
    while (n2 != 0L) {
        n1 = n2.also { n2 = n1 % n2 }
    }
    return (a * b) / n1
}

private fun Instructions.generateNodeSequence(start: String): Sequence<Pair<Int, String>> =
    generateSequence(seed = 0 to start) { (index, node) ->
        val nextDirection = directions[index % directions.length]
        val nextNode = if (nextDirection == 'L') {
            network.getValue(node).let { (next, _) -> next }
        } else {
            network.getValue(node).let { (_, next) -> next }
        }

        (index + 1) to nextNode
    }

private fun part1(data: Instructions): Long =
    data.generateNodeSequence(start = "AAA")
        .dropWhile { (_, node) -> node != "ZZZ" }
        .first().let { (index, _) -> index.toLong() }

private fun Instructions.periodFrom(start: String): Long {
    val path = mutableMapOf<Pair<String, Int>, Long>()
    return generateNodeSequence(start)
        .dropWhile { (index, node) ->
            val key = node to index % directions.length
            if (key !in path) {
                true.also { path[key] = index.toLong() }
            } else {
                false
            }
        }
        .first().let { (index, node) ->
            val directionIndex = index % directions.length
            val startingIndex = path.getValue(node to directionIndex)
            index.toLong() - startingIndex
        }
}

private fun part2(data: Instructions): Long =
    data.network.keys
        .filter { it.last() == 'A' }
        .map(data::periodFrom)
        .reduce(::lcm)
