package hashcode.y2020.qual

import java.io.File
import java.io.PrintWriter
import java.util.*

val filenames = listOf(
		"a_example.txt",
		"b_read_on.txt",
		"c_incunabula.txt",
		"d_tough_choices.txt",
		"e_so_many_books.txt",
		"f_libraries_of_the_world.txt"
).associateBy { it[0] }.toMap()

data class Library(
		val id: Int,
		val setupTime: Int, // setup
		val booksPerDay: Int, // books per day
		val books: List<Book>
)

data class Book(val id: Int, val score: Int)

data class Sol(val lib: Library, val books: List<Book>)

fun doSolve(libs: Array<Library>, days: Int, save: List<Sol>.() -> Unit): List<Sol> {

//    val l = libs.toList().sortedBy { -it.books.size }.toMutableSet()

	val b2l = mutableMapOf<Book, MutableList<Library>>()

	libs.forEach { l ->
		l.books.forEach {
			b2l.getOrPut(it) { mutableListOf() } += l
		}
	}

	libs.sortBy { it.hashCode() }

	val cnt = IntArray(libs.flatMap { it.books }.maxBy { it.id }!!.id + 1)
	val lCnt = IntArray(libs.size)

	libs.forEach {
		lCnt[it.id] = it.books.size
	}

//    val u = mutableSetOf<Book>()

//    val result = mutableListOf<Sol>()

	var d = 0

	val taken = mutableSetOf<Library>()

	while (true) {

		val lib = libs.maxBy { lCnt[it.id] }!!

		println("$d ${lib.id}")

		d += lib.setupTime

		if (d >= days) break

//        result += Sol(lib, lib.books.filter { cnt[it.id] == 0 })

		taken += lib

//        u += lib.books

		lib.books.forEach {
			if (cnt[it.id] == 0) {
				b2l[it]!!.forEach { i -> lCnt[i.id]-- }
			}
			cnt[it.id]++
		}
	}


	fun buildSol(): List<Sol> {

		val result = mutableListOf<Sol>()

		println("Building solution")

		libs.forEach {
			lCnt[it.id] = it.books.size
		}

		val nu = mutableSetOf<Book>()

		val nt = taken.toMutableSet()

		while (nt.isNotEmpty()) {

			val lib = nt.maxBy { lCnt[it.id] }!!

			result += Sol(lib, lib.books.filter { it !in nu })

			nt -= lib

			lib.books.forEach {
				if (it !in nu) {
					b2l[it]!!.forEach { i ->
						lCnt[i.id]--
					}
				}
			}

			nu += lib.books
		}

		println(result.size)

		return result
	}

	var allBooks = cnt.count { it > 0 }

	var max = 0

	outer@ while (true) {
		var foundBetter = false

		for (i in libs.indices) {
			val lib = libs[i]

			if (i % 1000 == 0) {
				if (cnt.count { it > 0} != allBooks) {
					error("1")
				}
				println(allBooks)

				if (allBooks > max) {
					buildSol().save()
					max = allBooks
				}
			}

//            if (allBooks > 78000) break@outer

			if (lib in taken) continue

			for (t in taken) {
				var b = 0

				(t.books - lib.books).forEach {
					if (cnt[it.id] == 1) --b
				}

				(lib.books - t.books).forEach {
					if (cnt[it.id] == 0) ++b
				}

				if (b > 0) {
					t.books.forEach {
						--cnt[it.id]
					}
					lib.books.forEach {
						++cnt[it.id]
					}

					taken -= t
					taken += lib
					foundBetter = true
					allBooks += b
					break
				}
			}
		}

		if (!foundBetter) break
	}

	return buildSol()
//    return libs.map { Sol(it, it.books.reversed())}
//    return libs.map { Sol(it, it.books) }
}


private fun solve(fName: String) {
	val scanner = Scanner(File(fName))
	val b = scanner.nextInt()
	val l = scanner.nextInt()
	val d = scanner.nextInt()

	val books = (0 until b).map { it to Book(it, scanner.nextInt()) }.toMap()
	val libs = Array(l) { scanner.readLibrary(it, books) }

//    val sol = listOf<Sol>(Sol(libs[1], listOf(books[5]!!, books[2]!!, books[3]!!)),
//        Sol(libs[0], listOf(books[0]!!, books[1]!!, books[2]!!, books[3]!!, books[4]!!)))

	val sol = doSolve(libs, d) {
		printScore(d, libs)
		PrintWriter(File("$fName.out")).use { out ->
			printTo(out)
		}
	}

	sol.printScore(d, libs)

	PrintWriter(File("$fName.out")).use { out ->
		sol.printTo(out)
	}
}

fun List<Sol>.printTo(out: PrintWriter) {
	out.println(size)
	forEach {
		out.println("${it.lib.id} ${it.books.size}")
		out.println(it.books.joinToString(separator = " ") { i -> i.id.toString() })
	}
}

fun List<Sol>.printScore(days: Int, libs: Array<Library>) {
	var day = 0
	var score = 0

	val scanned = mutableSetOf<Book>()

	forEach {
		day += it.lib.setupTime
		var td = day
		var c = it.lib.booksPerDay

		for (b in it.books) {

			if (b !in it.lib.books) {
				error("Book not in library ${it.lib.id}: ${b.id}")
			}

			if (td < days && b !in scanned) {
				score += b.score
				scanned += b
			}

			if (--c == 0) {
				++td
				c = it.lib.booksPerDay
			}
		}
	}

	var lostScore = 0

	libs.flatMap { it.books }.forEach {
		if (it !in scanned) {
			lostScore += it.score
			scanned += it
		}
	}

	println("score = $score")
	println("lost = $lostScore")
}

fun Scanner.readLibrary(id: Int, bookMap: Map<Int, Book>): Library {
	val n = nextInt()
	val t = nextInt()
	val m = nextInt()

	val books = Array(n) { bookMap.getValue(nextInt()) }

	return Library(id, t, m, books.toList())
}

fun main() {
	solve(filenames.getValue('d'))
}
