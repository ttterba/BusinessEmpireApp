package com.ttterbagames.businesssimulator

class Utils {
    companion object {
        fun convertMoneyToText(num: Double): String {
            if (num < 100000)
                return Strings.get(R.string.money, num)
            else if (num < 1000000)
                return Strings.get(R.string.thousand_compressed, num / 1000)
            else if (num < 1000000000)
                return Strings.get(R.string.million_compressed, num / 1000000)
            else if (num < 1000000000000)
                return Strings.get(R.string.billion_compressed, num / 1000000000)
            else if (num > 1000000000000)
                return Strings.get(R.string.trillion_compressed, num / 1000000000000)

            return Strings.get(R.string.money, num)
        }

    }


}