package com.example.simplebitmap

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.simplebitmap.databinding.ActivityBitmapBinding
import java.io.InputStreamReader
import java.util.*

class Bitmap : AppCompatActivity() {

    private lateinit var binding: ActivityBitmapBinding

    lateinit var imageView : ImageView
    var textView: TextView? = null
    var canvas: Canvas? = null
    var paint: Paint? = null
    var bitmap: Bitmap? = null
    var x = 0.0
    var y = 0.0
    var minX: Double? = null
    var maxX: Double? = null
    var minY: Double? = null
    var maxY: Double? = null

    private val PERMISSION_REQUEST_STORAGE = 1000
    private val READ_BLOCK_SIZE = 1
    private val PICK_TEXT = 1
    var text_uri: Uri? = null


    lateinit var ArraysT: Array<String>
    var arrayListX = ArrayList<Double>()
    var arrayListY = ArrayList<Double>()
    var newarrayListX = ArrayList<Float>()
    var newarrayListY = ArrayList<Float>()
    var newFace_pointline = ArrayList<String>()
    var newFace_temp_pointline = ArrayList<String>()

    var Facepoint1 = ArrayList<Double>()
    var Facepoint2 = ArrayList<Double>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)


// here we get the display width or height.
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

        // here we create the Bitmap on imageView
        bitmap = Bitmap.createBitmap(dw.toInt(), dh.toInt(), Bitmap.Config.ARGB_8888)

//        val bitmap  = com.apogee.customgui.MainActivity()
//        bitmap.intiliaze(imageView = binding.imagebitmap,this)
        val imageView = findViewById<ImageView>(R.id.imagebitmap)


        canvas = Canvas(bitmap!!)
        imageView.setImageBitmap(bitmap)
        //request permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_STORAGE
            )
        }

        binding.add.setOnClickListener {
            val str: String = binding.X.getText().toString()
            val str1: String = binding.Y.getText().toString()
            x = str.toDouble()
            y = str1.toDouble()

            arrayListX.add(x)
            arrayListY.add(y)

            minX = Collections.min(arrayListX)
            maxX = Collections.max(arrayListX)
            minY = Collections.min(arrayListY)
            maxY = Collections.max(arrayListY)

            println("minX = $minX, minY = $minY")
            println("maxX = $maxX, maxY = $maxY")
            Log.d("X", arrayListX.toString())
            Log.d("Y", arrayListY.toString())

        }

        binding.clear.setOnClickListener {
//            bitmap.clearpoints()
        }

        binding.draw.setOnClickListener {
//            bitmap.addPoint(125,250,"A")
            val differenceX = maxX!! - minX!!
            val differenceY = maxY!! - minY!!
            val differenceXY = Math.max(differenceX, differenceY)

            Log.i("differenceX", differenceX.toString())
            Log.i("differenceY", differenceY.toString())
            Log.i("differenceXY", differenceXY.toString())

            val scaleXY: Float = (differenceXY / dw).toFloat()
            Log.i("scaleXY", scaleXY.toString())

            val meanofX = (minX!! + maxX!!) / 2
            val meanofY = (minY!! + maxY!!) / 2
            Log.i("meanofX", meanofX.toString())
            Log.i("meanofY", meanofY.toString())
            run {
                var i = 0
                while (i < arrayListX.size && i < arrayListY.size) {
                    val px: Float = ((arrayListX[i] - meanofX) / scaleXY).toFloat() + centerX
                    val py: Float = ((arrayListY[i] - meanofY) / scaleXY).toFloat() + centerY
                    newarrayListX.add(px)
                    newarrayListY.add(py)
                    Log.i("px", px.toString())
                    Log.i("py", py.toString())
                    i++
                }
            }
            Log.d("newarrayListX", newarrayListX.toString())
            Log.d("newarrayListY", newarrayListY.toString())

            run {
                var j =0
                while (j < newarrayListX.size && j < newarrayListY.size){

                    paint = Paint()
                    paint!!.color = Color.RED
                    canvas?.drawCircle(newarrayListX[j], newarrayListY[j], 5f, paint!!)
                    imageView.setImageBitmap(bitmap)
                    j++
                }
            }

        }

        binding.load.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "text/*"
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_TEXT)
        }

        binding.read.setOnClickListener {

            try {
                val stream = contentResolver.openInputStream(text_uri!!)
                val InputRead = InputStreamReader(stream)
                val inputBuffer = CharArray(READ_BLOCK_SIZE)
                var s: String? = ""
                var charRead: Int
                while (InputRead.read(inputBuffer).also { charRead = it } > 0) {
//              char to string conversion
                    val readstring = String(inputBuffer, 0, charRead)
                    s += readstring
                }
                InputRead.close()
//                textView?.setText(s)
                ArraysT = s!!.split("\n").toTypedArray()
// in this part we read the Northing or Easting the .xml file
//               Log.d("ArraySize",ArraysT[0]);

// in this part we read the Northing or Easting the .xml file
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
                Log.d("x_coord_list", arrayListX.toString())
                Log.d("y_coord_list", arrayListY.toString())
                minX = Collections.min(arrayListX)
                maxX = Collections.max(arrayListX)
                minY = Collections.min(arrayListY)
                maxY = Collections.max(arrayListY)
                println("minX = $minX, minY = $minY")
                println("maxX = $maxX, maxY = $maxY")
                Log.d("X", arrayListX.toString())
                Log.d("Y", arrayListY.toString())

// int this part we read the facepoint from the .xml file
                for (j in ArraysT.indices) {
                    if (ArraysT[j].contains("<F>")) {
                        val Face_pointline = ArraysT[j]
                        val face_index1 = Face_pointline.indexOf(">")
                        // int U = Face_pointline.length();
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
                                                //result2.clear();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
////////////////////////////////////////////////////////////////////////////////////////////////
                Log.i("NewFace", newFace_pointline[0])
                Log.i("NewFaceSize", newFace_pointline[1])
//  in this part we split the arrayList of newFace_pointline
//  Log.i("objects", newFace_pointline.toArray());
                for (n in newFace_pointline.indices) {
                    var data = newFace_pointline[n]
                    data = data.replace("[", "")
                    data = data.replace("]", "")
                    val spilitdata = data.split(",").toTypedArray()
                    val i = spilitdata[0]
                    var j = spilitdata[1]
                    val k = spilitdata[2]
                    j = j.replace(" ", "")
                    val getindexEasting = arrayListX[i.toInt()]
                    val getIndexNorthing = arrayListY[j.toInt()]
                    Facepoint1.add(getindexEasting)
                    Facepoint2.add(getIndexNorthing)
                    Log.d("Facepoint1", Facepoint1.toString())
                    Log.d("Facepoint2", Facepoint2.toString())
                }

            }catch(e: Exception){
                e.printStackTrace()
//                Log.d("TAG", "onClick: " + e.message)
            }
        }

    }

//////////////////////////////// Get the Import File Name //////////////////////////////////
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != PICK_TEXT || resultCode != Activity.RESULT_OK) return else {
            text_uri = data?.data
//            textView.setText(" " + getFileName(text_uri));
            Toast.makeText(this, "GetFile Name" + getFileName(text_uri!!), Toast.LENGTH_SHORT)
                .show()
        }
        importFile(data?.data)
    }

    fun importFile(uri: Uri?) {
        val fileName = getFileName(text_uri!!)
    }
//////////////////////////////// Get File Name //////////////////////////////////
    @Throws(IllegalArgumentException::class)
    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor!!.count <= 0) {
            cursor.close()
            throw IllegalArgumentException("Can't obtain file name, cursor is empty")
        }
        cursor.moveToFirst()
        //cursor.close();
        return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
    }


}