package com.ttterbagames.businesssimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ttterbagames.businesssimulator.databinding.BuildingItemTemplateBinding

class BuildingAdapter(private val listener: OnItemClickListener): RecyclerView.Adapter<BuildingAdapter.BuildingHolder>() {
    var buildingList = ArrayList<Building>()

    var adReady = false

    inner class BuildingHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {
        private val binding = BuildingItemTemplateBinding.bind(item)
        init {
            binding.btnSkipTime.setOnClickListener(this)
            binding.btnSell.setOnClickListener(this)
        }

        fun bind(building: Building) {
            binding.buildingImage.setImageResource(building.imageId)
            binding.tvTitle.text = building.title
            binding.tvTime.text = Strings.get(R.string.building_time,
                building.timeLeft.value!!.floorDiv(1000*60*60),
                building.timeLeft.value!!.floorDiv(1000*60).mod(60))
            binding.tvSellPrice.text = Strings.get(R.string.integer_money, building.sellPrice.toInt())

            if (building.isBoosted.value!!) {
                binding.btnSkipTime.visibility = View.GONE
            }
            else if (!building.isBuilt.value!!) {
                if (adReady)
                    binding.btnSkipTime.visibility = View.VISIBLE
                else
                    binding.btnSkipTime.visibility = View.INVISIBLE
                binding.btnSell.visibility = View.GONE
            }


            if (building.isBuilt.value!!) {
                binding.btnSkipTime.visibility = View.GONE
                binding.btnSell.visibility = View.VISIBLE
                binding.tvTime.visibility = View.INVISIBLE
                binding.timeIcon.visibility = View.INVISIBLE
            } else {
                binding.btnSell.visibility = View.INVISIBLE
                binding.tvTime.visibility = View.VISIBLE
                binding.timeIcon.visibility = View.VISIBLE
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.building_item_template, parent, false)

        return BuildingHolder(view)
    }

    override fun onBindViewHolder(holder: BuildingHolder, position: Int) {
        holder.bind(buildingList[position])
    }

    override fun getItemCount(): Int {
        return buildingList.size
    }

    fun setBuildingsList(arr: ArrayList<Building>) {
        val currentSize = buildingList.size
        buildingList.clear()
        notifyDataSetChanged()

        buildingList.addAll(arr)
        notifyItemRangeChanged(0, currentSize)
        notifyItemRangeInserted(0, arr.size)
    }
}