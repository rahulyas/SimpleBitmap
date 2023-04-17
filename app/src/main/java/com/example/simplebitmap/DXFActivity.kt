package com.example.simplebitmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.io.*
import java.util.*

class DXFActivity : AppCompatActivity() {

    var import: AppCompatButton? = null
    private val PICK_TEXT = 101
    var fileuri: Uri? = null
    private val PERMISSION_REQUEST_STORAGE = 1000
    var temp_String = ArrayList<String>()
    var entities_list = ArrayList<String>()
    var point_map = HashMap<String, ArrayList<String>>()
    var mtext_map = HashMap<String, ArrayList<String>>()
    var circle_map = HashMap<String, ArrayList<String>>()
    var line_map = HashMap<String, ArrayList<String>>()
    var arc_map = HashMap<String, ArrayList<String>>()
    var lwpolyline_map = HashMap<String, ArrayList<String>>()
    var polyline_map = HashMap<String, ArrayList<ArrayList<String>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dxfactivity)
        onRequestpermission()
        entities_list.addAll(Arrays.asList("POINT", "TEXT", "MTEXT", "LINE", "ARC", "CIRCLE", "POLYLINE", "LWPOLYLINE", "ENDSEC", "VERTEX"))
        import = findViewById<AppCompatButton>(R.id.loadfile)

        import!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_TEXT)
        })

    }

    //////////////////////////////// Read File //////////////////////////////////
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.data
            readdxf(getFilePath(fileuri!!))
        }
    }

    /// this method is used for getting file path from uri
    fun getFilePath(uri: Uri): String? {
        val filename1: Array<String>
        val fn: String
        val filepath = uri.path
        val filePath1 = filepath!!.split(":").toTypedArray()
        filename1 = filepath.split("/").toTypedArray()
        fn = filename1[filename1.size - 1]
        return Environment.getExternalStorageDirectory().path + "/" + filePath1[1]
    }

    /// reading the dxf file
    fun readdxf(input: String?): String? {
        val file = File(input)
        val text = StringBuilder()
        try {
            val inputStream = contentResolver.openInputStream(fileuri!!)
            val br = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                println("Hello ji")
                temp_String.add(line!!)
            }
            Step1()
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Error")
        }
        return text.toString()
    }

    ///////////////////////// onRequestpermission ////////////
    fun onRequestpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }

    ////////////////////////////////////// Step1 ///////////////////////////////
    fun Step1() {
        var start_counter = temp_String.indexOf("ENTITIES")
        val end_counter = temp_String.subList(start_counter, temp_String.size - 1).indexOf("ENDSEC") + start_counter
        println("Start: $start_counter")
        println("End: $end_counter")
        while (start_counter < end_counter) {
            val current_word = temp_String[start_counter]
            if (!entities_list.contains(current_word)) {
//                println("No ")
                start_counter++
                continue
            }
            if (current_word == "POINT") {
                var point_loop_flag = true
                var point_loop_counter = temp_String.subList(start_counter, temp_String.size - 1).indexOf("AcDbPoint") + start_counter
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
                var mtext_loop_counter = temp_String.subList(start_counter, temp_String.size - 1).indexOf("AcDbMText") + start_counter
                val temp_mtext_list = ArrayList<String>()
                val mtext_match_list = ArrayList<String>()
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
                var circle_loop_counter = temp_String.subList(start_counter, temp_String.size - 1).indexOf("AcDbCircle") + start_counter
                val temp_circle_list = ArrayList<String>()
                val circle_match_list = ArrayList<String>()
                circle_match_list.addAll(Arrays.asList(" 10", " 20", " 30", " 40", "  10", "  20", "  30", "  40"))
                while (circle_loop_flag) {
                    if (entities_list.contains(temp_String[circle_loop_counter + 1])) {
                        val circle_prefix = "1Cr_" + (circle_map.size + 1)
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
                var line_loop_counter = temp_String.subList(start_counter, temp_String.size - 1).indexOf("AcDbLine") + start_counter
                val temp_line_list = ArrayList<String>()
                val line_match_list = ArrayList<String>()
                line_match_list.addAll(Arrays.asList(" 10", " 20", " 30", "  10", "  20", "  30", " 11", " 21", " 31", "  11", "  21", "  31"))
                while (line_loop_flag) {
                    if (entities_list.contains(temp_String[line_loop_counter + 1])) {
                        val line_prefix = "Ln_" + (line_map.size + 1)
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
                var arc_loop_counter = temp_String.subList(start_counter, temp_String.size - 1).indexOf("AcDbCircle") + start_counter
                val temp_arc_list = ArrayList<String>()
                val arc_match_list = ArrayList<String>()
                arc_match_list.addAll(Arrays.asList(" 10", " 20", " 30", " 40", " 50", " 51", "  10", "  20", "  30", "  40", "  50", "  51"))
                while (arc_loop_flag) {
                    if (entities_list.contains(temp_String[arc_loop_counter + 1])) {
                        val arc_prefix = "Ar_" + (arc_map.size + 1)
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
                var lwpolyline_loop_counter = temp_String.subList(start_counter, temp_String.size - 1).indexOf("AcDbPolyline") + start_counter
                val temp_lwpolyline_list = ArrayList<String>()
                val lwpolyline_match_list = ArrayList<String>()
                lwpolyline_match_list.addAll(Arrays.asList(" 10", " 20", "  10", "  20"))
                while (lwpolyline_loop_flag) {
                    if (entities_list.contains(temp_String[lwpolyline_loop_counter + 1])) {
                        val lwpolyline_prefix = "Pl_" + (lwpolyline_map.size + 1)
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
                var polyline_loop_flag = true
                var polyline_loop_counter = temp_String.subList(start_counter, temp_String.size - 1).indexOf("AcDb3dPolyline") + start_counter
                val temp_polyline_list = ArrayList<ArrayList<String>>()
                while (polyline_loop_flag) {
                    if (entities_list.subList(0, entities_list.size - 1).contains(temp_String[polyline_loop_counter + 1])) {
                        val polyline_prefix = "Pl_" + (polyline_map.size + 1)
                        polyline_map[polyline_prefix] = temp_polyline_list
                        start_counter = polyline_loop_counter
                        polyline_loop_flag = false
                        continue
                    }
                    val current_word_in_loop = temp_String[polyline_loop_counter]
                    if (current_word_in_loop == "VERTEX") {
                        var vertex_loop_flag = true
                        var vertex_loop_counter = temp_String.subList(polyline_loop_counter, temp_String.size - 1).indexOf("AcDbVertex") + polyline_loop_counter
                        val temp_vertex_list = ArrayList<String>()
                        val vertex_match_list = ArrayList<String>()
                        vertex_match_list.addAll(Arrays.asList(" 10", " 20", " 30", "  10", "  20", "  30"))
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
//        println("point_map: " + point_map.entries)
//        println("mtext_map: " + mtext_map.entries)
//        println("circle_map: " + circle_map.entries)
//        println("line_map: " + line_map.entries)
//        println("arc_map: " + arc_map.entries)
//        println("lwpolyline_map: " + lwpolyline_map.entries)
//        println("polyline_map: " + polyline_map.entries)


    }
}