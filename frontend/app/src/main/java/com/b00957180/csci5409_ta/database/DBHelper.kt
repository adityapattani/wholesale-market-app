package com.b00957180.csci5409_ta.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.b00957180.csci5409_ta.model.CentreItem
import com.b00957180.csci5409_ta.model.DataField
import com.b00957180.csci5409_ta.model.DateItem

/**
 * The class responsible for communicating with the SQLite database
 */
class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // Create tables on db creation
    override fun onCreate(db: SQLiteDatabase?) {
        val centreQuery = ("CREATE TABLE Centre(centre VARCHAR(255));")

        db?.execSQL(centreQuery)

        val dataFieldQuery = ("CREATE TABLE Field(commodity VARCHAR(255), variety VARCHAR(255), " +
                "grade_category VARCHAR(255), country VARCHAR(255), package_type VARCHAR(255));")

        db?.execSQL(dataFieldQuery)

        val dateQuery = ("CREATE TABLE Date(date VARCHAR(255));")

        db?.execSQL(dateQuery)
    }

    // Drop and recreate tables on upgrading db
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Centre;")
        db?.execSQL("DROP TABLE IF EXISTS Field;")
        db?.execSQL("DROP TABLE IF EXISTS Date;")
        onCreate(db)
    }

    fun truncateTables() {
        val db = this.writableDatabase

        db.execSQL("DELETE FROM Centre;")
        db.execSQL("DELETE FROM Date;")
        db.execSQL("DELETE FROM Field;")

        db.close()
    }

    fun saveDatesOrCentres(table: String, data: List<String>): Int {
        Log.e("Entering", table)
        val db = this.writableDatabase
        var retVal = 0

        for (dataItem in data) {
            val values = ContentValues()
            if (table == "Date") {
                values.put("date", dataItem)
            } else {
                values.put("centre", dataItem)
            }

            if (db.insert(table, null, values) != -1L) {
                retVal += 1
            }
        }

        db.close()
        return retVal
    }

    fun saveDataFields(data: List<DataField>): Int {
        Log.e("Entering", "Fields")
        val db = this.writableDatabase
        var retVal = 0

        for (dataItem in data) {
            val values = ContentValues()
            values.put("commodity", dataItem.commodity)
            values.put("variety", dataItem.variety)
            values.put("grade_category", dataItem.grade_category)
            values.put("country", dataItem.country)
            values.put("package_type", dataItem.package_type)

            if (db.insert("Field", null, values) != -1L) {
                retVal += 1
            }
        }

        db.close()
        return retVal
    }

    fun getAllDates(): List<String> {
        val db = readableDatabase
        val dates = mutableListOf<String>()

        val query = "SELECT * FROM Date"
        val cursor = db.rawQuery(query, null)

        // Iterate through the cursor and create Expense objects
        cursor.use {
            while (it.moveToNext()) {
                val date = it.getString(0)

                dates.add(date)
            }
        }
        db.close()

        return dates
    }

    fun getAllCentres(): List<String> {
        val db = readableDatabase
        val centres = mutableListOf<String>()

        val query = "SELECT * FROM Centre"
        val cursor = db.rawQuery(query, null)

        // Iterate through the cursor and create Expense objects
        cursor.use {
            while (it.moveToNext()) {
                val centre = it.getString(0)

                centres.add(centre)
            }
        }
        db.close()

        return centres
    }

    companion object {
        private const val DATABASE_NAME = "wholesale.db"
        private const val DATABASE_VERSION = 1
    }
}