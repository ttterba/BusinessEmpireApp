package com.ttterbagames.businesssimulator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ttterbagames.businesssimulator.databinding.FragmentOwnedAircraftBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random


class OwnedAircraftFragment(val aircraft: PurchasedAircraft) : Fragment() {

    lateinit var binding: FragmentOwnedAircraftBinding

    val playerModel: PlayerModel by activityViewModels()

    val sellFactor = 0.7

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentOwnedAircraftBinding.inflate(inflater)
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
            playerModel.ownedAircraftList.remove(aircraft)
            playerModel.dataBaseHelper.deleteOwnedAircraft(aircraft.id)

            playerModel.balance.value = playerModel.balance.value!! + aircraft.moneySpentOnIt * sellFactor

            if (aircraft.id == playerModel.dataBaseHelper.getPrimaryAircraftId())
                playerModel.primaryAircraftImageId = R.drawable.ic_aircraft

            requireActivity().supportFragmentManager.popBackStack()
            showInterstitialAd(0.2)
        }

        binding.btnSelectAsPrimary.setOnClickListener {
            playerModel.primaryAircraftImageId = aircraft.imageId
            playerModel.dataBaseHelper.setPrimaryAircraftId(aircraft.id)
            binding.btnSelectAsPrimary.visibility = View.INVISIBLE
        }
    }

    fun initStaticViews() {
        binding.carImage.setImageResource(aircraft.imageId)
        binding.tvTitle.text = aircraft.title

        binding.tvBoughtPrice.text = Strings.get(R.string.bought_price, aircraft.moneySpentOnIt)
        binding.tvSellPrice.text = Strings.get(R.string.money, aircraft.moneySpentOnIt * sellFactor)

        binding.tvTeam.text = aircraft.team
        binding.tvComplectation.text = aircraft.designType
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