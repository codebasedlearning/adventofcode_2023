// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/19

package dev.codebasedlearning.adventofcode.day19

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.toBlocks
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import java.util.ArrayDeque
import java.util.Queue

val examples = listOf(
"""
px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}
"""
)

fun main() {
    val story = object {
        val day = 19
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val (rulesBlock,partsBlock) = story.lines.toBlocks()

    data class Part(val attributes: Map<Char,Int>) {
        val rating get() = attributes.values.sum()
    }

    data class Rule(val attribute: Char, val op: String, val value: Int, val nextRule: String) {
        val hasNoConstraint get() = op.isEmpty()
        fun nextRuleOrNull(part: Part) = when {
            hasNoConstraint -> nextRule
            else -> part.attributes[attribute]!!.let { v -> if ((op == ">" && v > value) || (op == "<" && v < value)) nextRule else null }
        }
    }

    data class Workflow(val name: String, val rules: List<Rule>) {
        fun apply(p: Part) = rules.firstNotNullOfOrNull { it.nextRuleOrNull(p) } ?: throw RuntimeException("no target rule")
    }

    val xmas = "xmas" // the only attributes
    val startRule = "in"
    val (aRule,rRule) = "A" to "R"
    val regexLine = """(\w+)\{([^}]+)\}""".toRegex()
    val regexRule = """(\w+)(?:([<>=])(.*?):(\w+))?""".toRegex()
    val regexPart = """(\w+)=(\d+)""".toRegex()

    val workflows = rulesBlock.associate { line -> regexLine.matchEntire(line)!!.let { lineMatch ->
        val name = lineMatch.groupValues[1]
        val rules = lineMatch.groupValues[2].split(",").map { regexRule.matchEntire(it)!!.let { ruleMatch ->
            if (ruleMatch.groupValues[2].isEmpty()) Rule('-',"",0,ruleMatch.groupValues[1])
            else Rule(ruleMatch.groupValues[1][0],ruleMatch.groupValues[2],ruleMatch.groupValues[3].toInt(),ruleMatch.groupValues[4])
        } }
        name to Workflow(name,rules)
    } }

    val parts = partsBlock.map { line ->
        Part(regexPart.findAll(line).associate { match -> match.groupValues[1][0] to match.groupValues[2].toInt() })
    }

    // part 1: solutions: 19114 / 425811

    checkResult(425811) { // [M3 1.652709ms]
        parts.sumOf { part ->
            generateSequence(startRule) { rule -> workflows[rule]!!.apply(part) }
                .first { it == aRule || it == rRule }
                .let { if (it == aRule) part.rating else 0 }
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (rating numbers)") }

    // in{s<1351:px,qqz}               => s:[1..1350]->px, s:[1351..4000]->qqz
    // qqz{s>2770:qs,m<1801:hdj,R}     => s:[2771..4000]->qs, s:[1351..2770],m:[1..1800]->hdj

    // part 2: solutions: 167409079868000 / 131796824371749

    fun IntRange.intersect(other: IntRange): IntRange {
        val first = maxOf(this.first, other.first)
        val last = minOf(this.last, other.last)
        return if (first <= last) first..last else 0..-1 // isEmpty
    }

    data class Ranges(val attributes: Map<Char, IntRange>) {
        // for simplicity, we assume this to have all attributes ("xmas"), so this is not symmetrical
        fun merge(rg: Ranges) = Ranges(xmas.toList().associateWith { c ->
            rg.attributes[c]?.let { attributes[c]!!.intersect(it)} ?: attributes[c]!!
        })
        fun merge(rule: Rule): Pair<Ranges,Ranges> {
            val (rg1,rg2) = if (rule.op == "<") { 1..rule.value - 1 to rule.value..4000 }
            else { rule.value + 1..4000 to 1..rule.value }
            return merge(Ranges(mapOf(rule.attribute to rg1))) to merge(Ranges(mapOf(rule.attribute to rg2)))
        }
        val area: Long get() = attributes.values.let { values ->
            if (values.any { it.isEmpty() }) 0L else values.fold(1) { m,rg -> m * rg.count() }
        }
    }

    checkResult(131796824371749) { // [M3 10.513666ms]
        val accepted = mutableListOf<Ranges>() // just to have a look at it
        val queue: Queue<Pair<Ranges,String>> = ArrayDeque()

        queue.add(Ranges(xmas.toList().associateWith { 1..4000 }) to startRule)
        while (queue.isNotEmpty()) {
            var (ranges,name) = queue.remove()
            if (name == aRule) { accepted.add(ranges) }
            else if (name != rRule) { workflows[name]!!.rules.forEach {
                if (it.hasNoConstraint) { queue.add(ranges to it.nextRule) }
                else { ranges.merge(it).also { (r1,r2) -> queue.add(r1 to it.nextRule); ranges = r2 } }
            } }
        }
        accepted.sumOf { it.area }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (possible distinct combinations)") }
}
