package com.ttterbagames.businesssimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.ttterbagames.businesssimulator.databinding.FragmentChooseShopTypeBinding


class ChooseShopTypeFragment : Fragment() {

    lateinit var binding: FragmentChooseShopTypeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseShopTypeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        initListeners()

    }


    private fun initListeners() {
        binding.cardLevel1.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(BusinessConstants.BUSINESS_TYPE_KEY, BusinessConstants.SHOP_L1_ID)
            openFinalStageFragment(bundle)
        }
        binding.cardLevel2.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(BusinessConstants.BUSINESS_TYPE_KEY, BusinessConstants.SHOP_L2_ID)
            openFinalStageFragment(bundle)
        }
        binding.cardLevel3.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(BusinessConstants.BUSINESS_TYPE_KEY, BusinessConstants.SHOP_L3_ID)
            openFinalStageFragment(bundle)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initViews() {
        binding.tvLvl1OpenPrice.text = getString(R.string.money, BusinessConstants.SHOP_L1_OPEN_PRICE)
        binding.tvLvl2OpenPrice.text = getString(R.string.money, BusinessConstants.SHOP_L2_OPEN_PRICE)
        binding.tvLvl3OpenPrice.text = getString(R.string.money, BusinessConstants.SHOP_L3_OPEN_PRICE)
    }

    fun openFinalStageFragment(bundle: Bundle) {
        val obfs = OpenBusinessFinalStageFragment()
        obfs.arguments = bundle
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fl_wrapper, obfs)
            addToBackStack(null)
        }
    }
}