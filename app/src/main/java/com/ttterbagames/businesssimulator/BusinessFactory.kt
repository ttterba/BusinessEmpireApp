package com.ttterbagames.businesssimulator

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData

open class BusinessFactory(dataBaseHelper : DataBaseHelper): Business(dataBaseHelper) {



    var maxStage = 20
    override var imageId: Int = R.drawable.business_im_factory
    override var title = Strings.get(R.string.factory)
    override var businessType = Strings.get(R.string.factory)

    var category = Strings.get(R.string.small_factory)

    override var businessNumber = BusinessConstants.FACTORY_NUMBER

    var level = 1
    val updateCost : MutableLiveData<Double> = MutableLiveData<Double>(2999.88)
    var updateTime:Double = 10.0
    var profitGrowFactor = 1.16

    var affiliateFactor = 0.12

    override var capitalization: Double = BusinessConstants.FACTORY_L1_OPEN_PRICE
    override val profit : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(24999.0)
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
                maxStage = 20
                level = 1
                updateCost.value = 2999.88
                updateTime = 10.0
                profitGrowFactor = 1.16
                capitalization = BusinessConstants.FACTORY_L1_OPEN_PRICE
                profit.value = 520.0
                affiliateFactor = 0.12
                category = Strings.get(R.string.small_factory)
            }
            2 -> {
                maxStage = 50
                level = 2
                updateCost.value = 27748.5
                updateTime = 2.0
                profitGrowFactor = 1.17
                capitalization = BusinessConstants.FACTORY_L2_OPEN_PRICE
                profit.value = 3853.0
                affiliateFactor = 0.15
                category = Strings.get(R.string.big_factory)
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
            1 -> updateTime += 10
            2 -> {
                if (stage.value!! >= 16)
                    updateTime += 60
                else
                    updateTime *= 1.6
            }
        }

        capitalization += updateCost.value!!
        profit.value = profit.value?.times(profitGrowFactor)
        updateCost.value = capitalization * affiliateFactor
        stage.value = stage.value?.plus(1)

        timeLeft.value = 0

        return true
    }
}