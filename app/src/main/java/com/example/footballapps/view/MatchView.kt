package com.example.footballapps.view

interface MatchView {
    // todo: data is loading, data is finished
    fun dataIsLoading()
    fun dataLoadingFinished()
    // todo: show
}

// todo: untuk presenter pake 1 presenter dengan beberapa function yang penting si reusable aja si ya