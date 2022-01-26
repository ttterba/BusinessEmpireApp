package com.ttterbagames.businesssimulator.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.*
import com.ttterbagames.businesssimulator.databinding.FragmentTaxiBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


class TaxiFragment(var playerModel: PlayerModel) : Fragment() {

    var taxi = BusinessTaxiStation(playerModel.dataBaseHelper)

    var arr = ArrayList<TaxiCar>()

    lateinit var binding: FragmentTaxiBinding
    var position: Int = 0
    lateinit var bnv: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTaxiBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        bnv.visibility = View.GONE


        initStaticViews()

        setButtonListeners()

        addObservers()

    }

    private fun addObservers() {

        taxi.ownedCarList.observe(this as LifecycleOwner, {
            taxi.numberOfCars.value = it.size
            var sum = 0.0
            for (car in it)
                sum += car.profit
            taxi.profit.postValue(sum)
        })


        taxi.profit.observe(this as LifecycleOwner, {
            binding.tvProfit.text = Strings.get(R.string.money, it)
            setAppropriateSummaryProfitFontSize(it)
        })

        taxi.numberOfCars.observe(this as LifecycleOwner, {
            binding.tvCarCount.text = Strings.get(R.string.cars_count, it)

            if (it < taxi.carParkCapacity.value!!)
                binding.btnGetNewCar.visibility = View.VISIBLE
            else
                binding.btnGetNewCar.visibility = View.GONE
        })

        taxi.carParkCapacity.observe(this as LifecycleOwner, {
            binding.tvCarCapacity.text = Strings.get(R.string.car_capacity_count, it)

            if (it > taxi.numberOfCars.value!!)
                binding.btnGetNewCar.visibility = View.VISIBLE
            else
                binding.btnGetNewCar.visibility = View.GONE
        })

        playerModel.isRewardedAdReady.observe(this as LifecycleOwner, {
            if (it)
                binding.btnSkipLevelUp.visibility = View.VISIBLE
            else
                binding.btnSkipLevelUp.visibility = View.GONE
        })

        taxi.isUpdating.observe(this as LifecycleOwner, {
            if (it) {
                binding.addCarParkWrapper.visibility = View.GONE
                binding.expansionCard.visibility = View.VISIBLE
            }
            else {
                binding.addCarParkWrapper.visibility = View.VISIBLE
                binding.expansionCard.visibility = View.GONE
            }
        })

        taxi.timeLeft.observe(this as LifecycleOwner, {
            binding.tvTimeLeft.text = Strings.get(R.string.remain_time,
                it.floorDiv(1000*60*60), it.floorDiv(1000*60).mod(60), it.floorDiv(1000).mod(3600).mod(60) + 1)
        })

    }

    fun init(pPosition: Int) {
        position = pPosition
        taxi = (playerModel.ownedBusinesses.value?.get(position) as BusinessTaxiStation)
    }

    private fun initStaticViews() {
        binding.tvTitle.text = taxi.title
        binding.tvPlusFiveCost.text = Strings.get(R.string.integer_money, CarParkPrices.SMALL.toInt())
        binding.tvPlusTenCost.text = Strings.get(R.string.integer_money, CarParkPrices.MEDIUM.toInt())
        binding.tvPlusTwentyCost.text = Strings.get(R.string.integer_money, CarParkPrices.BIG.toInt())
    }

    private fun setButtonListeners() {
        binding.btnCarPark.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, TaxiAutoparkFragment(taxi))
                addToBackStack(null)
            }
        }

        binding.btnGetNewCar.setOnClickListener {

            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, TaxiShopFragment(taxi))
                addToBackStack(null)
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSettings.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, BusinessSettingsFragment(taxi, position))
                addToBackStack(null)
            }
        }

        binding.btnSkipLevelUp.setOnClickListener {
            showAd()
            //taxi.skipRaisingCapacity()
        }

        binding.btnPlusFive.setOnClickListener {
            if (playerModel.balance.value!! >= CarParkPrices.SMALL) {
                playerModel.balance.value = playerModel.balance.value!! - CarParkPrices.SMALL
                taxi.beginRaisingStage(5)
            }
        }

        binding.btnPlusTen.setOnClickListener {
            if (playerModel.balance.value!! >= CarParkPrices.MEDIUM) {
                playerModel.balance.value = playerModel.balance.value!! - CarParkPrices.MEDIUM
                taxi.beginRaisingStage(10)
            }
        }

        binding.btnPlusTwenty.setOnClickListener {
            if (playerModel.balance.value!! >= CarParkPrices.BIG) {
                playerModel.balance.value = playerModel.balance.value!! - CarParkPrices.BIG
                taxi.beginRaisingStage(20)
            }
        }

    }

    override fun onDetach() {
        super.onDetach()
        bnv.visibility = View.VISIBLE
    }

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireContext(), Strings.get(R.string.rewarded_ad_unit_id), adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.d("RewardedAd", p0.message)
                playerModel.mRewardedAd = null
                playerModel.isRewardedAdReady.postValue(false)
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d("RewardedAd", "Add was loaded.")
                playerModel.mRewardedAd = rewardedAd
                playerModel.isRewardedAdReady.postValue(true)
            }
        })
    }

    fun showAd() {
        if (playerModel.mRewardedAd != null) {
            playerModel.mRewardedAd?.show(requireActivity()) {
                Log.d("RewardedAd", "User earned the reward.")
                taxi.skipRaisingCapacity()
            }
            loadAd()
        } else {
            Log.d("RewardedAd", "The rewarded ad wasn't ready yet.")
            loadAd()
        }
    }

    private fun setAppropriateSummaryProfitFontSize(num: Double) {
        binding.tvProfit.apply {
            when (num) {
                in 0.0..BusinessConstants.PROFIT_CARD_BREAKPOINT_1 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_1..BusinessConstants.PROFIT_CARD_BREAKPOINT_2 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_2..BusinessConstants.PROFIT_CARD_BREAKPOINT_3 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_3..BusinessConstants.PROFIT_CARD_BREAKPOINT_4 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_4..BusinessConstants.PROFIT_CARD_BREAKPOINT_5 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_5..BusinessConstants.PROFIT_CARD_BREAKPOINT_6 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_6..BusinessConstants.PROFIT_CARD_BREAKPOINT_7 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_7..BusinessConstants.PROFIT_CARD_BREAKPOINT_8 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                else -> setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }
        }

    }

}

object TaxiFilters {
    const val ALL = 0
    const val HIGHEST_MILEAGE = 1
    const val LEAST_MILEAGE = 2
    const val HIGHEST_INCOME = 3
    const val LEAST_INCOME = 4
}