package com.ttterbagames.businesssimulator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import java.util.ArrayDeque

class CustomGraphView : View {

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr:Int) : super(context, attrs, defStyleAttr) {}

    private var linePaint: Paint? = null
    private var fillPaint: Paint? = null

    private var values = ArrayList<Double>()
    private var convertedValues = ArrayList<Double>()

    private var linePath: Path? = null
    private var fillPath: Path? = null
    private var testPath: Path? = null

    var minY = 0.0
    var maxY = 0.0

    init {
        linePaint = Paint()
        linePaint?.color = Color.parseColor("#2C7DFE")
        linePaint?.strokeWidth = 3f
        linePaint?.flags = Paint.ANTI_ALIAS_FLAG
        linePaint?.style = Paint.Style.STROKE

        fillPaint = Paint()
        fillPaint?.color = Color.argb(30,44,125,254)
        fillPaint?.flags = Paint.ANTI_ALIAS_FLAG

        linePath = Path()
        fillPath = Path()
        testPath = Path()
    }

    override fun onDraw(canvas: Canvas?) {

        convertedValues.clear()

        val scaleY = this.height.toDouble() / (maxY - minY)
        for (v in values) {
            val y = (maxY - v) * scaleY
            convertedValues.add(y)
        }

        linePath!!.reset()
        linePath!!.moveTo(0f, convertedValues[0].toFloat())

        fillPath!!.reset()
        fillPath!!.moveTo(0f, this.height.toFloat())
        fillPath!!.lineTo(0f, convertedValues[0].toFloat())

        var i = 1
        while (i < StockParams.STACK_SIZE) {
            linePath!!.lineTo((this.width.toDouble() * (i.toDouble() / (StockParams.STACK_SIZE - 1))).toFloat(), convertedValues[i].toFloat())
            fillPath!!.lineTo((this.width.toDouble() * (i.toDouble() / (StockParams.STACK_SIZE - 1))).toFloat(), convertedValues[i].toFloat())
            i++
        }
        fillPath!!.lineTo(this.width.toFloat(), this.height.toFloat())
        fillPath!!.lineTo(0f, this.height.toFloat())

        testPath!!.moveTo(0f, this.height.toFloat())
        testPath!!.lineTo(0f, this.height.toFloat() - 100f)
        testPath!!.lineTo(this.width.toFloat(), this.height.toFloat() - 100f)
        testPath!!.lineTo(this.width.toFloat(), this.height.toFloat())

        canvas?.drawPath(fillPath!!, fillPaint!!)
        //canvas?.drawPath(testPath!!, fillPaint!!)
        canvas?.drawPath(linePath!!, linePaint!!)

    }


    fun drawGraph(ad: ArrayDeque<Double>) {
        values.clear()
        convertedValues.clear()

        values.addAll(ad)
        values.reverse()

        minY = values.minOrNull()!! / 1.06
        maxY = values.maxOrNull()!! * 1.06
    }
}