// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/21

package dev.codebasedlearning.adventofcode.day21

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.geometry.visit
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import java.lang.Math.floorMod

val examples = listOf(
"""
...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
...........
"""
)

fun main() {
    val story = object {
        val day = 21
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val garden = story.lines.toGrid()
    val start = garden.positions.first { garden[it] == 'S' }
    garden[start] = '.'
    println("Start: $start, Garden: ${garden.rows} ${garden.cols}")

    fun gardenPlots(start: Position, repeats: Int, extendGrid: Boolean): Long {
        val queue = ArrayDeque<Position>()
        queue.add(start)

        val news = mutableSetOf<Position>()
        repeat(repeats) {
            news.clear()
            while (queue.isNotEmpty()) {
                val node = queue.removeFirst()
                news.addAll(node.visit(Direction.Cardinals).filter {
                    val p = if (extendGrid) Position(floorMod(it.row,garden.rows), floorMod(it.col,garden.cols)) else it
                    p in garden && garden[p]=='.' })
            }
            queue.addAll(news)
        }
        return news.size.toLong()
    }

    // part 1: solutions: - / 3642

    checkResult(3642) { // [M3 42.809500ms]
        gardenPlots(start,64,false)
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (garden plots)") }

    if (story.example != 0) return

    /*
    The solution of part 2 is very input dependent, e.g. the starting position is centred (65,65)
    in a square grid (131x131) and there are no blocks in straight lines to the borders, so
    the expansion is fast in these directions.
    In addition, after some steps (65+k*131) a specific chess-like pattern is formed and if you look
    at the grid at a 45-degree angle you can see a growing square.
    This leads to the conclusion that we have a quadratic form describing this pattern and the question is
    for the number of garden plots after 26501365 steps - which, 'luckily', is exactly
    k = (26501365 - 65) / 131 = 202300.
    You can find more details on reddit and also some figures showing these patterns, e.g.
    https://advent-of-code.xavd.id/writeups/2023/day/21/
    Finally, we need to evaluate the quadratic form on k, and to get the form we derive the coefficients
    from Lagrange's interpolation of order 2 for x_i=0,1,2 and y_i=65,65+1*131,65+2*131, see
    https://en.wikipedia.org/wiki/Lagrange_polynomial
    */

    // for ax^2+bx+c with x=0,1,2 and y=y0,y1,y2 we get this (use Lagrange basis and sum up coefficients for x^2, x and 1)
    fun lagrangeCoeffs2(y0: Long, y1: Long, y2: Long) = Triple(y0, -3L*y0/2L + 2L*y1 - y2/2L, y0/2L - y1 + y2/2L)

    // part 2: solutions: - / 608603023105276

    checkResult(608603023105276) { // [M3 3.795203208s]
        val n = garden.cols
        val mid = n/2
        val (c,b,a) = lagrangeCoeffs2(gardenPlots(start,mid,true),gardenPlots(start,mid+1*n,true),gardenPlots(start,mid+2*n,true))
        val x = (26501365L - mid) / n
        a * x*x + b * x + c
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (very large garden)") }
}
