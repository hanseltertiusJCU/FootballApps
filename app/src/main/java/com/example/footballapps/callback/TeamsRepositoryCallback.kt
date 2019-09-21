package com.example.footballapps.callback

interface TeamsRepositoryCallback<T> {
    fun onDataLoaded(data: T?)
    fun onDataError()
}