package com.b00957180.csci5409_ta.constants

class AppConstants {
    companion object {
//        const val BASE_URL = "http://18.234.169.178/"
        const val BASE_URL = "http://192.168.4.27/"
        const val APP_SHARED_PREFERENCES = "CSCI5409"
        const val SP_IS_SIGNED_IN_KEY = "isSignedIn"
        const val GET_DATES_QUERY = "SELECT DISTINCT date FROM wholesale_sor.wholesale_cleaned_input"
        const val GET_FIELDS_QUERY = "SELECT DISTINCT commodity, variety, grade_category, country, package_type FROM wholesale_sor.wholesale_cleaned_input"
        const val GET_CENTRE_QUERY = "SELECT DISTINCT centre FROM wholesale_sor.wholesale_cleaned_input"
    }
}