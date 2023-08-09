package com.example.simplebitmap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pointtoland extends AppCompatActivity {
    AppCompatButton button;
    AppCompatButton save;
    private final int PICK_TEXT = 101;
    Uri fileuri;

    ArrayList<Double> new_finallist = new ArrayList<>();
    List<String> list = new ArrayList<>();
    ArrayList<String> Northing = new ArrayList<>();
    ArrayList<String> Easting = new ArrayList<>();
    ArrayList<String> Elevation = new ArrayList<>();

    ArrayList<Double> FirstNorthing = new ArrayList<>();
    ArrayList<Double> FirstEasting = new ArrayList<>();
    ArrayList<Double> FirstElevation = new ArrayList<>();

    List<Integer> NorthingintegerList = new ArrayList<>();
    List<Integer> EastingintegerList = new ArrayList<>();
    List<Integer> ElevationintegerList = new ArrayList<>();
    StringBuilder calFileString = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointtoland);
        new Utils().requestStoragePermission(pointtoland.this);
        button = findViewById(R.id.load3);
        save = findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_TEXT);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Utils().calFile(calFileString.toString(),pointtoland.this);
            }
        });


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.getData();
            String path = new Utils().getFilePath(fileuri,pointtoland.this);
            String[] splitData = path.split("\\.");
            String splitDataPath = splitData[1];

            if (splitData.length > 0) {
                if (splitDataPath.contains("xml")) {
                    new Utils().readText(new Utils().getFilePath(fileuri));
                    new_finallist = new Utils().readNorthingEasting();
                } else if (splitDataPath.contains("txt")) {
                    list = new Utils().readAnyfile(fileuri,pointtoland.this);
                    new_finallist = new Utils().SplitDataList(list);
                }
            }
            finalTrianglePoint();
            Log.d(TAG, "onActivityResult: new_finallist == "+new_finallist);
        }
    }

    public void finalTrianglePoint() {
        List<Point> pointList = new ArrayList<>();
//        List<String> pointlist = new ArrayList<>();
        int i = 0;
        int id = 1;
        while (i < new_finallist.size()) {
            List<Double> tempPoints = new ArrayList<>();
            FirstNorthing.add(new_finallist.get(i + 0));
            FirstEasting.add(new_finallist.get(i + 1));
            FirstElevation.add(new_finallist.get(i + 2));
            Point p = new Point(new_finallist.get(i + 0), new_finallist.get(i + 1), new_finallist.get(i + 2), id++);
            pointList.add(p);
//            pointlist.add(p.toString());
//            Log.d(TAG, "tempPoints:=" + tempPoints);
            i = i + 3;
        }
        Log.d(TAG, "pointList:=" + pointList);
        Log.d(TAG, "FirstNorthing:=" + FirstNorthing);
        Log.d(TAG, "FirstEasting:=" + FirstEasting);
        Log.d(TAG, "FirstElevation:=" + FirstElevation);
//        Log.d(TAG, "pointlist:=" + pointlist);
        HashSet<Triangle> triangulation = getTriangulation(pointList);
        Log.d(TAG, "pointList=triangulation:=" + triangulation);
        Log.d(TAG, "pointList=triangulation:Size=" + triangulation.size());
//        makeOBJ(pointlist);

        for (Triangle triangle : triangulation) {
            // Access the properties of the Triangle object
            Point point1=triangle.p1;
            Point point2=triangle.p2;
            Point point3=triangle.p3;
            Northing.add(point1.toString());
            Easting.add(point2.toString());
            Elevation.add(point3.toString());
    /*        Log.d(TAG, "pointList=point1:=" + point1 +"=="+x+"=="+y+"=="+z);
            Log.d(TAG, "pointList=point2:=" + point2 +"=="+x1+"=="+y1+"=="+z1);
            Log.d(TAG, "pointList=point3:=" + point3 +"=="+x3+"=="+y3+"=="+z3);*/
        }
        Log.d(TAG, "point1: "+Northing);
        Log.d(TAG, "point2: "+Easting);
        Log.d(TAG, "point3: "+Elevation);



        for (String str : Northing) {
            // Use regular expression to extract the integer value from each string
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(str);

            // If a match is found, add the integer value to the integerList
            if (matcher.find()) {
                int intValue = Integer.parseInt(matcher.group());
                NorthingintegerList.add(intValue);
            }
        }
        for (String str : Easting) {
            // Use regular expression to extract the integer value from each string
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(str);

            // If a match is found, add the integer value to the integerList
            if (matcher.find()) {
                int intValue = Integer.parseInt(matcher.group());
                EastingintegerList.add(intValue);
            }
        }
        for (String str : Elevation) {
            // Use regular expression to extract the integer value from each string
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(str);

            // If a match is found, add the integer value to the integerList
            if (matcher.find()) {
                int intValue = Integer.parseInt(matcher.group());
                ElevationintegerList.add(intValue);
            }
        }

        Log.d(TAG, "NorthingintegerList: "+NorthingintegerList);
        Log.d(TAG, "EastingintegerList: "+EastingintegerList);
        Log.d(TAG, "ElevationintegerList: "+ElevationintegerList);
        create_landXml();
    }

    //3D surface reconstruction.
    private static HashSet<Triangle> getTriangulation(List<Point> pointList){

        //Bowyer–Watson algorithm
        int triangleID = 1;
        List<Triangle> triangulation = new LinkedList<>();
        double M = getMaximumAbsoluteCoordinate(pointList);
        Triangle superTriangle = new Triangle(  new Point(3*M, 0,0, -1),//-1
                new Point(0,3*M, 0, -2),//-2
                new Point( -3*M, -3*M, 0, -3), -1);//-3
        triangulation.add(superTriangle);
        HashSet<Triangle> solution = new HashSet<>();
        for (Point point : pointList) {
//            System.out.println();
            if (point.id % 10000 == 0)System.out.println("full stop" + point);
            HashSet<Edge> edge1stAppearance = new HashSet<>(); //integer counts num of same edges
            HashSet<Edge> polygon = new HashSet<>();

            Iterator<Triangle> i = triangulation.iterator();
            while (i.hasNext()) {
                Triangle triangle = i.next();
                if (inCircle(point, triangle.p1, triangle.p2, triangle.p3)) {
                    i.remove();
                    solution.remove(triangle);
                    if (edge1stAppearance.contains(triangle.e1)) {//already appeared - will NOT be in polygon
                        //edge is not shared by any other triangles in badTriangles
                        polygon.remove(triangle.e1);
                    } else {//1st appearance
                        edge1stAppearance.add(triangle.e1);
                        polygon.add(triangle.e1);
                    }

                    if (edge1stAppearance.contains(triangle.e2)) {
                        polygon.remove(triangle.e2);
                    } else {
                        edge1stAppearance.add(triangle.e2);
                        polygon.add(triangle.e2);
                    }

                    if (edge1stAppearance.contains(triangle.e3)) {
                        polygon.remove(triangle.e3);
                    } else {
                        edge1stAppearance.add(triangle.e3);
                        polygon.add(triangle.e3);
                    }
                }
            }

            for (Edge edge : polygon) {
                Triangle newTriangle = new Triangle(point, edge.p1, edge.p2, triangleID++);
                triangulation.add(newTriangle);
                if (hasNoSuperTrianglePoint(newTriangle, superTriangle)) {
                    solution.add(newTriangle);//assume it is solution, if not remove later
                }
            }
        }
        return solution;
    }

    private static boolean hasNoSuperTrianglePoint(Triangle triangle, Triangle superTriangle){
        return  (!triangle.p1.equals(superTriangle.p1) &&
                !triangle.p1 .equals( superTriangle.p2) &&
                !triangle.p1 .equals( superTriangle.p3) &&
                !triangle.p2 .equals( superTriangle.p1) &&
                !triangle.p2 .equals( superTriangle.p2) &&
                !triangle.p2 .equals( superTriangle.p3)&&
                !triangle.p3 .equals( superTriangle.p1) &&
                !triangle.p3 .equals( superTriangle.p2) &&
                !triangle.p3 .equals( superTriangle.p3));
    }

    private static double getMaximumAbsoluteCoordinate(List<Point> pointList) {
        double M = 0.0; //absolute maximum
        for (Point point : pointList) {
            if (Math.abs(point.x) > M) M = Math.abs(point.x);
            if (Math.abs(point.y) > M) M = Math.abs(point.y);
        }
        return M;
    }

    private static boolean inCircle(Point pt, Point v1, Point v2, Point v3) {

        double ax = v1.x;
        double ay = v1.y;
        double bx = v2.x;
        double by = v2.y;
        double cx = v3.x;
        double cy = v3.y;
        double dx = pt.x;
        double dy = pt.y;

        double  ax_ = ax-dx;
        double  ay_ = ay-dy;
        double  bx_ = bx-dx;
        double  by_ = by-dy;
        double  cx_ = cx-dx;
        double  cy_ = cy-dy;
        double det=  (
                (ax_*ax_ + ay_*ay_) * (bx_*cy_-cx_*by_) -
                        (bx_*bx_ + by_*by_) * (ax_*cy_-cx_*ay_) +
                        (cx_*cx_ + cy_*cy_) * (ax_*by_-bx_*ay_)
        );

        if (ccw ( ax,  ay,  bx,  by,  cx,  cy)) {
            return (det>0);
        } else {
            return (det<0);
        }

    }

    private static boolean ccw(double ax, double ay, double bx, double by, double cx, double cy) {//counter-clockwise
        return (bx - ax)*(cy - ay)-(cx - ax)*(by - ay) > 0;
    }

    public static class Triangle{

        Point p1,p2,p3;
        Edge e1, e2, e3;
        int id;

        Triangle(Point p1, Point p2, Point p3, int id)  {
            this.id = id;

            //point are ALWAYS from lowest to highest
            Point[] pointsArray = new Point[]{p1,p2,p3};
            Arrays.sort(pointsArray);
            this.p1 = pointsArray[0];
            this.p2 = pointsArray[1];
            this.p3 = pointsArray[2];

            this.e1 = new Edge(p1, p2, this);
            this.e2 = new Edge(p2, p3, this);
            this.e3 = new Edge(p3, p1, this);
        }

        @Override
        public boolean equals(Object obj) {
            Triangle tr = (Triangle) obj;
            return (tr.p1.equals(this.p1) && tr.p2.equals(this.p2) && tr.p3.equals(this.p3));
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {

            return "T." + p1+","+p2+ ","+ p3;// + ": points(" + this.p1 + "; " + this.p2 + "; " + p3;
        }
    }

    static class Edge {

        Point p1,p2;
        Triangle t;

        Edge (Point p1, Point p2, Triangle t){
            this.t = t;
            //from lowest Point.id to highest
            if (p1.id < p2.id){
                this.p1 = p1;
                this.p2 = p2;
            } else if (p1.id > p2.id){
                this.p1 = p2;
                this.p2 = p1;
            }else {
                try {
                    System.out.println("Dots error:= " + p1 + p2);
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean equals(Object obj) {

            return ((Edge) obj).p1.equals(this.p1) && ((Edge) obj).p2.equals(this.p2);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + this.p1.hashCode();
            hash = 71 * hash + this.p2.hashCode();
            return hash;

        }

        @Override
        public String toString() {

            return "E."+p1+"," + p2 + "(" + t+")";
        }
    }

    public static class Point implements Comparable<Point>{

        double x, y, z;
        int id;

        Point(double x, double y, double z, int id) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            Point p = (Point) obj;
            return (p.x == this.x && p.y == this.y && p.z == this.z);
        }

        @Override
        public int hashCode() {
//            int hash = 7;
//            hash = 71 * hash + Double.valueOf(this.x).hashCode();
//            hash = 71 * hash + Double.valueOf(this.y).hashCode();
//            hash = 71 * hash + Double.valueOf(this.z).hashCode();
//            return hash;

            return id;
        }

        @Override
        public String toString() {
            return "P" + id;// + "|" + "x= " + this.x + ", y = " + this.y + ", z = " + this.z;
        }

        @Override
        public int compareTo(Point o) {
            if (this.id > o.id ) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public void create_landXml() {
        double minX = Collections.min(FirstElevation);
        double maxX = Collections.max(FirstElevation);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        calFileString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        calFileString.append("<LandXML version=\"1.2\">\r\n");
        calFileString.append("  <Units>\r\n");
        calFileString.append("    <Metric linearUnit=\"meter\" widthUnit=\"meter\" heightUnit=\"meter\" diameterUnit=\"meter\" areaUnit=\"squareMeter\" volumeUnit=\"cubicMeter\" temperatureUnit=\"celsius\" pressureUnit=\"HPA\" angularUnit=\"decimal degrees\" directionUnit=\"decimal degrees\" />\r\n");
        calFileString.append("  </Units>\r\n");
        calFileString.append("  <Application name=\"GEOMaster\" manufacturer=\"Apogee Gnss\" version=\"37.0.8236.15475\" timeStamp=\"" + currentDate + "\">\r\n");
        calFileString.append("    <Author createdBy=\"createdByName\" timeStamp=\"" + currentDate + "\" />\r\n");
        calFileString.append("  </Application>\r\n");
        calFileString.append("  <Surfaces>\r\n");
        calFileString.append("    <Surface name=\"MCW (Finish)\">\r\n");
        calFileString.append("      <Definition surfType=\"TIN\" elevMax=\"" + minX + "\" elevMin=\"" + maxX + "\">\r\n");
        calFileString.append("        <Pnts>\r\n");

        for (int i = 0; i < FirstNorthing.size(); i++) {
            int n = i + 1;
            calFileString.append("          <P id=\"" + n + "\">" + FirstNorthing.get(i) + " " + FirstEasting.get(i) + " " + FirstElevation.get(i) + "</P>\r\n");
        }

        calFileString.append("        </Pnts>\r\n");
        calFileString.append("        <Faces>\r\n");

        for (int i = 0; i < NorthingintegerList.size(); i++) {
            calFileString.append("          <F>" + (int) NorthingintegerList.get(i) + " " + (int) EastingintegerList.get(i) + " " + (int) ElevationintegerList.get(i) + "</F>\r\n");
        }

        calFileString.append("        </Faces>\r\n");
        calFileString.append("        <Feature code=\"ApogeeGNSS\">\r\n");
        calFileString.append("          <Property label=\"color\" value=\"128,128,128\" />\r\n");
        calFileString.append("        </Feature>\r\n");
        calFileString.append("      </Definition>\r\n");
        calFileString.append("    </Surface>\r\n");
        calFileString.append("  </Surfaces>\r\n");
        calFileString.append("</LandXML>\r\n");

        Log.d(TAG, "create_landXml: calFileString" + calFileString.toString());
    }
}
