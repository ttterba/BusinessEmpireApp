package com.ttterbagames.businesssimulator

import androidx.lifecycle.MutableLiveData

class TransportationCar(title: String, price: Double, imageId: Int, val taxiCategory : Int, val maxMileage: Int, imageName: String) : Car(title, price, imageId, imageName) {

    var tariff = "unnamed"
    var profit = 0.0
    var mileagePerHour = 0

    var id = -1
    var ownerId = -1

    val mileage : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>(0)
    }

    var isBroken = false

    init {
        when (taxiCategory) {

            TransportationCategories.CITY -> {
                tariff = Strings.get(R.string.transportation_city)
                profit = TransportationProfits.CITY
                mileagePerHour = TransportationMileagePerHour.CITY
            }

            TransportationCategories.INTER_CITY -> {
                tariff = Strings.get(R.string.transportation_intercity)
                profit = TransportationProfits.INTER_CITY
                mileagePerHour = TransportationMileagePerHour.INTER_CITY
            }

        }


    }
}

object TransportationCategories {
    const val CITY = 0
    const val INTER_CITY = 1
}

object TransportationProfits {
    const val CITY = 1750.0
    const val INTER_CITY = 3800.0
}

object TransportationMileagePerHour {
//    const val CITY = 9027
//    //const val CITY = 99000000
//    const val INTER_CITY = 5952

    const val CITY = 18000
    const val INTER_CITY = 24000
}