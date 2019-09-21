package com.example.footballapps.callback

interface PlayerDetailRepositoryCallback<T> {
    fun onDataLoaded(data: T?)
    fun onDataError()
}