package com.ttterbagames.businesssimulator

class PurchasedCar(var tit: String, var pr: Double, val level: Int, var imId: Int): Car(tit, pr, imId, "заглушка") {
    var motorType = "none"
    var designType = "none"
    var moneySpentOnIt = 0.0

    var id = -1
}