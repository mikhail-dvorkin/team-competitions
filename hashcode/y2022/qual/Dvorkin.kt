package hashcode.y2022.qual

import java.io.*
import kotlin.random.Random

private fun solve(fileName: String) {
	val random = Random(fileName.hashCode())
	val outputFileName = fileName.removeSuffix(".in.txt") + ".out"
	printLn("Solving" , fileName)
	input = BufferedReader(FileReader(fileName))
	output = PrintWriter(outputFileName)
	val (peopleCount, projectsCount) = readInts()
	val people = List(peopleCount) {
		val (name, skillsCountString) = readStrings()
		val skillsCount = skillsCountString.toInt()
		val skills = List(skillsCount) {
			val (skill, levelString) = readStrings()
			val level = levelString.toInt()
			skill to level
		}.toMap()
		assert(skills.size == skillsCount)
		name to skills
	}.toMap()
	assert(people.size == peopleCount)
	printLn("People:", people.size)
	data class Project(val name: String, val duration: Int, val score: Int, val bestBefore: Int, val roles: List<Pair<String, Int>>)
	val projects = List(projectsCount) {
		val (name, ds, ss, bs, rs) = readStrings()
		val rolesCount = rs.toInt()
		val roles = List(rolesCount) {
			val (skill, levelString) = readStrings()
			val level = levelString.toInt()
			skill to level
		}
		assert(roles.size == rolesCount)
		Project(name, ds.toInt(), ss.toInt(), bs.toInt(), roles)
	}
	printLn("Projects:", projects.size)
	printLn("Random 5 people:", people.toList().shuffled(random).take(5))
	printLn("Random 5 projects:", projects.toList().shuffled(random).take(5))
	for (project in projects) {
//		if (project.roles.toSet().size == 1) continue
//		printLn(project.roles.toSet().size, project)
//		break
	}
	val skillOwned = mutableMapOf<String, MutableList<Int>>()
	for (person in people) for (skill in person.value.keys) skillOwned[skill] = mutableListOf()
	for (project in projects) for (skill in project.roles.map { it.first }) skillOwned[skill] = mutableListOf()
	for (person in people) person.value.forEach { skillOwned[it.key]?.add(it.value) }
	val skillOwnedBy = skillOwned.mapValues { mutableListOf<String>() }
	for (person in people) {
		person.value.forEach { skillOwnedBy[it.key]?.add(person.key) }
	}
	val skillsWillBeNeeded = skillOwned.mapValues { mutableListOf<Pair<Int, Int>>() }
	for (project in projects) {
		for (skill in project.roles.map { it.first }.toSet()) {
			val entries = project.roles.filter { it.first == skill }
			val maxNeeded = entries.maxOf { it.second }
			val count = entries.size
			skillsWillBeNeeded[skill]!!.add(maxNeeded to count)
		}
	}
//	val skillsWorstNeeded = skillOwned.mapValues { skillsWillBeNeeded[it.key]!!.maxByOrNull { it.first * 1024 + it.second }!! }
	val skillsWorstNeeded = skillOwned.mapValues {
		skillsWillBeNeeded[it.key]!!.map { it.first }.maxOrNull()!! to
		skillsWillBeNeeded[it.key]!!.map { it.second }.maxOrNull()!!
	}
	var time = 0
	val willBeFree = people.mapValues { 0 }
	println(skillOwned)
	println(skillOwnedBy)
	fun whatNow() {
		for (project in projects.shuffled(random).take(10)) {
			println(project)
			val itsSkills = project.roles.map { it.first }.toSet()
			for (skill in itsSkills) {
				printLn(skill, skillOwned[skill], skillsWorstNeeded[skill] /*, skillsWillBeNeeded[skill]*/)
			}
		}
	}
	val answer = mutableListOf<String>()
	var usedTeam = listOf<String>()
	val memoLeaders = mutableMapOf<String, String>()

	fun processSkill(skill: String, free: Collection<String>): Int {
		usedTeam = listOf()
		val needPeople = skillsWorstNeeded[skill]!!.second
		val needMax = skillsWorstNeeded[skill]!!.first
		val leader = free.filter { people[it]!!.keys.contains(skill) && people[it]!![skill]!! >= needMax }.firstOrNull() ?: return 0
		memoLeaders[skill] = leader
		if (free.size < needPeople) return 0
		println(skill)
		println(skillOwnedBy[skill]!!)
		println(skillsWillBeNeeded[skill]!!.sortedBy { it.first })
		val team = listOf(leader) + (free - leader).shuffled(random).take(needPeople - 1)
		usedTeam = team
		println(team)
		val ourProjects = mutableListOf<Project>()
		for (project in projects) {
			if (project.roles.any { it.first != skill }) continue
			if (project.roles.map { it.second }.toSet().size != 1) continue
			ourProjects.add(project)
		}
		ourProjects.sortBy { it.roles.maxOf { it.second } }
//		println(ourProjects.joinToString("\n"))
		var x = team.size
		var prevLevel = 0
		var prevCount = team.size - 1
		var day = 0
		var score = 0
		for (project in ourProjects) {
			val levelHere = project.roles.first().second
			if (levelHere > prevLevel) {
				if (levelHere > prevLevel + 1) break
				prevCount = minOf(x - 1, prevCount)
				prevLevel = levelHere
				x = 1
//				println(prevCount)
			}
			if (prevCount < project.roles.size - 1) continue
			if (day + project.duration > project.bestBefore) continue
//			println(project)
			answer.add(project.name)
			val sb = StringBuilder()
			sb.append(leader)
			repeat(project.roles.size - 1) {
				sb.append(" ").append(team[(x - 1) % prevCount + 1])
				x++
			}
			answer.add(sb.toString())
			day += project.duration
			score += project.score
		}
		return score
	}

	val free = people.keys.toMutableSet()
	val sortedSkills = skillOwned.keys.sortedByDescending { processSkill(it, free) }
	answer.clear()

	val countMagic = 24
	val leaders = sortedSkills.take(countMagic).map { memoLeaders[it]!! }
	var count = 0
	free.clear()
	free.addAll(people.keys)
	free.removeAll(leaders)
	for (skill in sortedSkills) {
		free.add(memoLeaders[skill]!!)
		println(processSkill(skill, free))
		if (usedTeam.isNotEmpty()) count++
		free.removeAll(usedTeam)
	}
//	println(count); return // 24
	output?.println(answer.size / 2)
	output?.println(answer.joinToString("\n"))
	output?.close()
	scorerMain(arrayOf(fileName, outputFileName))
}

fun main() {
	val files = "a_an_example.in.txt b_better_start_small.in.txt c_collaboration.in.txt d_dense_schedule.in.txt e_exceptional_skills.in.txt f_find_great_mentors.in.txt".split(" ")
		.takeLast(1)
	for (f in files) solve(f)
}

private var input: BufferedReader? = null
private var output: PrintWriter? = null
private fun readLn() = input?.readLine()!!
private fun readInt() = readLn().toInt()
private fun readStrings() = readLn().split(" ")
private fun readInts() = readStrings().map { it.toInt() }
private fun printLn(vararg a: Any?) = println(a.joinToString(" ") { it.toString() })
