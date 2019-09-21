package com.example.footballapps.view

import com.example.footballapps.model.TeamResponse

interface TeamsView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showTeamsData(teamsResponse : TeamResponse)
}