package day07

import java.io.File

fun main() {
    val data = parse("src/day07/Day07.txt")

    println("🎄 Day 07 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private data class Hand(
    val cards: String,
    val bid: Int,
)

private fun String.toHand(): Hand {
    val (cards, bid) = split(" ")
    return Hand(cards, bid.toInt())
}

private fun parse(path: String): List<Hand> =
    File(path)
        .readLines()
        .map(String::toHand)

// From weakest to strongest.
private val HAND_TYPES = listOf(
    listOf(1, 1, 1, 1, 1),
    listOf(2, 1, 1, 1),
    listOf(2, 2, 1),
    listOf(3, 1, 1),
    listOf(3, 2),
    listOf(4, 1),
    listOf(5),
)

private val PART1_LETTER_MAPPINGS = mapOf(
    'T' to 0x9UL,
    'J' to 0xAUL,
    'Q' to 0xBUL,
    'K' to 0xCUL,
    'A' to 0xDUL,
)

private val PART2_LETTER_MAPPINGS = mapOf(
    'J' to 0x0UL,
    'T' to 0x9UL,
    'Q' to 0xAUL,
    'K' to 0xBUL,
    'A' to 0xCUL,
)

private fun String.toCode(mappings: Map<Char, ULong>): ULong =
    fold(0UL) { acc, card ->
        // If the card doesn't correspond to a letter, it must be a digit.
        (acc shl 4) or (mappings[card] ?: (card.digitToInt() - 1).toULong())
    }

private fun String.toFrequencies(): List<Int> =
    this.groupingBy { it }
        .eachCount()
        .values
        .sortedDescending()

private fun solve(data: List<Pair<ULong, Hand>>): Int =
    data.sortedBy { (code, _) -> code }
        .mapIndexed { index, (_, hand) -> (index + 1) * hand.bid }
        .sum()

private fun Hand.encoded1(): ULong {
    val code = cards.toCode(PART1_LETTER_MAPPINGS)
    val type = HAND_TYPES.indexOf(cards.toFrequencies())
    return (type shl 20).toULong() or code
}

private fun part1(data: List<Hand>): Int =
    solve(data.map { hand -> hand.encoded1() to hand })

private fun Hand.encoded2(): ULong {
    val code = cards.toCode(PART2_LETTER_MAPPINGS)
    val frequencies = cards
        .filter { it != 'J' }
        .toFrequencies()
        .toMutableList()

    val jokers = cards.count { it == 'J' }
    if (frequencies.size > 0) {
        frequencies[0] += jokers
    } else {
        frequencies.add(jokers)
    }

    val type = HAND_TYPES.indexOf(frequencies)
    return (type shl 20).toULong() or code
}

private fun part2(data: List<Hand>): Int =
    solve(data.map { hand -> hand.encoded2() to hand })
