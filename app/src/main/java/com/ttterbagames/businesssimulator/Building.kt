package com.ttterbagames.businesssimulator

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData

class Building(val buildingType: Int, var dataBaseHelper: DataBaseHelper? = null) {

    var imageId = 0
    var title = "unnamed"

    var id = -1
    var ownerId = -1

    var woodNumber = 0
    var concreteNumber = 0
    var metalNumber = 0
    var buildersNumber = 0

    var sellPrice = 0.0
    var primeCost = 0.0
    var hoursToBuild = 0

    val isBuilt : MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>(false)
    }

    val isBoosted : MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>(false)
    }

    val timeLeft : MutableLiveData<Long> by lazy{
        MutableLiveData<Long>(0)
    }

    lateinit var timer: CountDownTimer
    var countDownInterval: Long = 1000 * 30 // 30 секунд

    init {
        when(buildingType) {
            BuildingTypes.PRIVATE_HOUSE -> {
                woodNumber = 50
                concreteNumber = 170
                metalNumber = 4
                buildersNumber = 4
                sellPrice = 59200.0
                hoursToBuild = 3
                imageId = R.drawable.buildings_private_house
                title = Strings.get(R.string.private_house)
            }
            BuildingTypes.OFFICE_BUILDING -> {
                woodNumber = 400
                concreteNumber = 1500
                metalNumber = 12
                buildersNumber = 13
                sellPrice = 385300.00
                hoursToBuild = 10
                imageId = R.drawable.buildings_office_building
                title = Strings.get(R.string.office_building)
            }
            BuildingTypes.SHOPPING_CENTER -> {
                woodNumber = 300
                concreteNumber = 3800
                metalNumber = 25
                buildersNumber = 90
                sellPrice = 644130.0
                hoursToBuild = 12
                imageId = R.drawable.buildings_shopping_center
                title = Strings.get(R.string.shopping_center)
            }
            BuildingTypes.APARTMENT_BUILDING -> {
                woodNumber = 2200
                concreteNumber = 30200
                metalNumber = 660
                buildersNumber = 150
                sellPrice = 4161225.0
                hoursToBuild = 20
                imageId = R.drawable.buildings_apartment_house
                title = Strings.get(R.string.apartment_building)
            }
            BuildingTypes.SKYSCRAPER -> {
                woodNumber = 2800
                concreteNumber = 190000
                metalNumber = 1450
                buildersNumber = 800
                sellPrice = 20482000.0
                hoursToBuild = 35
                imageId = R.drawable.buildings_skyscraper
                title = Strings.get(R.string.skyscraper)
            }
        }
        primeCost = woodNumber * ResourcesCost.WOOD + concreteNumber * ResourcesCost.CONCRETE +
                metalNumber * ResourcesCost.METAL + buildersNumber * ResourcesCost.BUILDER
    }

    fun doBackroundWork(duration: Long) {
        if (duration < timeLeft.value!!) {
            timeLeft.value = timeLeft.value!! - duration
            startBuilding(timeLeft.value!!)
        } else {
            startBuilding(1)
        }
    }

    fun startBuilding() {
        val time: Long = (hoursToBuild * 60 * 60 * 1000).toLong()
        //val time: Long = 1000 * 40
        timer = object: CountDownTimer( time, countDownInterval ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = millisUntilFinished
                dataBaseHelper?.setBuildingTimeLeft(id, millisUntilFinished)
            }

            override fun onFinish() {
                isBuilt.value = true
                dataBaseHelper?.setBuildingIsBuilt(id, 1)
            }
        }
        timer.start()
    }

    fun startBuilding(time: Long) {
        timer = object: CountDownTimer( time, countDownInterval ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = millisUntilFinished
                dataBaseHelper?.setBuildingTimeLeft(id, millisUntilFinished)
            }

            override fun onFinish() {
                isBuilt.value = true
                dataBaseHelper?.setBuildingIsBuilt(id, 1)
            }
        }
        timer.start()
    }

    fun boostBuilding() {
        timer.cancel()
        isBoosted.value = true
        val boostFactor = 3
        timeLeft.value = timeLeft.value!! / boostFactor
        timer = object: CountDownTimer(timeLeft.value!!, countDownInterval ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = millisUntilFinished
                dataBaseHelper?.setBuildingTimeLeft(id, millisUntilFinished)
            }

            override fun onFinish() {
                isBuilt.value = true
                dataBaseHelper?.setBuildingIsBuilt(id, 1)
            }
        }
        timer.start()
    }

    fun stopBuilding() {
        timer.cancel()
    }
}

object BuildingTypes {
    const val PRIVATE_HOUSE = 0
    const val OFFICE_BUILDING = 1
    const val SHOPPING_CENTER = 2
    const val APARTMENT_BUILDING = 3
    const val SKYSCRAPER = 4
}