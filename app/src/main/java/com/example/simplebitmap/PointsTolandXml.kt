package com.example.simplebitmap

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a3dmodel.arithmetic.Delaunay
import com.example.simplebitmap.databinding.ActivityPointsTolandXmlBinding
import io.github.jdiemke.triangulation.Triangle2D
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale


class PointsTolandXml : AppCompatActivity() {
    private lateinit var binding: ActivityPointsTolandXmlBinding
    private val PICK_TEXT = 101
    var fileuri: Uri? = null
    var new_finallist= ArrayList<Double>()
    var final_tempList = ArrayList<Double>()
    var list = ArrayList<String>()
    var calFileString: StringBuilder = StringBuilder()

    var PointNorthinglist = ArrayList<Double>()
    var PointEastinglist = ArrayList<Double>()
    var PointElevationlist = ArrayList<Double>()
    var FaceNorthinglist = ArrayList<Double>()
    var FaceEastinglist = ArrayList<Double>()
    var FaceElevationlist = ArrayList<Double>()
    var idlist = ArrayList<Int>()
    var minX = 0.0
    var maxX = 0.0
    var trianglelist: List<Triangle2D>? = null
    var pointList: MutableList<Point> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPointsTolandXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.load.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select CSV file"), PICK_TEXT)
        }

        binding.save.setOnClickListener {
            Utils().calFile(calFileString.toString(),this)
        }
    }

    //////////////////////////////// Get the Import File Name //////////////////////////////////
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.data

            Utils().readText(Utils().getFilePath(fileuri!!))
//            new_finallist = Utils().readNorthingEasting()
///*            list = Utils().readAnyfile(fileuri!!,this@PointsTolandXml) as ArrayList<String>
//            new_finallist = Utils().SplitDataList(list)*/
//            Log.d(TAG, "onActivityResult: new_finallist == "+new_finallist)
//            finalTrianglePoint()
//            points()
            val backgroundThread = Thread {
                // Background thread - perform long-running task here

                // Update UI from the main thread using Handler
                Handler(Looper.getMainLooper()).post {
                    // Main thread - update UI or perform UI-related tasks here
                    new_finallist = Utils().readNorthingEasting()
                    /*            list = Utils().readAnyfile(fileuri!!,this@PointsTolandXml) as ArrayList<String>
                                new_finallist = Utils().SplitDataList(list)*/
                    Log.d(TAG, "onActivityResult: new_finallist == "+new_finallist)
                    Log.d(TAG, "onActivityResult: new_finallistSize == "+new_finallist.size)
                    finalTrianglePoint()
                    points()
                }
            }
            backgroundThread.start()
////            Log.d(TAG, "onActivityResult: new_finallist == "+new_finallist)
//            list.clear()
//            list= Utils().readAnyfile(fileuri!!,this) as ArrayList<String>
//            SplitDataList(list)
//            Log.d(TAG, "onActivityResult: list == "+list)
        }
    }


   /**********************************************************************************************************/

   /**********************************************************************************************************/
   fun points(){
       GlobalScope.launch(Dispatchers.IO) {
           // Background thread - perform long-running task here
           withContext(Dispatchers.Main) {
               // Main thread - update UI or perform UI-related tasks here
               trianglelist = Delaunay.doDelaunayFromGit(new_finallist)
               val list = Delaunay.addHight(trianglelist!!, new_finallist)
               val list2 = Delaunay.doEdge(trianglelist!!, new_finallist)
               Log.d(TAG, "trianglelist:= "+trianglelist)
               Log.d(TAG, "readNorthingEasting:list "+list)
               Log.d(TAG, "readNorthingEasting:list2 =="+list2)

               var i = 0
               while (i < list.size) {
                   val xCoordinate = list[i + 0]
                   val yCoordinate = list[i + 1]
                   val zCoordinate = list[i + 2]
                   val id = findPointIdByCoordinates(xCoordinate,yCoordinate,zCoordinate)
                   idlist.add(id)
//           Log.d(TAG, "id: "+id)
                   i = i + 3
               }
               Log.d(TAG, "id: "+idlist.size)
               readfaceCoordinates()
               create_landXml()
           }
       }
   }
   /**********************************************************************************************************/
    fun readfaceCoordinates(){
      var j = 0
      while (j < idlist.size) {
          val xCoordinate = idlist[j + 0]
          val yCoordinate = idlist[j + 1]
          val zCoordinate = idlist[j + 2]
          FaceNorthinglist.add(xCoordinate.toDouble())
          FaceEastinglist.add(yCoordinate.toDouble())
          FaceElevationlist.add(zCoordinate.toDouble())
          j = j + 3
      }
      Log.d(TAG, "FaceNorthinglist =="+FaceNorthinglist)
    }
   /**********************************************************************************************************/

    fun finalTrianglePoint() {
        var i = 0
        var id = 1
        while (i < new_finallist.size) {
            PointNorthinglist.add(new_finallist[i + 0])
            PointEastinglist.add(new_finallist[i + 1])
            PointElevationlist.add(new_finallist[i + 2])
            val p = Point(new_finallist[i + 0], new_finallist[i + 1], new_finallist[i + 2], id++)
            pointList.add(p)
            i = i + 3
        }
       Log.d(TAG, "pointList:=$pointList")
       Log.d(TAG, "pointListSize:=${pointList.size}")
   }
   /**********************************************************************************************************/
   fun findPointIdByCoordinates(x: Double, y: Double,z: Double): Int {
       val foundPoint = pointList.find { it.x == x && it.y == y  && it.z == z}
       return foundPoint!!.id
   }
   /**********************************************************************************************************/

   fun SplitDataList(list:ArrayList<String>){
       val splitDataList = list.map { it.split(", ") }
       for (splitData in splitDataList) {
           Log.d(TAG, "readText:splitData =="+splitData)
           val identi = splitData[0]
           val values = identi.split("\\s+".toRegex())
           if(values.isNotEmpty() && values.contains("v")){
               val identifier = values[0]
               val Northing = values[1].toDouble()
               val Easting = values[2].toDouble()
               val Elevation = values[3].toDouble()
               PointNorthinglist.add(Northing)
               PointEastinglist.add(Easting)
               PointElevationlist.add(Elevation)
//               Log.d(TAG, "readsplitdata:Point=="+"identifier = "+identifier+"Northing = "+Northing+"Easting = "+Easting+"Elevation = "+Elevation)
           }else if (values.isNotEmpty() && values.contains("f")){
               val identifier = values[0]
               val Northing = values[1].toDouble()
               val Easting = values[2].toDouble()
               val Elevation = values[3].toDouble()
               FaceNorthinglist.add(Northing)
               FaceEastinglist.add(Easting)
               FaceElevationlist.add(Elevation)
//               Log.d(TAG, "readsplitdata:Face=="+"identifier = "+identifier+"Northing = "+Northing+"Easting = "+Easting+"Elevation = "+Elevation)
           }
       }
       Log.d(TAG, "FaceNorthinglist: "+FaceNorthinglist.size+"=="+FaceEastinglist.size+"=="+FaceElevationlist.size)
       create_landXml()
   }

   /**********************************************************************************************************/
    fun create_landXml(){
       minX = Collections.min(PointElevationlist)
       maxX = Collections.max(PointElevationlist)
       val currentDate: String = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
       calFileString.append(
           "<?xml version=\""+1.0+"\" encoding=UTF-\""+8+"\"?>\r\n" +
           "<LandXML version=\""+1.2+"\"    >\r\n" +
           "  <Units>\r\n"+
           "    <Metric linearUnit=\"meter\" widthUnit=\"meter\" heightUnit=\"meter\" diameterUnit=\"meter\" areaUnit=\"squareMeter\" volumeUnit=\"cubicMeter\" temperatureUnit=\"celsius\" pressureUnit=\"HPA\" angularUnit=\"decimal degrees\" directionUnit=\"decimal degrees\" />\r\n"+
           "  </Units>\r\n"+
           "  <Application name=\"GEOMaster\" manufacturer=\"Apogee Gnss\" version=\"37.0.8236.15475\" timeStamp=\""+currentDate+"\">\r\n"+
           "    <Author createdBy=\"createdByName\" timeStamp=\""+currentDate+"\" />\r\n"+
           "  </Application>\r\n"+
           "  <Surfaces>\r\n" +
           "    <Surface name=\"MCW (Finish)\">\r\n" +
           "      <Definition surfType=\"TIN\" elevMax=\""+minX+"\" elevMin=\""+maxX+"\">\r\n" +
           "        <Pnts>\r\n"
       )
       Log.d(TAG, "create_landXml: PointNorthinglist"+PointNorthinglist.size+"=="+PointEastinglist.size+"=="+PointElevationlist.size)
       for (i in PointNorthinglist.indices){
           val n= i+1
           calFileString.append(
               "          <P id=\""+n+"\">"+PointNorthinglist[i]+" "+PointEastinglist[i]+" "+PointElevationlist[i]+"</P>\r\n"
           )
       }
           calFileString.append(
               "        </Pnts>\r\n" +
               "        <Faces>\r\n"
           )
       for (i in FaceNorthinglist.indices){
           calFileString.append(
               "          <F>"+FaceNorthinglist[i].toInt()+" "+FaceEastinglist[i].toInt()+" "+FaceElevationlist[i].toInt()+"</F>\r\n"
           )
       }
       calFileString.append(
           "        </Faces>\r\n" +
           "        <Feature code=\"ApogeeGNSS\">\r\n"+
           "          <Property label=\"color\" value=\"128,128,128\" />\r\n"+
           "        </Feature>\r\n"+
           "      </Definition>\r\n"+
           "    </Surface>\r\n"+
           "  </Surfaces>\r\n"+
           "</LandXML>\r\n"
       )

       Log.d(TAG, "create_landXml: calFileString"+calFileString)
       Toast.makeText(this,"Now You Can Save Your Land Xml file ",Toast.LENGTH_SHORT).show()
    }
   /**********************************************************************************************************/

}