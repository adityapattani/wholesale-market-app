package com.b00957180.csci5409_ta.model

import com.google.gson.annotations.SerializedName

data class FinalDataItem (val commodity: String, val low_price: String, val high_price: String)

data class FinalDataResponse(@SerializedName("data") val data: List<FinalDataItem>)