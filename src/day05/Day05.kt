package day05

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
    val data = parse("src/day05/Day05.txt")

    println("🎄 Day 05 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private data class Range(
    val start: Long,
    val end: Long,
)

private data class Mapping(
    val src: Range,
    val dst: Range,
)

private data class Almanac(
    val seeds: List<Long>,
    val transforms: List<List<Mapping>>,
)

private fun String.toMapping(): Mapping =
    this.split(" ")
        .map(String::toLong)
        .let { (dst, src, length) ->
            Mapping(
                Range(src, src + length - 1),
                Range(dst, dst + length - 1),
            )
        }

private fun parse(path: String): Almanac {
    val sections = File(path)
        .readText()
        .split("\n\n", "\r\n\r\n")

    val seeds = sections.first()
        .split(": ", " ")
        .mapNotNull(String::toLongOrNull)

    val maps = sections.asSequence()
        .drop(1)
        .map { it.split("\n", "\r\n") }
        .map { it.drop(1) }
        .map { it.map(String::toMapping) }
        .toList()

    return Almanac(seeds, maps)
}

private fun intersection(a: Range, b: Range): Range? =
    if (a.end < b.start || b.end < a.start) {
        null
    } else {
        Range(max(a.start, b.start), min(a.end, b.end))
    }

private fun part1(data: Almanac): Long {
    val (seeds, transforms) = data

    val values = seeds.toMutableList()
    for (mapping in transforms) {
        transforming@ for ((i, value) in values.withIndex()) {
            for ((src, dst) in mapping) {
                if (value in src.start..src.end) {
                    values[i] = dst.start + (value - src.start)
                    continue@transforming
                }
            }
        }
    }

    return values.min()
}

private fun part2(data: Almanac): Long {
    val (seeds, transforms) = data
    val ranges = seeds
        .chunked(2)
        .map { (start, length) -> Range(start, start + length - 1) }
        .toMutableList()

    for (mappings in transforms) {
        // List of ranges that already had a mapping applied to them. These are
        // stored separately because we don't want to transform the same range
        // multiple times within the same transformation "round".
        val transformed = mutableListOf<Range>()

        for ((src, dst) in mappings) {
            // We may need to break each range into multiple sub-ranges in case
            // the mapping only applies to its portion. This is the "unmapped"
            // parts of the split ranges to be re-used later.
            val remainders = mutableListOf<Range>()

            for (range in ranges) {
                val intersection = intersection(range, src)
                if (intersection == null) {
                    remainders.add(range)
                    continue
                }

                val offset = intersection.start - src.start
                val length = intersection.end - intersection.start
                transformed.add(Range(
                    dst.start + offset,
                    dst.start + offset + length,
                ))

                if (range.start < src.start) {
                    remainders.add(Range(range.start, src.start - 1))
                }
                if (range.end > src.end) {
                    remainders.add(Range(src.end + 1, range.end))
                }
            }

            ranges.clear()
            ranges.addAll(remainders)
        }

        ranges.addAll(transformed)
    }

    return ranges.minOf(Range::start)
}
