package com.ttterbagames.businesssimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ttterbagames.businesssimulator.databinding.OwnedCarTemplateBinding

class OwnedAircraftAdapter(private val listener: OnItemClickListener): RecyclerView.Adapter<OwnedAircraftAdapter.AircraftHolder>() {

    private var aircraftList = ArrayList<PurchasedAircraft>()

    inner class AircraftHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {
        private val binding = OwnedCarTemplateBinding.bind(item)

        fun bind (aircraft: PurchasedAircraft) {
            itemView.setOnClickListener(this)
            Glide.with(binding.carImage.context).load(aircraft.imageId).into(binding.carImage)
            binding.carTitle.text = aircraft.title
            binding.carDescription.text = Strings.get(R.string.divided_text, aircraft.team, aircraft.designType)

        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemClick(v, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AircraftHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.owned_aircraft_template, parent, false)
        return AircraftHolder(view)
    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    override fun onBindViewHolder(holder: AircraftHolder, position: Int) {
        holder.bind(aircraftList[position])
    }

    override fun getItemCount(): Int {
        return aircraftList.size
    }

    fun setAircraftList(arr: ArrayList<PurchasedAircraft>) {
        aircraftList = arr
        notifyDataSetChanged()
    }
}