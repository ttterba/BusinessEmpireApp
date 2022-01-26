package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ttterbagames.businesssimulator.databinding.FragmentPurchasedCarBuyBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random


class PurchasedCarBuyFragment(val car: PurchasedCar) : Fragment() {

    val playerModel: PlayerModel by activityViewModels()

    lateinit var binding: FragmentPurchasedCarBuyBinding

    val dfFactor = 0.0
    val bstFactor = 0.15
    val sPlusFactor = 0.35
    val standartFactor = 0.0
    val premiumFactor = 0.4

    var selectedMotorFactor = 0.0
    var selectedEquipmentFactor = 0.0

    var color = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPurchasedCarBuyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initColor()

        invalidateBuyButton()
        binding.btnBuy.setCardBackgroundColor(color)

        setButtonListeners()
        initStaticViews()

        selectDf()
        selectStandart()

        setFilterButtonListeners()
    }

    private fun initStaticViews() {
        binding.tvBstPercent.text = Strings.get(R.string.plus_percent, bstFactor * 100)
        binding.tvSPlusPercent.text = Strings.get(R.string.plus_percent, sPlusFactor * 100)
        binding.tvPremiumPercent.text = Strings.get(R.string.plus_percent, premiumFactor * 100)

        binding.tvTitle.text = car.title
        binding.tvPrice.text = Strings.get(R.string.price_starts_from, car.price)
        binding.tvSummary.text = Strings.get(R.string.money, car.price)
        binding.carImage.setImageResource(car.imageId)
    }

    private fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnBuy.setOnClickListener {
            if (playerModel.balance.value!! >= car.price + car.price * (selectedMotorFactor + selectedEquipmentFactor)) {


                val c = PurchasedCar(car.title, car.price, car.level, car.imageId)
                c.imageName = car.imageName
                c.moneySpentOnIt = car.price + car.price * (selectedMotorFactor + selectedEquipmentFactor)
                when (selectedMotorFactor) {
                    dfFactor -> c.motorType = Strings.get(R.string.df)
                    bstFactor -> c.motorType = Strings.get(R.string.bst)
                    sPlusFactor -> c.motorType = Strings.get(R.string.s_plus)
                }
                when (selectedEquipmentFactor) {
                    standartFactor -> c.designType = Strings.get(R.string.standart)
                    premiumFactor -> c.designType = Strings.get(R.string.premium)
                }
                playerModel.balance.postValue(playerModel.balance.value!! - c.moneySpentOnIt)

                c.id = playerModel.dataBaseHelper.addOwnedCar(c)
                playerModel.ownedCarList.add(c)
                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.popBackStack()

                showInterstitialAd(0.7)
            }
        }

    }

    private fun setFilterButtonListeners() {
        binding.btnDf.setOnClickListener {
            selectDf()
            binding.tvSummary.text = Strings.get(R.string.money, car.price + car.price * (selectedMotorFactor + selectedEquipmentFactor))
            invalidateBuyButton()
        }
        binding.btnBst.setOnClickListener {
            selectBst()
            binding.tvSummary.text = Strings.get(R.string.money, car.price + car.price * (selectedMotorFactor + selectedEquipmentFactor))
            invalidateBuyButton()
        }
        binding.btnSPlus.setOnClickListener {
            selectSPlus()
            binding.tvSummary.text = Strings.get(R.string.money, car.price + car.price * (selectedMotorFactor + selectedEquipmentFactor))
            invalidateBuyButton()
        }
        binding.btnStandart.setOnClickListener {
            selectStandart()
            binding.tvSummary.text = Strings.get(R.string.money, car.price + car.price * (selectedMotorFactor + selectedEquipmentFactor))
            invalidateBuyButton()
        }
        binding.btnPremium.setOnClickListener {
            selectPremium()
            binding.tvSummary.text = Strings.get(R.string.money, car.price + car.price * (selectedMotorFactor + selectedEquipmentFactor))
            invalidateBuyButton()
        }
    }

    private fun initColor() {
        when (car.level) {
            1 -> color = Color.parseColor(Strings.get(R.string.car_lvl_1))
            2 -> color = Color.parseColor(Strings.get(R.string.car_lvl_2))
            3 -> color = Color.parseColor(Strings.get(R.string.car_lvl_3))
            4 -> color = Color.parseColor(Strings.get(R.string.car_lvl_4))
            5 -> color = Color.parseColor(Strings.get(R.string.car_lvl_5))
            6 -> color = Color.parseColor(Strings.get(R.string.car_lvl_6))
        }
    }

    private fun selectDf() {
        binding.apply {
            btnDf.setCardBackgroundColor(color)
            btnBst.setCardBackgroundColor(Color.parseColor("#F3F3F3"))
            btnSPlus.setCardBackgroundColor(Color.parseColor("#F3F3F3"))

            tvDf.setTextColor(Color.parseColor("#FFFFFF"))
            tvBst.setTextColor(Color.parseColor("#2F2F2F"))
            tvBstPercent.setTextColor(Color.parseColor("#2F2F2F"))
            tvSPlus.setTextColor(Color.parseColor("#2F2F2F"))
            tvSPlusPercent.setTextColor(Color.parseColor("#2F2F2F"))
        }

        selectedMotorFactor = dfFactor
    }

    private fun selectBst() {
        binding.apply {
            btnBst.setCardBackgroundColor(color)
            btnDf.setCardBackgroundColor(Color.parseColor("#F3F3F3"))
            btnSPlus.setCardBackgroundColor(Color.parseColor("#F3F3F3"))

            tvDf.setTextColor(Color.parseColor("#2F2F2F"))
            tvBst.setTextColor(Color.parseColor("#FFFFFF"))
            tvBstPercent.setTextColor(Color.parseColor("#FFFFFF"))
            tvSPlus.setTextColor(Color.parseColor("#2F2F2F"))
            tvSPlusPercent.setTextColor(Color.parseColor("#2F2F2F"))
        }

        selectedMotorFactor = bstFactor
    }

    private fun selectSPlus() {
        binding.apply {
            btnSPlus.setCardBackgroundColor(color)
            btnBst.setCardBackgroundColor(Color.parseColor("#F3F3F3"))
            btnDf.setCardBackgroundColor(Color.parseColor("#F3F3F3"))

            tvDf.setTextColor(Color.parseColor("#2F2F2F"))
            tvBst.setTextColor(Color.parseColor("#2F2F2F"))
            tvBstPercent.setTextColor(Color.parseColor("#2F2F2F"))
            tvSPlus.setTextColor(Color.parseColor("#FFFFFF"))
            tvSPlusPercent.setTextColor(Color.parseColor("#FFFFFF"))
        }

        selectedMotorFactor = sPlusFactor
    }

    private fun selectStandart() {
        binding.apply {
            btnStandart.setCardBackgroundColor(color)
            btnPremium.setCardBackgroundColor(Color.parseColor("#F3F3F3"))

            tvStandart.setTextColor(Color.parseColor("#FFFFFF"))
            tvPremium.setTextColor(Color.parseColor("#2F2F2F"))
            tvPremiumPercent.setTextColor(Color.parseColor("#2F2F2F"))
        }

        selectedEquipmentFactor = standartFactor
    }

    private fun selectPremium() {
        binding.apply {
            btnPremium.setCardBackgroundColor(color)
            btnStandart.setCardBackgroundColor(Color.parseColor("#F3F3F3"))

            tvStandart.setTextColor(Color.parseColor("#2F2F2F"))
            tvPremium.setTextColor(Color.parseColor("#FFFFFF"))
            tvPremiumPercent.setTextColor(Color.parseColor("#FFFFFF"))
        }

        selectedEquipmentFactor = premiumFactor
    }

    private fun invalidateBuyButton() {
        if (car.price + car.price * (selectedMotorFactor + selectedEquipmentFactor) > playerModel.balance.value!!)
            binding.btnBuy.visibility = View.INVISIBLE
        else
            binding.btnBuy.visibility = View.VISIBLE
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