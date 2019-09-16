package com.example.footballapps.callback

interface MatchesRepositoryCallback<T> {

    fun onDataLoaded(data : T?)
    fun onDataError()
}