// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/14

package dev.codebasedlearning.adventofcode.day14

import dev.codebasedlearning.adventofcode.commons.grid.Grid
import dev.codebasedlearning.adventofcode.commons.grid.copy
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#....
"""
)

fun main() {
    val story = object {
        val day = 14
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val rocks = story.lines.toGrid()
    // rocks.print(indent = 2, description = "Rocks:", separator = "")

    // implement only one direction, turn the grid for others;
    // could also be implemented in-place
    fun tiltNorth(grid: Grid<Char>) = grid.copy().apply {
        (0..<cols).forEach { col ->
            var freeRow: Int? = null
            (0..<rows).forEach { row ->
                when(this[row, col]) {
                    '.' -> if (freeRow == null) freeRow = row
                    '#' -> freeRow = null
                    'O' -> if (freeRow != null) { this[row, col] = '.'; this[freeRow++, col] = 'O' }
                }
            }
        }
    }

    fun calcTotalLoad(grid: Grid<Char>) = (0..<grid.rows).sumOf { row ->
        (0..<grid.cols).count { col -> grid[row, col] == 'O' } * (grid.rows-row)
    }

    // part 1: solutions: 136 / 108955

    checkResult(108955) { // [M3 2.617792ms]
        calcTotalLoad(tiltNorth(rocks))
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    fun turnClockwise(grid: Grid<Char>) = Grid<Char>(grid.cols,grid.rows) { '-' }.apply {
        grid.positions.forEach { (row,col) -> this[col,grid.rows - 1 - row] = grid[row,col] }
    }

    // we spin you around round
    fun turnAndTilt360(grid: Grid<Char>): Grid<Char> {
        val north = tiltNorth(grid)
        val west = tiltNorth(turnClockwise(north))
        val south = tiltNorth(turnClockwise(west))
        val east = tiltNorth(turnClockwise(south))
        return turnClockwise(east)
    }

    // part 2: solutions: 64 / 106689

    checkResult(106689) { // [M3 103.686500ms]
        val longRun = 1000000000
        // we assume, that even it is possible to wait for the result there is a shorter cycle,
        // i.e. after some n<longRun the current configuration repeats - so look for it
        var longRocks = rocks.copy()
        val memo = mutableMapOf<List<List<Char>>,Int>()
        do {
            memo[longRocks.data] = memo.size
            longRocks = turnAndTilt360(longRocks)
        } while (!memo.contains(longRocks.data))

        // we found one, now split the longRun into a start phase + m*cycle + remaining
        val cycleStart = memo[longRocks.data]!!
        val cycle = memo.size - cycleStart
        val remaining = (longRun - cycleStart) % cycle
        // println("\ncycle found: $cycle")

        repeat(remaining) { longRocks = turnAndTilt360(longRocks) }
        calcTotalLoad(longRocks)
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (...)") }
}
