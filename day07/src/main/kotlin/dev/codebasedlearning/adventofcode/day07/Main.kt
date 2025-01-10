// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/7

package dev.codebasedlearning.adventofcode.day07

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import kotlin.comparisons.thenBy

val examples = listOf(
"""
32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483
"""
)

enum class HandValue(val value: Int) {
    HIGH_CARD(1), ONE_PAIR(2), TWO_PAIRS(3), THREE_OF_A_KIND(4),
    FULL_HOUSE(5), FOUR_OF_A_KIND(6), FIVE_OF_A_KIND(7);

    companion object {
        // idea is to use the char-counts, independent of their positions
        fun of(map: Map<Char, Int>, withJoker: Boolean): HandValue {
            var rc = when {
                map.values.contains(5) -> FIVE_OF_A_KIND
                map.values.contains(4) -> FOUR_OF_A_KIND
                map.values.contains(3) && map.values.contains(2) -> FULL_HOUSE
                map.values.contains(3) -> THREE_OF_A_KIND
                map.values.count { it == 2 } == 2 -> TWO_PAIRS
                map.values.contains(2) -> ONE_PAIR
                else -> HIGH_CARD
            }
            // if jokers are allowed, this changes; note, the order is important
            val js = map['J'] ?: 0
            if (withJoker && js > 0) {
                rc = when (rc) {
                    FIVE_OF_A_KIND -> FIVE_OF_A_KIND
                    FOUR_OF_A_KIND -> FIVE_OF_A_KIND    // only one or four jokers
                    FULL_HOUSE -> FIVE_OF_A_KIND        // only two or three jokers
                    THREE_OF_A_KIND -> FOUR_OF_A_KIND   // only one or three jokers
                    TWO_PAIRS -> if (js==1) FULL_HOUSE else FOUR_OF_A_KIND  // only one or two jokers
                    ONE_PAIR -> THREE_OF_A_KIND         // only one or two jokers
                    HIGH_CARD -> ONE_PAIR               // only one joker
                }
            }
            return rc
        }
    }
}

// here we map the hand to a new string that represents the order

// A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2
// M  L  K  J  I  H  G  F  E  D  C  B  A
val cardValues1 = mapOf('A' to 'M', 'K' to 'L', 'Q' to 'K', 'J' to 'J', 'T' to 'I', '9' to 'H', '8' to 'G', '7' to 'F', '6' to 'E', '5' to 'D', '4' to 'C', '3' to 'B', '2' to 'A')

// A, K, Q, T, 9, 8, 7, 6, 5, 4, 3, 2, or J
// M  L  K  J  I  H  G  F  E  D  C  B  A
val cardValues2 = mapOf('A' to 'M', 'K' to 'L', 'Q' to 'K', 'T' to 'J', '9' to 'I', '8' to 'H', '7' to 'G', '6' to 'F', '5' to 'E', '4' to 'D', '3' to 'C', '2' to 'B', 'J' to 'A',)

fun main() {
    val story = object {
        val day = 7
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    data class Hand(val hand: String, val bid: Long,
                    val dist: Map<Char, Int> = hand.groupingBy { it }.eachCount(),
                    val baseValue1: HandValue = HandValue.of(dist, withJoker=false),
                    val cardValue1: String = hand.map { cardValues1[it] }.joinToString(""),
                    val baseValue2: HandValue = HandValue.of(dist, withJoker=true),
                    val cardValue2: String = hand.map { cardValues2[it] }.joinToString(""),
    )

    val hands = story.lines.map { line -> line.split(' ').let { (hand,bid) ->
        Hand(hand = hand, bid = bid.toLong())
    } }

    // sort list, first on hand value, then by the mapped hand
    val sortedHands1 = hands.sortedWith(compareBy<Hand> { it.baseValue1 }.thenBy { it.cardValue1 })
    val sortedHands2 = hands.sortedWith(compareBy<Hand> { it.baseValue2 }.thenBy { it.cardValue2 })

    // part 1: solutions: 6440 / 249638405

    checkResult(249638405) { // [M3 523us]
        sortedHands1.withIndex().sumOf { (index, hand) -> (index+1) * hand.bid }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    // part 2: solutions: 5905 / 249776650

    checkResult(249776650) { // [M3 6.750792ms]
        sortedHands2.withIndex().sumOf { (index, hand) -> (index+1) * hand.bid }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (...)") }
}
