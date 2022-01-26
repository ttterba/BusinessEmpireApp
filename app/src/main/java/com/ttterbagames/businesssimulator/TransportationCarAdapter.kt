package com.ttterbagames.businesssimulator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ttterbagames.businesssimulator.databinding.TaxiCarItemTemplateBinding
import kotlinx.coroutines.*

class TransportationCarAdapter: RecyclerView.Adapter<TransportationCarAdapter.CarHolder>() {

    private var carList = ArrayList<TransportationCar>()

    inner class CarHolder(item: View): RecyclerView.ViewHolder(item) {

        private val binding = TaxiCarItemTemplateBinding.bind(item)

        fun bind(car: TransportationCar) {
            var p = 0.0
            itemView.doAsync( {
                p = car.mileage.value!!.toDouble() / car.maxMileage.toDouble()
            }, {
//                binding.tvTitle.text = car.title
//                binding.tvProfit.text = Strings.get(R.string.money, car.profit)
//                binding.tvCategory.text = car.tariff
//                binding.tvMileage.text = Strings.get(R.string.mileage_out_of, car.mileage.value!!, car.maxMileage)
                binding.progressView.layoutParams = LinearLayout.LayoutParams((p * binding.progressBg.width).toInt(), binding.progressBg.height)
            })

            binding.tvTitle.text = car.title
            binding.tvProfit.text = Strings.get(R.string.money, car.profit)
            binding.tvCategory.text = car.tariff
            binding.tvMileage.text = Strings.get(R.string.mileage_out_of, car.mileage.value!!, car.maxMileage)

            Glide.with(binding.carImage.context).load(car.imageId).into(binding.carImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transportation_car_item_template, parent, false)

        return CarHolder(view)
    }

    override fun onBindViewHolder(holder: CarHolder, position: Int) {
        holder.bind(carList[position])
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    fun setCarList(arr: ArrayList<TransportationCar>) {
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