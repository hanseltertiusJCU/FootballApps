package com.example.footballapps.presenter

import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.service.LeagueDetailService
import com.example.footballapps.view.LeagueDetailView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*

class LeagueDetailPresenterTest {

    @Mock
    lateinit var leagueDetailService : LeagueDetailService

    @Mock
    lateinit var leagueDetailView: LeagueDetailView

    lateinit var leagueDetailPresenter: LeagueDetailPresenter


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        leagueDetailPresenter = LeagueDetailPresenter(leagueDetailView)
    }

    @Test
    fun getLeagueDetailInfo() {
        val leagueDetailResponse = LeagueDetailResponse()

        Mockito.`when`(leagueDetailService
            .getLeagueDetailResponse("4328"))
            .thenReturn(Observable.just(leagueDetailResponse))
    }
}