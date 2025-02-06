// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/22

package dev.codebasedlearning.adventofcode.day22

import dev.codebasedlearning.adventofcode.commons.geometry.Location
import dev.codebasedlearning.adventofcode.commons.grid.Space
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
1,0,1~1,2,1
0,0,2~2,0,2
0,2,3~2,2,3
0,0,4~0,2,4
2,0,5~2,2,5
0,1,6~2,1,6
1,1,8~1,1,9
""",
"""
1,0,2~1,2,2
0,0,3~2,0,3
0,2,4~2,2,4
0,0,5~0,2,5
2,0,6~2,2,6
0,1,7~2,1,7
1,1,9~1,1,10
"""
)

fun main() {
    val story = object {
        val day = 22
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    data class Brick(var start: Location, var end: Location, var dzArchive: Int = 0,
                     val id: String = ('A'..'Z').random().toString()) {
        val locations get() = (start.x..end.x).asSequence().flatMap { x -> (start.y..end.y).flatMap { y -> (start.z..end.z).map { z -> Location(x,y,z) } } }
        val bottom get() = (start.x..end.x).flatMap { x -> (start.y..end.y).map { y -> Location(x,y,start.z) } }
        fun correctFromArchive() {
            start = Location(start.x, start.y, start.z+dzArchive)
            end = Location(end.x, end.y, end.z+dzArchive)
        }
    }

    fun <T> Space<T>.copyTo(other: Space<T>) { locations.forEach { other[it] = this[it] } }

    fun Space<Brick?>.print() { // for the smaller examples
        for (y in (yDim-1) downTo 0) {
            for (z in 0..<zDim) { print(data[z][y].joinToString(separator = "", postfix = " | ") { it?.id ?: "." }) }
            println()
        }
    }

    fun Brick.storeIn(space: Space<Brick?>, withCheck: Boolean = false) = apply {
        if (withCheck && locations.any { space[it] != null }) throw RuntimeException("no room for brick $id")
        locations.forEach { space[it] = this }
    }

    fun Brick.removeFrom(space: Space<Brick?>) = apply { locations.forEach { space[it] = null } }

    fun Brick.fallIn(space: Space<Brick?>, justCheck: Boolean = false, shouldArchive: Boolean = false): Int {
        var z = start.z
        while (z>1 && bottom.all { space[it.x,it.y,z-1] == null }) { z-- }
        val dz = start.z-z
        if (!justCheck) {
            if (shouldArchive) this.dzArchive = dz
            if (dz>0) {
                locations.forEach { space[it] = null }
                start = Location(start.x, start.y, start.z - dz)
                end = Location(end.x, end.y, end.z - dz)
                locations.forEach { space[it] = this }
            }
        }
        return dz
    }

    // ---

    val bricksSnapshot = story.lines.map { line -> line.split("~").let { (start,end) ->
        Brick(Location(start.parseNumbers<Int>(',')),Location(end.parseNumbers<Int>(',')))
    } }
    val (xMax, yMax, zMax) = bricksSnapshot.run { Triple(maxOf { it.end.x }, maxOf { it.end.y }, maxOf { it.end.z }) }
    val tower = Space<Brick?>(xMax+1, yMax+1, zMax+1) { _ -> null }

    // snapshot bricks are not ordered (in contrast to the example)
    (1..<tower.zDim).forEach { z ->
        bricksSnapshot.filter { it.start.z == z }.forEach {
            it.storeIn(tower, withCheck = true).fallIn(tower)
        } }
    if (story.example>0) tower.print()

    val sortedBricks = bricksSnapshot.sortedBy { it.start.z } // this is our stable list

    // part 1: solutions: 5 / 463

    checkResult(463) { // [M3 63.755583ms]
        sortedBricks.withIndex().count { (i,brick) ->
            brick.removeFrom(tower)
            !sortedBricks.slice(i+1..<sortedBricks.size).any { it.fallIn(tower, justCheck = true) > 0 }
                .apply { brick.storeIn(tower, withCheck = false) } // restore
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (falling bricks)") }

    // part 2: solutions: 7 / 89727

    checkResult(89727) { // [M3 1.531593250s]
        val towerCopy = Space<Brick?>(tower.xDim,tower.yDim,tower.zDim) { _ -> null }
        sortedBricks.withIndex().sumOf { (i,brick) ->
            tower.copyTo(towerCopy)
            brick.removeFrom(tower)
            sortedBricks.slice(i+1..<sortedBricks.size).count { it.fallIn(tower, shouldArchive = true)>0 }
                .apply { // restore
                    towerCopy.copyTo(tower)
                    sortedBricks.slice(i+1..<sortedBricks.size).forEach { it.correctFromArchive() }
                }
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (chain reaction)") }
}
