package com.ttterbagames.businesssimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ttterbagames.businesssimulator.databinding.BuildingInfoTemplateBinding

class BuildingInfoAdapter(private val listener: OnItemClickListener): RecyclerView.Adapter<BuildingInfoAdapter.BuildingInfoHolder>() {
    var buildingList = ArrayList<Building>()

    inner class BuildingInfoHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {

        private val binding = BuildingInfoTemplateBinding.bind(item)
        init {
            item.setOnClickListener(this)
        }

        fun bind(building: Building) {
            binding.buildingImage.setImageResource(building.imageId)
            binding.tvTitle.text = building.title
            binding.tvMetal.text = Strings.get(R.string.tons_count, building.metalNumber)
            binding.tvBuilders.text = Strings.get(R.string.human_count, building.buildersNumber)
            binding.tvWood.text = Strings.get(R.string.сub_count, building.woodNumber)
            binding.tvConcrete.text = Strings.get(R.string.сub_count, building.concreteNumber)
            binding.tvTime.text = Strings.get(R.string.hours, building.hoursToBuild)
            binding.tvSellPrice.text = Strings.get(R.string.integer_money, building.sellPrice.toInt())
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemClick(v, adapterPosition)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuildingInfoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.building_info_template, parent, false)

        return BuildingInfoHolder(view)
    }

    override fun onBindViewHolder(holder: BuildingInfoHolder, position: Int) {
        holder.bind(buildingList[position])
    }

    override fun getItemCount(): Int {
        return buildingList.size
    }

    fun setBuildingsList(arr: ArrayList<Building>) {
        buildingList = arr
        notifyDataSetChanged()
    }
}