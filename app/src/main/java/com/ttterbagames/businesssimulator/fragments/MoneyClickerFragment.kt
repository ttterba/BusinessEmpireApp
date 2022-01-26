package com.ttterbagames.businesssimulator.fragments

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.ttterbagames.businesssimulator.*
import com.ttterbagames.businesssimulator.databinding.FragmentMoneyClickerBinding


class MoneyClickerFragment : Fragment() {
    private val playerModel: PlayerModel by activityViewModels()
    lateinit var binding: FragmentMoneyClickerBinding

    lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBaseHelper = DataBaseHelper(requireContext())

        binding = FragmentMoneyClickerBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (playerModel.payPerClick.value!! >= ClickerParams.MAX_PAY_PER_CLICK) {
            binding.btnLevelUp.visibility = View.GONE
            binding.progressWrapper.visibility = View.GONE

            playerModel.payPerClick.value = ClickerParams.MAX_PAY_PER_CLICK
            dataBaseHelper.setPayPerClick(ClickerParams.MAX_PAY_PER_CLICK)
        }

        binding.btnMoneyClick.setOnClickListener{
            playerModel.balance.value = playerModel.balance.value?.plus(playerModel.payPerClick.value!!)
            playerModel.earnedInClicker += playerModel.payPerClick.value!!

            dataBaseHelper.setEarnedInClicker(playerModel.earnedInClicker)
        }

        binding.btnLevelUp.setOnClickListener {
            playerModel.apply {
                if (balance.value!!.toDouble() >= toLevelUp.value!!.toDouble()) {
                    balance.value = balance.value!! - toLevelUp.value!!
                    level.value = level.value?.plus(1)
                }
            }
        }

        playerModel.balance.observe(activity as LifecycleOwner, {

            binding.tvBalance.text = context?.getString(R.string.money, it)

            if (playerModel.payPerClick.value!! >= ClickerParams.MAX_PAY_PER_CLICK) {
                binding.btnLevelUp.visibility = View.GONE
                binding.progressWrapper.visibility = View.GONE
            }
            else if (playerModel.balance.value!! >= playerModel.toLevelUp.value!!) {
                binding.btnLevelUp.text = Strings.get(R.string.get_new_level_signed, Utils.convertMoneyToText(playerModel.toLevelUp.value!!))
                binding.btnLevelUp.visibility = View.VISIBLE
                binding.progressWrapper.visibility = View.INVISIBLE
            }
            else {
                binding.btnLevelUp.visibility = View.INVISIBLE
                binding.progressWrapper.visibility = View.VISIBLE
            }

            setAppropriateFontSize(playerModel.balance.value!!)

            updateProgressBar()
        })

        playerModel.level.observe(activity as LifecycleOwner, {

            if (playerModel.payPerClick.value!! >= ClickerParams.MAX_PAY_PER_CLICK) {
                binding.btnLevelUp.visibility = View.GONE
                binding.progressWrapper.visibility = View.GONE

                playerModel.payPerClick.value = ClickerParams.MAX_PAY_PER_CLICK
                dataBaseHelper.setPayPerClick(ClickerParams.MAX_PAY_PER_CLICK)
            } else {
                binding.tvCurrLevel.text = it.toString()
                binding.tvNextLevel.text = (it+1).toString()

                if (it > dataBaseHelper.getClickerLevel()) {
                    if (it > ClickerParams.BREAKPOINT_LEVEL)
                        playerModel.payPerClick.value = playerModel.payPerClick.value!! * ClickerParams.PAY_GROW_AFTER_BREAKPOINT
                    else
                        playerModel.payPerClick.value = playerModel.payPerClick.value!! * ClickerParams.PAY_GROW
                }

                playerModel.toLevelUp.value = playerModel.payPerClick.value?.times(ClickerParams.CLICKS_TO_LEVEL_UP)

                updateProgressBar()

                dataBaseHelper.setClickerLevel(it)
            }

        })

        playerModel.toLevelUp.observe(activity as LifecycleOwner, {
            binding.tvToLevelUp.text = Strings.get(R.string.money, it)
            binding.btnLevelUp.text = Strings.get(R.string.get_new_level_signed, Utils.convertMoneyToText(playerModel.toLevelUp.value!!))
            dataBaseHelper.setClickerToLevelUp(it)
        })

        playerModel.payPerClick.observe(activity as LifecycleOwner, {
            binding.tvPayPerClick.text = Utils.convertMoneyToText(it)
            dataBaseHelper.setPayPerClick(it)

            if (it > ClickerParams.MAX_PAY_PER_CLICK) {
                binding.btnLevelUp.visibility = View.GONE
                binding.progressWrapper.visibility = View.GONE

                playerModel.payPerClick.value = ClickerParams.MAX_PAY_PER_CLICK
                dataBaseHelper.setPayPerClick(ClickerParams.MAX_PAY_PER_CLICK)
            }
        })

        setAppropriateFontSize(playerModel.balance.value!!)
    }


    fun setAppropriateFontSize(num: Double) {
        when (num) {
            in 0.0..10000000.0 ->
                binding.tvBalance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
            in 10000000.0..1000000000.0 ->
                binding.tvBalance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            in 1000000000.0..100000000000.0 ->
                binding.tvBalance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
            in 100000000000.0..10000000000000.0 ->
                binding.tvBalance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            else -> binding.tvBalance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        }
    }


    private fun updateProgressBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.levelProgressBar.setProgress(
                (playerModel.balance.value!! / playerModel.toLevelUp.value!! * 100).toInt(), true)
        } else {
            binding.levelProgressBar.progress = (playerModel.balance.value!! / playerModel.toLevelUp.value!! * 100).toInt()
        }
    }
}