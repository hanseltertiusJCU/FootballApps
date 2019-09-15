package com.example.footballapps.presenter

import android.util.Log
import com.example.footballapps.R
import com.example.footballapps.application.FootballApps
import com.example.footballapps.model.LeagueDetailResponse
import com.example.footballapps.rule.RxImmediateSchedulerRule
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
import org.mockito.InOrder
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import org.jetbrains.anko.Android
import org.junit.Rule
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


class LeagueDetailPresenterTest {

    @Rule
    @JvmField val rule = MockitoJUnit.rule()!!

    @Rule
    @JvmField var testSchedulerRule = RxImmediateSchedulerRule()

    @Mock
    lateinit var leagueDetailService : LeagueDetailService

    @Mock
    lateinit var leagueDetailView: LeagueDetailView

    @Mock
    lateinit var leagueDetailResponse : LeagueDetailResponse

    lateinit var leagueDetailPresenter: LeagueDetailPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        leagueDetailPresenter = LeagueDetailPresenter(leagueDetailView)
    }

    @Test
    fun getLeagueDetailInfo() {
        Mockito.`when`(leagueDetailService
            .getLeagueDetailResponse("4328"))
            .thenReturn(Observable.just(leagueDetailResponse))

        leagueDetailPresenter.getLeagueDetailInfo("4328")

        val inOrder = inOrder(leagueDetailView)
        inOrder.verify(leagueDetailView, times(1)).dataIsLoading()
//        inOrder.verify(leagueDetailView, times(1)).showLeagueDetailInfo(leagueDetailResponse.leagues?.first()!!)
        inOrder.verify(leagueDetailView, times(1)).dataLoadingFinished()

    }
}