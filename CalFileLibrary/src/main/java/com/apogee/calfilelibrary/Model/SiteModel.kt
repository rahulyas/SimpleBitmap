package com.apogee.calfilelibrary.Model

class SiteModel (val easting : String, val northing : String, val elevation : String, val east : String, val north : String, val elev : String, val  pointName : String, val zone : String, val zonel: String ,val status: Int) {
    override fun toString(): String {
        return "SiteModel(easting='$easting', northing='$northing', elevation='$elevation', east='$east', north='$north', elev='$elev', pointName='$pointName', zone='$zone', zonel='$zonel')"
    }
}