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

    val (_, _, V, R) = Array(4) { sc.nextInt() }
    B = sc.nextInt()
    val T = sc.nextInt()

    val rides = Array(R) { sc.nextRide(it) }

    val cars = Array(V) { Car(Point(0, 0), 0) }

    println("Max score: " + rides.sumBy { it.duration + B })

    rides.sortBy { it.end - it.duration }

    for (ride in rides) {
        cars.maxBy { it.canScore(ride) }?.take(ride)
    }

    println(cars.totalScore)

    printSolution(cars)
}

val Array<Car>.totalScore
    get() = fold(0) { acc, state -> acc + state.score }

var B = 0

class Car(var point: Point,
          var time: Int,
          val schedule: MutableList<Ride> = mutableListOf(),
          var score: Int = 0) {

    fun willFinishAt(ride: Ride) = possibleArrival(ride) + ride.duration

    fun possibleArrival(ride: Ride) = Math.max(time + (ride.start - point), ride.begin)

    fun waitTime(ride: Ride) = Math.max(possibleArrival(ride) - time, 0)

    fun canScore(ride: Ride): Int {
        return if (willFinishAt(ride) <= ride.end) {
            ride.duration + if (possibleArrival(ride) <= ride.begin) B else 0
        } else 0
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