package com.ttterbagames.businesssimulator

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.pow

class BusinessBank(dataBaseHelper : DataBaseHelper): Business(dataBaseHelper) {

    open val maxStage = 35
    override var imageId: Int = R.drawable.business_im_bank
    override var title: String = Strings.get(R.string.bank)
    override var businessType: String = Strings.get(R.string.bank)

    override var businessNumber = BusinessConstants.BANK_NUMBER

    var minDepositPercent = 6.3
    var maxDepositPercent = 8.5
    var minCreditPercent = 7.2
    var maxCreditPercent = 10.8

    var capitalGrowFactor = 1.0


    override var capitalization: Double = BusinessConstants.BANK_OPEN_PRICE
    override val profit : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(0.0)
    }

    val creditPercent : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(StartParameters.creditPercent)
    }
    val depositPercent : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(StartParameters.depositPercent)
    }


    val totalMoney : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(StartParameters.totalMoney)
    }

    val moneyInCredits : MutableLiveData<Double> = MutableLiveData<Double>(getCreditMoneyFactor() * StartParameters.totalMoney)

    val updateCost : MutableLiveData<Double> = MutableLiveData<Double>(1000000.00)
    val vaultCapacity : MutableLiveData<Long> = MutableLiveData<Long>(15000000)

    var updateTime = 60.0
    var vaultGrowFactor = 1.36

    var affiliateGrowFactor = 1.32


    val lastDepositIncome : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(0.0)
    }
    val lastCreditIncome : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(0.0)
    }
    val lastDepositPayment : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(0.0)
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
    var bankWorkTimer = Timer()
    val period: Long = 1000 * 60 * 20 //20 минут
    //val period: Long = 1000 * 15

    fun getCapitalGrowingFactor(): Double {
        var factor = vaultGrowFactor.pow(1.0/BankValues.maxGrowTime)
        val p = (vaultGrowFactor.pow(1.0/BankValues.minGrowTime) - vaultGrowFactor.pow(1.0/BankValues.maxGrowTime))

        factor += (depositPercent.value!! - minDepositPercent) * p / (maxDepositPercent - minDepositPercent)
        Log.d("deposit", factor.toString())
        return factor
    }

    fun getCreditMoneyFactor(): Double {
        var factor = 1.0
        val p = (1.0 - BankValues.minMoneyInCreditFactor)

        factor -= (creditPercent.value!! - minCreditPercent) * p / (maxCreditPercent - minCreditPercent)
        Log.d("credit", factor.toString())
        return factor
    }

    fun payDay() {
        lastDepositIncome.postValue(totalMoney.value!! * (getCapitalGrowingFactor() - 1))
        lastCreditIncome.postValue(moneyInCredits.value!! * creditPercent.value!! / 100)
        lastDepositPayment.postValue(totalMoney.value!! * depositPercent.value!! / 100)


        if (totalMoney.value!! + lastDepositIncome.value!! > vaultCapacity.value!!)
            totalMoney.postValue(vaultCapacity.value!!.toDouble())
        else
            totalMoney.postValue(totalMoney.value!! + lastDepositIncome.value!!)

        moneyInCredits.postValue(totalMoney.value!! * getCreditMoneyFactor())
        profit.postValue((moneyInCredits.value!! * creditPercent.value!! / 100 - totalMoney.value!! * depositPercent.value!! / 100) *
                (1000.0 * 60.0 * 60.0) / period.toDouble())

        dataBaseHelper.setBankAfterPayDay(id, profit.value!!, totalMoney.value!!, moneyInCredits.value!!,
            lastDepositIncome.value!!, lastCreditIncome.value!!, lastDepositPayment.value!!)
    }

    fun startWorking() {

        bankWorkTimer.schedule(0, period) {
            payDay()
        }
    }

    fun beginRaisingStage() {

        isUpdating.value = true
        dataBaseHelper.setBankIsUpdating(id, 1)
        val time = (updateTime * 60 * 1000).toLong()
        //val time: Long = 1000 * 40
        timer = object: CountDownTimer( time, 1000 ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = millisUntilFinished
                dataBaseHelper.setBankTimeLeft(id, millisUntilFinished)
            }

            override fun onFinish() {
                isUpdating.value = false
                dataBaseHelper.setBankIsUpdating(id, 0)

                raiseStage()
                dataBaseHelper.updateBankStage(id, stage.value!!, updateCost.value!!, updateTime, capitalization, vaultCapacity.value!!)
            }
        }
        timer.start()
    }

    fun beginRaisingStage(time: Long) {
        isUpdating.value = true
        dataBaseHelper.setBankIsUpdating(id, 1)
        timer = object: CountDownTimer( time, 1000 ) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.value = millisUntilFinished
                dataBaseHelper.setBankTimeLeft(id, millisUntilFinished)
            }

            override fun onFinish() {
                isUpdating.value = false

                raiseStage()
                dataBaseHelper.updateBankStage(id, stage.value!!, updateCost.value!!, updateTime, capitalization, vaultCapacity.value!!)
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

        updateTime += 60

        capitalization += updateCost.value!!
        vaultCapacity.value = (vaultCapacity.value!! * vaultGrowFactor).toLong()
        updateCost.value = updateCost.value!! * affiliateGrowFactor
        stage.value = stage.value?.plus(1)

        return true
    }

    override fun doBackgroundWork(duration: Long): Double {
        var sum = 0.0
        val periodsNumber = duration.div(period)
        val extraTime = duration.mod(period)

        var periodsBeforeExpanded = periodsNumber
        if (isUpdating.value!!)
            periodsBeforeExpanded = duration.coerceAtMost(timeLeft.value!!) / period
        val periodsAfterExpanded = periodsNumber - periodsBeforeExpanded


        for (i in 1..periodsBeforeExpanded) {
            Log.d("bank", "before")
            sum += profit.value!! * (period.toDouble() / 1000.0 / 3600.0)
            lastDepositIncome.value = (totalMoney.value!! * (getCapitalGrowingFactor() - 1))
            lastCreditIncome.value = (moneyInCredits.value!! * creditPercent.value!! / 100)
            lastDepositPayment.value = (totalMoney.value!! * depositPercent.value!! / 100)


            if (totalMoney.value!! + lastDepositIncome.value!! > vaultCapacity.value!!)
                totalMoney.value = (vaultCapacity.value!!.toDouble())
            else
                totalMoney.value = (totalMoney.value!! + lastDepositIncome.value!!)

            moneyInCredits.value = (totalMoney.value!! * getCreditMoneyFactor())
            profit.postValue((moneyInCredits.value!! * creditPercent.value!! / 100 - totalMoney.value!! * depositPercent.value!! / 100) *
                    (1000.0 * 60.0 * 60.0) / period.toDouble())
        }


        if (!isUpdating.value!!) {

        } else {
            if (duration < timeLeft.value!!) {

                timeLeft.value = timeLeft.value!! - duration
                beginRaisingStage(timeLeft.value!!)
            } else {

                isUpdating.value = false
                dataBaseHelper.setBankIsUpdating(id, 0)

                raiseStage()
                dataBaseHelper.updateBankStage(id, stage.value!!, updateCost.value!!, updateTime, capitalization, vaultCapacity.value!!)

            }
        }

        for (i in 1..periodsAfterExpanded) {
            lastDepositIncome.value = (totalMoney.value!! * (getCapitalGrowingFactor() - 1))
            lastCreditIncome.value = (moneyInCredits.value!! * creditPercent.value!! / 100)
            lastDepositPayment.value = (totalMoney.value!! * depositPercent.value!! / 100)

            sum += profit.value!! * (period.toDouble() / 1000.0 / 3600.0)

            if (totalMoney.value!! + lastDepositIncome.value!! > vaultCapacity.value!!)
                totalMoney.value = (vaultCapacity.value!!.toDouble())
            else
                totalMoney.value = (totalMoney.value!! + lastDepositIncome.value!!)

            moneyInCredits.value = (totalMoney.value!! * getCreditMoneyFactor())
            profit.value = ((moneyInCredits.value!! * creditPercent.value!! / 100 - totalMoney.value!! * depositPercent.value!! / 100) *
                    (1000.0 * 60.0 * 60.0) / period.toDouble())
        }

        dataBaseHelper.setBankAfterPayDay(id, profit.value!!, totalMoney.value!!, moneyInCredits.value!!,
            lastDepositIncome.value!!, lastCreditIncome.value!!, lastDepositPayment.value!!)


        allTimeEarnings += sum

        return sum
    }
}

object BankValues {
    const val maxGrowTime = 10.4
    const val minGrowTime = 6

    const val minMoneyInCreditFactor = 0.70
}

object StartParameters {
    const val creditPercent = 8.85
    const val depositPercent = 7.04
    const val totalMoney = 10000000.0
}