package com.ttterbagames.businesssimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ttterbagames.businesssimulator.databinding.OwnedCarTemplateBinding
import kotlinx.coroutines.*

class OwnedCarAdapter(private val listener: OnItemClickListener): RecyclerView.Adapter<OwnedCarAdapter.CarHolder>() {

    private var carList = ArrayList<PurchasedCar>()

    inner class CarHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {
        private val binding = OwnedCarTemplateBinding.bind(item)

        fun bind (car: PurchasedCar) {
            itemView.setOnClickListener(this)

            Glide.with(binding.carImage.context).load(car.imageId).into(binding.carImage)
            binding.carTitle.text = car.title
            binding.carDescription.text = Strings.get(R.string.divided_text, car.motorType, car.designType)

            var bgId = 0

            itemView.doAsync( {
                when (car.level) {
                    1 -> bgId = R.drawable.car_decor_lvl_1
                    2 -> bgId = R.drawable.car_decor_lvl_2
                    3 -> bgId = R.drawable.car_decor_lvl_3
                    4 -> bgId = R.drawable.car_decor_lvl_4
                    5 -> bgId = R.drawable.car_decor_lvl_5
                    6 -> bgId = R.drawable.car_decor_lvl_6
                }
            }, {
                binding.decor.setBackgroundResource(bgId)
            })

//            when (car.level) {
//                1 -> binding.decor.setBackgroundResource(R.drawable.car_decor_lvl_1)
//                2 -> binding.decor.setBackgroundResource(R.drawable.car_decor_lvl_2)
//                3 -> binding.decor.setBackgroundResource(R.drawable.car_decor_lvl_3)
//                4 -> binding.decor.setBackgroundResource(R.drawable.car_decor_lvl_4)
//                5 -> binding.decor.setBackgroundResource(R.drawable.car_decor_lvl_5)
//                6 -> binding.decor.setBackgroundResource(R.drawable.car_decor_lvl_6)
//            }

        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                listener.onItemClick(v, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.owned_car_template, parent, false)
        return CarHolder(view)
    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    override fun onBindViewHolder(holder: CarHolder, position: Int) {
        holder.bind(carList[position])
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    fun setCarList(arr: ArrayList<PurchasedCar>) {
        carList = arr
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