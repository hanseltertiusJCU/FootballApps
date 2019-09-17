package com.example.footballapps.presenter

import com.example.footballapps.callback.LeagueDetailRepositoryCallback
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.repository.LeagueDetailRepository
import com.example.footballapps.view.LeagueDetailView
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations


class LeagueDetailPresenterTest {

    @Mock
    private lateinit var leagueDetailRepository: LeagueDetailRepository

    @Mock
    lateinit var leagueDetailView: LeagueDetailView

    @Mock
    lateinit var leagueDetailResponse: LeagueDetailResponse

    private lateinit var leagueDetailPresenter: LeagueDetailPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        leagueDetailPresenter = LeagueDetailPresenter(leagueDetailView, leagueDetailRepository)
    }

    @Test
    fun getLeagueDetailInfoTest() {

        val id = "4328"

        leagueDetailPresenter.getLeagueDetailInfo(id)

        argumentCaptor<LeagueDetailRepositoryCallback<LeagueDetailResponse?>>().apply {

            verify(leagueDetailRepository).getLeagueDetail(eq(id), capture())
            firstValue.onDataLoaded(leagueDetailResponse)
        }

        val inOrder = inOrder(leagueDetailView)
        inOrder.verify(leagueDetailView, times(1)).dataIsLoading()
        inOrder.verify(leagueDetailView, times(1)).showLeagueDetailInfo(leagueDetailResponse)
        inOrder.verify(leagueDetailView, times(1)).dataLoadingFinished()

    }

    @Test
    fun getFailedLeagueDetailInfoTest() {

        val id = ""

        leagueDetailPresenter.getLeagueDetailInfo(id)

        argumentCaptor<LeagueDetailRepositoryCallback<LeagueDetailResponse?>>().apply {

            verify(leagueDetailRepository).getLeagueDetail(eq(id), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(leagueDetailView)
        inOrder.verify(leagueDetailView, times(1)).dataIsLoading()
        inOrder.verify(leagueDetailView, times(1)).dataFailedToLoad()

    }
}