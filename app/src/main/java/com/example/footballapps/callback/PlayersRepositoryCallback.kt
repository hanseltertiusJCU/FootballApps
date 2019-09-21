package com.example.footballapps.callback

interface PlayersRepositoryCallback<T> {
    fun onDataLoaded(data: T?)
    fun onDataError()
}