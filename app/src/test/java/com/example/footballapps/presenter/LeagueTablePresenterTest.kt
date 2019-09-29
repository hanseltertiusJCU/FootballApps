package com.example.footballapps.presenter

import com.example.footballapps.callback.LeagueTableRepositoryCallback
import com.example.footballapps.model.LeagueTableResponse
import com.example.footballapps.repository.LeagueTableRepository
import com.example.footballapps.view.LeagueTableView
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

class LeagueTablePresenterTest {

    @Mock
    private lateinit var leagueTableView: LeagueTableView

    @Mock
    private lateinit var leagueTableRepository: LeagueTableRepository

    @Mock
    private lateinit var leagueTableResponse: LeagueTableResponse

    private lateinit var leagueTablePresenter: LeagueTablePresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        leagueTablePresenter = LeagueTablePresenter(leagueTableView, leagueTableRepository)
    }

    @Test
    fun getLeagueTableInfoTest() {

        val id = "4328"

        val season = "1920"

        leagueTablePresenter.getLeagueTableInfo(id, season)

        argumentCaptor<LeagueTableRepositoryCallback<LeagueTableResponse?>>().apply {
            verify(leagueTableRepository).getLeagueTable(eq(id), eq(season), capture())
            firstValue.onDataLoaded(leagueTableResponse)
        }

        val inOrder = inOrder(leagueTableView)

        inOrder.verify(leagueTableView, times(1)).dataIsLoading()
        inOrder.verify(leagueTableView, times(1)).showLeagueTable(leagueTableResponse)
        inOrder.verify(leagueTableView, times(1)).dataLoadingFinished()
    }

    @Test
    fun getFailedLeagueTableInfoTest() {
        val id = ""

        val season = ""

        leagueTablePresenter.getLeagueTableInfo(id, season)

        argumentCaptor<LeagueTableRepositoryCallback<LeagueTableResponse?>>().apply {
            verify(leagueTableRepository).getLeagueTable(eq(id), eq(season), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(leagueTableView)

        inOrder.verify(leagueTableView, times(1)).dataIsLoading()
        inOrder.verify(leagueTableView, times(1)).dataFailedToLoad()
    }
}