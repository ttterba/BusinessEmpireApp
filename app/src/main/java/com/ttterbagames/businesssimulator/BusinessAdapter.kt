package com.ttterbagames.businesssimulator


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ttterbagames.businesssimulator.databinding.BusinessItemTemplateBinding

class BusinessAdapter(private val listener: OnItemClickListener): RecyclerView.Adapter<BusinessAdapter.BusinessHolder>() {

    private var businessList = ArrayList<Business>()

    inner class BusinessHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener{
        private val binding = BusinessItemTemplateBinding.bind(item)

        init {
            item.setOnClickListener(this)
        }

        fun bind(business: Business){
            binding.imBusiness.setImageResource(business.imageId)
//            tvTitle.text = business.title
//            tvProfit.text = Resources.getSystem().getString(R.string.money, business.profit)
//            tvBusinessType.text = Resources.getSystem().getString(business.businessTypeId)
            binding.tvTitle.text = business.title
            binding.tvProfit.text = business.profit.value?.let { Strings.get(R.string.money, it) }

            binding.tvBusinessType.text = business.businessType
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemClick(adapterPosition)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.business_item_template, parent, false)

        return BusinessHolder(view)
    }

    override fun onBindViewHolder(holder: BusinessHolder, position: Int) {
        holder.bind(businessList[position])
    }

    override fun getItemCount(): Int {
        return businessList.size
    }

    interface RecyclerViewClickListener{
        fun onClick(v: View, position: Int)
    }

    fun addBusiness(business: Business) {
        businessList.add(business)
        notifyDataSetChanged()
    }

    fun setBusinessList(arr: ArrayList<Business>) {
        businessList = arr
        notifyDataSetChanged()
    }
}