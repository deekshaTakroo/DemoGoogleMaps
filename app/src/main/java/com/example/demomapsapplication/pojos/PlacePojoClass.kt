package com.example.demomapsapplication.pojos

import com.google.android.libraries.places.api.model.Place
import java.io.Serializable

data class PlacePojoClass(val placeName: String, val placeLatitude: Double, val placeLongitude: Double,  val placeAddress: String): Serializable {


}