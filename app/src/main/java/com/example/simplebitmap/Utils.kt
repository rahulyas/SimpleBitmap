package com.example.simplebitmap

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset

class Utils {
    companion object {
//        var ArraysT =ArrayList<String>()
        lateinit var ArraysT:Array<String>
        var Northing = ArrayList<Double>()
        var Easting = ArrayList<Double>()
        var Elevation = ArrayList<Double>()
        var newFace_pointline = ArrayList<String>()
        var newFace_temp_pointline = ArrayList<String>()
        var new_finallist= ArrayList<Double>()
        var new_finallist2= ArrayList<Double>()
        var facepoint_inCoordinate = ArrayList<ArrayList<Double>>()
        var final_tempList = ArrayList<Double>()
        private val STORAGE_PERMISSION_CODE = 1
        var DIRECTORY_NAME = "/LandXmlFile"

    }

    fun requestStoragePermission(context : Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        } else {
            // Permission already granted, perform your file operations here
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
    /// reading the land xml file
    fun readText(input: String?): String? {
        val file = File(input)
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
//                ArraysT.addAll(line!!.split("\n").toTypedArray())
                ArraysT= (line!!.split("\n").toTypedArray())
                readNorthingEasting()
            }
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }

    fun readAnyfile(uri: Uri,context: Context): List<String> {
        val csvFile = context.contentResolver.openInputStream(uri)
        val isr = InputStreamReader(csvFile)
        return BufferedReader(isr).readLines()
    }

    fun getFilePath(uri: Uri,context: Context): String {
        val filepath = uri.path
        val filePath1 = filepath!!.split(":").toTypedArray()
        return Environment.getExternalStorageDirectory().path + "/" + filePath1[1]
    }

    fun SplitDataList(list: MutableList<String>): ArrayList<Double> {
        val splitDataList = list.map { it.split(", ") }
        for (splitData in splitDataList) {
            Log.d(ContentValues.TAG, "readText:splitData =="+splitData)
            val identi = splitData[0]
            val values = identi.split("\\s+".toRegex())
            if(values.isNotEmpty() && values.contains("v")){
                val identifier = values[0]
                val Northing = values[1].toDouble()
                val Easting = values[2].toDouble()
                val Elevation = values[3].toDouble()
                new_finallist2.add(Northing)
                new_finallist2.add(Easting)
                new_finallist2.add(Elevation)
                Log.d(ContentValues.TAG, "readsplitdata:=="+"identifier = "+identifier+"Northing = "+Northing+"Easting = "+Easting+"Elevation = "+Elevation)
            }else if(values.isNotEmpty() && values.contains("vn")){
                val identifier = values[0]
                val Northing = values[1].toDouble()
                val Easting = values[2].toDouble()
                val Elevation = values[3].toDouble()
                new_finallist2.add(Northing)
                new_finallist2.add(Easting)
                new_finallist2.add(Elevation)
                Log.d(ContentValues.TAG, "readsplitdata:=="+"identifier = "+identifier+"Northing = "+Northing+"Easting = "+Easting+"Elevation = "+Elevation)
            }
        }
        return new_finallist2;
    }

    fun calFile(text: String?,context: Context) {
        val externalFileDir = context.getExternalFilesDir(null)
        val dir = File(externalFileDir!!.absolutePath + File.separator + "projects" + File.separator)
        val root = File(dir, DIRECTORY_NAME)
        //val dir = File(Environment.getExternalStorageDirectory(), "/DesignFiles")
        val tstamp = System.currentTimeMillis()
        val fileName = "LandXML_$tstamp.xml" +
                ""
        val calGen = File(root, fileName)
        if (!root.exists()) {
            try {
                root.mkdirs()
            } catch (e: java.lang.Exception) {
            }
        }
        try {
            FileUtils.write(calGen, text, Charset.defaultCharset())
            Toast.makeText(context, "Land XML File Generated", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    /// In this part Read the Northing and Easting From .xml files
    fun readNorthingEasting():ArrayList<Double> {
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
                                        new_finallist.add(java.lang.Double.valueOf(point_line.substring(st_index + 2, index + 1)))
                                        new_finallist.add(java.lang.Double.valueOf(point_line.substring(index + 1, index2 + 1)))
                                        new_finallist.add(java.lang.Double.valueOf(point_line.substring(index2 + 1, index3)))
/*                                        Easting.add(java.lang.Double.valueOf(point_line.substring(st_index + 2, index + 1)))
                                        Northing.add(java.lang.Double.valueOf(point_line.substring(index + 1, index2 + 1)))
                                        Elevation.add(java.lang.Double.valueOf(point_line.substring(index2 + 1, index3)))*/
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
        Log.d(TAG, "readNorthingEasting:new_finallist $new_finallist")
        Log.d(TAG, "readEasting ${Easting.size}")
        Log.d(TAG, "readNorthing ${Northing.size}")
        Log.d(TAG, "readElevation ${Elevation.size}")
        return new_finallist
    }

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
        if (newFace_pointline.size > 0) {
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

                temp_data1.add(Northing[i1 - 1])
                temp_data1.add(Easting[i1 - 1])
                temp_data1.add(Elevation.get(i1 - 1))

                temp_data1.add(Northing[j1 - 1])
                temp_data1.add(Easting[j1 - 1])
                temp_data1.add(Elevation.get(j1 - 1))

                temp_data1.add(Northing[k1 - 1])
                temp_data1.add(Easting[k1 - 1])
                temp_data1.add(Elevation.get(k1 - 1))

                final_tempList.add(Northing[i1 - 1])
                final_tempList.add(Easting[i1 - 1])
                final_tempList.add(Elevation.get(i1 - 1))

                final_tempList.add(Northing[j1 - 1])
                final_tempList.add(Easting[j1 - 1])
                final_tempList.add(Elevation.get(j1 - 1))

                final_tempList.add(Northing[k1 - 1])
                final_tempList.add(Easting[k1 - 1])
                final_tempList.add(Elevation.get(k1 - 1))

                facepoint_inCoordinate.add(temp_data1)
            }
        }
        println("final_tempList"+final_tempList)
//        finaltrianglepoint()
        drawtrianglespoint()
    }

    fun drawtrianglespoint(){
        Log.d(ContentValues.TAG, "drawtrianglespoint: facepoint_inCoordinate"+facepoint_inCoordinate.toString())
        for (i in facepoint_inCoordinate.indices) {
            val temp_ListX = ArrayList<Double>()
            val temp_ListY = ArrayList<Double>()
            val temp_ListZ = ArrayList<Double>()
            val temp_newList = facepoint_inCoordinate[i]

            temp_ListX.add(temp_newList[0])
            temp_ListX.add(temp_newList[3])
            temp_ListX.add(temp_newList[6])
            temp_ListY.add(temp_newList[1])
            temp_ListY.add(temp_newList[4])
            temp_ListY.add(temp_newList[7])
            temp_ListZ.add(temp_newList[2])
            temp_ListZ.add(temp_newList[5])
            temp_ListZ.add(temp_newList[8])

            Log.d("TAG", "temp_ListX:= "+temp_ListX + "==" +"temp_ListY:= "+temp_ListY+ "==" +"temp_ListZ:= "+temp_ListZ)
        }

    }

    fun finaltrianglepoint(){
        var i = 0
        while (i in final_tempList.indices) {
            val tempPoints = ArrayList<Double>()
            tempPoints.add(final_tempList[i+0])
            tempPoints.add(final_tempList[i+1])
            tempPoints.add(final_tempList[i+2])
            Log.d(ContentValues.TAG, "tempPoints:"+tempPoints)
            i = i + 3
        }
    }


}