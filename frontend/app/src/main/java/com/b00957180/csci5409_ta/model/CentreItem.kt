package com.b00957180.csci5409_ta.model

import com.google.gson.annotations.SerializedName

data class CentreItem (val centre: String)

data class CentreResponse(@SerializedName("data") val centres: List<CentreItem>)