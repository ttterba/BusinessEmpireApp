package com.ttterbagames.businesssimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.databinding.FragmentTaxiAutoparkBinding
import com.ttterbagames.businesssimulator.fragments.TaxiFilters


class TaxiAutoparkFragment(val taxi: BusinessTaxiStation) : Fragment(), TaxiCarAdapter.OnItemClickListener {

    lateinit var binding: FragmentTaxiAutoparkBinding

    var selectedFilter = TaxiFilters.ALL
    lateinit var selectedCardFilter : View

    private val carsAdapter = TaxiCarAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTaxiAutoparkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedCardFilter = binding.cardFilterAll

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.rcViewCars.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewCars.adapter = carsAdapter

        taxi.ownedCarList.observe(this as LifecycleOwner, {
            carsAdapter.setCarList(it)
        })

        setFiltersListeners()
    }

    private fun setFiltersListeners() {

        binding.cardFilterAll.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_selected))
            selectedFilter = TaxiFilters.ALL
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.ALL)
        }

        binding.cardFilterHighestMileage.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_selected))
            selectedFilter = TaxiFilters.HIGHEST_MILEAGE
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.HIGHEST_MILEAGE)
        }

        binding.cardFilterLeastMileage.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_selected))
            selectedFilter = TaxiFilters.LEAST_MILEAGE
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.LEAST_MILEAGE)
        }

        binding.cardFilterHighestIncome.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_selected))
            selectedFilter = TaxiFilters.HIGHEST_INCOME
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.HIGHEST_INCOME)
        }

        binding.cardFilterLeastIncome.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_selected))
            selectedFilter = TaxiFilters.LEAST_INCOME
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.LEAST_INCOME)
        }


    }

    private fun setFilteredCarList(filterId: Int) {

        if (taxi.ownedCarList.value != null) {
            when(filterId) {
                TaxiFilters.ALL -> {
                    taxi.ownedCarList.value!!.shuffle()
                }
                TaxiFilters.LEAST_MILEAGE -> {
                    taxi.ownedCarList.value!!.sortBy { it.mileage.value }
                }
                TaxiFilters.HIGHEST_MILEAGE -> {
                    taxi.ownedCarList.value!!.sortBy { it.mileage.value }
                    taxi.ownedCarList.value!!.reverse()
                }
                TaxiFilters.LEAST_INCOME -> {
                    taxi.ownedCarList.value!!.sortBy { it.profit }
                }
                TaxiFilters.HIGHEST_INCOME -> {
                    taxi.ownedCarList.value!!.sortBy { it.profit }
                    taxi.ownedCarList.value!!.reverse()
                }
            }

            carsAdapter.setCarList(taxi.ownedCarList.value!!)
        }



    }

    override fun onItemClick(v: View?, position: Int) {
        val c = taxi.ownedCarList.value!![position]
        Toast.makeText(requireContext(), "Title: '${c.title}', profit: ${c.profit}, mileage: ${c.mileage.value}/${c.maxMileage}, " +
                "mileage per hour: ${c.mileagePerHour} id: ${c.id}, position: $position", Toast.LENGTH_SHORT).show()
    }
}