package com.apogee.calfilelibrary.Object

import android.util.Log
import com.apogee.calfilelibrary.Model.SiteModel
import com.apogee.calfilelibrary.Util.Util1


import org.json.JSONObject

object GenerateCalFile {
    fun globalMethod(siteList: ArrayList<SiteModel>): JSONObject {
        var response = JSONObject()
        try {
            val jsonObject = JSONObject()
            for (i in 0 until siteList.size) {
                if(siteList.get(i).status==1) {
                    jsonObject.put("type", "utm")
                    jsonObject.put("FixX" + (i + 1), siteList[i].easting)
                    jsonObject.put("FixY" + (i + 1), siteList[i].northing)
                    jsonObject.put("FixZ" + (i + 1), siteList[i].elevation)
                    jsonObject.put("VarX" + (i + 1), siteList[i].east)
                    jsonObject.put("VarY" + (i + 1), siteList[i].north)
                    jsonObject.put("VarZ" + (i + 1), siteList[i].elev)
                }
            }
            response = Util1().findCoordinateUTM5(jsonObject)
        } catch (e: Exception) {
            Log.d("TAG", "generateCalFile: " + e.message)
        }
        return response
    }
}