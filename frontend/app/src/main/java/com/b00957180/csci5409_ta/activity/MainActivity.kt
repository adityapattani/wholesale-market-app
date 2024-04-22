package com.b00957180.csci5409_ta.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.b00957180.csci5409_ta.R
import com.b00957180.csci5409_ta.adapter.CommodityListAdapter
import com.b00957180.csci5409_ta.constants.AppConstants
import com.b00957180.csci5409_ta.database.DBHelper
import com.b00957180.csci5409_ta.model.CentreResponse
import com.b00957180.csci5409_ta.model.DataFieldResponse
import com.b00957180.csci5409_ta.model.DateResponse
import com.b00957180.csci5409_ta.model.FinalDataItem
import com.b00957180.csci5409_ta.model.FinalDataResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.IOException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var dbHelper: DBHelper
    private lateinit var loadingLayout: RelativeLayout
    private lateinit var dataLayout: RelativeLayout
    private lateinit var submitBtn: Button

    private lateinit var dateSpinner: Spinner
    private lateinit var centreSpinner: Spinner
    private lateinit var commodityList: RecyclerView
    private lateinit var commodityListAdapter: CommodityListAdapter

    private var dataItems: List<FinalDataItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this, null)
        dbHelper.truncateTables()

        loadingLayout = findViewById(R.id.layout_loader)
        dataLayout = findViewById(R.id.layout_data)
        submitBtn = findViewById(R.id.btn_submit)
        dateSpinner = findViewById(R.id.date_filter)
        centreSpinner = findViewById(R.id.centre_filter)

        commodityList = findViewById(R.id.commodity_list)

        loadingLayout.visibility = View.VISIBLE
        dataLayout.visibility = View.GONE

        commodityListAdapter = CommodityListAdapter(dataItems, this)

        commodityList.layoutManager = LinearLayoutManager(this)
        commodityList.adapter = commodityListAdapter

        Thread {
            loadData()

            runOnUiThread {
                loadingLayout.visibility = View.GONE
                dataLayout.visibility = View.VISIBLE

                val dateAdapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, dbHelper.getAllDates().toTypedArray())
                dateSpinner.adapter = dateAdapter
                dateSpinner.setSelection(0)

                val centreAdapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_item, dbHelper.getAllCentres().toTypedArray())
                centreSpinner.adapter = centreAdapter
                centreSpinner.setSelection(0)

                submitBtn.setOnClickListener {
                    Toast.makeText(this, "Getting data from API", Toast.LENGTH_SHORT).show()
                    loadDataForDateCentre(dateSpinner.selectedItem.toString(), centreSpinner.selectedItem.toString())
                }
            }
        }.start()
    }

    private fun loadDataForDateCentre(date: String, centre: String) {
        Thread {
            val dateJson = JSONObject()
            dateJson.put("query", "SELECT DISTINCT commodity, low_price, high_price " +
                    "FROM wholesale_sor.wholesale_cleaned_input " +
                    "WHERE date = '$date' AND centre = '$centre'")

            val mediaType = "application/json".toMediaTypeOrNull()
            val finalRequestBody = RequestBody.create(mediaType, dateJson.toString())

            val finalRequest = Request.Builder()
                .url(AppConstants.BASE_URL + "query")
                .post(finalRequestBody)
                .build()

            client.newCall(finalRequest).execute().use { response ->

                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val gson = Gson()
                val responseData = gson.fromJson(response.body!!.string(), FinalDataResponse::class.java)

                // Extract dates
                dataItems = responseData.data

                // Filter out invalid dates
                runOnUiThread {
                    commodityListAdapter.setData(dataItems)
                    commodityListAdapter.notifyDataSetChanged()
                }
            }
        }.start()
    }

    private fun loadData() {
        val dateJson = JSONObject()
        dateJson.put("query", AppConstants.GET_DATES_QUERY)

        val mediaType = "application/json".toMediaTypeOrNull()
        val dateRequestBody = RequestBody.create(mediaType, dateJson.toString())

        val dateRequest = Request.Builder()
            .url(AppConstants.BASE_URL + "query")
            .post(dateRequestBody)
            .build()

        client.newCall(dateRequest).execute().use { response ->

            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val gson = Gson()
            val responseData = gson.fromJson(response.body!!.string(), DateResponse::class.java)

            // Extract dates
            val datesList = responseData.dates.map { it.date }

            // Filter out invalid dates
            val validDates = datesList.filter { it != "Date" }
            dbHelper.saveDatesOrCentres("Date", validDates)
        }

        val centreJson = JSONObject()
        centreJson.put("query", AppConstants.GET_CENTRE_QUERY)

        val centreRequestBody = RequestBody.create(mediaType, centreJson.toString())

        val centreRequest = Request.Builder()
            .url(AppConstants.BASE_URL + "query")
            .post(centreRequestBody)
            .build()

        client.newCall(centreRequest).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val gson = Gson()
            val responseData = gson.fromJson(response.body!!.string(), CentreResponse::class.java)

            // Extract dates
            val centresList = responseData.centres.map { it.centre }

            // Filter out invalid dates
            val validCentres = centresList.filter { it != "CentreEn_CentreAn" }
            dbHelper.saveDatesOrCentres("Centre", validCentres)
        }

        val dataJson = JSONObject()
        dataJson.put("query", AppConstants.GET_FIELDS_QUERY)

        val dataRequestBody = RequestBody.create(mediaType, dataJson.toString())

        val dataFieldRequest = Request.Builder()
            .url(AppConstants.BASE_URL + "query")
            .post(dataRequestBody)
            .build()

        client.newCall(dataFieldRequest).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val gson = Gson()
            val responseData =
                gson.fromJson(response.body!!.string(), DataFieldResponse::class.java)

            dbHelper.saveDataFields(responseData.data)
        }
    }
}