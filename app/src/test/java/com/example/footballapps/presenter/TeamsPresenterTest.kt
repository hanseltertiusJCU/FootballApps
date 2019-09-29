package com.example.footballapps.presenter

import com.example.footballapps.callback.TeamsRepositoryCallback
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.repository.TeamsRepository
import com.example.footballapps.view.TeamsView
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

class TeamsPresenterTest {

    @Mock
    private lateinit var teamsView : TeamsView

    @Mock
    private lateinit var teamsRepository : TeamsRepository

    @Mock
    private lateinit var teamsResponse : TeamResponse

    private lateinit var teamsPresenter : TeamsPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        teamsPresenter = TeamsPresenter(teamsView, teamsRepository)
    }

    @Test
    fun getTeamsInfoTest() {

        val id = "4328"

        teamsPresenter.getTeamsInfo(id)

        argumentCaptor<TeamsRepositoryCallback<TeamResponse?>>().apply {
            verify(teamsRepository).getTeams(eq(id), capture())
            firstValue.onDataLoaded(teamsResponse)
        }

        val inOrder = inOrder(teamsView)

        inOrder.verify(teamsView, times(1)).dataIsLoading()
        inOrder.verify(teamsView, times(1)).showTeamsData(teamsResponse)
        inOrder.verify(teamsView, times(1)).dataLoadingFinished()

    }

    @Test
    fun getFailedTeamsInfoTest() {

        val id = ""

        teamsPresenter.getTeamsInfo(id)

        argumentCaptor<TeamsRepositoryCallback<TeamResponse?>>().apply {
            verify(teamsRepository).getTeams(eq(id), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(teamsView)

        inOrder.verify(teamsView, times(1)).dataIsLoading()
        inOrder.verify(teamsView, times(1)).dataFailedToLoad()

    }


    @Test
    fun getSearchTeamsInfoTest() {

        val query = "Arsenal"

        teamsPresenter.getSearchTeamsInfo(query)

        argumentCaptor<TeamsRepositoryCallback<TeamResponse?>>().apply {
            verify(teamsRepository).getSearchResultTeams(eq(query), capture())
            firstValue.onDataLoaded(teamsResponse)
        }

        val inOrder = inOrder(teamsView)

        inOrder.verify(teamsView, times(1)).dataIsLoading()
        inOrder.verify(teamsView, times(1)).showTeamsData(teamsResponse)
        inOrder.verify(teamsView, times(1)).dataLoadingFinished()

    }

    @Test
    fun getFailedSearchTeamsInfoTest() {

        val query = "abracadabra"

        teamsPresenter.getSearchTeamsInfo(query)

        argumentCaptor<TeamsRepositoryCallback<TeamResponse?>>().apply {
            verify(teamsRepository).getSearchResultTeams(eq(query), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(teamsView)

        inOrder.verify(teamsView, times(1)).dataIsLoading()
        inOrder.verify(teamsView, times(1)).dataFailedToLoad()

    }

}