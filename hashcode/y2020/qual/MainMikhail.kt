package hashcode.y2020.qual

import java.io.*
import java.util.*
import kotlin.math.exp

private fun solve(filename: String) {
	val random = Random(566)
	var theBest = 0
	println(filename)
	val scanner = Scanner(File(filename))
	val books = scanner.nextInt()
	val libs = scanner.nextInt()
	val days = scanner.nextInt()
	val scores = IntArray(books) { scanner.nextInt() }
	val signups = IntArray(libs) { 0 }
	val speed = IntArray(libs) { 0 }
	val id = List(libs) { i ->
		val size = scanner.nextInt()
		signups[i] = scanner.nextInt()
		speed[i] = scanner.nextInt()
		List(size) { scanner.nextInt() }
	}
	val where = List(books) { mutableListOf<Int>() }
	for (i in id.indices) {
		for (x in id[i]) {
			where[x].add(x)
		}
	}
	var orderId = 0
	fun printOrder(order: List<Int>, doit: Boolean = false): Int {
		val out = if (doit) PrintWriter("$filename.$theBest.out") else null
		out?.println(order.size)
		var time = 0
		var gain = 0
		val scan = BooleanArray(books)
		for (i in order) {
			out?.println("$i ${id[i].size}")
			val scanned = id[i].sortedByDescending {
				if (scan[it]) return@sortedByDescending -1000000
				scores[it]
			}
			out?.println(scanned.joinToString(" "))
			time += signups[i]
			var t = time
			var inDay = 0
			for (x in scanned) {
				if (t < days) {
					if (!scan[x]) gain += scores[x]
					scan[x] = true
				}
				inDay++
				if (inDay == speed[i]) {
					t++
					inDay = 0
				}
			}
		}
		println("SCORE: $gain @$orderId")
		orderId++
		out?.close()
		if (gain > theBest) {
			theBest = gain
			printOrder(order, true)
		}
		return gain
	}

	@Suppress("unused")
	fun scoreFun(i: Int): Int {
		val scoreHere = id[i].map { x -> scores[x] }.sum()
//		scoreHere / signups[i]
		return (signups[i] * 9239 - scoreHere) * (20 - speed[i])
	}

	@Suppress("unused")
	fun shorten(order: List<Int>/*, add: Int = 0*/): List<Int> {
		var time = 0
		var normal = 0
		for (i in order) {
			time += signups[i]
			if (time >= days) break
			normal++
		}
		return order.take(normal)
	}

	@Suppress("unused")
	fun research(order: List<Int>) {
		println(order)
		println(order.map { signups[it] })
		println(order.map { speed[it] })
		println(order.map { id[it].size })
		val allBooks = order.map { id[it] }.flatten().toSet().toList()
		println(order.map { id[it] }.flatten().groupBy { it }.filter { it.value.size > 1 })
		println(allBooks.map { scores[it] }.sum())
		var o = order
		for (j in o.indices) {
			var best = 0
			var bestOrder = o
			for (i in 0..0) {
				val cur = printOrder(o)
				if (cur > best) {
					best = cur
					bestOrder = o
				}
				o = o.take(j) + o.drop(j).shuffled(random)
			}
			o = bestOrder
			println(best)
		}
	}

//    val orderLong = id.indices.sortedBy { scoreFun(it) }
	val orderLong = readOutput("$filename.base.out")
//    val orderShort = shorten(orderLong, 10)
//    printOrder(orderShort)
//    research(orderShort)

	@Suppress("unused")
	fun printStats() {
		println("$libs libs")
		println("${signups.avg()} avg signup")
		println("${speed.avg()} avg speed")
		println("${id.map { it.size }.avg()} avg lib size")
		println("${where.map { it.size }.avg()} avg occurrences")
		println("${where.map { it.size }.max()} max occurrences")
	}

	fun vary(a: List<Int>, r: Random): List<Int> {
		val b = a.toMutableList()
		val i = r.nextInt(b.size)
		val j = r.nextInt(b.size)
		val t = b[i]; b[i] = b[j]; b[j] = t
		return b
	}

	fun simulatedAnnealing(itemGiven: List<Int>, settings: Settings, r: Random): List<Int> {
		var item = itemGiven
		var energy = -printOrder(item)
		var answerEnergy = Int.MAX_VALUE
		var answer: List<Int> = item
		for (glob in 0 until settings.globalIterations) {
			if (glob > 0 && r.nextDouble() >= settings.probStartWithPrevious) {
				item = item.shuffled(r)
				energy = -printOrder(item)
			}
			var end = settings.iterations
			var iter = 1
			var recession = 0
			while (true) {
				if (energy < answerEnergy) {
					answerEnergy = energy
					answer = item
					if (answerEnergy <= settings.desiredEnergy) {
						return answer
					}
					end = maxOf(end, iter + settings.iterations)
				}
				if (iter > end) {
					break
				}
				val next = vary(item, r)
				val nextEnergy = -printOrder(next)
				val dEnergy = nextEnergy - energy
				var accept: Boolean
				if (dEnergy < 0) {
					accept = true
					recession = 0
				} else {
					recession++
					if (recession == settings.recessionLimit) {
						break
					}
					val barrier = exp(-1.0 * dEnergy * iter / settings.temp0)
					accept = r.nextDouble() < barrier
				}
				if (accept) {
					item = next
					energy = nextEnergy
				}
				iter++
			}
		}
		return answer
	}

	simulatedAnnealing(orderLong.take(160), Settings(), random)
}

class Settings @JvmOverloads constructor(var globalIterations: Int = 1024, var iterations: Int = 8192, var probStartWithPrevious: Double = 1 - 1.0 / 16, var recessionLimit: Int = Int.MAX_VALUE, var desiredEnergy: Double = -Double.MAX_VALUE, var temp0: Double = 1.0)

private fun readOutput(filename: String): MutableList<Int> {
	val scanner = Scanner(File(filename))
	val res = mutableListOf<Int>()
	repeat(scanner.nextInt()) {
		res.add(scanner.nextInt())
		repeat(scanner.nextInt()) { scanner.nextInt() }
	}
	return res
}

private fun List<Int>.avg(): Double = this.sum() * 1.0 / this.size
private fun IntArray.avg(): Double = this.sum() * 1.0 / this.size

fun main() {
	val filenames = listOf("a_example.txt", "b_read_on.txt", "c_incunabula.txt", "d_tough_choices.txt", "e_so_many_books.txt", "f_libraries_of_the_world.txt")
	for (fileIndex in 4..4) {
//	for (fileIndex in 5..5) {
		solve(filenames[fileIndex])
	}
}
