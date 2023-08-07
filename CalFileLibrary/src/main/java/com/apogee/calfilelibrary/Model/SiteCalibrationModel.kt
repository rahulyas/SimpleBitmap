package com.apogee.calfilelibrary.Model

class SiteCalibrationModel (val pointName : String, val angle : String, val scale : String, val x : String, var y : String) {
    override fun toString(): String {
        return "SiteCalibrationModel(pointName='$pointName', angle='$angle', scale='$scale', x='$x', y='$y')"
    }
}