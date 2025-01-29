// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/17

package dev.codebasedlearning.adventofcode.day17

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.geometry.walk
import dev.codebasedlearning.adventofcode.commons.graph.ShortestPaths
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import java.util.PriorityQueue

val examples = listOf(
"""
2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533
""",
"""
111111111111
999999999991
999999999991
999999999991
999999999991
"""
)

fun main() {
    val story = object {
        val day = 17
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val grid = story.lines.toGrid<Long> { it.digitToInt().toLong() }
    val startPos = Position(0,0)
    val endPos = Position(grid.rows-1,grid.cols-1)

    // node with state in order to take past directions into account
    data class StateNode(val pos: Position, val dir: Direction, val steps: Int)

    // adjusted from commons -> needs to be refactored or unified
    fun <T> findShortestPathsDijkstra(start: T,
                                      neighbors: (T) -> Sequence<Pair<T, Long>>
    ): ShortestPaths<T> {
        val distances = mutableMapOf<T, Long>()
        val predecessors = mutableMapOf<T,T?>()
        val priorityQueue = PriorityQueue<Pair<T, Long>>(compareBy { it.second })

        distances[start] = 0L
        priorityQueue.add(Pair(start, 0L))

        while (priorityQueue.isNotEmpty()) {
            val (current, distance) = priorityQueue.poll()

            if (distance > distances.getOrDefault(current,Long.MAX_VALUE)) continue

            // instead of connections
            neighbors(current).forEach { (neighbor, weight) ->
                val newDistance = distance + weight
                if (newDistance < distances.getOrDefault(neighbor,Long.MAX_VALUE)) {
                    distances[neighbor] = newDistance
                    priorityQueue.add(Pair(neighbor, newDistance))
                    predecessors[neighbor] = current
                }
            }
        }
        return ShortestPaths(start, distances, predecessors)
    }

    // part 1: solutions: 102/59 / 916

    checkResult(916) { // [M3 348.466416ms]
        findShortestPathsDijkstra(start = StateNode(startPos, Direction.Right, 0)) { node ->
            node.pos.walk(Direction.Cardinals).filter {
                it.pos in grid && !node.dir.isOpposite(it.dir)
                        && (it.dir!=node.dir || node.steps<3)
            }.map {
                StateNode(it.pos, it.dir, if (it.dir == node.dir) node.steps + 1 else 1) to grid[it.pos]
            }
        }.distances.filter { it.key.pos==endPos }.minBy { it.value }.value
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (minimize heat loss)") }

    // part 2: solutions: 94/71 / 1067

    checkResult(1067) { // [M3 1.054771625s]
        findShortestPathsDijkstra(start = StateNode(startPos, Direction.Right /* or Down */, 0)) { node ->
            node.pos.walk(Direction.Cardinals).filter { it.pos in grid && !node.dir.isOpposite(it.dir)
                    && ( (it.dir!=node.dir && (node.steps==0 /* for start */ || node.steps>=4)) || (it.dir==node.dir && node.steps<10))
            }.map {
                StateNode(it.pos, it.dir, if (it.dir == node.dir) node.steps + 1 else 1) to grid[it.pos]
            }
        }.distances.filter { it.key.pos==endPos && it.key.steps>=4 }.minBy { it.value }.value
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (ultra crucible)") }
}
