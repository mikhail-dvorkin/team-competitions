package hashcode.y2021.practice;

import java.io.*
import java.util.*

private fun solve(teamsCount: List<Int>, pizzas: List<List<String>>): List<List<Int>> {
	val p = pizzas.withIndex().sortedBy { it.value.size }.toMutableList()
	val t = teamsCount.toMutableList()
	val ans = mutableListOf<List<Int>>()
	while (true) {
		val teamSize = t.indices.lastOrNull { t[it] > 0 && p.size >= it } ?: break
		t[teamSize]--
		ans.add(p.takeLast(teamSize).map { it.index })
		repeat(teamSize) { p.removeLast() }
	}
	return ans
}

class Clazz{}

fun main() {
	val filenames = listOf("a_example.in", "b_little_bit_of_everything.in", "c_many_ingredients.in", "d_many_pizzas.in", "e_many_teams.in")
	val dir = File(::Clazz.javaClass.packageName.replace(".", "/"))
	for (fileIndex in 0..4) {
		val scanner = Scanner(File(dir, filenames[fileIndex]))
		val m = scanner.nextInt()
		val teamsCount = listOf(0, 0) + List(3) { scanner.nextInt() }
		val pizzas = List(m) { List(scanner.nextInt()) { scanner.next() } }
		val ans = solve(teamsCount, pizzas)
		val out = PrintWriter(File(dir, filenames[fileIndex].removeSuffix(".in") + ".out"))
		out.println(ans.size)
		for (list in ans) out.println("${list.size} ${list.joinToString(" ")}")
		out.close()
	}
}
