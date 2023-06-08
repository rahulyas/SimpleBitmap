package com.apogee.customgui

import android.graphics.*
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.util.stream.Collectors
import kotlin.time.measureTime

class NewMainActivity : AppCompatActivity() {

    lateinit var imageview : ImageView
    var width = 0
    var height = 0
    lateinit var bmp: Bitmap
    lateinit var bitmap: Bitmap
    lateinit var canvas : Canvas
    var paint = Paint()
    var textpaint = Paint()
    val line = Paint()
    // in this we get the point of x and y coordinates from .land xml file
    var referValueX = ArrayList<Double>()
    var referValueY = ArrayList<Double>()

    var plotValueX = ArrayList<Double>()
    var plotValueY = ArrayList<Double>()
    val pointName : ArrayList<String> = ArrayList()
    var minX = 0.0
    var maxX = 0.0
    var minY = 0.0
    var maxY = 0.0
    var differenceX = 0.0
    var differenceY = 0.0
    var differenceXY = 0.0
    var meanofX = 0.0
    var meanofY = 0.0
    var scaleXY = 0.0
    var diffX = 0.0
    var diffY = 0.0
    var deltaX : Float = 0f
    var deltaY : Float = 0f
    var pixelofX = ArrayList<Double>()
    var pixelofY = ArrayList<Double>()

    fun intiliaze(imageView: ImageView, context: AppCompatActivity){
        this.imageview = imageView
        val display = context.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        width = size.x
        height = size.y
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    fun addPoints(x : ArrayList<Double>, y : ArrayList<Double> , name : ArrayList<String>){
        referValueX.addAll(x)
        referValueY.addAll(y)
        pointName.addAll(name)

        minX = Collections.min(referValueX)
        maxX = Collections.max(referValueX)
        minY = Collections.min(referValueY)
        maxY = Collections.max(referValueY)
        differenceX = maxX - minX
        differenceY = maxY - minY
        differenceXY = Math.max(differenceX, differenceY)
        scaleXY = (width / differenceXY)
        Log.d("TAG", "Firstscale: $scaleXY")
        meanofX = (minX + maxX) / 2
        meanofY = (minY + maxY) / 2
        getPixel()
    }

    fun getPixel(): String {
        bitmap = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
        canvas = Canvas(bitmap)
        var plotX: Double
        var plotY: Double

        plotValueX = ArrayList(referValueX)
        plotValueY = ArrayList(referValueY)

        if (plotValueX.size > 1) {
            for (i in plotValueX.indices) {
                val value: Double = plotValueX[i] + diffX + deltaX
                plotValueX[i] = value
            }
            for (i in plotValueY.indices) {
                val value: Double = plotValueY[i] + diffY + deltaY
                plotValueY[i] = value
            }
            pixelofX.clear()
            pixelofY.clear()
            for (i in plotValueX.indices) {
                plotX = if (plotValueX[i] > meanofX) {
                    width / 2 + Math.abs(meanofX - plotValueX[i]) * scaleXY
                } else {
                    width / 2 - Math.abs(meanofX - plotValueX[i]) * scaleXY
                }
                pixelofX.add(plotX)

                plotY = if (plotValueY[i] > meanofY) {
                    height / 2 + Math.abs(meanofY - plotValueY[i]) * scaleXY
                } else {
                    height / 2 - Math.abs(meanofY - plotValueY[i]) * scaleXY
                }
                plotY = height - plotY
                pixelofY.add(plotY)
////////////////////////////////
                paint = Paint()
                paint.color = Color.RED
                paint.strokeWidth = 5f
                canvas.drawCircle(plotX.toFloat(), plotY.toFloat(), 5f, paint)
                imageview.setImageBitmap(bitmap)
            }


        }
//        drawpixelpoint()
        return scaleXY.toString()
    }

    fun zoomgetPixel(scaleXY : Double): String {
        var plotX: Double
        var plotY: Double

        val plotValueX = ArrayList(referValueX)
        val plotValueY = ArrayList(referValueY)

        if (plotValueX.size > 1) {
            for (i in plotValueX.indices) {
                val value: Double = plotValueX[i] + diffX + deltaX
                plotValueX[i] = value
            }
            for (i in plotValueY.indices) {
                val value: Double = plotValueY[i] + diffY + deltaY
                plotValueY[i] = value
            }
            pixelofX.clear()
            pixelofY.clear()
            for (i in plotValueX.indices) {
                plotX = if (plotValueX[i] > meanofX) {
                    width / 2 + Math.abs(meanofX - plotValueX[i]) * scaleXY
                } else {
                    width / 2 - Math.abs(meanofX - plotValueX[i]) * scaleXY
                }
                pixelofX.add(plotX)
            }
            for (i in plotValueY.indices) {
                plotY = if (plotValueY[i] > meanofY) {
                    height / 2 + Math.abs(meanofY - plotValueY[i]) * scaleXY
                } else {
                    height / 2 - Math.abs(meanofY - plotValueY[i]) * scaleXY
                }
                plotY = height - plotY
                pixelofY.add(plotY)
            }
        }
        drawpixelpoint()
        return scaleXY.toString()
    }

    fun scrollgetPixel(scaleXY : Double,deltaX : Float,deltaY : Float): String {
        bitmap = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
        canvas = Canvas(bitmap)
        var plotX: Double
        var plotY: Double
        pixelofX.clear()
        pixelofY.clear()
        plotValueX = ArrayList(referValueX)
        plotValueY = ArrayList(referValueY)

        if (plotValueX.size > 1) {
            plotValueX.replaceAll { num -> num + deltaX }
            plotValueY.replaceAll { num -> num + deltaY }

/*            val modifiedNumbersofX: MutableList<Double> = plotValueX.stream()
                .map { number -> width / 2 + (if (number > meanofX) 1 else -1) * Math.abs(meanofX - number) * scaleXY }
                .collect(Collectors.toList())
            pixelofX.addAll(modifiedNumbersofX)

            val modifiedNumbersofY: MutableList<Double> = plotValueY.stream()
                .map { number -> width / 2 + (if (number > meanofY) 1 else -1) * Math.abs(meanofY - number) * scaleXY }
                .collect(Collectors.toList())
            modifiedNumbersofY.replaceAll { num -> height - num}
            pixelofY.addAll(modifiedNumbersofY)*/

            repeat(plotValueX.size) { i ->
                plotX = width / 2 + (if (plotValueX[i] > meanofX) 1 else -1) * Math.abs(meanofX - plotValueX[i]) * scaleXY
                pixelofX.add(plotX)

                ////////////////////////////////////////////////////////////////
                plotY = width / 2 + (if (plotValueY[i] > meanofY) 1 else -1) * Math.abs(meanofY - plotValueY[i]) * scaleXY
                val plotYFlipped = height - plotY
                pixelofY.add(plotYFlipped)

                paint = Paint()
                paint.color = Color.RED
                paint.strokeWidth = 5f
                canvas.drawCircle(plotX.toFloat(), plotYFlipped.toFloat(), 5f, paint)
                imageview.setImageBitmap(bitmap)
            }
        }
//        drawpixelpoint()
        return scaleXY.toString()
    }

    fun drawpixelpoint() {

            bitmap = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
            canvas = Canvas(bitmap)
            var j = 0
        while (j < pixelofX.size && j < pixelofY.size) {
            paint = Paint()
            textpaint = Paint()
            paint.color = Color.RED
            textpaint.color = Color.BLACK
            canvas.drawCircle(pixelofX[j].toFloat(), pixelofY[j].toFloat(), 5f, paint)
            canvas.drawText(pointName[j],pixelofX[j].toFloat(), pixelofY[j].toFloat(), textpaint)
            imageview.setImageBitmap(bitmap)
            j++
        }


    }

    fun clearpoints(){
        referValueX.clear()
        referValueY.clear()
        pointName.clear()
        pixelofX.clear()
        pixelofY.clear()
        bitmap = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
        canvas = Canvas(bitmap)
        paint.color = Color.TRANSPARENT
        imageview.setImageBitmap(bitmap)
    }
}