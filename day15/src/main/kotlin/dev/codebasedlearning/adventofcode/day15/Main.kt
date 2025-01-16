// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/15

package dev.codebasedlearning.adventofcode.day15

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
"""
)

fun main() {
    val story = object {
        val day = 15
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val tokens = story.lines[0].split(",")
    val lenses = tokens.map { token ->
        token.split("=", "-").let { Triple(it[0], token.endsWith("-"), it.getOrNull(1)?.toIntOrNull() ?: -1) }
    }

    fun hash(token:String): Int = token.fold(0) { acc, c -> ((acc + c.code) * 17) % 256 }

    // part 1: solutions: 1320 / 510801

    checkResult(510801) { // [M3 629.042us]
        tokens.sumOf { hash(it) }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (HASHes)") }

    // part 2: solutions: 145 / 212763

    fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? = indexOfFirst(predicate).let { if (it<0) null else it }

    checkResult(212763) { // [M3 5.716833ms]
        Array<MutableList<Pair<String,Int>>>(256) { mutableListOf() }.let { boxes ->
            lenses.forEach { (label,doRemove,focal) -> hash(label).let { box ->
                if (doRemove) boxes[box].removeIf { it.first == label }
                else boxes[box].indexOfFirstOrNull { it.first == label }
                        ?.let { boxes[box][it] = label to focal}
                        ?: boxes[box].add(label to focal)
            } }
            boxes.withIndex().sumOf {
                (it.index+1) * it.value.withIndex().sumOf { (it.index+1) * it.value.second }
            }
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (focusing power)") }
}
