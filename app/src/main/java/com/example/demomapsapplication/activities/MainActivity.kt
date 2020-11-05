package com.example.demomapsapplication.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.demomapsapplication.R
import com.example.demomapsapplication.pojos.PlacePojoClass
import com.example.demomapsapplication.utils.SQLiteDatableHelperClass
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var supportMapFragment: SupportMapFragment
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var MAP_REQUEST_CODE = 101
    private var SEARCH_PLACE_REQUEST_CODE = 10000
    private var SELECTED_PLACE_REQUEST_CODE = 1000
    private lateinit var currentLocation: Location
    private lateinit var selectedPlace: Place
    private var sqliteDatabaseHelper = SQLiteDatableHelperClass(this)
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveLocationBt.setOnClickListener(this)
        fetchLocationBt.setOnClickListener(this)
        currentLocationIv.setOnClickListener(this)
        searchMapEt.setOnClickListener(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        fetchLocation()
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap?.clear()
        googleMap = p0
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("Current Location")
        p0?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        p0?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
        p0?.addMarker(markerOptions)

    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, permissions, MAP_REQUEST_CODE)
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                currentLocation = it
                supportMapFragment.getMapAsync(this)
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MAP_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.saveLocationBt -> {
                if(searchMapEt.text.isNotEmpty())
                {
                    val success = sqliteDatabaseHelper.addPlacesData(selectedPlace).toInt()
                    when {
                        success == -1 -> {
                            Toast.makeText(this, "Error in saving data", Toast.LENGTH_SHORT).show()
                        }
                        success == -2 -> {
                            Toast.makeText(this, "Data already exists", Toast.LENGTH_SHORT).show()
                        }
                        success > 0 -> {
                            Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else
                {
                    Toast.makeText(this, "Search place to proceed", Toast.LENGTH_SHORT).show()
                }



            }
            R.id.fetchLocationBt -> startActivityForResult(
                Intent(
                    this,
                    SavedPlacedDetailActivity::class.java
                ), SELECTED_PLACE_REQUEST_CODE
            )
            R.id.currentLocationIv ->
            {
                searchMapEt.text.clear()
                fetchLocation()
            }
            R.id.searchMapEt -> {
                if (!Places.isInitialized()) {
                    Places.initialize(
                        applicationContext,
                        // "AIzaSyDsIG8XXKNR2B1pklpLlbx1cXh0GI7k76E",
                        "AIzaSyDhjJnMoVEtsh7yFl9fDeWOP10HPAfiCu0",
                        Locale.getDefault()
                    )
                }

                val fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
                )

                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields
                ).setCountry("IN")
                    .build(this)
                startActivityForResult(intent, SEARCH_PLACE_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SEARCH_PLACE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        selectedPlace = Autocomplete.getPlaceFromIntent(data)
                        searchMapEt.setText(selectedPlace.name.toString())
                        googleMap?.clear()
                        val marker = MarkerOptions().position(selectedPlace.latLng!!)
                        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(selectedPlace.latLng!!))
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                selectedPlace.latLng!!,
                                10f
                            )
                        )
                        googleMap?.addMarker(marker)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Toast.makeText(this,status.statusMessage,Toast.LENGTH_SHORT).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(this,"Operation canceled",Toast.LENGTH_SHORT).show()
                }

            }
        } else if (requestCode == SELECTED_PLACE_REQUEST_CODE) {
            data?.let {
                val place: PlacePojoClass = data.extras?.get("selectedPlace") as PlacePojoClass
                searchMapEt.setText(place.placeName)
                googleMap?.clear()
                val marker =
                    MarkerOptions().position(LatLng(place.placeLatitude, place.placeLongitude))
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(
                            place.placeLatitude,
                            place.placeLongitude
                        )
                    )
                )
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            place.placeLatitude,
                            place.placeLongitude
                        ), 10f
                    )
                )
                googleMap?.addMarker(marker)

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
