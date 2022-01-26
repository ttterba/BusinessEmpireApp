package com.ttterbagames.businesssimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.databinding.FragmentTransportationAutoparkBinding
import com.ttterbagames.businesssimulator.fragments.TaxiFilters


class TransportationAutoparkFragment(val transport : BusinessTransportationCompany) : Fragment() {

    lateinit var binding: FragmentTransportationAutoparkBinding

    var selectedFilter = TransportationFilters.ALL
    lateinit var selectedCardFilter : View

    private val carsAdapter = TransportationCarAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTransportationAutoparkBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedCardFilter = binding.cardFilterAll
        setFiltersListeners()

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.rcViewCars.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewCars.adapter = carsAdapter

        transport.ownedCarList.observe(this as LifecycleOwner, {
            carsAdapter.setCarList(it)
        })
    }

    private fun setFiltersListeners() {

        binding.cardFilterAll.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_selected))
            selectedFilter = TaxiFilters.ALL
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.ALL)
        }

        binding.cardFilterHighestMileage.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_selected))
            selectedFilter = TaxiFilters.HIGHEST_MILEAGE
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.HIGHEST_MILEAGE)
        }

        binding.cardFilterLeastMileage.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_selected))
            selectedFilter = TaxiFilters.LEAST_MILEAGE
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.LEAST_MILEAGE)
        }

        binding.cardFilterHighestIncome.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_selected))
            selectedFilter = TaxiFilters.HIGHEST_INCOME
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.HIGHEST_INCOME)
        }

        binding.cardFilterLeastIncome.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transportation_filter_selected))
            selectedFilter = TaxiFilters.LEAST_INCOME
            selectedCardFilter = it
            setFilteredCarList(TaxiFilters.LEAST_INCOME)
        }


    }

    private fun setFilteredCarList(filterId: Int) {

        if (transport.ownedCarList.value != null) {
            when(filterId) {
                TaxiFilters.ALL -> {
                    transport.ownedCarList.value!!.shuffle()
                }
                TaxiFilters.LEAST_MILEAGE -> {
                    transport.ownedCarList.value!!.sortBy { it.mileage.value }
                }
                TaxiFilters.HIGHEST_MILEAGE -> {
                    transport.ownedCarList.value!!.sortBy { it.mileage.value }
                    transport.ownedCarList.value!!.reverse()
                }
                TaxiFilters.LEAST_INCOME -> {
                    transport.ownedCarList.value!!.sortBy { it.profit }
                }
                TaxiFilters.HIGHEST_INCOME -> {
                    transport.ownedCarList.value!!.sortBy { it.profit }
                    transport.ownedCarList.value!!.reverse()
                }
            }

            carsAdapter.setCarList(transport.ownedCarList.value!!)
        }



    }
}