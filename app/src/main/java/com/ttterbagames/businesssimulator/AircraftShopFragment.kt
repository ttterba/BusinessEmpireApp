package com.ttterbagames.businesssimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.databinding.FragmentAircraftShopBinding
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


class AircraftShopFragment : Fragment(), PurchasedAircraftAdapter.OnItemClickListener {

    val playerModel: PlayerModel by activityViewModels()
    lateinit var binding: FragmentAircraftShopBinding

    private val aircraftAdapter = PurchasedAircraftAdapter(this)

    lateinit var bnv: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAircraftShopBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        bnv.visibility = View.GONE

        setButtonListeners()
        setObservers()

        if (!playerModel.isPurchasedAircraftsInitialized) {
            readAircraftFromDB()
            playerModel.isPurchasedAircraftsInitialized = true
        }
        initRecyclerView()
    }

    override fun onDetach() {
        super.onDetach()
        bnv.visibility = View.VISIBLE
    }

    private fun readAircraftsFromFile() {
        val inputStream: InputStream = resources.openRawResource(R.raw.purchased_aircrafts)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        reader.readLines().forEach {

            val items = it.split(",")
            val imageId: Int = resources.getIdentifier(items[2], "drawable", requireContext().packageName)
            playerModel.purchasedAircraftList.add(PurchasedAircraft(items[0], items[1].toDouble(), imageId))

        }
    }

    private fun readAircraftFromDB() {
        val arr = playerModel.dataBaseHelper.getPurchasedAircrafts()
        playerModel.purchasedAircraftList.addAll(arr)
    }

    private fun initRecyclerView() {
        aircraftAdapter.setAircraftList(playerModel.purchasedAircraftList)
        binding.rcViewAircraft.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewAircraft.adapter = aircraftAdapter
    }

    private fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setObservers() {
        playerModel.balance.observe(this as LifecycleOwner, {
            binding.tvBalance.text = Strings.get(R.string.money, it)
        })
    }

    override fun onItemClick(v: View?, position: Int) {
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fl_wrapper, PurchasedAircraftBuyFragment(playerModel.purchasedAircraftList[position]))
            addToBackStack(null)
        }
    }
}