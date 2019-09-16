package com.example.footballapps.presenter

import com.example.footballapps.model.MatchResponse
import com.example.footballapps.rule.RxImmediateSchedulerRule
import com.example.footballapps.service.MatchService
import com.example.footballapps.view.MatchView
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit

class MatchPresenterTest {

    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()!!

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Mock
    lateinit var matchService: MatchService

    @Mock
    lateinit var matchView: MatchView

    @Mock
    lateinit var matchResponse: MatchResponse

    private lateinit var matchPresenter: MatchPresenter

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)

        matchPresenter = MatchPresenter(matchView)
    }

    @Test
    fun getNextMatchInfoTest() {

        matchPresenter.getNextMatchInfo("4328")

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchData(matchPresenter.nextMatchesData)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()

    }

    @Test
    fun getPreviousMatchInfoTest() {
        matchPresenter.getPreviousMatchInfo("4328")

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchData(matchPresenter.lastMatchesData)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()
    }

    @Test
    fun getSearchMatchInfoTest() {
        matchPresenter.getSearchMatchInfo("man united")

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchData(matchPresenter.filteredSearchMatchesData)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()
    }
}