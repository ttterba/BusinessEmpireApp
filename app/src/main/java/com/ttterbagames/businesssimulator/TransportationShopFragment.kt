package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.databinding.FragmentTransportationShopBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.card.MaterialCardView
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.random.Random


class TransportationShopFragment(val transport: BusinessTransportationCompany) : Fragment(), TransportationCarBuyAdapter.OnItemClickListener {

    private val playerModel: PlayerModel by activityViewModels()

    lateinit var binding: FragmentTransportationShopBinding

    private val carsAdapter = TransportationCarBuyAdapter(this)
    private val carList = ArrayList<TransportationCar>()

    lateinit var dataBaseHelper: DataBaseHelper

    var selectedCarNumber = -1
    var selectedCarCard : View? = null

    var count: MutableLiveData<Int> = MutableLiveData(1)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dataBaseHelper = DataBaseHelper(requireContext())
        binding = FragmentTransportationShopBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()

        setObservers()

        initStaticViews()
    }

    private fun setButtonListeners() {

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnBuyCar.setOnClickListener {
            if (playerModel.balance.value!! >= carList[selectedCarNumber].price * count.value!!) {
                playerModel.balance.value = playerModel.balance.value!! - carList[selectedCarNumber].price * count.value!!
                for (i in 1..count.value!!)
                    addOwnedCar(carList[selectedCarNumber])
                requireActivity().supportFragmentManager.popBackStack()
                showInterstitialAd(0.6)
            }
        }

        binding.btnMinus.setOnClickListener {
            if (count.value == 1) {
                binding.tvCount.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
            }
            else {
                count.value = count.value!! - 1
            }

        }
        binding.btnPlus.setOnClickListener {
            if (count.value!! >= transport.carParkCapacity.value!! - transport.numberOfCars.value!! ||
                (count.value!! + 1) * carList[selectedCarNumber].price > playerModel.balance.value!!) {
                binding.tvCount.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.shake))
            }
            else {
                count.value = count.value!! + 1
            }
        }

    }

    private fun setObservers() {

        playerModel.balance.observe(this as LifecycleOwner, {
            binding.tvBalance.text = Strings.get(R.string.money, it)
        })

        count.observe(this as LifecycleOwner, {
            binding.tvCount.text = it.toString()
            if (selectedCarNumber != -1) {
                val total = carList[selectedCarNumber].price * it
                binding.tvTotal.text = Strings.get(R.string.total_string, Utils.convertMoneyToText(total))
                if (total > playerModel.balance.value!!) {
                    binding.btnBuyCar.isEnabled = false
                    binding.btnBuyCar.setTextColor(Color.parseColor("#757575"))
                }
                else {
                    binding.btnBuyCar.isEnabled = true
                    binding.btnBuyCar.setTextColor(Color.parseColor("#2f2f2f"))
                }
            }
        })
    }

    private fun initStaticViews() {
        val inputStream: InputStream = resources.openRawResource(R.raw.transportation_cars)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        reader.readLines().forEach {

            val items = it.split(",")
            val imageId: Int = resources.getIdentifier(items[2], "drawable", requireContext().packageName)
            var categoryId: Int = 0
            when(items[3]) {
                "TransportationCategories.CITY" -> categoryId = TransportationCategories.CITY
                "TransportationCategories.INTER_CITY" -> categoryId = TransportationCategories.INTER_CITY
            }
            carList.add(TransportationCar(items[0], items[1].toDouble(), imageId, categoryId, items[4].toInt(), items[2] ))

        }
        carsAdapter.setCarList(carList)

        binding.rcViewCars.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewCars.adapter = carsAdapter
    }

    override fun onItemClick(v: View?, position: Int) {
        if (playerModel.balance.value!! >= carList[position].price) {
//            if (selectedCarCard != null) {
//                (selectedCarCard as MaterialCardView).strokeWidth = 0
//                (selectedCarCard as MaterialCardView).strokeColor = (ContextCompat.getColor(requireContext(), R.color.transportation_car_unselected))
//                (selectedCarCard as MaterialCardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_car_unselected))
//            }

            if (v != null) {
                selectedCarCard = v
            }
            selectedCarNumber = position

//            (v as MaterialCardView).strokeWidth = 3
//            (v as MaterialCardView).strokeColor = (ContextCompat.getColor(requireContext(), R.color.transportation_filter_selected))
//            (v as MaterialCardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_car_selected))

            binding.buyPanel.visibility = View.VISIBLE
            binding.tvTitle.text = Strings.get(R.string.buying_a, carList[position].title)
            count.value = 1
        }

    }

    private fun addOwnedCar(c : TransportationCar) {
        var tmp = transport.ownedCarList.value
        if (tmp == null)
            tmp = ArrayList<TransportationCar>()
        c.id = dataBaseHelper.addNewTransportationCar(c, transport.id)
        tmp.add(c)


        transport.profit.postValue(c.profit)
        transport.capitalization += c.price
        transport.ownedCarList.value = tmp

        dataBaseHelper.setTaxiCapitalization(transport.id, transport.capitalization)
    }

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(),Strings.get(R.string.interstitial_ad_unit_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("InterstitialAd", adError?.message)
                playerModel.mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("InterstitialAd", "Ad was loaded.")
                playerModel.mInterstitialAd = interstitialAd
            }
        })
    }

    fun showInterstitialAd() {
        if (playerModel.mInterstitialAd != null) {
            playerModel.mInterstitialAd?.show(requireActivity())
            loadInterstitialAd()
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
            loadInterstitialAd()
        }
    }

    fun showInterstitialAd(probability: Double) {
        val r = Random.nextDouble(0.0, 1.0)
        if (probability >= r) {
            if (playerModel.mInterstitialAd != null) {
                playerModel.mInterstitialAd?.show(requireActivity())
                loadInterstitialAd()
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                loadInterstitialAd()
            }
        }

    }
}