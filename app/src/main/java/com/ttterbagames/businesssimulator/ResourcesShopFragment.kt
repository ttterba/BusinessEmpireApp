package com.ttterbagames.businesssimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.databinding.FragmentResourcesShopBinding


class ResourcesShopFragment(var buildingCompany: BusinessBuildingCompany, var playerModel: PlayerModel) : Fragment() {

    private val balance = playerModel.balance.value!!

    lateinit var binding: FragmentResourcesShopBinding

    private var buildersValue = 0
    private var metalValue = 0
    private var woodValue = 0
    private var concreteValue = 0

    private var summary = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentResourcesShopBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStaticViews()

        setSeekBarsMaxValues()

        setSeekBarsListeners()

        setButtonListeners()
    }

    private fun setSeekBarsMaxValues() {
        val maxBuilders = (balance).toInt().floorDiv(ResourcesCost.BUILDER.toInt())
        val maxMetal = (balance).toInt().floorDiv(ResourcesCost.METAL.toInt())
        val maxWood = (balance).toInt().floorDiv(ResourcesCost.WOOD.toInt())
        val maxConcrete = (balance).toInt().floorDiv(ResourcesCost.CONCRETE.toInt())


        binding.apply {
            sbBuilders.max = maxBuilders
            sbMetal.max = maxMetal
            sbWood.max = maxWood
            sbConcrete.max = maxConcrete

            tvMinBuilders.text = Strings.get(R.string.human_count, 0)
            tvMinMetal.text = Strings.get(R.string.tons_count, 0)
            tvMinWood.text = Strings.get(R.string.сub_count, 0)
            tvMinConcrete.text = Strings.get(R.string.сub_count, 0)

            tvMaxBuilders.text = Strings.get(R.string.human_count, maxBuilders)
            tvMaxMetal.text = Strings.get(R.string.tons_count, maxMetal)
            tvMaxWood.text = Strings.get(R.string.сub_count, maxWood)
            tvMaxConcrete.text = Strings.get(R.string.сub_count, maxConcrete)
        }
    }

    private fun setSeekBarsListeners() {
        binding.apply {
            sbBuilders.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    buildersValue = progress
                    tvMinBuilders.text = Strings.get(R.string.human_count, buildersValue)
                    summary = (
                            progress * ResourcesCost.BUILDER +
                            metalValue * ResourcesCost.METAL +
                            woodValue * ResourcesCost.WOOD +
                            concreteValue * ResourcesCost.CONCRETE).toInt()
                    tvSummary.text = Strings.get(R.string.money, summary.toDouble())

                    invalidateBuyButton(summary.toDouble())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }

            })

            sbMetal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    metalValue = progress
                    tvMinMetal.text = Strings.get(R.string.tons_count, metalValue)

                    summary = (
                            progress * ResourcesCost.METAL +
                                    buildersValue * ResourcesCost.BUILDER +
                                    woodValue * ResourcesCost.WOOD +
                                    concreteValue * ResourcesCost.CONCRETE).toInt()
                    tvSummary.text = Strings.get(R.string.money, summary.toDouble())

                    invalidateBuyButton(summary.toDouble())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })

            sbWood.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    woodValue = progress
                    tvMinWood.text = Strings.get(R.string.сub_count, woodValue)

                    summary = (
                            progress * ResourcesCost.WOOD +
                                    metalValue * ResourcesCost.METAL +
                                    buildersValue * ResourcesCost.BUILDER +
                                    concreteValue * ResourcesCost.CONCRETE).toInt()
                    tvSummary.text = Strings.get(R.string.money, summary.toDouble())

                    invalidateBuyButton(summary.toDouble())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })

            sbConcrete.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    concreteValue = progress
                    tvMinConcrete.text = Strings.get(R.string.сub_count, concreteValue)
                    summary = (
                            progress * ResourcesCost.CONCRETE +
                                    metalValue * ResourcesCost.METAL +
                                    woodValue * ResourcesCost.WOOD +
                                    buildersValue * ResourcesCost.BUILDER).toInt()
                    tvSummary.text = Strings.get(R.string.money, summary.toDouble())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
        }
    }

    private fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnBuy.setOnClickListener {
            if (summary.toDouble() <= playerModel.balance.value!!) {
                playerModel.balance.postValue(playerModel.balance.value!! - summary)

                val newWorkers = buildingCompany.buildersNumber.value!! + buildersValue
                val newWood = buildingCompany.woodNumber.value!! + woodValue
                val newMetal = buildingCompany.metalNumber.value!! + metalValue
                val newConcrete = buildingCompany.concreteNumber.value!! + concreteValue

                buildingCompany.buildersNumber.postValue( newWorkers)
                buildingCompany.woodNumber.postValue(newWood)
                buildingCompany.metalNumber.postValue(newMetal)
                buildingCompany.concreteNumber.postValue(newConcrete)

                playerModel.dataBaseHelper.setBuildingCompanyResources(buildingCompany.id, newWorkers, newMetal, newWood, newConcrete)
                buildingCompany.calculateCapitalization()
                playerModel.dataBaseHelper.setBuildingCompanyCapitalization(buildingCompany.id, buildingCompany.capitalization)

                requireActivity().supportFragmentManager.popBackStack()
            }

        }
    }

    private fun invalidateBuyButton(sum: Double) {
        if (sum > playerModel.balance.value!!)
            binding.btnBuy.visibility = View.INVISIBLE
        else
            binding.btnBuy.visibility = View.VISIBLE
    }

    private fun initStaticViews() {
        binding.apply {
            tvSummary.text = Strings.get(R.string.money, summary.toDouble())

            tvBuilders.text = Strings.get(R.string.human_count, buildingCompany.buildersNumber.value!!)
            tvMetal.text = Strings.get(R.string.tons_count, buildingCompany.metalNumber.value!!)
            tvWood.text = Strings.get(R.string.сub_count, buildingCompany.woodNumber.value!!)
            tvConcrete.text = Strings.get(R.string.сub_count, buildingCompany.concreteNumber.value!!)
        }

        playerModel.balance.observe(activity as LifecycleOwner, {
            binding.tvBalance.text = Strings.get(R.string.money, it)
        })
    }

}