package com.apogee.calfilelibrary.Interface

import com.apogee.calfilelibrary.Model.SiteModel
import org.json.JSONObject

interface AdapterInterface {
    fun onDataReceived(data: JSONObject?, siteList: ArrayList<SiteModel>) // You can modify the data type as per your adapter data
}