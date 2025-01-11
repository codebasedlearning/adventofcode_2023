// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/9

package dev.codebasedlearning.adventofcode.day09

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45
"""
)

fun main() {
    val story = object {
        val day = 9
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val history = story.lines.map { line -> mutableListOf(line.parseNumbers<Int>(' ').toMutableList()) }

    // part 1: solutions: 114 / 1953784198

    checkResult(1953784198) { // [M3 5.714625ms]
        history.sumOf {
            // prepare diffs
            var last = it.last()
            while (last.any { it != 0 }) {
                last = last.zipWithNext { a,b -> b - a }.toMutableList()
                it.add(last)
            }
            // add values to the end
            it.last().add(0)
            (it.size - 2 downTo 0).forEach { i ->
                it[i].add(it[i+1].last() + it[i].last())
            }
            it[0].last()
        }

    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (extrapolated values)") }

    // part 2: solutions: 2 / 957

    checkResult(957) { // [M3 716.625us]
        history.sumOf {
            // add values to the front
            it.last().add(index = 0, 0)
            (it.size - 2 downTo 0).forEach { i ->
                it[i].add(index = 0, it[i].first() - it[i+1].first())
            }
            it[0].first()
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (extrapolate backwards)") }
}
