package com.ttterbagames.businesssimulator

object StockParams {
    const val BOUNDS = 0.2 // Динамика цен в пределах 20 %
    const val STEP = 0.013 //Макс. рост цены за один период
    //const val UPDATE_PERIOD: Long = 1000 * 60 * 2 // 2 минуты
    const val UPDATE_PERIOD: Long = 1000 * 50 // 1 минута
    const val STACK_SIZE: Int = 130

    val colors = mapOf("A" to "#34495e", "B" to "#4834d4", "C" to "#f9ca24", "D" to "#c0392b",
        "E" to "#e74c3c", "F" to "#d35400", "G" to "#f39c12", "H" to "#16a085",
        "I" to "#27ae60", "J" to "#2980b9", "K" to "#8e44ad", "L" to "#9b59b6",
        "M" to "#1abc9c", "N" to "#273c75", "O" to "#192a56", "P" to "#40739e",
        "Q" to "#44bd32", "R" to "#c44569", "S" to "#e15f41", "T" to "#574b90",
        "U" to "#58B19F", "V" to "#B33771", "W" to "#2C3A47", "X" to "#227093",
        "Y" to "#58B19F", "Z" to "#F97F51",
        "0" to "#34495e", "1" to "#95a5a6", "2" to "#7f8c8d", "3" to "#c0392b",
        "4" to "#e74c3c", "5" to "#d35400", "6" to "#f39c12", "7" to "#16a085",
        "8" to "#27ae60", "9" to "#2980b9")
}