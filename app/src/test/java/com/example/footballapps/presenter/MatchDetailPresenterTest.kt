package com.example.footballapps.presenter

import com.example.footballapps.callback.MatchDetailRepositoryCallback
import com.example.footballapps.model.CombinedMatchTeamsResponse
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.repository.LeagueDetailRepository
import com.example.footballapps.repository.MatchDetailRepository
import com.example.footballapps.rule.RxImmediateSchedulerRule
import com.example.footballapps.view.MatchDetailView
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.inOrder
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit

class MatchDetailPresenterTest {

    @Mock
    lateinit var matchDetailView: MatchDetailView

    @Mock
    private lateinit var matchDetailRepository: MatchDetailRepository

    @Mock
    private lateinit var combinedMatchTeamsResponse: CombinedMatchTeamsResponse

    private lateinit var matchDetailPresenter: MatchDetailPresenter

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        matchDetailPresenter = MatchDetailPresenter(matchDetailView, matchDetailRepository)
    }

    @Test
    fun getDetailMatchInfoTest() {

        val eventId = "602162"
        val homeTeamId = "133604"
        val awayTeamId = "133616"

        matchDetailPresenter.getDetailMatchInfo(eventId, homeTeamId, awayTeamId)

        argumentCaptor<MatchDetailRepositoryCallback<CombinedMatchTeamsResponse?>>().apply {
            verify(matchDetailRepository).getMatchDetail(eq(eventId), eq(homeTeamId), eq(awayTeamId), capture())
            firstValue.onDataLoaded(combinedMatchTeamsResponse)
        }

        val inOrder = inOrder(matchDetailView)

        inOrder.verify(matchDetailView, Mockito.times(1)).dataIsLoading()
        inOrder.verify(matchDetailView, Mockito.times(1)).showMatchDetailData(combinedMatchTeamsResponse)
        inOrder.verify(matchDetailView, Mockito.times(1)).dataLoadingFinished()


    }
}