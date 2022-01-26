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
import com.ttterbagames.businesssimulator.databinding.FragmentRealEstateOffersBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RealEstateOffersFragment() : Fragment(), RealEstateOffersAdapter.OnItemClickListener {

    val playerModel: PlayerModel by activityViewModels()
    lateinit var binding: FragmentRealEstateOffersBinding

    var reAdapter = RealEstateOffersAdapter(this)

    //lateinit var bnv: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRealEstateOffersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        //bnv.visibility = View.GONE

        binding.rcViewOffers.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        reAdapter.balance = playerModel.balance.value!!
        binding.rcViewOffers.adapter = reAdapter

        binding.tvBalance.text = Strings.get(R.string.money, playerModel.balance.value!!)

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        addObservers()

//        lifecycleScope.executeAsyncTask(onPreExecute = {
//
//        }, doInBackground = {
//            var arr = java.util.ArrayList<RealEstate>()
//            if (!playerModel.isRealEstateListInitialized) {
//                val inputStream: InputStream = resources.openRawResource(R.raw.real_estate)
//                val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))
//                reader.readLines().forEach {
//
//                    val items = it.split(",")
//                    val imageId: Int = resources.getIdentifier(items[3], "drawable", requireContext().packageName)
//
//                    arr.add( RealEstate(items[0], items[1], items[2].toLong(), imageId) )
//
//                }
//            }
//            arr
//
//        }, onPostExecute = {
//            if (!playerModel.isRealEstateListInitialized) {
//                playerModel.realEstateOffersList.postValue(it)
//                playerModel.isRealEstateListInitialized = true
//            }
//        })

        if (playerModel.realEstateOffersList.value?.size == 0)
            binding.tvEmpty.visibility = View.VISIBLE
        else
            binding.tvEmpty.visibility = View.GONE

    }

    override fun onDetach() {
        super.onDetach()
        //bnv.visibility = View.VISIBLE
    }

    private fun addObservers() {
        playerModel.realEstateOffersList.observe(activity as LifecycleOwner, {
            reAdapter.setREList(it)
        })

    }

    override fun onItemClick(v: View?, position: Int) {
        if (playerModel.realEstateOffersList.value!![position].Price <= playerModel.balance.value!!)
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.main_activity_container, RealEstateConfirmationFragment(playerModel.realEstateOffersList.value!![position], position))
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