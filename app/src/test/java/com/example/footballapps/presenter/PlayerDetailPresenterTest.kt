package com.example.footballapps.presenter

import com.example.footballapps.callback.PlayerDetailRepositoryCallback
import com.example.footballapps.model.PlayerResponse
import com.example.footballapps.repository.PlayerDetailRepository
import com.example.footballapps.repository.PlayersRepository
import com.example.footballapps.view.PlayerDetailView
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

class PlayerDetailPresenterTest {

    @Mock
    private lateinit var playerDetailView: PlayerDetailView

    @Mock
    private lateinit var playerDetailRepository: PlayerDetailRepository

    @Mock
    private lateinit var playerDetailResponse: PlayerResponse

    private lateinit var playerDetailPresenter: PlayerDetailPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        playerDetailPresenter = PlayerDetailPresenter(playerDetailView, playerDetailRepository)
    }

    @Test
    fun getPlayerDetailInfoTest() {
        val id = "34145444"

        playerDetailPresenter.getPlayerDetailInfo(id)

        argumentCaptor<PlayerDetailRepositoryCallback<PlayerResponse?>>().apply {
            verify(playerDetailRepository).getPlayerDetail(eq(id), capture())
            firstValue.onDataLoaded(playerDetailResponse)
        }

        val inOrder = inOrder(playerDetailView)

        inOrder.verify(playerDetailView, times(1)).dataIsLoading()
        inOrder.verify(playerDetailView, times(1)).showPlayerDetailData(playerDetailResponse)
        inOrder.verify(playerDetailView, times(1)).dataLoadingFinished()

    }



    @Test
    fun getFailedPlayerDetailInfoTest(){
        val id = ""

        playerDetailPresenter.getPlayerDetailInfo(id)

        argumentCaptor<PlayerDetailRepositoryCallback<PlayerResponse?>>().apply {
            verify(playerDetailRepository).getPlayerDetail(eq(id), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(playerDetailView)

        inOrder.verify(playerDetailView, times(1)).dataIsLoading()
        inOrder.verify(playerDetailView, times(1)).dataFailedToLoad()
    }
}