package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ttterbagames.businesssimulator.databinding.RealEstateItemTemplateBinding
import kotlinx.coroutines.*

class RealEstateOffersAdapter(private val listener: OnItemClickListener, var balance: Double = 0.0): RecyclerView.Adapter<RealEstateOffersAdapter.RealEstateBuyHolder>() {

    private var reList = ArrayList<RealEstate>()


    inner class RealEstateBuyHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener{

        private val binding = RealEstateItemTemplateBinding.bind(item)

        fun bind(re: RealEstate) {

            if (!re.isOwned && balance >= re.Price)
                binding.btnBuy.setOnClickListener(this)

            var btnBuyVisibilityState = 0
            var btnBuyParsedColor = 0
            var locationText = ""

            itemView.doAsync( {
                if (re.isOwned) {
                    btnBuyVisibilityState = View.GONE
                }
                else if (balance >= re.Price) {
                    btnBuyVisibilityState = View.VISIBLE
                    btnBuyParsedColor = Color.parseColor("#2C7DFE")
                }
                else {
                    btnBuyVisibilityState = View.VISIBLE
                    btnBuyParsedColor = Color.parseColor("#E0E0E0")
                }
                if (re.City == "") {
                    locationText = re.Country
                }
                else
                    locationText = Strings.get(R.string.geo_location, re.City, re.Country)

            }, {
                binding.btnBuy.visibility = btnBuyVisibilityState
                binding.btnBuy.setCardBackgroundColor(btnBuyParsedColor)
                binding.tvLocation.text = locationText
            })

            Glide.with(binding.image.context).load(re.imageId).into(binding.image)
            binding.tvPrice.text = Strings.get(R.string.integer_money, re.Price)

        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemClick(v, adapterPosition)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealEstateBuyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.real_estate_item_template, parent, false)

        return RealEstateBuyHolder(view)
    }

    override fun onBindViewHolder(holder: RealEstateBuyHolder, position: Int) {
        holder.bind(reList[position])
    }

    override fun getItemCount(): Int {
        return reList.size
    }

    fun setCarList(arr: ArrayList<RealEstate>) {
        val currentSize = reList.size
        reList.clear()
        reList.addAll(arr)
        notifyItemRangeRemoved(0, currentSize)
        notifyItemRangeInserted(0, arr.size)
    }

    fun setREList(arr: ArrayList<RealEstate>) {
        reList = arr
        notifyDataSetChanged()
    }

    fun setList(arr: ArrayList<RealEstate>) {
        val currentSize = reList.size
        reList.clear()
        reList.addAll(arr)
//        notifyItemRangeChanged(0, currentSize)
//        notifyItemRangeInserted(0, arr.size)
        notifyDataSetChanged()
    }

    inline fun <T> View.doAsync(
        crossinline backgroundTask: (scope: CoroutineScope) -> T,
        crossinline result: (T?) -> Unit) {
        val job = CoroutineScope(Dispatchers.Main)
        // Добавляем слушатель, который будет отменять
        // корутину, если вьюха откреплена
        val attachListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View?) {}
            override fun onViewDetachedFromWindow(p0: View?) {
                job.cancel()
                removeOnAttachStateChangeListener(this)
            }
        }
        this.addOnAttachStateChangeListener(attachListener)
        // Создаем Job, которая будет работать в основном потоке
        job.launch {
            // Создаем Deferred с результатом в фоновом потоке
            val data = async(Dispatchers.Default) {
                try {
                    backgroundTask(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@async null
                }
            }
            if (isActive) {
                try {
                    result.invoke(data.await())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            // Отписываем слушатель по окончании Job
            this@doAsync.removeOnAttachStateChangeListener(attachListener)
        }
    }
}