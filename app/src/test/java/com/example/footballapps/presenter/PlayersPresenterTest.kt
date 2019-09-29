package com.example.footballapps.presenter

import com.example.footballapps.callback.PlayersRepositoryCallback
import com.example.footballapps.model.PlayerResponse
import com.example.footballapps.repository.PlayersRepository
import com.example.footballapps.view.PlayersView
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

class PlayersPresenterTest {

    @Mock
    private lateinit var playersView: PlayersView

    @Mock
    private lateinit var playersRepository: PlayersRepository

    @Mock
    private lateinit var playersResponse: PlayerResponse

    private lateinit var playersPresenter : PlayersPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        playersPresenter = PlayersPresenter(playersView, playersRepository)
    }

    @Test
    fun getPlayersInfoTest() {

        val id = "133604"

        playersPresenter.getPlayersInfo(id)

        argumentCaptor<PlayersRepositoryCallback<PlayerResponse?>>().apply {
            verify(playersRepository).getPlayers(eq(id), capture())
            firstValue.onDataLoaded(playersResponse)
        }

        val inOrder = inOrder(playersView)

        inOrder.verify(playersView, times(1)).dataIsLoading()
        inOrder.verify(playersView, times(1)).showPlayersData(playersResponse)
        inOrder.verify(playersView, times(1)).dataLoadingFinished()
    }

    @Test
    fun getFailedPlayersInfoTest(){
        val id = ""

        playersPresenter.getPlayersInfo(id)

        argumentCaptor<PlayersRepositoryCallback<PlayerResponse?>>().apply {
            verify(playersRepository).getPlayers(eq(id), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(playersView)
        inOrder.verify(playersView, times(1)).dataIsLoading()
        inOrder.verify(playersView, times(1)).dataFailedToLoad()
    }
}