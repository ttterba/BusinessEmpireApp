package com.ttterbagames.businesssimulator

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttterbagames.businesssimulator.databinding.FragmentStockMarketBinding
import com.ttterbagames.businesssimulator.fragments.TaxiFilters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


class StockMarketFragment : Fragment(), StockAdapter.OnItemClickListener {

    val playerModel: PlayerModel by activityViewModels()

    lateinit var binding: FragmentStockMarketBinding
    lateinit var bnv: View

    var stockAdapter = StockAdapter(this)

    val changeNotifier: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    var selectedFilter = 0
    lateinit var selectedCardFilter : View
    lateinit var selectedText: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentStockMarketBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        bnv.visibility = View.GONE

        selectedCardFilter = binding.filterHighestDividend
        selectedText = binding.highestDividendText

        setButtonListeners()
        addObservers()

        binding.rcViewStocks.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        if (playerModel.stockMarketLots.size > 50)
            playerModel.stockMarketLots.distinctBy { it.title }
        stockAdapter.setList(playerModel.stockMarketLots)
        binding.rcViewStocks.adapter = stockAdapter

        setFiltersListeners()
    }

    private fun setFiltersListeners() {

        binding.filterHighestDividend.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_nice))

            (selectedText as TextView).setTextColor(Color.parseColor("#2f2f2f"))
            binding.highestDividendText.setTextColor(Color.parseColor("#ffffff"))

            selectedFilter = StockFilters.HIGH_DIVIDEND
            selectedText = binding.highestDividendText
            selectedCardFilter = it
            setFilteredList(selectedFilter)
        }

        binding.filterLowestDividend.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_nice))

            (selectedText as TextView).setTextColor(Color.parseColor("#2f2f2f"))
            binding.lowestDividendText.setTextColor(Color.parseColor("#ffffff"))

            selectedFilter = StockFilters.LOW_DIVIDEND
            selectedText = binding.lowestDividendText
            selectedCardFilter = it
            setFilteredList(selectedFilter)
        }

        binding.filterCheap.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_nice))

            (selectedText as TextView).setTextColor(Color.parseColor("#2f2f2f"))
            binding.cheapFirstText.setTextColor(Color.parseColor("#ffffff"))

            selectedFilter = StockFilters.CHEAP
            selectedText = binding.cheapFirstText
            selectedCardFilter = it
            setFilteredList(selectedFilter)
        }

        binding.filterExpensive.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_nice))

            (selectedText as TextView).setTextColor(Color.parseColor("#2f2f2f"))
            binding.expensiveFirstText.setTextColor(Color.parseColor("#ffffff"))

            selectedFilter = StockFilters.EXPENSIVE
            selectedText = binding.expensiveFirstText
            selectedCardFilter = it
            setFilteredList(selectedFilter)
        }

        binding.filterHighestCapitalization.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_nice))

            (selectedText as TextView).setTextColor(Color.parseColor("#2f2f2f"))
            binding.highCapitalizationText.setTextColor(Color.parseColor("#ffffff"))

            selectedFilter = StockFilters.HIGH_CAPITALIZATION
            selectedText = binding.highCapitalizationText
            selectedCardFilter = it
            setFilteredList(selectedFilter)
        }

        binding.filterLowestCapitalization.setOnClickListener {
            (selectedCardFilter as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.taxi_filter_unselected))
            (it as CardView).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_nice))

            (selectedText as TextView).setTextColor(Color.parseColor("#2f2f2f"))
            binding.lowCapitalizationText.setTextColor(Color.parseColor("#ffffff"))

            selectedFilter = StockFilters.LOW_CAPITALIZATION
            selectedText = binding.lowCapitalizationText
            selectedCardFilter = it
            setFilteredList(selectedFilter)
        }
    }

    private fun setFilteredList(filter: Int) {
        when (filter) {
            StockFilters.HIGH_DIVIDEND -> {
                playerModel.stockMarketLots.sortBy { it.dividendPercent }
                playerModel.stockMarketLots.reverse()
                stockAdapter.setList(playerModel.stockMarketLots)
            }
            StockFilters.LOW_DIVIDEND -> {
                playerModel.stockMarketLots.sortBy { it.dividendPercent }
                stockAdapter.setList(playerModel.stockMarketLots)
            }
            StockFilters.CHEAP -> {
                playerModel.stockMarketLots.sortBy { it.currentPrice }
                stockAdapter.setList(playerModel.stockMarketLots)
            }
            StockFilters.EXPENSIVE -> {
                playerModel.stockMarketLots.sortBy { it.currentPrice }
                playerModel.stockMarketLots.reverse()
                stockAdapter.setList(playerModel.stockMarketLots)
            }
            StockFilters.LOW_CAPITALIZATION -> {
                playerModel.stockMarketLots.sortBy { it.currentPrice * it.lotsCount }
                stockAdapter.setList(playerModel.stockMarketLots)
            }
            StockFilters.HIGH_CAPITALIZATION -> {
                playerModel.stockMarketLots.sortBy { it.currentPrice * it.lotsCount }
                playerModel.stockMarketLots.reverse()
                stockAdapter.setList(playerModel.stockMarketLots)
            }
        }
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

        changeNotifier.observe(activity as LifecycleOwner, {
            stockAdapter.setList(playerModel.stockMarketLots)
        })
    }

    override fun onDetach() {
        super.onDetach()
        bnv.visibility = View.VISIBLE
    }

    fun getStockList(): ArrayList<Stock> {
        val arr = ArrayList<Stock>()
        val inputStream: InputStream = resources.openRawResource(R.raw.stock_market_companies)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
        reader.readLines().forEach {

            val items = it.split(",")

            arr.add( Stock(items[0], items[1].toLong(), items[2].toDouble(), items[3].toDouble()) )

        }
        return arr
    }

    fun initRecyclerView() {
        if (!playerModel.isStockMarketLotsInitialized) {
            playerModel.isStockMarketLotsInitialized = true

            val inputStream: InputStream = resources.openRawResource(R.raw.stock_market_companies)
            val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
            reader.readLines().forEach {

                val items = it.split(",")

                playerModel.stockMarketLots.add( Stock(items[0], items[1].toLong(), items[2].toDouble(), items[3].toDouble()) )

            }

            playerModel.startStockMarket()
            Log.d("lax", "произшел старт биржи")
        }

        stockAdapter.setList(playerModel.stockMarketLots)

        binding.rcViewStocks.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rcViewStocks.adapter = stockAdapter
    }

    override fun onItemClick(v: View?, position: Int) {
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fl_wrapper, StockInfoFragment(playerModel.stockMarketLots[position]))
            addToBackStack(null)
        }
    }

    fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit,
    ) = launch {
        onPreExecute() // runs in Main Thread
        val result = withContext(Dispatchers.IO) {
            doInBackground() // runs in background thread without blocking the Main Thread
        }
        onPostExecute(result) // runs in Main Thread
    }

}

object StockFilters {
    const val HIGH_DIVIDEND = 1
    const val LOW_DIVIDEND = 2
    const val CHEAP = 3
    const val EXPENSIVE = 4
    const val HIGH_CAPITALIZATION = 5
    const val LOW_CAPITALIZATION = 6
}