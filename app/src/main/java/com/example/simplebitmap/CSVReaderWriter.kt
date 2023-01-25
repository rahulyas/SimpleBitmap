package com.example.simplebitmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class CSVReaderWriter : AppCompatActivity() {


    var mText: String? = null
    var TextPath: TextView? = null
    var textA = StringBuilder()
    var textB = StringBuilder()
    var textC = StringBuilder()
    var textD = StringBuilder()
    var textD1 = StringBuilder()
    var textE = StringBuilder()
    private val WRITE_EXTERNAL_STORAGE_CODE = 1
    var TAG = "CSVActivity"
    private val PERMISSION_REQUEST_STORAGE = 1000
    private val PICK_TEXT = 101
    var fileuri: Uri? = null
    var point_list = ArrayList<ArrayList<String>>()
    var shape_list = ArrayList<ArrayList<String>>()
    var arrayListeasting = ArrayList<Double>()
    var arrayListnorthing = ArrayList<Double>()

//    var point_data = HashMap<String, ArrayList<Double>>()
    var point_data: HashMap<String, List<Double>> = HashMap<String, List<Double>>()

    var minX: Double? = null
    var maxX: Double? = null
    var minY: Double? = null
    var maxY: Double? = null

    ////////////////////////////////////////////////////////////////
    var main = HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_csvreader_writer)

        val browse = findViewById<Button>(R.id.buttonnew)
        val save = findViewById<Button>(R.id.buttonsave)

        TextPath = findViewById(R.id.textView_csvResult)

        requestpermission()

        browse.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select CSV file"), PICK_TEXT)
        })

        save.setOnClickListener(View.OnClickListener {
            Savetotext(textB.toString())
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.data
            TextPath!!.text = readText(getFilePath(fileuri!!))
            val path = getFilePath(fileuri!!)
            val spilitdata = path.split("\\.").toTypedArray()
            if (spilitdata.size > 1) {
                if (spilitdata[1].contains("txt")) {
                    readText(getFilePath(fileuri!!))
                } else if (spilitdata[1].contains("csv")) {
                    readCSVFile(getFilePath(fileuri!!))
                }
            }
        }

//            Toast.makeText(this, "GetFile Name"+ getFileName(text_uri), Toast.LENGTH_SHORT).show();
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

    /// reading file data
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
            val index_prefix = Arrays.asList(*splited_temp_rows).indexOf("prefix")
            val index_zone = Arrays.asList(*splited_temp_rows).indexOf("zone")
            while (scanner.hasNextLine()) {
                temp_rows = scanner.nextLine()
                splited_temp_rows = temp_rows.split(",").toTypedArray()
                val temp_data = java.util.ArrayList<String>()
                temp_data.add(splited_temp_rows[index_point_name])
                temp_data.add(splited_temp_rows[index_easting])
                temp_data.add(splited_temp_rows[index_northing])
                temp_data.add(splited_temp_rows[index_elevation])
                temp_data.add(splited_temp_rows[index_prefix])
                temp_data.add(splited_temp_rows[index_zone])
                val prefix = splited_temp_rows[index_prefix]
                point_list.add(temp_data)
                if (prefix.startsWith("Ln") || prefix.startsWith("Pl") || prefix.startsWith("Pg") || prefix.startsWith(
                        "Sq"
                    ) || prefix.startsWith("1Cr") || prefix.startsWith("2Cr") || prefix.startsWith("Ar")
                ) {
                    shape_list.add(temp_data)
                } else {
                }
            }
            Step1()
            Step2()
            Step3()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
        return filepath
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
                text.append("\n")
            }
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }

////////////////////////////////////// Write file data //////////////////////////
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Savetotext(mText)
                } else {
                    Toast.makeText(baseContext, "Storage Permiison Request", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun Savetotext(s: String?) {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(
            System.currentTimeMillis()
        )
        try {
            var tempPartA: String? = ""
            try {
                val inputStream = assets.open("part_a.txt")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                tempPartA = String(buffer)
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ")
            }
            textA.delete(0, textA.length)
            textA.append(tempPartA)
            textA.append("\n")
            var tempPartC: String? = ""
            try {
                val inputStream = assets.open("part_c.txt")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                tempPartC = String(buffer)
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ")
            }
            textC.delete(0, textC.length)
            textC.append(tempPartC)
            textC.append("\n")
            var tempPartE: String? = ""
            try {
                val inputStream = assets.open("part_e.txt")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                tempPartE = String(buffer)
            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ")
            }
            textE.delete(0, textE.length)
            textE.append(tempPartE)
            textE.append("\n")
            val path = Environment.getExternalStorageDirectory()

//            val path = File("/storage/emulated/0/Android/data/com.apogee.surveydemo/files/projects/")
            val dir = File(path, "CSVREADER")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val filename = "MyFile_$timeStamp.dxf"
            val file = File(dir, filename)
            Log.d(TAG, "onActivityResult  MyFile_: " + file.absolutePath)
            val writer = FileWriter(file.absolutePath)
            val fw = BufferedWriter(writer)
            //            fw.write(String.valueOf(sb));
            fw.append(textA.toString())
            fw.append(textB.toString())
            fw.append(textC.toString())
            fw.append(textD.toString())
            fw.append(textD1.toString())
            fw.append(textE.toString())
            fw.close()
            //display file saved message
            Toast.makeText(
                baseContext, filename + "Saved \n" + path,
                Toast.LENGTH_SHORT
            ).show()
            Log.i("Save", "File saved successfully!$path")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error")
        }
    }

    fun Step1() {
        Log.d(TAG, "point_list: " + point_list.size + "=======" + point_list[0])
        // ArrayList<Double> temp_point_coord = new ArrayList<>();
        Log.d("timeCheck-", System.currentTimeMillis().toString())
        for (k in point_list.indices) {
            point_data[point_list[k][0]] = Arrays.asList(point_list[k][1].toDouble(), point_list[k][2].toDouble(), point_list[k][3].toDouble())
            arrayListeasting.add(point_list[k][1].toDouble())
            arrayListnorthing.add(point_list[k][2].toDouble())
        }
        Log.d("timeCheck--", System.currentTimeMillis().toString())

////////////////////////////////////////////////////////////////
        for (k in shape_list.indices) {
//                Log.i("Point_list",""+String.valueOf(point_list.get(k)));
            val shape_map = HashMap<String, HashMap<String, ArrayList<String>>>()
            val point_map = HashMap<String, ArrayList<String>>()
            val name_map = HashMap<String, ArrayList<String>>()
            val point_name = shape_list[k][0]
            val easting = shape_list[k][1].toDouble()
            val northing = shape_list[k][2].toDouble()
            val elevation = shape_list[k][3].toDouble()
            val prefix = shape_list[k][4]
            val prefix_splitted = prefix.split("_").toTypedArray()
            val prefix_start = prefix_splitted[0].substring(0, prefix_splitted[0].length - 1)
            val point_name_array = ArrayList<String>()
            val point_coords_array = ArrayList<String>()
            point_coords_array.add(easting.toString())
            point_coords_array.add(northing.toString())
            point_coords_array.add(elevation.toString())
            point_name_array.add(point_name)
            when (prefix_start) {
                "Ln" -> {
                    if (prefix_splitted[1] == "st") point_map[prefix_splitted[1]] =
                        point_coords_array else if (prefix_splitted[1] == "en") point_map[prefix_splitted[1]] =
                        point_coords_array else {
                        Log.i(
                            " LINE SHAPE ERROR",
                            "Invalid LINE Prefix End type" + prefix_splitted[1]
                        )
                        break
                    }
                    name_map[point_name] = point_name_array
                    shape_map["point_coords"] = point_map
                    shape_map["name"] = name_map
                    if (main.containsKey(prefix_splitted[0])) main[prefix_splitted[0]]!!["point_coords"]!![prefix_splitted[1]] =
                        point_coords_array else main[prefix_splitted[0]] =
                        shape_map
                }
                "Pl" -> {
                    point_map[prefix_splitted[1]] = point_coords_array
                    name_map[point_name] = point_name_array
                    shape_map["point_coords"] = point_map
                    shape_map["name"] = name_map
                    if (main.containsKey(prefix_splitted[0])) main[prefix_splitted[0]]!!["point_coords"]!![prefix_splitted[1]] =
                        point_coords_array else main[prefix_splitted[0]] =
                        shape_map
                }
                "Pg" -> {
                    point_map[prefix_splitted[1]] = point_coords_array
                    name_map[point_name] = point_name_array
                    shape_map["point_coords"] = point_map
                    shape_map["name"] = name_map
                    if (main.containsKey(prefix_splitted[0])) main[prefix_splitted[0]]!!["point_coords"]!![prefix_splitted[1]] =
                        point_coords_array else main[prefix_splitted[0]] =
                        shape_map
                }
                "Sq" -> {
                    if (prefix_splitted[1] == "t") point_map[prefix_splitted[1]] =
                        point_coords_array else if (prefix_splitted[1] == "b") point_map[prefix_splitted[1]] =
                        point_coords_array else {
                        Log.i(
                            " SQUARE SHAPE ERROR",
                            "Invalid SQUARE Prefix End type" + prefix_splitted[1]
                        )
                        break
                    }
                    name_map[point_name] = point_name_array
                    shape_map["point_coords"] = point_map
                    shape_map["name"] = name_map
                    if (main.containsKey(prefix_splitted[0])) main[prefix_splitted[0]]!!["point_coords"]!![prefix_splitted[1]] =
                        point_coords_array else main[prefix_splitted[0]] =
                        shape_map
                }
                "1Cr" -> {
                    if (prefix_splitted[1] == "c") point_map[prefix_splitted[1]] =
                        point_coords_array else if (prefix_splitted[1] == "r") point_map[prefix_splitted[1]] =
                        point_coords_array else {
                        Log.i(
                            " 1 CIRCLE SHAPE ERROR",
                            "Invalid CIRCLE Prefix End type" + prefix_splitted[1]
                        )
                        break
                    }
                    name_map[point_name] = point_name_array
                    shape_map["point_coords"] = point_map
                    shape_map["name"] = name_map
                    if (main.containsKey(prefix_splitted[0])) main[prefix_splitted[0]]!!["point_coords"]!![prefix_splitted[1]] =
                        point_coords_array else main[prefix_splitted[0]] =
                        shape_map
                }
                "2Cr" -> {
                    if (prefix_splitted[1] == "1") point_map[prefix_splitted[1]] =
                        point_coords_array else if (prefix_splitted[1] == "2") point_map[prefix_splitted[1]] =
                        point_coords_array else if (prefix_splitted[1] == "3") point_map[prefix_splitted[1]] =
                        point_coords_array else {
                        Log.i(
                            " 2 CIRCLE SHAPE ERROR",
                            "Invalid CIRCLE Prefix End type" + prefix_splitted[1]
                        )
                        break
                    }
                    name_map[point_name] = point_name_array
                    shape_map["point_coords"] = point_map
                    shape_map["name"] = name_map
                    if (main.containsKey(prefix_splitted[0])) main[prefix_splitted[0]]!!["point_coords"]!![prefix_splitted[1]] =
                        point_coords_array else main[prefix_splitted[0]] =
                        shape_map
                }
                "Ar" -> {
                    if (prefix_splitted[1] == "st") point_map[prefix_splitted[1]] =
                        point_coords_array else if (prefix_splitted[1] == "m") point_map[prefix_splitted[1]] =
                        point_coords_array else if (prefix_splitted[1] == "en") point_map[prefix_splitted[1]] =
                        point_coords_array else {
                        Log.i("ARC SHAPE ERROR", "Invalid ARC Prefix End type" + prefix_splitted[1])
                        break
                    }
                    name_map[point_name] = point_name_array
                    shape_map["point_coords"] = point_map
                    shape_map["name"] = name_map
                    if (main.containsKey(prefix_splitted[0])) main[prefix_splitted[0]]!!["point_coords"]!![prefix_splitted[1]] =
                        point_coords_array else main[prefix_splitted[0]] =
                        shape_map
                }
            }
        }
    }
    ////////////////////////////////
    fun Step2() {
//            System.out.println("Key: "+ main.get("Pg1").get("point_coords").get("1"));
        for (keyValue: Map.Entry<*, *> in main.entries) {
            val key = keyValue.key.toString()
            //            Log.i("key -->> ", key);
            if (key.startsWith("Sq")) if (main[key]!!["point_coords"]!!.size == 2) {
                val top_point = main[key]!!["point_coords"]!!["t"]
                val top_point_X = top_point!![0].toDouble()
                val top_point_Y = top_point[1].toDouble()
                val bottom_point = main[key]!!["point_coords"]!!["b"]
                val bottom_point_X = bottom_point!![0].toDouble()
                val bottom_point_Y = bottom_point[1].toDouble()
                val diagonal_len = Math.sqrt(
                    Math.pow(top_point_X - bottom_point_X, 2.0) + Math.pow(
                        top_point_Y - bottom_point_Y, 2.0
                    )
                )
                val radius = diagonal_len / 1.41421
                val mid_point_distance = diagonal_len / 2
                val height = Math.sqrt(Math.pow(radius, 2.0)) - Math.pow(mid_point_distance, 2.0)
                val temp_mid_point_X = (bottom_point_X + mid_point_distance * (top_point_X - bottom_point_X)) / diagonal_len
                val temp_mid_point_Y = (bottom_point_Y + mid_point_distance * (top_point_Y - bottom_point_Y)) / diagonal_len
                val x3 = temp_mid_point_X + height * (top_point_Y - bottom_point_Y) / diagonal_len
                val y3 = temp_mid_point_Y - height * (top_point_X - bottom_point_X) / diagonal_len
                val x4 = temp_mid_point_X - height * (top_point_Y - bottom_point_Y) / diagonal_len
                val y4 = temp_mid_point_Y + height * (top_point_X - bottom_point_X) / diagonal_len
                val temp_topPoints = ArrayList<String>()
                val temp_bottomPoints = ArrayList<String>()
                val temp_x3 = ArrayList<String>()
                val temp_x4 = ArrayList<String>()
                temp_topPoints.add(String.format("%.10f", top_point_X))
                temp_topPoints.add(String.format("%.10f", top_point_Y))
                temp_bottomPoints.add(String.format("%.10f", bottom_point_X))
                temp_bottomPoints.add(String.format("%.10f", bottom_point_Y))
                temp_x3.add(String.format("%.10f", x3))
                temp_x3.add(String.format("%.10f", y3))
                temp_x4.add(String.format("%.10f", x4))
                temp_x4.add(String.format("%.10f", y4))
                main[key]!!["point_coords"]!!.remove("t")
                main[key]!!["point_coords"]!!.remove("b")
                main[key]!!["point_coords"]!!["1"] = temp_topPoints
                main[key]!!["point_coords"]!!["2"] = temp_bottomPoints
                main[key]!!["point_coords"]!!["3"] = temp_x3
                main[key]!!["point_coords"]!!["4"] = temp_x4
            } else println("InSuffecient Points in Square GEOM...") else if (key.startsWith("1Cr")) if (main[key]!!["point_coords"]!!.size == 2) {
                val start_point = main[key]!!["point_coords"]!!["c"]
                val start_point_0 = start_point!![0].toDouble()
                val start_point_1 = start_point[1].toDouble()
                val radius_point = main[key]!!["point_coords"]!!["r"]
                val radius_point_0 = radius_point!![0].toDouble()
                val radius_point_1 = radius_point[1].toDouble()
                val radius = Math.sqrt(Math.pow(start_point_0 - radius_point_0, 2.0) + Math.pow(start_point_1 - radius_point_1, 2.0))
                val temp_center_point = ArrayList<String>()
                val temp_radius = ArrayList<String>()
                temp_center_point.add(String.format("%.10f", start_point_0))
                temp_center_point.add(String.format("%.10f", start_point_1))
                temp_radius.add(String.format("%.10f", radius))
                main[key]!!["point_coords"]!!.remove("c")
                main[key]!!["point_coords"]!!.remove("r")
                main[key]!!["point_coords"]!!["c"] = temp_center_point
                main[key]!!["point_coords"]!!["rd"] = temp_radius
            } else println("InSuffecient Points in Circle GEOM...") else if (key.startsWith("2Cr")) if (main[key]!!["point_coords"]!!.size == 3) {
                val start_point = main[key]!!["point_coords"]!!["1"]
                val start_point_0 = start_point!![0].toDouble()
                val start_point_1 = start_point[1].toDouble()
                val mid_point = main[key]!!["point_coords"]!!["2"]
                val mid_point_0 = mid_point!![0].toDouble()
                val mid_point_1 = mid_point[1].toDouble()
                val end_point = main[key]!!["point_coords"]!!["3"]
                val end_point_0 = end_point!![0].toDouble()
                val end_point_1 = end_point[1].toDouble()
                val f = (((Math.pow(start_point_0, 2.0) - Math.pow(end_point_0,
                    2.0
                )) * (start_point_0 - mid_point_0) + (Math.pow(start_point_1, 2.0) - Math.pow(
                    end_point_1,
                    2.0
                ) * (start_point_0 - mid_point_0) + Math.pow(mid_point_0, 2.0) - Math.pow(
                    start_point_0,
                    2.0
                )) * (start_point_0 - end_point_0) + (Math.pow(mid_point_1, 2.0) - Math.pow(
                    start_point_1,
                    2.0
                )) * (start_point_0 - end_point_0))
                        / 2 * ((end_point_1 - start_point_1) * (start_point_0 - mid_point_0) - (mid_point_1 - start_point_1) * (start_point_0 - end_point_0)))
                val g = (((Math.pow(start_point_0, 2.0) - Math.pow(
                    end_point_0,
                    2.0
                )) * (start_point_1 - mid_point_1) + (Math.pow(start_point_1, 2.0) - Math.pow(
                    end_point_1,
                    2.0
                ) * (start_point_1 - mid_point_1) + Math.pow(mid_point_0, 2.0) - Math.pow(
                    start_point_0,
                    2.0
                )) * (start_point_1 - end_point_1) + (Math.pow(mid_point_1, 2.0) - Math.pow(
                    start_point_1,
                    2.0
                )) * (start_point_1 - end_point_1))
                        / 2 * ((end_point_0 - start_point_0) * (start_point_1 - mid_point_1) - (mid_point_0 - start_point_0) * (start_point_1 - end_point_1)))
                val c =
                    -Math.pow(start_point_0, 2.0) - Math.pow(
                        start_point_1,
                        2.0
                    ) - 2 * g * start_point_0 - 2 * f * start_point_1
                val h: Double
                val k: Double
                h = -g
                k = -f
                val radius = Math.sqrt(Math.pow(h, 2.0) + Math.pow(k, 2.0) - c)
                val temp_center_point = ArrayList<String>()
                val temp_radius = ArrayList<String>()
                temp_center_point.add(String.format("%.10f", h))
                temp_center_point.add(String.format("%.10f", k))
                temp_radius.add(String.format("%.10f", radius))
                main[key]!!["point_coords"]!!.remove("1")
                main[key]!!["point_coords"]!!.remove("2")
                main[key]!!["point_coords"]!!.remove("3")
                main[key]!!["point_coords"]!!["c"] = temp_center_point
                main[key]!!["point_coords"]!!["rd"] = temp_radius
            } else println("InSuffecient Points in Circle GEOM...") else if (key.startsWith("Ar")) if (main[key]!!["point_coords"]!!.size == 3) {
                val start_point = main[key]!!["point_coords"]!!["st"]
                val start_point_0 = start_point!![0].toDouble()
                val start_point_1 = start_point[1].toDouble()
                val mid_point = main[key]!!["point_coords"]!!["m"]
                val mid_point_0 = mid_point!![0].toDouble()
                val mid_point_1 = mid_point[1].toDouble()
                val end_point = main[key]!!["point_coords"]!!["en"]
                val end_point_0 = end_point!![0].toDouble()
                val end_point_1 = end_point[1].toDouble()
                val f = (((Math.pow(start_point_0, 2.0) - Math.pow(
                    end_point_0,
                    2.0
                )) * (start_point_0 - mid_point_0) + (Math.pow(start_point_1, 2.0) - Math.pow(
                    end_point_1,
                    2.0
                ) * (start_point_0 - mid_point_0) + Math.pow(mid_point_0, 2.0) - Math.pow(
                    start_point_0,
                    2.0
                )) * (start_point_0 - end_point_0) + (Math.pow(mid_point_1, 2.0) - Math.pow(
                    start_point_1,
                    2.0
                )) * (start_point_0 - end_point_0))
                        / 2 * ((end_point_1 - start_point_1) * (start_point_0 - mid_point_0) - (mid_point_1 - start_point_1) * (start_point_0 - end_point_0)))
                val g = (((Math.pow(start_point_0, 2.0) - Math.pow(
                    end_point_0,
                    2.0
                )) * (start_point_1 - mid_point_1) + (Math.pow(start_point_1, 2.0) - Math.pow(
                    end_point_1,
                    2.0
                ) * (start_point_1 - mid_point_1) + Math.pow(mid_point_0, 2.0) - Math.pow(
                    start_point_0,
                    2.0
                )) * (start_point_1 - end_point_1) + (Math.pow(mid_point_1, 2.0) - Math.pow(
                    start_point_1,
                    2.0
                )) * (start_point_1 - end_point_1))
                        / 2 * ((end_point_0 - start_point_0) * (start_point_1 - mid_point_1) - (mid_point_0 - start_point_0) * (start_point_1 - end_point_1)))
                val c =
                    -Math.pow(start_point_0, 2.0) - Math.pow(
                        start_point_1,
                        2.0
                    ) - 2 * g * start_point_0 - 2 * f * start_point_1
                val h: Double
                val k: Double
                h = -g
                k = -f
                val radius = Math.sqrt(Math.pow(h, 2.0) + Math.pow(k, 2.0) - c)
                var st_angle = Math.toDegrees(
                    Math.atan2(
                        end_point_1 - k,
                        end_point_0 - h
                    )
                )
                var en_angle = Math.toDegrees(
                    Math.atan2(
                        start_point_1 - k,
                        start_point_0 - h
                    )
                )
                st_angle = st_angle + Math.ceil(-st_angle / 360) * 360
                en_angle = en_angle + Math.ceil(-en_angle / 360) * 360
                val stAngle = Math.min(st_angle, en_angle)
                val enAngle = Math.max(st_angle, en_angle)
                val temp_center_point = ArrayList<String>()
                val temp_radius = ArrayList<String>()
                val temp_start_angle = ArrayList<String>()
                val temp_end_angle = ArrayList<String>()
                temp_center_point.add(String.format("%.10f", h))
                temp_center_point.add(String.format("%.10f", k))
                temp_radius.add(String.format("%.10f", radius))
                temp_start_angle.add(String.format("%.10f", radius))
                temp_end_angle.add(String.format("%.10f", radius))
                main[key]!!["point_coords"]!!.remove("st")
                main[key]!!["point_coords"]!!.remove("m")
                main[key]!!["point_coords"]!!.remove("en")
                main[key]!!["point_coords"]!!["c"] = temp_center_point
                main[key]!!["point_coords"]!!["rd"] = temp_radius
                main[key]!!["point_coords"]!!["sa"] = temp_start_angle
                main[key]!!["point_coords"]!!["en"] = temp_end_angle
            } else println("InSuffecient Points in Arc GEOM...")
        }

//            System.out.println("Retreived entry set is : " + main.entrySet());
//        if (SDK_INT >= Build.VERSION_CODES.N) {
//            main.forEach(
//                    (key, value)
//                            -> System.out.println(key + " = " + value));
    }
    ////////////////////////////////
    fun Step3() {
        val page_margin = 4
        var hex_value = 80
        val TEXT_HEIGHT = 0.1
        val POINT_SIZE = 0
        val HEIGHT_BOX_WIDTH = 4.0
        val NUMBER_BOX_WIDTH = 2.0
        val POINT_HEIGHT_BOX_X_OFFSET = 2 * 0.2
        val POINT_HEIGHT_BOX_Y_OFFSET = 0.0
        val POINT_NUMBER_BOX_X_OFFSET = 2 * 0.2
        val POINT_NUMBER_BOX_Y_OFFSET = 0.0

//        Log.d("csvmodelArrayList", String.valueOf(csvmodelArrayList));
        Log.d("arrayListeasting", arrayListeasting.toString())
        Log.d("arrayListnorthing", arrayListnorthing.toString())
        minX = Collections.min(arrayListeasting)
        maxX = Collections.max(arrayListeasting)
        minY = Collections.min(arrayListnorthing)
        maxY = Collections.max(arrayListnorthing)
        textB.append(
            "  9\n" +
                    "\$EXTMIN \n" +
                    " 10 \n" +
                    minX + "\n" +
                    " 20\n" +
                    minY + "\n" +
                    " 30 \n" +
                    "0 \n" +
                    "  9 \n" +
                    "\$EXTMAX \n" +
                    " 10\n" +
                    maxX + "\n" +
                    " 20 \n" +
                    maxY + "\n" +
                    " 30 \n" +
                    "0 \n"
        )
        for (keyValue: Map.Entry<*, *> in point_data.entries) {
            val key: String = keyValue.key.toString()
            val coords: ArrayList<Double?> = ArrayList<Double?>()
            coords.add(point_data[key]!![0])
            coords.add(point_data[key]!![1])
            coords.add(point_data[key]!![2])

//            Log.d("Northing",""+String.valueOf(coords.get(0)));
//            Log.d("Easting",""+String.valueOf(coords.get(1)));
//            Log.d("Easting",""+String.valueOf(coords.get(2)));
            textD.append(
                ("  0\n" +
                        "POINT\n" +
                        "  5\n" +
                        (Integer.toHexString(hex_value)) + "\n" +
                        "100\n" +
                        "AcDbEntity\n" +
                        "  8\n" +
                        "TACKE\n" +
                        "  6\n" +
                        "ByLayer\n" +
                        " 62\n" +
                        "  256\n" +
                        "370\n" +
                        "   -1\n" +
                        "100\n" +
                        "AcDbPoint\n" +
                        " 39\n" +
                        POINT_SIZE + "\n" +
                        " 10\n" +
                        coords[0] + "\n" +
                        " 20\n" +
                        coords[1] + "\n" +
                        " 30\n" +
                        coords[2] + "\n")
            )
            hex_value++
            textD.append(
                ("  0\n" +
                        "MTEXT\n" +
                        "  5\n" +
                        (Integer.toHexString(hex_value)) + "\n" +
                        "100\n" +
                        "AcDbEntity\n" +
                        "  8\n" +
                        "BROJEVI SNIMLJENIH TACAKA\n" +
                        "  6\n" +
                        "ByLayer\n" +
                        " 62\n" +
                        "  256\n" +
                        "370\n" +
                        "   -1\n" +
                        "100\n" +
                        "AcDbMText\n" +
                        " 10\n" +
                        (coords[0]!! - POINT_NUMBER_BOX_X_OFFSET) + "\n" +
                        " 20\n" +
                        (coords[1]!! - POINT_NUMBER_BOX_Y_OFFSET) + "\n" +
                        " 30\n" +
                        "0\n" +
                        " 40\n" +
                        TEXT_HEIGHT + "\n" +
                        " 41\n" +
                        NUMBER_BOX_WIDTH + "\n" +
                        " 71\n" +
                        "    9\n" +
                        " 72\n" +
                        "    1\n" +
                        "  1\n" +
                        key + "\n" +
                        "  7\n" +
                        "standard\n" +
                        "210\n" +
                        "0\n" +
                        "220\n" +
                        "0\n" +
                        "230\n" +
                        "1\n" +
                        " 50\n" +
                        "0\n" +
                        " 73\n" +
                        "    2\n" +
                        " 44\n" +
                        "1\n")
            )
            hex_value++
        }
        for (keyValue: Map.Entry<*, *> in main.entries) {
            val key = keyValue.key.toString()
            val value = keyValue.value.toString()
            if (key.startsWith("Ln")) if (main[key]!!["point_coords"]!!.size == 2) {
                textD1.append(
                    ("  0\n" +
                            "LINE\n" +
                            "  5\n" +
                            (Integer.toHexString(hex_value)) + "\n" +
                            "  100\n" +
                            "AcDbEntity\n" +
                            "  8\n" +
                            "0\n" +
                            "  100\n" +
                            "AcDbLine\n" +
                            "  39\n" +
                            "10.0\n" +
                            "  10\n" +
                            main[key]!!["point_coords"]!!["st"]!![0] + "\n" +
                            "  20\n" +
                            main[key]!!["point_coords"]!!["st"]!![1] + "\n" +
                            "  30\n" +
                            "0.0\n" +
                            "  11\n" +
                            main[key]!!["point_coords"]!!["en"]!![0] + "\n" +
                            "  21\n" +
                            main[key]!!["point_coords"]!!["en"]!![1] + "\n" +
                            "  31\n" +
                            "0.0\n")
                )
                hex_value++
            }
            if (key.startsWith("Pl")) if (main[key]!!["point_coords"]!!.size > 2) {
                textD1.append(
                    ("  0\n" +
                            "POLYLINE\n" +
                            "  5\n" +
                            (Integer.toHexString(hex_value)) + "\n" +
                            "  100\n" +
                            "AcDbEntity\n" +
                            "  8\n" +
                            "0\n" +
                            "  100\n" +
                            "AcDb3dPolyline\n" +
                            "  66\n" +
                            "1\n" +
                            "  10\n" +
                            "0.0\n" +
                            "  20\n" +
                            "0.0\n" +
                            "  30\n" +
                            "0.0\n" +
                            "  39\n" +
                            "8.0\n" +
                            "  70\n" +
                            "0\n")
                )
                hex_value++
                for (i in 0..main[key]!!["point_coords"]!!.size) {
                    if (main[key]!!["point_coords"]!!.containsKey(i.toString())) {
                        textD1.append(
                            ("  0\n" +
                                    "VERTEX\n" +
                                    "  5\n" +
                                    (Integer.toHexString(hex_value)) + "\n" +
                                    "  100\n" +
                                    "AcDbEntity\n" +
                                    "  8\n" +
                                    "0\n" +
                                    "  100\n" +
                                    "AcDbVertex\n" +
                                    "  100\n" +
                                    "AcDb3dPolylineVertex\n" +
                                    "  10\n" +
                                    main[key]!!["point_coords"]!![i.toString()]!![0] + "\n" +
                                    "  20\n" +
                                    main[key]!!["point_coords"]!![i.toString()]!![1] + "\n" +
                                    "  30\n" +
                                    "0.0\n" +
                                    "  70\n" +
                                    "32\n")
                        )
                        hex_value++
                    }
                }
                textD1.append(
                    ("  0\n" +
                            "SEQEND\n" +
                            "  5\n" +
                            (Integer.toHexString(hex_value)) + "\n" +
                            "300\n" +
                            (Integer.toHexString(hex_value + 1)) + "\n" +
                            "  100\n" +
                            "AcDbEntity\n" +
                            "  8\n" +
                            "0\n")
                )
                hex_value++
            }
            if (key.startsWith("Pg")) if (main[key]!!["point_coords"]!!.size > 3) {
                textD1.append(
                    ("  0\n" +
                            "POLYLINE\n" +
                            "  5\n" +
                            (Integer.toHexString(hex_value)) + "\n" +
                            "  100\n" +
                            "AcDbEntity\n" +
                            "  8\n" +
                            "0\n" +
                            "  100\n" +
                            "AcDb3dPolyline\n" +
                            "  66\n" +
                            "1\n" +
                            "  10\n" +
                            "0.0\n" +
                            "  20\n" +
                            "0.0\n" +
                            "  30\n" +
                            "0.0\n" +
                            "  39\n" +
                            "8.0\n" +
                            "  70\n" +
                            "1\n")
                )
                hex_value++
                for (i in 0..main[key]!!["point_coords"]!!.size) {
                    if (main[key]!!["point_coords"]!!.containsKey(i.toString())) {
                        textD1.append(
                            ("  0\n" +
                                    "VERTEX\n" +
                                    "  5\n" +
                                    (Integer.toHexString(hex_value)) + "\n" +
                                    "  100\n" +
                                    "AcDbEntity\n" +
                                    "  8\n" +
                                    "0\n" +
                                    "  100\n" +
                                    "AcDbVertex\n" +
                                    "  100\n" +
                                    "AcDb3dPolylineVertex\n" +
                                    "  10\n" +
                                    main[key]!!["point_coords"]!![i.toString()]!![0] + "\n" +
                                    "  20\n" +
                                    main[key]!!["point_coords"]!![i.toString()]!![1] + "\n" +
                                    "  30\n" +
                                    "0.0\n" +
                                    "  70\n" +
                                    "32\n")
                        )
                        hex_value++
                    }
                }
                textD1.append(
                    ("  0\n" +
                            "SEQEND\n" +
                            "  5\n" +
                            (Integer.toHexString(hex_value)) + "\n" +
                            "300\n" +
                            (Integer.toHexString(hex_value + 1)) + "\n" +
                            "  100\n" +
                            "AcDbEntity\n" +
                            "  8\n" +
                            "0\n")
                )
                hex_value++
            }
            if (key.startsWith("1Cr") || key.startsWith("2Cr")) if (main[key]!!["point_coords"]!!.size == 2) {
                textD1.append(
                    ("  0\n" +
                            "CIRCLE\n" +
                            "  5\n" +
                            (Integer.toHexString(hex_value)) + "\n" +
                            "  100\n" +
                            "AcDbEntity\n" +
                            "  8\n" +
                            "0\n" +
                            "  100\n" +
                            "AcDbCircle\n" +
                            "  10\n" +
                            main[key]!!["point_coords"]!!["c"]!![0] + "\n" +
                            "  20\n" +
                            main[key]!!["point_coords"]!!["c"]!![1] + "\n" +
                            "  30\n" +
                            "0.0\n" +
                            "  40\n" +
                            main[key]!!["point_coords"]!!["rd"]!![0] + "\n")
                )
                hex_value++
            }
            if (key.startsWith("Ar")) if (main[key]!!["point_coords"]!!.size == 4) {
                textD1.append(
                    ("  0\n" +
                            "ARC\n" +
                            "  5\n" +
                            (Integer.toHexString(hex_value)) + "\n" +
                            "  100\n" +
                            "AcDbEntity\n" +
                            "  8\n" +
                            "0\n" +
                            "  100\n" +
                            "AcDbCircle\n" +
                            "  10\n" +
                            main[key]!!["point_coords"]!!["c"]!![0] + "\n" +
                            "  20\n" +
                            main[key]!!["point_coords"]!!["c"]!![1] + "\n" +
                            "  30\n" +
                            "0.0\n" +
                            "  40\n" +
                            main[key]!!["point_coords"]!!["rd"]!![0] + "\n" +
                            "  100\n" +
                            "AcDbArc\n" +
                            "  50\n" +
                            main[key]!!["point_coords"]!!["sa"]!![0] + "\n" +
                            "  51\n" +
                            main[key]!!["point_coords"]!!["ea"]!![0] + "\n")
                )
                hex_value++
            }
        }
    }

    /// Runtime RequestPermission
    fun requestpermission() {
        //request permission for Read
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
        //request permission for Write
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }

}


