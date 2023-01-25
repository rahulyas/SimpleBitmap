package com.apogee.customgui

import android.graphics.Bitmap
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity

import android.widget.ImageView

class NewMainActivity : AppCompatActivity() {

    lateinit var imageview : ImageView
    var width = 0
    var height = 0
    lateinit var bmp: Bitmap
    var meanX = 0.0
    var meanY = 0.0
    var currentPosition = "CurrentPosition"
    var diffX = 0.0
    var diffY = 0.0
    // in this we get the point of x and y coordinates from .land xml file
    var referValueX = ArrayList<Double>()
    var referValueY = ArrayList<Double>()
    val pointName = ArrayList<String>()
    // this scale
    var factorXY = 0.0
    // this is new pixel we get after Calculation
    var xpixel = ArrayList<Int>()
    var ypixel = ArrayList<Int>()

    var isReset = true
    // this want to change
    var deltaX: Float = 0f
    var deltaY: Float = 0f

    fun intiliaze(imageView: ImageView, context: AppCompatActivity){
        this.imageview = imageView
        val display = context.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        width = size.x
        height = size.y

        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    }

    fun getPixelPoint() {
        var plotX: Double
        var plotY: Double

        val plotValueX = ArrayList<Double>()
        val plotValueY = ArrayList<Double>()

        plotValueX.addAll(referValueX)
        plotValueY.addAll(referValueY)

        if (plotValueX.size > 1) {

            val centerPointX =  ((width - 100)/2) / factorXY
            val centerPointY =  ((height - 100)/2) / factorXY

            val minbandX = meanX - centerPointX
            val maxbandX = meanX + centerPointX

            val minbandY = meanY - centerPointY
            val maxbandY = meanY + centerPointY

            var isOutsideband = 0

            for (i in 0 until  pointName.size){
                if(pointName[i] == currentPosition){
                    if(isReset){
                        if(((plotValueX[i] +diffX+deltaX))< minbandX  || ((plotValueX[i] + diffX) + deltaX)  > maxbandX   || ((plotValueY[i] + diffY) + deltaY) < minbandY  || ((plotValueY[i] + diffY) + deltaY) > maxbandY){
                            diffX = meanX - plotValueX[i]
                            diffY = meanY - plotValueY[i]
//                            deltaX = 0f
//                            deltaY = 0f
                        }
                    }else{
                        if(plotValueX[i] < minbandX  || plotValueX[i] > maxbandX   || plotValueY[i]< minbandY  || plotValueY[i] > maxbandY){
                            diffX = meanX - plotValueX[i]
                            diffY = meanY - plotValueY[i]
                        }
                    }
                }
            }

            for (i in 0 until plotValueX.size) {
                plotValueX[i] = (plotValueX[i] + diffX) + deltaX
            }

            for (i in 0 until plotValueY.size) {
                plotValueY[i] = (plotValueY[i] + diffY) + deltaY
            }

            xpixel.clear()
            ypixel.clear()

            for (i in 0 until plotValueX.size) {
                if (plotValueX[i] > meanX) {
                    plotX = (width / 2) + ((Math.abs(meanX - plotValueX[i] )) * factorXY)
                } else {
                    plotX = (width / 2) - ((Math.abs(meanX - plotValueX[i] )) * factorXY)
                }
                //  plotX = (referValueX[i] - minX) * factorXY
                xpixel.add(plotX.toInt())
            }

            for (i in 0 until plotValueY.size) {
                if (plotValueY[i] > meanY) {
                    plotY = (height / 2) + ((Math.abs(meanY - plotValueY[i] )) * factorXY)
                } else {
                    plotY = (height / 2) - ((Math.abs(meanY - plotValueY[i] )) * factorXY)
                }
                //  plotY = (referValueY[i] - minY) * factorXY
                plotY = height - plotY
                ypixel.add(plotY.toInt())
            }

        }
    }

    fun pointplotOnBitmap(){

    }
}