package com.morcinek.players

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt


private const val numberOfPlayersInTheTeam = 3

class TeamsGenerator {

    fun balance(players: List<Int>, data: List<Pair<Set<Int>, Set<Int>>>) = balanceN(players, data.flatten())

    private fun balanceN(players: List<Int>, data: List<Set<Int>>): List<Pair<Int, List<Int>>> = players.map {
        val list = data.flatMap { team -> if (it in team) team.minus(it).toList() else emptyList() }
        it to list
    }

    fun opponents(players: List<Int>, teamBalance: Pair<Int, List<Int>>): List<Pair<Int, Int>> =
        players.mapNotNull { player -> if (player == teamBalance.first) null else player to teamBalance.second.count { it == player } }.sortedBy { it.second }

    private fun balanceScore(balance: List<Pair<Int, List<Int>>>) = balance.map { it.second.size }.sortedDescending().let { it.first() - it.last() } / 2

    //    private fun opponentsScore(opponents: List<Pair<Int, List<Pair<Int, Int>>>>): Int = opponents.sumBy { it.second.let { list -> 2.0.pow(list.last().second - list.first().second) }.toInt() }
//    private fun opponentsScore(opponents: List<Pair<Int, List<Pair<Int, Int>>>>): Int = opponents.sumBy { it.second.map { it.second }.sorted().let { 2.0.pow(- it.first() + it.last() ).toInt() } }
    //TODO median algorithm below is better in assuming the correct result
    private fun opponentsScore(opponents: List<Pair<Int, List<Pair<Int, Int>>>>): Int = opponents.sumBy {
        it.second.map { it.second }.let { games ->
            games.sumBy {
                2.0.pow(abs(games.average().roundToInt() - it)).toInt() - 1
            }
        }
    }

    @Deprecated("use opponentsData")
    fun playersOpponents(players: List<Int>, data: List<Set<Int>>) = balanceN(players, data).map { it.first to opponents(players, it) }

    fun opponentsData(balance: List<Pair<Int, List<Int>>>): List<Pair<Int, List<Pair<Int, Int>>>> =
        balance.map { it.first to opponents(balance.map(Pair<Int, List<Int>>::first), it) }

    fun dataScore(balance: List<Pair<Int, List<Int>>>): Pair<Int, Int> = balanceScore(balance) to opponentsScore(opponentsData(balance))

    fun take(balance: List<Pair<Int, List<Int>>>, number: Int): List<Int> = balance.shuffled().sortedBy { it.second.size }.take(number).map { it.first }
//    fun take(balance: List<Pair<Int, List<Int>>>, number: Int): List<Int> = balance.shuffled().take(number).map { it.first }
}

fun <T> List<Pair<Set<T>, Set<T>>>.flatten() = flatMap { listOf(it.first, it.second) }
