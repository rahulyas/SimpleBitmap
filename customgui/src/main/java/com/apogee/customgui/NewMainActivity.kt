package com.apogee.customgui

import android.graphics.*
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

import android.widget.ImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class NewMainActivity : AppCompatActivity() {

    lateinit var imageview : ImageView
    var width = 0
    var height = 0
    lateinit var bmp: Bitmap
    lateinit var operation: Bitmap

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

    var isFirstReference = true
    var referenceX = 0.0
    var referenceY = 0.0

    val codeNameList : ArrayList<String> = ArrayList()
    val prefixNameList:ArrayList<String> =ArrayList()
    val misc1List: ArrayList<String> = ArrayList()
    val misc2List: ArrayList<String> = ArrayList()

    var isZoomtofit = false
    var isScroll = false

    var minX = 0.0
    var minY = 0.0
    var maxX = 0.0
    var maxY = 0.0
    var diffRefereX = 0.0
    var diffRefereY = 0.0

    lateinit var paint: Paint
    lateinit var canvas: Canvas
    lateinit var textpaint: Paint
    val dotted = Paint()
    val line = Paint()
    val plPaint = Paint()
    val pgPaint = Paint()
    val cr1Paint = Paint()
    val arcPaint = Paint()

    fun intiliaze(imageView: ImageView, context: AppCompatActivity){
        this.imageview = imageView
        val display = context.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        width = size.x
        height = size.y

        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    }

//    private fun pointplot(Easting: Double, Northing: Double, finalpoint: String, prefix: String, code : String, misc1 : String, misc2 : String) {
//        val value1 = Easting
//        val value2 = Northing
//        if (isFirstReference) {
//            isFirstReference = false
//            referenceX = value1
//            referenceY = value2
//        }
//        if (pointName.contains(currentPosition) ){
//            val currIndex = pointName.indexOf(currentPosition)
//            pointName.removeAt(currIndex)
//            codeNameList.removeAt(currIndex)
//            prefixNameList.removeAt(currIndex)
//            referValueX.removeAt(currIndex)
//            referValueY.removeAt(currIndex)
//            misc1List.removeAt(currIndex)
//            misc2List.removeAt(currIndex)
//            referValueX.add(Easting)
//            referValueY.add(Northing)
//            pointName.add(finalpoint)
//            prefixNameList.add(prefix)
//            codeNameList.add(code)
//            misc1List.add(misc1)
//            misc2List.add(misc2)
//        } else {
//            if(!pointName.contains(finalpoint))
//            {
//                referValueX.add(value1)
//                referValueY.add(value2)
//                pointName.add(finalpoint)
//                prefixNameList.add(prefix)
//                codeNameList.add(code)
//                misc1List.add(misc1)
//                misc2List.add(misc2)
//
//            }
//        }
//        var diffXY = 0.0
//
//        if(referValueX.size < 3 || isZoomtofit){
//            minX = Collections.min(referValueX)
//            minY = Collections.min(referValueY)
//            maxX = Collections.max(referValueX)
//            maxY = Collections.max(referValueY)
//            diffRefereX = abs(maxX - minX)
//            diffRefereY = abs(maxY - minY)
//
//            meanX = (minX + maxX) / 2
//            meanY = (minY + maxY) / 2
//            diffXY = Math.max(diffRefereX, diffRefereY)
//        }
//
//        if(isZoomtofit){
//            isZoomtofit = false
//            deltaX = 0f
//            deltaY = 0f
//            diffX = 0.0
//            diffY = 0.0
//            factorXY = width / diffXY
//        }else if(!isScroll){
//            factorXY = width / 10.0
//
//        }
//        getPixelPoint()
//
//
//    }
    fun pointplot(Easting: ArrayList<Double>, Northing: ArrayList<Double>, finalpoint: ArrayList<String>, prefix: ArrayList<String>, code : ArrayList<String>, misc1 : ArrayList<String>, misc2 : ArrayList<String>) :String{
//        val value1 = Easting
//        val value2 = Northing
        if (isFirstReference) {
            isFirstReference = false
            referenceX = Easting[0]
            referenceY = Northing[0]
        }
        if (pointName.contains(currentPosition) ){
            val currIndex = pointName.indexOf(currentPosition)
            pointName.removeAt(currIndex)
            codeNameList.removeAt(currIndex)
            prefixNameList.removeAt(currIndex)
            referValueX.removeAt(currIndex)
            referValueY.removeAt(currIndex)
            misc1List.removeAt(currIndex)
            misc2List.removeAt(currIndex)
            referValueX.addAll(Easting)
            referValueY.addAll(Northing)
            pointName.addAll(finalpoint)
            prefixNameList.addAll(prefix)
            codeNameList.addAll(code)
            misc1List.addAll(misc1)
            misc2List.addAll(misc2)
        } else {
            if(!pointName.contains(finalpoint[0]))
            {
                referValueX.addAll(Easting)
                referValueY.addAll(Northing)
                pointName.addAll(finalpoint)
                prefixNameList.addAll(prefix)
                codeNameList.addAll(code)
                misc1List.addAll(misc1)
                misc2List.addAll(misc2)

            }
        }
        var diffXY = 0.0

        if(referValueX.size < 3 || isZoomtofit){
            minX = Collections.min(referValueX)
            minY = Collections.min(referValueY)
            maxX = Collections.max(referValueX)
            maxY = Collections.max(referValueY)
            diffRefereX = abs(maxX - minX)
            diffRefereY = abs(maxY - minY)

            meanX = (minX + maxX) / 2
            meanY = (minY + maxY) / 2
            diffXY = Math.max(diffRefereX, diffRefereY)
        }

        if(isZoomtofit){
            isZoomtofit = false
            deltaX = 0f
            deltaY = 0f
            diffX = 0.0
            diffY = 0.0
            factorXY = width / diffXY
        }else if(!isScroll){
            factorXY = width / 10.0

        }
        getPixelPoint()
        pointplotOnBitmap()

    return factorXY.toString()

    }
    fun clearpoints(){
        xpixel.clear()
        ypixel.clear()
        getPixelPoint()
    }

    fun getPixelPoint() {
        var plotX: Double
        var plotY: Double
        val plotValueX : ArrayList<Double> = ArrayList()
        val plotValueY : ArrayList<Double> = ArrayList()
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

                            deltaX = 0f
                            deltaY = 0f
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

    fun canvasdraw() {
        paint = Paint()
        canvas = Canvas(bmp!!)
        paint.strokeWidth = 5f
        textpaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textpaint.color = Color.rgb(110, 110, 110)
        textpaint.textSize = 16f


        dotted.color = Color.BLUE
        dotted.strokeWidth = 2f
        dotted.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 5f)
        dotted.isAntiAlias=true

        line.color = Color.BLACK
        line.strokeWidth = 3f
        line.isAntiAlias=true

        plPaint.color = Color.BLACK
        plPaint.strokeWidth = 3f
        plPaint.isAntiAlias=true

        pgPaint.color = Color.BLACK
        pgPaint.strokeWidth = 3f
        pgPaint.isAntiAlias=true

        cr1Paint.color = Color.BLACK
        cr1Paint.strokeWidth = 3f
        cr1Paint.style = Paint.Style.STROKE
        cr1Paint.isAntiAlias=true

        arcPaint.color = Color.RED
        arcPaint.style = Paint.Style.STROKE
        arcPaint.strokeWidth = 3f
        arcPaint.isAntiAlias=true
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


}