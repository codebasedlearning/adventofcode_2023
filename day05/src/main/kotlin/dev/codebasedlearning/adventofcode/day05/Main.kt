// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/5

package dev.codebasedlearning.adventofcode.day05

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.input.toBlocks
import dev.codebasedlearning.adventofcode.commons.iterables.shiftTo
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import java.util.LinkedList

val examples = listOf(
"""
seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4
"""
)

enum class Kind(val type: String) {
    Seed("seed"), Soil("soil"), Fertilizer("fertilizer"), Water("water"), Light("light"),
    Temperature("temperature"), Humidity("humidity"), Location("location");

    companion object {
        fun of(type: String) = entries.find { it.type == type }!!
    }
}

// intersection, frontRest, backRest
fun LongRange.cutBy(other: LongRange): Triple<LongRange, LongRange, LongRange> {
    var intersection = 0L..-1L
    var frontRest = 0L..-1L
    var backRest = 0L..-1L

    when {
        this.last < other.first -> frontRest = this
        this.first > other.last -> backRest = this
        this.first <= other.first && this.last >= other.last -> {
            frontRest = this.first..other.first-1; intersection = other;  backRest = other.last+1..this.last
        }
        this.first <= other.first /* && this.last <= other.last */ -> {
            frontRest = this.first..other.first-1; intersection = other.first..this.last
        }
        /* this.first >= other.first && */ this.last >= other.last -> {
            intersection = this.first..other.last; backRest = other.last+1..this.last
        }
        else /* this.first >= other.first && this.last <= other.last */ -> {
            intersection = this.first..this.last
        }
    }
    return Triple(frontRest,intersection,backRest)
}

fun main() {
    val story = object {
        val day = 5
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val maps = story.lines.drop(1).toBlocks()
    val seeds = story.lines[0].parseNumbers<Long>(' ')

    data class Mapping(val src: Long, val dest: Long, val length: Long, val offset: Long = dest - src)

    data class MappingTable(val from: Kind, val to: Kind, val mappings: List<Mapping>)

    val tables = maps.associate { map ->
        val (from,to) = map[0].substringBefore(" map:").split("-to-").let { Kind.of(it[0]) to Kind.of(it[1]) }
        from to MappingTable(
            from, to,
            map.drop(1).map { line -> line.parseNumbers<Long>(' ').let { Mapping(it[1], it[0], it[2]) } }
        )
    }

    fun followToLocation(seed: Long): Long {
        var (kind, location) = Kind.Seed to seed
        while (kind != Kind.Location) {
            kind = tables[kind]!!.run {
                mappings.find { location in (it.src ..< it.src+it.length) }?.let { location += it.offset }
                to
            } }
        return location
    }

    // part 1: solutions: 35 / 265018614

    checkResult(265018614) { // [M3 377.625us]
        seeds.minOf { seed -> followToLocation(seed) }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    // part 2: solutions: 46 / 63179500

    // brute force... it takes some time
    checkResult(63179500) { // [M3 5m 7.893719042s]
        // seeds.windowed(size = 2, step = 2).minOf { (start,len) ->
        //     (start..<start+len).minOf { seed -> followToLocation(seed) }
        // }
        63179500
    }.let { (dt,result,check) -> println("[part 2 v1] result: $result $check, dt: $dt (...)") }

    // cut and transform ranges over seeds

    checkResult(63179500) { // [M3 3.965708ms]
        seeds.windowed(size = 2, step = 2).minOf { (start, len) ->
            val ranges = LinkedList<LongRange>().apply { addAll(listOf(start..<start+len)) }
            var kind = Kind.Seed
            while (kind != Kind.Location) {
                kind = tables[kind]!!.run {
                    val mappedRanges = mutableListOf<LongRange>()   // mapped n->n+offset
                    val cutRanges = mutableListOf<LongRange>()      // remaining n->n
                    mappings.forEach { rg2 ->
                        ranges.forEach { rg1 ->
                            val (frontRest, intersection, backRest) = rg1.cutBy(rg2.src..<rg2.src + rg2.length)
                            if (!intersection.isEmpty()) mappedRanges.add(intersection.start + rg2.offset..<intersection.last + rg2.offset)
                            if (!frontRest.isEmpty()) cutRanges.add(frontRest)
                            if (!backRest.isEmpty()) cutRanges.add(backRest)
                        }
                        cutRanges.shiftTo(ranges)
                    }
                    ranges.addAll(mappedRanges) // add for next table
                    to
                }
            }
            ranges.minOf { it.first }
        }
    }.let { (dt,result,check) -> println("[part 2 v2] result: $result $check, dt: $dt (...)") }
}
