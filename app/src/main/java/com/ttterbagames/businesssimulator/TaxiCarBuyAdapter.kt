package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.ttterbagames.businesssimulator.databinding.TaxiCarShopItemTemplateBinding

class TaxiCarBuyAdapter(private val listener: OnItemClickListener): RecyclerView.Adapter<TaxiCarBuyAdapter.BuyCarHolder>() {

    var carList = ArrayList<TaxiCar>()

    inner class BuyCarHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {

        private val binding = TaxiCarShopItemTemplateBinding.bind(item)
        init {
            item.setOnClickListener(this)
        }

        fun bind(car: TaxiCar) {
            binding.carImage.setImageResource(car.imageId)
            binding.tvTitle.text = car.title
            binding.tvPrice.text = Strings.get(R.string.money, car.price)
            binding.tvCategory.text = car.tariff
            binding.tvMaxMileage.text = Strings.get(R.string.kilometers, car.maxMileage)

            if (car.selected) {

            }

        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemClick(v, adapterPosition)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyCarHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.taxi_car_shop_item_template, parent, false)

        return BuyCarHolder(view)
    }

    override fun onBindViewHolder(holder: BuyCarHolder, position: Int) {
        holder.bind(carList[position])
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    fun setCarsList(arr: ArrayList<TaxiCar>) {
        carList = arr
        notifyDataSetChanged()
    }
}