package com.ttterbagames.businesssimulator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.ttterbagames.businesssimulator.databinding.FragmentRealEstateConfirmationBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random


class RealEstateConfirmationFragment(val re: RealEstate, var position: Int) : Fragment() {

    lateinit var binding: FragmentRealEstateConfirmationBinding
    lateinit var bnv: View

    private val playerModel: PlayerModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRealEstateConfirmationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        //bnv.visibility = View.GONE

        setButtonListeners()

        initStaticViews()
    }

    private fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            if (playerModel.balance.value!! >= re.Price) {

                showInterstitialAd(0.8)

                playerModel.balance.value = playerModel.balance.value!! - re.Price

                playerModel.dataBaseHelper.setRealEstateIsOwned(re.id, 1)

                val tmp = ArrayList<RealEstate>()
                if (playerModel.ownedRealEstate.value != null) {
                    tmp.addAll(playerModel.ownedRealEstate.value!!)
                }

                re.apply {
                    tmp.add(RealEstate(City, Country, Price, imageId, true))
                }
                playerModel.ownedRealEstate.postValue(tmp)

                val tmp2 = ArrayList<RealEstate>()
                tmp2.addAll(playerModel.realEstateOffersList.value!!)
                tmp2.removeAt(position)

                playerModel.realEstateOffersList.postValue(tmp2)

//            reAdapter.balance = playerModel.balance.value!!
//            reAdapter.setCarList(playerModel.realEstateOffersList.value!!)
            }

            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun initStaticViews() {
        if (re.City == "") {
            binding.tvLocation.text = re.Country
        }
        else
            binding.tvLocation.text = Strings.get(R.string.geo_location, re.City, re.Country)
        binding.tvPrice.text = Strings.get(R.string.integer_money, re.Price);
        binding.tvProfit.text = Strings.get(R.string.money, re.Price * re.profitFactor)
    }

    override fun onDetach() {
        super.onDetach()
        //bnv.visibility = View.VISIBLE
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