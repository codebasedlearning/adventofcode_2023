// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2023/day/20

package dev.codebasedlearning.adventofcode.day20

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.math.scm
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import java.util.ArrayDeque
import java.util.Queue

val examples = listOf(
"""
broadcaster -> a, b, c
%a -> b
%b -> c
%c -> inv
&inv -> a
""",
"""
broadcaster -> a
%a -> inv, con
&inv -> b
%b -> con
&con -> output
""")

fun main() {
    val story = object {
        val day = 20
        val year = 2023
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    data class Pulse(val source: String, val target: String, val value: Boolean)

    abstract class Module(val name: String, val outputs: List<String>) {
        var state = false
        fun assignAndSend(value: Boolean): List<Pulse> {
            state = value;
            return outputs.map { Pulse(name, it, value = value) }
        }
        abstract fun apply(pulse: Pulse): List<Pulse>
        open fun reset() {}
    }

    class Broadcast(outputs: List<String>) : Module("broadcaster", outputs) {
        override fun apply(pulse: Pulse) = assignAndSend(pulse.value)
    }

    class FlipFlop(name: String, outputs: List<String>) : Module(name, outputs) {
        override fun apply(pulse: Pulse): List<Pulse> = when {
            pulse.value -> listOf()
            else -> assignAndSend(state.not())
        }
        override fun reset() { state = false }
    }

    class Conjunction(name: String, outputs: List<String>) : Module(name, outputs) {
        val inputs = mutableMapOf<String, Boolean>()
        override fun apply(pulse: Pulse): List<Pulse> {
            inputs[pulse.source] = pulse.value
            return assignAndSend(!inputs.values.all { it })
        }
        override fun reset() { inputs.keys.forEach { inputs[it] = false} }
    }

    class Output(name: String) : Module(name, listOf()) {
        override fun apply(pulse: Pulse) = listOf<Pulse>()
    }

    fun moduleOf(line: String): Module {
        val (nameToken,targetTokens) = line.split(" -> ")
        val targets = targetTokens.split(",").map { it.trim() }
        return when {
            nameToken=="broadcaster" -> Broadcast(targets)
            nameToken.startsWith("&") -> Conjunction(nameToken.substring(1),targets)
            nameToken.startsWith("%") -> FlipFlop(nameToken.substring(1),targets)
            else -> throw Exception("Unknown module name: $nameToken")
        }
    }

    val modules = story.lines.associate { line -> moduleOf(line).let { it.name to it } }
        .toMutableMap()
        .apply {
            values.forEach { module -> module.outputs.filter { this[it] is Conjunction }
                .forEach { (this[it] as Conjunction).inputs[module.name] = false } } }
        .apply {
            values.flatMap { module -> module.outputs.filter { it !in this } }
                .forEach { this[it] = Output(it) }
        }

    fun pressButton(repeats: Int, stopAction: (Int, Pulse) -> Boolean) {
        repeat(repeats) { cnt ->
            val queue: Queue<Pulse> = ArrayDeque()
            queue.add(Pulse("button", "broadcaster", value = false))
            while (queue.isNotEmpty()) {
                val pulse = queue.remove()
                queue.addAll(modules[pulse.target]!!.apply(pulse))
                if (stopAction(cnt,pulse)) return
            }
        }
    }

    // part 1: solutions: 32000000/11687500 / 980457412

    checkResult(980457412) { // [M3 19.184166ms]
        var (lows,highs) = 0L to 0L
        pressButton(1000) { _, pulse ->
            if (pulse.value) highs++ else lows++;
            false
        }
        lows * highs
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (pulses)") }

// my input:
//      &zc -> kl
//      &mk -> kl
//      &fp -> kl
//      &xt -> kl
//          &kl -> rx
//
// assume, we have a number of sub-graphs leading into final Conjunction "kl" feeding "rx",
// then a low on these lead to a high in "kl" and a final low on "rx";
// assume also, that there are cycles starting from the beginning of the button sequences...

    // part 2: solutions: - / 232774988886497

    if (story.example>0) return@main

    checkResult(232774988886497) { // [M3 6.750792ms]
        val kl = modules.values.first { "rx" in it.outputs } as Conjunction
        val cycles = kl.inputs.keys.map { modules[it]!! as Conjunction }.associateWith { 0L }.toMutableMap()

        modules.values.forEach { it.reset() }

        pressButton(100000) { cnt, pulse ->
            if (cnt > 0) {
                cycles.filter { it.value == 0L }
                    .filter { it.key.inputs.values.any { input -> !input } }
                    .forEach { cycles[it.key] = cnt + 1L }
            }
            (cycles.values.all { it > 0L })
        }
        scm(cycles.values)
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (low pulse to rx)") }
}
