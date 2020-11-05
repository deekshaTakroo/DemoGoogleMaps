package com.example.demomapsapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demomapsapplication.R
import com.example.demomapsapplication.interfaces.RecyclerViewPositionListener
import com.example.demomapsapplication.pojos.PlacePojoClass
import kotlinx.android.synthetic.main.places_detail_recycler_item.view.*

class PlacesDetailRvAdapter(val placesAl : ArrayList<PlacePojoClass>, val recyclerViewPositionListener: RecyclerViewPositionListener) : RecyclerView.Adapter<PlacesDetailRvAdapter.PlacesDetailRViewHolder>() {

    class PlacesDetailRViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.placeNameTv
            itemView.placeAddressTv
            itemView.placeLatLongTv
            itemView.locateInMapBt
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlacesDetailRvAdapter.PlacesDetailRViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.places_detail_recycler_item,parent,false)
        return PlacesDetailRViewHolder(view)
    }

    override fun getItemCount(): Int {
       return placesAl.size
    }

    override fun onBindViewHolder(
        holder: PlacesDetailRvAdapter.PlacesDetailRViewHolder,
        position: Int
    ) {
        holder.itemView.placeNameTv.text = placesAl[position].placeName
        holder.itemView.placeAddressTv.text = placesAl[position].placeAddress
        holder.itemView.placeLatLongTv.text = placesAl[position].placeLatitude.toString() + ", "+placesAl[position].placeLongitude
        holder.itemView.locateInMapBt.setOnClickListener {
          recyclerViewPositionListener.getRecItemClickedPos(position)
        }
    }

}