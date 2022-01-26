package com.ttterbagames.businesssimulator.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.ttterbagames.businesssimulator.*
import com.ttterbagames.businesssimulator.databinding.FragmentStockBuyBinding
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.min
import kotlin.random.Random


class StockBuyFragment(var s: Stock, val from: String = "") : Fragment() {

    lateinit var binding: FragmentStockBuyBinding
    val playerModel: PlayerModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentStockBuyBinding.inflate(inflater)
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
            if (s.lotsCount == s.soldLotsCount) tvSubText.text = Strings.get(R.string.company_sold)
            else tvSubText.text = Strings.get(R.string.available)
            tvCost.text = Strings.get(R.string.money, s.currentPrice)

            if (s.lastDiff >= 0.0) {
                tvDiff.text = Strings.get(R.string.price_diff_positive, s.lastDiff, s.lastDiffPercent.absoluteValue)
                tvDiff.setTextColor(Color.parseColor("#05C46B"))
            } else {
                tvDiff.text = Strings.get(R.string.price_diff_negative, s.lastDiff.absoluteValue, s.lastDiffPercent.absoluteValue)
                tvDiff.setTextColor(Color.parseColor("#e74c3c"))
            }

            tvStockIcon.background.setTint(Color.parseColor(StockParams.colors[s.title[0].toString().uppercase()]))
        }
    }

    fun initStaticViews() {
        binding.tvAvailable.text = Strings.get(R.string.number_of_available, (s.lotsCount - s.soldLotsCount))
        binding.tvSummary.text = Strings.get(R.string.money, 0.0)
        binding.btnBuy.isEnabled = false

        if (s.lotsCount == s.soldLotsCount)
            binding.edNumber.isEnabled = false

        val maxLength = (s.lotsCount - s.soldLotsCount).toString().length
        binding.edNumber.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
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
                    binding.btnBuy.isEnabled = num * s.currentPrice <= playerModel.balance.value!!
                            && num != 0L && num <= (s.lotsCount - s.soldLotsCount)
                } catch (e: Exception) {
                    binding.btnBuy.isEnabled = false
                    binding.tvSummary.text = Strings.get(R.string.money, 0.0)
                    return false
                }
                return false
            }
        })

        binding.btnBuy.setOnClickListener {
            try {
                val num = binding.edNumber.text.toString().toLong()

                if (num * s.currentPrice <= playerModel.balance.value!!
                    && num != 0L && num <= (s.lotsCount - s.soldLotsCount)) {
                    s.soldLotsCount += num
                    s.moneySpentOnIt += num * s.currentPrice

                    playerModel.dataBaseHelper.updateStockAfterDeal(s.id, s.soldLotsCount, s.moneySpentOnIt)

                    playerModel.balance.postValue(playerModel.balance.value!! - num * s.currentPrice)

                    if (playerModel.ownedStockList.value == null)
                        playerModel.ownedStockList.value = ArrayList<Stock>()

                    if (!playerModel.ownedStockList.value!!.contains(s)) {
                        playerModel.ownedStockList.value!!.add(s)
                    }

                    if (from == "owned")
                        requireActivity().supportFragmentManager.popBackStack()
                    else
                        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                    showInterstitialAd(0.3)

                }
            } catch (e: Exception) {

            }
        }

        binding.btnMax.setOnClickListener {
            val c = min(s.lotsCount - s.soldLotsCount, floor(playerModel.balance.value!! / s.currentPrice).toLong())

            binding.edNumber.setText(c.toString())
            binding.edNumber.setSelection(binding.edNumber.length())
            val num = binding.edNumber.text.toString().toLong()
            binding.tvSummary.text = Strings.get(R.string.money, (num * s.currentPrice))
            binding.btnBuy.isEnabled = num <= (s.lotsCount - s.soldLotsCount) && num != 0L
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