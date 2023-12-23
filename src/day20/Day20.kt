package day20

import java.io.File

fun main() {
    val data = parse("src/day20/Day20.txt")

    println("🎄 Day 20 🎄")

    println()

    println("[Part 1]")
    println("Answer: ${part1(data)}")

    println()

    println("[Part 2]")
    println("Answer: ${part2(data)}")
}

private data class Configuration(
    val prefix: Char,
    val source: String,
    val targets: List<String>,
)

private fun String.toConfiguration(): Configuration {
    val (lhs, rhs) = this.split(" -> ")

    val prefix = lhs.first()
    val source = lhs.trim('%', '&')
    val targets = rhs.split(", ")

    return Configuration(prefix, source, targets)
}

private fun parse(path: String): List<Configuration> =
    File(path)
        .readLines()
        .map(String::toConfiguration)

private data class Pulse(
    val source: String,
    val target: String,
    val value: Boolean,
)

private abstract class Module(protected val name: String) {

    protected val inputs: MutableList<String> = mutableListOf()
    protected val outputs: MutableList<String> = mutableListOf()

    abstract fun receive(pulse: Pulse): List<Pulse>

    fun inputs(): List<String> = inputs.toList()

    fun outputs(): List<String> = outputs.toList()

    open fun connectInput(name: String) {
        inputs.add(name)
    }

    open fun connectOutput(name: String) {
        outputs.add(name)
    }

}

private class FlipFlop(name: String) : Module(name) {

    private var value: Boolean = false

    override fun receive(pulse: Pulse): List<Pulse> {
        if (pulse.value) {
            return listOf()
        }

        value = !value
        return outputs.map { target -> Pulse(name, target, value) }
    }

}

private class Conjunction(name: String) : Module(name) {

    private val states: MutableMap<String, Boolean> = mutableMapOf()

    override fun connectInput(name: String) {
        super.connectInput(name)
        states[name] = false
    }

    override fun receive(pulse: Pulse): List<Pulse> {
        states[pulse.source] = pulse.value
        val value = states.all { (_, v) -> v }
        return outputs.map { target -> Pulse(name, target, !value) }
    }

}

private class Broadcaster(name: String) : Module(name) {

    override fun receive(pulse: Pulse): List<Pulse> {
        return outputs.map { target -> Pulse(name, target, pulse.value) }
    }

}

private fun part1(data: List<Configuration>): Int {
    val modules = mutableMapOf<String, Module>()
    for ((prefix, source, _) in data) {
        when (prefix) {
            '%' -> modules[source] = FlipFlop(source)
            '&' -> modules[source] = Conjunction(source)
            'b' -> modules[source] = Broadcaster(source)
        }
    }

    for ((_, source, targets) in data) {
        for (target in targets) {
            modules[source]?.connectOutput(target)
            modules[target]?.connectInput(source)
        }
    }

    var hi = 0
    var lo = 0

    repeat(1000) {
        val queue = ArrayDeque<Pulse>()
            .apply { add(Pulse(source = "button", target = "broadcaster", value = false)) }

        while (queue.isNotEmpty()) {
            val pulse = queue.removeFirst()
            val target = modules[pulse.target]
            if (target != null) {
                queue.addAll(target.receive(pulse))
            }

            if (pulse.value) {
                hi += 1
            } else {
                lo += 1
            }
        }
    }

    return hi * lo
}

private fun lcm(a: Long, b: Long): Long {
    var n1 = a
    var n2 = b
    while (n2 != 0L) {
        n1 = n2.also { n2 = n1 % n2 }
    }
    return (a * b) / n1
}

private fun part2(data: List<Configuration>): Long {
    val modules = mutableMapOf<String, Module>()
    for ((prefix, source, _) in data) {
        when (prefix) {
            '%' -> modules[source] = FlipFlop(source)
            '&' -> modules[source] = Conjunction(source)
            'b' -> modules[source] = Broadcaster(source)
        }
    }

    for ((_, source, targets) in data) {
        for (target in targets) {
            modules[source]?.connectOutput(target)
            modules[target]?.connectInput(source)
        }
    }

    val (terminal) = modules.mapNotNull { (name, module) ->
        if ("rx" in module.outputs()) name else null
    }

    val feed = modules.mapNotNull { (name, module) ->
        if (terminal in module.outputs()) name else null
    }

    val periods = mutableMapOf<String, Long>()

    var presses = 0L
    while (true) {
        presses += 1L

        val queue = ArrayDeque<Pulse>()
            .apply { add(Pulse(source = "button", target = "broadcaster", value = false)) }

        while (queue.isNotEmpty()) {
            val pulse = queue.removeFirst()

            if (pulse.source in feed && pulse.value) {
                periods[pulse.source] = presses
                if (periods.size == feed.size) {
                    return periods.values.reduce(::lcm)
                }
            }

            val target = modules[pulse.target]
            if (target != null) {
                queue.addAll(target.receive(pulse))
            }
        }
    }
}
