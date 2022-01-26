package com.ttterbagames.businesssimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ttterbagames.businesssimulator.databinding.TaxiCarShopItemTemplateBinding

class TransportationCarBuyAdapter(private val listener: OnItemClickListener): RecyclerView.Adapter<TransportationCarBuyAdapter.BuyCarHolder>() {

    private var carList = ArrayList<TransportationCar>()

    inner class BuyCarHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {

        private val binding = TaxiCarShopItemTemplateBinding.bind(item)
        init {
            item.setOnClickListener(this)
        }

        fun bind(car: TransportationCar) {
            binding.carImage.setImageResource(car.imageId)
            binding.tvTitle.text = car.title
            binding.tvPrice.text = Strings.get(R.string.money, car.price)
            binding.tvCategory.text = car.tariff
            binding.tvMaxMileage.text = Strings.get(R.string.kilometers, car.maxMileage)

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transportation_car_shop_item_template, parent, false)

        return BuyCarHolder(view)
    }

    override fun onBindViewHolder(holder: BuyCarHolder, position: Int) {
        holder.bind(carList[position])
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    fun setCarList(arr: ArrayList<TransportationCar>) {
        carList = arr
        notifyDataSetChanged()
    }
}