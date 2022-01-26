package com.ttterbagames.businesssimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.ttterbagames.businesssimulator.databinding.FragmentCreateBusinessBinding


class CreateBusinessFragment : Fragment() {

    lateinit var binding: FragmentCreateBusinessBinding
    lateinit var bnv: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreateBusinessBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bnv = activity?.findViewById<View>(R.id.bottom_navigation)!!
        bnv.visibility = View.GONE

        binding.apply {
            optionCardShop.setOnClickListener {
                openChooseShopTypeFragment()
            }
            optionCardFactory.setOnClickListener {
                openChooseFactoryTypeFragment()
            }
            optionCardTaxiStation.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(BusinessConstants.BUSINESS_TYPE_KEY, BusinessConstants.TAXI_STATION_ID)
                openFinalStageFragment(bundle)
            }
            optionCardTransportationCompany.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(BusinessConstants.BUSINESS_TYPE_KEY, BusinessConstants.TRANSPORTATION_COMPANY_ID)
                openFinalStageFragment(bundle)
            }
            optionCardBuildingCompany.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(BusinessConstants.BUSINESS_TYPE_KEY, BusinessConstants.BUILDING_COMPANY_ID)
                openFinalStageFragment(bundle)
            }
            optionCardBank.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt(BusinessConstants.BUSINESS_TYPE_KEY, BusinessConstants.BANK_ID)
                openFinalStageFragment(bundle)
            }

            cbcBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        bnv.visibility = View.VISIBLE
    }

    fun openChooseShopTypeFragment() {
        val cstf = ChooseShopTypeFragment()
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fl_wrapper, cstf)
            addToBackStack(null)
        }
    }

    fun openChooseFactoryTypeFragment() {
        val cftf = ChooseFactoryTypeFragment()
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fl_wrapper, cftf)
            addToBackStack(null)
        }
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