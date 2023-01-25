package com.example.simplebitmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import com.example.simplebitmap.databinding.ActivityBitmapBinding
import com.ortiz.touchview.OnTouchImageViewListener
import com.ortiz.touchview.TouchImageView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.*

class Bitmap : AppCompatActivity() {
    private lateinit var binding: ActivityBitmapBinding

    var canvas: Canvas? = null
    var paint: Paint? = null
    var bitmap: Bitmap? = null
    var imageView: TouchImageView? = null
    var x = 0.0
    var y = 0.0
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
    var oldscaleXY = 0.0
    var currentdisplay: Display? = null

    private val PERMISSION_REQUEST_STORAGE = 1000
    private val PICK_TEXT = 1
    var fileuri: Uri? = null

    var curSpanX : Float = 0f
    var prevSpanX : Float = 0f
    var curSpanY : Float = 0f

    var prevSpanY : Float = 0f
    var deltaX : Float = 0f
    var deltaY : Float = 0f
    var isScaleSetFirstTime = true
    var isZoomtofit = false
    var isScroll = false

    lateinit var ArraysT: Array<String>
    var arrayListX = ArrayList<Double>()
    var arrayListY = ArrayList<Double>()
    var newarrayListX = ArrayList<Float>()
    var newarrayListY = ArrayList<Float>()
    var newFace_pointline = ArrayList<String>()
    var newFace_temp_pointline = ArrayList<String>()

    var newfacepointListIx = ArrayList<Double>()
    var newfacepointListIy = ArrayList<Double>()
    var newfacepointListJx = ArrayList<Double>()
    var newfacepointListJy = ArrayList<Double>()
    var newfacepointListZx = ArrayList<Double>()
    var newfacepointListZy = ArrayList<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        //request permission
        requestpermission()

        binding.add.setOnClickListener {
            val str: String = binding.X.getText().toString()
            val str1: String = binding.Y.getText().toString()
            x = str.toDouble()
            y = str1.toDouble()

            arrayListX.add(x)
            arrayListY.add(y)

            pointplot()

        }

        binding.clear.setOnClickListener {
            arrayListX.clear()
            arrayListY.clear()
            newarrayListX.clear()
            newarrayListY.clear()
            clear()
        }

        binding.draw.setOnClickListener {
            draw()
        }

        binding.load.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "Select CSV file"), PICK_TEXT)


        }

        binding.reset.setOnClickListener {
            isZoomtofit = true
            onRest()
        }

        imageView!!.setOnTouchImageViewListener(object : OnTouchImageViewListener {
            override fun onMove(x: Float, y: Float, scaleFactor: Float) {
                if(arrayListX.size > 0){
                    if(x< dw && y < dh){
                        val changeSpanX = ((curSpanX - prevSpanX ))
                        scaleXY = -((changeSpanX / dw) * scaleXY) + scaleXY
                        getPixel()
                        draw()
                        isScaleSetFirstTime = false
                        isScroll = true
//                        Log.i("Tag","changeSpanX:= " +changeSpanX)
//                        Log.i("Tag","scaleXY:= " +scaleXY)
                    }
                }
            }

            override fun onScaleBegin(x: Float, y: Float, scaleFactor: Float) {

            }

            override fun onScaleEnd(x: Float, y: Float,scaleFactor : Float) {

            }

            override fun onDrag(deltax: Float, deltay: Float) {
                if(arrayListX.size > 0){
                    deltaX = deltax
                    deltaY = -deltay
                    getPixel()
                    draw()
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

            }

        })

    }
/////////////////////// get Pixel /////////////////////////////////////////
    fun pointplot() {
        minX = Collections.min(arrayListX)
        maxX = Collections.max(arrayListX)
        minY = Collections.min(arrayListY)
        maxY = Collections.max(arrayListY)
        differenceX = maxX - minX
        differenceY = maxY - minY
        differenceXY = Math.max(differenceX, differenceY)
        scaleXY = (differenceXY / dw)
        Log.d("TAG", "Firstscale: $scaleXY")
        oldscaleXY = scaleXY
        Log.d("TAG", "Oldscale: $oldscaleXY")
        meanofX = (minX + maxX) / 2
        meanofY = (minY + maxY) / 2
        getPixel()
        minX = Collections.min(arrayListX)
        maxX = Collections.max(arrayListX)
        minY = Collections.min(arrayListY)
        maxY = Collections.max(arrayListY)

        println("minX = $minX, minY = $minY")
        println("maxX = $maxX, maxY = $maxY")
        Log.d("X", arrayListX.toString())
        Log.d("Y", arrayListY.toString())
    }
/////////////////////// get a PixelPoint /////////////////////////////////////////
    fun getPixel() {
        var plotX: Double
        var plotY: Double
        if (arrayListX.size > 1 && arrayListY.size > 1) {
            var i = 0
            while (i < arrayListX.size && i < arrayListY.size) {
                val test = arrayListX[i] + deltaX * scaleXY
                arrayListX.removeAt(i)
                arrayListX.add(i, test)
                val test1 = arrayListY[i] - deltaY * scaleXY
                arrayListY.removeAt(i)
                arrayListY.add(i, test1)
                i++
            }
        }
        if (arrayListX.size > 1 && arrayListY.size > 1) {
            newarrayListX.clear()
            newarrayListY.clear()
            var i = 0
            while (i < arrayListX.size && i < arrayListY.size) {
                plotX = ((arrayListX[i] - meanofX) / scaleXY) + centerX
                plotY = ((arrayListY[i] - meanofY) / scaleXY) + centerY
                newarrayListX.add(plotX.toFloat())
                newarrayListY.add(plotY.toFloat())
                i++
            }
        }
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
        while (j < newarrayListX.size && j < newarrayListY.size) {
            paint = Paint()
            paint!!.color = Color.RED
            canvas!!.drawCircle(
                newarrayListX[j], newarrayListY[j], 5f,
                paint!!
            )
            imageView!!.setImageBitmap(bitmap)
            j++
        }
    }
//////////////////////// clear ///////////////////////////////////////
    fun clear() {
        bitmap = Bitmap.createBitmap(dw.toInt(), dh.toInt(), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        paint!!.color = Color.TRANSPARENT
        imageView!!.setImageBitmap(bitmap)
    }
///////////////////////// Reset ////////////////////
    fun onRest(): Double? {
        if (isZoomtofit) {
            isZoomtofit = false
            deltaX = 0f
            deltaY = 0f
            differenceX = 0.0
            differenceY = 0.0
            scaleXY = dw / differenceXY
        } else if (!isScroll) {
            scaleXY = dw / differenceXY
        }
        var plotX: Double
        var plotY: Double
        //        deltaX = centerX;
//        deltaY = centerY;
        if (arrayListX.size > 1 && arrayListY.size > 1) {
            var i = 0
            while (i < arrayListX.size && i < arrayListY.size) {
                val test = arrayListX[i] + deltaX * scaleXY
                arrayListX.removeAt(i)
                arrayListX.add(i, test)
                val test1 = arrayListY[i] - deltaY * scaleXY
                arrayListY.removeAt(i)
                arrayListY.add(i, test1)
                i++
            }
        }
        if (arrayListX.size > 1 && arrayListY.size > 1) {
            newarrayListX.clear()
            newarrayListY.clear()
            var i = 0
            while (i < arrayListX.size && i < arrayListY.size) {
                plotX = ((arrayListX[i] - meanofX) / scaleXY) + centerX
                plotY = ((arrayListY[i] - meanofY) / scaleXY) + centerY
                newarrayListX.add(plotX.toFloat())
                newarrayListY.add(plotY.toFloat())
                i++
            }
        }
        draw()
        return scaleXY
    }
//////////////////////////////// Get the Import File Name //////////////////////////////////
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_TEXT && data != null) {
                fileuri = data.data
                readText(getFilePath(fileuri!!))
            }
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
    /// reading the Text file
    fun readText(input: String?): String? {
        val file = File(input)
        val text = java.lang.StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                ArraysT = line!!.split("\n").toTypedArray()
                readtheNorthingorEasting()
            }
            pointplot()
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }
    /// In this part Read the Northing and Easting From .xml files
    fun readtheNorthingorEasting(){
        for (i in ArraysT.indices) {
            if (ArraysT[i].contains("<P id=")) {
                var index = 0
                var index2 = 0
                var st_index = 0
                val point_line = ArraysT[i]
                st_index = point_line.indexOf("\">")
                index = st_index
                while (index < point_line.length) {
                    if (point_line[index] == ' ') {
                        index2 = index + 2
                        while (index2 < point_line.length) {
                            if (point_line[index2] == ' ') {
                                arrayListY.add(java.lang.Double.valueOf(point_line.substring(st_index + 2, index + 1)))
                                arrayListX.add(java.lang.Double.valueOf(point_line.substring(index + 1, index2 + 1)))
                            }
                            index2++
                        }
                    }
                    index++
                }
            }
        }
        readfacepoint()
        Log.d("newFace_pointline", newFace_pointline.toString())
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
        newface_pointlines()
    }
    /// this method is used for getting the newface_pointlines from .xml file
    fun newface_pointlines() {
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
            newfacepointListIx.add(arrayListX[i1])
            newfacepointListIy.add(arrayListY[i1])
            if (j1 < arrayListY.size) {
                newfacepointListJx.add(arrayListX[j1])
                newfacepointListJy.add(arrayListY[j1])
            }
            if (k1 < arrayListX.size && k1 < arrayListY.size) {
                newfacepointListZx.add(arrayListX[k1])
                newfacepointListZy.add(arrayListY[k1])
            }
        }
    }
    /// Runtime RequestPermission
    fun requestpermission() {
        //request permission for Read
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }

}