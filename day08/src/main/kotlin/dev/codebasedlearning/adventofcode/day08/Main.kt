// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/8

package dev.codebasedlearning.adventofcode.day08

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.splitHeaderBlock
import dev.codebasedlearning.adventofcode.commons.input.toBlocks
import dev.codebasedlearning.adventofcode.commons.iterables.countWhile
import dev.codebasedlearning.adventofcode.commons.iterables.repeat
import dev.codebasedlearning.adventofcode.commons.math.scm
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
LLR

AAA = (BBB, BBB)
BBB = (AAA, ZZZ)
ZZZ = (ZZZ, ZZZ)
""",
"""
LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)    
"""
)

fun main() {
    val story = object {
        val day = 8
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val regex = "(\\w+)|(?<=\\()\\w+(?=,)|(?<=,)\\s*\\w+(?=\\))".toRegex()
    val (instructions, elements) = story.lines.toBlocks().splitHeaderBlock(1).let {
        it.first[0][0] to it.second[0].associate { line ->
            regex.findAll(line).map { it.value.trim() }.toList().let { it[0] to (it[1] to it[2]) }
        }
    }

    // part 1: solutions: 6 / 21389

    if (story.example in 0..1) {
        checkResult(21389) { // [M3 7.616333ms]
            val instruction = repeat(input = instructions).iterator()
            var current = "AAA"
            countWhile({ current != "ZZZ" }) {
                current = elements[current]!!.let { if (instruction.next() == 'L') it.first else it.second }
            }
        }.let { (dt, result, check) -> println("[part 1] result: $result $check, dt: $dt (steps to ZZZ)") }
    }

    // part 2: solutions: 6 / 21083806112641

    val ghosts = elements.keys.filter { it.endsWith('A') }.associateWith { 0L }.toMutableMap()

    checkResult(21083806112641) { // [M3 7.180916ms]
        // input is carefully crafted such that each ghost run into a loop; so in the end
        // the smallest common multiple (SCM) of all loops is the solution
        ghosts.keys.forEach { ghost ->
            val instruction = repeat(input = instructions).iterator()
            var current = ghost
            val cnt = countWhile({ !current.endsWith('Z') }) {
                current = elements[current]!!.let { if (instruction.next() == 'L') it.first else it.second }
            }
            ghosts[ghost] = cnt
        }
        scm(ghosts.values.toList())
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (ghosts)") }
}
