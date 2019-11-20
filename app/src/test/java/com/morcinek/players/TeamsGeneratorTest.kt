package com.morcinek.players

import com.morcinek.players.ui.funino.creator.TeamsGenerator
import com.morcinek.players.ui.funino.creator.flatten
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
        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6), setOf(1, 4, 5) to setOf(2, 3, 6), setOf(1, 6, 5) to setOf(3, 4, 2), setOf(1, 3, 5) to setOf(2, 4, 6))
//        val pairs = listOf(setOf(1, 2, 3) to setOf(4, 5, 6))

        teamsGenerator.opponentsData(teamsGenerator.balance(players, pairs)).forEachIndexed { index, it ->
            println("${it.first}: ${it.second}")
        }
        println(teamsGenerator.dataScore(teamsGenerator.balance(players, pairs)))
    }

    @Test
    fun dataScore_take3() {
        val players = (0..19).toList()
        val pairs = mutableListOf<Pair<Set<Int>, Set<Int>>>()
        (1..36).forEach { index ->
            val balance = teamsGenerator.balance(players, pairs)
            var bestPairScore: Pair<Int, Pair<Set<Int>, Set<Int>>>? = null
            repeat(6000) {
                val updatedList = teamsGenerator.take(balance, 6).shuffled()
                //                println(updatedList)
                val game = updatedList.take(3).toSet() to updatedList.subList(3, 6).toSet()
                val score = teamsGenerator.dataScore(teamsGenerator.balance(players, pairs.plus(game))).second
                if (bestPairScore == null || score < bestPairScore!!.first) {
                    bestPairScore = score to game
                }
            }
            println("Index: $index ,Score: ${bestPairScore!!.first}")
            pairs.add(bestPairScore!!.second)
        }
//        println(pairs)
        print("val pairs${players.size} = listOf(")
        pairs.forEachIndexed { index, it ->
            print("setOf(${it.first.joinToString(",")}) to setOf(${it.second.joinToString(",")})")
            if (index < pairs.size - 1) print(",")
        }
        println(")")
        val balance = teamsGenerator.balance(players, pairs)
        teamsGenerator.opponentsData(balance).forEach {
            println("${it.first}: ${it.second}")
        }
        println(teamsGenerator.dataScore(balance))
        //todo znajdz sobie 2000 kombinacji i z nich wybieraj
    }

    @Test
    fun playersTest() {
        val players = (0..7).toList()
        val pairs9 = listOf(setOf(5,4,3) to setOf(6,2,7),setOf(8,6,3) to setOf(0,1,7),setOf(1,2,4) to setOf(8,5,0),setOf(4,8,7) to setOf(0,2,3),setOf(0,4,6) to setOf(5,7,1),setOf(6,3,1) to setOf(8,5,2),setOf(6,5,2) to setOf(8,7,3),setOf(4,8,1) to setOf(7,5,0),setOf(3,2,1) to setOf(4,0,6),setOf(4,3,5) to setOf(8,1,6),setOf(7,4,2) to setOf(1,3,0),setOf(8,2,0) to setOf(7,5,6),setOf(8,7,3) to setOf(5,1,4),setOf(0,3,4) to setOf(2,6,8),setOf(2,0,7) to setOf(5,6,1),setOf(2,5,3) to setOf(4,6,7),setOf(0,5,8) to setOf(2,4,1),setOf(3,0,6) to setOf(1,7,8),setOf(6,0,1) to setOf(4,8,5),setOf(0,1,8) to setOf(2,3,7),setOf(3,4,7) to setOf(5,2,6),setOf(6,8,3) to setOf(7,0,5),setOf(2,4,8) to setOf(3,1,5),setOf(0,4,6) to setOf(7,1,2),setOf(7,3,6) to setOf(2,4,0),setOf(0,5,8) to setOf(3,1,4),setOf(6,5,2) to setOf(1,8,7),setOf(4,5,7) to setOf(3,0,2),setOf(1,0,5) to setOf(8,6,4),setOf(7,1,6) to setOf(3,2,8),setOf(0,2,1) to setOf(3,7,5),setOf(7,0,8) to setOf(4,6,1),setOf(6,3,5) to setOf(4,8,2),setOf(7,2,6) to setOf(3,8,1),setOf(0,3,4) to setOf(2,1,5),setOf(5,8,4) to setOf(6,0,7))

        val balance = teamsGenerator.balance(players, pairs9.take(12))// 20, 30
        balance.println()
        teamsGenerator.opponentsData(balance).forEach {
            println("${it.first}: ${it.second}")
        }
        println(teamsGenerator.dataScore(balance))
    }
}

private fun <T, B> List<Pair<T, B>>.println() {
    forEach {
        print(it.first)
        print(": ")
        println(it.second)
    }
}