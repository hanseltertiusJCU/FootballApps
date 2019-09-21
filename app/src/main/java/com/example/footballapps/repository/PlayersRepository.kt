package com.example.footballapps.repository

import com.example.footballapps.callback.PlayersRepositoryCallback
import com.example.footballapps.client.RetrofitClient
import com.example.footballapps.model.PlayerResponse
import com.example.footballapps.service.PlayersService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PlayersRepository {

    fun getPlayers(id : String, callback : PlayersRepositoryCallback<PlayerResponse?>) {
        RetrofitClient
            .createService(PlayersService::class.java)
            .getPlayersResponse(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<PlayerResponse> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(playerResponse: PlayerResponse) {
                    val playersList = playerResponse.playersList
                    if(playersList != null){
                        if(playersList.isNotEmpty()){
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