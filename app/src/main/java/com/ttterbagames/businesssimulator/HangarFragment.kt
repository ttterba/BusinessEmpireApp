package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.databinding.FragmentHangarBinding

class HangarFragment : Fragment(), OwnedAircraftAdapter.OnItemClickListener {

    val playerModel: PlayerModel by activityViewModels()
    lateinit var binding: FragmentHangarBinding
    lateinit var bnv: View

    private val aircraftAdapter = OwnedAircraftAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHangarBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        bnv.visibility = View.GONE

        setButtonListeners()
        initRecyclerView()
    }

    override fun onDetach() {
        super.onDetach()
        bnv.visibility = View.VISIBLE
    }

    private fun initRecyclerView() {

        aircraftAdapter.setAircraftList(playerModel.ownedAircraftList)
        binding.rcViewAircrafts.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewAircrafts.adapter = aircraftAdapter
    }

    private fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        binding.carFilterExpensive.setOnClickListener {
            selectExpensive()
        }

        binding.carFilterCheap.setOnClickListener {
            selectCheap()
        }

    }

    private fun selectExpensive() {
        binding.carFilterCheap.setCardBackgroundColor(Color.parseColor("#ECF1F5"))
        binding.carFilterExpensive.setCardBackgroundColor(Color.parseColor("#2C7DFE"))

        binding.carFilterCheapText.setTextColor(Color.parseColor("#2f2f2f"))
        binding.carFilterExpensiveText.setTextColor(Color.parseColor("#ffffff"))

        playerModel.ownedAircraftList.sortBy { it.moneySpentOnIt }
        playerModel.ownedAircraftList.reverse()

        aircraftAdapter.setAircraftList(playerModel.ownedAircraftList)
    }

    private fun selectCheap() {
        binding.carFilterCheap.setCardBackgroundColor(Color.parseColor("#2C7DFE"))
        binding.carFilterExpensive.setCardBackgroundColor(Color.parseColor("#ECF1F5"))

        binding.carFilterCheapText.setTextColor(Color.parseColor("#ffffff"))
        binding.carFilterExpensiveText.setTextColor(Color.parseColor("#2f2f2f"))

        playerModel.ownedAircraftList.sortBy { it.moneySpentOnIt }

        aircraftAdapter.setAircraftList(playerModel.ownedAircraftList)
    }

    override fun onItemClick(v: View?, position: Int) {
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fl_wrapper, OwnedAircraftFragment(playerModel.ownedAircraftList[position]))
            addToBackStack(null)
        }
    }

}