// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/16

package dev.codebasedlearning.adventofcode.day16

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.geometry.Step
import dev.codebasedlearning.adventofcode.commons.geometry.plus
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
.|...\....
|.-.\.....
.....|-...
........|.
..........
.........\
..../.\\..
.-.-/..|..
.|....-|.\
..//.|....
"""
)

fun main() {
    val story = object {
        val day = 16
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val mirrors = story.lines.toGrid()

    fun Step.into(dir: Direction) = Step(this.pos + dir, dir)

    fun energizedTiles(start: Step): Int {
        val newSteps = mutableListOf<Step>()
        val visitedSteps = mutableSetOf<Step>()
        var nextStep: Step? = start
        while (nextStep!=null || newSteps.isNotEmpty()) {
            var step = nextStep ?: newSteps.removeFirst()
            if (step !in visitedSteps && step.pos in mirrors) {
                visitedSteps.add(step)
                when (mirrors[step.pos]) {
                    '.' -> nextStep = step.into(step.dir)
                    '/' -> nextStep = step.into(when (step.dir) {
                            Direction.Right -> Direction.Up
                            Direction.Up -> Direction.Right
                            Direction.Left -> Direction.Down
                            Direction.Down -> Direction.Left
                            else -> throw Exception("Invalid direction: $step")
                        })
                    '\\' -> nextStep = step.into(when (step.dir) {
                            Direction.Right -> Direction.Down
                            Direction.Up -> Direction.Left
                            Direction.Left -> Direction.Up
                            Direction.Down -> Direction.Right
                            else -> throw Exception("Invalid direction: $step")
                        })
                    '|' -> if (step.dir.isHorizontal) {
                                nextStep = step.into(Direction.Up)
                                newSteps.add(step.into(Direction.Down))
                            } else nextStep = step.into(step.dir)
                    '-' -> if (step.dir.isVertical) {
                                nextStep = step.into(Direction.Left)
                                newSteps.add(step.into(Direction.Right))
                            } else nextStep = step.into(step.dir)
                    else -> throw Exception("Invalid character at $step")
                }
            } else nextStep = null
        }
        return visitedSteps.map { it.pos }.toSet().size
    }

    // part 1: solutions: 46 / 8112

    checkResult(8112) { // [M3 8.267500ms]
        energizedTiles(Step(pos=Position(0, 0), dir=Direction.Right))
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (energized tiles)") }

    // part 2: solutions: 51 / 8314

    checkResult(8314) { // [M3 344.692584ms]
        sequence {
            yieldAll( (0..<mirrors.cols).map { col -> Step(pos=Position(0,col), dir=Direction.Down) })
            yieldAll( (0..<mirrors.rows).map { row -> Step(pos=Position(row,0), dir=Direction.Right) })
            yieldAll( (0..<mirrors.rows).map { row -> Step(pos=Position(row,mirrors.cols-1), dir=Direction.Left) })
            yieldAll( (0..<mirrors.cols).map { col -> Step(pos=Position(mirrors.rows-1,col), dir=Direction.Up) })
        }.maxOf { energizedTiles(it) }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (min energized tiles)") }
}
