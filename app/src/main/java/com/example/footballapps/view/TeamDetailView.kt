package com.example.footballapps.view

import com.example.footballapps.model.TeamResponse

interface TeamDetailView {
    fun dataIsLoading()
    fun dataLoadingFinished()
    fun dataFailedToLoad()
    fun showTeamDetailData(teamsResponse : TeamResponse)
}