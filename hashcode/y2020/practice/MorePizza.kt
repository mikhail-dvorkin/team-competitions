package hashcode.y2020.practice

import java.io.*
import java.util.*

private fun solve(m: Int, a: List<Int>): Set<Int> {
	val toLeave = a.sum() - m
	val can = IntArray(toLeave + a.max()!! + 1) { -1 }
	can[0] = 0
	var max = 0
	for (i in a.indices) {
		val v = a[i]
		for (s in minOf(max, toLeave) downTo 0) {
			if (can[s] >= 0 && s + v < can.size && can[s + v] == -1) {
				can[s + v] = i
			}
		}
		max += v
		if (can[toLeave] >= 0) break
	}
	var left = (toLeave until can.size).first { can[it] >= 0 }
	val take = a.indices.toMutableSet()
	while (left > 0) {
		take.remove(can[left])
		left -= a[can[left]]
	}
	return take
}

fun main() {
	val filenames = listOf("a_example.in", "b_small.in", "c_medium.in", "d_quite_big.in", "e_also_big.in")
	val dir = File(::main.javaClass.packageName.replace(".", "/"))
	for (fileIndex in 0..4) {
		val scanner = Scanner(File(dir, filenames[fileIndex]))
		val m = scanner.nextInt()
		val a = List(scanner.nextInt()) { scanner.nextInt() }
		val ans = solve(m, a)
		val out = PrintWriter(File(dir, filenames[fileIndex] + ".out"))
		out.println("${ans.size}\n${ans.joinToString(" ")}")
		out.close()
	}
}
