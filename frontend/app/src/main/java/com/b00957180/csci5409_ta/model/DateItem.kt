package com.b00957180.csci5409_ta.model

import com.google.gson.annotations.SerializedName

data class DateItem (val date: String)

data class DateResponse(@SerializedName("data") val dates: List<DateItem>)