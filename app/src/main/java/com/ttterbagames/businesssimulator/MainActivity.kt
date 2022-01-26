package com.ttterbagames.businesssimulator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.databinding.ActivityMainBinding
import com.ttterbagames.businesssimulator.fragments.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.*
import java.util.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val playerModel: PlayerModel by viewModels()

    lateinit var dataBaseHelper: DataBaseHelper

    private val profileFragment = ProfileFragment()
    private val businessFragment = BusinessFragment()
    private val moneyClickerFragment = MoneyClickerFragment()
    private val stocksFragment = StocksFragment()
    private val realEstateFragment = RealEstateFragment()

    var s = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playerModel.dataBaseHelper = DataBaseHelper(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        MobileAds.initialize(this) {}
        var adRequest = AdRequest.Builder().build()

        RewardedAd.load(this, Strings.get(R.string.rewarded_ad_unit_id), adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.d("RewardedAd", p0.message)
                playerModel.mRewardedAd = null
                playerModel.isRewardedAdReady.postValue(false)
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d("RewardedAd", "Add was loaded.")
                playerModel.mRewardedAd = rewardedAd
                playerModel.isRewardedAdReady.postValue(true)
            }
        })

        adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, Strings.get(R.string.interstitial_ad_unit_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("InterstitialAd", adError?.message)
                playerModel.mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("InterstitialAd", "Ad was loaded.")
                playerModel.mInterstitialAd = interstitialAd
            }
        })

        dataBaseHelper = DataBaseHelper(this)

        replaceFragment(moneyClickerFragment)
        binding.bottomNavigation.selectedItemId = R.id.ic_money_clicker

        binding.bottomNavigation.setOnItemSelectedListener { mi ->
            when (mi.itemId){
                R.id.ic_money_clicker -> replaceFragment(moneyClickerFragment)
                R.id.ic_profile -> replaceFragment(profileFragment)
                R.id.ic_business -> replaceFragment(businessFragment)
                R.id.ic_stocks -> replaceFragment(stocksFragment)
                R.id.ic_real_estate -> replaceFragment(realEstateFragment)
            }
            true
        }

        initPlayerModel()
        addStocks()
        addAllRealEstate()

        addOwnedCars()
        addOwnedAircraft()

        setPrimaryVehiclesImageId()


        setStatsValues()



        val timer = Timer()
        val paymentPeriod: Long = GlobalConstants.PAYMENT_PERIOD
        val scheduler = PaymentScheduler(playerModel, paymentPeriod)
        timer.schedule(scheduler, 0, paymentPeriod)


        playerModel.balance.observe(this as LifecycleOwner, {
            dataBaseHelper.setBalance(it)
        })


    }


    override fun onStop() {
        super.onStop()
        dataBaseHelper.setTimeStamp(System.currentTimeMillis())
    }

    override fun onDestroy() {
        super.onDestroy()
        dataBaseHelper.setTimeStamp(System.currentTimeMillis())
    }





    private fun replaceFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }


    private fun initPlayerModel() {

        playerModel.balance.value = dataBaseHelper.getBalance()

        playerModel.level.value = dataBaseHelper.getClickerLevel()
        playerModel.payPerClick.value = dataBaseHelper.getPayPerClick()
        playerModel.toLevelUp.value = dataBaseHelper.getClickerToLevelUp()

        playerModel.ownedBusinesses.value = ArrayList<Business>()
        playerModel.ownedStockList.value = ArrayList<Stock>()
        playerModel.ownedRealEstate.value = ArrayList<RealEstate>()
        playerModel.realEstateOffersList.value = ArrayList<RealEstate>()
        playerModel.ownedCarList = ArrayList<PurchasedCar>()
        playerModel.ownedAircraftList = ArrayList<PurchasedAircraft>()

        //Добавление магазинов и произоводтс
        addShopsAndFactories()
        //Добавление таксопарков и транспортников
        playerModel.balance.value = playerModel.balance.value!! + addTaxiAndTransportation()
        //Добавление билдинг компанис
        addBuildingCompanies()
        //Добавление банков
        addBanks()

        playerModel.summaryOwnedBusinessesProfit.value = 0.0
        if (playerModel.ownedBusinesses.value != null )
            for (b in playerModel.ownedBusinesses.value!!) {
                playerModel.summaryOwnedBusinessesProfit.value =
                    playerModel.summaryOwnedBusinessesProfit.value!!.plus(b.profit.value!!)
            }
    }

    fun setStatsValues() {
        playerModel.earnedInClicker = dataBaseHelper.getEarnedInClicker()
        playerModel.earnedInBusiness = dataBaseHelper.getEarnedInBusiness()
        playerModel.earnedOnRealEstate = dataBaseHelper.getEarnedOnRealEstate()
        playerModel.earnedOnDividend = dataBaseHelper.getEarnedOnDividend()
        playerModel.earnedOnStocksSelling = dataBaseHelper.getEarnedOnStocksSelling()
    }

    fun addShopsAndFactories() {
        val shops = dataBaseHelper.getAllShops()
        val time = dataBaseHelper.getTimeStamp()
        var sum = 0.0
        for (b in shops) {

            if (time != (0).toLong() && time != (-1).toLong()) {
                val income = b.doBackgroundWork(System.currentTimeMillis() - time)
                sum += income
                Log.d("income", income.toString())
                playerModel.balance.value = playerModel.balance.value!! + income
                dataBaseHelper.setShopAllTimeEarnings(b.id, b.allTimeEarnings)
            }
            b.startCountingAllTimeEarnings()
            playerModel.ownedBusinesses.value!!.add(b)
        }

        val factories = dataBaseHelper.getAllFactories()
        for (f in factories) {

            if (time != (0).toLong() && time != (-1).toLong()) {
                val income = f.doBackgroundWork(System.currentTimeMillis() - time)
                sum += income
                Log.d("income", income.toString())
                playerModel.balance.value = playerModel.balance.value!! + income
                dataBaseHelper.setShopAllTimeEarnings(f.id, f.allTimeEarnings)
            }

            f.startCountingAllTimeEarnings()
            playerModel.ownedBusinesses.value!!.add(f)
        }

        val newEarnedInBusiness = dataBaseHelper.getEarnedInBusiness() + sum
        dataBaseHelper.setEarnedInBusiness(newEarnedInBusiness)
    }

    fun addTaxiAndTransportation(): Double {
        //Получили id такси комп. и транс. комп.
        val taxiIds = dataBaseHelper.getTaxiCompaniesId()
        val transportationIds = dataBaseHelper.getTransportationCompaniesId()

        //Время работы в бэкграунде
        val duration: Long = System.currentTimeMillis() - dataBaseHelper.getTimeStamp()

        //Машины для удаления и общий доход в бэкграунде
        val carsToDelete = ArrayList<Int>()
        var gross = 0.0

        //Проход по id таксиКомпаний
        for (id in taxiIds) {
            val cars = dataBaseHelper.getAllTaxiCars(id)
            var income = 0.0
            var profit = 0.0
            var capitalization = dataBaseHelper.getTaxiCapitalization(id)

            for (car in cars) {
                val bgMileage = (duration.toDouble() / 1000.0 / 3600.0) * car.mileagePerHour.toDouble()
                val remainMileage = car.maxMileage - car.mileage.value!!

                if (bgMileage.toInt() < remainMileage) {
                    profit += car.profit
                    income += (duration.toDouble() / 1000.0 / 3600.0) * car.profit
                    dataBaseHelper.setCarMileage(car.id, bgMileage.toInt() + car.mileage.value!!)
                } else {
                    capitalization -= car.price
                    carsToDelete.add(car.id)
                    income += remainMileage.toDouble() / car.mileagePerHour.toDouble() * car.profit
                }
            }
            gross += income

            val allTimeEarns = dataBaseHelper.getTaxiAllTimeEarnings(id) + income
            dataBaseHelper.setTaxiAllTimeEarnings(id, allTimeEarns)
            dataBaseHelper.setTaxiProfit(id, profit)
            dataBaseHelper.setTaxiCapitalization(id, capitalization)
        }

        //Проход по id транспортКомпаний
        for (id in transportationIds) {
            val cars = dataBaseHelper.getAllTransportationCars(id)
            var income = 0.0
            var profit = 0.0
            var capitalization = dataBaseHelper.getTaxiCapitalization(id)

            for (car in cars) {
                val bgMileage = (duration.toDouble() / 1000.0 / 3600.0) * car.mileagePerHour.toDouble()
                val remainMileage = car.maxMileage - car.mileage.value!!

                if (bgMileage.toInt() < remainMileage) {
                    profit += car.profit
                    income += (duration.toDouble() / 1000.0 / 3600.0) * car.profit
                    dataBaseHelper.setCarMileage(car.id, bgMileage.toInt() + car.mileage.value!!)
                } else {
                    capitalization -= car.price
                    carsToDelete.add(car.id)
                    income += remainMileage.toDouble() / car.mileagePerHour.toDouble() * car.profit
                }
            }
            gross += income

            val allTimeEarns = dataBaseHelper.getTaxiAllTimeEarnings(id) + income
            dataBaseHelper.setTaxiAllTimeEarnings(id, allTimeEarns)
            dataBaseHelper.setTaxiProfit(id, profit)
            dataBaseHelper.setTaxiCapitalization(id, capitalization)
        }

        //Удаление из бд сломаных авто
        for (id in carsToDelete) {
            dataBaseHelper.deleteTaxiTransportationCar(id)
        }

        //Добавление такси компаний + bgWork
        val taxiCompanies = dataBaseHelper.getAllTaxiCompanies()
        for (company in taxiCompanies) {
            company.ownedCarList.value = dataBaseHelper.getAllTaxiCars(company.id)
            company.startCountingMileage()
            company.doBackgroundWork(duration)
            company.startCountingAllTimeEarnings()
        }
        playerModel.ownedBusinesses.value!!.addAll(taxiCompanies)

        //Добавление транспорт компаний + bgWork
        val transportCompanies = dataBaseHelper.getAllTransportationCompanies()
        for (company in transportCompanies) {
            company.ownedCarList.value = dataBaseHelper.getAllTransportationCars(company.id)
            company.startCountingMileage()
            company.doBackgroundWork(duration)
            company.startCountingAllTimeEarnings()
        }
        playerModel.ownedBusinesses.value!!.addAll(transportCompanies)

        val newEarnedInBusiness = dataBaseHelper.getEarnedInBusiness() + gross
        dataBaseHelper.setEarnedInBusiness(newEarnedInBusiness)

        return gross
    }

    fun addBanks() {
        val banks = dataBaseHelper.getAllBanks()
        val time = dataBaseHelper.getTimeStamp()
        var sum = 0.0
        for (b in banks) {

            if (time != (0).toLong() && time != (-1).toLong()) {
                val income = b.doBackgroundWork(System.currentTimeMillis() - time)
                sum += income
                dataBaseHelper.setBankAllTimeEarnings(b.id, b.allTimeEarnings)
            }
            b.startCountingAllTimeEarnings()
            b.startWorking()
            playerModel.ownedBusinesses.value!!.add(b)
        }
        playerModel.balance.value = playerModel.balance.value!! + sum

        val newEarnedInBusiness = dataBaseHelper.getEarnedInBusiness() + sum
        dataBaseHelper.setEarnedInBusiness(newEarnedInBusiness)
    }

    fun addBuildingCompanies() {
        val duration: Long = System.currentTimeMillis() - dataBaseHelper.getTimeStamp()

        val companies = dataBaseHelper.getAllBuildingCompanies()
        for (c in companies) {
            val buildings = dataBaseHelper.getAllBuildings(c.id)
            for (b in buildings) {
                b.dataBaseHelper = playerModel.dataBaseHelper
                b.doBackroundWork(duration)
            }
            if (c.buildingList.value == null)
                c.buildingList.value = ArrayList<Building>()
            c.buildingList.value!!.addAll(buildings)
        }
        playerModel.ownedBusinesses.value!!.addAll(companies)
    }

    fun addStocks() {
        Log.d("stocks", dataBaseHelper.isStocksTableEmpty().toString())
        val duration: Long = System.currentTimeMillis() - dataBaseHelper.getTimeStamp()

        if (dataBaseHelper.isStocksTableEmpty()) {
            val arr = getStockList()
            for (s in arr) {
                s.id = dataBaseHelper.addNewStock(s)
                playerModel.stockMarketLots.add(s)
            }
            playerModel.isStockMarketLotsInitialized = true
            playerModel.startStockMarket()
        } else {
            val arr = dataBaseHelper.getAllStocks()
            var sum = 0.0
            for (s in arr) {
                sum += s.doBackgroundWork(duration)
                playerModel.stockMarketLots.add(s)
                if (s.soldLotsCount > 0) {
                    if (playerModel.ownedStockList.value == null)
                        playerModel.ownedStockList.value = ArrayList<Stock>()
                    playerModel.ownedStockList.value!!.add(s)
                }
            }

            var sumProfit = 0.0
            if (playerModel.ownedStockList.value != null)
                for (s in playerModel.ownedStockList.value!!) {
                    sumProfit += s.currentPrice * (s.dividendPercent / 100) * s.soldLotsCount
                }
            playerModel.summaryStocksProfit.value = sumProfit

            playerModel.isStockMarketLotsInitialized = true

            playerModel.resumeStockMarket()

            playerModel.balance.value = playerModel.balance.value!! + sum
            val newEarnedOnDividend = dataBaseHelper.getEarnedOnDividend() + sum
            dataBaseHelper.setEarnedOnDividend(newEarnedOnDividend)
        }
    }


    fun addAllRealEstate() {
        val duration: Long = System.currentTimeMillis() - dataBaseHelper.getTimeStamp()

        if (playerModel.realEstateOffersList.value == null)
            playerModel.realEstateOffersList.value = ArrayList<RealEstate>()
        if (playerModel.ownedRealEstate.value == null)
            playerModel.ownedRealEstate.value = ArrayList<RealEstate>()

        val offers = dataBaseHelper.getOffersRealEstate()
        val owned = dataBaseHelper.getOwnedRealEstate()

        var sum = 0.0
        for (re in owned) {
            sum += re.Price * RealEstateConstants.profitFactor
        }
        playerModel.summaryOwnedRealEstateProfit.value = sum

        val gross = sum * (duration.toDouble() / 1000.0 / 60.0 / 60.0)
        playerModel.balance.value = playerModel.balance.value!! + gross

        val newEarnedOnRealEstate = dataBaseHelper.getEarnedOnRealEstate() + gross
        dataBaseHelper.setEarnedOnRealEstate(newEarnedOnRealEstate)

        playerModel.realEstateOffersList.value!!.addAll(offers)
        playerModel.ownedRealEstate.value!!.addAll(owned)
    }


    fun addOwnedCars() {
        val arr = dataBaseHelper.getOwnedCars()
        playerModel.ownedCarList.addAll(arr)
    }

    fun addOwnedAircraft() {
        val arr = dataBaseHelper.getOwnedAircrafts()
        playerModel.ownedAircraftList.addAll(arr)
    }


    fun setPrimaryVehiclesImageId() {
        val carId = dataBaseHelper.getPrimaryCarId()
        val aircraftId = dataBaseHelper.getPrimaryAircraftId()

        for (c in playerModel.ownedCarList) {
            if (c.id == carId) {
                playerModel.primaryCarImageId = c.imageId
            }
        }

        for (a in playerModel.ownedAircraftList) {
            if (a.id == aircraftId) {
                playerModel.primaryAircraftImageId = a.imageId
            }
        }
    }


    fun getStockList(): ArrayList<Stock> {
        val arr = ArrayList<Stock>()
        val inputStream: InputStream = resources.openRawResource(R.raw.stock_market_companies)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        reader.readLines().forEach {

            val items = it.split(",")

            arr.add( Stock(items[0], items[1].toLong(), items[2].toDouble(), items[3].toDouble()) )

        }
        return arr
    }

    fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit
    ) = launch {
        onPreExecute() // runs in Main Thread
        val result = withContext(Dispatchers.IO) {
            doInBackground() // runs in background thread without blocking the Main Thread
        }
        onPostExecute(result) // runs in Main Thread
    }

}