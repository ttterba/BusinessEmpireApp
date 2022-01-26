package com.ttterbagames.businesssimulator

import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.concurrent.schedule

open class Business(val dataBaseHelper : DataBaseHelper) {
//    open lateinit var ctx: Context

    var id = -1

    open var title: String = "unnamed"
    open var businessType = "unnamed"
    open var businessNumber = -1
    open val profit : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(0.0)
    }
    open var capitalization: Double = 0.0
    open var imageId: Int = 0
//    open val profitString: String
//        get() = ctx.getString(R.string.money, profit.value)


//    fun setContext(pCtx: Context) {
//        ctx = pCtx
//    }

    open var allTimeEarnings = 0.0

    open val isCountingProfit : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val countingAllTimeEarningsTimer = Timer()

    open fun doBackgroundWork(duration: Long): Double {
        return 0.0
    }

    fun setData (pTitle: String,
                pBusinessType: String,
                pProfit: Double,
                pCapitalization: Double,
                pImageId: Int) {
        title = pTitle
        businessType = pBusinessType
        profit.value = pProfit
        capitalization = pCapitalization
        imageId = pImageId

    }

    fun startCountingAllTimeEarnings() {
        isCountingProfit.value = true
        countingAllTimeEarningsTimer.schedule(0, GlobalConstants.PAYMENT_PERIOD) {
            try {
                allTimeEarnings += profit.value!! / (1000 * 60 * 60 / GlobalConstants.PAYMENT_PERIOD)
                if (businessNumber == BusinessConstants.SHOP_NUMBER || businessNumber == BusinessConstants.FACTORY_NUMBER)
                    dataBaseHelper.setShopAllTimeEarnings(id, allTimeEarnings)
                if (businessNumber == BusinessConstants.TAXI_NUMBER || businessNumber == BusinessConstants.TRANSPORTATION_NUMBER)
                    dataBaseHelper.setTaxiAllTimeEarnings(id, allTimeEarnings)
                if (businessNumber == BusinessConstants.BANK_NUMBER)
                    dataBaseHelper.setBankAllTimeEarnings(id, allTimeEarnings)
            } catch (e: Exception) { }

        }
    }

    fun stopCountingAllTimeEarnings() {
        isCountingProfit.value = false
        countingAllTimeEarningsTimer.cancel()
    }
}
