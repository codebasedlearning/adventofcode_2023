// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/4

package dev.codebasedlearning.adventofcode.day04

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
"""
)

fun main() {
    val story = object {
        val day = 4
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    data class Card(val id:Int, val winningNumbers: Set<Int>, val myNumbers: Set<Int>, var matches: Int = 0)

    val regexLine = """Card\s+(\d+):\s([\d\s]+)\|\s([\d\s]+)""".toRegex()
    val cards = story.lines.map { line ->
        regexLine.matchEntire(line)!!.run {
            Card(
                id = groups[1]!!.value.toInt(),
                winningNumbers = groups[2]!!.value.parseNumbers<Int>(' ').toSet(),
                myNumbers = groups[3]!!.value.parseNumbers<Int>(' ').toSet()
            )
        } }
    // println("Cards: ${cards}\n")

    val copies = cards.associate { it.id to 1 }.toMutableMap()

    // part 1: solutions: 13 / 25571

    checkResult(25571) { // [M3 635.917us]
        cards.sumOf { card -> card.run {
            matches = winningNumbers.count { it in myNumbers } // faster than intersect
            if (matches > 0) { 1 shl (matches-1) } else { 0 }
        } }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    // part 2: solutions: 30 / 8805731

    checkResult(8805731) { // [M3 589.125us]
        cards.forEach { card ->
            repeat(card.matches) { copies[card.id+it+1] = copies[card.id+it+1]!! + copies[card.id]!! }
        }
        copies.values.sum()
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (...)") }
}
