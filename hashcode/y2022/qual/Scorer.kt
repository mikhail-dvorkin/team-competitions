package hashcode.y2022.qual

import java.util.*
import java.io.*

fun main(args: Array<String>) = scorerMain(args)

fun scorerMain(args: Array<String>) {
    val f = File(args[0])
    val of = File(args[1])

//    val f = File("data/a_an_example.in.txt")
//    val of = File("a_an_example.out.txt")

    solve(f, of)
}

class Contributor(val name: String, val skills: MutableMap<String, Int>)

class Project(
    val name: String,
    val duration: Int,
    val score: Int,
    val bestBefore: Int,
    val roles: List<Pair<String, Int>>
)

class ProjectAssignment(val projectName: String, val assignees: List<String>)

private fun solve(f: File, of: File) {

    val sc = Scanner(f)

    val C = sc.nextInt()
    val P = sc.nextInt()


    val contributors = Array(C) { sc.nextContributor() }

    val contributorByName = contributors.associate { it.name to it }

    val projects = Array(P) { sc.nextProject() }

    val projectByName = projects.associate { it.name to it }

    val osc = Scanner(of)

    val E = osc.nextInt()

    val assignments = Array(E) {
        val name = osc.next()
        ProjectAssignment(name, osc.readList(projectByName[name]!!.roles.size))
    }

    val occupiedUntil = mutableMapOf<String, Int>()

    var score = 0L

    for (assignment in assignments) {
        val project = projectByName[assignment.projectName]!!

        val endTime = assignment.assignees.map { occupiedUntil.getOrDefault(it, 0) }.maxOrNull()!! + project.duration

        var cost = project.score
        if (endTime > project.bestBefore) {
            cost -= endTime - project.bestBefore
        }
        cost = maxOf(0, cost)
        score += cost

        println("Project '${project.name}' finished at day ${endTime - 1} and scored $cost points (best before: ${project.bestBefore})")

        assignment.assignees.forEachIndexed { i, c ->
            occupiedUntil[c] = endTime
            val (skillName, skillLevel) = project.roles[i]

            val contributor = contributorByName[c]!!

            val contributorSkillLevel = contributor.skills.getOrDefault(skillName, 0)

            if (contributorSkillLevel + 1 < skillLevel) error("Project ${project.name} assignee #$i ($c) skill level '$skillName' too low (has: $contributorSkillLevel, required: $skillLevel)")

            if (contributorSkillLevel + 1 == skillLevel) {
                if (!assignment.assignees.any { contributorByName[it]!!.skills.getOrDefault(skillName, 0) >= skillLevel }) {
                    error("Project ${project.name} assignee #$i ($c) requires a mentor for skill '$skillName', but none could be found")
                }
            }

            if (contributorSkillLevel <= skillLevel) {
                contributor.skills[skillName] = contributorSkillLevel + 1
                println("    Contributor ${contributor.name} advanced skill '$skillName' to ${contributorSkillLevel + 1}")
            }
        }

        println()
    }

    println("Total score: $score")
}

fun Scanner.nextContributor(): Contributor = Contributor(next(), nextMap())

fun Scanner.nextProject(): Project = Project(next(), nextInt(), nextInt(), nextInt(), nextRolesList())

fun Scanner.nextMap(): MutableMap<String, Int> {
    val n = nextInt()

    val skills = mutableMapOf<String, Int>()

    for (i in 0 until n) {
        skills[next()] = nextInt()
    }

    return skills
}

fun Scanner.nextRolesList(): List<Pair<String, Int>> {
    val n = nextInt()

    val roles = mutableListOf<Pair<String, Int>>()

    for (i in 0 until n) {
        roles += next() to nextInt()
    }

    return roles
}

fun Scanner.readList(length: Int): List<String> {
    val result = mutableListOf<String>()
    for (i in 0 until length) {
        result += next()
    }
    return result
}
