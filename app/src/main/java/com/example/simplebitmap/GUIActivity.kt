package com.example.simplebitmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apogee.customgui.MainActivity
import com.ortiz.touchview.OnTouchImageViewListener
import com.ortiz.touchview.TouchImageView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class GUIActivity : AppCompatActivity() {

    lateinit var imageview : TouchImageView
//    var x : Double = 125.0
//    var y : Double = 250.0
//    var newx = ArrayList<Double>()
//    var newy = ArrayList<Double>()
    var factorX = 0.0
    var factorY = 0.0
    var factorXY = 0.0
    var width = 0
    var height = 0
    lateinit var bmp: Bitmap

    var curSpanX : Float = 0f
    var prevSpanX : Float = 0f
    var curSpanY : Float = 0f
    var prevSpanY : Float = 0f
    var deltaX : Float = 0f
    var deltaY : Float = 0f
    var isScaleSetFirstTime = true
    val threeDecimalPlaces = DecimalFormat("0.000")

    private val PERMISSION_REQUEST_STORAGE = 1000
    private val PICK_TEXT = 101
    var fileuri: Uri? = null
    lateinit var ArraysT: Array<String>
    var arrayListX = java.util.ArrayList<Double>()
    var arrayListY = java.util.ArrayList<Double>()
    var pointName = kotlin.collections.ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestpermission()
        val currentdisplay = windowManager.defaultDisplay
        val dw = currentdisplay.width.toInt()
        val dh = currentdisplay.height.toInt()
        Log.i("width", dw.toString())
        Log.i("height", dh.toString())

        bmp = Bitmap.createBitmap(dw, dh, Bitmap.Config.ARGB_8888)


        val imageView = findViewById<TouchImageView>(R.id.ivmap)
        val clear = findViewById<Button>(R.id.clear)
        val draw = findViewById<Button>(R.id.draw)
        val import = findViewById<Button>(R.id.read)
/////////////////////////// Here we use CustomUi Module /////////////////////////////////////
        val mainActivity : com.apogee.customgui.MainActivity= MainActivity()
        mainActivity.intiliaze(imageView = imageView,this)

        clear.setOnClickListener {
            mainActivity.clearpoints()
        }

//        newx.add(500.2)
//        newx.add(361.0)
//        newx.add(1050.50)
//        newx.add(3256.0)
//        newx.add(2201.100)
//        newy.add(361.2)
//        newy.add(500.2)
//        newy.add(1155.2)
//        newy.add(2500.2)
//        newy.add(3264.2)
//        Log.i("Tag","newx:= " +newx)
//        Log.i("Tag","newy:= " +newy)

        draw.setOnClickListener {
            Log.d("TAG", arrayListX.size.toString())
          /*  for (i in arrayListX.indices) {
                Log.d("TAG", "newX:="+arrayListX[i]+","+"newY:="+arrayListY[i])
               val factorXY= mainActivity.addPoint(arrayListX[i], arrayListY[i],"A")
                factorX=factorXY.substringBefore(",").toString().toDouble()
                factorY=factorXY.substringAfter(",").toString().toDouble()

            }*/
            for(i in 0 until arrayListX.size){
                pointName.add(i.toString())
            }

                val factorXY1= mainActivity.addPoints(arrayListX, arrayListY,pointName)

                factorXY=factorXY1.toDouble()
            Log.d("TAG", "factorXY: "+factorXY)
//                factorX=factorXY.substringBefore(",").toString().toDouble()
//                factorY=factorXY.substringAfter(",").toString().toDouble()

            Toast.makeText(this,"checkkk",Toast.LENGTH_SHORT).show()
//            mainActivity.addPoint(125.0, 250.0,"A")
        }

        import.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "Select CSV file"), PICK_TEXT)
        })

/////////////////////////// Here we use TouchView Module //////////////////////////////////

        val touchView : MainActivity = MainActivity()

        imageView.setOnTouchImageViewListener(object : OnTouchImageViewListener {

            override fun onMove(x: Float, y: Float, scaleFactor: Float) {
                if(arrayListX.size > 0){
                if(x< bmp.width && y < bmp.height){
//                    val changeSpanX = ((curSpanX - prevSpanX ) * 1.5)
                    val changeSpanX = (curSpanX - prevSpanX )
//                    val changeSpanY = ((curSpanY - prevSpanY ) * 1.5)
//                    factorX =  ((changeSpanX/dw) * factorX) + factorX
//                    factorY =  ((changeSpanY/dh) * factorY) + factorY
                    factorXY = ((changeSpanX / dw) * factorXY) + factorXY
                    isScaleSetFirstTime = false
//                    mainActivity.pointZoomplot(factorX, factorY)
                    mainActivity.pointZoomplot(factorXY)
//                    Log.i("Tag","FactorX:= " +factorX)
//                    Log.i("Tag","FactorY:= " +factorY)
                    Log.i("Tag","FactorXY:= " +factorXY)
                    Log.i("Tag","changeSpanX:= " +changeSpanX)
//                    Log.i("Tag","changeSpanY:= " +changeSpanY)

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
                    deltaX = threeDecimalPlaces.format((deltaX / factorX.toFloat()).toDouble()).toFloat()
                    deltaY = threeDecimalPlaces.format((deltaY / factorY.toFloat()).toDouble()).toFloat()
                    deltaX = (deltaX / factorXY.toFloat())
                    deltaY = (deltaY / factorXY.toFloat())
                    mainActivity.scrollplot(deltaX,deltaY)
//                    Log.i("Tag","deltaX:= " +deltaX)
//                    Log.i("Tag","deltaX:= " +deltaX)
                    isScaleSetFirstTime = false
                }
            }

            override fun onSpan(currX: Float, currY: Float, prevX: Float, prevY: Float) {
                curSpanX = currX
                curSpanY = currY
                prevSpanX = prevX
                prevSpanY = prevY
//
                Log.i("Tag","curSpanX:= " +curSpanX)
                Log.i("Tag","curSpanY:= " +curSpanY)
                Log.i("Tag","prevSpanX:= " +prevSpanX)
                Log.i("Tag","prevSpanY:= " +prevSpanY)

            }

            override fun onSingleTap(x: Float, y: Float) {

            }

        })

    }

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
//                Log.i("Text","" +ArraysT[0])
                readtheNorthingorEasting()
                Log.d("x_coord_list", arrayListX.toString())
                Log.d("y_coord_list", arrayListY.toString())
            }
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
//                        Log.d("point_line", String.valueOf(point_line));
                index = st_index
                while (index < point_line.length) {
                    if (point_line[index] == ' ') {
//                                    Log.d("index", String.valueOf(index));
                        index2 = index + 2
                        while (index2 < point_line.length) {
                            if (point_line[index2] == ' ') {
//                                            Log.d("index2", String.valueOf(index2));
//                                            Log.d("x_coord", point_line.substring(st_index+2, index+1));
//                                            Log.d("y_coord", point_line.substring(index+1, index2+1));
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