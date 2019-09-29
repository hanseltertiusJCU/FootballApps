package com.example.footballapps.presenter

import com.example.footballapps.callback.PlayersRepositoryCallback
import com.example.footballapps.model.PlayerResponse
import com.example.footballapps.repository.PlayersRepository
import com.example.footballapps.view.PlayersView

class PlayersPresenter(
    private val playersView: PlayersView,
    private val playersRepository: PlayersRepository
) {
    fun getPlayersInfo(teamId: String) {
        playersView.dataIsLoading()

        playersRepository.getPlayers(teamId, object : PlayersRepositoryCallback<PlayerResponse?> {
            override fun onDataLoaded(data: PlayerResponse?) {
                playersView.showPlayersData(data!!)

                playersView.dataLoadingFinished()
            }

            override fun onDataError() {
                playersView.dataFailedToLoad()
            }

        })
    }
}