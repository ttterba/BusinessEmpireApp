package com.ttterbagames.businesssimulator

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class BusinessTransportationCompany(dataBaseHelper : DataBaseHelper): Business(dataBaseHelper) {

    override var imageId: Int = R.drawable.business_im_transportation
    override var title: String = Strings.get(R.string.transportation)
    override var businessType: String = Strings.get(R.string.transportation)

    var expansionType = 0

    override var businessNumber = BusinessConstants.TRANSPORTATION_NUMBER


    override var capitalization: Double = BusinessConstants.TRANSPORTATION_COMPANY_OPEN_PRICE
    override val profit : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(0.0)
    }
    val numberOfCars : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>(0)
    }

    val carParkCapacity : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>(5)
    }

    val isUpdating : MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>(false)
    }

    val timeLeft : MutableLiveData<Long> by lazy{
        MutableLiveData<Long>(0)
    }

    val ownedCarList: MutableLiveData<ArrayList<TransportationCar>> by lazy {
        MutableLiveData<ArrayList<TransportationCar>>()
    }

    var carsToRemove = ArrayList<Int>()

    lateinit var raiseCapacityTimer: CountDownTimer
    var carsMileageTimer = Timer()

    fun beginRaisingStage(num: Int) {

        isUpdating.value = true
        dataBaseHelper.setTaxiIsUpdating(id , 1)
        expansionType = num
        dataBaseHelper.setExpansionType(id, num)

        var updateTime = 0.0
        when (num) {
            5 -> updateTime += TransportationCarParkUpdateTime.SMALL
            10 -> updateTime += TransportationCarParkUpdateTime.MEDIUM
            20 -> updateTime += TransportationCarParkUpdateTime.BIG
        }
        raiseCapacityTimer = object: CountDownTimer( (updateTime * 60 * 1000).toLong(), 1000 ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = millisUntilFinished
                dataBaseHelper.setTaxiTimeLeft(id, millisUntilFinished)
            }

            override fun onFinish() {
                isUpdating.value = false
                dataBaseHelper.setTaxiIsUpdating(id , 0)
                raiseCapacity(num)

                dataBaseHelper.setTaxiCarParkCapacity(id, carParkCapacity.value!!)
                dataBaseHelper.setTaxiCapitalization(id, capitalization)
            }
        }
        raiseCapacityTimer.start()
    }

    fun beginRaisingStage(num: Int, duration: Long) {

        isUpdating.value = true
        dataBaseHelper.setTaxiIsUpdating(id , 1)

        raiseCapacityTimer = object: CountDownTimer( duration, 1000 ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = millisUntilFinished
                try {
                    dataBaseHelper.setTaxiTimeLeft(id, millisUntilFinished)
                } catch (e: Exception) {

                }
            }

            override fun onFinish() {
                isUpdating.value = false
                dataBaseHelper.setTaxiIsUpdating(id , 0)
                raiseCapacity(num)

                dataBaseHelper.setTaxiCarParkCapacity(id, carParkCapacity.value!!)
                dataBaseHelper.setTaxiCapitalization(id, capitalization)
            }
        }
        raiseCapacityTimer.start()
    }

    fun raiseCapacity(num: Int) {
        carParkCapacity.value = carParkCapacity.value?.plus(num)

        when (num) {
            5 -> capitalization += TransportationCarParkPrices.SMALL
            10 -> capitalization += TransportationCarParkPrices.MEDIUM
            20 -> capitalization += TransportationCarParkPrices.BIG
        }
    }

    fun skipRaisingCapacity() {
        raiseCapacityTimer.onFinish()
        raiseCapacityTimer.cancel()
    }

    fun startCountingMileage() {
        val period: Long = 1000 * 30 //30 сек

        carsMileageTimer.schedule(0, period) {
            try {
                var sum = 0.0
                if (ownedCarList.value != null) {
                    val brokenCars = ArrayList<TransportationCar>()
                    for (car in ownedCarList.value!!) {
                        val km: Int = (car.mileagePerHour.toDouble() / 60 / 60 / 1000 * period).toInt()
                        car.mileage.postValue(car.mileage.value?.plus(km))
                        dataBaseHelper.setCarMileage(car.id, car.mileage.value!!)
                        if (car.mileage.value!! >= car.maxMileage) {
                            brokenCars.add(car)
                        } else {
                            sum += car.profit
                        }
                    }
                    if (brokenCars.size > 0) {
                        for (car in brokenCars) {
                            ownedCarList.value!!.remove(car)
                            capitalization -= car.price

                            dataBaseHelper.deleteTaxiTransportationCar(car.id)
                            dataBaseHelper.setTaxiCapitalization(id, capitalization)
                        }
                        ownedCarList.postValue(ownedCarList.value)
                    }

                }
                profit.postValue(sum)
            } catch (e: Exception) {

            }

        }
    }

    override fun doBackgroundWork(duration: Long): Double {

        if (isUpdating.value!!) {
            if (duration < timeLeft.value!!) {
                timeLeft.value = timeLeft.value!! - duration
                beginRaisingStage(expansionType, timeLeft.value!!)
            } else {
                beginRaisingStage(expansionType, 1)
            }
        }

        return 0.0
    }

}

object TransportationCarParkPrices {
    const val SMALL = 30000.0
    const val MEDIUM = 80000.0
    const val BIG = 200000.0
}

object TransportationCarParkUpdateTime {
    const val SMALL = 90.0
    const val MEDIUM = 300.0
    const val BIG = 600.0

//    const val SMALL = 0.3
//    const val MEDIUM = 0.6
//    const val BIG = 0.9
}