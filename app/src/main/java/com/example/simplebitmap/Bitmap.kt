package com.example.simplebitmap

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Display
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simplebitmap.databinding.ActivityBitmapBinding
import com.ortiz.touchview.OnTouchImageViewListener
import com.ortiz.touchview.TouchImageView
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class Bitmap : AppCompatActivity() {
    private lateinit var binding: ActivityBitmapBinding

    var canvas: Canvas? = null
    var paint: Paint? = null
    var paint1: Paint? = null
    var bitmap: Bitmap? = null
    var imageView: TouchImageView? = null
//    var x = 0.0
//    var y = 0.0
    var minX = 0.0
    var maxX = 0.0
    var minY = 0.0
    var maxY = 0.0
    var differenceX = 0.0
    var differenceY = 0.0
    var differenceXY = 0.0
    var meanofX = 0.0
    var meanofY = 0.0
    var dw = 0f
    var dh = 0f
    var centerX = 0f
    var centerY = 0f
    var scaleXY = 0.0
    var currentdisplay: Display? = null

    private val PERMISSION_REQUEST_STORAGE = 1000
    private val PICK_TEXT = 101
    var fileuri: Uri? = null

    var curSpanX : Float = 0f
    var prevSpanX : Float = 0f
    var curSpanY : Float = 0f
    var prevSpanY : Float = 0f
    var deltaX : Float = 0f
    var deltaY : Float = 0f
    var isScaleSetFirstTime = true
    var isZoomtofit = false
    var isReset = false
    var isScroll = false
    var diffX = 0.0
    var diffY = 0.0
    var FminX = 0.0
    var FmaxX = 0.0
    var FminY = 0.0
    var FmaxY = 0.0
    var current_triangle = 0
    var selected = false

    var x = 421291.5
    var y = 2942665.5

    lateinit var ArraysT: Array<String>

    var Northing = ArrayList<Double>()
    var Easting = ArrayList<Double>()
    var Elevation = ArrayList<Double>()
    var temp_keys = ArrayList<String>()

    var pixelofX = ArrayList<Double>()
    var pixelofY = ArrayList<Double>()

    var newFace_pointline = ArrayList<String>()
    var newFace_temp_pointline = ArrayList<String>()

    var facepoint_inpixel = ArrayList<ArrayList<Double>>()
    var facepoint_inCoordinate = ArrayList<ArrayList<Double>>()

    var main = HashMap<String, HashMap<String, ArrayList<Double>>>()
    val linePixel : HashMap<Int, ArrayList<PointModel>> = HashMap()


    var Dxftemp_String = ArrayList<String>()
    var Csvpoint_list = ArrayList<ArrayList<String>>()

    var pathtext = StringBuilder()
    var spilitdatapath = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //request permission
        requestpermission()
        // here we get the display width or height.
        currentdisplay = windowManager.defaultDisplay
        dw = currentdisplay!!.width.toFloat()
        dh = currentdisplay!!.height.toFloat()
        Log.i("width", dw.toString())
        Log.i("height", dh.toString())

        // here we get the display centerX or centerY
        centerX = currentdisplay!!.width.toFloat() / 2
        centerY = currentdisplay!!.height.toFloat() / 2
        Log.i("centerX", centerX.toString())
        Log.i("centerY", centerY.toString())

        // here we create the Bitmap on imageView
        bitmap = Bitmap.createBitmap(dw.toInt(), dh.toInt(), Bitmap.Config.ARGB_8888)
        imageView = findViewById<TouchImageView>(R.id.imagebitmap)
        canvas = Canvas(bitmap!!)
        imageView!!.setImageBitmap(bitmap)

        binding.add.setOnClickListener {
            val str: String = binding.X.getText().toString()
            val str1: String = binding.Y.getText().toString()
            x = str.toDouble()
            y = str1.toDouble()
            Northing.add(x)
            Easting.add(y)
            pointplot()
        }

        binding.clear.setOnClickListener {
            Northing.clear()
            Easting.clear()
            pixelofX.clear()
            pixelofY.clear()
            facepoint_inpixel.clear()
            clear()
        }

        binding.draw.setOnClickListener {
            if (spilitdatapath.contains("xml")){
                drawpixelpoint()
            } else if(spilitdatapath.contains("csv")){
                draw()
            }
//            draw()
//            drawpixelpoint()
        }

        binding.load.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select CSV file"), PICK_TEXT)
        }

        binding.reset.setOnClickListener {
            isZoomtofit = true
            isReset = false
            deltaX = 0f
            deltaY = 0f
            scaleXY = (dw / differenceXY)
            getPixel()
            if (spilitdatapath.contains("xml")){
                drawpixelpoint()
            } else if(spilitdatapath.contains("csv")){
                draw()
            }
//            draw()
//            drawpixelpoint()
        }

        imageView!!.setOnTouchImageViewListener(object : OnTouchImageViewListener {

            override fun onMove(x: Float, y: Float, scaleFactor: Float) {
                if(Northing.size > 0){
                    if(x< dw && y < dh){
                        val changeSpanX = ((curSpanX - prevSpanX ))
//                        scaleXY = ((changeSpanX / dw) * scaleXY) - scaleXY
                        scaleXY = ((changeSpanX / dw) * scaleXY) + scaleXY
                        getPixel()
                        if (spilitdatapath.contains("xml")){
                            drawpixelpoint()
                        } else if(spilitdatapath.contains("csv")){
                            draw()
                        }
//                        draw()
//                        drawpixelpoint()
                        isScaleSetFirstTime = false
                        isScroll = true
                    }
                }
            }

            override fun onScaleBegin(x: Float, y: Float, scaleFactor: Float) {
            }

            override fun onScaleEnd(x: Float, y: Float,scaleFactor : Float) {

            }

            override fun onDrag(deltax: Float, deltay: Float) {
                if(Northing.size > 0){
                    deltaX += (deltax / scaleXY).toFloat()
                    deltaY += (-deltay / scaleXY).toFloat()
                    getPixel()
                    if (spilitdatapath.contains("xml")){
                        drawpixelpoint()
                    } else if(spilitdatapath.contains("csv")){
                        draw()
                    }
//                    draw()
//                    drawpixelpoint()
                    isScaleSetFirstTime = false
                }
            }

            override fun onSpan(currX: Float, currY: Float, prevX: Float, prevY: Float) {
                curSpanX = currX
                curSpanY = currY
                prevSpanX = prevX
                prevSpanY = prevY
            }

            override fun onSingleTap(x: Float, y: Float) {
                val lowerlimitx = x - 50
                val upperlimitx = x + 50
                val lowerlimity = y - 50
                val upperlimity = y + 50
//                val lowerlimitx = x - 20
//                val upperlimitx = x + 20
//                val lowerlimity = y - 20
//                val upperlimity = y + 20

                val tapListx : ArrayList<Int> = ArrayList()
                val tapListy : ArrayList<Int> = ArrayList()
                val pointNList : ArrayList<String> = ArrayList()

                for(key in linePixel.keys){
                    val values = linePixel[key]
                    values?.let {
                        for(i in 0 until it.size){
                            if(values[i].x >= lowerlimitx && values[i].x <= upperlimitx && values[i].y >= lowerlimity && values[i].y <= upperlimity){
                                Log.d("checkkk==", key.toString() + "  " + true.toString())
                                    tapListx.add(values[i].x.toInt())
                                    tapListy.add(values[i].y.toInt())
                                    pointNList.add(key.toString())
                                break
                            }
                        }
                        Log.d("XList",""+tapListx+"\n" + tapListy+"\n"+""+ pointNList)
                    }
                }
            }
        })

    }
/////////////////////// get Pixel /////////////////////////////////////////
    fun pointplot() {
        minX = Collections.min(Northing)
        maxX = Collections.max(Northing)
        minY = Collections.min(Easting)
        maxY = Collections.max(Easting)
        differenceX = maxX - minX
        differenceY = maxY - minY
        differenceXY = Math.max(differenceX, differenceY)
        scaleXY = (dw / differenceXY)
        Log.d("TAG", "Firstscale: $scaleXY")
        meanofX = (minX + maxX) / 2
        meanofY = (minY + maxY) / 2
        getPixel()
    }
/////////////////////// get a PixelPoint /////////////////////////////////////////
    fun getPixel() {
        var plotX: Double
        var plotY: Double

        val plotValueX = ArrayList(Northing)
        val plotValueY = ArrayList(Easting)

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
                    dw / 2 + Math.abs(meanofX - plotValueX[i]) * scaleXY
                } else {
                    dw / 2 - Math.abs(meanofX - plotValueX[i]) * scaleXY
                }
                pixelofX.add(plotX)
            }
            for (i in plotValueY.indices) {
                plotY = if (plotValueY[i] > meanofY) {
                    dh / 2 + Math.abs(meanofY - plotValueY[i]) * scaleXY
                } else {
                    dh / 2 - Math.abs(meanofY - plotValueY[i]) * scaleXY
                }
                plotY = dh - plotY
                pixelofY.add(plotY)
            }
        }
        if(spilitdatapath.contains("xml")){
            newface_pointlines()
        }
//        newface_pointlines()
    }
    /// this method is used for getting the newface_pointlines from .xml file
    fun newface_pointlines() {
        if (newFace_pointline.size > 0) {
            facepoint_inpixel.clear()
            facepoint_inCoordinate.clear()
            for (n in newFace_pointline.indices) {
                var data = newFace_pointline[n]
                data = data.replace("[", "")
                data = data.replace("]", "")
                val spilitdata = data.split(", ").toTypedArray()
                val i = spilitdata[0]
                val i1 = i.toInt()
                var j = spilitdata[1]
                j = j.replace(" ", "")
                val j1 = j.toInt()
                var k = spilitdata[2]
                k = k.replace(" ", "")
                val k1 = k.toInt()

                val temp_data = ArrayList<Double>()
                val temp_data1 = ArrayList<Double>()

                temp_data.add(pixelofX[i1 - 1])
                temp_data.add(pixelofY[i1 - 1])

                temp_data.add(pixelofX[j1 - 1])
                temp_data.add(pixelofY[j1 - 1])

                temp_data.add(pixelofX[k1 - 1])
                temp_data.add(pixelofY[k1 - 1])

                temp_data1.add(Northing[i1 - 1])
                temp_data1.add(Easting[i1 - 1])
                temp_data1.add(Elevation.get(i1 - 1))

                temp_data1.add(Northing[j1 - 1])
                temp_data1.add(Easting[j1 - 1])
                temp_data1.add(Elevation.get(j1 - 1))

                temp_data1.add(Northing[k1 - 1])
                temp_data1.add(Easting[k1 - 1])
                temp_data1.add(Elevation.get(k1 - 1))

                facepoint_inpixel.add(temp_data)
                facepoint_inCoordinate.add(temp_data1)
            }
            Log.d(TAG, "facepoint_inCoordinate:size1"+facepoint_inCoordinate.size)

        }

        findHeight()
    }
    fun findHeight() {
        println("_inCoordinate:size --->" + facepoint_inCoordinate.size)
        Log.d(TAG, "facepoint_inCoordinate:size2"+facepoint_inCoordinate.size)
        val time1 = System.currentTimeMillis()
        for (i in facepoint_inCoordinate.indices) {
            Log.d(TAG, "facepoint_inCoordinate: "+facepoint_inCoordinate)
            val temp_ListX = ArrayList<Double>()
            val temp_ListY = ArrayList<Double>()
            val temp_newList = facepoint_inCoordinate[i]
            temp_ListX.add(temp_newList[0])
            temp_ListX.add(temp_newList[3])
            temp_ListX.add(temp_newList[6])
            temp_ListY.add(temp_newList[1])
            temp_ListY.add(temp_newList[4])
            temp_ListY.add(temp_newList[7])
            FminX = Collections.min(temp_ListX)
            FmaxX = Collections.max(temp_ListX)
            FminY = Collections.min(temp_ListY)
            FmaxY = Collections.max(temp_ListY)
            val map = HashMap<String, ArrayList<Double>>()
            val temp_Fmin = ArrayList<Double>()
            val temp_Fmax = ArrayList<Double>()
            //// min
            temp_Fmin.add(FminX)
            temp_Fmin.add(FminY)
            //// max
            temp_Fmax.add(FmaxX)
            temp_Fmax.add(FmaxY)
            map["points"] = temp_newList
            map["min"] = temp_Fmin
            map["max"] = temp_Fmax
            main.put(i.toString() + "", map)
        }
/*        for (keyValue: Map.Entry<*, *> in main.entries) {
            val key = keyValue.key.toString()
            val e_min: Double = main[key]!!["min"]!![0]
            val n_min: Double = main[key]!!["min"]!![1]
            val e_max: Double = main[key]!!["max"]!![0]
            val n_max: Double = main[key]!!["max"]!![1]
            if (e_min < x && x < e_max && n_min < y && y < n_max) {
                temp_keys.add(key)
            }
        }*/

        for (key in main.keys) {
            println("Key==============="+key)
            val key = key
            val e_min: Double = main[key]!!["min"]!![0]
            val n_min: Double = main[key]!!["min"]!![1]
            val e_max: Double = main[key]!!["max"]!![0]
            val n_max: Double = main[key]!!["max"]!![1]
            if (e_min < x && x < e_max && n_min < y && y < n_max) {
                temp_keys.add(key)
            }
        }
        for ((key, value) in main.entries) {
            val key = key.toString()
            val e_min: Double = main[key]!!["min"]!![0]
            val n_min: Double = main[key]!!["min"]!![1]
            val e_max: Double = main[key]!!["max"]!![0]
            val n_max: Double = main[key]!!["max"]!![1]
            if (e_min < x && x < e_max && n_min < y && y < n_max) {
                temp_keys.add(key)
            }
        }

        for (i in temp_keys.indices) {
//            System.out.println("Running for Triangle : " + temp_keys.get(i));
            val x1: Double = main.get(temp_keys.get(i))!!.get("points")!!.get(0)
            val y1: Double = main.get(temp_keys.get(i))!!.get("points")!!.get(1)
            val x2: Double = main.get(temp_keys.get(i))!!.get("points")!!.get(3)
            val y2: Double = main.get(temp_keys.get(i))!!.get("points")!!.get(4)
            val x3: Double = main.get(temp_keys.get(i))!!.get("points")!!.get(6)
            val y3: Double = main.get(temp_keys.get(i))!!.get("points")!!.get(7)


            ///////////////////////////////// fourth try //////////////////////////////////
            val c1 = (x2 - x1) * (y - y1) - (y2 - y1) * (x - x1)
            val c2 = (x3 - x2) * (y - y2) - (y3 - y2) * (x - x2)
            val c3 = (x1 - x3) * (y - y3) - (y1 - y3) * (x - x3)
            if (c1 < 0 && c2 < 0 && c3 < 0 || c1 > 0 && c2 > 0 && c3 > 0) {
                println("Yes for Triangle --->" + temp_keys.get(i))
                current_triangle = temp_keys.get(i).toInt()
                println("current_triangle --->" + current_triangle)

            } /*else {
//                println("No")
            }*/
        }
        val x1: Double = main.get(current_triangle.toString())!!.get("points")!!.get(0)
        val y1: Double = main.get(current_triangle.toString())!!.get("points")!!.get(1)
        val z1: Double = main.get(current_triangle.toString())!!.get("points")!!.get(2)
        val x2: Double = main.get(current_triangle.toString())!!.get("points")!!.get(3)
        val y2: Double = main.get(current_triangle.toString())!!.get("points")!!.get(4)
        val z2: Double = main.get(current_triangle.toString())!!.get("points")!!.get(5)
        val x3: Double = main.get(current_triangle.toString())!!.get("points")!!.get(6)
        val y3: Double = main.get(current_triangle.toString())!!.get("points")!!.get(7)
        val z3: Double = main.get(current_triangle.toString())!!.get("points")!!.get(8)
        val a1 = x2 - x1
        val b1 = y2 - y1
        val c1 = z2 - z1
        val a2 = x3 - x1
        val b2 = y3 - y1
        val c2 = z3 - z1
        val a = b1 * c2 - b2 * c1
        val b = a2 * c1 - a1 * c2
        val c = a1 * b2 - b1 * a2
        val d = -a * x1 - b * y1 - c * z1
        println("equation of plane is " + a + " x + " + b + " y + " + c + " z + " + d + " = 0");
        val new_height = -(a * x + b * y + d) / c
        println("New Height is " + new_height )
        val time2 = System.currentTimeMillis()
        Log.d(TAG, "total_time_in_STEP1...: " + (time2 - time1))

    }
/////////////////////////ON draw///////////////////////////////////////
    fun draw() {
        bitmap = Bitmap.createBitmap(
            currentdisplay!!.width,
            currentdisplay!!.height, Bitmap.Config.ARGB_8888
        )
        canvas = Canvas(bitmap!!)
        //   Draw a Points
        var j = 0
        while (j < pixelofX.size && j < pixelofY.size) {
            paint = Paint()
            paint!!.color = Color.RED
            canvas!!.drawCircle(pixelofX[j].toFloat(), pixelofY[j].toFloat(), 5f, paint!!)
            imageView!!.setImageBitmap(bitmap)
            j++
        }
        //  Draw a lINE
        // change for new try j = j++ by j = j+3

//        var j = 0
//        while (j < pixelofX.size - 1 && j < pixelofY.size - 1) {
//            paint = Paint()
//            paint!!.color = Color.BLACK
//            paint!!.strokeWidth = 5f
//            canvas!!.drawLine(pixelofX[j].toFloat(), pixelofY[j].toFloat(), pixelofX[j + 1].toFloat(), pixelofY[j + 1].toFloat(), paint!!)
//            imageView!!.setImageBitmap(bitmap)
//            j = j + 3
//        }

    }
    fun drawpixelpoint() {
        bitmap = Bitmap.createBitmap(currentdisplay!!.width, currentdisplay!!.height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        Log.d(TAG, "drawpixelpoint: "+facepoint_inpixel)
        for (i in facepoint_inpixel.indices) {
            val temp_ListX = ArrayList<Double>()
            val temp_ListY = ArrayList<Double>()
            val temp_newList = facepoint_inpixel[i]
            for (k in temp_newList.indices) {
                if (k % 2 == 0) {
                    temp_ListX.add(temp_newList[k])
                } else {
                    temp_ListY.add(temp_newList[k])
                }
            }
            Log.d(TAG, "temp_ListXDraw: $temp_ListX")
            Log.d(TAG, "temp_ListYDraw: $temp_ListY")
            var isFirstPoint = true
            var firstpos = -1
            var lastpos = -1
            var j = 0
            while (j < temp_ListY.size - 1 && j < temp_ListX.size - 1) {
                paint = Paint()
                paint!!.strokeWidth = 3f
                paint!!.color = Color.BLACK
                val textPaint = Paint()
                textPaint.strokeWidth = 10f
                textPaint.color = Color.BLACK
                if (isFirstPoint) {
                    isFirstPoint = false
                    firstpos = j
                }
                val newposition = j + 1
                lastpos = j + 1
                canvas!!.drawText(i.toString() + "", temp_ListX[j].toFloat() - 10, temp_ListY[j].toFloat() - 10, textPaint)

                canvas!!.drawLine(temp_ListX[j].toFloat(), temp_ListY[j].toFloat(), temp_ListX[newposition].toFloat(), temp_ListY[newposition].toFloat(), paint!!)

                val pixelModel = getlinePixels(temp_ListX[j].toInt(),temp_ListY[j].toInt(), temp_ListX[newposition].toInt(),temp_ListY[newposition].toInt())
                linePixel[i] = pixelModel
                imageView!!.setImageBitmap(bitmap)
                j++
            }
            if (firstpos != -1 && lastpos != -1) {
                paint = Paint()
                paint!!.strokeWidth = 3f
                paint!!.color = Color.BLACK
                canvas!!.drawLine(temp_ListX[lastpos].toFloat(), temp_ListY[lastpos].toFloat(), temp_ListX[firstpos].toFloat(), temp_ListY[firstpos].toFloat(), paint!!)
                val pixelModel = getlinePixels(temp_ListX[lastpos].toInt(),temp_ListY[lastpos].toInt(), temp_ListX[firstpos].toInt(), temp_ListY[firstpos].toInt())
                linePixel[i] = pixelModel
            }
            ////////////////////////// Second method //////////////////////////
            paint = Paint()
            paint!!.color = Color.YELLOW
            paint!!.style = Paint.Style.FILL
            val path1 = Path()
            path1.moveTo(temp_ListX.get(0).toFloat(), temp_ListY.get(0).toFloat())
            path1.lineTo(temp_ListX.get(1).toFloat(), temp_ListY.get(1).toFloat())
            path1.lineTo(temp_ListX.get(2).toFloat(), temp_ListY.get(2).toFloat())
            path1.lineTo(temp_ListX.get(0).toFloat(), temp_ListY.get(0).toFloat())
            canvas!!.drawPath(path1, paint!!)
            val paint1 = Paint()
            paint1.color = Color.BLUE
            paint1.textSize = 15f
            canvas!!.drawTextOnPath(i.toString() + "", path1, 25f, 25f, paint1)
        }
        var j = 0
            while (j < pixelofX.size && j < pixelofY.size) {
                paint = Paint()
                paint!!.color = Color.BLUE
                paint!!.strokeWidth = 5f
                canvas!!.drawCircle(
                    pixelofX[pixelofX.size - 1].toFloat(),
                    pixelofY[pixelofY.size - 1].toFloat(),
                    5f,
                    paint!!
                )
                imageView!!.setImageBitmap(bitmap)
                j++
            }
//        println("linePixel: " + linePixel.entries)
    }
//////////////////////// clear ///////////////////////////////////////
    fun clear() {
        bitmap = Bitmap.createBitmap(dw.toInt(), dh.toInt(), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        paint!!.color = Color.TRANSPARENT
        imageView!!.setImageBitmap(bitmap)
    }
//////////////////////////////// Get the Import File Name //////////////////////////////////
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.data
//            readText(getFilePath(fileuri!!))
//            readCSVFile(getFilePath(fileuri!!))
            val path = getFilePath(fileuri!!)
            val spilitdata = path.split("\\.").toTypedArray()
            spilitdatapath = spilitdata[0]
            if (spilitdata.size > 0) {
                if (spilitdata[0].contains("xml")) {
                    readText(getFilePath(fileuri!!))
                } else if (spilitdata[0].contains("csv")) {
                    readCSVFile(getFilePath(fileuri!!))
                }
            }
            pathtext.append(getFilePath(fileuri!!))
        }
    Log.d(TAG, "onActivityResult:pathtext "+pathtext)
    Savetotext()
/*
    if (requestCode == PICK_TEXT && data != null) {
        val clipData = data.clipData
        if (clipData != null) {
            // Multiple files selected
            for (i in 0 until clipData.itemCount) {
                fileuri = clipData.getItemAt(i).uri
                val path = getFilePath(fileuri!!)
                val spilitdata = path.split("\\.").toTypedArray()
                if (spilitdata.size > 1) {
                    if (spilitdata[1].contains("xml")) {
                        readXml(getFilePath(fileuri!!))
                    } else if (spilitdata[1].contains("csv")) {
                        readCSVFile(getFilePath(fileuri!!))
                    } else if (spilitdata[1].contains("dxf")) {
                        readdxf(getFilePath(fileuri!!))
                    }
                }
            }
        } else {
            // Single file selected
            fileuri = data.data
            val path = getFilePath(fileuri!!)
            val spilitdata = path.split("\\.").toTypedArray()
            if (spilitdata.size > 1) {
                if (spilitdata[1].contains("xml")) {
                    readXml(getFilePath(fileuri!!))
                } else if (spilitdata[1].contains("csv")) {
                    readCSVFile(getFilePath(fileuri!!))
                } else if (spilitdata[1].contains("dxf")) {
                    readdxf(getFilePath(fileuri!!))
                }
            }
        }
    }
*/

}
    /// this method is used for getting file path from uri
    fun getFilePath(uri: Uri): String {
        val filename1: Array<String>
        val fn: String
        val filepath = uri.path
        val filePath1 = filepath!!.split(":").toTypedArray()
        filename1 = filepath.split("/").toTypedArray()
        fn = filename1[filename1.size - 1]
        return Environment.getExternalStorageDirectory().path + "/" + filePath1[1]

    }

    /// reading the dxf file
    fun readdxf(input: String?): String? {
        val file = File(input)
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                Dxftemp_String.add(line!!)
            }
            Log.i("Dxftemp_String", Dxftemp_String.toString())
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "DxfError", Toast.LENGTH_SHORT).show()
        }
        return text.toString()
    }

    /// reading csv file data
    fun readCSVFile(path: String?): String? {
        val filepath: String? = null
        val file = File(path)
        try {
            val scanner = Scanner(file)
            var temp_rows = scanner.nextLine()
            var splited_temp_rows = temp_rows.split(",").toTypedArray()
            val index_point_name = Arrays.asList(*splited_temp_rows).indexOf("Point_name")
            val index_easting = Arrays.asList(*splited_temp_rows).indexOf("Easting")
            val index_northing = Arrays.asList(*splited_temp_rows).indexOf("Northing")
            val index_elevation = Arrays.asList(*splited_temp_rows).indexOf("Elevation")
            //            int index_prefix = Arrays.asList(splited_temp_rows).indexOf("prefix");
            val index_prefix = Arrays.asList(*splited_temp_rows).indexOf("Prefix")
            //            int index_zone = Arrays.asList(splited_temp_rows).indexOf("zone");
            val index_zone = Arrays.asList(*splited_temp_rows).indexOf("Zone")
            while (scanner.hasNextLine()) {
                temp_rows = scanner.nextLine()
                splited_temp_rows = temp_rows.split(",").toTypedArray()
                val temp_data = ArrayList<String>()
                temp_data.add(splited_temp_rows[0])
                temp_data.add(splited_temp_rows[index_easting])
                temp_data.add(splited_temp_rows[index_northing])
                temp_data.add(splited_temp_rows[index_elevation])
                temp_data.add(splited_temp_rows[index_prefix])
                temp_data.add(splited_temp_rows[index_zone])
                Csvpoint_list.add(temp_data)
            }
            readcsvpoint()
            pointplot()
            Log.e("Csvpoint_list", Csvpoint_list[1].get(1).toString())
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "CsvError", Toast.LENGTH_SHORT).show()
        }
        return filepath
    }

    /// reading the land xml file
    fun readText(input: String?): String? {
        val file = File(input)
        val text = java.lang.StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                ArraysT = line!!.split("\n").toTypedArray()
                readNorthingEasting()
            }
            pointplot()
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }

    /// In this part Read the Northing and Easting From .xml files
    fun readNorthingEasting() {
        for (i in ArraysT.indices) {
            if (ArraysT[i].contains("<P id=")) {
                var index = 0
                var index2 = 0
                var st_index = 0
                var index3 = 0
                val point_line = ArraysT[i]
                st_index = point_line.indexOf("\">")
                index = st_index
                while (index < point_line.length) {
                    if (point_line[index] == ' ') {
                        index2 = index + 2
                        while (index2 < point_line.length) {
                            if (point_line[index2] == ' ') {
                                index3 = index2 + 3
                                while (index3 < point_line.length) {
                                    if (point_line[index3] == '<') {
                                        Easting.add(java.lang.Double.valueOf(point_line.substring(st_index + 2, index + 1)))
                                        Northing.add(java.lang.Double.valueOf(point_line.substring(index + 1, index2 + 1)))
                                        Elevation.add(java.lang.Double.valueOf(point_line.substring(index2 + 1, index3)))
                                    }
                                    index3++
                                }
                            }
                            index2++
                        }
                    }
                    index++
                }
            }
        }
        readfacepoint()
        println("Northing in NewImport"+Northing)
        println("Easting in NewImport"+Easting)
        println("Elevation in NewImport"+Elevation)
    }
    /// In this part we read the facepoint from the .xml file
    fun readfacepoint(){
        for (j in ArraysT.indices) {
            if (ArraysT[j].contains("<F>")) {
                val Face_pointline = ArraysT[j]
                val face_index1 = Face_pointline.indexOf(">")
                for (k in face_index1 until Face_pointline.length) {
                    if (Face_pointline[k] == ' ') {
                        for (l in k + 1 until Face_pointline.length) {
                            if (Face_pointline[l] == ' ') {
                                for (m in l + 1 until Face_pointline.length) {
                                    if (Face_pointline[m] == '<') {
                                        newFace_temp_pointline.add(Face_pointline.substring(face_index1 + 1, k))
                                        newFace_temp_pointline.add(Face_pointline.substring(k, l))
                                        newFace_temp_pointline.add(Face_pointline.substring(l, m))
                                        newFace_pointline.add(newFace_temp_pointline.toString())
                                        newFace_temp_pointline.clear()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /// Runtime RequestPermission
    fun requestpermission() {
        //request permission for Read
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_STORAGE)
        }
    }

    fun getlinePixels(x: Int, y: Int, x2: Int, y2: Int) : ArrayList<PointModel> {
        var  pointModelList : ArrayList<PointModel> = ArrayList()
        var x = x
        var y = y
        val w = x2 - x
        val h = y2 - y
        var dx1 = 0
        var dy1 = 0
        var dx2 = 0
        var dy2 = 0
        if (w < 0) dx1 = -1 else if (w > 0) dx1 = 1
        if (h < 0) dy1 = -1 else if (h > 0) dy1 = 1
        if (w < 0) dx2 = -1 else if (w > 0) dx2 = 1
        var longest = Math.abs(w)
        var shortest = Math.abs(h)
        if (longest <= shortest) {
            longest = Math.abs(h)
            shortest = Math.abs(w)
            if (h < 0) dy2 = -1 else if (h > 0) dy2 = 1
            dx2 = 0
        }
        var numerator = longest shr 1
        for (i in 0..longest) {
            //putpixel(x, y, color)
            pointModelList.add(PointModel(x.toDouble(),y.toDouble()))
            numerator += shortest
            if (numerator >= longest) {
                numerator -= longest
                x += dx1
                y += dy1
            } else {
                x += dx2
                y += dy2
            }
        }

        return  pointModelList
    }


    //// for csv /////

    fun readcsvpoint() {
        for (i in Csvpoint_list.indices) {
            val index = Csvpoint_list.get(i).get(1)
            val index1 = Csvpoint_list.get(i).get(2)
            Northing.add(index.toDouble())
            Easting.add(index1.toDouble())
        }
    }

    fun Savetotext() {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        try {
            val path = Environment.getExternalStorageDirectory()
            val dir = File(path, "FilePath")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val filename = "MyFile_$timeStamp.txt"
            val file = File(dir, filename)
            val writer = FileWriter(file.absolutePath)
            val fw = BufferedWriter(writer)
            //            fw.write(String.valueOf(sb));
            fw.append(pathtext.toString())
            fw.close()
            //display file saved message
            Toast.makeText(baseContext, filename + "Saved \n" + path, Toast.LENGTH_SHORT).show()
            Log.i("Save", "File saved successfully!$path")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error")
        }
    }
}