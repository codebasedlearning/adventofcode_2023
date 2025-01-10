// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/6

package dev.codebasedlearning.adventofcode.day06

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

val examples = listOf(
"""
Time:      7  15   30
Distance:  9  40  200
"""
)

fun main() {
    val story = object {
        val day = 6
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val (times,time) = story.lines[0].substringAfter(':').run {
        parseNumbers<Long>(' ') to replace(" ","").toLong()
    }
    val (distances,distance) = story.lines[1].substringAfter(':').run {
        parseNumbers<Long>(' ') to replace(" ","").toLong()
    }

    // solution to this is the inner line between the roots of this (quadratic) upside-down parabel:
    //      (T-hold)*hold > dist
    //      -hold^2 + T hold - dist > 0
    // so solve this
    //      hold^2 - T hold + dist == 0
    // and take the next int for the left solution and the previous int of the right;
    // note: in case of roots being ints we need to take the next int as we need to be
    // better than the given distance

    fun incIfInt(value: Double) = if (value == value.toLong().toDouble()) 1 else 0

    fun calcWinningWays(time: Long, dist: Long): Long {
        val p2 = time.toDouble() / 2.0
        val sq = sqrt(p2*p2 - dist.toDouble())
        val (xLeft, xRight) = p2-sq to p2+sq
        return  (floor(xRight).toLong()-incIfInt(xRight)) - (ceil(xLeft).toLong()+incIfInt(xLeft)) + 1L
    }

    // part 1: solutions: 288 / 1413720

    checkResult(1413720) { // [M3 168.666us]
        times.zip(distances).fold(1L) { m, (time, dist) ->
            m * calcWinningWays(time,dist)
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (many Winning Ways)") }

    // part 2: solutions: 71503 / 30565288

    checkResult(30565288) { // [M3 4.958us]
        calcWinningWays(time,distance)
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (larger Winning Ways)") }
}
