// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/18

package dev.codebasedlearning.adventofcode.day18

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
R 6 (#70c710)
D 5 (#0dc571)
L 2 (#5713f0)
D 2 (#d2c081)
R 2 (#59c680)
D 2 (#411b91)
L 5 (#8ceee2)
U 2 (#caa173)
L 1 (#1b58a2)
U 2 (#caa171)
R 2 (#7807d2)
U 3 (#a77fa3)
L 2 (#015232)
U 2 (#7a21e3)
""",
"""
R 3 (#70c710)
D 3 (#70c710)
L 3 (#70c710)
U 3 (#70c710)
""".trimIndent()
)
/*
  ####
  #  #
  #  #
  ####
 */
fun main() {
    val story = object {
        val day = 18
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val regexLine = """(.)\s(\d+)\s\(#([a-fA-F0-9]{6})\)""".toRegex()
    val tripleData = story.lines.map { line ->
        regexLine.find(line)!!.destructured.run { Triple(component1(), component2(), component3()) }
    }

    data class Edge(val dir: Direction, val len: Long, val color: String)

    fun cubicMetersFromPickAndShoelace(edges: List<Edge>): Long {
        val n = edges.size
        var perimeter = 0L
        // simply provide points for indices 0 and n+1 as the sum goes from 1...n using i-1 and i+1
        val points = mutableListOf<Pair<Long,Long>>().apply {
            var (x,y) = 0L to 0L
            edges.forEach { border ->
                add(x to y)
                x += border.len.toLong() * border.dir.dCol.toLong() // Position and Direction use Int
                y += border.len.toLong() * border.dir.dRow.toLong() // so we do this manually
                perimeter += border.len.toLong()
            }
            add(0,this.last())  // P_0 = P_n
            add(x to y)         // P_n+1 = P_1
        }
        // shoelace: https://en.wikipedia.org/wiki/Shoelace_formula
        val area = (1..n).sumOf { i -> points[i].first * (points[i+1].second - points[i-1].second) } / 2
        // pick: https://en.wikipedia.org/wiki/Pick's_theorem
        val interior = area - perimeter / 2 + 1
        return interior + perimeter
    }

    // part 1: solutions: 62 / 62365

    checkResult(62365) { // [M3 5.546459ms]
        val toDir1 = mapOf("R" to Direction.Right, "L" to Direction.Left, "U" to Direction.Up, "D" to Direction.Down)
        val edges1 = tripleData.map { (dir, len, color) -> Edge(toDir1[dir]!!, len.toLong(), color) }
        cubicMetersFromPickAndShoelace(edges1)
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (lava meters)") }

    // part 2: solutions: 952408144115 / 159485361249806

    checkResult(159485361249806) { // [M3 1.740208ms]
        val toDir2 = mapOf("0" to Direction.Right, "2" to Direction.Left, "3" to Direction.Up, "1" to Direction.Down)
        val edges2 = tripleData.map { (_, _, color) ->
            Edge(dir=toDir2[color.substring(5..<6)]!!,len=color.substring(0..4).toLong(16),"")
        }
        cubicMetersFromPickAndShoelace(edges2)
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (lava lagoon)") }
}
