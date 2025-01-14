// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/12

package dev.codebasedlearning.adventofcode.day12

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
"""
)

/*
count Arrangements

.??..??...?##. 1,1,3
.1...1....333
..1..1....333
.1....1...333
..1...1...333

?###???????? 3,2,1
.333.22.1...
...
.333....22.1

*/

fun main() {
    val story = object {
        val day = 12
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val springs = story.lines.map { line -> line.split(" ").let { it[0] to it[1].parseNumbers<Int>(',') } }

    fun countArrangements(springs: String, sizes: List<Int>,
                          springIndex: Int, sizeIndex:Int, fits: Int,
                          memo: MutableMap<Triple<Int,Int,Int>, Long> = mutableMapOf()
    ): Long = when {
        springIndex >= springs.length -> if (sizeIndex>=sizes.size && fits==0) 1 else 0     // one point if all fit
        else -> memo.getOrPut(key=Triple(springIndex,sizeIndex,fits)) {                     // memoization
            (if (springs[springIndex]=='?') ".#" else springs[springIndex].toString()).sumOf { c ->
                when {
                    // fits! next char
                    c == '#' -> countArrangements(springs, sizes, springIndex + 1, sizeIndex, fits + 1, memo)
                    // c==. restart at next char
                    fits == 0 -> countArrangements(springs, sizes, springIndex + 1, sizeIndex, 0, memo)
                    // c==. all fit! next char, next group
                    sizes.getOrNull(sizeIndex) == fits -> countArrangements(springs, sizes, springIndex + 1, sizeIndex + 1, 0, memo)
                    else -> 0
                }
            }
        }
    }

    // part 1: solutions: 21 / 7173

    checkResult(7173) { // [M3 10.286042ms]
        springs.sumOf { (springs, sizes) ->
            countArrangements("$springs.", sizes, springIndex = 0, sizeIndex = 0, fits = 0)
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (count arrangements)") }

    // part 2: solutions: 525152 / 29826669191291

    checkResult(29826669191291) { // [M3 111.307ms]
        springs.sumOf { (springs, sizes) ->
            countArrangements(("$springs?").repeat(5).dropLast(1)+".", List(5) { sizes }.flatten(),
                springIndex = 0, sizeIndex = 0, fits = 0)
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (count arrangements 5x)") }
}
