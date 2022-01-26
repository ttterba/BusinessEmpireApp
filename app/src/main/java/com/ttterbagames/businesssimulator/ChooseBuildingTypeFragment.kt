package com.ttterbagames.businesssimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.databinding.FragmentChooseBuildingTypeBinding
import com.google.android.material.card.MaterialCardView


class ChooseBuildingTypeFragment(var building: BusinessBuildingCompany) : Fragment(), BuildingInfoAdapter.OnItemClickListener {

    lateinit var binding: FragmentChooseBuildingTypeBinding
    var buildingList = ArrayList<Building>()
    var selectedBuildingNumber = -1
    var selectedBuildingCard : View? = null

    private val buildingsAdapter = BuildingInfoAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChooseBuildingTypeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addObservers()

        initRecyclerView()

        setButtonListeners()


    }

    private fun addObservers() {

        building.buildersNumber.observe(this as LifecycleOwner, {
            binding.tvBuilders.text = Strings.get(R.string.human_count, it)
        })

        building.metalNumber.observe(this as LifecycleOwner, {
            binding.tvMetal.text = Strings.get(R.string.tons_count, it)
        })

        building.woodNumber.observe(this as LifecycleOwner, {
            binding.tvWood.text = Strings.get(R.string.сub_count, it)
        })

        building.concreteNumber.observe(this as LifecycleOwner, {
            binding.tvConcrete.text = Strings.get(R.string.сub_count, it)
        })
    }

    private fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnStartBuilding.setOnClickListener {
            if (selectedBuildingNumber != -1) {
                val selected = buildingList[selectedBuildingNumber]

                val newWorkers = building.buildersNumber.value!!.minus(selected.buildersNumber)
                val newMetal = building.metalNumber.value!!.minus(selected.metalNumber)
                val newWood = building.woodNumber.value!!.minus(selected.woodNumber)
                val newConcrete = building.concreteNumber.value!!.minus(selected.concreteNumber)

                building.buildersNumber.postValue(newWorkers)
                building.metalNumber.postValue(newMetal)
                building.woodNumber.postValue(newWood)
                building.concreteNumber.postValue(newConcrete)

                building.dataBaseHelper.setBuildingCompanyResources(building.id, newWorkers, newMetal, newWood, newConcrete)
                building.calculateCapitalization()
                building.dataBaseHelper.setBuildingCompanyCapitalization(building.id, building.capitalization)

                selected.id = building.dataBaseHelper.addNewBuilding(selected, building.id)
                building.addBuilding(selected)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun initRecyclerView() {
        var i = 0
        while (i <= 4) {
            buildingList.add(Building(i, building.dataBaseHelper))
            i++
        }

        buildingsAdapter.setBuildingsList(buildingList)

        binding.rcViewBuildings.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewBuildings.adapter = buildingsAdapter
    }

    override fun onItemClick(v: View?, position: Int) {
        if (building.woodNumber.value!! >= buildingList[position].woodNumber && building.metalNumber.value!! >= buildingList[position].metalNumber && building.buildersNumber.value!! >= buildingList[position].buildersNumber && building.concreteNumber.value!! >= buildingList[position].concreteNumber) {



            if (selectedBuildingCard != null) {
                (selectedBuildingCard as MaterialCardView).strokeWidth = 0
                (selectedBuildingCard as MaterialCardView).strokeColor = (ContextCompat.getColor(requireContext(), R.color.taxi_car_unselected))
                (selectedBuildingCard as MaterialCardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_car_unselected))
            }

            if (v != null) {
                selectedBuildingCard = v
            }
            selectedBuildingNumber= position

            (v as MaterialCardView).strokeWidth = 3
            (v as MaterialCardView).strokeColor = (ContextCompat.getColor(requireContext(), R.color.building_red))
            (v as MaterialCardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.building_selected))

            binding.btnStartBuilding.visibility = View.VISIBLE


        }
    }

}