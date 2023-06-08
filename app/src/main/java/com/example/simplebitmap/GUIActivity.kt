package com.example.simplebitmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apogee.customgui.NewMainActivity
import com.ortiz.touchview.OnTouchImageViewListener
import com.ortiz.touchview.TouchImageView
import java.io.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class GUIActivity : AppCompatActivity() {

    lateinit var imageview : TouchImageView
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
    var arrayListX = ArrayList<Double>()
    var arrayListY = ArrayList<Double>()
    var pointName =  ArrayList<String>()
    var dw = 0f
    var dh = 0f
    var isScroll = false
    var differenceXY = 0.0
    var Csvpoint_list = java.util.ArrayList<java.util.ArrayList<String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestpermission()
        val currentdisplay = windowManager.defaultDisplay
        dw = currentdisplay.width.toFloat()
        dh = currentdisplay.height.toFloat()
        Log.i("width", dw.toString())
        Log.i("height", dh.toString())

        bmp = Bitmap.createBitmap(dw.toInt(), dh.toInt(), Bitmap.Config.ARGB_8888)


        val imageView = findViewById<TouchImageView>(R.id.ivmap)
        val clear = findViewById<Button>(R.id.clear)
        val draw = findViewById<Button>(R.id.draw)
        val import = findViewById<Button>(R.id.read)
        val reset = findViewById<Button>(R.id.reset)
/////////////////////////// Here we use CustomUi Module /////////////////////////////////////
/*        val mainActivity : com.apogee.customgui.MainActivity= MainActivity()
        mainActivity.intiliaze(imageView = imageView,this)*/
        val mainActivity : com.apogee.customgui.NewMainActivity= NewMainActivity()
        mainActivity.intiliaze(imageView = imageView,this)



        clear.setOnClickListener {
            arrayListX.clear()
            arrayListY.clear()
            pointName.clear()
            mainActivity.clearpoints()
        }

        draw.setOnClickListener {
            Log.d("TAG", arrayListX.size.toString())

            for(i in 0 until arrayListX.size){
                pointName.add(i.toString())
            }
                mainActivity.addPoints(arrayListX, arrayListY,pointName)
                factorXY= mainActivity.getPixel().toDouble()
                Log.d("TAG", "factorXY: "+factorXY)
                Toast.makeText(this,"checkkk",Toast.LENGTH_SHORT).show()
        }

        import.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "Select CSV file"), PICK_TEXT)
        })

        reset.setOnClickListener {
            deltaX = 0f
            deltaY = 0f
            factorXY= mainActivity.getPixel().toDouble()
            differenceXY = mainActivity.differenceXY
            factorXY = (dw / differenceXY)
//            mainActivity.drawpixelpoint()
        }

/////////////////////////// Here we use TouchView Module //////////////////////////////////
        imageView.setOnTouchImageViewListener(object : OnTouchImageViewListener {

            override fun onMove(x: Float, y: Float, scaleFactor: Float) {
                if(arrayListX.size > 0){
                    if(x< dw && y < dh){
                        val changeSpanX = (curSpanX - prevSpanX )
                        factorXY = ((changeSpanX / dw) * factorXY) + factorXY
//                        factorXY= mainActivity.zoomgetPixel(factorXY).toDouble()
                        factorXY= mainActivity.scrollgetPixel(factorXY,deltaX,deltaY).toDouble()
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

                if(arrayListX.size > 0){
                    deltaX += (deltax / factorXY).toFloat()
                    deltaY += (-deltay / factorXY).toFloat()
                    factorXY= mainActivity.scrollgetPixel(factorXY,deltaX,deltaY).toDouble()
                    isScaleSetFirstTime = false
                }
                Log.i("Tag","deltaX:= " +deltaX)
                Log.i("Tag","deltaY:= " +deltaY)
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
            val path = getFilePath(fileuri!!)
            val spilitdata = path.split("\\.").toTypedArray()
            val spilitdatapath = spilitdata[0]
            if (spilitdata.size > 0) {
                if (spilitdata[0].contains("xml")) {
                    readText(getFilePath(fileuri!!))
                } else if (spilitdata[0].contains("csv")) {
                    readCSVFile(getFilePath(fileuri!!))
                }
            }
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
    fun readText(input: String): String {
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
    /// reading csv file data
    fun readCSVFile(path: String?): String? {
        val filepath: String? = null
        val file = File(path)
        try {
            val scanner = Scanner(file)
            var temp_rows = scanner.nextLine()
            var splited_temp_rows = temp_rows.split(",").toTypedArray()

            if(splited_temp_rows.size>0)
            {
                Log.d("TAG", "readCSVFile: "+Arrays.toString(splited_temp_rows))
                val index_point_name = Arrays.asList(*splited_temp_rows).indexOf("\uFEFFPoint_name")
                val index_easting = Arrays.asList(*splited_temp_rows).indexOf("Easting")
                val index_northing = Arrays.asList(*splited_temp_rows).indexOf("Northing")
                val index_elevation = Arrays.asList(*splited_temp_rows).indexOf("Elevation")
                //            int index_prefix = Arrays.asList(splited_temp_rows).indexOf("prefix");
                val index_prefix =Arrays.asList(*splited_temp_rows).indexOf("Prefix")
                //            int index_zone = Arrays.asList(splited_temp_rows).indexOf("zone");
                val index_zone = Arrays.asList(*splited_temp_rows).indexOf("Zone")
                while (scanner.hasNextLine()) {
                    temp_rows = scanner.nextLine()
                    splited_temp_rows = temp_rows.split(",").toTypedArray()
                    val temp_data = java.util.ArrayList<String>()
                    temp_data.add(splited_temp_rows[0])
                    temp_data.add(splited_temp_rows[index_easting])
                    temp_data.add(splited_temp_rows[index_northing])
                    temp_data.add(splited_temp_rows[index_elevation])
                    temp_data.add(splited_temp_rows[index_prefix])
                    temp_data.add(splited_temp_rows[index_zone])
                    Csvpoint_list.add(temp_data)
                }
                readcsvpoint()
                Log.e("Csvpoint_list", index_point_name.toString())
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "CsvError", Toast.LENGTH_SHORT).show()
        }
        return filepath
    }

    fun readcsvpoint() {
        for (i in Csvpoint_list.indices) {
            val index = Csvpoint_list.get(i).get(1)
            val index1 = Csvpoint_list.get(i).get(2)
            arrayListX.add(index.toDouble())
            arrayListY.add(index1.toDouble())
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