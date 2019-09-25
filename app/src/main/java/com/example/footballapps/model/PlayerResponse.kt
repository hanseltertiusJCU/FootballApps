package com.example.footballapps.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/*
{
    "players": [
        {
            "idPlayer": "34165469",
            "idTeam": "133604",
            "idTeamNational": null,
            "idSoccerXML": "9",
            "idPlayerManager": null,
            "strNationality": "Spain",
            "strPlayer": "Unai Emery",
            "strTeam": "Arsenal",
            "strTeamNational": null,
            "strSport": "Soccer",
            "intSoccerXMLTeamID": null,
            "dateBorn": "1971-11-03",
            "strNumber": null,
            "dateSigned": null,
            "strSigning": "",
            "strWage": "",
            "strOutfitter": null,
            "strKit": null,
            "strAgent": null,
            "strBirthLocation": "Hondarribia, Spain",
            "strDescriptionEN": "Unai Emery Etxegoien (Spanish pronunciation: ; born 3 November 1971) is a Spanish football manager and former player. He is the head coach of Premier League club Arsenal.\r\n\r\nAfter a modest playing career, spent mostly in Spain's Segunda División, Emery transitioned into coaching after retiring in 2004. He began at Lorca Deportiva CF, where he achieved promotion to the Segunda División in his first season, and was awarded the Miguel Muñoz Trophy. He then joined Almería, who he led to promotion to La Liga for the first time in the club's history. This earned him a move to La Liga heavyweights Valencia, where he regularly led the team to top three finishes. After leaving Valencia, he coached Spartak Moscow for six months, before moving to Sevilla in 2013.\r\n\r\nAt Sevilla, Emery gained plaudits for his style of football, and shrewdness in the transfer market alongside Monchi, the club's director of football. He achieved an unprecedented three consecutive Europa League victories, which earned him a move to French club Paris Saint-Germain in 2016. Although he achieved limited European success at PSG, he won the Coupe de France, Coupe de la Ligue and Trophée des Champions in his first year. He won all four domestic trophies in his second season, including Ligue 1, the Coupe de France, the Coupe de la Ligue and the Trophée des Champions. After the expiry of his contract, he moved to English side Arsenal in 2018.",
            "strDescriptionDE": null,
            "strDescriptionFR": null,
            "strDescriptionCN": null,
            "strDescriptionIT": null,
            "strDescriptionJP": null,
            "strDescriptionRU": null,
            "strDescriptionES": null,
            "strDescriptionPT": null,
            "strDescriptionSE": null,
            "strDescriptionNL": null,
            "strDescriptionHU": null,
            "strDescriptionNO": null,
            "strDescriptionIL": null,
            "strDescriptionPL": null,
            "strGender": "Male",
            "strSide": null,
            "strPosition": "Manager",
            "strCollege": null,
            "strFacebook": "",
            "strWebsite": "",
            "strTwitter": "",
            "strInstagram": "",
            "strYoutube": "",
            "strHeight": "1.80 m (5 ft 11 in)",
            "strWeight": "",
            "intLoved": "0",
            "strThumb": "https://www.thesportsdb.com/images/media/player/thumb/gthgk01549363867.jpg",
            "strCutout": null,
            "strRender": null,
            "strBanner": null,
            "strFanart1": null,
            "strFanart2": null,
            "strFanart3": null,
            "strFanart4": null,
            "strCreativeCommons": null,
            "strLocked": "unlocked"
        }
    ]
}
 */

data class PlayerResponse(
    @SerializedName("player")
    var playersList : List<PlayerItem>? = emptyList(),
    @SerializedName("players")
    var playerDetail : List<PlayerItem>? = emptyList()
)

@Parcelize
data class PlayerItem(
    @SerializedName("idPlayer")
    var playerId : String?,
    @SerializedName("strPlayer")
    var playerName : String?,
    @SerializedName("strCutout")
    var playerPhoto : String?,
    @SerializedName("strTeam")
    var playerTeam : String?,
    @SerializedName("strNumber")
    var playerShirtNumber : String?,
    @SerializedName("strNationality")
    var playerNationality : String?,
    @SerializedName("dateBorn")
    var playerBirthDate : String?,
    @SerializedName("dateSigned")
    var playerSignedDate : String?,
    @SerializedName("strPosition")
    var playerPosition : String?,
    @SerializedName("strSide")
    var playerStrongFoot : String?,
    @SerializedName("strBirthLocation")
    var playerBirthLocation : String?,
    @SerializedName("strDescriptionEN")
    var playerDescription : String?,
    @SerializedName("strHeight")
    var playerHeight : String?,
    @SerializedName("strWeight")
    var playerWeight : String?,
    @SerializedName("strThumb")
    var playerFanArt : String?
) : Parcelable