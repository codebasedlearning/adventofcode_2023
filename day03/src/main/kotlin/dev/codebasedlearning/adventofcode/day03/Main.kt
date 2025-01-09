// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/3

package dev.codebasedlearning.adventofcode.day03

import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.grid.withGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598..
"""
)

fun main() {
    val story = object {
        val day = 3
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val grid = story.lines.toGrid() // it makes things easier

    val symbolPos = grid.positions.withGrid(grid)
        .filter { it.value != '.' && !it.value.isDigit() }
        .associate { it.pos to it.value }
    println("Symbols: ${symbolPos.values.toSet()}\n") // nice to know

    fun corona(row: Int, cols: IntRange): Sequence<Position> = sequence {
        yieldAll((cols.first-1..cols.last+1).map { Position(row-1, it) })
        yield(Position(row, cols.first-1))
        yield(Position(row, cols.last+1))
        yieldAll((cols.first-1..cols.last+1).map { Position(row+1, it) })
    }

    val number = "\\d+".toRegex()
    val foundAt = symbolPos.mapValues { mutableListOf<Int>() }
    story.lines.forEachIndexed { row, line ->
        number.findAll(line).forEach { match ->
            corona(row, match.range).forEach { foundAt[it]?.add(match.value.toInt()) }
        }
    }

    // part 1: solutions: 4361 / 525911

    checkResult(525911) { // [M3 376us]
        foundAt.values.sumOf { it.sum() }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    // part 2: solutions: 467835 / 75805607

    checkResult(75805607) { // [M3 217.042us]
        foundAt.filter { (pos,nums) -> grid[pos]=='*' && nums.size==2 }.values.sumOf { it[0]*it[1] }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (...)") }
}
