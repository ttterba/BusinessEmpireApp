package com.ttterbagames.businesssimulator.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.*
import com.ttterbagames.businesssimulator.databinding.FragmentBusinessBinding


class BusinessFragment : Fragment(), BusinessAdapter.OnItemClickListener{


    private val playerModel: PlayerModel by activityViewModels()
    lateinit var binding: FragmentBusinessBinding

    private val ownedBusinessesAdapter = BusinessAdapter(this)



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBusinessBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        playerModel.summaryOwnedBusinessesProfit.observe(this as LifecycleOwner, {
            binding.tvSummaryProfit.text = getString(R.string.money, it)
            setAppropriateSummaryProfitFontSize(it)
        })

        playerModel.ownedBusinesses.observe(activity as LifecycleOwner, {
            if (it.size >= 10)
                binding.btnCreateBusiness.visibility = View.GONE
            else
                binding.btnCreateBusiness.visibility = View.VISIBLE

            ownedBusinessesAdapter.setBusinessList(it)
            updateSummaryProfit()

            if (it.size == 0)
                binding.tvNoBusiness.visibility = View.VISIBLE
            else
                binding.tvNoBusiness.visibility = View.GONE
        })

        if (playerModel.ownedBusinesses.value != null) {
            for (business: Business in playerModel.ownedBusinesses.value!!)
                business.profit.observe(activity as LifecycleOwner, {
                    updateSummaryProfit()
                    ownedBusinessesAdapter.notifyDataSetChanged()
                })
        }

        init()

        binding.tvSummaryProfit.setOnClickListener {
            Log.d("profit", playerModel.summaryOwnedBusinessesProfit.value.toString())
        }

    }



    private fun init() {
        binding.rcViewMyCompanies.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewMyCompanies.adapter = ownedBusinessesAdapter
//        for (b in playerModel.ownedBusinesses)
//            ownedBusinessesAdapter.addBusiness(b)

//        binding.btnCreateBusiness.setOnClickListener {
//            val shop = BusinessShop(this.requireContext())
//            shop.initData(2)
//            addOwnedBusiness(shop)
//        }



        binding.btnCreateBusiness.setOnClickListener {
            if (playerModel.ownedBusinesses.value!!.size  < 10) {
                val cbc = CreateBusinessFragment()
                requireActivity().supportFragmentManager.commit {
                    setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                    replace(R.id.fl_wrapper, cbc)
                    addToBackStack(null)
                }
            }

        }
    }

//    private fun addOwnedBusiness(b : Business) {
//        playerModel.ownedBusinesses.add(b)
//        ownedBusinessesAdapter.addBusiness(b)
//        playerModel.summaryOwnedBusinessesProfit.value =
//            playerModel.summaryOwnedBusinessesProfit.value?.plus(b.profit)
//    }

    private fun addOwnedBusiness(b : Business) {
        var tmp = playerModel.ownedBusinesses.value
        if (tmp == null)
            tmp = ArrayList<Business>()
        tmp.add(b)

        playerModel.ownedBusinesses.value = tmp
        playerModel.summaryOwnedBusinessesProfit.value =
            b.profit.value?.let { playerModel.summaryOwnedBusinessesProfit.value?.plus(it) }
    }

    private fun setAppropriateSummaryProfitFontSize(num: Double) {
        when (num) {
            in 0.0..10000000.0 ->
                binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
            in 10000000.0..1000000000.0 ->
                binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            in 1000000000.0..100000000000.0 ->
                binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            in 100000000000.0..10000000000000.0 ->
                binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            else -> binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }
    }

    private fun updateSummaryProfit() {
        Log.d("Tag", "я сработаль")
        var sum = 0.0
        if (playerModel.ownedBusinesses.value != null) {
            for (s in playerModel.ownedBusinesses.value!!)
                sum += s.profit.value!!
        }
        playerModel.summaryOwnedBusinessesProfit.value = sum
    }


    override fun onItemClick(position: Int) {
        when (playerModel.ownedBusinesses.value!![position].businessType) {
            getString(R.string.shop) -> {
                val sf = ShopFragment(playerModel)
                sf.init(playerModel.ownedBusinesses.value!![position], position)
                openBusinessPage(sf)
            }
            getString(R.string.factory) -> {
                val sf = FactoryFragment(playerModel)
                sf.init(playerModel.ownedBusinesses.value!![position], position)
                openBusinessPage(sf)
            }
            getString(R.string.taxi_station) -> {
                val f = TaxiFragment(playerModel)
                f.init(position)
                openBusinessPage(f)
            }
            getString(R.string.transportation) -> {
                val f = TransportationFragment(playerModel)
                f.init(position)
                openBusinessPage(f)
            }
            getString(R.string.building_company) -> {
                val f = BuildingFragment(playerModel)
                f.init(position)
                openBusinessPage(f)
            }
            getString(R.string.bank) -> {
                val f = BankFragment(playerModel)
                f.init(position)
                openBusinessPage(f)
            }
        }

//        sf.init(playerModel.ownedBusinesses.value!![position], position)
//        requireActivity().supportFragmentManager.commit {
//            setCustomAnimations(
//                R.anim.slide_in,
//                R.anim.fade_out,
//                R.anim.fade_in,
//                R.anim.slide_out
//            )
//            replace(R.id.fl_wrapper, sf)
//            addToBackStack(null)
//        }
    }

    private fun openBusinessPage(f: Fragment) {
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fl_wrapper, f)
            addToBackStack(null)
        }
    }


}