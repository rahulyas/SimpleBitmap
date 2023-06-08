package com.apogee.customgui

import android.graphics.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    lateinit var bmp: Bitmap
    lateinit var operation: Bitmap
    lateinit var canvas : Canvas
    var width = 0
    var height = 0
    // this is new pixel we get after Calculation
    var xpixel = ArrayList<Int>()
    var ypixel = ArrayList<Int>()
    lateinit var imageview : ImageView
    // in this we get the point of x and y coordinates from .land xml file
    var referValueX = ArrayList<Double>()
    var referValueY = ArrayList<Double>()
    val pointName : ArrayList<String> = ArrayList()
    ////////////////////////////////

    var isFirstReference = true
    var referenceX = 0.0
    var referenceY = 0.0
    var isScaleSetFirstTime = true
    var minX  = 0.0
    var minY = 0.0
    var maxX = 0.0
    var maxY = 0.0
    var isReset = false
    var diffRefereX = 0.0
    var diffRefereY = 0.0
    var PXmin = 0.0
    var PYmin = 0.0
    var PXmax = 0.0
    var PYmax = 0.0
    var factorX = 0.0
    var factorY = 0.0
    var factorXY = 0.0
    var diffXY = 0.0

    var isForFirstPoint = true


    fun intiliaze(imageView: ImageView, context: AppCompatActivity){
        this.imageview = imageView
        val display = context.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        width = size.x
        height = size.y //700,800
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    }

    fun addPoints(x : ArrayList<Double>, y : ArrayList<Double> , name : ArrayList<String>): String{
        if (isFirstReference) {
            isFirstReference = false
            referenceX = x[0]
            referenceY = y[0]
        }
        referValueX.addAll(x)
        referValueY.addAll(y)
        pointName.addAll(name)
        if(isScaleSetFirstTime || isReset){
            minX = Collections.min(referValueX)
            minY = Collections.min(referValueY)
            maxX = Collections.max(referValueX)
            maxY = Collections.max(referValueY)
            diffRefereX = abs(maxX - minX)
            diffRefereY = abs(maxY - minY)

            val diffXY : Double
            diffXY = Math.max(diffRefereX, diffRefereY)

            val Xs: Double
            val Ys: Double
            Xs = diffRefereX
            Ys = diffRefereY
            PXmin = minX - diffRefereX / 4
            PYmin = minY - diffRefereY / 4
            PXmax = maxX + diffRefereX / 4
            PYmax = maxY + diffRefereY / 4

//            factorX = width / Xs
//            factorY = height / Ys

            factorXY = width / diffXY

        }

        var plotX: Double
        var plotY: Double
        if (referValueX.size > 1) {
            xpixel.clear()
            ypixel.clear()
            for (i in 0 until referValueX.size) {
                plotX = (referValueX[i] - minX) * factorXY
                if(plotX == 0.0){
                    plotX += 10  // if point is plot at 0 xaxis, 10 pixel will be added
                }else if(plotX == bmp.width.toDouble()){
                    plotX -= 10 // if point is plot at heighest xaxis, 10 pixel will be deduct
                }
                xpixel.add(plotX.toInt())
            }
            for (i in 0 until referValueY.size) {
                plotY = (referValueY[i] - minY) * factorXY
                plotY = height - plotY
                if(plotY == 0.0){
                    plotY += 10  // if point is plot at 0 yaxis, 10 pixel will be added
                }else if(plotY == bmp.height.toDouble()){
                    plotY -= 10 // if point is plot at heighest yaxis, 10 pixel will be deduct
                }
                ypixel.add(plotY.toInt())
            }
        }
        pointplotOnBitmap()
//        return factorX.toString()+","+factorY.toString()
        return factorXY.toString()
    }

    fun clearpoints(){
        xpixel.clear()
        ypixel.clear()
        pointplotOnBitmap()
    }

    fun pointplotOnBitmap(){
        operation = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
        val paint = Paint()
        canvas = Canvas(operation)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        if (xpixel.size != 0 && ypixel.size != 0) {
            for (k in xpixel.indices) {
                if (k == xpixel.size - 1) {
                    paint.color = Color.GREEN
                    paint.strokeWidth = 5f
                    canvas.drawBitmap(operation, Matrix(), null)
                    canvas.drawCircle(xpixel[k].toFloat(), ypixel[k].toFloat(), 10f, paint)
                    val textpaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    // text color - #3D3D3D
                    textpaint.color = Color.rgb(110, 110, 110)
                    // text size in pixels
                    textpaint.textSize = 16f
                    // text shadow
                    //    paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)
                    canvas.drawText(pointName[k],xpixel[k].toFloat(),ypixel[k].toFloat(),textpaint)
                } else {
                    paint.color = Color.BLACK
                    paint.strokeWidth = 5f
                    canvas.drawBitmap(operation, Matrix(), null)
                    canvas.drawCircle(xpixel[k].toFloat(), ypixel[k].toFloat(), 10f, paint)
                    val textpaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    // text color - #3D3D3D
                    textpaint.color = Color.rgb(110, 110, 110)
                    // text size in pixels
                    textpaint.textSize = 16f
                    // text shadow
                    //  paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)
                    canvas.drawText(pointName[k],xpixel[k].toFloat(),ypixel[k].toFloat(),textpaint)
                }
            }
        }
        else if(pointName.size > 0) {
            paint.color = Color.BLACK
            paint.strokeWidth = 5f
            canvas.drawBitmap(operation, Matrix(), null)
            canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), 8f, paint)
            val textpaint = Paint(Paint.ANTI_ALIAS_FLAG)
            // text color - #3D3D3D
            textpaint.color = Color.rgb(110, 110, 110)
            // text size in pixels
            textpaint.textSize = 16f
            // text shadow
            //  paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)
            canvas.drawText(pointName[0],(width/2).toFloat(),(height/2).toFloat(),textpaint)
        }
        imageview.setImageBitmap(operation)
    }

    fun pointZoomplot(factorXY : Double){
        var plotX: Double
        var plotY: Double
        if (referValueX.size > 1) {
            xpixel.clear()
            ypixel.clear()
            for (i in 0 until referValueX.size) {
                plotX = (referValueX[i] - minX) * factorXY
                if(plotX == 0.0){
                    plotX += 10
                }else if(plotX == bmp!!.width.toDouble()){
                    plotX -= 10
                }
                xpixel.add(plotX.toInt())
            }
            for (i in 0 until referValueY.size) {
                plotY = (referValueY[i] - minY) * factorXY
                plotY = height - plotY
                if(plotY == 0.0){
                    plotY += 10
                }else if(plotY == bmp!!.height.toDouble()){
                    plotY -= 10
                }
                ypixel.add(plotY.toInt())
            }
            isForFirstPoint = false
        }
        pointplotOnBitmap()

        Log.i("Tag","xpixel:= " +xpixel)
        Log.i("Tag","ypixel:= " +ypixel)


    }

    fun scrollplot(deltaX : Float, deltaY : Float): String{
        var plotX: Double
        var plotY: Double
        if (referValueX.size > 1) {
            for (i in 0 until referValueX.size) {
                referValueX[i] = referValueX[i]  + deltaX.toDouble()
            }
            for (i in 0 until referValueY.size) {
                referValueY[i] = referValueY[i] + deltaY.toDouble()
            }
        }


        if (referValueX.size > 1) {
            xpixel.clear()
            ypixel.clear()
            for (i in 0 until referValueX.size) {
                plotX = (referValueX[i] - minX) * factorX
                if(plotX == 0.0){
                    plotX += 10
                }else if(plotX == bmp!!.width.toDouble()){
                    plotX -= 10
                }
                xpixel.add(plotX.toInt())
            }
            for (i in 0 until referValueY.size) {
                plotY = (referValueY[i] - minY) * factorY
                plotY = height - plotY
                if(plotY == 0.0){
                    plotY += 10
                }else if(plotY == bmp!!.height.toDouble()){
                    plotY -= 10
                }
                ypixel.add(plotY.toInt())
            }
            isForFirstPoint = false
        }
        pointplotOnBitmap()
        return deltaX.toString()+","+deltaY.toString()
    }

}