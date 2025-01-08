// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/1

package dev.codebasedlearning.adventofcode.day01

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
// v1
"""
1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet
""",
// v2
"""
two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen
""",
// v3
"""
oneight 
"""
)

fun main() {
    val story = object {
        val day = 1
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    // part 1: solutions: 142 / 54697

    checkResult(54697) { // [M3 6.508458ms]
        val regexPart1 = "\\d".toRegex()
        story.lines.map { regexPart1.findAll(it) }.sumOf { matches ->
            val first = matches.firstOrNull()?.value ?: "0" // for example 2
            val last = matches.lastOrNull()?.value ?: "0"
            "$first$last".toInt() // or first * 10 + last
        }
    }.let { (dt, result, check) -> println("[part 1] result: $result $check, dt: $dt (two-digit number)") }

    // I also wanted to solve this problem with RegEx in an analogous way,
    // and the number of overlapping numbers is small.

    // part 2: solutions: 281 / 54885

    checkResult(54885) { // [M3 5.881459ms]
        val regexPart2 = "\\d|oneight|twone|threeight|fiveight|sevenine|eightwo|eighthree|nineight|one|two|three|four|five|six|seven|eight|nine".toRegex()
        val digitsMap = mapOf(
            "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
            "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
        )
        val mixedFirst = mapOf(
            "oneight" to 1, "twone" to 2, "threeight" to 3, "fiveight" to 5,
            "sevenine" to 7, "eightwo" to 8, "eighthree" to 8, "nineight" to 9,
        )
        val mixedLast = mapOf(
            "oneight" to 8, "twone" to 1, "threeight" to 8, "fiveight" to 8,
            "sevenine" to 9, "eightwo" to 2, "eighthree" to 3, "nineight" to 8,
        )
        story.lines.map { regexPart2.findAll(it) }.sumOf { matches ->
            val first = matches.first().value
            val last = matches.last().value
            "${(digitsMap[first] ?: mixedFirst[first] ?: first)}${(digitsMap[last] ?: mixedLast[last] ?: last)}".toInt()
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (spelled out with letters)") }
}
