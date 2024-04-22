package com.b00957180.csci5409_ta.model

import com.google.gson.annotations.SerializedName

data class DataField(val commodity: String, val variety: String, val grade_category: String, val country: String, val package_type: String)

data class DataFieldResponse(@SerializedName("data") val data: List<DataField>)
