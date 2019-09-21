package com.example.footballapps.callback

interface LeagueTableRepositoryCallback<T> {
    fun onDataLoaded(data: T?)
    fun onDataError()
}