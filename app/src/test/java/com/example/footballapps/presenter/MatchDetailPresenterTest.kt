package com.example.footballapps.presenter

import com.example.footballapps.rule.RxImmediateSchedulerRule
import com.example.footballapps.view.MatchDetailView
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

    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()!!

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Mock
    lateinit var matchDetailView: MatchDetailView

    private lateinit var matchDetailPresenter: MatchDetailPresenter

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        matchDetailPresenter = MatchDetailPresenter(matchDetailView)
    }

    @Test
    fun getDetailMatchInfoTest() {

        matchDetailPresenter.getDetailMatchInfo("602162", "133604", "133616")

        val inOrder = inOrder(matchDetailView)
        inOrder.verify(matchDetailView, Mockito.times(1)).dataIsLoading()
        inOrder.verify(matchDetailView, Mockito.times(1)).showMatchData(matchDetailPresenter.combinedMatchHomaAwayTeamsResponse.matchDetailResponse.events?.first()!!)
        inOrder.verify(matchDetailView, Mockito.times(1)).showHomeTeamBadge(matchDetailPresenter.combinedMatchHomaAwayTeamsResponse.homeTeamResponse.teams?.first()?.teamBadge)
        inOrder.verify(matchDetailView, Mockito.times(1)).showAwayTeamBadge(matchDetailPresenter.combinedMatchHomaAwayTeamsResponse.awayTeamResponse.teams?.first()?.teamBadge)
        inOrder.verify(matchDetailView, Mockito.times(1)).dataLoadingFinished()


    }
}