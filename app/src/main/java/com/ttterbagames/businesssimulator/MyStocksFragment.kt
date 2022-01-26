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
import com.ttterbagames.businesssimulator.databinding.FragmentMyStocksBinding


class MyStocksFragment : Fragment(), StockAdapter.OnItemClickListener {

    val playerModel: PlayerModel by activityViewModels()

    lateinit var binding: FragmentMyStocksBinding
    lateinit var bnv: View

    var stockAdapter = StockAdapter(this, true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyStocksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        bnv.visibility = View.GONE

        if (playerModel.ownedStockList.value == null || playerModel.ownedStockList.value?.size == 0)
            binding.tvNoStocks.visibility = View.VISIBLE

        setButtonListeners()
        addObservers()
        initRecyclerView()
    }

    fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun addObservers() {
        playerModel.updateStockNotifier.observe(activity as LifecycleOwner, {
            stockAdapter.notifyDataSetChanged()
        })
    }

    fun initRecyclerView() {



        if (playerModel.ownedStockList.value != null) {
            playerModel.ownedStockList.value!!.distinctBy { it.title }
            stockAdapter.setList(playerModel.ownedStockList.value!!)
        }


        binding.rcViewStocks.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewStocks.adapter = stockAdapter
    }

    override fun onDetach() {
        super.onDetach()
        bnv.visibility = View.VISIBLE
    }

    override fun onItemClick(v: View?, position: Int) {
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fl_wrapper, StockOwnedInfoFragment(playerModel.ownedStockList.value!![position]))
            addToBackStack(null)
        }
    }
}