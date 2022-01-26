package com.ttterbagames.businesssimulator

import androidx.lifecycle.MutableLiveData

class TaxiCar(title: String, price: Double, imageId: Int, val taxiCategory : Int, val maxMileage: Int, imageName: String) : Car(title, price, imageId, imageName) {

    var tariff = "unnamed"
    var profit = 0.0
    var mileagePerHour = 0

    var id = -1
    var ownerId = -1

    val mileage : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>(0)
    }

    var isBroken = false
    var selected = false



    init {
        when (taxiCategory) {

            TaxiCategories.ECONOMY -> {
                tariff = Strings.get(R.string.taxi_economy)
                profit = TaxiProfits.ECONOMY
                mileagePerHour = TaxiMileagePerHour.ECONOMY
            }

            TaxiCategories.COMFORT -> {
                tariff = Strings.get(R.string.taxi_comfort)
                profit = TaxiProfits.COMFORT
                mileagePerHour = TaxiMileagePerHour.COMFORT
            }

            TaxiCategories.COMFORT_PLUS -> {
                tariff = Strings.get(R.string.taxi_comfort_plus)
                profit = TaxiProfits.COMFORT_PLUS
                mileagePerHour = TaxiMileagePerHour.COMFORT_PLUS
            }

            TaxiCategories.BUSINESS -> {
                tariff = Strings.get(R.string.taxi_business)
                profit = TaxiProfits.BUSINESS
                mileagePerHour = TaxiMileagePerHour.BUSINESS
            }

            TaxiCategories.PREMIER -> {
                tariff = Strings.get(R.string.taxi_premier)
                profit = TaxiProfits.PREMIER
                mileagePerHour = TaxiMileagePerHour.PREMIER
            }

            TaxiCategories.ELITE -> {
                tariff = Strings.get(R.string.taxi_elite)
                profit = TaxiProfits.ELITE
                mileagePerHour = TaxiMileagePerHour.ELITE
            }
        }


    }
}

object TaxiCategories {
    const val ECONOMY = 0
    const val COMFORT = 1
    const val COMFORT_PLUS = 2
    const val BUSINESS = 3
    const val PREMIER = 4
    const val ELITE = 5
}

object TaxiProfits {
    const val ECONOMY = 560.0
    //const val ECONOMY = 3600000.0
    const val COMFORT = 954.0
    const val COMFORT_PLUS = 1180.0
    const val BUSINESS = 2420.0
    const val PREMIER = 5040.0
    const val ELITE = 13050.0
}

object TaxiMileagePerHour {
//    const val ECONOMY = 6944
//    const val COMFORT = 4166
//    const val COMFORT_PLUS = 3334
//    const val BUSINESS = 3125
//    const val PREMIER = 1785
//    const val ELITE = 2500

    const val ECONOMY = 12500
    const val COMFORT = 8300
    const val COMFORT_PLUS = 6900
    const val BUSINESS = 6200
    const val PREMIER = 3900
    const val ELITE = 5000

//    const val ECONOMY = 6200000
//    const val COMFORT = 62000000
//    const val COMFORT_PLUS = 62000000
//    const val BUSINESS = 62000000
//    const val PREMIER = 62000000
//    const val ELITE = 62000000
}
