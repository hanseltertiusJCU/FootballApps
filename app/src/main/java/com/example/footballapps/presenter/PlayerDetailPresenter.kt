package com.example.footballapps.presenter

import com.example.footballapps.callback.PlayerDetailRepositoryCallback
import com.example.footballapps.model.PlayerResponse
import com.example.footballapps.repository.PlayerDetailRepository
import com.example.footballapps.view.PlayerDetailView

class PlayerDetailPresenter(
    private val playerDetailView: PlayerDetailView,
    private val playerDetailRepository: PlayerDetailRepository
) {
    fun getPlayerDetailInfo(playerId: String) {
        playerDetailView.dataIsLoading()

        playerDetailRepository.getPlayerDetail(
            playerId,
            object : PlayerDetailRepositoryCallback<PlayerResponse?> {
                override fun onDataLoaded(data: PlayerResponse?) {
                    playerDetailView.showPlayerDetailData(data!!)

                    playerDetailView.dataLoadingFinished()
                }

                override fun onDataError() {
                    playerDetailView.dataFailedToLoad()
                }

            })
    }
}