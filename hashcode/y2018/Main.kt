import java.io.File
import java.io.PrintWriter
import java.util.*

//val filename = "a_example"
//val filename = "b_should_be_easy"
//val filename = "c_no_hurry"
val filename = "d_metropolis"
//val filename = "e_high_bonus"

fun main(args: Array<String>) {
    val sc = Scanner(File("data/$filename.in"))

    val rows = sc.nextInt()
    val columns = sc.nextInt()

    val V = sc.nextInt()

    val R = sc.nextInt()

    B = sc.nextInt()
    val T = sc.nextInt()

    val rides = Array(R) { sc.nextRide(it) }

    val origin = Point(0, 0)

    val pos = Array(V) { Car(origin, 0, it) }

    println("Max score: " + rides.sumBy { it.duration })

//    val longRides = rides.filter { it.longRide }.sortedBy { it.duration }

//    longRides.foldIndexed(0) { index, acc, ride ->
//        println("$index $acc")
//        acc + ride.duration
//    }


//    rides.sortBy { it.duration }
//    for (i in rides.indices step 100) {
//        println("$i ${rides[i].duration}")
//    }
//    rides.withIndex().forEach { (i, ride) ->  println("$i ${ride.duration}") }

//    println("Long rides: ${rides.count { it.longRide }}")
//    println("Long rides: ${rides.filter {it.longRide}.sumBy { it.duration }}")
//    println("Short rides: ${rides.filter {!it.longRide}.sumBy { it.duration }}")

    rides.sortBy { it.end - it.duration - it.duration / 2}

//    rides.filter { it.longRide }.sortedByDescending { it.begin }.take(20).zip(pos).forEach {(ride, car) ->
//        car.longRide = ride
//    }

//    println(rides.filter { !it.longRide }.maxBy { it.duration }?.duration)

    for (ride in rides) {
//        if (ride.longRide) continue
        val cars = pos//70.let { if (ride.longRide) pos.take(it) else pos.drop(it) }
        var best: Car? = null
        var bestScore = 0
        var bestWait = Int.MAX_VALUE
        for (p in cars) {
            if (p.canScore(ride) > bestScore /*|| p.canScore(ride) == bestScore && p.waitTime(ride) < bestWait*/) {
                best = p
                bestScore = p.canScore(ride)
                bestWait = p.waitTime(ride)
            }
        }

        if (best != null) {
            best.take(ride)
        }
    }

    println(pos.totalScore)

    printSolution(pos)
}

val rand = Random()

fun <T> Array<T>.shuffled() = toList().also { Collections.shuffle(it) }

val Array<Car>.totalScore
    get() = fold(0) { acc, state -> acc + state.score }

var B = 0

class Car(var point: Point,
          var time: Int,
          val num: Int,
          val schedule: MutableList<Ride> = mutableListOf(),
          var score: Int = 0,
          var longRide: Ride? = null) {

    fun willFinishAt(ride: Ride) = possibleArrival(ride) + ride.duration

    fun possibleArrival(ride: Ride) = Math.max(time + (ride.start - point), ride.begin)

    fun waitTime(ride: Ride) = Math.max(possibleArrival(ride) - time, 0)

    fun canScore(ride: Ride): Int {
        val possibleArrival = possibleArrival(ride)
        if (possibleArrival + ride.duration <= ride.end) {
            val lr = longRide
            if (lr != null && ride !== lr && willFinishAt(ride) + (lr.start - ride.finish) + lr.duration > lr.end) {
                return 0
            }

            return ride.duration + if (possibleArrival <= ride.begin) B else 0
        }
        return 0
    }

    fun take(ride: Ride) {
        score += canScore(ride)
        schedule += ride
        time = willFinishAt(ride)
        point = ride.finish
    }
}

fun printSolution(solution: Array<Car>) {
    PrintWriter("$filename-${solution.totalScore}.out").use { pr ->
        solution.forEach {
            pr.print(it.schedule.size)
            it.schedule.forEach {
                pr.print(" ${it.num}")
            }
            pr.println()
        }
    }
}

operator fun Point.minus(o: Point) = Math.abs(x - o.x) + Math.abs(y - o.y)

data class Point(val x: Int, val y: Int)

data class Ride(val num: Int, val start: Point, val finish: Point, val begin: Int, val end: Int) {
    val duration
        get() = finish - start
}

val Ride.longRide
    get() = finish.x > 5000 || finish.y > 5000

fun Scanner.nextPoint() = Point(nextInt(), nextInt())

fun Scanner.nextRide(num: Int) = Ride(num, nextPoint(), nextPoint(), nextInt(), nextInt())