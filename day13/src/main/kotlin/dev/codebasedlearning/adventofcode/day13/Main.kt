// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/13

package dev.codebasedlearning.adventofcode.day13

import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.toBlocks
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import kotlin.math.min

val examples = listOf(
"""
#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#
"""
)

fun main() {
    val story = object {
        val day = 13
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val patterns = story.lines.toBlocks()

    val similarities = patterns.map { pattern ->
        val grid = pattern.toGrid()
        val rows = (1..<grid.rows).associateWith { row ->
            val expansion = min(row, grid.rows-row)
            (0..<grid.cols).sumOf { col ->
                (0..<expansion).count { r -> grid[row-1-r,col]!=grid[row+r,col]}
            }
        }
        val cols = (1..<grid.cols).associateWith { col ->
            val expansion = min(col, grid.cols-col)
            (0..<grid.rows).sumOf { row ->
                (0..<expansion).count { c -> grid[row,col-1-c]!=grid[row,col+c]}
            }
        }
        rows to cols
    }

    // part 1: solutions: 405 / 33520

    checkResult(33520) { // [M3 374.334us]
        similarities.sumOf { (rows,cols) ->
            cols.entries.find { it.value==0 }?.key ?: rows.entries.find { it.value==0 }?.let { it.key*100 } ?: 0
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (line of reflection)") }

    // part 2: solutions: 400 / 34824

    checkResult(34824) { // [M3 158.917us]
        similarities.sumOf { (rows,cols) ->
            cols.entries.find { it.value==1 }?.key ?: rows.entries.find { it.value==1 }?.let { it.key*100 } ?: 0
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (mirrors with smudge)") }
}
