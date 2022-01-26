package com.ttterbagames.businesssimulator

import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.random.Random

class Stock(val title: String, val lotsCount: Long, val declaredPrice: Double, val dividendPercent: Double) {

    var id = -1
    var lastPrice = 0.0

    var currentPrice = declaredPrice
    val priceChangeNotifier: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }
    var soldLotsCount: Long = 0

    var priceToReach = calcPriceToReach()
    var lastPricesStack = ArrayDeque<Double>(StockParams.STACK_SIZE)
    var lastDiff = 0.0
    var lastDiffPercent = 0.0

    var lastUpdateHour = 0
    var lastUpdateMinute = 0

    var moneySpentOnIt = 0.0

    var lastUpdateMillis: Long = 0

    fun updatePrice() {
        if (currentPrice < priceToReach) {
            val step = Random.nextDouble(-StockParams.STEP / Random.nextDouble(1.1, 2.5), StockParams.STEP)
            lastDiff = currentPrice * step
            lastDiffPercent = step * 100
            currentPrice += currentPrice * step
            lastPricesStack.push(currentPrice)
            if (lastPricesStack.size > StockParams.STACK_SIZE)
                lastPricesStack.removeLast()
            //println(currentPrice)
            if (currentPrice >= priceToReach)
                priceToReach = calcPriceToReach()
        } else {
            val step = Random.nextDouble(-StockParams.STEP, StockParams.STEP / Random.nextDouble(1.1, 2.5))
            lastDiff = currentPrice * step
            lastDiffPercent = step * 100
            currentPrice += currentPrice * step
            lastPricesStack.push(currentPrice)
            if (lastPricesStack.size > StockParams.STACK_SIZE)
                lastPricesStack.removeLast()
            //println(currentPrice)
            if (currentPrice <= priceToReach)
                priceToReach = calcPriceToReach()
        }

        val rightNow = Calendar.getInstance()
        lastUpdateHour = rightNow.get(Calendar.HOUR_OF_DAY)
        lastUpdateMinute = rightNow.get(Calendar.MINUTE)
        lastUpdateMillis = System.currentTimeMillis()
        priceChangeNotifier.postValue(true)
    }

    fun doBackgroundWork(duration: Long): Double {
        var sum = 0.0
        if (soldLotsCount > 0)
            sum += (soldLotsCount * declaredPrice * dividendPercent / 100) * (duration.toDouble() / 1000.0 / 60.0 / 60.0)

        for (i in 1..StockParams.STACK_SIZE) {
            updatePrice()
        }

        val numOfPeriods = duration.div(StockParams.UPDATE_PERIOD)

        lastPricesStack.push(lastPrice)
        currentPrice = lastPrice
        if (lastPricesStack.size > StockParams.STACK_SIZE)
            lastPricesStack.removeLast()

        if (numOfPeriods > 0) {
            for (i in 1..numOfPeriods)
                updatePrice()
        }

        return sum
    }

    private fun calcPriceToReach(): Double {
        val maxPlus = declaredPrice + declaredPrice * StockParams.BOUNDS - currentPrice
        val maxMinus = (declaredPrice - declaredPrice * StockParams.BOUNDS) - currentPrice

        val diff = Random.nextDouble(maxMinus, maxPlus)
        //println("Новая цель: ${currentPrice + diff}")
        return currentPrice + diff
    }
}