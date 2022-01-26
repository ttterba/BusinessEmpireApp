package com.ttterbagames.businesssimulator

import java.util.*

class PaymentScheduler(playerModel: PlayerModel, paymentPeriod: Long) : TimerTask() {
    private val pm = playerModel
    private val pp = paymentPeriod
    override fun run() {
        pm.balance.postValue(pm.getSummaryProfit().div(60*60*1000 / pp).let {
            pm.balance.value?.plus(
                it
            )
        })

    }

}