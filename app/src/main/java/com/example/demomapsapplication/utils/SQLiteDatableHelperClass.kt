package com.example.demomapsapplication.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.ColorSpace.Model
import android.widget.Toast
import com.example.demomapsapplication.pojos.PlacePojoClass
import com.google.android.libraries.places.api.model.Place
import com.google.gson.Gson


class SQLiteDatableHelperClass(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "MapsPlacesDatabase"
        private val PLACES_TABLE = "PlacesTable"
        private val KEY_PLACE_ID = "id"
        private val KEY_PLACE_NAME = "name"
        private val KEY_PLACE_LATITUDE = "latitude"
        private val KEY_PLACE_LONGITUDE = "longitude"
        private val KEY_PLACE_ADDRESS = "address"
       // private val KEY_PLACE_DETAIL = "placeDetail"
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val CREATE_TABLE =
            ("CREATE TABLE $PLACES_TABLE($KEY_PLACE_ID INTEGER PRIMARY KEY AUTOINCREMENT,$KEY_PLACE_NAME TEXT,$KEY_PLACE_LATITUDE DOUBLE,$KEY_PLACE_LONGITUDE DOUBLE,$KEY_PLACE_ADDRESS TEXT)")
          //  ("CREATE TABLE $PLACES_TABLE($KEY_PLACE_ID INTEGER PRIMARY KEY AUTOINCREMENT,$KEY_PLACE_DETAIL TEXT)")
        p0?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $PLACES_TABLE")
        onCreate(p0)
    }

    fun addPlacesData(place: Place): Long {
        val success :Long
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $PLACES_TABLE WHERE $KEY_PLACE_LATITUDE = ${place.latLng?.latitude} AND $KEY_PLACE_LONGITUDE = ${place.latLng?.longitude}",null)
        if(cursor.count == 0)
        {
            val contentValues = ContentValues()
            contentValues.put(KEY_PLACE_NAME, place.name)
            contentValues.put(KEY_PLACE_LATITUDE, place.latLng?.latitude)
            contentValues.put(KEY_PLACE_LONGITUDE, place.latLng?.longitude)
            contentValues.put(KEY_PLACE_ADDRESS, place.address)
            success = db.insert(PLACES_TABLE,null,contentValues)
            db.close()
        }
        else
        {
            success = -2
        }

        return success
    }

    fun fetchPlacesData(): ArrayList<PlacePojoClass>{
        val placesAl = ArrayList<PlacePojoClass>()
        val db = this.readableDatabase
        val fetchDetailsQuery = "SELECT  * FROM $PLACES_TABLE"
        var cursor : Cursor? = null
        try {
            cursor=db.rawQuery(fetchDetailsQuery,null)
           // db.execSQL(fetchDetailsQuery)
        }
        catch (e: SQLException)
        {
            db.execSQL(fetchDetailsQuery)
        }
        var placeName: String
        var placeLatitude: Double
        var placeLongitude: Double
        var placeAddress: String
        if (cursor?.moveToFirst()!!) {
            do {
                placeName = cursor.getString(cursor.getColumnIndex(KEY_PLACE_NAME))
                placeLatitude = cursor.getDouble(cursor.getColumnIndex(KEY_PLACE_LATITUDE))
                placeLongitude = cursor.getDouble(cursor.getColumnIndex(KEY_PLACE_LONGITUDE))
                placeAddress = cursor.getString(cursor.getColumnIndex(KEY_PLACE_ADDRESS))
              /*  val gson = Gson()
                val placeDetailObj: Place = gson.fromJson(placeDetail, Place::class.java)*/

                placesAl.add(PlacePojoClass(placeName,placeLatitude, placeLongitude, placeAddress))
            } while (cursor.moveToNext())
        }
        return placesAl

    }

}