package com.morcinek.players.ui.team

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.morcinek.android.HasKey
import com.morcinek.android.setupSections
import com.morcinek.core.ui.showPopupMenu
import com.morcinek.players.AppPreferences
import com.morcinek.players.R
import com.morcinek.players.core.BaseFragment
import com.morcinek.players.core.createFabConfiguration
import com.morcinek.players.core.createMenuConfiguration
import com.morcinek.players.core.data.PlayerData
import com.morcinek.players.core.database.FirebaseReferences
import com.morcinek.players.core.database.playersForTeamLiveDataForValueListener
import com.morcinek.players.core.extensions.*
import com.morcinek.players.databinding.FragmentListBinding
import com.morcinek.players.databinding.VhHeaderBinding
import com.morcinek.players.databinding.VhPlayerBinding
import com.morcinek.players.ui.lazyNavController
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class PlayersFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    private val viewModel by viewModel<PlayersViewModel>()

    private val navController by lazyNavController()

    override val fabConfiguration = createFabConfiguration(R.drawable.ic_person_add) {
        navController.navigate(R.id.action_nav_players_to_nav_create_player, viewModel.teamData.toBundle())
    }

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.sort, R.drawable.ic_sort) {
            requireContext().showPopupMenu(
                requireActivity().findViewById(it.itemId),
                Pair("Sort by name") { viewModel.updateSortingMethod(SortingMethod.SortByName) },
                Pair("Sort by age") { viewModel.updateSortingMethod(SortingMethod.SortByAge) },
                Pair("Group by age") { viewModel.updateSortingMethod(SortingMethod.GroupByAge) },
            )
        }
    }

    private val playersFormatter = standardDateFormat()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            progressBar.show()
            recyclerView.run {
                setupSections {
                    sectionBinding(VhPlayerBinding::inflate) { _, item: IndexedPlayer ->
                        name.text = "${item.index}. ${item.playerData}"
                        date.text = playersFormatter.formatCalendar(item.playerData.getBirthDate())
                        root.setOnClickListener {
                            navController.navigate(
                                R.id.action_nav_players_to_nav_player_details,
                                item.playerData.toBundle(),
                                null,
                                FragmentNavigatorExtras(name, date)
                            )
                        }
                    }
                    sectionBinding(VhHeaderBinding::inflate) { _, item: Header -> root.text = item.text }
                    observe(viewModel.items) { submitList(it) { progressBar.hide() } }
                }
            }
        }
    }
}

private class PlayersViewModel(references: FirebaseReferences, appPreferences: AppPreferences) : ViewModel() {

    val teamData = appPreferences.selectedTeamData!!

    private val sortingMethod = MutableLiveData<SortingMethod>(SortingMethod.SortByName)

    private val players = references.playersForTeamLiveDataForValueListener(teamData.key)

    fun updateSortingMethod(newSortingMethod: SortingMethod) = sortingMethod.postValue(newSortingMethod)

    val items = combine(sortingMethod, players) { sortingMethod, players -> sortingMethod.transform(players) }
}

private sealed class SortingMethod(val transform: (List<PlayerData>) -> List<HasKey>) {
    object GroupByAge : SortingMethod({
        it.groupBy { it.getBirthDate().year }.toSortedMap().flatMap { listOf<HasKey>(Header("${it.key}")).plus(it.value.mapIndexedPlayer()) }
    })

    open class SortBy<R : Comparable<R>>(val selector: (PlayerData) -> R?) : SortingMethod({ it.sortedBy(selector).mapIndexedPlayer() })

    object SortByAge : SortBy<Long>({ it.birthDateInMillis })
    object SortByName : SortBy<String>({ it.surname })
}

private class Header(val text: String) : HasKey {
    override val key: String get() = text
}

private class IndexedPlayer(val index: Int, val playerData: PlayerData) : HasKey by playerData

private fun List<PlayerData>.mapIndexedPlayer() = mapIndexed { index, playerData -> IndexedPlayer(index + 1, playerData) }

val playersModule = module {
    viewModel { PlayersViewModel(get(), get()) }
}