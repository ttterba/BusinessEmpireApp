package com.ttterbagames.businesssimulator

import androidx.lifecycle.MutableLiveData

class BusinessBuildingCompany(dataBaseHelper : DataBaseHelper): Business(dataBaseHelper) {

    override var imageId: Int = R.drawable.business_im_building_company
    override var title: String = Strings.get(R.string.building_company)
    override var businessType: String = Strings.get(R.string.building_company)

    override var businessNumber = BusinessConstants.BUILDING_NUMBER

    override var capitalization: Double = BusinessConstants.BUILDING_COMPANY_OPEN_PRICE
    override val profit : MutableLiveData<Double> by lazy{
        MutableLiveData<Double>(0.0)
    }

    val buildingList: MutableLiveData<ArrayList<Building>> by lazy {
        MutableLiveData<ArrayList<Building>>()
    }

    val buildersNumber : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>(0)
    }

    val metalNumber : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>(0)
    }

    val woodNumber : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>(0)
    }

    val concreteNumber : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>(0)
    }

    fun calculateCapitalization() {
        capitalization = BusinessConstants.BUILDING_COMPANY_OPEN_PRICE +
                buildersNumber.value!! * ResourcesCost.BUILDER +
                metalNumber.value!! * ResourcesCost.METAL +
                woodNumber.value!! * ResourcesCost.WOOD +
                concreteNumber.value!! * ResourcesCost.CONCRETE
    }

    fun addBuilding(building: Building) {
        var arr = ArrayList<Building>()
        if (buildingList.value != null)
            arr = buildingList.value!!
        building.startBuilding()
        arr.add(building)
        buildingList.postValue(arr)
    }

}

object ResourcesCost {
    const val WOOD = 324.0
    const val CONCRETE = 47.0
    const val METAL = 1171.0
    const val BUILDER = 2650.0
}