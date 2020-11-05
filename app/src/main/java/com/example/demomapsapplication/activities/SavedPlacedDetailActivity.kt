package com.example.demomapsapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demomapsapplication.R
import com.example.demomapsapplication.interfaces.RecyclerViewPositionListener
import com.example.demomapsapplication.adapters.PlacesDetailRvAdapter
import com.example.demomapsapplication.pojos.PlacePojoClass
import com.example.demomapsapplication.utils.SQLiteDatableHelperClass
import kotlinx.android.synthetic.main.activity_saved_placed_detail.*

class SavedPlacedDetailActivity : AppCompatActivity(), RecyclerViewPositionListener {

    lateinit var placesDetailAl : ArrayList<PlacePojoClass>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_placed_detail)

        val sqLiteDatableHelper = SQLiteDatableHelperClass(this)
        placesDetailAl = sqLiteDatableHelper.fetchPlacesData()
        sqLiteDatableHelper.close()

        placesDetailRv.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        val placesDetailRvAdapter = PlacesDetailRvAdapter(placesDetailAl,this)
        placesDetailRv.adapter = placesDetailRvAdapter

    }

    override fun getRecItemClickedPos(position: Int) {
      val selectedPlace = placesDetailAl[position]
        val data = Intent(this,MainActivity::class.java)
        data.putExtra("selectedPlace", selectedPlace)
        setResult(1000,data)
        finish()
    }
}