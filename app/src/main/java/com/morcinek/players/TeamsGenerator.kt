package com.morcinek.players


private const val numberOfPlayersInTheTeam = 3

class TeamsGenerator {

    fun balance(players: List<Int>, data: List<Pair<Set<Int>, Set<Int>>>) = balanceN(players, data.flatten())

    private fun balanceN(players: List<Int>, data: List<Set<Int>>): List<Pair<Int, List<Int>>> = players.map {
        val list = data.flatMap { team -> if (it in team) team.minus(it).toList() else emptyList() }
        it to list
    }

    fun opponents(players: List<Int>, teamBalance: Pair<Int, List<Int>>) : List<Pair<Int,Int>> =
        players.mapNotNull { player ->  if(player == teamBalance.first) null else player to teamBalance.second.count { it == player } }.sortedBy { it.second }

    private fun balanceScore(balance : List<Pair<Int, List<Int>>>) = balance.map { it.second.size }.sortedDescending().let { it.last() - it.first() }

    private fun opponentsScore(opponents: List<Pair<Int, List<Pair<Int, Int>>>>) = opponents.sumBy { it.second.let { list -> list.last().second - list.first().second } }

    fun score(opponents: List<Pair<Int,Int>>) = opponents.last().second - opponents.first().second

    @Deprecated("use opponentsData")
    fun playersOpponents(players: List<Int>, data: List<Set<Int>>) = balanceN(players, data).map { it.first to opponents(players, it) }

    fun opponentsData(balance: List<Pair<Int, List<Int>>>): List<Pair<Int, List<Pair<Int, Int>>>> = balance.map { it.first to opponents(balance.map(Pair<Int, List<Int>>::first), it) }

    fun dataScore(balance: List<Pair<Int, List<Int>>>) = balanceScore(balance) * balance.size + opponentsScore(opponentsData(balance))
//    fun nextTeam(players: List<Int>, data: List<Set<Int>>): Set<Int>{
//        val playersOpponents = playersOpponents(players, data)
//        val firstPlayer = playersOpponents.first()
//        return mutableSetOf<Int>().apply {
//            add(firstPlayer.first)
//            addAll(firstPlayer.second.take(numberOfPlayersInTheTeam- 1).map { it.first })
//        }
//    }
}

fun <T> List<Pair<Set<T>, Set<T>>>.flatten() = flatMap { listOf(it.first, it.second) }
