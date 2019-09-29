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
    private lateinit var matchView: MatchView

    @Mock
    private lateinit var matchesRepository: MatchesRepository

    @Mock
    private lateinit var matchResponse: MatchResponse

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
            verify(matchesRepository).getLeagueNextMatches(eq(id), capture())
            firstValue.onDataLoaded(matchResponse)
        }

        val inOrder = inOrder(matchView)

        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchesData(matchResponse)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()

    }

    @Test
    fun getFailedNextMatchInfoTest() {

        val id = ""

        matchPresenter.getNextMatchInfo(id)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getLeagueNextMatches(eq(id), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(matchView)

        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).dataFailedToLoad()

    }

    @Test
    fun getPreviousMatchInfoTest() {

        val id = "4328"

        matchPresenter.getPreviousMatchInfo(id)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getLeagueLastMatches(eq(id), capture())
            firstValue.onDataLoaded(matchResponse)
        }

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchesData(matchResponse)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()
    }

    @Test
    fun getFailedPreviousMatchInfoTest() {

        val id = ""

        matchPresenter.getPreviousMatchInfo(id)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getLeagueLastMatches(eq(id), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).dataFailedToLoad()

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

    @Test
    fun getFailedSearchMatchInfoTest() {

        val query = "abracadabra"

        matchPresenter.getSearchMatchInfo(query)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getSearchResultMatches(eq(query), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).dataFailedToLoad()
    }

    @Test
    fun getTeamPreviousMatchInfoTest() {

        val id = "133604"

        matchPresenter.getTeamLastMatchInfo(id)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getTeamLastMatches(eq(id), capture())
            firstValue.onDataLoaded(matchResponse)
        }

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchesData(matchResponse)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()
    }

    @Test
    fun getFailedTeamPreviousMatchInfoTest() {
        val id = ""

        matchPresenter.getTeamLastMatchInfo(id)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getTeamLastMatches(eq(id), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).dataFailedToLoad()
    }

    @Test
    fun getTeamNextMatchInfoTest() {
        val id = "133604"

        matchPresenter.getTeamNextMatchInfo(id)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getTeamNextMatches(eq(id), capture())
            firstValue.onDataLoaded(matchResponse)
        }

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).showMatchesData(matchResponse)
        inOrder.verify(matchView, times(1)).dataLoadingFinished()

    }

    @Test
    fun getFailedTeamNextMatchInfoTest() {
        val id = ""

        matchPresenter.getTeamNextMatchInfo(id)

        argumentCaptor<MatchesRepositoryCallback<MatchResponse?>>().apply {
            verify(matchesRepository).getTeamNextMatches(eq(id), capture())
            firstValue.onDataError()
        }

        val inOrder = inOrder(matchView)
        inOrder.verify(matchView, times(1)).dataIsLoading()
        inOrder.verify(matchView, times(1)).dataFailedToLoad()
    }
}