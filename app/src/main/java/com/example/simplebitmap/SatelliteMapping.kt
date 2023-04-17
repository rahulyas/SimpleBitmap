package com.example.simplebitmap

import android.graphics.*
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SatelliteMapping : AppCompatActivity() {
    var bitmap: Bitmap? = null
    var canvas: Canvas? = null
    var paint: Paint? = null
    var paint1: Paint? = null
    var paint2: Paint? = null
    var paint3: Paint? = null
    var paint4: Paint? = null
    var paint5: Paint? = null
    var paint6: Paint? = null
    var paint7: Paint? = null
    var paint8: Paint? = null
    var x:Int = 0
    var y:Int = 0
    var Elevation = 60
    var Azimuth = 150
    var fontsize = 0
    var imageView: ImageView? = null


    var newElevation = ArrayList<Int>()
    var newAzimuth = ArrayList<Int>()

    var numbers = intArrayOf(30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 0)
    var degress = intArrayOf(75, 60, 45, 30, 15, 0)

    var GlGSVElevation = java.util.ArrayList<Int>()
    var GLGSVAzimuth = java.util.ArrayList<Int>()
    var GLGSVPRN = java.util.ArrayList<Int>()

    var GPGSVElevation = java.util.ArrayList<Int>()
    var GPGSVAzimuth = java.util.ArrayList<Int>()
    var GPGSVPRN = java.util.ArrayList<Int>()

    var GAGSVElevation = java.util.ArrayList<Int>()
    var GAGSVAzimuth = java.util.ArrayList<Int>()
    var GAGSVPRN = java.util.ArrayList<Int>()

    var GQGSVElevation = java.util.ArrayList<Int>()
    var GQGSVAzimuth = java.util.ArrayList<Int>()
    var GQGSVPRN = java.util.ArrayList<Int>()

    var BDGSVElevation = java.util.ArrayList<Int>()
    var BDGSVAzimuth = java.util.ArrayList<Int>()
    var BDGSVPRN = java.util.ArrayList<Int>()
    var str =
        "\$GLGSV,3,1,10,81,63,034,51,82,53,272,51,80,52,292,,79,38,200,49*6A\n\$GLGSV,3,2,10,65,24,046,42,66,16,105,47,73,15,334,46,88,14,062,46*61\n\$GLGSV,3,3,10,83,11,253,,72,07,001,44*68\n\$GPGSV,4,1,16,200,04,085,53,11,76,091,,12,61,180,51,25,58,284,52*79\n\$GPGSV,4,2,16,20,42,142,50,06,33,056,47,29,28,280,47,05,19,166,47*7F\n\$GPGSV,4,3,16,31,15,321,46,19,08,093,46,04,04,025,,09,03,055,*78\n\$GPGSV,4,4,16,44,32,184,48,51,31,171,48,48,31,194,47,46,30,199,48*7E\n\$GAGSV,3,1,09,34,72,231,53,30,65,251,53,36,51,059,51,02,36,170,49*62\n\$GAGSV,3,2,09,27,25,314,47,15,19,236,47,04,08,037,46,09,04,085,*65\n\$GAGSV,3,3,09,11,03,057,*50\n\$GQGSV,1,1,01,02,08,309,37*4D\n\$BDGSV,5,1,18,34,85,015,53,11,67,274,51,12,55,069,49,43,39,265,50*61"
    lateinit var ArraysT: Array<String>
    var elevationL = java.util.ArrayList<Int>()
    var azimuthL = java.util.ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_satellite_mapping)
        // here we get the display width or height.

        // here we get the display width or height.
        val currentdisplay = windowManager.defaultDisplay
        val dw = currentdisplay.width.toFloat()
        val dh = currentdisplay.height.toFloat()
        Log.i("width", dw.toString())
        Log.i("height", dh.toString())

        // here we get the display centerX or centerY

        // here we get the display centerX or centerY
        val centerX = currentdisplay.width.toFloat() / 2
        val centerY = currentdisplay.height.toFloat() / 2
        Log.i("centerX", centerX.toString())
        Log.i("centerY", centerY.toString())

        imageView = findViewById<ImageView>(R.id.elevationbitmap)
        val GlGSV = findViewById<CheckBox>(R.id.GlGSV)
        val GPGSV = findViewById<CheckBox>(R.id.GPGSV)
        val GAGSV = findViewById<CheckBox>(R.id.GAGSV)
        val GQGSV = findViewById<CheckBox>(R.id.GQGSV)
        val BDGSV = findViewById<CheckBox>(R.id.BDGSV)

        // here we create the Bitmap on imageView

        // here we create the Bitmap on imageView
        bitmap = Bitmap.createBitmap(dw.toInt(), dh.toInt(), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        imageView!!.setImageBitmap(bitmap)

        paint = Paint()
        paint!!.color = Color.BLACK
        fontsize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15f, resources.displayMetrics).toInt()
        paint!!.textSize = fontsize.toFloat()
//////////////////////////////////////////
        paint1 = Paint()
        paint1!!.color = Color.RED
        paint1!!.style = Paint.Style.STROKE
        paint1!!.strokeWidth = 5f
//////////////////////////////////////////
        paint3 = Paint()
        paint3!!.color = Color.GREEN
        paint3!!.style = Paint.Style.STROKE
        paint3!!.strokeWidth = 8f
//////////////////////////////////////////
        paint2 = Paint()
        paint2!!.color = Color.BLACK
        val dashPath = DashPathEffect(floatArrayOf(10f, 10f), 10.0.toFloat())
        paint2!!.pathEffect = dashPath
        paint2!!.style = Paint.Style.FILL_AND_STROKE


/////////////////////////// Draw Circle Line /////////////////
        drawCircleLine(canvas!!, paint1)
////////////////////////////// This is for Draw ///////////////////
        shortthedata(str)
//////////////////////////////////////////
        GlGSV.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                paint4 = Paint()
                paint4!!.color = Color.parseColor("#FA8072")
                draw(GlGSVElevation, GLGSVAzimuth, GLGSVPRN, paint, paint4, canvas!!)
            } else {
                redraw()
                GlGSV.isChecked = false
                GPGSV.isChecked = false
                GAGSV.isChecked = false
                GQGSV.isChecked = false
                BDGSV.isChecked = false
            }
        }
        GPGSV.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                paint5 = Paint()
                paint5!!.color = Color.parseColor("#F4A460")
                draw(GPGSVElevation, GPGSVAzimuth, GPGSVPRN, paint, paint5, canvas!!)
            } else {
                redraw()
                GPGSV.isChecked = false
                GlGSV.isChecked = false
                GAGSV.isChecked = false
                GQGSV.isChecked = false
                BDGSV.isChecked = false
            }
        }
        GAGSV.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                paint6 = Paint()
                paint6!!.color = Color.parseColor("#E9967A")
                draw(GAGSVElevation, GAGSVAzimuth, GAGSVPRN, paint, paint6, canvas!!)
            } else {
                redraw()
                GAGSV.isChecked = false
                GlGSV.isChecked = false
                GPGSV.isChecked = false
                GQGSV.isChecked = false
                BDGSV.isChecked = false
            }
        }
        GQGSV.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                paint7 = Paint()
                paint7!!.color = Color.parseColor("#AFEEEE")
                draw(GQGSVElevation, GQGSVAzimuth, GQGSVPRN, paint, paint7, canvas!!)
            } else {
                redraw()
                GQGSV.isChecked = false
                GlGSV.isChecked = false
                GPGSV.isChecked = false
                GAGSV.isChecked = false
                BDGSV.isChecked = false
            }
        }
        BDGSV.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                paint8 = Paint()
                paint8!!.color = Color.parseColor("#778899")
                draw(BDGSVElevation, BDGSVAzimuth, BDGSVPRN, paint, paint8, canvas!!)
            } else {
                redraw()
                BDGSV.isChecked = false
                GlGSV.isChecked = false
                GPGSV.isChecked = false
                GAGSV.isChecked = false
                GQGSV.isChecked = false
            }
        }

////////////////////////////// Draw a Number /////////////////////////////////////
        drawnumber()
///////////////////////////// This is For Degress /////////////////////
        drawdegress()
///////////////////////////////////////////////////////////////////////


}

    fun shortthedata(str: String) {
        ArraysT = str.split("\n").toTypedArray()
        println("arraySize" + ArraysT.size.toString())
        for (i in ArraysT.indices) {
            val str1: String = ArraysT.get(i)
            if (str1.contains("\$GLGSV")) {
                val spilitdata = str1.split(",").toTypedArray()
                println("length" + spilitdata.size.toString())
                var j = 4
                while (j < spilitdata.size) {
                    val prnnumber = str1.split(",").toTypedArray()[j + 0]
                    val prnnumber1 = prnnumber.toInt()
                    println("prnnumber:=$prnnumber")
                    GLGSVPRN.add(prnnumber1)
                    val elevation = str1.split(",").toTypedArray()[j + 1]
                    val elevation1 = elevation.toInt()
                    elevationL.add(elevation1)
                    GlGSVElevation.add(elevation1)
                    println("elevation:=$elevation")
                    val azimuth = str1.split(",").toTypedArray()[j + 2]
                    println("azimuth:=$azimuth")
                    val azimuth1 = azimuth.toInt()
                    azimuthL.add(azimuth1)
                    GLGSVAzimuth.add(azimuth1)
                    val snr = str1.split(",").toTypedArray()[j + 3]
                    println("snr:=$snr")
                    j += 4
                }
            } else if (str1.contains("\$GPGSV")) {
                val spilitdata = str1.split(",").toTypedArray()
                println("length" + spilitdata.size.toString())
                var j = 4
                while (j < spilitdata.size) {
                    val prnnumber = str1.split(",").toTypedArray()[j + 0]
                    val prnnumber1 = prnnumber.toInt()
                    println("prnnumber:=$prnnumber")
                    GPGSVPRN.add(prnnumber1)
                    val elevation = str1.split(",").toTypedArray()[j + 1]
                    val elevation1 = elevation.toInt()
                    elevationL.add(elevation1)
                    GPGSVElevation.add(elevation1)
                    println("elevation:=$elevation")
                    val azimuth = str1.split(",").toTypedArray()[j + 2]
                    println("azimuth:=$azimuth")
                    val azimuth1 = azimuth.toInt()
                    azimuthL.add(azimuth1)
                    GPGSVAzimuth.add(azimuth1)
                    val snr = str1.split(",").toTypedArray()[j + 3]
                    println("snr:=$snr")
                    j += 4
                }
            } else if (str1.contains("\$GAGSV")) {
                val spilitdata = str1.split(",").toTypedArray()
                println("length" + spilitdata.size.toString())
                var j = 4
                while (j < spilitdata.size) {
                    val prnnumber = str1.split(",").toTypedArray()[j + 0]
                    val prnnumber1 = prnnumber.toInt()
                    println("prnnumber:=$prnnumber")
                    GAGSVPRN.add(prnnumber1)
                    val elevation = str1.split(",").toTypedArray()[j + 1]
                    val elevation1 = elevation.toInt()
                    elevationL.add(elevation1)
                    GAGSVElevation.add(elevation1)
                    println("elevation:=$elevation")
                    val azimuth = str1.split(",").toTypedArray()[j + 2]
                    println("azimuth:=$azimuth")
                    val azimuth1 = azimuth.toInt()
                    azimuthL.add(azimuth1)
                    GAGSVAzimuth.add(azimuth1)
                    val snr = str1.split(",").toTypedArray()[j + 3]
                    println("snr:=$snr")
                    j += 4
                }
            } else if (str1.contains("\$GQGSV")) {
                val spilitdata = str1.split(",").toTypedArray()
                println("length" + spilitdata.size.toString())
                var j = 4
                while (j < spilitdata.size) {
                    val prnnumber = str1.split(",").toTypedArray()[j + 0]
                    val prnnumber1 = prnnumber.toInt()
                    println("prnnumber:=$prnnumber")
                    GQGSVPRN.add(prnnumber1)
                    val elevation = str1.split(",").toTypedArray()[j + 1]
                    val elevation1 = elevation.toInt()
                    elevationL.add(elevation1)
                    GQGSVElevation.add(elevation1)
                    println("elevation:=$elevation")
                    val azimuth = str1.split(",").toTypedArray()[j + 2]
                    println("azimuth:=$azimuth")
                    val azimuth1 = azimuth.toInt()
                    azimuthL.add(azimuth1)
                    GQGSVAzimuth.add(azimuth1)
                    val snr = str1.split(",").toTypedArray()[j + 3]
                    println("snr:=$snr")
                    j += 4
                }
            } else if (str1.contains("\$BDGSV")) {
                val spilitdata = str1.split(",").toTypedArray()
                println("length" + spilitdata.size.toString())
                var j = 4
                while (j < spilitdata.size) {
                    val prnnumber = str1.split(",").toTypedArray()[j + 0]
                    val prnnumber1 = prnnumber.toInt()
                    println("prnnumber:=$prnnumber")
                    BDGSVPRN.add(prnnumber1)
                    val elevation = str1.split(",").toTypedArray()[j + 1]
                    val elevation1 = elevation.toInt()
                    elevationL.add(elevation1)
                    BDGSVElevation.add(elevation1)
                    println("elevation:=$elevation")
                    val azimuth = str1.split(",").toTypedArray()[j + 2]
                    println("azimuth:=$azimuth")
                    val azimuth1 = azimuth.toInt()
                    azimuthL.add(azimuth1)
                    BDGSVAzimuth.add(azimuth1)
                    val snr = str1.split(",").toTypedArray()[j + 3]
                    println("snr:=$snr")
                    j += 4
                }
            }
        }
        println("elevationL:=" + elevationL)
        println("azimuthL:=" + azimuthL)
    }

    fun draw(
        elevationlist: java.util.ArrayList<Int>,
        azimuthlist: java.util.ArrayList<Int>,
        prn: java.util.ArrayList<Int>,
        paint: Paint?,
        paint3: Paint?,
        canvas: Canvas
    ) {
        // here we get the display width or height.
        val currentdisplay = windowManager.defaultDisplay
        val dw = currentdisplay.width.toFloat()
        val dh = currentdisplay.height.toFloat()
        Log.i("width", dw.toString())
        Log.i("height", dh.toString())

        // here we get the display centerX or centerY
        val centerX = currentdisplay.width.toFloat() / 2
        val centerY = currentdisplay.height.toFloat() / 2
        var k = 0
        while (k < elevationlist.size && k < azimuthlist.size && k < prn.size) {
            val newdh = (centerX - 50).toDouble() / 90
            Log.i("newdh", "" + newdh)
            val elevationangle = (90 - elevationlist[k])
            Log.i("Elevation", "" + elevationangle)
            val azimuth = (360 - azimuthlist[k] + 90)
            Log.i("azimuth", "" + azimuth)
            val radiusarc = (elevationangle * newdh).toInt()
            Log.i("radiusarc", "" + radiusarc)
            if (360 < azimuth) {
                val newazimuth = (azimuth - 360)
                Log.i("newazimuth", "" + newazimuth)
                val rad = Math.toRadians(newazimuth.toDouble())
                val xcos = radiusarc.toDouble() * Math.cos(rad)
                val ysin = -radiusarc.toDouble() * Math.sin(rad)
                Log.i("xcos", "" + xcos)
                Log.i("ysin", "" + ysin)
                val newcenterx = centerX + xcos
                val newcentery = centerY + ysin
                Log.i("newcenterx", "" + newcenterx)
                Log.i("newcentery", "" + newcentery)
                canvas.drawCircle(newcenterx.toFloat(), newcentery.toFloat(), 30f, paint3!!)
                canvas.drawText(
                    prn[k].toString(), newcenterx.toFloat(), newcentery.toFloat(),
                    paint!!
                )
            } else {
                val rad = Math.toRadians(azimuth.toDouble())
                val xcos = radiusarc.toDouble() * Math.cos(rad)
                val ysin = -radiusarc.toDouble() * Math.sin(rad)
                Log.i("xcos1", "" + xcos)
                Log.i("ysin1", "" + ysin)
                val newcenterx = centerX + xcos
                val newcentery = centerY + ysin
                Log.i("newcenterx1", "" + newcenterx)
                Log.i("newcentery1", "" + newcentery)
                canvas.drawCircle(newcenterx.toFloat(), newcentery.toFloat(), 30f, paint3!!)
                canvas.drawText(
                    prn[k].toString(), newcenterx.toFloat() + 2, newcentery.toFloat() + 2,
                    paint!!
                )
                //                canvas.drawText(elevationlist.get(k) +", "+ azimuthlist.get(k), (float) newcenterx, (float) newcentery,paint);
            }
            k++
        }
    }

    fun drawCircleLine(canvas: Canvas, paint1: Paint?) {
        val currentdisplay = windowManager.defaultDisplay
        val dw = currentdisplay.width.toFloat()
        val dh = currentdisplay.height.toFloat()
        Log.i("width", dw.toString())
        Log.i("height", dh.toString())

        // here we get the display centerX or centerY
        val centerX = currentdisplay.width.toFloat() / 2
        val centerY = currentdisplay.height.toFloat() / 2
        Log.i("centerX", centerX.toString())
        Log.i("centerY", centerY.toString())
        canvas.drawCircle(centerX, centerY, centerX - 50, paint1!!)
        canvas.drawCircle(centerX, centerY, (dw - 100) / 2 * 1 / 6, paint1)
        canvas.drawCircle(centerX, centerY, (dw - 100) / 2 * 2 / 6, paint1)
        canvas.drawCircle(centerX, centerY, (dw - 100) / 2 * 3 / 6, paint1)
        canvas.drawCircle(centerX, centerY, (dw - 100) / 2 * 4 / 6, paint1)
        canvas.drawCircle(centerX, centerY, (dw - 100) / 2 * 5 / 6, paint1)
        canvas.drawLine(50f, dh / 2, dw - 50, dh / 2, paint1)
        canvas.drawLine(dw / 2, dh / 2 - centerX + 50, dw / 2, dh / 2 + centerX - 50, paint1)
    }

    fun drawnumber() {
        val currentdisplay = windowManager.defaultDisplay
        val centerX = currentdisplay.width.toFloat() / 2
        val centerY = currentdisplay.height.toFloat() / 2
        for (i in numbers.indices) {
            val e = 90
            val a = (360 - numbers[i] + 90)
            val ra = (e * (centerX / 90)).toInt()
            //            int ra = (int)((e*(centerX/90))-45);
            if (360 < a) {
                val rad = Math.toRadians((a - 360).toDouble())
                val xcos = ra.toDouble() * Math.cos(rad)
                val ysin = -ra.toDouble() * Math.sin(rad)
                val newcenterx = centerX + xcos
                val newcentery = centerY + ysin
                canvas!!.drawLine(
                    centerX, centerY, newcenterx.toFloat(), newcentery.toFloat(),
                    paint2!!
                )
                canvas!!.drawText(
                    numbers[i].toString(), newcenterx.toFloat(), newcentery.toFloat(),
                    paint!!
                )
            } else {
                val rad = Math.toRadians(a.toDouble())
                val xcos = ra.toDouble() * Math.cos(rad)
                val ysin = -ra.toDouble() * Math.sin(rad)
                val newcenterx = centerX + xcos
                val newcentery = centerY + ysin
                canvas!!.drawLine(
                    centerX, centerY, newcenterx.toFloat(), newcentery.toFloat(),
                    paint2!!
                )
                canvas!!.drawText(
                    numbers[i].toString(), newcenterx.toFloat(), newcentery.toFloat(),
                    paint!!
                )
            }
        }
    }

    fun drawdegress() {
        val currentdisplay = windowManager.defaultDisplay
        val centerX = currentdisplay.width.toFloat() / 2
        val centerY = currentdisplay.height.toFloat() / 2
        for (j in degress.indices) {
            val e1 = 90 - degress[j]
            val a1 = 90
            val ra1 = (e1 * (centerX / 90)).toInt()
            val rad1 = Math.toRadians(a1.toDouble())
            val xcos1 = ra1.toDouble() * Math.cos(rad1)
            val ysin1 = -ra1.toDouble() * Math.sin(rad1)
            val newcenterx1 = centerX + xcos1
            val newcentery1 = centerY + ysin1
            canvas!!.drawText(
                degress[j].toString(), newcenterx1.toFloat(), newcentery1.toFloat(),
                paint!!
            )
        }
    }


    fun redraw() {
        val currentdisplay = windowManager.defaultDisplay
        val dw = currentdisplay.width.toFloat()
        val dh = currentdisplay.height.toFloat()
        bitmap = Bitmap.createBitmap(dw.toInt(), dh.toInt(), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        drawCircleLine(canvas!!, paint1)
        drawnumber()
        drawdegress()
        imageView?.setImageBitmap(bitmap)
    }


}