package com.ttterbagames.businesssimulator

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData

open class BusinessShop(dataBaseHelper : DataBaseHelper): Business(dataBaseHelper) {
    init {
//        super.setContext(context)
    }

    open val maxStage = 10
    override var imageId: Int = R.drawable.business_im_shop
    override var title = Strings.get(R.string.shop)
    override var businessType = Strings.get(R.string.shop)

    override var businessNumber = BusinessConstants.SHOP_NUMBER

    var categoryId = R.string.local_shop

    var level = 1

    val updateCost : MutableLiveData<Double> = MutableLiveData<Double>(1469.70)

    var updateTime:Double = 2.0
    var profitGrowFactor = 1.35

    var affiliateFactor = 0.3

    override var capitalization: Double = 4899.00

    override val profit : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(102.06)
    }

    open val stage : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>(1)
    }

    val isUpdating : MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>(false)
    }

    val timeLeft : MutableLiveData<Long> by lazy{
        MutableLiveData<Long>(0)
    }

    lateinit var timer: CountDownTimer

    override fun doBackgroundWork(duration: Long): Double {
        var gross = 0.0

        Log.d("duration", "duration: ${duration}, timeLeft: ${timeLeft.value!!}")
        Log.d("worker", isUpdating.value.toString())
        if (!isUpdating.value!!) {
            Log.d("worker", "case 1")
            gross = (duration.toDouble() / GlobalConstants.HOUR_MILLIS.toDouble()) * profit.value!!
            allTimeEarnings += gross
            return gross
        } else {
            if (duration < timeLeft.value!!) {
                Log.d("worker", "case 2")
                gross = (duration.toDouble() / GlobalConstants.HOUR_MILLIS.toDouble()) * profit.value!!
                allTimeEarnings += gross
                timeLeft.value = timeLeft.value!! - duration
                beginRaisingStage(timeLeft.value!!)
                return gross
            } else {
                Log.d("worker", "case 3")
                val grossLastStage = (timeLeft.value!! / GlobalConstants.HOUR_MILLIS.toDouble()) * profit.value!!
                val timeOnNewStage = duration - timeLeft.value!!

                beginRaisingStage(1)

                val grossNewStage = (timeOnNewStage.toDouble() / GlobalConstants.HOUR_MILLIS.toDouble()) * profit.value!!

                gross = grossLastStage + grossNewStage
                allTimeEarnings += gross
                return gross
            }
        }
    }

    fun initData(lvl: Int) {
        when(lvl) {
            1 -> {
                level = 1
                updateCost.value = 1469.70
                updateTime = 2.0
                profitGrowFactor = 1.35
                capitalization = 4899.00
                profit.value = 102.06
                categoryId = R.string.local_shop
            }
            2 -> {
                level = 2
                updateCost.value = 3899.70
                updateTime = 20.0
                profitGrowFactor = 1.38
                capitalization = 12999.00
                profit.value = 324.97
                categoryId = R.string.small_shop_network
            }
            3 -> {
                level = 3
                updateCost.value = 16499.70
                updateTime = 20.0
                profitGrowFactor = 1.42
                capitalization = 54999.00
                profit.value = 1527.75
                categoryId = R.string.big_shop_network
            }
        }
    }

    fun beginRaisingStage() {

        isUpdating.value = true
        dataBaseHelper.setShopIsUpdating(id, 1)
        val time = (updateTime * 60 * 1000).toLong()
        timer = object: CountDownTimer( time, 1000 ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = millisUntilFinished
                dataBaseHelper.setShopTimeLeft(id, millisUntilFinished)
            }

            override fun onFinish() {
                isUpdating.value = false
                raiseStage()
                dataBaseHelper.updateShopStage(id, stage.value!!, updateCost.value!!, updateTime, profit.value!!, capitalization)
            }
        }
        timer.start()
    }

    fun beginRaisingStage(time: Long) {
        isUpdating.value = true
        dataBaseHelper.setShopIsUpdating(id, 1)
        timer = object: CountDownTimer( time, 1000 ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = millisUntilFinished
                dataBaseHelper.setShopTimeLeft(id, millisUntilFinished)
            }

            override fun onFinish() {
                isUpdating.value = false
                raiseStage()
                dataBaseHelper.updateShopStage(id, stage.value!!, updateCost.value!!, updateTime, profit.value!!, capitalization)
            }
        }
        timer.start()
    }

    fun skipRaisingStage() {
        timer.onFinish()
        timer.cancel()
    }

    fun raiseStage(): Boolean {
        if (stage.value == maxStage) return false

        when (level) {
            1 -> updateTime *= 2
            2 -> {
                if (stage.value!! >= 5)
                    updateTime += 20
                else
                    updateTime *=2
            }
            3 -> {
                if (stage.value!! >= 5)
                    updateTime += 20
                else
                    updateTime *=2
            }
        }

        stage.value = stage.value!! + 1
        capitalization += updateCost.value!!
        profit.value = profit.value?.times(profitGrowFactor)
        updateCost.value = capitalization * affiliateFactor

        timeLeft.value = 0

        return true
    }

}