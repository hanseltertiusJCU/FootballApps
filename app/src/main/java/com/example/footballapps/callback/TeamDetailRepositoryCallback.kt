package com.example.footballapps.callback

interface TeamDetailRepositoryCallback<T> {
    fun onDataLoaded(data: T?)
    fun onDataError()
}