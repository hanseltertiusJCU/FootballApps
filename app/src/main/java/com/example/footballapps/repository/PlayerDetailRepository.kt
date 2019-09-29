package com.example.footballapps.repository

import com.example.footballapps.callback.PlayerDetailRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.PlayerResponse
import com.example.footballapps.service.PlayersService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PlayerDetailRepository {

    fun getPlayerDetail(id: String, callback: PlayerDetailRepositoryCallback<PlayerResponse?>) {
        RetrofitClient
            .createService(PlayersService::class.java)
            .getPlayerDetailResponse(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<PlayerResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(playerResponse: PlayerResponse) {
                    val playerDetail = playerResponse.playerDetail
                    if (playerDetail != null) {
                        if (playerDetail.isNotEmpty()) {
                            callback.onDataLoaded(playerResponse)
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
}