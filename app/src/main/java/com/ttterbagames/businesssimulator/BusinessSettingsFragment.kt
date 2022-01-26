package com.ttterbagames.businesssimulator

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.ttterbagames.businesssimulator.databinding.FragmentBusinessSettingsBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random


class BusinessSettingsFragment(val business: Business, val position: Int) : Fragment() {

    val playerModel: PlayerModel by activityViewModels()

    lateinit var binding: FragmentBusinessSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBusinessSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnCloseBusiness.setOnClickListener {
            removeOwnedBusiness()
            playerModel.dataBaseHelper.deleteBusiness(business.id, business.businessNumber)
            playerModel.balance.value = playerModel.balance.value!! + business.capitalization * 0.3
            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            showInterstitialAd(0.4)
        }

    }

    private fun initViews() {
        binding.tvAllTimeEarnings.text = Strings.get(R.string.money, business.allTimeEarnings)
        binding.tvCapitalization.text = getString(R.string.money, business.capitalization)
        binding.tvProfit.text = getString(R.string.money, business.profit.value)
        binding.tvTitle.text = business.title
        binding.imBusiness.setImageResource(business.imageId)
        setAppropriateSummaryProfitFontSize(business.allTimeEarnings)

        when (business.businessType) {
            getString(R.string.shop) -> setIconsColor(R.color.shop_green)
            getString(R.string.factory) -> setIconsColor(R.color.factory_blue)
            getString(R.string.taxi_station) -> setIconsColor(R.color.taxi_yellow)
            getString(R.string.transportation) -> setIconsColor(R.color.transportation_brown)
            getString(R.string.building_company) -> setIconsColor(R.color.building_red)
            getString(R.string.bank) -> setIconsColor(R.color.bank_gray)
        }
    }

    private fun setAppropriateSummaryProfitFontSize(num: Double) {
        binding.tvAllTimeEarnings.apply {
            when (num) {
                in 0.0..BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_1 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
                in BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_1..BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_2 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
                in BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_2..BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_3 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
                in BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_3..BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_4 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                in BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_4..BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_5 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                in BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_5..BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_6 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f)
                in BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_6..BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_7 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                in BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_7..BusinessConstants.ALL_TIME_EARNINGS_CARD_BREAKPOINT_8 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                else -> setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }
        }

    }

    private fun removeOwnedBusiness() {
        var tmp = playerModel.ownedBusinesses.value
        playerModel.ownedBusinesses.value?.get(position)?.stopCountingAllTimeEarnings()
        if (tmp == null)
            tmp = ArrayList<Business>()
        tmp.removeAt(position)

        playerModel.ownedBusinesses.value = tmp
    }

    private fun setIconsColor(colorId: Int) {
        DrawableCompat.setTint(
            DrawableCompat.wrap(binding.ic1.drawable),
            ContextCompat.getColor(requireContext(), colorId)
        )
        DrawableCompat.setTint(
            DrawableCompat.wrap(binding.ic2.drawable),
            ContextCompat.getColor(requireContext(), colorId)
        )
        DrawableCompat.setTint(
            DrawableCompat.wrap(binding.ic3.drawable),
            ContextCompat.getColor(requireContext(), colorId)
        )
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