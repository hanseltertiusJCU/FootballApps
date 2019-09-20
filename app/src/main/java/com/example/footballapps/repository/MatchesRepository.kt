package com.example.footballapps.repository

import com.example.footballapps.callback.MatchesRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.MatchItem
import com.example.footballapps.model.MatchResponse
import com.example.footballapps.service.MatchService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MatchesRepository {

    fun getNextMatches(id: String, callback: MatchesRepositoryCallback<MatchResponse?>) {
        RetrofitClient
            .createService(MatchService::class.java)
            .getLeagueNextMatchesResponse(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MatchResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(matchResponse: MatchResponse) {
                    val nextMatchesList = matchResponse.events
                    if (nextMatchesList != null) {
                        if (nextMatchesList.isNotEmpty()) {
                            callback.onDataLoaded(matchResponse)
                        } else {
                            callback.onDataError()
                        }
                    } else {
                        callback.onDataError()
                    }
                }

                override fun onError(error: Throwable) {
                    callback.onDataError()
                }

            })
    }

    fun getLastMatches(id: String, callback: MatchesRepositoryCallback<MatchResponse?>) {
        RetrofitClient
            .createService(MatchService::class.java)
            .getLeagueLastMatchesResponse(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MatchResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(matchResponse: MatchResponse) {
                    val lastMatchesList = matchResponse.events
                    if (lastMatchesList != null) {
                        if (lastMatchesList.isNotEmpty()) {
                            callback.onDataLoaded(matchResponse)
                        } else {
                            callback.onDataError()
                        }
                    } else {
                        callback.onDataError()
                    }
                }

                override fun onError(error: Throwable) {
                    callback.onDataError()
                }

            })
    }

    fun getSearchResultMatches(query: String, callback: MatchesRepositoryCallback<MatchResponse?>) {
        RetrofitClient
            .createService(MatchService::class.java)
            .getSearchResultMatchesResponse(query)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MatchResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(matchResponse: MatchResponse) {
                    val searchResultMatchesList = matchResponse.searchResultEvents
                    val filteredSearchResultMatchesList = mutableListOf<MatchItem>()

                    if (searchResultMatchesList != null) {
                        for (match in searchResultMatchesList) {
                            if (match.sportType.equals("Soccer")) {
                                filteredSearchResultMatchesList.add(match)
                            }
                        }
                        matchResponse.searchResultEvents = filteredSearchResultMatchesList
                    }

                    if (filteredSearchResultMatchesList.isNotEmpty()) {
                        callback.onDataLoaded(matchResponse)
                    } else {
                        callback.onDataError()
                    }
                }

                override fun onError(error: Throwable) {
                    callback.onDataError()
                }

            })
    }

    // todo: tinggal implement dari last match, next match dari team


}