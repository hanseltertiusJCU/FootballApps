package com.example.footballapps.callback

interface MatchDetailRepositoryCallback <T> {

    fun onDataLoaded(data : T?)
    fun onDataError()
}