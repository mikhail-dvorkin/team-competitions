package hashcode.y2021.qual

//import Main
import java.util.*
import java.io.*

fun main() {
	listOf("a", "b", "c", "d", "e", "f").forEach {
		solve(it)
	}
}

private fun solve(f: String) {

	val sc = Scanner(File("$f.txt"))

	val D = sc.nextInt()
	val I = sc.nextInt()
	val S = sc.nextInt()
	val V = sc.nextInt()
	val F = sc.nextInt()

	val b = IntArray(S)
	val e = IntArray(S)
	val l = IntArray(S)

	val s2n = mutableMapOf<String, Int>()

	val n2s = Array<String>(S) { i ->
		b[i] = sc.nextInt()
		e[i] = sc.nextInt()
		sc.next().also {
			l[i] = sc.nextInt()
			s2n[it] = i
		}
	}

	val cars = Array<IntArray>(V) { i ->
		val p = sc.nextInt()
		IntArray(p) { s2n[sc.next()]!! }
	}

	val traffic = IntArray(S)
	cars.forEach { r ->
		r.forEach {
			traffic[it]++
		}
	}

	val inbound = Array<MutableSet<Int>>(I) { mutableSetOf() }
	for (i in 0 until S) {
		inbound[e[i]].add(i)
	}

	val out = PrintWriter("${f}_out.txt")

	out.println(I)

	inbound.forEachIndexed { i, streets ->
		out.println(i)


		val total = streets.map { traffic[it] }.sum()

		if (total != 0) {
			val len = streets.map { s -> traffic[s] }
			out.println(len.count { it != 0 })

			streets.forEachIndexed { i, s ->
				if (len[i] != 0) {
					out.println(n2s[s] + " " + len[i])
				}
			}
		} else {
			out.println(streets.size)
			for (s in streets) {
				out.println(n2s[s] + " " + 1)
			}
		}


	}

	out.close()


//    Main.main1(arrayOf("$f.txt", "${f}_out.txt"))
}
