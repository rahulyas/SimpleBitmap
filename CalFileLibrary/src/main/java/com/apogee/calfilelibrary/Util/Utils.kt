package com.apogee.calfilelibrary.Util

import android.app.ProgressDialog
import android.content.Context

class Utils {
    fun progressDialog(activity: Context, msg: String): ProgressDialog {
        val progressDialog: ProgressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Please Wait..")
        progressDialog.setMessage(msg)
        return progressDialog
    }
}