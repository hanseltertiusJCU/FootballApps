package com.example.footballapps.callback

interface LeagueDetailRepositoryCallback<T> {

    fun onDataLoaded(data : T?)
    fun onDataError()
}