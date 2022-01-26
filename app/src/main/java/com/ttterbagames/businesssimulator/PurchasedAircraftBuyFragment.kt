package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ttterbagames.businesssimulator.databinding.FragmentPurchasedAircraftBuyBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random


class PurchasedAircraftBuyFragment(val aircraft: PurchasedAircraft) : Fragment() {

    val playerModel: PlayerModel by activityViewModels()
    lateinit var binding: FragmentPurchasedAircraftBuyBinding

    val noTeamFactor = 0.0
    val teamFactor = 0.1
    val standartFactor = 0.0
    val luxFactor = 0.25

    var selectedTeamFactor = 0.0
    var selectedDecorFactor = 0.0

    var color = Color.parseColor("#3A6FED")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPurchasedAircraftBuyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStaticViews()
        invalidateBuyButton()

        setFilterButtonListeners()
        setButtonListeners()

    }

    private fun initStaticViews() {
        binding.tvHireTeamPercent.text = Strings.get(R.string.plus_percent, teamFactor * 100)
        binding.tvLuxPercent.text = Strings.get(R.string.plus_percent, luxFactor * 100)

        binding.tvTitle.text = aircraft.title
        binding.tvPrice.text = Strings.get(R.string.price_starts_from, aircraft.price)
        binding.tvSummary.text = Strings.get(R.string.money, aircraft.price)
        binding.carImage.setImageResource(aircraft.imageId)
    }

    private fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnBuy.setOnClickListener {
            if (playerModel.balance.value!! >= aircraft.price + aircraft.price * (selectedTeamFactor + selectedDecorFactor)) {



                val c = PurchasedAircraft(aircraft.title, aircraft.price, aircraft.imageId)
                c.imageName = aircraft.imageName
                c.moneySpentOnIt = aircraft.price + aircraft.price * (selectedTeamFactor + selectedDecorFactor)
                when (selectedTeamFactor) {
                    noTeamFactor -> c.team = Strings.get(R.string.no_team)
                    teamFactor -> c.team = Strings.get(R.string.team)
                }
                when (selectedDecorFactor) {
                    standartFactor -> c.designType = Strings.get(R.string.standart)
                    luxFactor -> c.designType = Strings.get(R.string.lux)
                }
                playerModel.balance.postValue(playerModel.balance.value!! - c.moneySpentOnIt)
                playerModel.ownedAircraftList.add(c)

                playerModel.dataBaseHelper.addOwnedAircraft(c)

                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.popBackStack()

                showInterstitialAd(0.5)
            }
        }

    }

    private fun invalidateBuyButton() {
        if (aircraft.price + aircraft.price * (selectedTeamFactor + selectedDecorFactor) > playerModel.balance.value!!)
            binding.btnBuy.visibility = View.INVISIBLE
        else
            binding.btnBuy.visibility = View.VISIBLE
    }

    private fun setFilterButtonListeners() {
        binding.btnHireTeam.setOnClickListener {
            clickHireTeam()
            binding.tvSummary.text = Strings.get(R.string.money, aircraft.price + aircraft.price * (selectedTeamFactor + selectedDecorFactor))
            invalidateBuyButton()
        }

        binding.btnStandart.setOnClickListener {
            selectStandart()
            binding.tvSummary.text = Strings.get(R.string.money, aircraft.price + aircraft.price * (selectedTeamFactor + selectedDecorFactor))
            invalidateBuyButton()
        }
        binding.btnLux.setOnClickListener {
            selectLux()
            binding.tvSummary.text = Strings.get(R.string.money, aircraft.price + aircraft.price * (selectedTeamFactor + selectedDecorFactor))
            invalidateBuyButton()
        }
    }

    private fun clickHireTeam() {
        binding.apply {
            selectedTeamFactor = if (selectedTeamFactor == noTeamFactor) {
                btnHireTeam.setCardBackgroundColor(color)
                tvHireTeam.setTextColor(Color.parseColor("#FFFFFF"))
                tvHireTeamPercent.setTextColor(Color.parseColor("#FFFFFF"))
                teamFactor
            } else {
                btnHireTeam.setCardBackgroundColor(Color.parseColor("#F3F3F3"))
                tvHireTeam.setTextColor(Color.parseColor("#2f2f2f"))
                tvHireTeamPercent.setTextColor(Color.parseColor("#2f2f2f"))
                noTeamFactor
            }

        }

    }

    private fun selectStandart() {
        binding.apply {
            btnStandart.setCardBackgroundColor(color)
            btnLux.setCardBackgroundColor(Color.parseColor("#F3F3F3"))

            tvStandart.setTextColor(Color.parseColor("#FFFFFF"))
            tvLux.setTextColor(Color.parseColor("#2F2F2F"))
            tvLuxPercent.setTextColor(Color.parseColor("#2F2F2F"))
        }

        selectedDecorFactor = standartFactor
    }

    private fun selectLux() {
        binding.apply {
            btnLux.setCardBackgroundColor(color)
            btnStandart.setCardBackgroundColor(Color.parseColor("#F3F3F3"))

            tvStandart.setTextColor(Color.parseColor("#2F2F2F"))
            tvLux.setTextColor(Color.parseColor("#FFFFFF"))
            tvLuxPercent.setTextColor(Color.parseColor("#FFFFFF"))
        }

        selectedDecorFactor = luxFactor
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