package hashcode.y2021.qual

import java.util.*
import java.io.*

private fun Simulation.accordingToFrequencies(period: Int = 32): List<Int>? {
	for (inter in 0 until nInter) {
		val streets = inbound[inter]
		val frequencies = streets.map { freq[it] }
		val freqSum = frequencies.sum()
		val seconds = frequencies.map { f ->
			if (f == 0) 0 else maxOf(f * period / freqSum, 1)
		}
		println(seconds)
	}
	return null
}

//fun newDay() {
//	val accordingToFrequencies = accordingToFrequencies()
//
//}

private fun Simulation.solve() {
	accordingToFrequencies()
}

fun main() {
	listOf("a", "b", "c", "d", "e", "f").forEach { solve(it) }
}

private fun solve(f: String) {
	val inFile = "$f.txt";
	val outFile = "${f}_out.txt"
	val sim = Simulation()
	Simulation.`in` = BufferedReader(InputStreamReader(FileInputStream(inFile)))
	sim.input()
	sim.solve()
	Simulation.`in` = BufferedReader(InputStreamReader(FileInputStream(outFile)))
	val score = sim.simulate()
	println(score)
	println("-----")
}
