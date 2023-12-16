package day16

import java.io.File

fun main() {
    val data = parse("src/day16/Day16.txt")

    println("🎄 Day 16 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private fun parse(path: String): List<List<Char>> =
    File(path)
        .readLines()
        .map(String::toList)

private data class Vec2(
    val x: Int,
    val y: Int,
)

private data class Beam(
    val position: Vec2,
    val direction: Vec2,
)

private fun Beam.advance(): Beam =
    copy(position = Vec2(position.x + direction.x, position.y + direction.y))

private val List<List<Char>>.w get() = this[0].size
private val List<List<Char>>.h get() = this.size

private fun List<List<Char>>.contains(beam: Beam): Boolean =
    beam.position.let { (x, y) -> x >= 0 && y >= 0 && x < w && y < h }

private fun List<List<Char>>.at(beam: Beam): Char =
    beam.position.let { (x, y) -> this[y][x] }

private fun solve(grid: List<List<Char>>, initialBeam: Beam): Int {
    val pool = ArrayDeque<Beam>().apply { add(initialBeam) }

    val cache = mutableSetOf<Beam>()
    val trace = mutableSetOf<Vec2>()

    while (pool.isNotEmpty()) {
        var beam = pool.removeFirst()
        while (grid.contains(beam) && beam !in cache) {
            cache += beam
            trace += beam.position

            when (grid.at(beam)) {
                '-' -> if (beam.direction.y != 0) {
                    pool += beam.copy(direction = Vec2(+1, 0))
                    pool += beam.copy(direction = Vec2(-1, 0))
                    break
                }
                '|' -> if (beam.direction.x != 0) {
                    pool += beam.copy(direction = Vec2(0, +1))
                    pool += beam.copy(direction = Vec2(0, -1))
                    break
                }
                '/' -> {
                    val (dx, dy) = beam.direction
                    beam = if (dx == 0) {
                        beam.copy(direction = Vec2(-dy, 0))
                    } else {
                        beam.copy(direction = Vec2(0, -dx))
                    }
                }
                '\\' -> {
                    val (dx, dy) = beam.direction
                    beam = if (dx == 0) {
                        beam.copy(direction = Vec2(dy, 0))
                    } else {
                        beam.copy(direction = Vec2(0, dx))
                    }
                }
            }

            beam = beam.advance()
        }
    }

    return trace.size
}

private fun part1(data: List<List<Char>>): Int {
    return solve(data, initialBeam = Beam(Vec2(0, 0), Vec2(1, 0)))
}

private fun part2(data: List<List<Char>>): Int {
    val (x0, x1) = (0 to data.w - 1)
    val (y0, y1) = (0 to data.h - 1)
    return maxOf(
        (x0..x1).maxOf { x -> solve(data, initialBeam = Beam(Vec2(x, y0), Vec2(0, +1))) },
        (x0..x1).maxOf { x -> solve(data, initialBeam = Beam(Vec2(x, y1), Vec2(0, -1))) },
        (y0..y1).maxOf { y -> solve(data, initialBeam = Beam(Vec2(x0, y), Vec2(+1, 0))) },
        (y0..y1).maxOf { y -> solve(data, initialBeam = Beam(Vec2(x1, y), Vec2(-1, 0))) },
    )
}
