package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.databinding.FragmentStockSellBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.math.absoluteValue
import kotlin.random.Random


class StockSellFragment(var s: Stock) : Fragment() {

    val playerModel: PlayerModel by activityViewModels()

    lateinit var binding:FragmentStockSellBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentStockSellBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStaticViews()
        initStock()
        addObservers()
        setButtonListeners()
    }

    fun initStock() {
        binding.apply {
            tvStockText.text = s.title[0].toString()
            tvTitle.text = s.title

            tvSubText.text = Strings.get(R.string.lot_pieces, s.soldLotsCount)
            tvCost.text = Utils.convertMoneyToText(s.currentPrice * s.soldLotsCount)

            val diff = (s.currentPrice * s.soldLotsCount - s.moneySpentOnIt) / (s.currentPrice * s.soldLotsCount) * 100
            if (diff >= 0.0) {
                tvDiff.text = Strings.get(R.string.positive_percent, diff)
                tvDiff.setTextColor(Color.parseColor("#05C46B"))
            } else {
                tvDiff.text = Strings.get(R.string.negative_percent, diff.absoluteValue)
                tvDiff.setTextColor(Color.parseColor("#e74c3c"))
            }

            tvStockIcon.background.setTint(Color.parseColor(StockParams.colors[s.title[0].toString().uppercase()]))
        }
    }

    fun initStaticViews() {
        binding.tvAvailable.text = Strings.get(R.string.number_of_available, s.soldLotsCount)
        binding.tvSummary.text = Strings.get(R.string.money, 0.0)
        binding.btnSell.isEnabled = false

        val maxLength = (s.soldLotsCount).toString().length
        binding.edNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }

    fun addObservers() {
        playerModel.balance.observe(activity as LifecycleOwner, {
            binding.tvBalance.text = Strings.get(R.string.money, it)
        })

        s.priceChangeNotifier.observe(activity as LifecycleOwner, {
            initStock()
        })
    }

    fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnMax.setOnClickListener {
            binding.edNumber.setText(s.soldLotsCount.toString())
            binding.edNumber.setSelection(binding.edNumber.length())
            val num = binding.edNumber.text.toString().toLong()
            binding.tvSummary.text = Strings.get(R.string.money, (num * s.currentPrice))
            binding.btnSell.isEnabled = num <= s.soldLotsCount && num != 0L
        }

        binding.edNumber.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.sv.post(Runnable { binding.sv.scrollTo(0, 200) })
            }

        }

        binding.edNumber.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                try {
                    val num = binding.edNumber.text.toString().toLong()

                    binding.tvSummary.text = Strings.get(R.string.money, (num * s.currentPrice))
                    binding.btnSell.isEnabled = num <= s.soldLotsCount && num != 0L
                } catch (e: Exception) {
                    binding.btnSell.isEnabled = false
                    binding.tvSummary.text = Strings.get(R.string.money, 0.0)
                    return false
                }
                return false
            }
        })

        binding.btnSell.setOnClickListener {
            try {
                val num = binding.edNumber.text.toString().toLong()

                if (num <= s.soldLotsCount && num != 0L) {

                    showInterstitialAd(0.8)

                    playerModel.earnedOnStocksSelling += num * s.currentPrice - s.moneySpentOnIt * num.toDouble() / s.soldLotsCount.toDouble()
                    playerModel.dataBaseHelper.setEarnedOnStocksSelling(playerModel.earnedOnStocksSelling)

                    s.soldLotsCount -= num
                    s.moneySpentOnIt = s.soldLotsCount * s.currentPrice

                    playerModel.dataBaseHelper.updateStockAfterDeal(s.id, s.soldLotsCount, s.moneySpentOnIt)

                    playerModel.balance.postValue(playerModel.balance.value!! + num * s.currentPrice)
                    requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)


                    if (s.soldLotsCount == 0L) {
                        playerModel.ownedStockList.value!!.remove(s)
                    }

                }
            } catch (e: Exception) {

            }
        }
    }

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(),Strings.get(R.string.interstitial_ad_unit_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("InterstitialAd", adError?.message)
                playerModel.mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("InterstitialAd", "Ad was loaded.")
                playerModel.mInterstitialAd = interstitialAd
            }
        })
    }

    fun showInterstitialAd() {
        if (playerModel.mInterstitialAd != null) {
            playerModel.mInterstitialAd?.show(requireActivity())
            loadInterstitialAd()
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
            loadInterstitialAd()
        }
    }

    fun showInterstitialAd(probability: Double) {
        val r = Random.nextDouble(0.0, 1.0)
        if (probability >= r) {
            if (playerModel.mInterstitialAd != null) {
                playerModel.mInterstitialAd?.show(requireActivity())
                loadInterstitialAd()
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                loadInterstitialAd()
            }
        }

    }

}