package com.example.footballapps.presenter

import com.example.footballapps.callback.TeamDetailRepositoryCallback
import com.example.footballapps.model.TeamResponse
import com.example.footballapps.repository.TeamDetailRepository
import com.example.footballapps.view.TeamDetailView
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

class TeamDetailPresenterTest {

    @Mock
    private lateinit var teamDetailView: TeamDetailView

    @Mock
    private lateinit var teamDetailRepository: TeamDetailRepository

    @Mock
    private lateinit var teamDetailResponse: TeamResponse

    private lateinit var teamDetailPresenter: TeamDetailPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        teamDetailPresenter = TeamDetailPresenter(teamDetailView, teamDetailRepository)
    }

    @Test
    fun getTeamDetailInfoTest() {

        val id = "133604"

        teamDetailPresenter.getTeamDetailInfo(id)

        argumentCaptor<TeamDetailRepositoryCallback<TeamResponse?>>().apply {
            verify(teamDetailRepository).getTeamDetail(eq(id), capture())
            firstValue.onDataLoaded(teamDetailResponse)
        }

        val inOrder = inOrder(teamDetailView)

        inOrder.verify(teamDetailView, times(1)).dataIsLoading()
        inOrder.verify(teamDetailView, times(1)).showTeamDetailData(teamDetailResponse)
        inOrder.verify(teamDetailView, times(1)).dataLoadingFinished()

    }

    @Test
    fun getFailedTeamDetailInfoTest() {

        val id = ""

        teamDetailPresenter.getTeamDetailInfo(id)

        argumentCaptor<TeamDetailRepositoryCallback<TeamResponse?>>().apply {
            verify(teamDetailRepository).getTeamDetail(eq(id), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(teamDetailView)

        inOrder.verify(teamDetailView, times(1)).dataIsLoading()
        inOrder.verify(teamDetailView, times(1)).dataFailedToLoad()

    }
}