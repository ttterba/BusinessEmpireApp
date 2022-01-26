package com.ttterbagames.businesssimulator

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.databinding.FragmentBuildingBinding


class BuildingFragment(val playerModel: PlayerModel) : Fragment(){

    lateinit var binding: FragmentBuildingBinding

    var building = BusinessBuildingCompany(playerModel.dataBaseHelper)

    var position: Int = 0
    lateinit var bnv: View

    var builtNumber = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBuildingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        bnv.visibility = View.GONE

        setButtonListeners()

        binding.tvCarCount.text = Strings.get(R.string.number_of_buildings_in_progress, 0)
        binding.tvBuiltNumber.text = Strings.get(R.string.ready_to_sell, 0)

        addObservers()

        initStaticViews()
    }

    override fun onDetach() {
        super.onDetach()
        bnv.visibility = View.VISIBLE
    }

    fun init(pPosition: Int) {
        position = pPosition
        building = (playerModel.ownedBusinesses.value?.get(position) as BusinessBuildingCompany)
    }

    private fun setButtonListeners() {

        binding.btnBuildingList.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, BuildingsInProcessFragment(building))
                addToBackStack(null)
            }
        }

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSettings.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, BusinessSettingsFragment(building, position))
                addToBackStack(null)
            }
        }

        binding.btnStartNewBuilding.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, ChooseBuildingTypeFragment(building))
                addToBackStack(null)
            }
        }

        binding.btnBuyResources.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.fl_wrapper, ResourcesShopFragment(building, playerModel))
                addToBackStack(null)
            }
        }

    }

    private fun calcBuiltNumber() {
        builtNumber = 0
        for (b in building.buildingList.value!!)
            if (b.isBuilt.value!!)
                builtNumber++
    }

    private fun addObservers() {

        building.buildingList.observe(this as LifecycleOwner, {
            if (it != null) {
                var sum = 0.0
                for (b in it)
                    sum += b.sellPrice
                binding.tvProfit.text = Strings.get(R.string.money, sum)

                calcBuiltNumber()
                binding.tvCarCount.text = Strings.get(R.string.number_of_buildings_in_progress, it.size - builtNumber)
                binding.tvBuiltNumber.text = Strings.get(R.string.ready_to_sell, builtNumber)
                setAppropriateSummaryProfitFontSize(sum)
            } else {
                binding.tvCarCount.text = Strings.get(R.string.number_of_buildings_in_progress, 0)
            }
        })

        if (building.buildingList.value != null) {
            for ((i, b) in building.buildingList.value!!.withIndex()) {
                b.isBuilt.observe(activity as LifecycleOwner, {
                    calcBuiltNumber()
                    binding.tvCarCount.text = Strings.get(R.string.number_of_buildings_in_progress, building.buildingList.value!!.size - builtNumber)
                    binding.tvBuiltNumber.text = Strings.get(R.string.ready_to_sell, builtNumber)
                })
            }

        }


        building.buildersNumber.observe(this as LifecycleOwner, {
            binding.tvBuilders.text = Strings.get(R.string.human_count, it)
            building.calculateCapitalization()
        })

        building.metalNumber.observe(this as LifecycleOwner, {
            binding.tvMetal.text = Strings.get(R.string.tons_count, it)
            building.calculateCapitalization()
        })

        building.woodNumber.observe(this as LifecycleOwner, {
            binding.tvWood.text = Strings.get(R.string.сub_count, it)
            building.calculateCapitalization()
        })

        building.concreteNumber.observe(this as LifecycleOwner, {
            binding.tvConcrete.text = Strings.get(R.string.сub_count, it)
            building.calculateCapitalization()
        })


    }

    private fun initStaticViews() {
        binding.tvTitle.text = building.title

        var sum = 0.0
        if (building.buildingList.value != null) {
            for (b in building.buildingList.value!!)
                sum += b.sellPrice
        }
        binding.tvProfit.text = Strings.get(R.string.money, sum)
        setAppropriateSummaryProfitFontSize(sum)

    }

    fun initate(pPosition: Int) {
        position = pPosition
        building = (playerModel.ownedBusinesses.value?.get(position) as BusinessBuildingCompany)
    }


    private fun setAppropriateSummaryProfitFontSize(num: Double) {
        binding.tvProfit.apply {
            when (num) {
                in 0.0..BusinessConstants.PROFIT_CARD_BREAKPOINT_1 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_1..BusinessConstants.PROFIT_CARD_BREAKPOINT_2 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_2..BusinessConstants.PROFIT_CARD_BREAKPOINT_3 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_3..BusinessConstants.PROFIT_CARD_BREAKPOINT_4 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_4..BusinessConstants.PROFIT_CARD_BREAKPOINT_5 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_5..BusinessConstants.PROFIT_CARD_BREAKPOINT_6 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 21f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_6..BusinessConstants.PROFIT_CARD_BREAKPOINT_7 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                in BusinessConstants.PROFIT_CARD_BREAKPOINT_7..BusinessConstants.PROFIT_CARD_BREAKPOINT_8 ->
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
                else -> setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }
        }

    }

}