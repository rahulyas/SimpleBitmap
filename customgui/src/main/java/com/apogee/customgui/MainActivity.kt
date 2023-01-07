package com.apogee.customgui

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    var width = 0
    var height = 0
    lateinit var bmp: Bitmap
    lateinit var operation: Bitmap
    lateinit var canvas : Canvas
    var xpixel = ArrayList<Float>()
    var ypixel = ArrayList<Float>()
    val pointName : ArrayList<String> = ArrayList()
    lateinit var imageview : ImageView

    fun intiliaze(imageView: ImageView, context: AppCompatActivity){
        this.imageview = imageView
        val display = context.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        width = size.x
        height = size.y //700,800

        //  width = 700
        //  height = 900
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    fun addPoint(x: Float, y: Float, name: String){
        xpixel.add(x)
        ypixel.add(y)
        pointName.add(name)
        pointplotOnBitmap()

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
        imageview.setImageBitmap(operation)
    }
}