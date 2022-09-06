package hashcode.y2022.qual

import java.util.*
import java.io.*

fun main() {
    File("data").listFiles()!!.sortedBy { it.name }.forEach {
        solve(it)
    }
}

private fun solve(f: File) {

    println()
    println(f.name)

    val sc = Scanner(f)

    val C = sc.nextInt()
    val P = sc.nextInt()

    val contributors = Array(C) { sc.nextContributor() }

    val contributorByName = contributors.associate { it.name to it }

    val projects = Array(P) { sc.nextProject() }

    val projectByName = projects.associate { it.name to it }

    println("Contributors: $C; projects: $P")
    println("Max score: ${projects.sumOf { it.score }}")

    val of = File("output/${f.name.dropLast(6)}out.txt")

    PrintWriter(of).use { out ->
        val result = mutableMapOf<Project, List<Contributor>>()

        for (p in projects.sortedBy { it.bestBefore }) {
            val pr = mutableSetOf<Contributor>()


            var assignment = arrayOfNulls<Contributor>(p.roles.size)
            var changed: Boolean

            do {
                changed = false
                for (i in p.roles.indices) {
                    if (assignment[i] != null) continue

                    val (skillName, skillLevel) = p.roles[i]

                    for (c in contributors) {
                        if (c in pr) continue

                        val contributorSkillLevel = c.skills.getOrDefault(skillName, 0)

                        if (contributorSkillLevel >= skillLevel ||
                            contributorSkillLevel == skillLevel - 1 && pr.any { it.skills.getOrDefault(skillName, 0) >= skillLevel }) {
                            pr += c
                            assignment[i] = c
                            changed = true
                            break
                        }
                    }
                }
            } while (changed)

            if (pr.size == p.roles.size) {
                result[p] = assignment.map { it!! }

                pr.forEachIndexed { index, contributor ->
                    val (skillName, skillLevel) = p.roles[index]

                    if (skillLevel == contributor.skills.getOrDefault(skillName, 0)) {
                        contributor.skills[skillName] = skillLevel + 1
                    }
                }

            }
        }

        out.println(result.size)
        for ((p, r) in result) {
            out.println(p.name)
            out.println(r.joinToString(separator = " ") { it.name })
        }
    }

//    try {
//        hashcode.y2022.scorer.solve(f, of)
//    } catch (t: Throwable) {
//        System.err.println(t.message)
//    }
}
