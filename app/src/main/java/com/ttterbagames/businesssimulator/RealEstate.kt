package com.ttterbagames.businesssimulator

class RealEstate(var City: String, var Country: String, var Price: Long, var imageId: Int, var isOwned: Boolean = false) {
    var profitFactor = RealEstateConstants.profitFactor
    var imageName = ""
    var id = -1
}

object RealEstateConstants {
    const val profitFactor = 0.0158 //1,58 %
}