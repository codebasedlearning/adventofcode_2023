// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/25

package dev.codebasedlearning.adventofcode.day25

import dev.codebasedlearning.adventofcode.commons.graph.Graph
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
jqt: rhn xhk nvd
rsh: frs pzl lsr
xhk: hfx
cmg: qnr nvd lhk bvb
rhn: xhk bvb hfx
bvb: xhk hfx
pzl: lsr hfx nvd
qnr: nvd
ntq: jqt hfx bvb xhk
nvd: lhk
lsr: lhk
rzs: qnr cmg lsr rsh
frs: qnr lhk lsr
"""
)

fun main() {
    val story = object {
        val day = 25
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val components = Graph<String>()
    story.lines.forEach { line ->
        val (comp, connected) = line.split(": ")
        connected.split(" ").forEach { c -> components.add(from=comp, to=c.trim())}
    }
    // println(components)

    // part 1: solutions: 54 / 596376

    println("cycle sizes before: ${components.findCycleComponents().map { it.size }}")
    checkResult(596376) { // [M3 523us]
        val (minCut3, success) = components.repeatedKargerMinCut(targetSize = 3, trials = 1000)
        if (!success) {
            println("cut: $minCut3")
            minCut3.forEach { components.remove(it.first, it.second) }
            components.findCycleComponents().run {
                println("cycle sizes after: ${map { it.size }}")
                fold(1) { acc, cycle -> acc * cycle.size }
            }
        } else -1
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (components are connected)") }

    // no part 2 as it is the last in 2023
}

// not sure if it goes to commons... maybe Stoer–Wagner is also a candidate

fun <T> Graph<T>.findCycleComponents(): List<List<T>> {
    val visited = mutableSetOf<T>()
    val cycleComponents = mutableListOf<List<T>>()

    fun dfsCollectComponent(node: T, component: MutableList<T>) {
        visited.add(node)
        component.add(node)

        connections[node]?.keys?.forEach { neighbor ->
            if (neighbor !in visited) {
                dfsCollectComponent(neighbor, component)
            }
        }
    }

    fun containsCycle(component: List<T>): Boolean {
        val nodeSet = component.toSet()
        val localVisited = mutableSetOf<T>()

        fun dfs(node: T, parent: T?): Boolean {
            localVisited.add(node)

            connections[node]?.keys?.forEach { neighbor ->
                if (neighbor != parent) {
                    // cycle detected
                    if (neighbor in localVisited) return true
                    if (neighbor in nodeSet && neighbor !in localVisited) {
                        if (dfs(neighbor, node)) return true
                    }
                }
            }
            return false
        }
        return component.any { node -> node !in localVisited && dfs(node, null) }
    }

    for (v in connections.keys) {   // find connected components
        if (v !in visited) {
            val component = mutableListOf<T>()
            dfsCollectComponent(v, component)
            if (containsCycle(component)) {
                cycleComponents.add(component)
            }
        }
    }

    return cycleComponents
}

fun <T> Graph<T>.kargerMinCut(): Set<Pair<T, T>> {
    val tmpCopy = connections.entries.flatMap { it.value.keys.map { k-> it.key to k } }.toMutableList()
    val edgesCopy = mutableSetOf<Pair<T, T>>().apply { tmpCopy.forEach { (a, b) -> if (!this.contains(Pair(a,b)) && (b to a) !in this) add(a to b) } }

    val parent = connections.keys.associate { it to it }.toMutableMap()
    val size = connections.keys.associateWith { 1 }.toMutableMap()

    fun find(v: T): T { // find root
        if (parent[v] == v) return v
        parent[v] = find(parent[v]!!)
        return parent[v]!!
    }

    fun union(u: T, v: T) { // merge two components
        val rootU = find(u)
        val rootV = find(v)
        if (rootU != rootV) {
            if (size[rootU]!! > size[rootV]!!) {
                parent[rootV] = rootU
                size.getOrPut(rootU) { 0 }
                size[rootU] = size[rootU]!! + size[rootV]!!
            } else {
                parent[rootU] = rootV
                size[rootV] = size[rootV]!! + size[rootU]!!
            }
        }
    }

    var remainingVertices = connections.keys.size

    while (remainingVertices > 2) {
        val (u, v) = edgesCopy.random() // sometimes it works, sometimes not

        val rootU = find(u)
        val rootV = find(v)
        if (rootU != rootV) {
            union(rootU, rootV) // contract the edge, basic idea of the algorithm
            remainingVertices--
        }

        edgesCopy.removeAll { (a, b) -> find(a) == find(b) } // remove self-loops
    }
    return edgesCopy
}

fun <T> Graph<T>.repeatedKargerMinCut(targetSize: Int = Int.MAX_VALUE, trials: Int = 50): Pair<Set<Pair<T, T>>,Boolean> {
    var cnt = 0
    var minCut = kargerMinCut()

    while(minCut.size > targetSize && cnt < trials) {
        minCut = kargerMinCut()
        cnt++
    }
    return minCut to (minCut.size > targetSize)
}
