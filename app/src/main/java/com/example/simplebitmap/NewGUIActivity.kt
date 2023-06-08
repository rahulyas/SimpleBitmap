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
import com.apogee.customgui.MainActivity
import com.apogee.customgui.NewMainActivity
import com.example.simplebitmap.R
import com.ortiz.touchview.TouchImageView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.text.DecimalFormat

class NewGUIActivity : AppCompatActivity() {
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
    var finalpoint =  ArrayList<String>()
    var prefix =  ArrayList<String>()
    var code =  ArrayList<String>()
    var misc1 =  ArrayList<String>()
    var misc2 =  ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_guiactivity)
        requestpermission()

        val imageView = findViewById<TouchImageView>(R.id.newivmap)
        val clear = findViewById<Button>(R.id.clear)
        val draw = findViewById<Button>(R.id.draw)
        val import = findViewById<Button>(R.id.read)

        val currentdisplay = windowManager.defaultDisplay
        val dw = currentdisplay.width.toInt()
        val dh = currentdisplay.height.toInt()
        Log.i("width", dw.toString())
        Log.i("height", dh.toString())

        bmp = Bitmap.createBitmap(dw, dh, Bitmap.Config.ARGB_8888)

        val mainActivity : com.apogee.customgui.MainActivity= MainActivity()
        mainActivity.intiliaze(imageView = imageView,this)

        draw.setOnClickListener {
            Log.d("TAG", arrayListX.size.toString())


            for(i in 0 until arrayListX.size){
                finalpoint.add(i.toString())
                prefix.add(i.toString())
                code.add(i.toString())
                misc1.add(i.toString())
                misc2.add(i.toString())
            }
/*            val factorXY1= mainActivity.pointplot(arrayListX, arrayListY,finalpoint,prefix,code,misc1,misc2)
            mainActivity.canvasdraw()*/
//            factorXY=factorXY1.toDouble()
            Log.d("TAG", "factorXY: "+factorXY)
            Toast.makeText(this,"checkkk", Toast.LENGTH_SHORT).show()
//              mainActivity.addPoint(125.0, 250.0,"A")
        }

        import.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "Select CSV file"), PICK_TEXT)
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