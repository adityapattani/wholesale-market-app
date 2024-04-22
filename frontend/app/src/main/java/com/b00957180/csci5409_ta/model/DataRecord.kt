package com.b00957180.csci5409_ta.model

import java.util.Date

data class DataRecord (
    val date: Date,
    val centre: String,
    val commodity: String,
    val variety: String,
    val grade_category: String,
    val country: String,
    val province: String,
    val low_price: Float,
    val high_price: Float,
    val package_type: String,
    val centre_type: String,
    val package_quantity: String,
    val package_weight: String,
    val units: String,
    val package_size: String,
)