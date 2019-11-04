package com.morcinek.players

import junit.framework.Assert.assertEquals
import org.junit.Test

class TeamsGeneratorTest {

    private val teamsGenerator = TeamsGenerator()

    @Test
    fun numbers_flatten() {
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6))
        assertEquals(listOf(setOf(1, 2, 3), setOf(4, 5, 6)), pairs.flatten())
    }

    @Test
    fun numbers_flatten2() {
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6), setOf(1, 4, 5) to setOf(2, 3, 6))
        assertEquals(listOf(setOf(1, 2, 3), setOf(4, 5, 6), setOf(1, 4, 5), setOf(2, 3, 6)), pairs.flatten())
    }

    @Test
    fun printBalance() {
        val players = (1..6).toList()
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6))

        val balance = teamsGenerator.balance(players, pairs)
        balance.println()
    }

    @Test
    fun printBalance_step2() {
        val players = (1..6).toList()
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6), setOf(1, 4, 5) to setOf(2, 3, 6))

        val balance = teamsGenerator.balance(players, pairs)
        balance.println()
    }

    @Test
    fun opponents_initial() {
        val players = (1..6).toList()
        val balance = teamsGenerator.balance(players, listOf())
        balance.println()
        println(teamsGenerator.opponents(players, balance.first()))
    }

    @Test
    fun opponents_step1_for_player1() {
        val players = (1..6).toList()
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6))

        val balance = teamsGenerator.balance(players, pairs)
        balance.println()
        println(teamsGenerator.opponents(players, balance.first()))
    }

    @Test
    fun opponents_step2() {
        val players = (1..6).toList()
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6), setOf(1, 4, 5) to setOf(2, 3, 6))

        val balance = teamsGenerator.balance(players, pairs)
        balance.println()
        println(teamsGenerator.opponents(players, balance.first()))
    }

    @Test
    fun opponents_step2_all() {
        val players = (1..6).toList()
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6), setOf(1, 4, 5) to setOf(2, 3, 6))

        val balance = teamsGenerator.balance(players, pairs)
        balance.println()
        players.forEachIndexed { index, it ->
            print("$it: ")
            println(teamsGenerator.opponents(players, balance[index]))
        }
    }

    @Test
    fun playersOpponents_step1() {
        val players = (1..6).toList()
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6), setOf(1, 4, 5) to setOf(2, 3, 6))
//        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6))

        teamsGenerator.playersOpponents(players, pairs.flatten()).forEachIndexed { index, it ->
            println("${it.first}: ${it.second}")
        }
        println()
        teamsGenerator.opponentsData(teamsGenerator.balance(players, pairs)).forEachIndexed { index, it ->
            println("${it.first}: ${it.second}")
        }
    }

    @Test
    fun dataScore_step2() {
        val players = (1..6).toList()
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6), setOf(1, 4, 5) to setOf(2, 3, 6))
//        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6))

        teamsGenerator.opponentsData(teamsGenerator.balance(players, pairs)).forEachIndexed { index, it ->
            println("${it.first}: ${it.second}")
        }
        println(teamsGenerator.dataScore(teamsGenerator.balance(players, pairs)))
    }

//    @Test
//    fun nextTeam_initial() {
//        val players = (1..6).toList()
//
//        val nextTeam = teamsGenerator.nextTeam(players, listOf())
//        assertEquals(setOf(1, 2, 3), nextTeam)
//    }
//
//    @Test
//    fun nextTeam_step1() {
//        val players = (1..6).toList()
////        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6), setOf(1, 4, 5) to setOf(2, 3, 6))
//        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6))
//
//        val nextTeam = teamsGenerator.nextTeam(players, pairs.flatten())
//        println(nextTeam)
////        teamsGenerator.playersOpponents(players, pairs.flatten()).forEachIndexed { index, it ->
////            println("${it.first}: ${it.second}")
////        }
//    }
}

private fun <T, B> List<Pair<T, B>>.println() {
    forEach {
        print(it.first)
        print(": ")
        println(it.second)
    }
}