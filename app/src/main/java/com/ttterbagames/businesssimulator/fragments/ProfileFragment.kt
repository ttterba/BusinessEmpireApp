package com.ttterbagames.businesssimulator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.doOnLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ttterbagames.businesssimulator.*
import com.ttterbagames.businesssimulator.databinding.FragmentProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {

    val playerModel: PlayerModel by activityViewModels()

    lateinit var binding: FragmentProfileBinding

    var sumBusiness = 0.0
    var sumStocks = 0.0
    var sumRealEstate = 0.0
    var sumTransport = 0.0

    var scaleParams = MutableLiveData<ArrayList<Double>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.executeAsyncTask(onPreExecute = {

        }, doInBackground = {
            calcFortune()
            getInfographicParams()
        }, onPostExecute = {
            updateFortuneUI()
            scaleParams.postValue(it)
        })

        scaleParams.observe(activity as LifecycleOwner, {
            updateInfographicsUI(it)
        })

        initStats()

        //initFortune()

        setButtonListeners()

        setPrimaryVehicleImage()

        //initInfographics()
    }

    private fun setPrimaryVehicleImage() {
        Glide.with(binding.imSelectedCar.context).load(playerModel.primaryCarImageId).into(binding.imSelectedCar)
        Glide.with(binding.imSelectedAircraft.context).load(playerModel.primaryAircraftImageId).into(binding.imSelectedAircraft)
    }

    private fun calcFortune() {
        sumBusiness = 0.0
        if (playerModel.ownedBusinesses.value != null)
            for (b in playerModel.ownedBusinesses.value!!)
                sumBusiness += b.capitalization

        sumStocks = 0.0
        if (playerModel.ownedStockList.value != null)
            for (s in playerModel.ownedStockList.value!!)
                sumStocks += s.soldLotsCount * s.currentPrice

        sumRealEstate = 0.0
        if (playerModel.ownedRealEstate.value != null)
            for (r in playerModel.ownedRealEstate.value!!)
                sumRealEstate += r.Price

        sumTransport = 0.0
        for (c in playerModel.ownedCarList)
            sumTransport += c.moneySpentOnIt
        for (a in playerModel.ownedAircraftList)
            sumTransport += a.moneySpentOnIt
    }

    private fun updateFortuneUI() {
        binding.apply {
            tvBalance.text = Utils.convertMoneyToText(playerModel.balance.value!!)
            tvBusiness.text = Utils.convertMoneyToText(sumBusiness)
            tvStocks.text = Utils.convertMoneyToText(sumStocks)
            tvRealEstate.text = Utils.convertMoneyToText(sumRealEstate)
            tvTransport.text = Utils.convertMoneyToText(sumTransport)

            tvFortune.text = Utils.convertMoneyToText(playerModel.balance.value!! + sumBusiness + sumStocks +
                    sumRealEstate + sumTransport)
        }
    }

    private fun getInfographicParams() : ArrayList<Double> {
        val arr = ArrayList<Double>()
        val sum = playerModel.balance.value!! + sumBusiness + sumStocks +
                sumRealEstate + sumTransport


        arr.add( playerModel.balance.value!! / sum )
        arr.add( sumBusiness / sum)
        arr.add( sumStocks / sum)
        arr.add( sumRealEstate / sum )
        arr.add( sumTransport / sum  )

        return arr
    }

    private fun updateInfographicsUI(arr: ArrayList<Double>) {

        binding.scaleWrapper.doOnLayout {
            val height = binding.scaleWrapper.height
            val width = binding.scaleWrapper.width
            binding.apply {
                scaleBalance.layoutParams = LinearLayout.LayoutParams((arr[0] * width).toInt(), height)
                scaleBusiness.layoutParams = LinearLayout.LayoutParams((arr[1] * width).toInt(), height)
                scaleStocks.layoutParams = LinearLayout.LayoutParams((arr[2] * width).toInt(), height)
                scaleRealEstate.layoutParams = LinearLayout.LayoutParams((arr[3] * width).toInt(), height)
                scaleTransport.layoutParams = LinearLayout.LayoutParams((arr[4] * width).toInt(), height)
            }
        }
    }

    private fun initFortune() {
        sumBusiness = 0.0
        if (playerModel.ownedBusinesses.value != null)
            for (b in playerModel.ownedBusinesses.value!!)
                sumBusiness += b.capitalization

        sumStocks = 0.0
        if (playerModel.ownedStockList.value != null)
            for (s in playerModel.ownedStockList.value!!)
                sumStocks += s.soldLotsCount * s.currentPrice

        sumRealEstate = 0.0
        if (playerModel.ownedRealEstate.value != null)
            for (r in playerModel.ownedRealEstate.value!!)
                sumRealEstate += r.Price

        sumTransport = 0.0
        for (c in playerModel.ownedCarList)
            sumTransport += c.moneySpentOnIt
        for (a in playerModel.ownedAircraftList)
            sumTransport += a.moneySpentOnIt

        binding.apply {
            tvBalance.text = Utils.convertMoneyToText(playerModel.balance.value!!)
            tvBusiness.text = Utils.convertMoneyToText(sumBusiness)
            tvStocks.text = Utils.convertMoneyToText(sumStocks)
            tvRealEstate.text = Utils.convertMoneyToText(sumRealEstate)
            tvTransport.text = Utils.convertMoneyToText(sumTransport)

            tvFortune.text = Utils.convertMoneyToText(playerModel.balance.value!! + sumBusiness + sumStocks +
                    sumRealEstate + sumTransport)
        }
    }

    private fun initInfographics() {
        val sum = playerModel.balance.value!! + sumBusiness + sumStocks +
                sumRealEstate + sumTransport
        binding.scaleWrapper.doOnLayout {
            val width = binding.scaleWrapper.width
            val height = binding.scaleWrapper.height
            binding.apply {
                scaleBalance.layoutParams = LinearLayout.LayoutParams((playerModel.balance.value!! / sum * width).toInt(), height)
                scaleBusiness.layoutParams = LinearLayout.LayoutParams((sumBusiness / sum * width).toInt(), height)
                scaleStocks.layoutParams = LinearLayout.LayoutParams((sumStocks / sum * width).toInt(), height)
                scaleRealEstate.layoutParams = LinearLayout.LayoutParams((sumRealEstate / sum * width).toInt(), height)
                scaleTransport.layoutParams = LinearLayout.LayoutParams((sumTransport / sum * width).toInt(), height)
            }
        }

    }

    private fun initStats() {
        binding.apply {
            if (playerModel.ownedBusinesses.value == null)
                tvStatsBusinessCount.text = Strings.get(R.string.integer_value, 0)
            else
                tvStatsBusinessCount.text = Strings.get(R.string.integer_value, playerModel.ownedBusinesses.value!!.size)

            if (playerModel.ownedRealEstate.value == null)
                tvStatsRealEstateCount.text = Strings.get(R.string.integer_value, 0)
            else
                tvStatsRealEstateCount.text = Strings.get(R.string.integer_value, playerModel.ownedRealEstate.value!!.size)

            var num = 0
            if (playerModel.ownedStockList.value != null)
                for (c in playerModel.ownedStockList.value!!)
                    if (c.soldLotsCount == c.lotsCount)
                         num++
            tvStatsCompaniesBought.text = Strings.get(R.string.integer_value, num)

            tvStatsCarsCount.text = Strings.get(R.string.integer_value, playerModel.ownedCarList.size)
            tvStatsAircraftCount.text = Strings.get(R.string.integer_value, playerModel.ownedAircraftList.size)

            tvStatsEarnedClicker.text = Utils.convertMoneyToText(playerModel.earnedInClicker)
            tvStatsEarnedBusiness.text = Utils.convertMoneyToText(playerModel.earnedInBusiness)
            tvStatsEarnedRealEstate.text = Utils.convertMoneyToText(playerModel.earnedOnRealEstate)
            tvStatsEarnedDividend.text = Utils.convertMoneyToText(playerModel.earnedOnDividend)
            tvStatsEarnedSellingStocks.text = Utils.convertMoneyToText(playerModel.earnedOnStocksSelling)
        }
    }

    private fun setButtonListeners() {
        binding.tvInformation.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, InformationFragment())
                addToBackStack(null)
            }
        }

        binding.btnGarage.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, GarageFragment())
                addToBackStack(null)
            }
        }

        binding.btnCarShowroom.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, CarShowroomFragment())
                addToBackStack(null)
            }
        }

        binding.btnHangar.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, HangarFragment())
                addToBackStack(null)
            }
        }

        binding.btnAircraftShop.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, AircraftShopFragment())
                addToBackStack(null)
            }
        }
    }

    fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit,
    ) = launch {
        onPreExecute() // runs in Main Thread
        val result = withContext(Dispatchers.IO) {
            doInBackground() // runs in background thread without blocking the Main Thread
        }
        onPostExecute(result) // runs in Main Thread
    }

}