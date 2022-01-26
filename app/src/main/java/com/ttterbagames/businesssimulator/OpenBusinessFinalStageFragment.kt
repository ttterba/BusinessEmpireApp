package com.ttterbagames.businesssimulator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.ttterbagames.businesssimulator.databinding.FragmentOpenBusinessFinalStageBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlin.random.Random


class OpenBusinessFinalStageFragment : Fragment() {
    private val playerModel: PlayerModel by activityViewModels()

    lateinit var binding: FragmentOpenBusinessFinalStageBinding
    var businessId = 1
    var openPrice = 0.0
    //var business = Business()
    lateinit var business: Business

    lateinit var dataBaseHelper: DataBaseHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBaseHelper = DataBaseHelper(requireContext())

        binding = FragmentOpenBusinessFinalStageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOpenBusinessFinalStageBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        binding.btnSubmit.setOnClickListener {
            if (binding.edTitle.text.toString() != "")
                business.title = binding.edTitle.text.toString()

            if (playerModel.balance.value!! >= openPrice) {
                addOwnedBusiness(business)
                requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                playerModel.balance.value = playerModel.balance.value!! - openPrice

                showInterstitialAd(0.8)
            } else {
                binding.tvMessage.visibility = View.VISIBLE
            }

        }

        val bundle = this.arguments
        if (bundle != null)
            businessId = bundle.getInt(BusinessConstants.BUSINESS_TYPE_KEY)

        when(businessId) {
            BusinessConstants.SHOP_L1_ID -> {
                binding.openBusinessFinalStageTitle.text = getString(R.string.write_shop_title)
                binding.tvSummary.text = getString(R.string.money, BusinessConstants.SHOP_L1_OPEN_PRICE)
                business = BusinessShop(playerModel.dataBaseHelper)
                (business as BusinessShop).initData(1)
                openPrice = BusinessConstants.SHOP_L1_OPEN_PRICE
            }
            BusinessConstants.SHOP_L2_ID -> {
                binding.openBusinessFinalStageTitle.text = getString(R.string.write_shop_title)
                binding.tvSummary.text = getString(R.string.money, BusinessConstants.SHOP_L2_OPEN_PRICE)
                business = BusinessShop(playerModel.dataBaseHelper)
                (business as BusinessShop).initData(2)
                openPrice = BusinessConstants.SHOP_L2_OPEN_PRICE
            }
            BusinessConstants.SHOP_L3_ID -> {
                binding.openBusinessFinalStageTitle.text = getString(R.string.write_shop_title)
                binding.tvSummary.text = getString(R.string.money, BusinessConstants.SHOP_L3_OPEN_PRICE)
                business = BusinessShop(playerModel.dataBaseHelper)
                (business as BusinessShop).initData(3)
                openPrice = BusinessConstants.SHOP_L3_OPEN_PRICE
            }
            BusinessConstants.FACTORY_L1_ID -> {
                binding.openBusinessFinalStageTitle.text = getString(R.string.write_factory_title)
                binding.tvSummary.text = getString(R.string.money, BusinessConstants.FACTORY_L1_OPEN_PRICE)

                business = BusinessFactory(playerModel.dataBaseHelper)
                (business as BusinessFactory).initData(1)
                openPrice = BusinessConstants.FACTORY_L1_OPEN_PRICE
            }
            BusinessConstants.FACTORY_L2_ID -> {
                binding.openBusinessFinalStageTitle.text = getString(R.string.write_factory_title)
                binding.tvSummary.text = getString(R.string.money, BusinessConstants.FACTORY_L2_OPEN_PRICE)
                business = BusinessFactory(playerModel.dataBaseHelper)
                (business as BusinessFactory).initData(2)
                openPrice = BusinessConstants.FACTORY_L2_OPEN_PRICE
            }
            BusinessConstants.TAXI_STATION_ID -> {
                binding.openBusinessFinalStageTitle.text = getString(R.string.write_taxi_station_title)
                binding.tvSummary.text = getString(R.string.money, BusinessConstants.TAXI_STATION_OPEN_PRICE)

                business = BusinessTaxiStation(playerModel.dataBaseHelper)
                business as BusinessTaxiStation
                openPrice = BusinessConstants.TAXI_STATION_OPEN_PRICE
            }
            BusinessConstants.TRANSPORTATION_COMPANY_ID -> {
                binding.openBusinessFinalStageTitle.text = getString(R.string.write_transportation_company_title)
                binding.tvSummary.text = getString(R.string.money, BusinessConstants.TRANSPORTATION_COMPANY_OPEN_PRICE)

                business = BusinessTransportationCompany(playerModel.dataBaseHelper)
                business as BusinessTransportationCompany
                openPrice = BusinessConstants.TRANSPORTATION_COMPANY_OPEN_PRICE
            }
            BusinessConstants.BUILDING_COMPANY_ID -> {
                binding.openBusinessFinalStageTitle.text = getString(R.string.write_building_company_title)
                binding.tvSummary.text = getString(R.string.money, BusinessConstants.BUILDING_COMPANY_OPEN_PRICE)

                business = BusinessBuildingCompany(playerModel.dataBaseHelper)
                business as BusinessBuildingCompany
                openPrice = BusinessConstants.BUILDING_COMPANY_OPEN_PRICE
            }
            BusinessConstants.BANK_ID -> {
                binding.openBusinessFinalStageTitle.text = getString(R.string.write_bank_title)
                binding.tvSummary.text = getString(R.string.money, BusinessConstants.BANK_OPEN_PRICE)

                business = BusinessBank(playerModel.dataBaseHelper)
                business as BusinessBank
                openPrice = BusinessConstants.BANK_OPEN_PRICE
            }
        }

    }

    private fun addOwnedBusiness(b : Business) {
        var tmp = playerModel.ownedBusinesses.value
        if (tmp == null)
            tmp = ArrayList<Business>()
        if (businessId != BusinessConstants.BUILDING_COMPANY_ID)
            b.startCountingAllTimeEarnings()
        if (businessId == BusinessConstants.TAXI_STATION_ID)
            (b as BusinessTaxiStation).startCountingMileage()
        if (businessId == BusinessConstants.TRANSPORTATION_COMPANY_ID)
            (b as BusinessTransportationCompany).startCountingMileage()

        if (businessId == BusinessConstants.BANK_ID)
            (b as BusinessBank).startWorking()
        tmp.add(b)

        playerModel.ownedBusinesses.value = tmp

        if (businessId == BusinessConstants.SHOP_L1_ID || businessId == BusinessConstants.SHOP_L2_ID || businessId == BusinessConstants.SHOP_L3_ID)
            b.id = dataBaseHelper.addNewShop(b as BusinessShop)
        if (businessId == BusinessConstants.FACTORY_L1_ID || businessId == BusinessConstants.FACTORY_L2_ID)
            b.id = dataBaseHelper.addNewFactory(b as BusinessFactory)

        if (businessId == BusinessConstants.TAXI_STATION_ID)
            b.id = dataBaseHelper.addNewTaxiCompany(b as BusinessTaxiStation)
        if (businessId == BusinessConstants.TRANSPORTATION_COMPANY_ID)
            b.id = dataBaseHelper.addNewTransportationCompany(b as BusinessTransportationCompany)

        if(businessId == BusinessConstants.BUILDING_COMPANY_ID)
            b.id = dataBaseHelper.addNewBuildingCompany(b as BusinessBuildingCompany)

        if(businessId == BusinessConstants.BANK_ID) {
            b.id = dataBaseHelper.addNewBank(b as BusinessBank)

            b.lastDepositIncome.value = (b.totalMoney.value!! * (b.getCapitalGrowingFactor() - 1))
            b.lastCreditIncome.value = (b.moneyInCredits.value!! * b.creditPercent.value!! / 100)
            b.lastDepositPayment.value = (b.totalMoney.value!! * b.depositPercent.value!! / 100)


            b.moneyInCredits.value = (b.totalMoney.value!! * b.getCreditMoneyFactor())
            b.profit.value = ((b.moneyInCredits.value!! * b.creditPercent.value!! / 100 - b.totalMoney.value!! * b.depositPercent.value!! / 100) *
                    (1000.0 * 60.0 * 60.0) / b.period.toDouble())

            dataBaseHelper.setBankAfterPayDay(b.id, b.profit.value!!, b.totalMoney.value!!, b.moneyInCredits.value!!,
                b.lastDepositIncome.value!!, b.lastCreditIncome.value!!, b.lastDepositPayment.value!!)
        }

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