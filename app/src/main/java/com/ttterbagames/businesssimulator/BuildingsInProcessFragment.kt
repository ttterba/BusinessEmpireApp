package com.ttterbagames.businesssimulator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.databinding.FragmentBuildingsInProcessBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


class BuildingsInProcessFragment(val building: BusinessBuildingCompany) : Fragment(), BuildingAdapter.OnItemClickListener {

    val playerModel: PlayerModel by activityViewModels()

    lateinit var binding: FragmentBuildingsInProcessBinding
    var buildingAdapter = BuildingAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBuildingsInProcessBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.rcViewBuildings.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewBuildings.adapter = buildingAdapter

        building.buildingList.observe(this as LifecycleOwner, {
            buildingAdapter.setBuildingsList(it)
        })

        playerModel.isRewardedAdReady.observe(this as LifecycleOwner, {
            buildingAdapter.adReady = it
            buildingAdapter.notifyDataSetChanged()
        })

        if (building.buildingList.value != null) {
            for ((i, b) in building.buildingList.value!!.withIndex()) {
                b.timeLeft.observe(activity as LifecycleOwner, {
                    buildingAdapter.notifyItemChanged(i)
                })
                b.isBuilt.observe(activity as LifecycleOwner, {
                    buildingAdapter.notifyItemChanged(i)
                })
            }

        }
    }

    override fun onItemClick(v: View?, position: Int) {
        if (!building.buildingList.value?.get(position)?.isBuilt?.value!! && !building.buildingList.value?.get(position)?.isBoosted?.value!!) {
            showAd(position)

            buildingAdapter.notifyItemChanged(position)

        } else if (building.buildingList.value?.get(position)?.isBuilt?.value!!){
            playerModel.balance.value = playerModel.balance.value?.plus(building.buildingList.value?.get(position)?.sellPrice!!)
            playerModel.earnedInBusiness += building.buildingList.value!![position].sellPrice - building.buildingList.value!![position].primeCost
            playerModel.dataBaseHelper.setEarnedInBusiness(playerModel.earnedInBusiness)

            building.allTimeEarnings += building.buildingList.value?.get(position)?.sellPrice!! - building.buildingList.value!![position].primeCost
            playerModel.dataBaseHelper.setBuildingCompanyAllTimeEarnings(building.id, building.allTimeEarnings)

            val arr = building.buildingList.value!!
            playerModel.dataBaseHelper.deleteBuilding(arr[position].id)
            arr.removeAt(position)
            buildingAdapter.setBuildingsList(arr)
        }
    }

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireContext(), Strings.get(R.string.rewarded_ad_unit_id), adRequest, object : RewardedAdLoadCallback() {
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
    }

    fun showAd(position: Int) {
        if (playerModel.mRewardedAd != null) {
            playerModel.mRewardedAd?.show(requireActivity()) {
                Log.d("RewardedAd", "User earned the reward.")
                building.buildingList.value?.get(position)?.boostBuilding()
                playerModel.dataBaseHelper.setBuildingIsBoosted(building.buildingList.value!![position].id, 1)
                playerModel.dataBaseHelper.setBuildingTimeLeft(building.buildingList.value!![position].id, building.buildingList.value!![position].timeLeft.value!!)
            }
            loadAd()
        } else {
            Log.d("RewardedAd", "The rewarded ad wasn't ready yet.")
            loadAd()
        }
    }

}