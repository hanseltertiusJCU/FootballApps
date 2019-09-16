package com.example.footballapps.presenter

import com.example.footballapps.callback.MatchesRepositoryCallback
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.repository.MatchesRepository
import com.example.footballapps.view.MatchView
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

class MatchPresenterTest {

    @Mock
    lateinit var matchView: MatchView

    @Mock
    private lateinit var matchesRepository: MatchesRepository

    @Mock
    lateinit var matchResponse: MatchResponse

    private lateinit var matchPresenter: MatchPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        matchPresenter = MatchPresenter(matchView, matchesRepository)
    }

    @Test
    fun getNextMatchInfoTest() {

        val id = "4328"

        matchPresenter.getNextMatchInfo(id)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getNextMatches(eq(id), capture())
            firstValue.onDataLoaded(matchResponse)
        }

        val inOrder = inOrder(matchView)

        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchesData(matchResponse)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()

    }

    @Test
    fun getPreviousMatchInfoTest() {

        val id = "4328"

        matchPresenter.getPreviousMatchInfo(id)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getLastMatches(eq(id), capture())
            firstValue.onDataLoaded(matchResponse)
        }

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchesData(matchResponse)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()
    }

    @Test
    fun getSearchMatchInfoTest() {

        val query = "man united"

        matchPresenter.getSearchMatchInfo(query)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getSearchResultMatches(eq(query), capture())
            firstValue.onDataLoaded(matchResponse)
        }

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchesData(matchResponse)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()
    }
}