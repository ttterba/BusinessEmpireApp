package com.ttterbagames.businesssimulator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ttterbagames.businesssimulator.databinding.FragmentOwnedCarInfoBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random


class OwnedCarInfoFragment(val car: PurchasedCar) : Fragment() {

    lateinit var binding: FragmentOwnedCarInfoBinding

    val playerModel: PlayerModel by activityViewModels()

    val sellFactor = 0.67

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentOwnedCarInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()
        initStaticViews()
    }

    fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSell.setOnClickListener {
            playerModel.ownedCarList.remove(car)
            playerModel.dataBaseHelper.deleteOwnedCar(car.id)

            playerModel.balance.value = playerModel.balance.value!! + car.moneySpentOnIt * sellFactor

            if (car.id == playerModel.dataBaseHelper.getPrimaryCarId())
                playerModel.primaryCarImageId = R.drawable.ic_car


            requireActivity().supportFragmentManager.popBackStack()

            showInterstitialAd(0.2)
        }

        binding.btnSelectAsPrimary.setOnClickListener {
            playerModel.primaryCarImageId = car.imageId
            playerModel.dataBaseHelper.setPrimaryCarId(car.id)
            binding.btnSelectAsPrimary.visibility = View.INVISIBLE
        }
    }

    fun initStaticViews() {
        binding.carImage.setImageResource(car.imId)
        binding.tvTitle.text = car.tit

        binding.tvBoughtPrice.text = Strings.get(R.string.bought_price, car.moneySpentOnIt)
        binding.tvSellPrice.text = Strings.get(R.string.money, car.moneySpentOnIt * sellFactor)

        binding.tvMotorType.text = car.motorType
        binding.tvComplectation.text = car.designType
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