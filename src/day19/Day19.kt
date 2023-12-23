package day19

import java.io.File

fun main() {
    val (workflows, parts) = parse("src/day19/Day19.txt")

    println("🎄 Day 19 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(workflows, parts)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(workflows)}")
}

private data class Condition(
    val key: Char,
    val cmp: Char,
    val num: Int,
)

private data class Rule(
    val condition: Condition?,
    val transition: String,
)

private typealias Part = Map<Char, Int>
private typealias Workflows = Map<String, List<Rule>>

private fun String.toPart(): Part =
    this.trim('{', '}')
        .split(',')
        .map { it.split('=') }
        .associate { (k, n) -> k.first() to n.toInt() }

private fun String.toRule(): Rule {
    if (!this.contains(':')) {
        return Rule(condition = null, transition = this)
    }

    val (lhs, transition) = this.split(':')
    val condition = Condition(
        key = lhs[0],
        cmp = lhs[1],
        num = lhs.substring(2).toInt(),
    )

    return Rule(condition, transition)
}

private fun String.toWorkflow(): Pair<String, List<Rule>> {
    val (name, rules) = this.trim('}').split('{')
    return Pair(name, rules.split(',').map(String::toRule))
}

private fun parse(path: String): Pair<Workflows, List<Part>> {
    val (workflowsPart, partsPart) = File(path)
        .readText()
        .split("\n\n", "\r\n\r\n")

    val workflows = workflowsPart.lines()
        .map(String::toWorkflow)
        .associate { (name, rules) -> name to rules }

    val parts = partsPart.lines()
        .map(String::toPart)

    return Pair(workflows, parts)
}

private fun count(part: Part, workflows: Workflows): Int {
    var target = "in"
    while (target != "A" && target != "R") {
        val rules = workflows.getValue(target)
        for ((condition, transition) in rules) {
            if (condition == null) {
                target = transition
                break
            }

            val (key, cmp, num) = condition
            val satisfies = if (cmp == '>') {
                part.getValue(key) > num
            } else {
                part.getValue(key) < num
            }

            if (satisfies) {
                target = transition
                break
            }
        }
    }

    return if (target == "A") {
        part.values.sum()
    } else {
        0
    }
}

private fun part1(workflows: Workflows, parts: List<Part>): Int =
    parts.sumOf { count(it, workflows) }

private fun part2(workflows: Workflows): Long {
    val queue = ArrayDeque<Pair<String, Map<Char, IntRange>>>()
        .apply { add("in" to mapOf(
            'x' to 1..4000,
            'm' to 1..4000,
            'a' to 1..4000,
            's' to 1..4000,
        )) }

    var total = 0L
    while (queue.isNotEmpty()) {
        val (target, startingRanges) = queue.removeFirst()
        if (target == "A" || target == "R") {
            if (target == "A") {
                val product = startingRanges.values
                    .map { (it.last - it.first + 1).toLong() }
                    .reduce { acc, it -> acc * it }

                total += product
            }
            continue
        }

        val ranges = startingRanges.toMutableMap()
        val rules = workflows.getValue(target)
        for ((condition, transition) in rules) {
            if (condition == null) {
                queue += transition to ranges
                break
            }

            val (key, cmp, num) = condition
            val range = ranges.getValue(key)

            val (t, f) = if (cmp == '>') {
                IntRange(num + 1, range.last) to IntRange(range.first, num)
            } else {
                IntRange(range.first, num - 1) to IntRange(num, range.last)
            }

            queue += transition to ranges.toMutableMap().apply { set(key, t) }
            ranges[key] = f
        }
    }

    return total
}
