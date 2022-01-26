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
import com.ttterbagames.businesssimulator.databinding.FragmentBankBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class BankFragment(var playerModel: PlayerModel) : Fragment() {

    lateinit var binding: FragmentBankBinding

    lateinit var bank: BusinessBank
    var position: Int = 0

    lateinit var bnv: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBankBinding.inflate(inflater)
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

    override fun onDetach() {
        super.onDetach()
        bnv.visibility = View.VISIBLE
    }

    private fun initStaticViews() {
        binding.tvTitle.text = bank.title
        binding.tvLastInfo.text = Strings.get(R.string.bank_last_period_info, (bank.period / 1000 / 60).toInt())
    }

    private fun setButtonListeners() {
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
                replace(R.id.fl_wrapper, BusinessSettingsFragment(bank, position))
                addToBackStack(null)
            }
        }

        binding.btnSkipLevelUp.setOnClickListener {
            showAd()
        }

        binding.btnLevelUp.setOnClickListener {
            if (playerModel.balance.value!! >= bank.updateCost.value!! && !bank.isUpdating.value!!) {
                playerModel.balance.value = playerModel.balance.value!! - bank.updateCost.value!!
                bank.beginRaisingStage()
            }
        }

        binding.btnConfigure.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, BankConfigureFragment(bank))
                addToBackStack(null)
            }
        }
    }

    private fun addObservers() {
        bank.apply {
            profit.observe(activity as LifecycleOwner, {
                binding.tvProfit.text = Strings.get(R.string.money, it)
                setAppropriateSummaryProfitFontSize(it)
            })

            creditPercent.observe(activity as LifecycleOwner, {
                binding.tvCreditPercent.text = Strings.get(R.string.bank_percent, it)
            })

            depositPercent.observe(activity as LifecycleOwner, {
                binding.tvDepositPercent.text = Strings.get(R.string.bank_percent, it)
            })

            totalMoney.observe(activity as LifecycleOwner, {
                binding.tvTotalMoney.text = Strings.get(R.string.money, it)
            })

            vaultCapacity.observe(activity as LifecycleOwner, {
                binding.tvVaultCapacity.text = Strings.get(R.string.bank_integer_money, it)
            })

            moneyInCredits.observe(activity as LifecycleOwner, {
                binding.tvInCredit.text = Strings.get(R.string.money, it)
                binding.tvInCreditPercent.text = Strings.get(R.string.bank_percent_in_brackets, it / totalMoney.value!! * 100)
            })

            stage.observe(activity as LifecycleOwner, {
                binding.tvToLevelUp.text = Strings.get(R.string.money, bank.updateCost.value!!)
                binding.tvCapacityGrow.text = Strings.get(R.string.money,
                    vaultCapacity.value!!.times((bank.vaultGrowFactor)))

                if (it == bank.maxStage) {
                    binding.openNewCard.visibility = View.GONE
                    binding.achievedMaxStageCard.visibility = View.VISIBLE
                    binding.expansionCard.visibility = View.GONE
                }
            })

            playerModel.isRewardedAdReady.observe(activity as LifecycleOwner, {
                if (it && bank.isUpdating.value!!)
                    binding.btnSkipLevelUp.visibility = View.VISIBLE
                else
                    binding.btnSkipLevelUp.visibility = View.INVISIBLE
            })

            playerModel.balance.observe(activity as LifecycleOwner, {
                if (it >= bank.updateCost.value!! && !bank.isUpdating.value!! && bank.stage.value != bank.maxStage) {
                    binding.btnLevelUp.visibility = View.VISIBLE
                } else {
                    binding.btnLevelUp.visibility = View.INVISIBLE
                }
            })

            isUpdating.observe(activity as LifecycleOwner, {
                when {
                    it -> {
                        binding.openNewCard.visibility = View.GONE
                        binding.achievedMaxStageCard.visibility = View.GONE
                        binding.expansionCard.visibility = View.VISIBLE
                        binding.btnLevelUp.visibility = View.GONE
                        if (playerModel.isRewardedAdReady.value!!)
                            binding.btnSkipLevelUp.visibility = View.VISIBLE
                        else
                            binding.btnSkipLevelUp.visibility = View.INVISIBLE

                    }
                    bank.stage.value != bank.maxStage -> {
                        binding.openNewCard.visibility = View.VISIBLE
                        binding.achievedMaxStageCard.visibility = View.GONE
                        binding.expansionCard.visibility = View.GONE
                        if (playerModel.balance.value!! >= bank.updateCost.value!!)
                            binding.btnLevelUp.visibility = View.VISIBLE
                        binding.btnSkipLevelUp.visibility = View.GONE
                    }
                    bank.stage.value == bank.maxStage -> {
                        binding.openNewCard.visibility = View.GONE
                        binding.achievedMaxStageCard.visibility = View.VISIBLE
                        binding.expansionCard.visibility = View.GONE
                        binding.btnLevelUp.visibility = View.GONE
                        binding.btnSkipLevelUp.visibility = View.GONE
                    }
                }
            })

            lastDepositIncome.observe(activity as LifecycleOwner, {
                binding.tvDepositIncome.text = Strings.get(R.string.money, it)
            })
            lastCreditIncome.observe(activity as LifecycleOwner, {
                binding.tvCreditsIncome.text = Strings.get(R.string.money, it)
            })
            lastDepositPayment.observe(activity as LifecycleOwner, {
                binding.tvDepositPayments.text = Strings.get(R.string.money, it)
            })

            timeLeft.observe(activity as LifecycleOwner, {
                binding.tvTimeLeft.text = Strings.get(R.string.remain_time,
                    it.floorDiv(1000*60*60), it.floorDiv(1000*60).mod(60), it.floorDiv(1000).mod(3600).mod(60) + 1)
            })
        }
    }

    fun init(pPosition: Int) {
        position = pPosition
        bank = (playerModel.ownedBusinesses.value?.get(position) as BusinessBank?)!!
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
        println(requireActivity())
        if (playerModel.mRewardedAd != null) {
            playerModel.mRewardedAd?.show(requireActivity()) {
                Log.d("RewardedAd", "User earned the reward.")
                if (bank.isUpdating.value!!)
                    bank.skipRaisingStage()
            }
            loadAd()
        } else {
            Log.d("RewardedAd", "The rewarded ad wasn't ready yet.")
            loadAd()
        }
    }


}