package day22

import java.io.File
import kotlin.math.max

fun main() {
    val data = parse("src/day22/Day22.txt")
    val (supports, standsOn) = simulate(data)

    println("🎄 Day 22 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data.size, supports, standsOn)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data.size, supports, standsOn)}")
}

private data class Brick(
    val x0: Int, val x1: Int,
    val y0: Int, val y1: Int,
    val z0: Int, val z1: Int,
)

private fun String.toBrick(): Brick {
    val (lhs, rhs) = this.split('~')

    val (x0, y0, z0) = lhs.split(',')
    val (x1, y1, z1) = rhs.split(',')

    return Brick(
        x0 = x0.toInt(), x1 = x1.toInt(),
        y0 = y0.toInt(), y1 = y1.toInt(),
        z0 = z0.toInt(), z1 = z1.toInt(),
    )
}

private fun parse(path: String): List<Brick> =
    File(path)
        .readLines()
        .map(String::toBrick)
        .sortedBy { it.z0 }

private fun overlap(a: Brick, b: Brick): Boolean =
    (a.x1 >= b.x0 && b.x1 >= a.x0) && (a.y1 >= b.y0 && b.y1 >= a.y0)

private fun simulate(data: List<Brick>): Pair<List<Set<Int>>, List<Set<Int>>> {
    val bricks = data.toMutableList()
    for ((i, upper) in bricks.withIndex()) {
        var z = 1
        for (lower in bricks.subList(0, i)) {
            if (overlap(upper, lower)) {
                z = max(z, lower.z1 + 1)
            }
        }

        bricks[i] = upper.copy(
            z0 = z,
            z1 = z + (upper.z1 - upper.z0),
        )
    }

    bricks.sortBy { it.z0 }

    val supports = bricks.map { mutableSetOf<Int>() }
    val standsOn = bricks.map { mutableSetOf<Int>() }

    for ((i, upper) in bricks.withIndex()) {
        for ((j, lower) in bricks.subList(0, i).withIndex()) {
            if (upper.z0 == lower.z1 + 1) {
                if (overlap(upper, lower)) {
                    supports[j] += i
                    standsOn[i] += j
                }
            }
        }
    }

    return Pair(supports, standsOn)
}

private fun part1(n: Int, supports: List<Set<Int>>, standsOn: List<Set<Int>>): Int =
    (0..<n).count { i -> supports[i].all { j -> standsOn[j].size > 1 } }

private fun part2(n: Int, supports: List<Set<Int>>, standsOn: List<Set<Int>>): Int {
    var count = 0
    for (i in 0..<n) {
        val queue = ArrayDeque<Int>()
            .apply { addAll(supports[i].filter { j -> standsOn[j].size == 1 }) }

        val falling = mutableSetOf(i)
        falling += queue

        while (queue.isNotEmpty()) {
            val j = queue.removeFirst()
            for (k in supports[j]) {
                if (k !in falling && falling.containsAll(standsOn[k])) {
                    queue += k
                    falling += k
                }
            }
        }

        count += falling.size - 1
    }

    return count
}
