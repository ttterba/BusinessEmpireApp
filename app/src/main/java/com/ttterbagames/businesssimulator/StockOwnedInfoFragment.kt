package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.databinding.FragmentStockOwnedInfoBinding
import com.ttterbagames.businesssimulator.fragments.StockBuyFragment
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


class StockOwnedInfoFragment(var s: Stock) : Fragment() {

    lateinit var binding: FragmentStockOwnedInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentStockOwnedInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStaticViews()

        setButtonListeners()

        setGraphAxis()
        setTimeAxis()

        addObservers()

        binding.graphWrapper.drawGraph(s.lastPricesStack)
        binding.graphWrapper.invalidate()
    }

    fun addObservers() {
        s.priceChangeNotifier.observe(activity as LifecycleOwner, {
            binding.graphWrapper.drawGraph(s.lastPricesStack)
            binding.graphWrapper.invalidate()

            binding.tvShareCost.text = Strings.get(R.string.money, s.currentPrice)
            binding.tvSummaryShareCost.text = Strings.get(R.string.money, s.soldLotsCount * s.currentPrice)

            val diff = (s.currentPrice * s.soldLotsCount - s.moneySpentOnIt) / (s.currentPrice * s.soldLotsCount) * 100
            if (diff >= 0.0) {
                binding.tvDiff.text = Strings.get(R.string.price_diff_positive, (s.currentPrice * s.soldLotsCount - s.moneySpentOnIt), diff)
                binding.tvDiff.setTextColor(Color.parseColor("#05C46B"))
            } else {
                binding.tvDiff.text = Strings.get(R.string.price_diff_negative, (s.currentPrice * s.soldLotsCount - s.moneySpentOnIt).absoluteValue, diff.absoluteValue)
                binding.tvDiff.setTextColor(Color.parseColor("#e74c3c"))
            }
            setGraphAxis()
            setTimeAxis()
        })
    }

    fun initStaticViews() {
        binding.apply {
            tvStockText.text = s.title[0].toString()
            tvTitle.text = s.title
            tvShareCost.text = Strings.get(R.string.money, s.currentPrice)

            tvSummaryShareCost.text = Strings.get(R.string.money, s.soldLotsCount * s.currentPrice)

            val diff = (s.currentPrice * s.soldLotsCount - s.moneySpentOnIt) / (s.currentPrice * s.soldLotsCount) * 100
            if (diff >= 0.0) {
                tvDiff.text = Strings.get(R.string.price_diff_positive, (s.currentPrice * s.soldLotsCount - s.moneySpentOnIt), diff)
                tvDiff.setTextColor(Color.parseColor("#05C46B"))
            } else {
                tvDiff.text = Strings.get(R.string.price_diff_negative, (s.currentPrice * s.soldLotsCount - s.moneySpentOnIt).absoluteValue, diff.absoluteValue)
                tvDiff.setTextColor(Color.parseColor("#e74c3c"))
            }

            tvStockIcon.background.setTint(Color.parseColor(StockParams.colors[s.title[0].toString().uppercase()]))

            tvDividendYield.text = Strings.get(R.string.percent, s.dividendPercent)
            tvSharesNumber.text = Strings.get(R.string.integer_value, s.soldLotsCount)
        }
    }

    fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSell.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, StockSellFragment(s))
                addToBackStack(null)
            }
        }

        binding.btnBuy.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, StockBuyFragment(s, "owned"))
                addToBackStack(null)
            }
        }
    }

    fun setGraphAxis() {
        binding.apply {
            val yMax = s.lastPricesStack.maxOrNull()?.times(1.06) //Костыль! Обязательно сравнить со значением в CustomGraphView
            val yMin = s.lastPricesStack.minOrNull()?.times(1 / 1.06) //Костыль! Обязательно сравнить со значением в CustomGraphView
            val y2 = (yMax!! - yMin!!) / 3 + yMin
            val y3 = yMax - (yMax - yMin) / 3

            tvY1.text = Strings.get(R.string.double_value, yMin)
            tvY2.text = Strings.get(R.string.double_value, y2)
            tvY3.text = Strings.get(R.string.double_value, y3)
            tvY4.text = Strings.get(R.string.double_value, yMax)
        }
    }

    fun setTimeAxis() {
        val timeLengthMillis: Double = (StockParams.STACK_SIZE * StockParams.UPDATE_PERIOD).toDouble()
        val timeLengthMinutes: Double = timeLengthMillis / 1000 / 60

        val mins = timeLengthMinutes.mod(60f).toInt()
        val hours: Int = ((timeLengthMinutes - mins) / 60).mod(24.0).toInt()

        val rightNow = Calendar.getInstance()
        val currHour = rightNow.get(Calendar.HOUR_OF_DAY)
        val currMinute = rightNow.get(Calendar.MINUTE)


        val startHour: Int = subtractTime(s.lastUpdateHour, s.lastUpdateMinute, hours, mins)[0]
        val startMin: Int = subtractTime(s.lastUpdateHour, s.lastUpdateMinute, hours, mins)[1]

        val startMinutes = startHour * 60 + startMin
        val currMinutes = s.lastUpdateHour * 60 + s.lastUpdateMinute
        val step = timeLengthMinutes / 4

        val m2 = startMinutes + step
        val m3 = m2 + step
        val m4 = m3 + step

        val mins2 = m2.mod(60f).toInt()
        val hours2: Int = ((m2 - mins2) / 60).mod(24.0).toInt()
        val mins3 = m3.mod(60f).toInt()
        val hours3: Int = ((m3 - mins3) / 60).mod(24.0).toInt()
        val mins4 = m4.mod(60f).toInt()
        val hours4: Int = ((m4 - mins4) / 60).mod(24.0).toInt()

        binding.tvX1.text = Strings.get(R.string.hour_minute, startHour, startMin)
        binding.tvX2.text = Strings.get(R.string.hour_minute, hours2, mins2)
        binding.tvX3.text = Strings.get(R.string.hour_minute, hours3, mins3)
        binding.tvX4.text = Strings.get(R.string.hour_minute, hours4, mins4)
        binding.tvX5.text = Strings.get(R.string.hour_minute, s.lastUpdateHour, s.lastUpdateMinute)
    }

    fun subtractTime(h1: Int, m1: Int, h2: Int, m2: Int): ArrayList<Int> {
        var h = h1 - h2
        var m = m1 - m2

        if (m < 0) {
            h -= 1
            m += 60
        }

        if (m > 60) {
            h += 1
            m -= 60
        }

        if (h < 0)
            h += 24
        if (h > 24)
            h -= 24

        return arrayListOf(h,m)
    }

}