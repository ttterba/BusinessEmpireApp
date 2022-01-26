package com.ttterbagames.businesssimulator

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.databinding.FragmentShopBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


class ShopFragment(var playerModel: PlayerModel) : Fragment() {
//    private val playerModel2: PlayerModel by activityViewModels()


    lateinit var shop: BusinessShop
    var position: Int = 0

    lateinit var binding: FragmentShopBinding
    lateinit var bnv: View

    lateinit var dataBaseHelper: DataBaseHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dataBaseHelper = DataBaseHelper(requireContext())
        binding = FragmentShopBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        bnv.visibility = View.GONE



        initStaticViews()

        shop.profit.observe(activity as LifecycleOwner, {
            binding.tvProfit.text = Strings.get(R.string.money, it)
            setAppropriateSummaryProfitFontSize(it)
        })

        shop.updateCost.observe(this as LifecycleOwner, {
            binding.tvToLevelUp.text = getString(R.string.money, it)
            binding.tvProfitGrow.text = getString(R.string.money,
                shop.profit.value?.times(((shop as BusinessShop).profitGrowFactor - 1)))
        })

        shop.stage.observe(this as LifecycleOwner, {
            binding.tvStage.text = it.toString()

            if (it == shop.maxStage) {
                binding.openNewCard.visibility = View.INVISIBLE
                binding.achievedMaxStageCard.visibility = View.VISIBLE
                binding.expansionCard.visibility = View.GONE
            }
        })

        playerModel.balance.observe(this as LifecycleOwner, {
            if (it >= shop.updateCost.value!! && !shop.isUpdating.value!! && shop.stage.value != shop.maxStage) {
                binding.btnLevelUp.visibility = View.VISIBLE
            } else {
                binding.btnLevelUp.visibility = View.INVISIBLE
            }
        })

        playerModel.isRewardedAdReady.observe(this as LifecycleOwner, {
            if (it && shop.isUpdating.value!!)
                binding.btnSkipLevelUp.visibility = View.VISIBLE
            else
                binding.btnSkipLevelUp.visibility = View.INVISIBLE
        })

        shop.isUpdating.observe(this as LifecycleOwner, {
            if (it) {
                binding.openNewCard.visibility = View.INVISIBLE
                binding.achievedMaxStageCard.visibility = View.GONE
                binding.expansionCard.visibility = View.VISIBLE
                binding.btnLevelUp.visibility = View.INVISIBLE
                if (playerModel.isRewardedAdReady.value!!)
                    binding.btnSkipLevelUp.visibility = View.VISIBLE
                else
                    binding.btnSkipLevelUp.visibility = View.INVISIBLE

            }
            else if (shop.stage.value != shop.maxStage) {
                binding.openNewCard.visibility = View.VISIBLE
                binding.achievedMaxStageCard.visibility = View.GONE
                binding.expansionCard.visibility = View.GONE
                if (playerModel.balance.value!! >= shop.updateCost.value!!)
                    binding.btnLevelUp.visibility = View.VISIBLE
                binding.btnSkipLevelUp.visibility = View.INVISIBLE
            }
            else if (shop.stage.value == shop.maxStage) {
                binding.openNewCard.visibility = View.INVISIBLE
                binding.achievedMaxStageCard.visibility = View.VISIBLE
                binding.expansionCard.visibility = View.GONE
                binding.btnLevelUp.visibility = View.INVISIBLE
                binding.btnSkipLevelUp.visibility = View.INVISIBLE
            }
        })

        shop.timeLeft.observe(this as LifecycleOwner, {
            binding.tvTimeLeft.text = Strings.get(R.string.remain_time,
            it.floorDiv(1000*60*60), it.floorDiv(1000*60).mod(60), it.floorDiv(1000).mod(3600).mod(60) + 1)
        })

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
                replace(R.id.fl_wrapper, BusinessSettingsFragment(shop, position))
                addToBackStack(null)
            }
        }


        binding.btnSkipLevelUp.setOnClickListener {

            showAd()
            //shop.skipRaisingStage()
        }



        binding.btnLevelUp.setOnClickListener {
            if (playerModel.balance.value!! >= shop.updateCost.value!! && !shop.isUpdating.value!!) {
                playerModel.balance.value = playerModel.balance.value!! - shop.updateCost.value!!
                shop.beginRaisingStage()
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
                shop.skipRaisingStage()
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

    private fun initStaticViews() {
        if (playerModel.balance.value!! >= shop.updateCost.value!! && !shop.isUpdating.value!! && shop.stage.value != shop.maxStage) {
            binding.btnLevelUp.visibility = View.VISIBLE
        } else {
            binding.btnLevelUp.visibility = View.INVISIBLE
        }

        binding.tvCategory.setText(shop.categoryId)
        binding.tvTitle.text = shop.title
    }

//    private fun initViews() {
//        shop as BusinessShop
//        binding.apply {
//            tvTitle.text = shop.title
//            tvProfit.text = shop.profitString
//            tvStage.text = (shop as BusinessShop).stage.toString()
//            tvCategory.text = (shop as BusinessShop).category
//            tvToLevelUp.text = context?.getString(R.string.money, (shop as BusinessShop).updateCost)
//            tvProfitGrow.text = context?.getString(R.string.money, (shop.profit * (shop as BusinessShop).profitGrowFactor - shop.profit))
//        }
//
//        if (!(playerModel.ownedBusinesses.value?.get(position) as BusinessShop).isUpdating &&
//            playerModel.balance.value!! >= (playerModel.ownedBusinesses.value?.get(position) as BusinessShop).updateCost)
//                binding.btnLevelUp.visibility = View.VISIBLE
//        else
//                binding.btnLevelUp.visibility = View.INVISIBLE
//
//        if ((playerModel.ownedBusinesses.value?.get(position) as BusinessShop).isUpdating) {
//            binding.openNewCard.visibility = View.INVISIBLE
//            binding.expansionCard.visibility = View.VISIBLE
//        } else {
//            binding.openNewCard.visibility = View.VISIBLE
//            binding.expansionCard.visibility = View.GONE
//        }
//    }

    fun init(pShop: Business, pPosition: Int) {
        position = pPosition
        shop = (playerModel.ownedBusinesses.value?.get(position) as BusinessShop?)!!
    }

}