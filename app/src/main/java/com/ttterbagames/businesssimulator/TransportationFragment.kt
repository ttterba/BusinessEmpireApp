package com.ttterbagames.businesssimulator

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.databinding.FragmentTransportationBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


class TransportationFragment(var playerModel: PlayerModel) : Fragment() {

    var transport = BusinessTransportationCompany(playerModel.dataBaseHelper)

    lateinit var binding: FragmentTransportationBinding
    var position: Int = 0
    lateinit var bnv: View




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTransportationBinding.inflate(inflater)
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

        transport.ownedCarList.observe(this as LifecycleOwner, {
            transport.numberOfCars.value = it.size
            var sum = 0.0
            for (car in it)
                sum += car.profit
            transport.profit.postValue(sum)
        })

        transport.profit.observe(this as LifecycleOwner, {
            binding.tvProfit.text = Strings.get(R.string.money, it)
            setAppropriateSummaryProfitFontSize(it)
        })

        transport.numberOfCars.observe(this as LifecycleOwner, {
            binding.tvCarCount.text = Strings.get(R.string.cars_count, it)

            if (it < transport.carParkCapacity.value!!)
                binding.btnGetNewCar.visibility = View.VISIBLE
            else
                binding.btnGetNewCar.visibility = View.GONE
        })

        transport.carParkCapacity.observe(this as LifecycleOwner, {
            binding.tvCarCapacity.text = Strings.get(R.string.car_capacity_count, it)

            if (it > transport.numberOfCars.value!!)
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

        transport.isUpdating.observe(this as LifecycleOwner, {
            if (it) {
                binding.addCarParkWrapper.visibility = View.GONE
                binding.expansionCard.visibility = View.VISIBLE
            }
            else {
                binding.addCarParkWrapper.visibility = View.VISIBLE
                binding.expansionCard.visibility = View.GONE
            }
        })

        transport.timeLeft.observe(this as LifecycleOwner, {
            binding.tvTimeLeft.text = Strings.get(R.string.remain_time,
                it.floorDiv(1000*60*60), it.floorDiv(1000*60).mod(60), it.floorDiv(1000).mod(3600).mod(60) + 1)
        })

    }

    fun init(pPosition: Int) {
        position = pPosition
        transport = (playerModel.ownedBusinesses.value?.get(position) as BusinessTransportationCompany)
    }

    private fun initStaticViews() {
        binding.tvTitle.text = transport.title
        binding.tvPlusFiveCost.text = Strings.get(R.string.integer_money, TransportationCarParkPrices.SMALL.toInt())
        binding.tvPlusTenCost.text = Strings.get(R.string.integer_money, TransportationCarParkPrices.MEDIUM.toInt())
        binding.tvPlusTwentyCost.text = Strings.get(R.string.integer_money, TransportationCarParkPrices.BIG.toInt())

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
                replace(R.id.fl_wrapper, TransportationAutoparkFragment(transport))
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
                replace(R.id.fl_wrapper, TransportationShopFragment(transport))
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
                replace(R.id.fl_wrapper, BusinessSettingsFragment(transport, position))
                addToBackStack(null)
            }
        }

        binding.btnSkipLevelUp.setOnClickListener {
            showAd()
        //transport.skipRaisingCapacity()
        }

        binding.btnPlusFive.setOnClickListener {
            if (playerModel.balance.value!! >= TransportationCarParkPrices.SMALL) {
                playerModel.balance.value = playerModel.balance.value!! - TransportationCarParkPrices.SMALL
                transport.beginRaisingStage(5)
            }
        }

        binding.btnPlusTen.setOnClickListener {
            if (playerModel.balance.value!! >= TransportationCarParkPrices.MEDIUM) {
                playerModel.balance.value = playerModel.balance.value!! - TransportationCarParkPrices.MEDIUM
                transport.beginRaisingStage(10)
            }
        }

        binding.btnPlusTwenty.setOnClickListener {
            if (playerModel.balance.value!! >= TransportationCarParkPrices.BIG) {
                playerModel.balance.value = playerModel.balance.value!! - TransportationCarParkPrices.BIG
                transport.beginRaisingStage(20)
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
                transport.skipRaisingCapacity()
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

object TransportationFilters {
    const val ALL = 0
    const val HIGHEST_MILEAGE = 1
    const val LEAST_MILEAGE = 2
    const val HIGHEST_INCOME = 3
    const val LEAST_INCOME = 4
}