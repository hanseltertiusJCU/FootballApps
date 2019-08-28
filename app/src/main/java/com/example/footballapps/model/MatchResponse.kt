package com.example.footballapps.model

import com.google.gson.annotations.SerializedName

/*
{
    "events": [
        {
            "idEvent": "602149",
            "idSoccerXML": "402000",
            "strEvent": "Bournemouth vs Man City",
            "strFilename": "English Premier League 2019-08-24 Bournemouth vs Man City",
            "strSport": "Soccer",
            "idLeague": "4328",
            "strLeague": "English Premier League",
            "strSeason": "1920",
            "strDescriptionEN": "Can Bournemouth stop Manchester City? Probably not. Eight defeats from eight meetings in the Premier League speaks for itself. How Pep Guardiola’s side respond to completely dominating Tottenham but failing to win is key. If profligacy is again the order of the day then Eddie Howe could spring a surprise. A rather large if, however, considering the embarrassment of talent at Guardiola’s disposal.(The Guardian)",
            "strHomeTeam": "Bournemouth",
            "strAwayTeam": "Man City",
            "intHomeScore": null,
            "intRound": "3",
            "intAwayScore": null,
            "intSpectators": null,
            "strHomeGoalDetails": "",
            "strHomeRedCards": "",
            "strHomeYellowCards": "",
            "strHomeLineupGoalkeeper": "",
            "strHomeLineupDefense": "",
            "strHomeLineupMidfield": "",
            "strHomeLineupForward": "",
            "strHomeLineupSubstitutes": "",
            "strHomeFormation": null,
            "strAwayRedCards": "",
            "strAwayYellowCards": "",
            "strAwayGoalDetails": "",
            "strAwayLineupGoalkeeper": "",
            "strAwayLineupDefense": "",
            "strAwayLineupMidfield": "",
            "strAwayLineupForward": "",
            "strAwayLineupSubstitutes": "",
            "strAwayFormation": null,
            "intHomeShots": null,
            "intAwayShots": null,
            "dateEvent": "2019-08-25",
            "datetimeEventUTC": null,
            "strDate": "25/08/19",
            "strTime": "13:00:00+00:00",
            "strTVStation": null,
            "idHomeTeam": "134301",
            "idAwayTeam": "133613",
            "strResult": "",
            "strCircuit": null,
            "strCountry": null,
            "strCity": null,
            "strPoster": null,
            "strFanart": null,
            "strThumb": "https://www.thesportsdb.com/images/media/event/thumb/dxyq3e1566545623.jpg",
            "strBanner": null,
            "strMap": null,
            "strTweet1": "",
            "strTweet2": "",
            "strTweet3": "",
            "strVideo": "",
            "strLocked": "unlocked"
        }
     ]
}
 */
data class MatchResponse(
    @SerializedName("events")
    var events : List<MatchItem> = emptyList(),
    // todo: tinggal pake event seriaized name
    @SerializedName("event")
    var searchResultEvents : List<MatchItem> = emptyList()
)

data class MatchItem(
    @SerializedName("idEvent")
    var idEvent : String?,
    @SerializedName("strEvent")
    var strEvent : String?,
    @SerializedName("strLeague")
    var leagueName : String?,
    @SerializedName("intRound")
    var leagueMatchWeek : String?,
    @SerializedName("strHomeTeam")
    var homeTeamName : String?,
    @SerializedName("strAwayTeam")
    var awayTeamName : String?,
    @SerializedName("intHomeScore")
    var homeTeamScore : String?,
    @SerializedName("intAwayScore")
    var awayTeamScore : String?,
    @SerializedName("dateEvent")
    var dateEvent : String?,
    @SerializedName("strTime")
    var timeEvent : String?,
    @SerializedName("strSport")
    var sportType : String?,
    @SerializedName("intSpectators")
    var spectators : String?,
    @SerializedName("strHomeGoalDetails")
    var homeTeamGoalDetails : String?,
    @SerializedName("strAwayGoalDetails")
    var awayTeamGoalDetails : String?,
    @SerializedName("strHomeYellowCards")
    var homeTeamYellowCards : String?,
    @SerializedName("strHomeRedCards")
    var homeTeamRedCards : String?,
    @SerializedName("strAwayYellowCards")
    var awayTeamYellowCards : String?,
    @SerializedName("strAwayRedCards")
    var awayTeamRedCards : String?,
    @SerializedName("strHomeFormation")
    var homeTeamFormation : String?,
    @SerializedName("strHomeLineupGoalkeeper")
    var homeTeamGoalkeeper : String?,
    @SerializedName("strHomeLineupDefense")
    var homeTeamDefense : String?,
    @SerializedName("strHomeLineupMidfield")
    var homeTeamMidfield : String?,
    @SerializedName("strHomeLineupForward")
    var homeTeamForward : String?,
    @SerializedName("strHomeLineupSubstitutes")
    var homeTeamSubstitutes : String?,
    @SerializedName("strAwayFormation")
    var awayTeamFormation : String?,
    @SerializedName("strAwayLineupGoalkeeper")
    var awayTeamGoalkeeper : String?,
    @SerializedName("strAwayLineupDefense")
    var awayTeamDefense : String?,
    @SerializedName("strAwayLineupMidfield")
    var awayTeamMidfield : String?,
    @SerializedName("strAwayLineupForward")
    var awayTeamForward : String?,
    @SerializedName("strAwayLineupSubstitutes")
    var awayTeamSubstitutes : String?
)