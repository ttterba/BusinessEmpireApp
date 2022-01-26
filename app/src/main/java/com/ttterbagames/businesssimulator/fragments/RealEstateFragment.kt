package com.ttterbagames.businesssimulator.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.*
import com.ttterbagames.businesssimulator.databinding.FragmentRealEstateBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RealEstateFragment : Fragment(), RealEstateOffersAdapter.OnItemClickListener  {

    private val playerModel: PlayerModel by activityViewModels()

    lateinit var binding: FragmentRealEstateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentRealEstateBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("lifecycle", "OnViewCreated")
        super.onViewCreated(view, savedInstanceState)

        binding.tvCount.text = Strings.get(R.string.owned_real_estate_count, 0)
        addObservers()

        updateSummaryProfit()

        binding.btnMyProperty.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.main_activity_container, RealEstateOwnedFragment())
                addToBackStack(null)
            }
        }
        binding.btnOffers.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace(R.id.main_activity_container, RealEstateOffersFragment())
                addToBackStack(null)
            }
        }

    }

    private fun addObservers() {
        playerModel.ownedRealEstate.observe(activity as LifecycleOwner, {
            updateSummaryProfit()
            binding.tvCount.text = Strings.get(R.string.owned_real_estate_count, it.size)
        })

        playerModel.summaryOwnedRealEstateProfit.observe(activity as LifecycleOwner, {
            binding.tvSummaryProfit.text = Strings.get(R.string.money, it)
            setAppropriateSummaryProfitFontSize(it)
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
            replace(R.id.main_activity_container, RealEstateConfirmationFragment(playerModel.realEstateOffersList.value!![position], position))
            addToBackStack(null)
        }
    }

    private fun updateSummaryProfit() {
        var sum = 0.0
        if (playerModel.ownedRealEstate.value != null) {
            for (s in playerModel.ownedRealEstate.value!!)
                sum += s.Price * RealEstateConstants.profitFactor
        }
        playerModel.summaryOwnedRealEstateProfit.value = sum
    }

    private fun setAppropriateSummaryProfitFontSize(num: Double) {
        when (num) {
            in 0.0..10000000.0 ->
                binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
            in 10000000.0..1000000000.0 ->
                binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            in 1000000000.0..100000000000.0 ->
                binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            in 100000000000.0..10000000000000.0 ->
                binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            else -> binding.tvSummaryProfit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
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