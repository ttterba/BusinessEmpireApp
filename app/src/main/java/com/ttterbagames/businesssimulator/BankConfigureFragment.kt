package com.ttterbagames.businesssimulator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.ttterbagames.businesssimulator.databinding.FragmentBankConfigureBinding


class BankConfigureFragment(var bank: BusinessBank) : Fragment() {

    lateinit var binding: FragmentBankConfigureBinding

    var creditPercent = 0.0
    var depositPercent = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentBankConfigureBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        creditPercent = bank.creditPercent.value!!
        depositPercent = bank.depositPercent.value!!

        setSeekBarsMaxValues()

        initStaticViews()

        setSeekBarsListeners()

        setButtonListeners()
    }

    private fun initStaticViews() {
        binding.apply {
            tvDepositPercent.text = Strings.get(R.string.bank_percent, bank.depositPercent.value!!)
            tvCreditPercent.text = Strings.get(R.string.bank_percent, bank.creditPercent.value!!)

            sbDepositPercent.progress = (bank.depositPercent.value!! * 100 - bank.minDepositPercent * 100).toInt()
            sbCreditPercent.progress = (bank.creditPercent.value!! * 100 - bank.minCreditPercent * 100).toInt()
        }
    }

    private fun setSeekBarsListeners() {
        binding.apply {

            sbDepositPercent.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    depositPercent = progress.toDouble() / 100 + bank.minDepositPercent
                    tvDepositPercent.text = Strings.get(R.string.bank_percent, depositPercent)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })

            sbCreditPercent.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    creditPercent = progress.toDouble() / 100 + bank.minCreditPercent
                    tvCreditPercent.text = Strings.get(R.string.bank_percent, creditPercent)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })

        }
    }

    private fun setSeekBarsMaxValues() {

        binding.apply {
            sbDepositPercent.max = (bank.maxDepositPercent * 100 - bank.minDepositPercent * 100).toInt()
            sbCreditPercent.max = (bank.maxCreditPercent * 100 - bank.minCreditPercent * 100).toInt()
        }
    }

    private fun setButtonListeners() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnApply.setOnClickListener {
            bank.depositPercent.postValue(depositPercent)
            bank.creditPercent.postValue(creditPercent)

            bank.dataBaseHelper.setBankPercents(bank.id, creditPercent, depositPercent)

            requireActivity().supportFragmentManager.popBackStack()
        }
    }

}