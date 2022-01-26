package com.ttterbagames.businesssimulator

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

class DataBaseHelper(val context: Context): SQLiteOpenHelper(context, "main.db", null, 1) {

    companion object {
        const val SHOP_ID = 0
        const val FACTORY_ID = 1
        const val TAXI_ID = 3
        const val TRANSPORTATION_ID = 4

        //region PLAYER_TABLE
        const val PLAYER_TABLE = "PLAYER_TABLE"
        const val COLUMN_BALANCE = "BALANCE"
        const val COLUMN_SAVED_TIME_STAMP = "SAVED_TIME_STAMP"
        const val COLUMN_PRIMARY_CAR_ID = "PRIMARY_CAR_ID"
        const val COLUMN_PRIMARY_AIRCRAFT_ID = "PRIMARY_AIRCRAFT_ID"
        //endregion

        //region STATS_TABLE
        const val STATS_TABLE = "STATS_TABLE"
        const val COLUMN_EARNED_IN_CLICKER = "EARNED_IN_CLICKER"
        const val COLUMN_EARNED_IN_BUSINESS = "EARNED_IN_BUSINESS"
        const val COLUMN_EARNED_ON_REAL_ESTATE = "EARNED_ON_REAL_ESTATE"
        const val COLUMN_EARNED_ON_DIVIDEND = "EARNED_ON_DIVIDEND"
        const val COLUMN_EARNED_ON_STOCKS_SELLING = "EARNED_ON_STOCKS_SELLING"
        //endregion

        //region CLICKER_TABLE
        const val CLICKER_TABLE = "CLICKER_TABLE"
        const val COLUMN_PAY_PER_CLICK = "PAY_PER_CLICK"
        const val COLUMN_LEVEL = "LEVEL"
        const val COLUMN_TO_LEVEL_UP = "TO_LEVEL_UP"
        //endregion


        //region SHOPS_FACTORIES_TABLE
        const val SHOPS_FACTORIES_TABLE = "SHOPS_FACTORIES_TABLE"
        const val COLUMN_ID = "ID"
        const val COLUMN_TITLE = "TITLE"
        const val COLUMN_TYPE = "TYPE"
        const val COLUMN_STAGE = "STAGE"
        //level
        const val COLUMN_UPDATE_COST = "UPDATE_COST"
        const val COLUMN_UPDATE_TIME = "UPDATE_TIME"
        const val COLUMN_PROFIT = "PROFIT"
        const val COLUMN_CAPITALIZATION = "CAPITALIZATION"
        const val COLUMN_IS_UPDATING = "IS_UPDATING"
        const val COLUMN_TIME_LEFT = "TIME_LEFT"
        const val COLUMN_ALL_TIME_EARNINGS = "ALL_TIME_EARNINGS"
        //endregion

        //region TAXI_TRANSPORTATION_TABLE
        const val TAXI_TRANSPORTATION_TABLE = "TAXI_TRANSPORTATION_TABLE"
        //id
        //type
        //title
        //capitalization
        //isUpdating
        //timeLeft
        //allTimeEarnings
        const val COLUMN_CAR_PARK_CAPACITY = "CAR_PARK_CAPACITY"
        const val COLUMN_EXPANSION_TYPE = "CAR_EXPANSION_TYPE"
        //profit
        //endregion

        //region TAXI_TRANSPORTATION_CARS_TABLE
        const val TAXI_TRANSPORTATION_CARS_TABLE = "TAXI_TRANSPORTATION_CARS_TABLE"
        //id
        //TITLE
        const val COLUMN_PRICE = "PRICE"
        const val COLUMN_IMAGE_ID = "IMAGE_ID"
        const val COLUMN_CATEGORY = "CATEGORY"
        const val COLUMN_MILEAGE = "MILEAGE"
        const val COLUMN_MAX_MILEAGE = "MAX_MILEAGE"
        const val COLUMN_OWNER_ID = "OWNER_ID"
        //endregion

        //region BUILDING_COMPANIES_TABLE
        const val BUILDING_COMPANIES_TABLE = "BUILDING_COMPANIES_TABLE"
        //id
        //title
        //profit
        //capitalization
        //allTimeEarnings
        const val COLUMN_WORKERS = "WORKERS"
        const val COLUMN_METAL = "METAL"
        const val COLUMN_WOOD = "WOOD"
        const val COLUMN_CONCRETE = "CONCRETE"
        //endregion

        //region BUILDINGS
        const val BUILDINGS_TABLE = "BUILDINGS_TABLE"
        //id
        //type
        const val COLUMN_IS_BOOSTED = "IS_BOOSTED"
        //timeLeft
        //ownerId
        const val COLUMN_IS_BUILT = "IS_BUILT"
        //endregion

        //region BANKS_TABLE
        const val BANKS_TABLE = "BANKS_TABLE"
        //id
        //title
        //stage
        //update_cost
        //update_time
        //profit
        //capitalization
        //is_updating
        //time_left
        //all_time_earnings
        const val COLUMN_CREDIT_PERCENT = "CREDIT_PERCENT"
        const val COLUMN_DEPOSIT_PERCENT = "DEPOSIT_PERCENT"
        const val COLUMN_TOTAL_MONEY = "TOTAL_MONEY"
        const val COLUMN_MONEY_IN_CREDITS = "MONEY_IN_CREDITS"
        const val COLUMN_VAULT_CAPACITY = "VAULT_CAPACITY"
        const val COLUMN_LAST_DEPOSIT_INCOME = "LAST_DEPOSIT_INCOME"
        const val COLUMN_LAST_CREDIT_INCOME = "LAST_CREDIT_INCOME"
        const val COLUMN_LAST_DEPOSIT_PAYMENT = "LAST_DEPOSIT_PAYMENT"
        //endregion


        //region STOCKS_TABLE
        const val STOCKS_TABLE = "STOCKS_TABLE"
        //id
        //TITLE
        const val COLUMN_LOTS_COUNT = "LOTS_COUNT"
        const val COLUMN_SOLD_LOTS_COUNT = "SOLD_LOTS_COUNT"
        const val COLUMN_DECLARED_PRICE = "DECLARED_PRICE"
        const val COLUMN_LAST_PRICE = "LAST_PRICE"
        const val COLUMN_DIVIDEND_PERCENT = "DIVIDEND_PERCENT"
        const val COLUMN_LAST_UPDATE_HOUR = "LAST_UPDATE_HOUR"
        const val COLUMN_LAST_UPDATE_MINUTE = "LAST_UPDATE_MINUTE"
        const val COLUMN_LAST_UPDATE_MILLIS = "LAST_UPDATE_MILLIS"
        const val COLUMN_MONEY_SPENT_ON_IT = "MONEY_SPENT_ON_IT"
        const val COLUMN_LAST_DIFF = "LAST_DIFF"
        const val COLUMN_LAST_DIFF_PERCENT = "LAST_DIFF_PERCENT"
        //endregion

        //region REAL_ESTATE_TABLE
        const val REAL_ESTATE_TABLE = "REAL_ESTATE_TABLE"
        //id
        const val COLUMN_CITY = "CITY"
        const val COLUMN_COUNTRY = "COUNTRY"
        //price
        const val COLUMN_IS_OWNED = "IS_OWNED"
        const val COLUMN_IMAGE_NAME = "IMAGE_NAME"
        //endregion

        //region PURCHASED_CARS_TABLE
        const val PURCHASED_CARS_TABLE = "PURCHASED_CARS_TABLE"
        //title
        //price
        //level
        //imageName
        //endregion

        //region OWNED_CARS_TABLE
        const val OWNED_CARS_TABLE = "OWNED_CARS_TABLE"
        //id
        //title
        //price
        //level
        //imageName
        const val COLUMN_MOTOR_TYPE = "MOTOR_TYPE"
        const val COLUMN_DESIGN_TYPE = "DESIGN_TYPE"
        //moneySpentOnIt
        //endregion

        //region PURCHASED_AIRCRAFTS_TABLE
        const val PURCHASED_AIRCRAFTS_TABLE = "PURCHASED_AIRCRAFTS_TABLE"
        //title
        //price
        //imageName
        //endregion

        //region OWNED_AIRCRAFTS_TABLE
        const val OWNED_AIRCRAFTS_TABLE = "OWNED_AIRCRAFTS_TABLE"
        //id
        //title
        //price
        //imageName
        const val COLUMN_TEAM = "TEAM"
        //designType
        //moneySpentOnIt
        //endregion



    }

    override fun onCreate(db: SQLiteDatabase) {

        //region Таблица PLAYER_TABLE
        val createPlayerTableStatement =
            "CREATE TABLE $PLAYER_TABLE ($COLUMN_BALANCE REAL, $COLUMN_SAVED_TIME_STAMP INTEGER, $COLUMN_PRIMARY_CAR_ID INTEGER, $COLUMN_PRIMARY_AIRCRAFT_ID INTEGER)"
        db.execSQL(createPlayerTableStatement)

        var cv = ContentValues()
        cv.put(COLUMN_BALANCE, 0.0)
        cv.put(COLUMN_SAVED_TIME_STAMP, 0)
        cv.put(COLUMN_PRIMARY_CAR_ID, -1)
        cv.put(COLUMN_PRIMARY_AIRCRAFT_ID, -1)
        db.insert(PLAYER_TABLE, null, cv)
        //endregion

        //region Таблица STATS_TABLE
        val createStatsTableStatement =
            "CREATE TABLE $STATS_TABLE ($COLUMN_EARNED_IN_CLICKER REAL, $COLUMN_EARNED_IN_BUSINESS REAL, $COLUMN_EARNED_ON_REAL_ESTATE REAL, $COLUMN_EARNED_ON_DIVIDEND REAL, $COLUMN_EARNED_ON_STOCKS_SELLING REAL)"
        db.execSQL(createStatsTableStatement)

        cv = ContentValues()
        cv.put(COLUMN_EARNED_IN_CLICKER, 0.0)
        cv.put(COLUMN_EARNED_IN_BUSINESS, 0.0)
        cv.put(COLUMN_EARNED_ON_REAL_ESTATE, 0.0)
        cv.put(COLUMN_EARNED_ON_DIVIDEND, 0.0)
        cv.put(COLUMN_EARNED_ON_STOCKS_SELLING, 0.0)
        db.insert(STATS_TABLE, null, cv)
        //endregion

        //region Таблица CLICKER_TABLE
        val createClickerTableStatement =
            "CREATE TABLE $CLICKER_TABLE ($COLUMN_PAY_PER_CLICK REAL, $COLUMN_LEVEL INTEGER, $COLUMN_TO_LEVEL_UP REAL)"
        db.execSQL(createClickerTableStatement)

        cv = ContentValues()
        cv.put(COLUMN_PAY_PER_CLICK, ClickerParams.BASE_CLICK_PAY)
        cv.put(COLUMN_LEVEL, 1)
        cv.put(COLUMN_TO_LEVEL_UP, ClickerParams.BASE_CLICK_PAY * ClickerParams.CLICKS_TO_LEVEL_UP)
        db.insert(CLICKER_TABLE, null, cv)
        //endregion


        //region Таблица SHOPS_FACTORIES_TABLE
        val createShopsFactoriesTableStatement =
            "CREATE TABLE $SHOPS_FACTORIES_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_TYPE INTEGER, $COLUMN_STAGE INTEGER, $COLUMN_LEVEL INTEGER, $COLUMN_UPDATE_COST REAL, $COLUMN_UPDATE_TIME REAL, $COLUMN_PROFIT REAL, $COLUMN_CAPITALIZATION REAL, $COLUMN_IS_UPDATING BOOL, $COLUMN_TIME_LEFT INTEGER, $COLUMN_ALL_TIME_EARNINGS REAL)"
        db.execSQL(createShopsFactoriesTableStatement)
        //endregion

        //region Таблица TAXI_TRANSPORTATION_TABLE
        val createTaxiTransportationTableStatement =
            "CREATE TABLE $TAXI_TRANSPORTATION_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_TYPE INTEGER, $COLUMN_CAPITALIZATION REAL, $COLUMN_IS_UPDATING BOOL, $COLUMN_TIME_LEFT INTEGER, $COLUMN_ALL_TIME_EARNINGS REAL, $COLUMN_CAR_PARK_CAPACITY INTEGER, $COLUMN_PROFIT REAL, $COLUMN_EXPANSION_TYPE INTEGER)"
        db.execSQL(createTaxiTransportationTableStatement)
        //endregion

        //region Таблица TAXI_TRANSPORTATION_CARS_TABLE
        val createTaxiTransportationCarsTableStatement =
            "CREATE TABLE $TAXI_TRANSPORTATION_CARS_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_PRICE REAL, $COLUMN_IMAGE_ID INTEGER, $COLUMN_CATEGORY INTEGER, $COLUMN_MILEAGE REAL, $COLUMN_MAX_MILEAGE REAL, $COLUMN_OWNER_ID INTEGER)"
        db.execSQL(createTaxiTransportationCarsTableStatement)
        //endregion

        //region Таблица BUILDING_COMPANIES_TABLE
        val createBuildingCompaniesTableStatement =
            "CREATE TABLE $BUILDING_COMPANIES_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_PROFIT REAL, $COLUMN_CAPITALIZATION REAL, $COLUMN_ALL_TIME_EARNINGS REAL, $COLUMN_WORKERS INTEGER, $COLUMN_METAL INTEGER, $COLUMN_WOOD INTEGER, $COLUMN_CONCRETE INTEGER)"
        db.execSQL(createBuildingCompaniesTableStatement)
        //endregion

        //region Таблица BUILDINGS
        val createBuildingsTableStatement =
            "CREATE TABLE $BUILDINGS_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TYPE INTEGER, $COLUMN_IS_BOOSTED BOOL, $COLUMN_TIME_LEFT INTEGER, $COLUMN_OWNER_ID INTEGER, $COLUMN_IS_BUILT BOOL)"
        db.execSQL(createBuildingsTableStatement)
        //endregion

        //region Таблица BANKS_TABLE
        val createBanksTableStatement =
            "CREATE TABLE $BANKS_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_STAGE INTEGER, $COLUMN_UPDATE_COST REAL, $COLUMN_UPDATE_TIME REAL, $COLUMN_PROFIT REAL, $COLUMN_CAPITALIZATION REAL, $COLUMN_IS_UPDATING BOOL, $COLUMN_TIME_LEFT INTEGER, $COLUMN_ALL_TIME_EARNINGS REAL, $COLUMN_CREDIT_PERCENT REAL, $COLUMN_DEPOSIT_PERCENT REAL, $COLUMN_TOTAL_MONEY REAL, $COLUMN_MONEY_IN_CREDITS REAL, $COLUMN_VAULT_CAPACITY INTEGER, $COLUMN_LAST_DEPOSIT_INCOME REAL, $COLUMN_LAST_CREDIT_INCOME REAL, $COLUMN_LAST_DEPOSIT_PAYMENT REAL)"
        db.execSQL(createBanksTableStatement)
        //endregion


        //region Таблица STOCKS_TABLE
        val createStocksTableStatement =
            "CREATE TABLE $STOCKS_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_LOTS_COUNT INTEGER, $COLUMN_SOLD_LOTS_COUNT INTEGER, $COLUMN_DECLARED_PRICE REAL, $COLUMN_LAST_PRICE REAL, $COLUMN_DIVIDEND_PERCENT REAL, $COLUMN_LAST_UPDATE_HOUR INTEGER, $COLUMN_LAST_UPDATE_MINUTE INTEGER, $COLUMN_LAST_UPDATE_MILLIS INTEGER, $COLUMN_MONEY_SPENT_ON_IT REAL, $COLUMN_LAST_DIFF REAL, $COLUMN_LAST_DIFF_PERCENT REAL)"
        db.execSQL(createStocksTableStatement)
        //endregion


        //region Таблица REAL_ESTATE_TABLE
        val createRealEstateTableStatement =
            "CREATE TABLE $REAL_ESTATE_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_CITY TEXT, $COLUMN_COUNTRY TEXT, $COLUMN_PRICE INTEGER, $COLUMN_IS_OWNED BOOL, $COLUMN_IMAGE_NAME TEXT)"
        db.execSQL(createRealEstateTableStatement)


        val inputStream: InputStream = context.resources.openRawResource(R.raw.real_estate)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        reader.readLines().forEach {

            val items = it.split(",")

            val cvRE = ContentValues()

            cvRE.put(COLUMN_CITY, items[0])
            cvRE.put(COLUMN_COUNTRY, items[1])
            cvRE.put(COLUMN_PRICE, items[2].toLong())
            cvRE.put(COLUMN_IS_OWNED, false)
            cvRE.put(COLUMN_IMAGE_NAME, items[3])

            db.insert(REAL_ESTATE_TABLE, null, cvRE)

        }
        //endregion


        //region Таблица PURCHASED_CARS_TABLE
        val createPurchasedCarsTableStatement =
            "CREATE TABLE $PURCHASED_CARS_TABLE ($COLUMN_TITLE TEXT, $COLUMN_PRICE REAL, $COLUMN_LEVEL INTEGER, $COLUMN_IMAGE_NAME TEXT)"
        db.execSQL(createPurchasedCarsTableStatement)

        val inputStreamCars: InputStream = context.resources.openRawResource(R.raw.purchased_cars)
        val readerCars = BufferedReader(InputStreamReader(inputStreamCars, Charset.forName("UTF-8")))
        readerCars.readLines().forEach {

            val cvPurCar = ContentValues()
            val items = it.split(",")

            cvPurCar.put(COLUMN_TITLE, items[0])
            cvPurCar.put(COLUMN_PRICE, items[1].toDouble())
            cvPurCar.put(COLUMN_LEVEL, items[2].toInt())
            cvPurCar.put(COLUMN_IMAGE_NAME, items[3])

            db.insert(PURCHASED_CARS_TABLE, null, cvPurCar)

        }
        //endregion

        //region Таблица OWNED_CARS_TABLE
        val createOwnedCarsTableStatement =
            "CREATE TABLE $OWNED_CARS_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_PRICE REAL, $COLUMN_LEVEL INTEGER, $COLUMN_IMAGE_NAME TEXT, $COLUMN_MOTOR_TYPE TEXT, $COLUMN_DESIGN_TYPE TEXT, $COLUMN_MONEY_SPENT_ON_IT REAL)"
        db.execSQL(createOwnedCarsTableStatement)
        //endregion

        //region Таблица PURCHASED_AIRCRAFTS
        val createPurchasedAircraftsTableStatement =
            "CREATE TABLE $PURCHASED_AIRCRAFTS_TABLE ($COLUMN_TITLE TEXT, $COLUMN_PRICE REAL, $COLUMN_IMAGE_NAME TEXT)"
        db.execSQL(createPurchasedAircraftsTableStatement)

        val inputStreamAirs: InputStream = context.resources.openRawResource(R.raw.purchased_aircrafts)
        val readerAirs = BufferedReader(InputStreamReader(inputStreamAirs, Charset.forName("UTF-8")))
        readerAirs.readLines().forEach {

            val cvPurAir = ContentValues()
            val items = it.split(",")

            cvPurAir.put(COLUMN_TITLE, items[0])
            cvPurAir.put(COLUMN_PRICE, items[1].toDouble())
            cvPurAir.put(COLUMN_IMAGE_NAME, items[2])

            db.insert(PURCHASED_AIRCRAFTS_TABLE, null, cvPurAir)

        }
        //endregion

        //region Таблица OWNED_CARS_TABLE
        val createOwnedAircraftsTableStatement =
            "CREATE TABLE $OWNED_AIRCRAFTS_TABLE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_PRICE REAL, $COLUMN_IMAGE_NAME TEXT, $COLUMN_TEAM TEXT, $COLUMN_DESIGN_TYPE TEXT, $COLUMN_MONEY_SPENT_ON_IT REAL)"
        db.execSQL(createOwnedAircraftsTableStatement)
        //endregion

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    //region RUD для PLAYER_TABLE
    fun setBalance(value: Double) {
        setDouble(PLAYER_TABLE, COLUMN_BALANCE, value)
    }

    fun getBalance(): Double {
        return getDouble(PLAYER_TABLE, COLUMN_BALANCE)
    }

    fun setTimeStamp(value: Long) {
        setLong(PLAYER_TABLE, COLUMN_SAVED_TIME_STAMP, value)
    }

    fun getTimeStamp(): Long {
        return getLong(PLAYER_TABLE, COLUMN_SAVED_TIME_STAMP)
    }

    fun setPrimaryCarId(value: Int) {
        setInt(PLAYER_TABLE, COLUMN_PRIMARY_CAR_ID, value)
    }

    fun getPrimaryCarId(): Int {
        return getInt(PLAYER_TABLE, COLUMN_PRIMARY_CAR_ID)
    }

    fun setPrimaryAircraftId(value: Int) {
        setInt(PLAYER_TABLE, COLUMN_PRIMARY_AIRCRAFT_ID, value)
    }

    fun getPrimaryAircraftId(): Int {
        return getInt(PLAYER_TABLE, COLUMN_PRIMARY_AIRCRAFT_ID)
    }
    //endregion


    //region Кликер геттеры и сеттеры
    fun setPayPerClick(value: Double) { setDouble(CLICKER_TABLE, COLUMN_PAY_PER_CLICK, value) }
    fun getPayPerClick(): Double { return getDouble(CLICKER_TABLE, COLUMN_PAY_PER_CLICK) }

    fun setClickerLevel(value: Int) { setInt(CLICKER_TABLE, COLUMN_LEVEL, value) }
    fun getClickerLevel(): Int { return getInt(CLICKER_TABLE, COLUMN_LEVEL) }

    fun setClickerToLevelUp(value: Double) { setDouble(CLICKER_TABLE, COLUMN_TO_LEVEL_UP, value) }
    fun getClickerToLevelUp(): Double { return getDouble(CLICKER_TABLE, COLUMN_TO_LEVEL_UP) }
    //endregion


    //region RUD для STATS_TABLE
    fun setEarnedInClicker(value: Double) { setDouble(STATS_TABLE, COLUMN_EARNED_IN_CLICKER, value) }
    fun getEarnedInClicker(): Double { return getDouble(STATS_TABLE, COLUMN_EARNED_IN_CLICKER) }

    fun setEarnedInBusiness(value: Double) { setDouble(STATS_TABLE, COLUMN_EARNED_IN_BUSINESS, value) }
    fun getEarnedInBusiness(): Double { return getDouble(STATS_TABLE, COLUMN_EARNED_IN_BUSINESS) }

    fun setEarnedOnRealEstate(value: Double) { setDouble(STATS_TABLE, COLUMN_EARNED_ON_REAL_ESTATE, value) }
    fun getEarnedOnRealEstate(): Double { return getDouble(STATS_TABLE, COLUMN_EARNED_ON_REAL_ESTATE) }

    fun setEarnedOnDividend(value: Double) { setDouble(STATS_TABLE, COLUMN_EARNED_ON_DIVIDEND, value) }
    fun getEarnedOnDividend(): Double { return getDouble(STATS_TABLE, COLUMN_EARNED_ON_DIVIDEND) }

    fun setEarnedOnStocksSelling(value: Double) { setDouble(STATS_TABLE, COLUMN_EARNED_ON_STOCKS_SELLING, value) }
    fun getEarnedOnStocksSelling(): Double { return getDouble(STATS_TABLE, COLUMN_EARNED_ON_STOCKS_SELLING) }
    //endregion


    //region RUD для SHOP_FACTORY_TABLE
    fun addNewShop(shop: BusinessShop): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, shop.title)
        cv.put(COLUMN_TYPE, SHOP_ID)
        cv.put(COLUMN_STAGE, 1)
        cv.put(COLUMN_LEVEL, shop.level)
        cv.put(COLUMN_UPDATE_COST, shop.updateCost.value!!)
        cv.put(COLUMN_UPDATE_TIME, shop.updateTime)
        cv.put(COLUMN_PROFIT, shop.profit.value)
        cv.put(COLUMN_CAPITALIZATION, shop.capitalization)
        cv.put(COLUMN_IS_UPDATING, false)
        cv.put(COLUMN_TIME_LEFT, (0).toLong())
        cv.put(COLUMN_ALL_TIME_EARNINGS, 0.0)

        db.insert(SHOPS_FACTORIES_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $SHOPS_FACTORIES_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()

        return result
    }

    fun getAllShops(): ArrayList<BusinessShop> {
        val arr = ArrayList<BusinessShop>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $SHOPS_FACTORIES_TABLE WHERE $COLUMN_TYPE = $SHOP_ID"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val stage = cursor.getInt(3)
                val level = cursor.getInt(4)
                val updateCost = cursor.getDouble(5)
                val updateTime = cursor.getDouble(6)
                val profit = cursor.getDouble(7)
                val capitalization = cursor.getDouble(8)
                val isUpdating = cursor.getInt(9) == 1

                val timeLeft = cursor.getLong(10)
                val allTimeEarnings = cursor.getDouble(11)

                val dbHelper = DataBaseHelper(context)
                val b = BusinessShop(dbHelper)
                b.initData(level)
                b.id = id
                b.title = title
                b.stage.postValue(stage)
                b.updateCost.postValue(updateCost)
                b.updateTime = updateTime
                b.profit.postValue(profit)
                b.capitalization = capitalization
                b.isUpdating.value = isUpdating
                b.timeLeft.value = timeLeft
                b.allTimeEarnings = allTimeEarnings

                arr.add(b)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return arr
    }

    fun addNewFactory(factory: BusinessFactory): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, factory.title)
        cv.put(COLUMN_TYPE, FACTORY_ID)
        cv.put(COLUMN_STAGE, 1)
        cv.put(COLUMN_LEVEL, factory.level)
        cv.put(COLUMN_UPDATE_COST, factory.updateCost.value!!)
        cv.put(COLUMN_UPDATE_TIME, factory.updateTime)
        cv.put(COLUMN_PROFIT, factory.profit.value)
        cv.put(COLUMN_CAPITALIZATION, factory.capitalization)
        cv.put(COLUMN_IS_UPDATING, false)
        cv.put(COLUMN_TIME_LEFT, (0).toLong())
        cv.put(COLUMN_ALL_TIME_EARNINGS, 0.0)

        db.insert(SHOPS_FACTORIES_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $SHOPS_FACTORIES_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()
        db.close()

        return result
    }

    fun getAllFactories(): ArrayList<BusinessFactory> {
        val arr = ArrayList<BusinessFactory>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $SHOPS_FACTORIES_TABLE WHERE $COLUMN_TYPE = $FACTORY_ID"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val stage = cursor.getInt(3)
                val level = cursor.getInt(4)
                val updateCost = cursor.getDouble(5)
                val updateTime = cursor.getDouble(6)
                val profit = cursor.getDouble(7)
                val capitalization = cursor.getDouble(8)
                val isUpdating = cursor.getInt(9) == 1

                val timeLeft = cursor.getLong(10)
                val allTimeEarnings = cursor.getDouble(11)

                val dbHelper = DataBaseHelper(context)
                val b = BusinessFactory(dbHelper)
                b.initData(level)
                b.id = id
                b.title = title
                b.stage.postValue(stage)
                b.updateCost.postValue(updateCost)
                b.updateTime = updateTime
                b.profit.postValue(profit)
                b.capitalization = capitalization
                b.isUpdating.value = isUpdating
                b.timeLeft.value = timeLeft
                b.allTimeEarnings = allTimeEarnings

                arr.add(b)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return arr
    }

    fun updateShopStage(id: Int, stage: Int, updateCost: Double, updateTime: Double, profit: Double, capitalization: Double) {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COLUMN_STAGE, stage)
        cv.put(COLUMN_UPDATE_COST, updateCost)
        cv.put(COLUMN_UPDATE_TIME, updateTime)
        cv.put(COLUMN_PROFIT, profit)
        cv.put(COLUMN_IS_UPDATING, false)
        cv.put(COLUMN_CAPITALIZATION, capitalization)
        db.update(SHOPS_FACTORIES_TABLE, cv, "$COLUMN_ID = $id", null)

    }

    fun setShopIsUpdating(id: Int, isUpdating: Int) {
        val db = this.writableDatabase
        val queryString = "UPDATE $SHOPS_FACTORIES_TABLE SET $COLUMN_IS_UPDATING=$isUpdating WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setShopTimeLeft(id: Int, timeLeft: Long) {
        val db = this.writableDatabase
        val queryString = "UPDATE $SHOPS_FACTORIES_TABLE SET $COLUMN_TIME_LEFT=$timeLeft WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setShopAllTimeEarnings(id: Int, value: Double) {
        val db = this.writableDatabase
        val queryString = "UPDATE $SHOPS_FACTORIES_TABLE SET $COLUMN_ALL_TIME_EARNINGS=$value WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }
    //endregion


    //region RUD для TAXI_TRANSPORTATION_TABLE
    fun addNewTaxiCompany(taxi: BusinessTaxiStation): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, taxi.title)
        cv.put(COLUMN_TYPE, TAXI_ID)
        cv.put(COLUMN_CAPITALIZATION, taxi.capitalization)
        cv.put(COLUMN_IS_UPDATING, false)
        cv.put(COLUMN_TIME_LEFT, (0).toLong())
        cv.put(COLUMN_ALL_TIME_EARNINGS, 0.0)
        cv.put(COLUMN_CAR_PARK_CAPACITY, 5)
        cv.put(COLUMN_PROFIT, 0.0)
        cv.put(COLUMN_EXPANSION_TYPE, 0)

        db.insert(TAXI_TRANSPORTATION_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $TAXI_TRANSPORTATION_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()
        db.close()

        return result
    }

    fun addNewTransportationCompany(transportation: BusinessTransportationCompany): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, transportation.title)
        cv.put(COLUMN_TYPE, TRANSPORTATION_ID)
        cv.put(COLUMN_CAPITALIZATION, transportation.capitalization)
        cv.put(COLUMN_IS_UPDATING, false)
        cv.put(COLUMN_TIME_LEFT, (0).toLong())
        cv.put(COLUMN_ALL_TIME_EARNINGS, 0.0)
        cv.put(COLUMN_CAR_PARK_CAPACITY, 5)
        cv.put(COLUMN_PROFIT, 0.0)
        cv.put(COLUMN_EXPANSION_TYPE, 0)

        db.insert(TAXI_TRANSPORTATION_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $TAXI_TRANSPORTATION_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()

        return result
    }

    fun getAllTaxiCompanies(): ArrayList<BusinessTaxiStation> {
        val arr = ArrayList<BusinessTaxiStation>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TAXI_TRANSPORTATION_TABLE WHERE $COLUMN_TYPE = $TAXI_ID"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val capitalization = cursor.getDouble(3)
                val isUpdating = cursor.getInt(4) == 1
                val timeLeft = cursor.getLong(5)
                val allTimeEarnings = cursor.getDouble(6)
                val carParkCapacity = cursor.getInt(7)
                val profit = cursor.getDouble(8)

                val dbHelper = DataBaseHelper(context)
                val b = BusinessTaxiStation(dbHelper)
                b.id = id
                b.title = title
                b.capitalization = capitalization
                b.isUpdating.value = isUpdating
                b.timeLeft.value = timeLeft
                b.allTimeEarnings = allTimeEarnings
                b.carParkCapacity.value = carParkCapacity
                b.profit.value = profit
                b.expansionType = cursor.getInt(9)

                arr.add(b)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return arr
    }

    fun getAllTransportationCompanies(): ArrayList<BusinessTransportationCompany> {
        val arr = ArrayList<BusinessTransportationCompany>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TAXI_TRANSPORTATION_TABLE WHERE $COLUMN_TYPE = $TRANSPORTATION_ID"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val capitalization = cursor.getDouble(3)
                val isUpdating = cursor.getInt(4) == 1
                val timeLeft = cursor.getLong(5)
                val allTimeEarnings = cursor.getDouble(6)
                val carParkCapacity = cursor.getInt(7)
                val profit = cursor.getDouble(8)

                val dbHelper = DataBaseHelper(context)
                val b = BusinessTransportationCompany(dbHelper)
                b.id = id
                b.title = title
                b.capitalization = capitalization
                b.isUpdating.value = isUpdating
                b.timeLeft.value = timeLeft
                b.allTimeEarnings = allTimeEarnings
                b.carParkCapacity.value = carParkCapacity
                b.profit.value = profit
                b.expansionType = cursor.getInt(9)

                arr.add(b)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return arr
    }

    fun getTaxiCompaniesId(): ArrayList<Int> {
        val arr = ArrayList<Int>()

        val db = this.readableDatabase
        val query = "SELECT $COLUMN_ID FROM $TAXI_TRANSPORTATION_TABLE WHERE $COLUMN_TYPE = $TAXI_ID"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                arr.add(cursor.getInt(0))

            } while (cursor.moveToNext())
        }

        cursor.close()
        return arr
    }
    fun getTransportationCompaniesId(): ArrayList<Int> {
        val arr = ArrayList<Int>()

        val db = this.readableDatabase
        val query = "SELECT $COLUMN_ID FROM $TAXI_TRANSPORTATION_TABLE WHERE $COLUMN_TYPE = $TRANSPORTATION_ID"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                arr.add(cursor.getInt(0))

            } while (cursor.moveToNext())
        }

        cursor.close()
        return arr
    }

    fun setTaxiCarParkCapacity(id: Int, value: Int) {
        val db = this.writableDatabase
        val queryString = "UPDATE $TAXI_TRANSPORTATION_TABLE SET $COLUMN_CAR_PARK_CAPACITY=$value WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }
    fun setTaxiCapitalization(id: Int, value: Double) {
        val db = this.writableDatabase
        val queryString = "UPDATE $TAXI_TRANSPORTATION_TABLE SET $COLUMN_CAPITALIZATION=$value WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }



    fun setTaxiIsUpdating(id: Int, isUpdating: Int) {
        val db = this.writableDatabase
        val queryString = "UPDATE $TAXI_TRANSPORTATION_TABLE SET $COLUMN_IS_UPDATING=$isUpdating WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setTaxiTimeLeft(id: Int, timeLeft: Long) {
        val db = this.writableDatabase
        val queryString = "UPDATE $TAXI_TRANSPORTATION_TABLE SET $COLUMN_TIME_LEFT=$timeLeft WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setTaxiAllTimeEarnings(id: Int, value: Double) {
        val db = this.writableDatabase
        val queryString = "UPDATE $TAXI_TRANSPORTATION_TABLE SET $COLUMN_ALL_TIME_EARNINGS=$value WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun getTaxiAllTimeEarnings(id: Int): Double {
        var result = -1.0

        val db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ALL_TIME_EARNINGS FROM $TAXI_TRANSPORTATION_TABLE WHERE $COLUMN_ID=$id"

        val cursor = db.rawQuery(queryString, null)

        if (cursor.moveToFirst())
            result = cursor.getDouble(0)

        cursor.close()
        return result
    }

    fun getTaxiCapitalization(id: Int): Double {
        var result = -1.0

        val db = this.readableDatabase
        val queryString = "SELECT $COLUMN_CAPITALIZATION FROM $TAXI_TRANSPORTATION_TABLE WHERE $COLUMN_ID=$id"

        val cursor = db.rawQuery(queryString, null)

        if (cursor.moveToFirst())
            result = cursor.getDouble(0)

        cursor.close()
        return result
    }

    fun setTaxiProfit(id: Int, value: Double) {
        val db = this.writableDatabase
        val queryString = "UPDATE $TAXI_TRANSPORTATION_TABLE SET $COLUMN_PROFIT=$value WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setExpansionType(id: Int, type: Int) {
        val db = this.writableDatabase
        val queryString = "UPDATE $TAXI_TRANSPORTATION_TABLE SET $COLUMN_EXPANSION_TYPE=$type WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }
    //endregion


    //region RUD для TAXI_TRANSPORTATION_CARS_TABLE
    fun addNewTaxiCar(car: TaxiCar, ownerId: Int): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, car.title)
        cv.put(COLUMN_PRICE, car.price)
        cv.put(COLUMN_IMAGE_ID, car.imageId)
        cv.put(COLUMN_CATEGORY, car.taxiCategory)
        cv.put(COLUMN_MILEAGE, car.mileage.value!!)
        cv.put(COLUMN_MAX_MILEAGE, car.maxMileage)
        cv.put(COLUMN_OWNER_ID, ownerId)

        db.insert(TAXI_TRANSPORTATION_CARS_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $TAXI_TRANSPORTATION_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()

        return result
    }

    fun addNewTransportationCar(car: TransportationCar, ownerId: Int): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, car.title)
        cv.put(COLUMN_PRICE, car.price)
        cv.put(COLUMN_IMAGE_ID, car.imageId)
        cv.put(COLUMN_CATEGORY, car.taxiCategory)
        cv.put(COLUMN_MILEAGE, car.mileage.value!!)
        cv.put(COLUMN_MAX_MILEAGE, car.maxMileage)
        cv.put(COLUMN_OWNER_ID, ownerId)

        db.insert(TAXI_TRANSPORTATION_CARS_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $TAXI_TRANSPORTATION_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()

        return result
    }

    fun getAllTaxiCars(ownerId: Int): ArrayList<TaxiCar> {
        val arr = ArrayList<TaxiCar>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TAXI_TRANSPORTATION_CARS_TABLE WHERE $COLUMN_OWNER_ID = $ownerId"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val price = cursor.getDouble(2)
                val imageId = cursor.getInt(3)
                val category = cursor.getInt(4)
                val mileage = cursor.getInt(5)
                val maxMileage = cursor.getInt(6)
                val owId = cursor.getInt(7)


                val c = TaxiCar(title, price, imageId, category, maxMileage, "imageName")
                c.mileage.value = mileage
                c.id = id
                c.ownerId = owId

                if (c.id >= 0 && c.title != "" && c.maxMileage >= 0 && c.mileage.value != null && c.mileage.value!! >= 0)
                    arr.add(c)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return arr
    }

    fun getAllTransportationCars(ownerId: Int): ArrayList<TransportationCar> {
        val arr = ArrayList<TransportationCar>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TAXI_TRANSPORTATION_CARS_TABLE WHERE $COLUMN_OWNER_ID = $ownerId"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val price = cursor.getDouble(2)
                val imageId = cursor.getInt(3)
                val category = cursor.getInt(4)
                val mileage = cursor.getInt(5)
                val maxMileage = cursor.getInt(6)
                val owId = cursor.getInt(7)


                val c = TransportationCar(title, price, imageId, category, maxMileage, "imageName")
                c.mileage.value = mileage
                c.id = id
                c.ownerId = owId

                if (c.id >= 0 && c.title != "" && c.maxMileage >= 0 && c.mileage.value != null && c.mileage.value!! >= 0)
                    arr.add(c)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return arr
    }

    fun deleteTaxiTransportationCar(id: Int) {
        val db = this.writableDatabase
        val queryString = "DELETE FROM $TAXI_TRANSPORTATION_CARS_TABLE WHERE $COLUMN_ID = $id"

        db.execSQL(queryString)
    }

    fun setCarMileage(id: Int, value: Int) {
        var db = this.readableDatabase
        val query1 = "SELECT $COLUMN_MILEAGE FROM $TAXI_TRANSPORTATION_CARS_TABLE WHERE $COLUMN_ID=$id"

        val cursor = db.rawQuery(query1, null)

        var currMileage = 0
        if (cursor.moveToFirst())
            currMileage = cursor.getInt(0)

        cursor.close()

        if (value >= currMileage) {
            db = this.writableDatabase
            val queryString = "UPDATE $TAXI_TRANSPORTATION_CARS_TABLE SET $COLUMN_MILEAGE=$value WHERE $COLUMN_ID=$id"

            db.execSQL(queryString)
        }
    }


    //endregion


    //region RUD для BUILDING_COMPANIES_TABLE
    fun addNewBuildingCompany(b: BusinessBuildingCompany): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, b.title)
        cv.put(COLUMN_PROFIT, 0.0)
        cv.put(COLUMN_CAPITALIZATION, b.capitalization)
        cv.put(COLUMN_ALL_TIME_EARNINGS, b.allTimeEarnings)
        cv.put(COLUMN_WORKERS, b.woodNumber.value!!)
        cv.put(COLUMN_METAL, b.metalNumber.value!!)
        cv.put(COLUMN_WOOD, b.woodNumber.value!!)
        cv.put(COLUMN_CONCRETE, b.concreteNumber.value!!)

        db.insert(BUILDING_COMPANIES_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $BUILDING_COMPANIES_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()

        return result
    }

    fun getAllBuildingCompanies(): ArrayList<BusinessBuildingCompany> {
        val arr = ArrayList<BusinessBuildingCompany>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $BUILDING_COMPANIES_TABLE"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val profit = cursor.getDouble(2)
                val capitalization = cursor.getDouble(3)
                val allTimeEarnings = cursor.getDouble(4)
                val workers = cursor.getInt(5)
                val metal = cursor.getInt(6)
                val wood = cursor.getInt(7)
                val concrete = cursor.getInt(8)

                val dbHelper = DataBaseHelper(context)
                val b = BusinessBuildingCompany(dbHelper)
                b.id = id
                b.title = title
                b.profit.postValue(profit)
                b.capitalization = capitalization
                b.allTimeEarnings = allTimeEarnings
                b.buildersNumber.value = workers
                b.metalNumber.value = metal
                b.woodNumber.value = wood
                b.concreteNumber.value = concrete

                arr.add(b)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return arr
    }

    fun setBuildingCompanyCapitalization(id: Int, value: Double) {
        val db = this.writableDatabase
        val queryString = "UPDATE $BUILDING_COMPANIES_TABLE SET $COLUMN_CAPITALIZATION=$value WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun getBuildingCompanyCapitalization(id: Int): Double {
        var result = -1.0

        val db = this.readableDatabase
        val queryString = "SELECT $COLUMN_CAPITALIZATION FROM $BUILDING_COMPANIES_TABLE WHERE $COLUMN_ID=$id"

        val cursor = db.rawQuery(queryString, null)

        if (cursor.moveToFirst())
            result = cursor.getDouble(0)

        cursor.close()
        return result
    }

    fun setBuildingCompanyAllTimeEarnings(id: Int, value: Double) {
        val db = this.writableDatabase
        val queryString = "UPDATE $BUILDING_COMPANIES_TABLE SET $COLUMN_ALL_TIME_EARNINGS=$value WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun getBuildingCompanyAllTimeEarnings(id: Int): Double {
        var result = -1.0

        val db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ALL_TIME_EARNINGS FROM $BUILDING_COMPANIES_TABLE WHERE $COLUMN_ID=$id"

        val cursor = db.rawQuery(queryString, null)

        if (cursor.moveToFirst())
            result = cursor.getDouble(0)

        cursor.close()
        return result
    }

    fun setBuildingCompanyResources(id: Int, workers: Int, metal: Int, wood: Int, concrete: Int) {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COLUMN_WORKERS, workers)
        cv.put(COLUMN_METAL, metal)
        cv.put(COLUMN_WOOD, wood)
        cv.put(COLUMN_CONCRETE, concrete)
        db.update(BUILDING_COMPANIES_TABLE, cv, "$COLUMN_ID = $id", null)
    }

    //endregion


    //region RUD для BUILDINGS_TABLE
    fun addNewBuilding(b: Building, ownerId: Int): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TYPE, b.buildingType)
        cv.put(COLUMN_IS_BOOSTED, false)
        cv.put(COLUMN_TIME_LEFT, b.hoursToBuild * 60 * 60 * 1000)
        cv.put(COLUMN_OWNER_ID, ownerId)
        cv.put(COLUMN_IS_BUILT, false)

        db.insert(BUILDINGS_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $BUILDINGS_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()

        return result
    }

    fun deleteBuilding(id: Int) {
        val db = this.writableDatabase
        val queryString = "DELETE FROM $BUILDINGS_TABLE WHERE $COLUMN_ID = $id"

        db.execSQL(queryString)
    }

    fun getAllBuildings(ownerId: Int): ArrayList<Building> {
        val arr = ArrayList<Building>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $BUILDINGS_TABLE WHERE $COLUMN_OWNER_ID = $ownerId"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val type = cursor.getInt(1)
                val isBoosted = cursor.getInt(2) == 1
                val timeLeft = cursor.getLong(3)
                val owId = cursor.getInt(4)
                val isBuilt = cursor.getInt(5) == 1


                val b = Building(type)
                b.id = id
                b.isBoosted.value = isBoosted
                b.timeLeft.value = timeLeft
                b.ownerId = owId
                b.isBuilt.value = isBuilt

                arr.add(b)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return arr
    }

    fun setBuildingIsBoosted(id: Int, isBoosted: Int) {
        val db = this.writableDatabase
        val queryString = "UPDATE $BUILDINGS_TABLE SET $COLUMN_IS_BOOSTED=$isBoosted WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setBuildingIsBuilt(id: Int, isBuilt: Int) {
        val db = this.writableDatabase
        val queryString = "UPDATE $BUILDINGS_TABLE SET $COLUMN_IS_BUILT=$isBuilt WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setBuildingTimeLeft(id: Int, timeLeft: Long) {
        val db = this.writableDatabase
        val queryString = "UPDATE $BUILDINGS_TABLE SET $COLUMN_TIME_LEFT=$timeLeft WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }
    //endregion


    //region RUD для BANKS_TABLE
    fun addNewBank(bank: BusinessBank): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, bank.title)
        cv.put(COLUMN_STAGE, 1)
        cv.put(COLUMN_UPDATE_COST, bank.updateCost.value!!)
        cv.put(COLUMN_UPDATE_TIME, bank.updateTime)
        cv.put(COLUMN_PROFIT, bank.profit.value!!)
        cv.put(COLUMN_CAPITALIZATION, bank.capitalization)
        cv.put(COLUMN_IS_UPDATING, false)
        cv.put(COLUMN_TIME_LEFT, (0).toLong())
        cv.put(COLUMN_ALL_TIME_EARNINGS, 0.0)
        cv.put(COLUMN_CREDIT_PERCENT, bank.creditPercent.value!!)
        cv.put(COLUMN_DEPOSIT_PERCENT, bank.depositPercent.value!!)
        cv.put(COLUMN_TOTAL_MONEY, bank.totalMoney.value!!)
        cv.put(COLUMN_MONEY_IN_CREDITS, bank.moneyInCredits.value!!)
        cv.put(COLUMN_VAULT_CAPACITY, bank.vaultCapacity.value!!)
        cv.put(COLUMN_LAST_DEPOSIT_INCOME, bank.lastDepositIncome.value!!)
        cv.put(COLUMN_LAST_CREDIT_INCOME, bank.lastCreditIncome.value!!)
        cv.put(COLUMN_LAST_DEPOSIT_PAYMENT, bank.lastDepositPayment.value!!)

        db.insert(BANKS_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $BANKS_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()

        return result
    }

    fun getAllBanks(): ArrayList<BusinessBank> {
        val arr = ArrayList<BusinessBank>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $BANKS_TABLE"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val stage = cursor.getInt(2)
                val updateCost = cursor.getDouble(3)
                val updateTime = cursor.getDouble(4)
                val profit = cursor.getDouble(5)
                val capitalization = cursor.getDouble(6)
                val isUpdating = cursor.getInt(7) == 1
                val timeLeft = cursor.getLong(8)
                val allTimeEarnings = cursor.getDouble(9)
                val creditPercent = cursor.getDouble(10)
                val depositPercent = cursor.getDouble(11)
                val totalMoney = cursor.getDouble(12)
                val moneyInCredits = cursor.getDouble(13)
                val vaultCapacity = cursor.getLong(14)
                val lastDepositIncome = cursor.getDouble(15)
                val lastCreditIncome = cursor.getDouble(16)
                val lastDepositPayment = cursor.getDouble(17)

                val dbHelper = DataBaseHelper(context)
                val b = BusinessBank(dbHelper)
                b.id = id
                b.title = title
                b.stage.value = stage
                b.updateCost.value = updateCost
                b.updateTime = updateTime
                b.profit.value = profit
                b.capitalization = capitalization
                b.isUpdating.value = isUpdating
                b.timeLeft.value = timeLeft
                b.allTimeEarnings = allTimeEarnings
                b.creditPercent.value = creditPercent
                b.depositPercent.value = depositPercent
                b.totalMoney.value = totalMoney
                b.moneyInCredits.value = moneyInCredits
                b.vaultCapacity.value = vaultCapacity
                b.lastDepositIncome.value = lastDepositIncome
                b.lastCreditIncome.value = lastCreditIncome
                b.lastDepositPayment.value = lastDepositPayment

                arr.add(b)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return arr
    }

    fun updateBankStage(id: Int, stage: Int, updateCost: Double, updateTime: Double, capitalization: Double, vaultCapacity: Long) {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COLUMN_STAGE, stage)
        cv.put(COLUMN_UPDATE_COST, updateCost)
        cv.put(COLUMN_UPDATE_TIME, updateTime)
        cv.put(COLUMN_IS_UPDATING, false)
        cv.put(COLUMN_CAPITALIZATION, capitalization)
        cv.put(COLUMN_VAULT_CAPACITY, vaultCapacity)
        db.update(BANKS_TABLE, cv, "$COLUMN_ID = $id", null)

    }

    fun setBankIsUpdating(id: Int, isUpdating: Int) {
        val db = this.writableDatabase
        val queryString = "UPDATE $BANKS_TABLE SET $COLUMN_IS_UPDATING=$isUpdating WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setBankTimeLeft(id: Int, timeLeft: Long) {
        val db = this.writableDatabase
        val queryString = "UPDATE $BANKS_TABLE SET $COLUMN_TIME_LEFT=$timeLeft WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setBankProfit(id: Int, profit: Double) {
        val db = this.writableDatabase
        val queryString = "UPDATE $BANKS_TABLE SET $COLUMN_PROFIT=$profit WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setBankAllTimeEarnings(id: Int, value: Double) {
        val db = this.writableDatabase
        val queryString = "UPDATE $BANKS_TABLE SET $COLUMN_ALL_TIME_EARNINGS=$value WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }

    fun setBankPercents(id: Int, creditPercent: Double, depositPercent: Double) {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COLUMN_CREDIT_PERCENT, creditPercent)
        cv.put(COLUMN_DEPOSIT_PERCENT, depositPercent)
        db.update(BANKS_TABLE, cv, "$COLUMN_ID = $id", null)
    }

    fun setBankAfterPayDay(id: Int, profit: Double, totalMoney: Double, moneyInCredits: Double,
                           lastDepositIncome: Double, lastCreditIncome: Double, lastDepositPayment: Double) {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COLUMN_PROFIT, profit)
        cv.put(COLUMN_TOTAL_MONEY, totalMoney)
        cv.put(COLUMN_MONEY_IN_CREDITS, moneyInCredits)
        cv.put(COLUMN_LAST_DEPOSIT_INCOME, lastDepositIncome)
        cv.put(COLUMN_LAST_CREDIT_INCOME, lastCreditIncome)
        cv.put(COLUMN_LAST_DEPOSIT_PAYMENT, lastDepositPayment)
        db.update(BANKS_TABLE, cv, "$COLUMN_ID = $id", null)
    }

    //endregion

    //region RUD для всех бизнесов
    fun deleteBusiness(id: Int, businessNum: Int) {
        var tableName = ""
        if (businessNum == BusinessConstants.SHOP_NUMBER || businessNum == BusinessConstants.FACTORY_NUMBER)
            tableName = SHOPS_FACTORIES_TABLE
        else if (businessNum == BusinessConstants.TAXI_NUMBER || businessNum == BusinessConstants.TRANSPORTATION_NUMBER)
            tableName = TAXI_TRANSPORTATION_TABLE
        else if (businessNum == BusinessConstants.BUILDING_NUMBER)
            tableName = BUILDING_COMPANIES_TABLE
        else if (businessNum == BusinessConstants.BANK_NUMBER)
            tableName = BANKS_TABLE

        val db = this.writableDatabase
        val queryString = "DELETE FROM $tableName WHERE $COLUMN_ID = $id"

        db.execSQL(queryString)
    }
    //endregion


    //region RUD для STOCKS_TABLE
    fun isStocksTableEmpty(): Boolean {
        var empty = true
        val db = this.readableDatabase
        val cur = db.rawQuery("SELECT COUNT(*) FROM $STOCKS_TABLE", null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0)
        }
        cur.close()

        return empty
    }

    fun addNewStock(stock: Stock): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, stock.title)
        cv.put(COLUMN_LOTS_COUNT, stock.lotsCount)
        cv.put(COLUMN_SOLD_LOTS_COUNT, stock.soldLotsCount)
        cv.put(COLUMN_DECLARED_PRICE, stock.declaredPrice)
        cv.put(COLUMN_LAST_PRICE, stock.declaredPrice)
        cv.put(COLUMN_DIVIDEND_PERCENT, stock.dividendPercent)
        cv.put(COLUMN_LAST_UPDATE_HOUR, stock.lastUpdateHour)
        cv.put(COLUMN_LAST_UPDATE_MINUTE, stock.lastUpdateMinute)
        cv.put(COLUMN_LAST_UPDATE_MILLIS, stock.lastUpdateMillis)
        cv.put(COLUMN_MONEY_SPENT_ON_IT, stock.moneySpentOnIt)
        cv.put(COLUMN_LAST_DIFF, stock.lastDiff)
        cv.put(COLUMN_LAST_DIFF_PERCENT, stock.lastDiffPercent)

        db.insert(STOCKS_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $STOCKS_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()
        db.close()

        return result
    }

    fun getAllStocks(): ArrayList<Stock> {
        val arr = ArrayList<Stock>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $STOCKS_TABLE"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val lotsCount = cursor.getLong(2)
                val soldLotsCount = cursor.getLong(3)
                val declaredPrice = cursor.getDouble(4)
                val lastPrice = cursor.getDouble(5)
                val dividendPercent = cursor.getDouble(6)
                val lastUpdateHour = cursor.getInt(7)
                val lastUpdateMinute = cursor.getInt(8)
                val lastUpdateMillis = cursor.getLong(9)
                val moneySpentOnIt = cursor.getDouble(10)
                val lastDiff = cursor.getDouble(11)
                val lastDiffPercent = cursor.getDouble(12)

                val dbHelper = DataBaseHelper(context)
                val s = Stock(title, lotsCount, declaredPrice, dividendPercent)
                s.id = id
                s.soldLotsCount = soldLotsCount
                s.lastPrice = lastPrice
                s.lastUpdateHour = lastUpdateHour
                s.lastUpdateMinute = lastUpdateMinute
                s.lastUpdateMillis = lastUpdateMillis
                s.moneySpentOnIt = moneySpentOnIt
                s.lastDiff = lastDiff
                s.lastDiffPercent = lastDiffPercent

                arr.add(s)

            } while (cursor.moveToNext())
        }

        cursor.close()
        return arr
    }

    fun updateStockAfterDeal(id: Int, soldLotsCount: Long, moneySpentOnIt: Double) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_SOLD_LOTS_COUNT, soldLotsCount)
        cv.put(COLUMN_MONEY_SPENT_ON_IT, moneySpentOnIt)

        db.update(STOCKS_TABLE, cv, "$COLUMN_ID = $id", null)
    }

    fun updateStockAfterChange(id: Int, price: Double, lastDiff: Double, lastDiffPercent: Double, lastMillis: Long) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_LAST_PRICE, price)
        cv.put(COLUMN_LAST_DIFF, lastDiff)
        cv.put(COLUMN_LAST_DIFF_PERCENT, lastDiffPercent)
        cv.put(COLUMN_LAST_UPDATE_MILLIS, lastMillis)


        db.update(STOCKS_TABLE, cv, "$COLUMN_ID = $id", null)
    }


    //endregion


    //region RUD для REAL_ESTATE_TABLE
    fun getOffersRealEstate(): ArrayList<RealEstate> {
        val arr = ArrayList<RealEstate>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $REAL_ESTATE_TABLE WHERE $COLUMN_IS_OWNED = 0"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val city = cursor.getString(1)
                val country = cursor.getString(2)
                val price = cursor.getLong(3)
                val imageName = cursor.getString(5)

                val imageId: Int = context.resources.getIdentifier(imageName, "drawable", context.packageName)

                val re = RealEstate(city, country, price, imageId, false)
                re.id = id
                arr.add(re)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return arr
    }

    fun getOwnedRealEstate(): ArrayList<RealEstate> {
        val arr = ArrayList<RealEstate>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $REAL_ESTATE_TABLE WHERE $COLUMN_IS_OWNED = 1"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val city = cursor.getString(1)
                val country = cursor.getString(2)
                val price = cursor.getLong(3)
                val imageName = cursor.getString(5)

                val imageId: Int = context.resources.getIdentifier(imageName, "drawable", context.packageName)

                val re = RealEstate(city, country, price, imageId, true)
                re.id = id
                arr.add(re)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return arr
    }

    fun setRealEstateIsOwned(id: Int, isOwned: Int) {
        val db = this.writableDatabase
        val queryString = "UPDATE $REAL_ESTATE_TABLE SET $COLUMN_IS_OWNED=$isOwned WHERE $COLUMN_ID=$id"

        db.execSQL(queryString)
    }
    //endregion


    //region RUD для cars
    fun getPurchasedCars(): ArrayList<PurchasedCar> {
        val arr = ArrayList<PurchasedCar>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $PURCHASED_CARS_TABLE"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(0)
                val price = cursor.getDouble(1)
                val level = cursor.getInt(2)
                val imageName = cursor.getString(3)

                val imageId: Int = context.resources.getIdentifier(imageName, "drawable", context.packageName)

                val c = PurchasedCar(title, price, level, imageId)
                c.imageName = imageName
                arr.add(c)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return arr
    }

    fun addOwnedCar(car: PurchasedCar): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, car.title)
        cv.put(COLUMN_PRICE, car.price)
        cv.put(COLUMN_LEVEL, car.level)
        cv.put(COLUMN_IMAGE_NAME, car.imageName)
        cv.put(COLUMN_MOTOR_TYPE, car.motorType)
        cv.put(COLUMN_DESIGN_TYPE, car.designType)
        cv.put(COLUMN_MONEY_SPENT_ON_IT, car.moneySpentOnIt)

        db.insert(OWNED_CARS_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $OWNED_CARS_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()
        db.close()

        return result
    }

    fun getOwnedCars(): ArrayList<PurchasedCar> {
        val arr = ArrayList<PurchasedCar>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $OWNED_CARS_TABLE"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val price = cursor.getDouble(2)
                val level = cursor.getInt(3)
                val imageName = cursor.getString(4)
                val mType = cursor.getString(5)
                val dType = cursor.getString(6)
                val moneySpent = cursor.getDouble(7)

                val imageId: Int = context.resources.getIdentifier(imageName, "drawable", context.packageName)

                val c = PurchasedCar(title, price, level, imageId)
                c.id = id
                c.motorType = mType
                c.designType = dType
                c.moneySpentOnIt = moneySpent
                arr.add(c)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return arr
    }

    fun deleteOwnedCar(id: Int) {
        val db = this.writableDatabase
        val queryString = "DELETE FROM $OWNED_CARS_TABLE WHERE $COLUMN_ID = $id"

        db.execSQL(queryString)
    }
    //endregion

    //region RUD для aircrafts
    fun getPurchasedAircrafts(): ArrayList<PurchasedAircraft> {
        val arr = ArrayList<PurchasedAircraft>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $PURCHASED_AIRCRAFTS_TABLE"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(0)
                val price = cursor.getDouble(1)
                val imageName = cursor.getString(2)

                val imageId: Int = context.resources.getIdentifier(imageName, "drawable", context.packageName)

                val c = PurchasedAircraft(title, price, imageId)
                c.imageName = imageName
                arr.add(c)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return arr
    }

    fun addOwnedAircraft(aircraft: PurchasedAircraft): Int {
        var db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TITLE, aircraft.title)
        cv.put(COLUMN_PRICE, aircraft.price)
        cv.put(COLUMN_IMAGE_NAME, aircraft.imageName)
        cv.put(COLUMN_TEAM, aircraft.team)
        cv.put(COLUMN_DESIGN_TYPE, aircraft.designType)
        cv.put(COLUMN_MONEY_SPENT_ON_IT, aircraft.moneySpentOnIt)

        db.insert(OWNED_AIRCRAFTS_TABLE, null, cv)

        db = this.readableDatabase
        val queryString = "SELECT $COLUMN_ID FROM $OWNED_AIRCRAFTS_TABLE"

        val cursor = db.rawQuery(queryString, null)

        var result = -1
        if (cursor.moveToLast())
            result = cursor.getInt(0)

        cursor.close()
        db.close()

        return result
    }

    fun getOwnedAircrafts(): ArrayList<PurchasedAircraft> {
        val arr = ArrayList<PurchasedAircraft>()

        val db = this.readableDatabase
        val query = "SELECT * FROM $OWNED_AIRCRAFTS_TABLE"

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val title = cursor.getString(1)
                val price = cursor.getDouble(2)
                val imageName = cursor.getString(3)
                val mType = cursor.getString(4)
                val dType = cursor.getString(5)
                val moneySpent = cursor.getDouble(6)

                val imageId: Int = context.resources.getIdentifier(imageName, "drawable", context.packageName)

                val c = PurchasedAircraft(title, price, imageId)
                c.id = id
                c.team = mType
                c.designType = dType
                c.moneySpentOnIt = moneySpent
                arr.add(c)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return arr
    }

    fun deleteOwnedAircraft(id: Int) {
        val db = this.writableDatabase
        val queryString = "DELETE FROM $OWNED_AIRCRAFTS_TABLE WHERE $COLUMN_ID = $id"

        db.execSQL(queryString)
    }
    //endregion

    //region Utils
    private fun setDouble(table: String, col: String, value: Double) {
        val db = this.writableDatabase
        val queryString = "UPDATE $table SET $col=$value"

        db.execSQL(queryString)
    }

    private fun getDouble(table: String, col: String): Double {
        var result = -1.0

        val db = this.readableDatabase
        val queryString = "SELECT $col FROM $table"

        val cursor = db.rawQuery(queryString, null)

        if (cursor.moveToFirst())
            result = cursor.getDouble(0)

        cursor.close()
        return result
    }

    private fun setInt(table: String, col: String, value: Int) {
        val db = this.writableDatabase
        val queryString = "UPDATE $table SET $col=$value"

        db.execSQL(queryString)
    }

    private fun getInt(table: String, col: String): Int {
        var result = -1

        val db = this.readableDatabase
        val queryString = "SELECT $col FROM $table"

        val cursor = db.rawQuery(queryString, null)

        if (cursor.moveToFirst())
            result = cursor.getInt(0)

        cursor.close()
        return result
    }

    private fun setLong(table: String, col: String, value: Long) {
        val db = this.writableDatabase
        val queryString = "UPDATE $table SET $col=$value"

        db.execSQL(queryString)
    }

    private fun getLong(table: String, col: String): Long {
        var result: Long = -1

        val db = this.readableDatabase
        val queryString = "SELECT $col FROM $table"

        val cursor = db.rawQuery(queryString, null)

        if (cursor.moveToFirst())
            result = cursor.getLong(0)

        cursor.close()
        return result
    }
    //endregion

}