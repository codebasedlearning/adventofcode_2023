// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/2

package dev.codebasedlearning.adventofcode.day02

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"""
)

fun main() {
    val story = object {
        val day = 2
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    data class Game(val id:Int, val cubes: List<Map<String,Int>>)

    val games = story.lines.map { line -> line.split(": ").let { (game,sets) ->
        Game(
            id = game.substringAfter("Game ").toInt(),
            cubes = sets.split("; ").map { it.split(", ").associate { cube ->
                cube.split(" ").let { (count,color) -> color to count.toInt() }
            } }
        )
    } }

    fun rgbMax(cubes: List<Map<String,Int>>): Triple<Int,Int,Int> {
        val red = cubes.maxOf { it["red"] ?: 0 }
        val green = cubes.maxOf { it["green"] ?: 0 }
        val blue = cubes.maxOf { it["blue"] ?: 0 }
        return Triple(red,green,blue)
    }

    // part 1: solutions: 8 / 2061

    checkResult(2061) { // [M3 523us]
        games.sumOf { game ->
            val (red,green,blue) = rgbMax(game.cubes)
            if (red <= 12 && green <= 13 && blue <= 14) game.id else 0
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (valid games)") }

    // part 2: solutions: 2286 / 72596

    checkResult(72596) { // [M3 6.750792ms]
        games.sumOf { game ->
            val (red,green,blue) = rgbMax(game.cubes)
            red * green * blue
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (powers)") }
}
