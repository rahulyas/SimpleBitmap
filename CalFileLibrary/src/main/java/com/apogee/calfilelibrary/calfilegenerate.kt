package com.apogee.calfilelibrary
import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import com.apogee.calfilelibrary.Model.SiteCalibrationModel
import com.apogee.calfilelibrary.Model.SiteModel
import com.apogee.calfilelibrary.Object.GenerateCalFile
import com.apogee.calfilelibrary.Util.LatLon2UTM
import org.apache.commons.io.FileUtils

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class calfilegenerate(context: Context) {

    companion object {
        val siteList = ArrayList<SiteModel>()
        var calFileString: StringBuilder = StringBuilder()
        val siteCalibrationList = ArrayList<SiteCalibrationModel>()
        var DIRECTORY_NAME = "/CalFiles"
        val threeDecimalPlaces = DecimalFormat("0.000")
        val fiveDecimalPlaces = DecimalFormat("0.00000")
        val nineDecimalPlaces = DecimalFormat("0.000000000")
        var angle = ""
        var scale = ""
        var Tx = ""
        var Ty = ""
        var Origin_Easting = ""
        var Origin_Northing = ""
        var vertical_shift = ""
        var xzSlopeAvg = ""
        var yzSlopeAvg = ""
        var Origin_Easting_vertical = ""
        var Origin_Northing_vertical = ""
        var sigmaHList: ArrayList<String> = ArrayList()
        var p_name = ""
        var eastingAccuracy: ArrayList<String> = ArrayList()
        var northingAccuracy: ArrayList<String> = ArrayList()
        var Aaccuracy: ArrayList<String> = ArrayList()
        lateinit var latLon2UTM: LatLon2UTM
        lateinit var progressDialog: ProgressDialog
        var CentralMeridianvalue =""
        var elevation_avg =""
        lateinit var context:Context
        var slopeNorth = ""
        var slopeEast = ""
        var Aaccuracyaverage:Double =0.0
    }

    fun generateCalFile(siteList:ArrayList<SiteModel>,context: Context) : ArrayList<SiteCalibrationModel>{
        latLon2UTM = LatLon2UTM(context)
        val response = GenerateCalFile.globalMethod(siteList)
        Log.d("TAG", "generateCalFile: "+siteList)
        Log.d("TAG", "generateCalresponse: "+response)
        val getResponse =  getResponse(response,siteList)
        return getResponse
    }

    fun generateAfterCalFile(siteList:ArrayList<SiteModel>){
        val response = GenerateCalFile.globalMethod(siteList)
        Log.d("TAG", "generateCalFile: "+response)
        getResponseAfterChange(response, siteList)
    }

    fun getResponse(response2: JSONObject, siteList: ArrayList<SiteModel>):ArrayList<SiteCalibrationModel> {
        Log.d(TAG, "getResponse: response2"+response2)
        try {
            val responseData = response2.toString()
            val jsonRoot = JSONObject(responseData)
            val jsonData: JSONArray = jsonRoot.getJSONArray("values")
            val response = jsonData.getJSONObject(0)
            val currentDate: String = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
            angle = response!!.getString("avgAngle").toString()
            scale = response.getString("avgScale")
            Origin_Northing = response.getString("Origin_Northing")
            Origin_Easting = response.getString("Origin_Easting")
            Tx = response.getString("Tx")
            Ty = response.getString("Ty")
            Origin_Easting_vertical = response.getString("Origin_Easting_vertical")
            Origin_Northing_vertical = response.getString("Origin_Northing_vertical")
            vertical_shift = response.getString("vertical_shift")
            xzSlopeAvg = response.getString("xzSlopeAvg")
            yzSlopeAvg = response.getString("yzSlopeAvg")
            elevation_avg = response.getString("elevation avg")
            var originNorthing = fiveDecimalPlaces.format(Origin_Northing.toDouble())
            var originEasting = fiveDecimalPlaces.format(Origin_Easting.toDouble())
            var ty = fiveDecimalPlaces.format(Ty.toDouble())
            var tx = fiveDecimalPlaces.format(Tx.toDouble())
            var angleValue = nineDecimalPlaces.format(angle.toDouble())
            var scaleValue = nineDecimalPlaces.format(scale.toDouble())
            var originNorthingVertical = fiveDecimalPlaces.format(Origin_Northing_vertical.toDouble())
            var originEastingVertical = fiveDecimalPlaces.format(Origin_Easting_vertical.toDouble())
            originNorthing = getResult(originNorthing.length, originNorthing)
            originEasting = getResult(originEasting.length, originEasting)
            ty = getResult(ty.length, ty)
            tx = getResult(tx.length, tx)
            angleValue = getResult(angleValue.length, angleValue)
            scaleValue = getResult(scaleValue.length, scaleValue)
            originNorthingVertical = getResult(originNorthingVertical.length, originNorthingVertical)
            originEastingVertical = getResult(originEastingVertical.length, originEastingVertical)
            vertical_shift = getResult(vertical_shift.length, vertical_shift)
            yzSlopeAvg = getResult(yzSlopeAvg.length, yzSlopeAvg)
            xzSlopeAvg = getResult(xzSlopeAvg.length, xzSlopeAvg)
            calFileString.clear()

            val zone = siteList[0].zone.toDouble()
            val converted = latLon2UTM.convertToLatLng(
                siteList[0].easting.toDouble(),
                siteList[0].northing.toDouble(),
                zone.toInt(),
                false
            )
            val longitue = nineDecimalPlaces.format(converted.longitude)
            CentralMeridianvalue = getCentralMeridian(longitue)
            val caliberation_length = CentralMeridianvalue.length
            CentralMeridianvalue = getResult(caliberation_length, CentralMeridianvalue)
            Log.d("TAG", "getResponse:newvalue==" + CentralMeridianvalue)

            calFileString.append(
                "00NMSC V10-70           " + currentDate + "111111\r\n" +
                        "10NMJob Name        121111\r\n" +
                        "78NM11\r\n" +
                        "13NMSCS900 3.75.20200.54                                        \r\n" +
                        "65KI   6378137.00000  298.2572235630\r\n" +
                        "D5KI                                                     1.000000000         0.00000         0.00000\r\n" +
                        "64KI3     0.000000000    " + CentralMeridianvalue + "                         0.00000    500000.00000                     0.999600000                                \r\n" +
                        "49KI3   6378137.00000  298.2572229329     0.000000000     0.000000000     0.000000000         0.00000         0.00000         0.00000     0.000000000\r\n" +
                        "50KI" + originNorthing + originEasting + ty + tx + angleValue + scaleValue + "\r\n" +
                        "81KI1" + originNorthingVertical + "" + originEastingVertical + "" + vertical_shift + "" + yzSlopeAvg + "" + xzSlopeAvg + "                \r\n" +
                        "C8KI3SCS900 Localization             SCS900 Record                   WGS84 Equivalent Datum          \r\n" +
                        "13NMGPS type: Unknown                                           \r\n" +
                        "57NM         0.000001\r\n"
            )

            val jsonArray1: JSONArray = jsonRoot.getJSONArray("values1")


            Aaccuracy.clear()
            northingAccuracy.clear()
            eastingAccuracy.clear()
            for (i in 0 until jsonArray1.length()) {
                val obj1: JSONObject = jsonArray1.get(i) as JSONObject
                Log.d("TAG", "getResponse:== " + obj1)
                val horizontal_accuracy = obj1.get("Northing_accuracy_")
                val vertical_accuracy = obj1.get("Easting_accuracy_")
                val accuracy = obj1.get("accuracy_")
                northingAccuracy.add(horizontal_accuracy.toString())
                eastingAccuracy.add(vertical_accuracy.toString())
                Aaccuracy.add(accuracy.toString())
            }
            Log.d(ContentValues.TAG, "getResponseAaccuracy: "+jsonArray1.length()+"===="+Aaccuracy.size)

            val sum = Aaccuracy.mapNotNull {
                try {
                    it.toDouble() // Convert the string to a double
                } catch (e: NumberFormatException) {
                    // Handle invalid numbers here if needed
                    println("Invalid number: $it")
                    null // Return null for invalid numbers
                }
            }.sum()
            Aaccuracyaverage = sum / Aaccuracy.size

            val jsonArray2: JSONArray = jsonRoot.getJSONArray("values3")
            Log.d("TAG", "jsonArray2:== " + jsonArray2.length())

            for (i in 0 until jsonArray2.length()) {
                val obj1: JSONObject = jsonArray2.get(i) as JSONObject
                Log.d("TAG", "getResponse:==== " + obj1)
                val threed_accuracy = obj1.get("sigmaH_")
                sigmaHList.add(threed_accuracy.toString())
            }

            Log.d("TAG", "siteList: " + siteList + "====" + siteList.size)
            Log.d("TAG", "northingAccuracy: " + northingAccuracy + "====" + northingAccuracy.size)
            Log.d("TAG", "eastingAccuracy: " + eastingAccuracy + "====" + eastingAccuracy.size)
            Log.d("TAG", "sigmaHList: " + sigmaHList + "====" + sigmaHList.size)

            siteCalibrationList.clear()
            for (i in 0 until siteList.size) {
                if(siteList[i].status==1)
                {
                    val zone = siteList[i].zone.toDouble()
                    val converted = latLon2UTM.convertToLatLng(
                        siteList[i].easting.toDouble(),
                        siteList[i].northing.toDouble(),
                        zone.toInt(),
                        false
                    )
                    var latitude = nineDecimalPlaces.format(converted.latitude)
                    var longitue = nineDecimalPlaces.format(converted.longitude)

                    val lati_length = latitude.length
                    val longi_length = longitue.length

                    latitude = getResult(lati_length, latitude)
                    longitue = getResult(longi_length, longitue)
                    val heightitem = fiveDecimalPlaces.format(siteList[i].elevation.toDouble())
                    val height_length = heightitem.length
                    val heightList = getResult(height_length, heightitem)

                    val pointNameLength = siteList[i].pointName.length
                    var pointName = ""
                    var pointName1 = ""
                    var accPointName = ""
                    if (siteList[i].pointName.contains("GPS_")) {
                        pointName = getPointResult1(pointNameLength, siteList[i].pointName)
                        val newName = siteList[i].pointName.replace("GPS_", "")
                        val newNameLength = newName.length
                        pointName1 = getPointResult1(newNameLength, newName)

                        val newAccName = newName.plus("4")
                        val accPointLength = newAccName.length
                        accPointName = getPointResult1(accPointLength, newAccName)


                    } else {
                        pointName =
                            getPointResult(pointNameLength, "GPS_" + siteList[i].pointName)
                        pointName1 = getPointResult1(pointNameLength, siteList[i].pointName)
                    }


                    var northing = fiveDecimalPlaces.format(siteList[i].north.toDouble())
                    val n_length = northing.length
                    northing = getResult(n_length, northing)

                    var easting = fiveDecimalPlaces.format(siteList[i].east.toDouble())
                    val e_length = easting.length
                    easting = getResult(e_length, easting)
                    var elevation = fiveDecimalPlaces.format(siteList[i].elev.toDouble())
                    val ele_length = elevation.length
                    elevation = getResult(ele_length, elevation)
                    var variableElev =
                        fiveDecimalPlaces.format(siteList[i].elevation.toDouble())
                    val varialbeEle_length = variableElev.length
                    variableElev = getResult(varialbeEle_length, variableElev)
                    val n_acc_length = northingAccuracy[i].length
                    val nAccuracy = getResult(n_acc_length, northingAccuracy[i])
                    val e_acc_length = eastingAccuracy[i].length
                    val eAccuracy = getResult(e_acc_length, eastingAccuracy[i])
                    val sigmaH_length = sigmaHList[i].length
                    val sigmaH = getResult(sigmaH_length, sigmaHList[i])

                    Log.d("TAG", "nAccuracy " + nAccuracy)
                    Log.d("TAG", "eAccuracy " + eAccuracy)
//                    Log.d("TAG", "getResponse:sigmaHList "+sigmaH_length)

                    if(siteList[i].status==1) {
                        calFileString.append(
                            "66TP" + pointName + latitude + longitue + variableElev + "                41     0.000000000     0.000000000\r\n" +
                                    /* 4 space after pName */   "69KI" + pointName1 + northing + easting + elevation + "                11\r\n" +
                                    "B1CB" + pointName + accPointName + nAccuracy + eAccuracy + sigmaH + "\r\n"
                        )
                    }
                    siteCalibrationList.add(
                        SiteCalibrationModel(
                            siteList[i].pointName, angle, scale, nAccuracy, eAccuracy
                        )
                    )
                    slopeNorth = threeDecimalPlaces.format(xzSlopeAvg.toDouble())
                    slopeEast = threeDecimalPlaces.format(yzSlopeAvg.toDouble())

                }

            }
            Log.d(ContentValues.TAG, "getResponseSize: "+ siteList.size+"==="+siteCalibrationList.size)
        } catch (ex: Exception) {
            Log.d("TAG", "getResponse: error =" + ex)

        }
        return siteCalibrationList
    }

    fun onDataReceived(data: JSONObject?, siteList: ArrayList<SiteModel>) {
        getResponseAfterChange(data!!,siteList)
    }

    fun getResponseAfterChange(response2: JSONObject, siteList: ArrayList<SiteModel>) {
        try {
            val responseData = response2.toString()
            val jsonRoot = JSONObject(responseData)
            val jsonData: JSONArray = jsonRoot.getJSONArray("values")
            val response = jsonData.getJSONObject(0)
//            progressDialog.dismiss()
            val currentDate: String =
                SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
            angle = response!!.getString("avgAngle").toString()
            scale = response.getString("avgScale")
            Origin_Northing = response.getString("Origin_Northing")
            Origin_Easting = response.getString("Origin_Easting")
            Tx = response.getString("Tx")
            Ty = response.getString("Ty")
            Origin_Easting_vertical = response.getString("Origin_Easting_vertical")
            Origin_Northing_vertical = response.getString("Origin_Northing_vertical")
            vertical_shift = response.getString("vertical_shift")
            xzSlopeAvg = response.getString("xzSlopeAvg")
            yzSlopeAvg = response.getString("yzSlopeAvg")
            elevation_avg = response.getString("elevation avg")
            var originNorthing = fiveDecimalPlaces.format(Origin_Northing.toDouble())
            var originEasting = fiveDecimalPlaces.format(Origin_Easting.toDouble())
            var ty = fiveDecimalPlaces.format(Ty.toDouble())
            var tx = fiveDecimalPlaces.format(Tx.toDouble())
            var angleValue = nineDecimalPlaces.format(angle.toDouble())
            var scaleValue = nineDecimalPlaces.format(scale.toDouble())
            var originNorthingVertical =
                fiveDecimalPlaces.format(Origin_Northing_vertical.toDouble())
            var originEastingVertical =
                fiveDecimalPlaces.format(Origin_Easting_vertical.toDouble())
            originNorthing = getResult(originNorthing.length, originNorthing)
            originEasting = getResult(originEasting.length, originEasting)
            ty = getResult(ty.length, ty)
            tx = getResult(tx.length, tx)
            angleValue = getResult(angleValue.length, angleValue)
            scaleValue = getResult(scaleValue.length, scaleValue)
            originNorthingVertical =
                getResult(originNorthingVertical.length, originNorthingVertical)
            originEastingVertical =
                getResult(originEastingVertical.length, originEastingVertical)
            vertical_shift = getResult(vertical_shift.length, vertical_shift)
            yzSlopeAvg = getResult(yzSlopeAvg.length, yzSlopeAvg)
            xzSlopeAvg = getResult(xzSlopeAvg.length, xzSlopeAvg)
            calFileString.clear()

            val zone =siteList[0].zone.toDouble()
            val converted = latLon2UTM.convertToLatLng(
                siteList[0].easting.toDouble(),
                siteList[0].northing.toDouble(),
                zone.toInt(),
                false
            )
            val longitue = nineDecimalPlaces.format(converted.longitude)
            CentralMeridianvalue = getCentralMeridian(longitue)
            val caliberation_length = CentralMeridianvalue.length
            CentralMeridianvalue = getResult(caliberation_length, CentralMeridianvalue)
            Log.d("TAG", "getResponse:newvalue==" + CentralMeridianvalue)

            calFileString.append(
                "00NMSC V10-70           " + currentDate + "111111\r\n" +
                        "10NMJob Name        121111\r\n" +
                        "78NM11\r\n" +
                        "13NMSCS900 3.75.20200.54                                        \r\n" +
                        "65KI   6378137.00000  298.2572235630\r\n" +
                        "D5KI                                                     1.000000000         0.00000         0.00000\r\n" +
                        "64KI3     0.000000000    " + CentralMeridianvalue + "                         0.00000    500000.00000                     0.999600000                                \r\n" +
                        "49KI3   6378137.00000  298.2572229329     0.000000000     0.000000000     0.000000000         0.00000         0.00000         0.00000     0.000000000\r\n" +
                        "50KI" + originNorthing + originEasting + ty + tx + angleValue + scaleValue + "\r\n" +
                        "81KI1" + originNorthingVertical + "" + originEastingVertical + "" + vertical_shift + "" + yzSlopeAvg + "" + xzSlopeAvg + "                \r\n" +
                        "C8KI3SCS900 Localization             SCS900 Record                   WGS84 Equivalent Datum          \r\n" +
                        "13NMGPS type: Unknown                                           \r\n" +
                        "57NM         0.000001\r\n"
            )

            val jsonArray1: JSONArray = jsonRoot.getJSONArray("values1")


            Aaccuracy.clear()
            for (i in 0 until jsonArray1.length()) {
                val obj1: JSONObject = jsonArray1.get(i) as JSONObject
                Log.d("TAG", "getResponse:== " + obj1)
                val horizontal_accuracy = obj1.get("Northing_accuracy_")
                val vertical_accuracy = obj1.get("Easting_accuracy_")
                val accuracy = obj1.get("accuracy_")
                northingAccuracy.add(horizontal_accuracy.toString())
                eastingAccuracy.add(vertical_accuracy.toString())
                Aaccuracy.add(accuracy.toString())
            }

            Log.d(ContentValues.TAG, "getResponseAfterChangeAaccuracy: "+jsonArray1.length()+"==="+Aaccuracy.size)
            val sum = Aaccuracy.mapNotNull {
                try {
                    it.toDouble() // Convert the string to a double
                } catch (e: NumberFormatException) {
                    // Handle invalid numbers here if needed
                    println("Invalid number: $it")
                    null // Return null for invalid numbers
                }
            }.sum()
            Aaccuracyaverage = sum / Aaccuracy.size
            val jsonArray2: JSONArray = jsonRoot.getJSONArray("values3")
            Log.d("TAG", "jsonArray2:== " + jsonArray2.length())

            for (i in 0 until jsonArray2.length()) {
                val obj1: JSONObject = jsonArray2.get(i) as JSONObject
                Log.d("TAG", "getResponse:==== " + obj1)
                val threed_accuracy = obj1.get("sigmaH_")
                sigmaHList.add(threed_accuracy.toString())
            }

            Log.d("TAG", "siteList: " +siteList + "====" + siteList.size)
            Log.d("TAG", "northingAccuracy: " + northingAccuracy + "====" + northingAccuracy.size)
            Log.d("TAG", "eastingAccuracy: " + eastingAccuracy + "====" + eastingAccuracy.size)
            Log.d("TAG", "sigmaHList: " + sigmaHList + "====" + sigmaHList.size)


            for (i in 0 until siteList.size) {

                val zone = siteList[i].zone.toDouble()
                val converted = latLon2UTM.convertToLatLng(
                    siteList[i].easting.toDouble(),
                    siteList[i].northing.toDouble(),
                    zone.toInt(),
                    false
                )
                var latitude = nineDecimalPlaces.format(converted.latitude)
                var longitue = nineDecimalPlaces.format(converted.longitude)


                val lati_length = latitude.length
                val longi_length = longitue.length

                latitude = getResult(lati_length, latitude)
                longitue = getResult(longi_length, longitue)
                val heightitem = fiveDecimalPlaces.format(siteList[i].elevation.toDouble())
                val height_length = heightitem.length
                val heightList = getResult(height_length, heightitem)

                val pointNameLength = siteList[i].pointName.length
                var pointName = ""
                var pointName1 = ""
                var accPointName = ""
                if (siteList[i].pointName.contains("GPS_")) {
                    pointName = getPointResult1(pointNameLength, siteList[i].pointName)
                    val newName = siteList[i].pointName.replace("GPS_", "")
                    val newNameLength = newName.length
                    pointName1 = getPointResult1(newNameLength, newName)

                    val newAccName = newName.plus("4")
                    val accPointLength = newAccName.length
                    accPointName = getPointResult1(accPointLength, newAccName)


                } else {
                    pointName = getPointResult(pointNameLength, "GPS_" + siteList[i].pointName)
                    pointName1 = getPointResult1(pointNameLength, siteList[i].pointName)
                }


                var northing = fiveDecimalPlaces.format(siteList[i].north.toDouble())
                val n_length = northing.length
                northing = getResult(n_length, northing)

                var easting = fiveDecimalPlaces.format(siteList[i].east.toDouble())
                val e_length = easting.length
                easting = getResult(e_length, easting)
                var elevation = fiveDecimalPlaces.format(siteList[i].elev.toDouble())
                val ele_length = elevation.length
                elevation = getResult(ele_length, elevation)
                var variableElev = fiveDecimalPlaces.format(siteList[i].elevation.toDouble())
                val varialbeEle_length = variableElev.length
                variableElev = getResult(varialbeEle_length, variableElev)
                val n_acc_length = northingAccuracy[i].length
                val nAccuracy = getResult(n_acc_length, northingAccuracy[i])
                val e_acc_length = eastingAccuracy[i].length
                val eAccuracy = getResult(e_acc_length, eastingAccuracy[i])
                val sigmaH_length = sigmaHList[i].length
                val sigmaH = getResult(sigmaH_length, sigmaHList[i])
                Log.d("TAG", "nAccuracy " + nAccuracy)
                Log.d("TAG", "eAccuracy " + eAccuracy)
//                    Log.d("TAG", "getResponse:sigmaHList "+sigmaH_length)
                if(siteList[i].status==1) {
                    calFileString.append(
                        "66TP" + pointName + latitude + longitue + variableElev + "                41     0.000000000     0.000000000\r\n" +
                                /* 4 space after pName */   "69KI" + pointName1 + northing + easting + elevation + "                11\r\n" +
                                "B1CB" + pointName + accPointName + nAccuracy + eAccuracy + sigmaH + "\r\n"
                    )
                }
            }
            Log.d(ContentValues.TAG, "getResponseSize1: "+ siteList.size+"==="+siteCalibrationList.size)
            slopeNorth = threeDecimalPlaces.format(xzSlopeAvg.toDouble())
            slopeEast = threeDecimalPlaces.format(yzSlopeAvg.toDouble())
        } catch (ex: Exception) {
            Log.d("TAG", "getResponse: " + ex)
        }
    }

    fun getResult(length: Int, result: String): String {
        var modifyResult = result
        if (length <=16) {
            val addlength = 16 - length
            for (i in 0 until addlength) {
                modifyResult = " $modifyResult"
            }
        } else if (length > 16) {
            val minuslength = length - 16
            for (i in 0 until minuslength) {
                modifyResult = modifyResult.substring(0, modifyResult.length - 1)
            }
        }
        return modifyResult;
    }

    fun getPointResult(length: Int, result: String): String {
        var gpsPointName = result
        if (length <=15) {
            val addlength = 15 - length
            for (i in 0 until addlength) {
                gpsPointName = " $gpsPointName"
            }
        } else if (length > 15) {
            val minuslength = length - 15
            for (i in 0 until minuslength) {
                gpsPointName = gpsPointName.substring(0, gpsPointName.length - 1)
            }
        }
        return gpsPointName
    }

    fun getPointResult1(length: Int, result: String): String {
        var gpsPointName = result
        if (length <=16) {
            val addlength = 16 - length
            for (i in 0 until addlength) {
                gpsPointName = " $gpsPointName"
            }
        } else if (length > 16) {
            val minuslength = length - 16
            for (i in 0 until minuslength) {
                gpsPointName = gpsPointName.substring(0, gpsPointName.length - 1)
            }
        }
        return gpsPointName
    }

    fun calFile(context:Context,text: String?) {
        val externalFileDir = context.getExternalFilesDir(null)
        val dir = File(externalFileDir!!.absolutePath + File.separator + "projects" + File.separator + p_name)
        val root = File(dir, DIRECTORY_NAME)
        //val dir = File(Environment.getExternalStorageDirectory(), "/DesignFiles")
        val tstamp = System.currentTimeMillis()
        val fileName = "CalFile_$tstamp.cal"
        val calGen = File(root, fileName)
        if (!root.exists()) {
            try {
                root.mkdirs()
            } catch (e: java.lang.Exception) {
            }
        }
        try {
            FileUtils.write(calGen, text, Charset.defaultCharset())
            Toast.makeText(context, "Cal File Generated", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    fun getCentralMeridian(longitude: String?): String {
        var result = ""
        try {
            var zoneNo = (java.lang.Double.valueOf(longitude) + 180) / 6
            zoneNo=increaseIfDecimalExists(zoneNo)
            val cm = Math.round(zoneNo).toInt() * 6 - 183
            result = "$cm.000000000"
        } catch (e: java.lang.Exception) {
            println("Model.WebServiceModel.getCentralMeridian(): $e")
        }
        return result
    }

    fun increaseIfDecimalExists(number: Double): Double {
        var number = number
        if (number * 10 % 10 != 0.0) {
            number = Math.ceil(number)
        }
        return number
    }
}