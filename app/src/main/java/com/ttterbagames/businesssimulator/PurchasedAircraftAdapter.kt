package com.ttterbagames.businesssimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ttterbagames.businesssimulator.databinding.PurchasedAircraftTemplateBinding

class PurchasedAircraftAdapter(private val listener: OnItemClickListener): RecyclerView.Adapter<PurchasedAircraftAdapter.AircraftHolder>() {

    private var aircraftList = ArrayList<PurchasedAircraft>()

    inner class AircraftHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {
        private val binding = PurchasedAircraftTemplateBinding.bind(item)

        fun bind(aircraft: PurchasedAircraft) {
            binding.decor.setOnClickListener(this)

            Glide.with(binding.aircraftImage.context).load(aircraft.imageId).into(binding.aircraftImage)
            binding.tvTitle.text = aircraft.title
            binding.tvPrice.text = Strings.get(R.string.price_starts_from, aircraft.price)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemClick(v, adapterPosition)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AircraftHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.purchased_aircraft_template, parent, false)
        return AircraftHolder(view)
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