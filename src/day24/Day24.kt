package day24

import java.io.File
import kotlin.math.max

fun main() {
    val data = parse("src/day24/Day24.txt")

    println("🎄 Day 24 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    // For Part 2, see the Python implementation.
}

private data class Vec3(
    val x: Long,
    val y: Long,
    val z: Long,
)

private data class Hailstone(
    val position: Vec3,
    val velocity: Vec3,
)

private fun String.toHailstone(): Hailstone {
    val (lhs, rhs) = this.split(" @ ")

    val position = lhs.split(", ")
        .map(String::trim)
        .map(String::toLong)
        .let { (x, y, z) -> Vec3(x, y, z) }

    val velocity = rhs.split(", ")
        .map(String::trim)
        .map(String::toLong)
        .let { (x, y, z) -> Vec3(x, y, z) }

    return Hailstone(position, velocity)
}

private fun parse(path: String): List<Hailstone> =
    File(path)
        .readLines()
        .map(String::toHailstone)

private fun intersect(a: Hailstone, b: Hailstone, min: Long, max: Long): Boolean {
    val dx = (b.position.x - a.position.x).toDouble()
    val dy = (b.position.y - a.position.y).toDouble()

    val det = (b.velocity.x * a.velocity.y - b.velocity.y * a.velocity.x).toDouble()

    val u = (dy * b.velocity.x - dx * b.velocity.y) / det
    val v = (dy * a.velocity.x - dx * a.velocity.y) / det

    if (u < 0 || v < 0) {
        return false
    }

    val x = a.position.x + a.velocity.x * u
    val y = a.position.y + a.velocity.y * u

    if (x.toLong() !in min..max) {
        return false
    }
    if (y.toLong() !in min..max) {
        return false
    }

    return true
}

private fun part1(data: List<Hailstone>): Int {
    val min = 200000000000000L
    val max = 400000000000000L

    var counter = 0
    for ((i, a) in data.withIndex()) {
        for ((j, b) in data.withIndex()) {
            if (j > i && intersect(a, b, min, max)) {
                counter += 1
            }
        }
    }

    return counter
}
