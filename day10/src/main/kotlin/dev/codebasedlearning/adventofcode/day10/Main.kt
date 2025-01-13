// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/10

package dev.codebasedlearning.adventofcode.day10

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import dev.codebasedlearning.adventofcode.commons.geometry.plus

val examples = listOf(
"""
-L|F7
7S-7|
L|7||
-L-J|
L|-JF
""",
"""
7-F7-
.FJ|7
SJLL7
|F--J
LJ.LJ
""",
"""
...........
.S-------7.
.|F-----7|.
.||.....||.
.||.....||.
.|L-7.F-J|.
.|..|.|..|.
.L--J.L--J.
...........
""",
"""
.F----7F7F7F7F-7....
.|F--7||||||||FJ....
.||.FJ||||||||L7....
FJL7L7LJLJ||LJ.L-7..
L--J.L7...LJS7F-7L7.
....F-J..F7FJ|L7L7L7
....L7.F7||L7|.L7L7|
.....|FJLJ|FJ|F7|.LJ
....FJL-7.||.||||...
....L---J.LJ.LJLJ...
"""
)

fun main() {
    val story = object {
        val day = 10
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val pipes = story.lines.toGrid()

    fun findStartAndRepair(): Pair<Position, Direction>
    = pipes.positions.find { pipes[it] == 'S' }!!.let { start ->
        Direction.Cardinals.filter { start + it in pipes }.filter {
            (it == Direction.Up && pipes[start + it] in "|7F") ||
                    (it == Direction.Right && pipes[start + it] in "-J7") ||
                    (it == Direction.Down && pipes[start + it] in "|LJ") ||
                    (it == Direction.Left && pipes[start + it] in "-LF")
        }.let { inOut ->
            pipes[start] = when (inOut.toSet()) {
                setOf(Direction.Up, Direction.Right) -> 'L'
                setOf(Direction.Up, Direction.Down) -> '|'
                setOf(Direction.Up, Direction.Left) -> 'J'
                setOf(Direction.Right, Direction.Down) -> 'F'
                setOf(Direction.Right, Direction.Left) -> '-'
                setOf(Direction.Down, Direction.Left) -> '7'
                else -> throw RuntimeException("unknown combination at $start")
            }
            start to inOut.first()
        } }

    fun nextPipe(pos: Position, from: Direction):Direction {
        val c = pipes[pos]
        return when {
            c=='-' && (from==Direction.Right || from==Direction.Left) -> from
            c=='|' && (from==Direction.Up || from==Direction.Down) -> from
            c=='J' && from==Direction.Right -> Direction.Up
            c=='J' && from==Direction.Down -> Direction.Left
            c=='7' && from==Direction.Right -> Direction.Down
            c=='7' && from==Direction.Up -> Direction.Left
            c=='L' && from==Direction.Left -> Direction.Up
            c=='L' && from==Direction.Down -> Direction.Right
            c=='F' && from==Direction.Left -> Direction.Down
            c=='F' && from==Direction.Up -> Direction.Right
            else -> throw RuntimeException("unknown $c at $pos from $from")
        }
    }

    var (start,dir) = findStartAndRepair()
    println("Start: $start Direction: $dir\n")

    val pipePos = mutableSetOf<Position>()
    var pos = start

    // part 1: solutions: - / 6682

    checkResult(6682) { // [M3 523us]
        do {
            pipePos.add(pos)
            pos += dir
            dir = nextPipe(pos, dir)
        } while (pos!=start)
        pipePos.size/2
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    // part 2: solutions: - / 353

    checkResult(353) { // [M3 6.750792ms]
        // everytime we cross a pipe, we switch inner to outer area or vice versa;
        // if we run along a pipe, we have to postpone the decision up to the point
        // until we know if we cross (FJ or L7) or bounce back (F7, LJ)
        var innerPos = 0
        var corner = '?'
        var isIn = false
        // positions run row-wise!
        pipes.positions.forEach { pos ->
            if (pos.row==0) { isIn = false; corner = '?' }
            if (pos !in pipePos) {
                if (isIn) { ++innerPos }
            } else {
                when(pipes[pos]) {
                    '|' -> isIn = !isIn                     // crossed
                    '-' -> {}                               // still unknown
                    'F', 'L' -> corner = pipes[pos]
                    '7' -> if (corner=='L') isIn = !isIn    // change if crossed
                    'J' -> if (corner=='F') isIn = !isIn
                }
            }
        }
        innerPos
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (...)") }
}
