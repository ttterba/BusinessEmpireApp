package com.ttterbagames.businesssimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.databinding.FragmentRealEstateOwnedBinding


class RealEstateOwnedFragment : Fragment(), RealEstateOffersAdapter.OnItemClickListener {

    val playerModel: PlayerModel by activityViewModels()
    lateinit var binding: FragmentRealEstateOwnedBinding

    var reAdapter = RealEstateOffersAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRealEstateOwnedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (playerModel.ownedRealEstate.value == null || playerModel.ownedRealEstate.value?.size == 0)
            binding.tvEmpty.visibility = View.VISIBLE
        else
            binding.tvEmpty.visibility = View.GONE

        binding.rcViewOwned.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        reAdapter.balance = playerModel.balance.value!!
        binding.rcViewOwned.adapter = reAdapter

        if (playerModel.ownedRealEstate.value != null)
            reAdapter.setREList(playerModel.ownedRealEstate.value!!)

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onItemClick(v: View?, position: Int) {

    }

}