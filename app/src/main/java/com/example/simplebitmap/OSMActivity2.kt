package com.example.simplebitmap

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.simplebitmap.databinding.ActivityOsmactivity2Binding
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.api.IGeoPoint
import org.osmdroid.bonuspack.kml.KmlDocument
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Arrays
import java.util.Scanner
import kotlin.math.pow


class OSMActivity2 : AppCompatActivity() ,MapEventsReceiver {
    private lateinit var binding: ActivityOsmactivity2Binding
    private lateinit var map: MapView
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private val PICK_TEXT = 101
    var fileuri: Uri? = null
    var spilitdatapath = ""
    var Csvpoint_list = ArrayList<ArrayList<String>>()
    var Northing = ArrayList<Double>()
    var Easting = ArrayList<Double>()
    var Latitude = ArrayList<Double>()
    var Longitude = ArrayList<Double>()
    private val SCALE_FACTOR = 0.9996
    private val MAJOR_RADIUS = 6378137.0
    private val MINOR_RADIUS = 6356752.314

    var geoPoints: MutableList<IGeoPoint> = mutableListOf()
//    var geoPoints: MutableList<GeoPoint> = mutableListOf()
    var rectanglePoints: MutableList<GeoPoint> = mutableListOf()
    var trianglePoints: MutableList<GeoPoint> = mutableListOf()
    var linePoints: MutableList<GeoPoint> = mutableListOf()
    var isAllFabsVisible: Boolean? = null

    private var markers: ArrayList<Marker> = ArrayList()
    private var polylines: MutableList<Polyline> = mutableListOf()

    /*for xml*/
    lateinit var ArraysT: Array<String>
    var newFace_pointline = ArrayList<String>()
    var newFace_temp_pointline = ArrayList<String>()
    var facepoint_inCoordinate = ArrayList<ArrayList<Double>>()

    /*for dxf*/
    var temp_String = ArrayList<String>()
    var entities_list = ArrayList<String>()
    var point_map = HashMap<String, ArrayList<String>>()
    var mtext_map = HashMap<String, ArrayList<String>>()
    var circle_map = HashMap<String, ArrayList<String>>()
    var line_map = HashMap<String, ArrayList<String>>()
    var arc_map = HashMap<String, ArrayList<String>>()
    var lwpolyline_map = HashMap<String, ArrayList<String>>()
    var polyline_map = HashMap<String, ArrayList<ArrayList<String>>>()
    var polygon_map = HashMap<String, ArrayList<ArrayList<String>>>()
    var Newlwpolyline_map = HashMap<String, MutableList<GeoPoint>>()
    var Newpolygon_map = HashMap<String, MutableList<GeoPoint>>()
    var Newline_map = HashMap<String, MutableList<GeoPoint>>()
    var Newcircle_map = HashMap<String, HashMap<ArrayList<String>, MutableList<GeoPoint>>>()
    var XmltrianglePoint_map = HashMap<String, MutableList<GeoPoint>>()

    /*29-06-2023*/
    private var isDrawingPolygon: Boolean = false
    private var currentPolygonPoints: MutableList<GeoPoint> = mutableListOf()
    private var totalDistance: Double = 0.0
    lateinit var sharedpreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOsmactivity2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermissionsIfNecessary(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET
            )
        )
        val context: Context = applicationContext
        val isAvailable = isInternetAvailable(context)
        sharedpreferences = getSharedPreferences(Constants.MYPreference, MODE_PRIVATE)
        map = findViewById(R.id.mapview)
        map.setTileSource(TileSourceFactory.MAPNIK)
        //        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.controller.setZoom(15.0)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setBuiltInZoomControls(false) // this is for remove the zoom control button
        map.setMultiTouchControls(true)
        //        map.setBackgroundColor(Color.WHITE)
        if (isAvailable) {
            // Internet connectivity is available
            //this for load the osmdroid map
            val ctx = this.applicationContext
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
            Configuration.getInstance().cacheMapTileCount = 100 // Set the desired cache size
            Configuration.getInstance().cacheMapTileOvershoot = 30 // Set the desired cache overshoot
        }
        else {
            // No internet connection, show an AlertDialog or take appropriate action
            map.setUseDataConnection(false) // Disable data connection for the map
            map.overlays.clear() // Clear any existing overlays
            //      Create a custom overlay with a white background
            val backgroundOverlay = object : Overlay() {
                override fun draw(canvas: Canvas?, mapView: MapView?, shadow: Boolean) {
                    super.draw(canvas, mapView, shadow)
                    if (!shadow) {
                        canvas?.drawColor(Color.WHITE)
/*
                        val gridSize = 50 // Set the size of each grid cell
                        val width = canvas!!.width
                        val height = canvas!!.height

                        val paint = Paint()
                        // Set grid line color and width
                        paint.color = Color.BLACK
                        paint.strokeWidth = 2f

                        // Draw vertical grid lines
                        var x = gridSize
                        while (x < width) {
                            canvas!!.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), paint)
                            x += gridSize
                        }

                        // Draw horizontal grid lines
                        var y = gridSize
                        while (y < height) {
                            canvas!!.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), paint)
                            y += gridSize
                        }*/
                    }
                }
            }
            map.overlays.add(backgroundOverlay)
        }

        

        //Scalebar
        val metrics = resources.displayMetrics
        val mScaleBar = ScaleBarOverlay(map)
        mScaleBar.setCentred(true)
        mScaleBar.setScaleBarOffset(metrics.widthPixels / 2, 10)
        map.overlays.add(mScaleBar)

        isAllFabsVisible = false
        binding.addFab.shrink()
        binding.addFab.setOnClickListener {
            isAllFabsVisible = if (!isAllFabsVisible!!) {
                binding.load.visibility = View.VISIBLE
                binding.rectangle.setVisibility(View.VISIBLE)
                binding.triangle.setVisibility(View.VISIBLE)
                binding.line.setVisibility(View.VISIBLE)
                binding.resetZoomButton.setVisibility(View.VISIBLE)
                binding.redraw.setVisibility(View.VISIBLE)
                binding.selectPolygonButton.setVisibility(View.VISIBLE)
                binding.addFab.extend()
                true
            } else {
                binding.load.visibility = View.GONE
                binding.rectangle.setVisibility(View.GONE)
                binding.triangle.setVisibility(View.GONE)
                binding.line.setVisibility(View.GONE)
                binding.resetZoomButton.setVisibility(View.GONE)
                binding.redraw.setVisibility(View.GONE)
                binding.selectPolygonButton.setVisibility(View.GONE)
                binding.addFab.shrink()
                false
            }
        }
        binding.load.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_TEXT)
        }
        binding.rectangle.setOnClickListener {
            onDrawRectangleButtonClick()
        }
        binding.triangle.setOnClickListener {
            onDrawTriangleButtonClick()
        }
        binding.line.setOnClickListener {
            onDrawLineButtonClick()
        }
        binding.resetZoomButton.setOnClickListener {
            map.controller.setZoom(15.0)
        }

        binding.redraw.setOnClickListener {
            val gson = Gson()
//            val json1: String = sharedpreferences.getString(Constants.Newlwpolyline_map, "").toString()
            val json1: String = sharedpreferences.getString(Constants.XmltrianglePoint_map, "").toString()
            val type = object : TypeToken<HashMap<String, MutableList<GeoPoint>>>() {}.getType()
            val mySet1: HashMap<String, MutableList<GeoPoint>> = gson.fromJson(json1, type)
//            Newlwpolyline_map = mySet1
            XmltrianglePoint_map = mySet1
/*
            Newlwpolyline_map.forEach { (key, value) ->
                val key = key
                val value = value
                val paint = Paint()
                paint.color = Color.GREEN
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
                for (l in value.indices) {
                    map.overlays.add(drawlwpolyline(value, paint))
                    map.controller.setCenter(value[l])
                }
            }
*/
            XmltrianglePoint_map.forEach { (key, value) ->
                val key = key
                val value = value
                val paint = Paint()
                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
                for (l in value.indices) {
                    map.overlays.add(drawlines(value, paint))
                    map.controller.setCenter(value[l])
                }
            }
        }
////////////////////////////////////////////////////////////////////
        // Set the center of the map or current location
        val startPoint = GeoPoint(28.619558, 77.380608,55.0068) // Start point coordinates
        map.controller.setCenter(startPoint)
        val marker = Marker(map)
        marker.position = startPoint
        marker.setTitle(startPoint.toString())
        // marker.setTextIcon(startPoint.toString())
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)
        // Add MapEventsOverlay to handle touch events
        val mapEventsOverlay = MapEventsOverlay(this)
        map.overlays.add(0, mapEventsOverlay)
////////////////////////////////////////////////////////////////////////
        /*for example i want to calculate the distance between two Geopoints points*/
        /*        val geoPoint1 = GeoPoint(40.7128, -74.0060)
        val geoPoint2 = GeoPoint(34.0522, -118.2437)

        val distance: Double = geoPoint1.distanceToAsDouble(geoPoint2)
        val newdistance = map.getProjection().metersToPixels(distance.toFloat());

        println("Distance: $distance meters====== $newdistance")*/
        /*29-06-2023*/
        binding.selectPolygonButton.setOnClickListener {
            isDrawingPolygon = !isDrawingPolygon
            if (isDrawingPolygon) {
                binding.selectPolygonButton.text = "FD"
            } else {
                drawPolygonOnMap()
                binding.selectPolygonButton.text = "SP"
            }
        }


//        newploting()
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        /*this code is working*/
        /*        if (p != null) {
            val latitude = p.latitude
            val longitude = p.longitude

            // Display the coordinates in a toast
            val message = "Latitude: $latitude\nLongitude: $longitude"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val marker = Marker(map)
            marker.position = p
            marker.setTitle(p.toString())
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(marker)
        }
        return false*/

//////////////////////////////////////////////////////////////////////////////////////////
        if (p != null) {
            val touchedPoint = p
            if (spilitdatapath.contains("xml")) {
                for ((key, value) in XmltrianglePoint_map) {
                    val key = key
                    val value = value
                    val paint = Paint()
                    paint.color = Color.RED
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 4f
                    for (point in value.indices) {
                        val point = point
                        Log.d(TAG, "singleTapConfirmedHelperpoint:+" + point)
                        val distance = value[point].distanceToAsDouble(touchedPoint)
                        Log.d(TAG, "singleTapConfirmedHelperdistance: " + distance)
                        val touchRadiusInPixels = 10
                        if (distance < touchRadiusInPixels) {
                            val pointList = value.joinToString(separator = "\n") { it.toString() }
                            map.overlays.add(drawlines(value, paint))
                            map.controller.setCenter(value[point])
                            val alertDialogBuilder = AlertDialog.Builder(this)
                            alertDialogBuilder.setTitle("Selected Line: $key")
                            alertDialogBuilder.setMessage(pointList)
                            alertDialogBuilder.setPositiveButton("OK", null)
                            alertDialogBuilder.show()
                            return true
                        }
                    }
                }
            } else if (spilitdatapath.contains("dxf")) {
                for ((key, value) in Newlwpolyline_map) {
                    val key = key
                    val value = value
                    val paint = Paint()
                    paint.color = Color.RED
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 4f
                    for (point in value.indices) {
                        val point = point
                        Log.d(TAG, "singleTapConfirmedHelperpoint:+" + point)
                        val distance = value[point].distanceToAsDouble(touchedPoint)
                        val newdistance = map.getProjection().metersToPixels(distance.toFloat());
                        Log.d(
                            TAG,
                            "singleTapConfirmedHelperdistance: " + distance + "newdistance: " + newdistance
                        )
                        val touchRadiusInPixels = 100
                        if (newdistance < touchRadiusInPixels) {
                            val pointList = value.joinToString(separator = "\n") { it.toString() }
                            map.overlays.add(drawlwpolyline(value, paint))
                            map.controller.setCenter(value[point])
                            val alertDialogBuilder = AlertDialog.Builder(this)
                            alertDialogBuilder.setTitle("Selected Line: $key\n Size:= ${value[point]}")
                            alertDialogBuilder.setMessage(pointList)
                            alertDialogBuilder.setPositiveButton("OK", null)
                            alertDialogBuilder.show()
                            return true
                        }
                    }
                }
            }
        }
        /*29-06-2023*/
        if (isDrawingPolygon && p != null) {
            if (currentPolygonPoints.isNotEmpty()) {
                val previousPoint = currentPolygonPoints.last()
                val distance = previousPoint.distanceToAsDouble(p)
                totalDistance += distance
            }
            Toast.makeText(this, "Total distance: $totalDistance", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Total distance: " + totalDistance)
            currentPolygonPoints.add(p)
            totalDistance = 0.0
            return true
        }
        return false
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        /*This is for to draw a circle in osm map where user enter the radius of circle in meters */
        if (p != null) {
            // Display an AlertDialog to get the radius
            val editText = EditText(this)
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Enter Radius")
                .setView(editText)
                .setPositiveButton("Draw Circle") { dialog, _ ->
                    val radiusString = editText.text.toString()
                    val radius = radiusString.toDoubleOrNull()

                    if (radius != null) {
                        // Draw a circle with the given radius
                        map.overlays.add(drawCircles(p, Paint(Paint.ANTI_ALIAS_FLAG), radius))
                    } else {
                        Toast.makeText(this, "Invalid radius", Toast.LENGTH_SHORT).show()
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }
        return false
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.data

            val path = getFilePath(fileuri!!)
            val spilitdata = path.split("\\.").toTypedArray()
            spilitdatapath = spilitdata[0]
            if (spilitdata.size > 0) {
                if (spilitdata[0].contains("xml")) {
                    Easting.clear()
                    Northing.clear()
                    readText(getFilePath(fileuri!!))
                } else if (spilitdata[0].contains("csv")) {
                    readCSVFile(getFilePath(fileuri!!))
                } else if (spilitdata[0].contains("dxf")) {
                    readdxf(getFilePath(fileuri!!))
                } else if (spilitdata[0].contains("kml")) {
                    // Load and parse the KML file
                    val kmlDocument = KmlDocument().apply {
                        parseKMLStream(FileInputStream(getFilePath(fileuri!!)), null)
                    }
                    // Plot the KML features on the map
                    plotKmlFeatures(kmlDocument)
                }
            }
        }
    }

    /// this method is used for getting file path from uri
    fun getFilePath(uri: Uri): String {
        val filepath = uri.path
        val filePath1 = filepath!!.split(":").toTypedArray()
        return Environment.getExternalStorageDirectory().path + "/" + filePath1[1]
    }

    /// reading csv file data
    fun readCSVFile(path: String?): String? {
        val filepath: String? = null
        val file = File(path)
        try {
            val scanner = Scanner(file)
            var temp_rows = scanner.nextLine()
            var splited_temp_rows = temp_rows.split(",").toTypedArray()
            val index_point_name = Arrays.asList(*splited_temp_rows).indexOf("Point_name")
            val index_easting = Arrays.asList(*splited_temp_rows).indexOf("Easting")
            val index_northing = Arrays.asList(*splited_temp_rows).indexOf("Northing")
            val index_elevation = Arrays.asList(*splited_temp_rows).indexOf("Elevation")
            //            int index_prefix = Arrays.asList(splited_temp_rows).indexOf("prefix");
            val index_prefix = Arrays.asList(*splited_temp_rows).indexOf("Prefix")
            //            int index_zone = Arrays.asList(splited_temp_rows).indexOf("zone");
            val index_zone = Arrays.asList(*splited_temp_rows).indexOf("Zone")
            while (scanner.hasNextLine()) {
                temp_rows = scanner.nextLine()
                splited_temp_rows = temp_rows.split(",").toTypedArray()
                val temp_data = ArrayList<String>()
                temp_data.add(splited_temp_rows[0])
                temp_data.add(splited_temp_rows[index_easting])
                temp_data.add(splited_temp_rows[index_northing])
                temp_data.add(splited_temp_rows[index_elevation])
                /*                temp_data.add(splited_temp_rows[index_prefix])
                temp_data.add(splited_temp_rows[index_zone])*/
                Csvpoint_list.add(temp_data)
            }
            readcsvpoint()
            Log.e("Csvpoint_list", Csvpoint_list[1].get(1).toString())
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
            Northing.add(index.toDouble())
            Easting.add(index1.toDouble())
        }
        convertNorthingEastingtoLatitudeLongitude()
    }

    fun convertNorthingEastingtoLatitudeLongitude() {
        val time1 = System.currentTimeMillis()
        for (i in Easting.indices) {
//            val converted = convertToLatLng(Easting[i], Northing[i], 43, false)
            val converted = convertToLatLng(Northing[i], Easting[i], 43, false)
            Latitude.add(converted.latitude)   // easting
            Longitude.add(converted.longitude) // northing
//            Log.d(TAG, "convertNorthingEastingtoLatitudeLongitude:=="+converted)
        }
        val time2 = System.currentTimeMillis()
        Log.d(TAG, "total_time_in_STEP1...: " + (time2 - time1))
        if (spilitdatapath.contains("xml")) {
            newface_pointlines()
        } else if (spilitdatapath.contains("csv")) {
            for (i in Latitude.indices) {
                geoPoints.add(LabelledGeoPoint(Latitude[i], Longitude[i], "Point #$i"))
            }
            plotpointonosm()
        }

    }

    fun convertToLatLng(x: Double, y: Double, zone: Int, southhemi: Boolean): LatLng {
        var x = x
        var y = y
        x -= 500000.0
        x /= SCALE_FACTOR

        /* If in southern hemisphere, adjust y accordingly. */if (southhemi) {
            y -= 10000000.0
        }
        y /= SCALE_FACTOR
        val cmeridian = getCentralMeridian(zone)
        return mapPointToLatLng(x, y, cmeridian)
    }

    fun getCentralMeridian(zone: Int): Double {
        return Math.toRadians(-183.0 + zone * 6.0)
    }

    fun mapPointToLatLng(x: Double, y: Double, lambda0: Double): LatLng {
        val phif: Double = getFootpointLatitude(y)
        val ep2 =
            ((Math.pow(MAJOR_RADIUS, 2.0) - Math.pow(MINOR_RADIUS, 2.0))
                    / Math.pow(MINOR_RADIUS, 2.0))
        val cf = Math.cos(phif)
        val nuf2 = ep2 * Math.pow(cf, 2.0)
        val Nf: Double = Math.pow(
            MAJOR_RADIUS,
            2.0
        ) / (MINOR_RADIUS * Math.sqrt(1 + nuf2))
        var Nfpow = Nf
        val tf = Math.tan(phif)
        val tf2 = tf * tf
        val tf4 = tf2 * tf2
        val x1frac = 1.0 / (Nfpow * cf)
        Nfpow *= Nf
        val x2frac = tf / (2.0 * Nfpow)
        Nfpow *= Nf
        val x3frac = 1.0 / (6.0 * Nfpow * cf)
        Nfpow *= Nf
        val x4frac = tf / (24.0 * Nfpow)
        Nfpow *= Nf
        val x5frac = 1.0 / (120.0 * Nfpow * cf)
        Nfpow *= Nf
        val x6frac = tf / (720.0 * Nfpow)
        Nfpow *= Nf
        val x7frac = 1.0 / (5040.0 * Nfpow * cf)
        Nfpow *= Nf
        val x8frac = tf / (40320.0 * Nfpow)
        val x2poly = -1.0 - nuf2
        val x3poly = -1.0 - 2 * tf2 - nuf2
        val x4poly =
            5.0 + 3.0 * tf2 + 6.0 * nuf2 - 6.0 * tf2 * nuf2 - 3.0 * (nuf2 * nuf2) - 9.0 * tf2 * (nuf2 * nuf2)
        val x5poly = 5.0 + 28.0 * tf2 + 24.0 * tf4 + 6.0 * nuf2 + 8.0 * tf2 * nuf2
        val x6poly = (-61.0 - 90.0 * tf2 - 45.0 * tf4 - 107.0 * nuf2
                + 162.0 * tf2 * nuf2)
        val x7poly = -61.0 - 662.0 * tf2 - 1320.0 * tf4 - 720.0 * (tf4 * tf2)
        val x8poly = 1385.0 + 3633.0 * tf2 + 4095.0 * tf4 + 1575 * (tf4 * tf2)
        val lat_rad = phif + x2frac * x2poly * (x * x) + x4frac * x4poly * Math.pow(
            x,
            4.0
        ) + x6frac * x6poly * Math.pow(x, 6.0) + x8frac * x8poly * Math.pow(x, 8.0)
        val lng_rad = lambda0 + x1frac * x + x3frac * x3poly * Math.pow(
            x,
            3.0
        ) + x5frac * x5poly * Math.pow(x, 5.0) + x7frac * x7poly * Math.pow(x, 7.0)
        var latt = 0.0
        var lonn = 0.0
        latt = Math.toDegrees(lat_rad)
        lonn = Math.toDegrees(lng_rad)
        var bdLat = BigDecimal.valueOf(latt)
        var bdLon = BigDecimal.valueOf(lonn)
        bdLat = bdLat.setScale(8, RoundingMode.DOWN)
        bdLon = bdLon.setScale(8, RoundingMode.DOWN)
        return LatLng(bdLat.toDouble(), bdLon.toDouble())
    }

    fun getFootpointLatitude(y: Double): Double {
        val n: Double = (MAJOR_RADIUS - MINOR_RADIUS) / (MAJOR_RADIUS + MINOR_RADIUS)
        val alpha_: Double = ((MAJOR_RADIUS + MINOR_RADIUS) / 2.0
                * (1 + Math.pow(n, 2.0) / 4 + Math.pow(n, 4.0) / 64))
        val y_ = y / alpha_
        val beta_ = (3.0 * n / 2.0 + -27.0 * Math.pow(n, 3.0) / 32.0
                + 269.0 * Math.pow(n, 5.0) / 512.0)
        val gamma_ = (21.0 * Math.pow(n, 2.0) / 16.0
                + -55.0 * Math.pow(n, 4.0) / 32.0)
        val delta_ = (151.0 * Math.pow(n, 3.0) / 96.0
                + -417.0 * Math.pow(n, 5.0) / 128.0)
        val epsilon_ = 1097.0 * Math.pow(n, 4.0) / 512.0
        return (y_ + beta_ * Math.sin(2.0 * y_)
                + gamma_ * Math.sin(4.0 * y_)
                + delta_ * Math.sin(6.0 * y_)
                + epsilon_ * Math.sin(8.0 * y_))
    }

    fun plotpointonosm() {
//        val poiIcon = resources.getDrawable(R.drawable.marker_poi_default)
        for (j in geoPoints.indices) {
            val startMarker = Marker(map)
            startMarker.position = geoPoints[j] as GeoPoint
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            startMarker.icon = resources.getDrawable(R.drawable.currentlocation)
            startMarker.title = "" + j
            startMarker.showInfoWindow()
            startMarker.setOnMarkerClickListener { marker, mapView ->
                // Get the coordinates of the double tap event
                val point = (marker.position) as GeoPoint
                rectanglePoints.add(point)
                trianglePoints.add(point)
                linePoints.add(point)
                // this is working
                /*                if (point != null) {
                    drawPolyline(point)
                }*/
                Toast.makeText(
                    this@OSMActivity2,
                    "Clicked point: ${point.latitude}, ${point.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
            map.overlays.add(startMarker)
            map.controller.setCenter(geoPoints[j])
        }

    }

    fun onDrawRectangleButtonClick() {
        // Draw the Rectangle when four points are selected
        if (rectanglePoints.size == 4) {
            drawRectangle()
            Toast.makeText(this, "RectangleClicked", Toast.LENGTH_SHORT).show()
        } else if (rectanglePoints.size > 4) {
            rectanglePoints.clear()
        } else {
            Toast.makeText(this, "Check the list size==" + rectanglePoints.size, Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun drawRectangle() {
        val polygon = Polygon(map)
        polygon.fillColor = Color.argb(100, 0, 0, 255)
        polygon.strokeColor = Color.BLUE
        polygon.strokeWidth = 1.0f
        // Add triangle points to the polygon
        for (point in rectanglePoints) {
            polygon.addPoint(point)
        }
        // Calculate the lengths of the sides
        val sideA = rectanglePoints[0].distanceToAsDouble(rectanglePoints[1])
        val sideB = rectanglePoints[1].distanceToAsDouble(rectanglePoints[2])
        val sideC = rectanglePoints[2].distanceToAsDouble(rectanglePoints[3])
        val sideD = rectanglePoints[3].distanceToAsDouble(rectanglePoints[0])
        // Calculate the area
        val area = sideA * sideB // base * height
        // Calculate the perimeter
        val perimeter = sideA + sideB + sideC + sideD
        // Print the calculated area and perimeter
        Toast.makeText(this, "Area: $area, Perimeter: $perimeter", Toast.LENGTH_SHORT).show()
        // Add polygon overlay to the MapView
        map.overlays.add(polygon)
        rectanglePoints.clear()
        // Refresh the MapView to update the display
        map.invalidate()
    }

    fun onDrawTriangleButtonClick() {
        // Draw the triangle when three points are selected
        if (trianglePoints.size == 3) {
            drawTriangle()
            Toast.makeText(this, "TriangleClicked", Toast.LENGTH_SHORT).show()
        } else if (trianglePoints.size > 3) {
            trianglePoints.clear()
        } else {
            Toast.makeText(this, "Check the list size==" + trianglePoints.size, Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun drawTriangle() {
        val polygon = Polygon(map)
        polygon.fillColor = Color.argb(100, 255, 0, 0)
        polygon.strokeColor = Color.RED
        polygon.strokeWidth = 1.0f
        // Add triangle points to the polygon
        for (point in trianglePoints) {
            polygon.addPoint(point)
        }
        // Calculate the lengths of the sides
        val sideA: Double = trianglePoints[0].distanceToAsDouble(trianglePoints[1])
        val sideB: Double = trianglePoints[1].distanceToAsDouble(trianglePoints[2])
        val sideC: Double = trianglePoints[2].distanceToAsDouble(trianglePoints[0])
        // Calculate the semi-perimeter
        val semiPerimeter = (sideA + sideB + sideC) / 2.0
        // Calculate the area using Heron's formula
        val area =
            Math.sqrt(semiPerimeter * (semiPerimeter - sideA) * (semiPerimeter - sideB) * (semiPerimeter - sideC))
        // Calculate the perimeter
        val perimeter = sideA + sideB + sideC
        // Print the calculated area and perimeter
        Log.d("Triangle", "Area: $area")
        Log.d("Triangle", "Perimeter: $perimeter")
        Toast.makeText(this, "Area:=$area\nPerimeter:=$perimeter", Toast.LENGTH_SHORT).show()
        // Add polygon overlay to the MapView
        map.overlays.add(polygon)
        trianglePoints.clear()
        // Refresh the MapView to update the display
        map.invalidate()
    }

    fun onDrawLineButtonClick() {
        // Draw the triangle when three points are selected
        if (linePoints.size == 2) {
            drawline()
            Toast.makeText(this, "LineClicked", Toast.LENGTH_SHORT).show()
        } else if (linePoints.size > 2) {
            linePoints.clear()
        } else {
            Toast.makeText(this, "Check the list size==" + trianglePoints.size, Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun drawline() {
        // Set the center of the map
        val startPoint =
            GeoPoint(linePoints[0].latitude, linePoints[0].longitude) // Start point coordinates
        map.controller.setCenter(startPoint)
        // Add a polyline between two points
        val endPoint =
            GeoPoint(linePoints[1].latitude, linePoints[1].longitude) // End point coordinates

        val polyline = Polyline()
        polyline.addPoint(startPoint)
        polyline.addPoint(endPoint)
        /*        polyline.getOutlinePaint().setPathEffect(
            DashPathEffect(
                floatArrayOf(10f, 10f),
                0f
            )
        )*/ // Set dash pattern for dotted line
        map.overlays.add(polyline)
        // Calculate the distance between two points
        val distance = calculateDistance(startPoint, endPoint)
        println("Distance: $distance m")

        map.invalidate()

    }

    private fun calculateDistance(startPoint: GeoPoint, endPoint: GeoPoint): Double {
        val earthRadius = 6371.0 // Radius of the Earth in kilometers

        val lat1 = startPoint.latitude * Math.PI / 180
        val lon1 = startPoint.longitude * Math.PI / 180

        val lat2 = endPoint.latitude * Math.PI / 180
        val lon2 = endPoint.longitude * Math.PI / 180

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distanceInKm = earthRadius * c
        val distanceInMeters = distanceInKm * 1000

        return distanceInMeters
    }

    private fun drawPolyline(geoPoint: GeoPoint) {
        Log.d("TAG", "addMarker: geoPoint: $geoPoint")
        /*        if (startMarker == null) {
            // Add start marker
            startMarker = Marker(map)
            startMarker?.position = geoPoint
            startMarker?.title = "Start"
        } else if (endMarker == null) {
            // Add end marker
            endMarker = Marker(map)
            endMarker?.position = geoPoint
            endMarker?.title = "End"
            // Draw polyline between start and end markers
            polyline = Polyline()
            polyline?.addPoint(startMarker?.position)
            polyline?.addPoint(endMarker?.position)
            polyline?.outlinePaint?.color = Color.RED
            map.overlays.add(polyline)
            // Invalidate the map view to redraw the overlays
            map.invalidate()
        }*/
        val marker = Marker(map)
        marker.position = geoPoint
        marker.title = "Point ${markers.size + 1}"
        markers.add(marker)

        if (markers.size > 1) {
            val previousMarker = markers[markers.size - 2]
            val polyline = Polyline()
            polyline.addPoint(previousMarker.position)
            polyline.addPoint(marker.position)
            polyline.outlinePaint.color = Color.RED
            map.overlays.add(polyline)
            polylines.add(polyline)
        }
        map.invalidate()
    }

    class drawCircles(
        private val geoPoint: GeoPoint,
        private var paint: Paint,
        private val radius: Double
    ) :
        Overlay() {
        override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
            val screenPoint = mapView.projection.toPixels(geoPoint, null)
            paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            val circleRadius = mapView.projection.metersToPixels(radius.toFloat())
            canvas.drawCircle(screenPoint.x.toFloat(), screenPoint.y.toFloat(), circleRadius, paint)
        }
    }

    /* From here xml file part start*/
    /// reading the land xml file
    fun readText(input: String?): String? {
        val file = File(input)
        val text = java.lang.StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                ArraysT = line!!.split("\n").toTypedArray()
                readNorthingEasting()
            }
            convertNorthingEastingtoLatitudeLongitude()
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }

    /// In this part Read the Northing and Easting From .xml files
    fun readNorthingEasting() {
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
                                        Easting.add(
                                            java.lang.Double.valueOf(
                                                point_line.substring(
                                                    st_index + 2,
                                                    index + 1
                                                )
                                            )
                                        )
                                        Northing.add(
                                            java.lang.Double.valueOf(
                                                point_line.substring(
                                                    index + 1,
                                                    index2 + 1
                                                )
                                            )
                                        )
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
        readfacepoint()
    }

    /// In this part we read the facepoint from the .xml file
    fun readfacepoint() {
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
                                        newFace_temp_pointline.add(
                                            Face_pointline.substring(
                                                face_index1 + 1,
                                                k
                                            )
                                        )
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
    }

    fun newface_pointlines() {
        val time3 = System.currentTimeMillis()
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

                val temp_data1 = ArrayList<Double>()


                temp_data1.add(Longitude[i1 - 1])
                temp_data1.add(Latitude[i1 - 1])

                temp_data1.add(Longitude[j1 - 1])
                temp_data1.add(Latitude[j1 - 1])

                temp_data1.add(Longitude[k1 - 1])
                temp_data1.add(Latitude[k1 - 1])

                facepoint_inCoordinate.add(temp_data1)
            }
        }
        val time4 = System.currentTimeMillis()
        Log.d(TAG, "total_time_in_STEP2...: " + (time4 - time3))
        plotxmlpointonosm()
    }

    fun plotxmlpointonosm() {
        XmltrianglePoint_map.clear()
        val time5 = System.currentTimeMillis()
        Log.d(TAG, "facepoint_inCoordinate: " + facepoint_inCoordinate)
        for (i in facepoint_inCoordinate.indices) {
            val temp_ListN = ArrayList<Double>()
            val temp_ListE = ArrayList<Double>()
            var temp_trianglePoint: MutableList<GeoPoint> = mutableListOf()
            val temp_newList = facepoint_inCoordinate[i]
            Log.d(TAG, "facepoint_inCoordinate: " + facepoint_inCoordinate[i])
            var i = 0
            while (i in temp_newList.indices) {
                temp_ListN.add(temp_newList.get(i))
                temp_ListE.add(temp_newList.get(i+1))
                Log.d(TAG, "tempPoints:"+temp_newList.get(i)+"== "+temp_newList.get(i+1))
                i = i + 2
            }
/*            for (k in temp_newList.indices) {
                if (k % 2 == 0) {
                    temp_ListN.add(temp_newList[k])
                } else {
                    temp_ListE.add(temp_newList[k])
                }
            }*/
            Log.d(TAG, "temp_ListE: " + temp_ListE + "\n" + temp_ListN)
            for (j in temp_ListE.indices) {
                temp_trianglePoint.add(GeoPoint(temp_ListE[j], temp_ListN[j]))
            }
//            XmltrianglePoint_map.put(i.toString(), temp_trianglePoint)
            val paint = Paint()
            paint.color = Color.BLUE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            for(l in temp_trianglePoint.indices) {
                map.overlays.add(drawlines(temp_trianglePoint,paint))
                map.controller.setCenter(temp_trianglePoint[l])
            }

            Log.d(TAG, "temp_trianglePoint:=" + temp_trianglePoint)
        }
        val time6 = System.currentTimeMillis()
        Log.d(TAG, "total_time_in_STEP3...: " + (time6 - time5))
      /*  val gson = Gson()
        val json1: String = gson.toJson(XmltrianglePoint_map)
        val editor = sharedpreferences.edit()
        editor.putString(Constants.XmltrianglePoint_map, json1)
        editor.apply()
        XmltrianglePoint_map.forEach { (key, value) ->
            val key = key
            val value = value
            val paint = Paint()
            paint.color = Color.BLUE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            for (l in value.indices) {
                map.overlays.add(drawlines(value, paint))
                map.controller.setCenter(value[l])
            }
        }*/
        val time7 = System.currentTimeMillis()
        Log.d(TAG, "total_time_in_STEP4...: " + (time7 - time5))
    }

    class drawlines(
        private val points: List<GeoPoint>,
        private var paint: Paint,
    ) :
        Overlay() {
        override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
            val startPoint = mapView.projection.toPixels(points[0], null)
            val endPoint = mapView.projection.toPixels(points[1], null)
            val thirdPoint = mapView.projection.toPixels(points[2], null)
            val path1 = Path()
            path1.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
            path1.lineTo(endPoint.x.toFloat(), endPoint.y.toFloat())
            path1.lineTo(thirdPoint.x.toFloat(), thirdPoint.y.toFloat())
            path1.lineTo(startPoint.x.toFloat(), startPoint.y.toFloat())
            canvas.drawPath(path1, paint)
            /*this code is also working*/
            /*            canvas.drawLine(startPoint.x.toFloat(), startPoint.y.toFloat(), endPoint.x.toFloat(), endPoint.y.toFloat(), paint)
            canvas.drawLine(endPoint.x.toFloat(), endPoint.y.toFloat(), thirdPoint.x.toFloat(), thirdPoint.y.toFloat(), paint)
            canvas.drawLine(thirdPoint.x.toFloat(), thirdPoint.y.toFloat(), startPoint.x.toFloat(), startPoint.y.toFloat(), paint)*/
        }
    }

    class drawpoints(
        private val geoPoint: IGeoPoint,
        private var paint: Paint,
    ) :
        Overlay() {
        override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
            val screenPoint = mapView.projection.toPixels(geoPoint, null)
            paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.FILL
            paint.strokeWidth = 2f
            canvas.drawCircle(screenPoint.x.toFloat(), screenPoint.y.toFloat(), 5F, paint)

        }
    }

    /* From here dxf file part start*/
    fun readdxf(input: String?): String? {

        val file = File(input)
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                temp_String.add(line!!)
            }
            Log.d(TAG, "readdxf: " + temp_String)
            entities_list.addAll(
                Arrays.asList(
                    "POINT",
                    "TEXT",
                    "MTEXT",
                    "LINE",
                    "ARC",
                    "CIRCLE",
                    "POLYLINE",
                    "LWPOLYLINE",
                    "ENDSEC",
                    "VERTEX"
                )
            )
            importDxf()
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }

    fun importDxf() {
        Log.d(TAG, "importDxf: entities_list" + entities_list)
        var start_counter = temp_String.indexOf("ENTITIES")
        val end_counter = temp_String.subList(start_counter, temp_String.size - 1)
            .indexOf("ENDSEC") + start_counter
        println("Start: $start_counter")
        println("End: $end_counter")
        while (start_counter < end_counter) {
            val current_word = temp_String[start_counter]
            if (!entities_list.contains(current_word)) {
                println("No ")
                start_counter++
                continue
            }
            if (current_word == "POINT") {
                var point_loop_flag = true
                var point_loop_counter = temp_String.subList(start_counter, temp_String.size - 1)
                    .indexOf("AcDbPoint") + start_counter
                val temp_point_list = ArrayList<String>()
                val point_match_list = ArrayList<String>()
                point_match_list.addAll(Arrays.asList(" 10", " 20", " 30"))
                while (point_loop_flag) {
                    if (entities_list.contains(temp_String[point_loop_counter + 1])) {
                        val point_prefix = "Pt_" + (point_map.size + 1)
                        point_map[point_prefix] = temp_point_list
                        start_counter = point_loop_counter
                        point_loop_flag = false
                        continue
                    }
                    val current_word_in_loop = temp_String[point_loop_counter]
                    if (point_match_list.contains(current_word_in_loop)) {
                        temp_point_list.add(temp_String[point_loop_counter + 1])
                        point_loop_counter += 1
                        continue
                    }
                    point_loop_counter += 1
                }
            }
            if (current_word == "MTEXT") {
                var mtext_loop_flag = true
                var mtext_loop_counter = temp_String.subList(start_counter, temp_String.size - 1)
                    .indexOf("AcDbMText") + start_counter
                val temp_mtext_list = java.util.ArrayList<String>()
                val mtext_match_list = java.util.ArrayList<String>()
                mtext_match_list.addAll(Arrays.asList(" 1", "  1"))
                while (mtext_loop_flag) {
                    if (entities_list.contains(temp_String[mtext_loop_counter + 1])) {
                        val mtext_prefix = "txt_" + (mtext_map.size + 1)
                        mtext_map[mtext_prefix] = temp_mtext_list
                        start_counter = mtext_loop_counter
                        mtext_loop_flag = false
                        continue
                    }
                    val current_word_in_loop = temp_String[mtext_loop_counter]
                    if (mtext_match_list.contains(current_word_in_loop)) {
                        temp_mtext_list.add(temp_String[mtext_loop_counter + 1])
                        mtext_loop_counter += 1
                        continue
                    }
                    mtext_loop_counter += 1
                }
            }
            if (current_word == "CIRCLE") {
                var circle_loop_flag = true
                var circle_loop_counter = temp_String.subList(start_counter, temp_String.size - 1)
                    .indexOf("AcDbCircle") + start_counter
                val temp_circle_list = java.util.ArrayList<String>()
                val circle_match_list = java.util.ArrayList<String>()
                circle_match_list.addAll(
                    Arrays.asList(
                        " 10",
                        " 20",
                        " 30",
                        " 40",
                        "  10",
                        "  20",
                        "  30",
                        "  40"
                    )
                )
                while (circle_loop_flag) {
                    if (entities_list.contains(temp_String[circle_loop_counter + 1])) {
                        val circle_prefix = "1Cr" + (circle_map.size + 1)
                        circle_map[circle_prefix] = temp_circle_list
                        start_counter = circle_loop_counter
                        circle_loop_flag = false
                        continue
                    }
                    val current_word_in_loop = temp_String[circle_loop_counter]
                    if (circle_match_list.contains(current_word_in_loop)) {
                        temp_circle_list.add(temp_String[circle_loop_counter + 1])
                        circle_loop_counter += 1
                        continue
                    }
                    circle_loop_counter += 1
                }
            }
            if (current_word == "LINE") {
                var line_loop_flag = true
                var line_loop_counter = temp_String.subList(start_counter, temp_String.size - 1)
                    .indexOf("AcDbLine") + start_counter
                val temp_line_list = java.util.ArrayList<String>()
                val line_match_list = java.util.ArrayList<String>()
                line_match_list.addAll(
                    Arrays.asList(
                        " 10",
                        " 20",
                        " 30",
                        "  10",
                        "  20",
                        "  30",
                        " 11",
                        " 21",
                        " 31",
                        "  11",
                        "  21",
                        "  31"
                    )
                )
                while (line_loop_flag) {
                    if (entities_list.contains(temp_String[line_loop_counter + 1])) {
                        val line_prefix = "Ln" + (line_map.size + 1)
                        line_map[line_prefix] = temp_line_list
                        start_counter = line_loop_counter
                        line_loop_flag = false
                        continue
                    }
                    val current_word_in_loop = temp_String[line_loop_counter]
                    if (line_match_list.contains(current_word_in_loop)) {
                        temp_line_list.add(temp_String[line_loop_counter + 1])
                        line_loop_counter += 1
                        continue
                    }
                    line_loop_counter += 1
                }
            }
            if (current_word == "ARC") {
                var arc_loop_flag = true
                var arc_loop_counter = temp_String.subList(start_counter, temp_String.size - 1)
                    .indexOf("AcDbCircle") + start_counter
                val temp_arc_list = java.util.ArrayList<String>()
                val arc_match_list = java.util.ArrayList<String>()
                arc_match_list.addAll(
                    Arrays.asList(
                        " 10",
                        " 20",
                        " 30",
                        " 40",
                        " 50",
                        " 51",
                        "  10",
                        "  20",
                        "  30",
                        "  40",
                        "  50",
                        "  51"
                    )
                )
                while (arc_loop_flag) {
                    if (entities_list.contains(temp_String[arc_loop_counter + 1])) {
                        val arc_prefix = "Ar" + (arc_map.size + 1)
                        arc_map[arc_prefix] = temp_arc_list
                        start_counter = arc_loop_counter
                        arc_loop_flag = false
                        continue
                    }
                    val current_word_in_loop = temp_String[arc_loop_counter]
                    if (arc_match_list.contains(current_word_in_loop)) {
                        temp_arc_list.add(temp_String[arc_loop_counter + 1])
                        arc_loop_counter += 1
                        continue
                    }
                    arc_loop_counter += 1
                }
            }
            if (current_word == "LWPOLYLINE") {
                var lwpolyline_loop_flag = true
                var lwpolyline_loop_counter =
                    temp_String.subList(start_counter, temp_String.size - 1)
                        .indexOf("AcDbPolyline") + start_counter
                val temp_lwpolyline_list = java.util.ArrayList<String>()
                val lwpolyline_match_list = java.util.ArrayList<String>()
                lwpolyline_match_list.addAll(Arrays.asList(" 10", " 20", "  10", "  20"))
                while (lwpolyline_loop_flag) {
                    if (entities_list.contains(temp_String[lwpolyline_loop_counter + 1])) {
                        val lwpolyline_prefix = "Pl" + (lwpolyline_map.size + 1) + "_"
                        lwpolyline_map[lwpolyline_prefix] = temp_lwpolyline_list
                        start_counter = lwpolyline_loop_counter
                        lwpolyline_loop_flag = false
                        continue
                    }
                    val current_word_in_loop = temp_String[lwpolyline_loop_counter]
                    if (lwpolyline_match_list.contains(current_word_in_loop)) {
                        temp_lwpolyline_list.add(temp_String[lwpolyline_loop_counter + 1])
                        lwpolyline_loop_counter += 1
                        continue
                    }
                    lwpolyline_loop_counter += 1
                }
            }
            if (current_word == "POLYLINE") {
                var is_polygon_flag = false
                var polyline_loop_flag = true
                var polyline_loop_counter = temp_String.subList(start_counter, temp_String.size - 1)
                    .indexOf("AcDb3dPolyline") + start_counter
                val temp_polyline_list = ArrayList<ArrayList<String>>()
                var is_polygon_flagindex = temp_String.subList(start_counter, temp_String.size - 1)
                    .indexOf("  70") + start_counter + 1
                if (temp_String.elementAt(is_polygon_flagindex).equals("1")) {
                    Log.d(TAG, "importDxf:  POLYGON ${temp_String.elementAt(is_polygon_flagindex)}")
                    is_polygon_flag = true
                    Log.d(TAG, "importDxf:  POLYGON $is_polygon_flag")

                }
                Log.d(TAG, "importDxf: is_polygon_flag $is_polygon_flagindex ")
                while (polyline_loop_flag) {
                    if (entities_list.subList(0, entities_list.size - 1)
                            .contains(temp_String[polyline_loop_counter + 1])
                    ) {
                        if (is_polygon_flag) {
                            val polygon_prefix = "Pg" + (polygon_map.size + 1) + "_"
                            polygon_map[polygon_prefix] = temp_polyline_list
                        } else {
                            val polyline_prefix = "Pl" + (polyline_map.size + 1) + "_"
                            polyline_map[polyline_prefix] = temp_polyline_list
                        }
                        start_counter = polyline_loop_counter
                        polyline_loop_flag = false
                        continue
                    }
                    val current_word_in_loop = temp_String[polyline_loop_counter]
                    if (current_word_in_loop == "VERTEX") {
                        var vertex_loop_flag = true
                        var vertex_loop_counter =
                            temp_String.subList(polyline_loop_counter, temp_String.size - 1)
                                .indexOf("AcDbVertex") + polyline_loop_counter
                        val temp_vertex_list = ArrayList<String>()
                        val vertex_match_list = ArrayList<String>()
                        vertex_match_list.addAll(
                            Arrays.asList(
                                " 10",
                                " 20",
                                " 30",
                                "  10",
                                "  20",
                                "  30"
                            )
                        )
                        while (vertex_loop_flag) {
                            if (temp_String[vertex_loop_counter + 1] == "SEQEND" || temp_String[vertex_loop_counter + 1] == "VERTEX") {
                                temp_polyline_list.add(temp_vertex_list)
                                polyline_loop_counter = vertex_loop_counter
                                vertex_loop_flag = false
                                continue
                            }
                            val current_word_in_inner_loop = temp_String[vertex_loop_counter]
                            if (vertex_match_list.contains(current_word_in_inner_loop)) {
                                temp_vertex_list.add(temp_String[vertex_loop_counter + 1])
                                vertex_loop_counter += 1
                                continue
                            }
                            vertex_loop_counter += 1
                        }
                    }
                    polyline_loop_counter += 1
                }
            }
            start_counter++
        }
        /*        println("point_map: " + point_map.entries)
        println("mtext_map: " + mtext_map.entries)
        println("circle_map: " + circle_map.entries)
        println("line_map: " + line_map.entries)
        println("arc_map: " + arc_map.entries)
        println("lwpolyline_map: " + lwpolyline_map.entries)
        println("polyline_map: " + polyline_map.entries)
        println("polygon_map: " + polygon_map.entries)*/
//        println("lwpolyline_map: " + lwpolyline_map.entries)
        readlwpolyline_map()
        readcircle_map()
        readpolygon_map()
//        readline_map()
    }

    fun readlwpolyline_map() {
        lwpolyline_map.forEach { (key, value) ->
            val key = key
            val value = value
            val temp_ListN = ArrayList<String>()
            val temp_ListE = ArrayList<String>()
            val temp_Latitude = ArrayList<Double>()
            val temp_Longitude = ArrayList<Double>()
            var temp_linePoint: MutableList<GeoPoint> = mutableListOf()

            for (k in value.indices) {
                if (k % 2 == 0) {
                    temp_ListE.add(value[k])
                } else {
                    temp_ListN.add(value[k])
                }
            }
            for (i in temp_ListE.indices) {
                val converted =
                    convertToLatLng(temp_ListE[i].toDouble(), temp_ListN[i].toDouble(), 43, false)
                temp_Latitude.add(converted.latitude)   // easting
                temp_Longitude.add(converted.longitude) // northing
            }
            for (j in temp_Latitude.indices) {
                temp_linePoint.add(GeoPoint(temp_Latitude[j], temp_Longitude[j]))
            }
            Newlwpolyline_map.put(key, temp_linePoint)

            /*            for(l in temp_linePoint.indices) {
                map.overlays.add(drawlwpolyline(temp_linePoint,Paint(Paint.ANTI_ALIAS_FLAG)))
                map.controller.setCenter(temp_linePoint[l])
            }*/

            /*            Log.d(TAG, "temp_ListEN: "+temp_ListE+"\n"+temp_ListN)
            Log.d(TAG, "temp_Latitude: "+temp_Latitude+"\n"+temp_Longitude)
            Log.d(TAG, "temp_linePoint: "+temp_linePoint)*/
        }
        Log.d(TAG, "Newlwpolyline_map:=" + Newlwpolyline_map)
        val gson = Gson()
        val json1: String = gson.toJson(Newlwpolyline_map)
        val editor = sharedpreferences.edit()
        editor.putString(Constants.Newlwpolyline_map, json1)
        editor.apply()
        Newlwpolyline_map.forEach { (key, value) ->
            val key = key
            val value = value
            val paint = Paint()
            paint.color = Color.GREEN
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            for (l in value.indices) {
                map.overlays.add(drawlwpolyline(value, paint))
                map.controller.setCenter(value[l])
            }
        }

    }

    class drawlwpolyline(
        private val points: List<GeoPoint>,
        private var paint: Paint,
    ) :
        Overlay() {
        override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
            val path = Path()
            for (i in 0 until points.size) {
                val geoPoint = points[i]
                val point = mapView.projection.toPixels(geoPoint, null)
                val x = point.x.toFloat()
                val y = point.y.toFloat()

                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            canvas.drawPath(path, paint)
        }
    }

    fun readcircle_map() {
        circle_map.forEach { (key, value) ->
            val key = key
            val value = value
            val temp_ListE = ArrayList<String>()
            val temp_ListN = ArrayList<String>()
            val temp_radius = ArrayList<String>()
            val temp_Latitude = ArrayList<Double>()
            val temp_Longitude = ArrayList<Double>()
            val temp_circlePoint: MutableList<GeoPoint> = mutableListOf()
            val temp_hashmap = HashMap<ArrayList<String>, MutableList<GeoPoint>>()

            for (k in value.indices) {
                temp_ListE.add(value[0])
                temp_ListN.add(value[1])
                temp_radius.add(value[3])
            }
            for (i in temp_ListE.indices) {
                val converted =
                    convertToLatLng(temp_ListE[i].toDouble(), temp_ListN[i].toDouble(), 43, false)
                temp_Latitude.add(converted.latitude)   // easting
                temp_Longitude.add(converted.longitude) // northing
            }
            for (j in temp_Latitude.indices) {
                temp_circlePoint.add(GeoPoint(temp_Latitude[j], temp_Longitude[j]))
            }
            temp_hashmap.put(temp_radius, temp_circlePoint)
            Newcircle_map.put(key, temp_hashmap)
            /*            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            for(l in temp_circlePoint.indices) {
                map.overlays.add(drawcircle(temp_circlePoint,paint,temp_radius))
                map.controller.setCenter(temp_circlePoint[l])
            }*/
        }
        Newcircle_map.forEach { (key, value) ->
            val key = key
            val value = value
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            value.forEach { (key, value) ->
                val temp_key = key
                val temp_value = value
                for (l in temp_value.indices) {
                    map.overlays.add(drawcircle(temp_value, paint, temp_key))
                    map.controller.setCenter(value[l])
                }
            }
        }
        Log.d(TAG, "readcircle_map: " + Newcircle_map)
    }

    class drawcircle(
        private val points: List<GeoPoint>,
        private var paint: Paint,
        private var radius: List<String>,
    ) :
        Overlay() {
        override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
            for (i in 0 until points.size) {
                val geoPoint = points[i]
                val point = mapView.projection.toPixels(geoPoint, null)
                val x = point.x.toFloat()
                val y = point.y.toFloat()
                val newradius = mapView.getProjection().metersToPixels(radius[i].toFloat())
                canvas.drawCircle(x, y, newradius, paint)
            }


        }
    }

    fun readpolygon_map() {
        polygon_map.forEach { (key, value) ->
            val key = key
            val value = value
            val paint = Paint()
            paint.color = Color.GREEN
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 40f

            val temp_ListN = ArrayList<String>()
            val temp_ListE = ArrayList<String>()
            val temp_Latitude = ArrayList<Double>()
            val temp_Longitude = ArrayList<Double>()
            var temp_polygonPoint: MutableList<GeoPoint> = mutableListOf()
            for (i in value.indices) {
                for (j in value[i].indices) {
                    temp_ListE.add(value[i][0])
                    temp_ListN.add(value[i][1])
                }
            }
            for (i in temp_ListE.indices) {
                val converted =
                    convertToLatLng(temp_ListE[i].toDouble(), temp_ListN[i].toDouble(), 43, false)
                temp_Latitude.add(converted.latitude)   // easting
                temp_Longitude.add(converted.longitude) // northing
            }
            for (j in temp_Latitude.indices) {
                temp_polygonPoint.add(GeoPoint(temp_Latitude[j], temp_Longitude[j]))
            }

            Newpolygon_map.put(key, temp_polygonPoint)
            /*            Log.d(TAG, "readpolygon_map: "+temp_polygonPoint)

            for(l in temp_polygonPoint.indices) {
                map.overlays.add(drawlwpolyline(temp_polygonPoint,paint))
                map.controller.setCenter(temp_polygonPoint[l])
            }*/
        }
        Newpolygon_map.forEach { (key, value) ->
            val key = key
            val value = value
            val paint = Paint()
            paint.color = Color.GREEN
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            for (l in value.indices) {
                map.overlays.add(drawlwpolyline(value, paint))
                map.controller.setCenter(value[l])
            }
        }
    }

    fun readline_map() {
        line_map.forEach { (key, value) ->
            val key = key
            val value = value
//            Log.d(TAG, "readline_map: "+"key: $key\n"+"value: $value")
            val temp_ListN = ArrayList<String>()
            val temp_ListE = ArrayList<String>()
            val temp_Latitude = ArrayList<Double>()
            val temp_Longitude = ArrayList<Double>()
            var temp_linePoint: MutableList<GeoPoint> = mutableListOf()
            val paint = Paint()
            paint.color = Color.BLUE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            for (i in value) {
                temp_ListE.add(value[0])
                temp_ListE.add(value[3])
                temp_ListN.add(value[1])
                temp_ListN.add(value[4])
            }
            Log.d(TAG, "readline_map: " + temp_ListN + " values: " + temp_ListE)
            /*            for (i in temp_ListE.indices) {
                val converted = convertToLatLng(temp_ListE[i].toDouble(), temp_ListN[i].toDouble(), 43, false)
                temp_Latitude.add(converted.latitude)   // easting
                temp_Longitude.add(converted.longitude) // northing
            }
            for(j in temp_Latitude.indices) {
                temp_linePoint.add(GeoPoint(temp_Latitude[j], temp_Longitude[j]))
            }
            Newline_map.put(key,temp_linePoint)
            Log.d(TAG, "readline_map: "+temp_linePoint)
            for(l in temp_linePoint.indices) {
                map.overlays.add(drawlwpolyline(temp_linePoint,paint))
                map.controller.setCenter(temp_linePoint[l])
            }*/
        }
        Log.d(TAG, "Newline_map: " + Newline_map)

        /*        Newline_map.forEach{(key, value) ->
            val key = key
            val value = value
            val paint = Paint()
            paint.color = Color.BLUE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            for(l in value.indices) {
                map.overlays.add(drawdxflines(value,paint))
                map.controller.setCenter(value[l])
            }
        }*/
    }

    class drawdxflines(
        private val points: List<GeoPoint>,
        private var paint: Paint,
    ) :
        Overlay() {
        override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
            val startPoint = mapView.projection.toPixels(points[0], null)
            val endPoint = mapView.projection.toPixels(points[1], null)
            /*this code is also working*/
            canvas.drawLine(
                startPoint.x.toFloat(),
                startPoint.y.toFloat(),
                endPoint.x.toFloat(),
                endPoint.y.toFloat(),
                paint
            )
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /*Read a Kml File*/
    private fun plotKmlFeatures(kmlDocument: KmlDocument) {
        val kmlOverlay = kmlDocument.mKmlRoot.buildOverlay(map, null, null, kmlDocument)
        map.getOverlays().add(kmlOverlay)
        map.invalidate()
    }

    /*loadGeoJsonFile*/
    private fun loadGeoJsonFile(fileName: String): String? {
        try {
            val assetManager: AssetManager = applicationContext.assets
            val inputStream = assetManager.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.use { it.readText() }
            return jsonString
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /*29-06-2023*/
    private fun drawPolygonOnMap() {
        val polygonOverlay = Polygon()
        polygonOverlay.points = currentPolygonPoints
        map.overlays.add(polygonOverlay)
        map.invalidate()
        // Clear the current polygon points for future drawings
        currentPolygonPoints.clear()
    }



}