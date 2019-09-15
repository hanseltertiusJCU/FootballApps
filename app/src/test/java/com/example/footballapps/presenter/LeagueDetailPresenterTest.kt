package com.example.footballapps.presenter

import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.rule.RxImmediateSchedulerRule
import com.example.footballapps.service.LeagueDetailService
import com.example.footballapps.view.LeagueDetailView
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit


class LeagueDetailPresenterTest {

    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()!!

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Mock
    lateinit var leagueDetailService: LeagueDetailService

    @Mock
    lateinit var leagueDetailView: LeagueDetailView

    @Mock
    lateinit var leagueDetailResponse: LeagueDetailResponse

    private lateinit var leagueDetailPresenter: LeagueDetailPresenter



    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        leagueDetailPresenter = LeagueDetailPresenter(leagueDetailView)
    }

    @Test
    fun getLeagueDetailInfo() {
        Mockito.`when`(
            leagueDetailService
                .getLeagueDetailResponse("4328")
        ).thenReturn(Observable.just(leagueDetailResponse))

        leagueDetailPresenter.getLeagueDetailInfo("4328")

        val inOrder = inOrder(leagueDetailView)
        inOrder.verify(leagueDetailView, times(1)).dataIsLoading()
//        inOrder.verify(leagueDetailView, times(1))
//            .showLeagueDetailInfo(leagueDetailResponse.leagues?.first()!!)
        inOrder.verify(leagueDetailView, times(1))
            .showLeagueDetailInfo(leagueDetailPresenter.leaguesData.first())
        inOrder.verify(leagueDetailView, times(1)).dataLoadingFinished()

    }
}