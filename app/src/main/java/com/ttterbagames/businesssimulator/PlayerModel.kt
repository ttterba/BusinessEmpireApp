package com.ttterbagames.businesssimulator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class PlayerModel : ViewModel() {

    var mRewardedAd: RewardedAd? = null
    val isRewardedAdReady : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    var mInterstitialAd: InterstitialAd? = null

    private var stockMarketTimer = Timer()
    lateinit var dataBaseHelper: DataBaseHelper

    val balance : MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    //Кликер
    val level : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val payPerClick : MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    val toLevelUp : MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    //Бизнесы
    val ownedBusinesses : MutableLiveData<ArrayList<Business>> by lazy {
        MutableLiveData<ArrayList<Business>>()
    }

    //Недвижимость
    val ownedRealEstate : MutableLiveData<ArrayList<RealEstate>> by lazy {
        MutableLiveData<ArrayList<RealEstate>>()
    }
    val realEstateOffersList : MutableLiveData<ArrayList<RealEstate>> by lazy {
        MutableLiveData<ArrayList<RealEstate>>()
    }
    var isRealEstateListInitialized = false


    val summaryOwnedBusinessesProfit : MutableLiveData<Double> by lazy {
        MutableLiveData<Double>(0.0)
    }

    val summaryOwnedRealEstateProfit : MutableLiveData<Double> by lazy {
        MutableLiveData<Double>(0.0)
    }

    //Для статистики
    var earnedInClicker = 0.0
    var earnedInBusiness = 0.0
    var earnedOnRealEstate = 0.0
    var earnedOnDividend = 0.0
    var earnedOnStocksSelling = 0.0


    //Акции
    var stockMarketLots = ArrayList<Stock>()
    var isStockMarketLotsInitialized = false
    val updateStockNotifier : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }
    val moneySpentToStocks : MutableLiveData<Double> by lazy {
        MutableLiveData<Double>(0.0)
    }
    val summaryStocksProfit : MutableLiveData<Double> by lazy {
        MutableLiveData<Double>(0.0)
    }
    val ownedStockList : MutableLiveData<ArrayList<Stock>> by lazy {
        MutableLiveData<ArrayList<Stock>>()
    }


    var primaryCarImageId = R.drawable.ic_car
    var primaryAircraftImageId = R.drawable.ic_aircraft

    fun startStockMarket() {
        for (i in 1..StockParams.STACK_SIZE) {
            for (s in stockMarketLots)
                s.updatePrice()
        }
        stockMarketTimer.schedule(0, StockParams.UPDATE_PERIOD) {
            for (s in stockMarketLots) {
                s.updatePrice()
                dataBaseHelper.updateStockAfterChange(s.id, s.currentPrice, s.lastDiff, s.lastDiffPercent, System.currentTimeMillis())
            }

            updateStockNotifier.postValue(1)
        }
    }

    fun resumeStockMarket() {
        stockMarketTimer.schedule(StockParams.UPDATE_PERIOD, StockParams.UPDATE_PERIOD) {
            for (s in stockMarketLots) {
                s.updatePrice()
                dataBaseHelper.updateStockAfterChange(s.id, s.currentPrice, s.lastDiff, s.lastDiffPercent, System.currentTimeMillis())
            }
            updateStockNotifier.postValue(1)
        }
    }

    //Транспорт
    var isPurchasedCarsInitialized = false
    var purchasedCarList = ArrayList<PurchasedCar>()
    var ownedCarList = ArrayList<PurchasedCar>()

    var isPurchasedAircraftsInitialized = false
    var purchasedAircraftList = ArrayList<PurchasedAircraft>()
    var ownedAircraftList = ArrayList<PurchasedAircraft>()

    fun getSummaryProfit() : Double {
        var sum = 0.0
        if (summaryOwnedBusinessesProfit.value != null)
            sum += summaryOwnedBusinessesProfit.value!!
        if (summaryOwnedRealEstateProfit.value != null)
            sum += summaryOwnedRealEstateProfit.value!!
        if (summaryStocksProfit.value != null)
            sum += summaryStocksProfit.value!!

        earnedInBusiness += summaryOwnedBusinessesProfit.value!!.div(60*60*1000 / GlobalConstants.PAYMENT_PERIOD)
        try {
            dataBaseHelper.setEarnedInBusiness(earnedInBusiness)
        }
        catch(e: Exception) { }

        earnedOnDividend += summaryStocksProfit.value!!.div(60*60*1000 / GlobalConstants.PAYMENT_PERIOD)
        try {
            dataBaseHelper.setEarnedOnDividend(earnedOnDividend)
        }
        catch(e: Exception) { }

        earnedOnRealEstate += summaryOwnedRealEstateProfit.value!!.div(60*60*1000 / GlobalConstants.PAYMENT_PERIOD)
        dataBaseHelper.setEarnedOnRealEstate(earnedOnRealEstate)
        try {
            dataBaseHelper.setEarnedOnRealEstate(earnedOnRealEstate)
        }
        catch(e: Exception) { }

        return sum
    }
}

object ClickerParams {
    const val CLICKS_TO_LEVEL_UP = 1800
    const val PAY_GROW = 1.5849
    const val BASE_CLICK_PAY = 1.0

    const val BREAKPOINT_LEVEL = 16
    const val PAY_GROW_AFTER_BREAKPOINT = 1.03

    const val MAX_LEVEL = 5
    const val MAX_PAY_PER_CLICK = 10.0
}