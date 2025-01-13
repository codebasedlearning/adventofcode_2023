// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/11

package dev.codebasedlearning.adventofcode.day11

import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.geometry.minus
import dev.codebasedlearning.adventofcode.commons.geometry.norm1
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import kotlin.math.max
import kotlin.math.min

val examples = listOf(
"""
...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#.....
"""
)

fun main() {
    val story = object {
        val day = 11
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val space = story.lines.toGrid()
    val galaxies = space.positions.filter { space[it] == '#' }.toList()

    val freeRows = (0..<space.rows).filter { row -> galaxies.all { it.row != row } }
    val freeCols = (0..<space.cols).filter { col -> galaxies.all { it.col != col } }

    // part 1: solutions: 374 / 9693756

    val galaxyPairs = mutableListOf<Pair<Position, Position>>().apply {
        for (i in 0..<galaxies.size) {
            for (j in i + 1 until galaxies.size) {
                add(Pair(galaxies[i], galaxies[j]))
            }
        }
    }

    fun calcDistance(p1: Position, p2: Position, expansion: Long)
    = (p1-p2).asDir.norm1 +
            ( freeRows.count { it in min(p1.row,p2.row)..max(p1.row,p2.row) } +
              freeCols.count { it in min(p1.col,p2.col)..max(p1.col,p2.col) } ) * (expansion-1L)

    checkResult(9693756) { // [M3 523us]
        galaxyPairs.sumOf { (p1,p2) -> calcDistance(p1,p2,expansion = 2L) }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    // part 2: solutions: 8410 (for factor 100) / 717878258016

    checkResult(717878258016) { // [M3 6.750792ms]
        galaxyPairs.sumOf { (p1,p2) -> calcDistance(p1,p2,expansion = 1000000L) }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (...)") }
}
