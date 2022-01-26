package com.ttterbagames.businesssimulator.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.*
import com.ttterbagames.businesssimulator.databinding.FragmentStocksBinding
import kotlin.math.absoluteValue


class StocksFragment : Fragment() {

    val playerModel: PlayerModel by activityViewModels()
    lateinit var binding: FragmentStocksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStocksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()
        addObservers()
    }

    fun addObservers() {
        playerModel.updateStockNotifier.observe(activity as LifecycleOwner, {
            var sum = 0.0
            var sumProfit = 0.0
            var sumGrowth = 0.0

            if (playerModel.ownedStockList.value != null)
            for (s in playerModel.ownedStockList.value!!) {
                sum += s.currentPrice * s.soldLotsCount
                sumProfit += s.currentPrice * (s.dividendPercent / 100) * s.soldLotsCount
                sumGrowth += s.currentPrice * s.soldLotsCount - s.moneySpentOnIt
            }

            playerModel.summaryStocksProfit.value = sumProfit

            binding.apply {
                tvPortfolioCost.text = Strings.get(R.string.money, sum)
                tvDividendYield.text = Strings.get(R.string.money, sumProfit)

                if (sumGrowth >= 0.0) {
                    tvDiff.text = Strings.get(R.string.price_diff_positive, sumGrowth, (sumGrowth / sum) * 100)
                    tvDiff.setTextColor(Color.parseColor("#05C46B"))
                } else {
                    tvDiff.text = Strings.get(R.string.price_diff_negative, sumGrowth.absoluteValue, ((sumGrowth / sum) * 100).absoluteValue)
                    tvDiff.setTextColor(Color.parseColor("#e74c3c"))
                }

                if (sumGrowth == 0.0) {
                    tvDiff.text = Strings.get(R.string.price_diff_positive, 0.0, 0.0)
                    tvDiff.setTextColor(Color.parseColor("#05C46B"))
                }
            }

            setAppropriateFontSize(sum)
        })
    }

    fun setButtonListeners() {
        binding.btnMyStocks.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, MyStocksFragment())
                addToBackStack(null)
            }
        }

        binding.btnStockMarket.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, StockMarketFragment())
                addToBackStack(null)
            }
        }
    }

    private fun setAppropriateFontSize(num: Double) {
        when (num) {
            in 0.0..10000000.0 ->
                binding.tvPortfolioCost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
            in 10000000.0..1000000000.0 ->
                binding.tvPortfolioCost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            in 1000000000.0..100000000000.0 ->
                binding.tvPortfolioCost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            in 100000000000.0..10000000000000.0 ->
                binding.tvPortfolioCost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            else -> binding.tvPortfolioCost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }
        when (num) {
            in 0.0..10000000.0 ->
                binding.tvDividendYield.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
            in 10000000.0..1000000000.0 ->
                binding.tvDividendYield.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            in 1000000000.0..100000000000.0 ->
                binding.tvDividendYield.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            in 100000000000.0..10000000000000.0 ->
                binding.tvDividendYield.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            else -> binding.tvDividendYield.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }
    }
}