// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/23

package dev.codebasedlearning.adventofcode.day23

import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.geometry.plus
import dev.codebasedlearning.adventofcode.commons.geometry.walkCardinals
import dev.codebasedlearning.adventofcode.commons.grid.inGrid
import dev.codebasedlearning.adventofcode.commons.grid.mapKeysToDir
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
#.#####################
#.......#########...###
#######.#########.#.###
###.....#.>.>.###.#.###
###v#####.#v#.###.#.###
###.>...#.#.#.....#...#
###v###.#.#.#########.#
###...#.#.#.......#...#
#####.#.#.#######.#.###
#.....#.#.#.......#...#
#.#####.#.#.#########v#
#.#...#...#...###...>.#
#.#.#v#######v###.###v#
#...#.>.#...>.>.#.###.#
#####v#.#.###v#.#.###.#
#.....#...#...#.#.#...#
#.#########.###.#.#.###
#...###...#...#...#.###
###.###.#.###v#####v###
#...#...#.#.>.>.#.>.###
#.###.###.#.###.#.#v###
#.....###...###...#...#
#####################.#
"""
)

fun main() {
    val story = object {
        val day = 23
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val hike = story.lines.toGrid()
    val start = Position(0,hike.data[0].indexOfFirst { it != '#' })
    val end = Position(hike.rows-1,hike.data[hike.rows-1].indexOfFirst { it != '#' })
    println("Start: $start, End: $end")

    // part 1: solutions: 94 / 2334

    checkResult(2334) { // [M3 102.385834ms]
        findLongestPath(start,end) { pos ->
            if (hike[pos] in mapKeysToDir) listOf(pos+mapKeysToDir[hike[pos]]!! to 1)
            else pos.walkCardinals().inGrid(hike).filter { it.value!='#' }.map { it.pos to 1 }.toList()
        }.size
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (hiking)") }

    // part 2: solutions: 154 / 6422

    checkResult(6422) { // [M3 2.176487875s]
        // there are a lot of narrow paths, i.e. with no junctions, so the idea is to "compress"
        // the map and build edges by connecting only the junctions;
        // note that we must track the length of the path as it is not simply the distance between
        val neighbors = hike.positions.filter { hike[it]!='#' }.associateWith { pos ->
            pos.walkCardinals().inGrid(hike).filter { it.value!='#' }.map { ngPos ->
                var (pPrev,p) = pos to ngPos.pos // follow the narrow path
                var length = 1
                do {
                    val path = p.walkCardinals().inGrid(hike).filter { it.pos!=pPrev && it.value!='#' }.map { it.pos }.toList()
                    if (path.size==1) { pPrev = p; p = path[0]; length++ }
                } while (path.size==1)
                p to length
            }.toList()
        }
        findLongestPath(start,end) { pos -> neighbors[pos]!! }.toMutableList()
            .apply { add(end) }
            .zipWithNext { a,b -> neighbors[a]!!.find { it.first==b }!!.second }.sum()
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (nice hiking)") }
}

fun longestPath(current: Position, end: Position,
                neighbors: (Position) -> List<Pair<Position, Int>>,
                longestPath: MutableList<Position>,
                visited: MutableSet<Position> = mutableSetOf<Position>(),
                currentPath: MutableList<Position> = mutableListOf<Position>(),
                currentWeight: Int = 0,
                maxWeight: MutableMap<MutableList<Position>, Int> = mutableMapOf<MutableList<Position>, Int>()
) {
    if (current == end) {
        if (currentWeight > (maxWeight[longestPath] ?: Int.MIN_VALUE)) {
            longestPath.clear()
            longestPath.addAll(currentPath)
            maxWeight[longestPath] = currentWeight
        }
        return
    }

    if (current in visited) return // for cycles

    visited.add(current)
    currentPath.add(current)
    for ((neighbor, weight) in neighbors(current)) {
        longestPath(neighbor, end, neighbors, longestPath, visited, currentPath, currentWeight + weight, maxWeight)
    }
    visited.remove(current) // restore
    currentPath.removeAt(currentPath.size - 1)
}

fun findLongestPath(start: Position, end: Position,
                    neighbors: (Position) -> List<Pair<Position, Int>>): List<Position>
    = mutableListOf<Position>().apply { longestPath(start, end, neighbors, this) }
