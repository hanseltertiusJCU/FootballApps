package com.example.footballapps.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class PlayerResponse(
    @SerializedName("player")
    var playersList: List<PlayerItem>? = emptyList(),
    @SerializedName("players")
    var playerDetail: List<PlayerItem>? = emptyList()
)

@Parcelize
data class PlayerItem(
    @SerializedName("idPlayer")
    var playerId: String?,
    @SerializedName("strPlayer")
    var playerName: String?,
    @SerializedName("strCutout")
    var playerPhoto: String?,
    @SerializedName("strTeam")
    var playerTeam: String?,
    @SerializedName("strNumber")
    var playerShirtNumber: String?,
    @SerializedName("strNationality")
    var playerNationality: String?,
    @SerializedName("dateBorn")
    var playerBirthDate: String?,
    @SerializedName("dateSigned")
    var playerSignedDate: String?,
    @SerializedName("strPosition")
    var playerPosition: String?,
    @SerializedName("strSide")
    var playerStrongFoot: String?,
    @SerializedName("strBirthLocation")
    var playerBirthLocation: String?,
    @SerializedName("strDescriptionEN")
    var playerDescription: String?,
    @SerializedName("strHeight")
    var playerHeight: String?,
    @SerializedName("strWeight")
    var playerWeight: String?,
    @SerializedName("strThumb")
    var playerFanArt: String?,
    @SerializedName("strSigning")
    var playerTransferFee: String?,
    @SerializedName("strWage")
    var playerWages: String?,
    @SerializedName("strOutfitter")
    var playerOutfitter: String?,
    @SerializedName("strAgent")
    var playerAgent: String?,
    @SerializedName("strKit")
    var playerKit: String?
) : Parcelable