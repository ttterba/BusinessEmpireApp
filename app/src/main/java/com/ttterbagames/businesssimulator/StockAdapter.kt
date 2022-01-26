package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ttterbagames.businesssimulator.databinding.StockItemTemplateBinding
import kotlinx.coroutines.*
import kotlin.math.absoluteValue

class StockAdapter(private val listener: OnItemClickListener, val ownedToShow: Boolean = false): RecyclerView.Adapter<StockAdapter.StockHolder>() {

    private var stockList = ArrayList<Stock>()

    inner class StockHolder(item: View): RecyclerView.ViewHolder(item), View.OnClickListener {

        private val binding = StockItemTemplateBinding.bind(item)
        init {
            item.setOnClickListener(this)
        }

        fun bind(s: Stock) {
            binding.apply {
                tvStockText.text = s.title[0].toString()
                tvTitle.text = s.title

                var subText = ""
                var cost = ""
                var differ = 0.0
                var diffText = ""
                var diffParsedColor = 0
                var iconParsedColor = 0

                itemView.doAsync({
                                 if (ownedToShow) {
                                     subText = Strings.get(R.string.lot_pieces, s.soldLotsCount)
                                     cost = Utils.convertMoneyToText(s.currentPrice * s.soldLotsCount)
                                     differ = (s.currentPrice * s.soldLotsCount - s.moneySpentOnIt) / (s.currentPrice * s.soldLotsCount) * 100
                                     if (differ >= 0.0) {
                                         diffText = Strings.get(R.string.positive_percent, differ)
                                         diffParsedColor = Color.parseColor("#05C46B")
                                     } else {
                                         diffText = Strings.get(R.string.negative_percent, differ.absoluteValue)
                                         diffParsedColor = Color.parseColor("#e74c3c")
                                     }
                                 } else {
                                     subText = if (s.lotsCount == s.soldLotsCount) Strings.get(R.string.company_sold)
                                     else Strings.get(R.string.available)
                                     cost = Strings.get(R.string.money, s.currentPrice)

                                     if (s.lastDiff >= 0.0) {
                                         diffText = Strings.get(R.string.price_diff_positive, s.lastDiff, s.lastDiffPercent.absoluteValue)
                                         diffParsedColor = Color.parseColor("#05C46B")
                                     } else {
                                         diffText = Strings.get(R.string.price_diff_negative, s.lastDiff.absoluteValue, s.lastDiffPercent.absoluteValue)
                                         diffParsedColor = Color.parseColor("#e74c3c")
                                     }
                                 }
                    iconParsedColor = Color.parseColor(StockParams.colors[s.title[0].toString().uppercase()])
                }, {
                    tvStockIcon.background.setTint(iconParsedColor)
                    tvCost.text = cost
                    tvDiff.text = diffText
                    tvSubText.text = subText
                    tvDiff.setTextColor(diffParsedColor)
                })

//                if (ownedToShow) {
//                    tvSubText.text = Strings.get(R.string.lot_pieces, s.soldLotsCount)
//                    tvCost.text = Utils.convertMoneyToText(s.currentPrice * s.soldLotsCount)
//
//                    val diff = (s.currentPrice * s.soldLotsCount - s.moneySpentOnIt) / (s.currentPrice * s.soldLotsCount) * 100
//                    if (diff >= 0.0) {
//                        tvDiff.text = Strings.get(R.string.positive_percent, diff)
//                        tvDiff.setTextColor(Color.parseColor("#05C46B"))
//                    } else {
//                        tvDiff.text = Strings.get(R.string.negative_percent, diff.absoluteValue)
//                        tvDiff.setTextColor(Color.parseColor("#e74c3c"))
//                    }
//                }
//                else {
//                    if (s.lotsCount == s.soldLotsCount) tvSubText.text = Strings.get(R.string.company_sold)
//                    else tvSubText.text = Strings.get(R.string.available)
//                    tvCost.text = Strings.get(R.string.money, s.currentPrice)
//
//                    if (s.lastDiff >= 0.0) {
//                        tvDiff.text = Strings.get(R.string.price_diff_positive, s.lastDiff, s.lastDiffPercent.absoluteValue)
//                        tvDiff.setTextColor(Color.parseColor("#05C46B"))
//                    } else {
//                        tvDiff.text = Strings.get(R.string.price_diff_negative, s.lastDiff.absoluteValue, s.lastDiffPercent.absoluteValue)
//                        tvDiff.setTextColor(Color.parseColor("#e74c3c"))
//                    }
//                }
//
//
//                tvStockIcon.background.setTint(Color.parseColor(StockParams.colors[s.title[0].toString().uppercase()]))
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.stock_item_template, parent, false)

        return StockHolder(view)
    }

    override fun onBindViewHolder(holder: StockHolder, position: Int) {
        holder.bind(stockList[position])
    }

    override fun getItemCount(): Int {
        return stockList.size
    }

    fun setList(arr: ArrayList<Stock>) {
        stockList = arr
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