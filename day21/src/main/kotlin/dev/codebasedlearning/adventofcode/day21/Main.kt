// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/21

package dev.codebasedlearning.adventofcode.day21

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
abc
"""
)

fun main() {
    val story = object {
        val day = 21
        val year = 2023
        val example = 1
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }


    // part 1: solutions: x / 1

    checkResult(1) { // [M3 523us]
        1
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    // part 2: solutions: y / 2

    checkResult(2) { // [M3 6.750792ms]
        2
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (...)") }

}
