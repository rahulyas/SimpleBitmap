package com.example.simplebitmap;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class CSVActivity extends AppCompatActivity {

    Button browse, save;
    String mText;
    TextView TextPath;
    StringBuilder textA = new StringBuilder();
    StringBuilder textB = new StringBuilder();
    StringBuilder textC = new StringBuilder();
    StringBuilder textD = new StringBuilder();
    StringBuilder textD1 = new StringBuilder();
    StringBuilder textE = new StringBuilder();
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    String TAG="CSVActivity";


    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    private final int PICK_TEXT = 101;
    Uri fileuri;
    ArrayList<ArrayList<String>> point_list = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> shape_list = new ArrayList<ArrayList<String>>();
    ArrayList<Double> arrayListeasting = new ArrayList<Double>();
    ArrayList<Double> arrayListnorthing = new ArrayList<Double>();
    LinkedHashMap<String, List<Double>> point_data = new LinkedHashMap<String, List<Double>>();

    Double minX;
    Double maxX;
    Double minY;
    Double maxY;
    ////////////////////////////////////////////////////////////////
    HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> main = new HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>>();

    long time1;
    long time2;
    long time3;
    long time4;
    long time5;
    long time6;


    long time7;
    long time8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csvactivity);
        time1=System.currentTimeMillis();
        browse = findViewById(R.id.buttonnew);
        save = findViewById(R.id.buttonsave);

        TextPath = findViewById(R.id.textView_csvResult);

        requestpermission();

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time2=System.currentTimeMillis();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select CSV file"), PICK_TEXT);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                mText = TextPath.getText().toString().trim();
                Savetotext(String.valueOf(textB));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_TEXT && data != null) {

            fileuri = data.getData();
                TextPath.setText(readText(getFilePath(fileuri)));
                String path = getFilePath(fileuri);
                String[] spilitdata = path.split("\\.");

                time7=System.currentTimeMillis();
                if (spilitdata.length > 1) {
                    if (spilitdata[1].contains("txt")) {
                        readText(getFilePath(fileuri));
                    } else if (spilitdata[1].contains("csv")) {
                        readCSVFile(getFilePath(fileuri));
                    }

                }
                time8=System.currentTimeMillis();
                Log.d(TAG, "onActivityResult...: "+(time8 - time7));

        }
    }

    /// this method is used for getting file path from uri
    public String getFilePath(Uri uri) {
        String[] filename1;
        String fn;
        String filepath = uri.getPath();
        String filePath1[] = filepath.split(":");
        filename1 = filepath.split("/");
        fn = filename1[filename1.length - 1];
        return Environment.getExternalStorageDirectory().getPath() + "/" + filePath1[1];
    }

    /// reading file data
    public String readCSVFile(String path) {
        String filepath = null;
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            String temp_rows = scanner.nextLine();
            String[] splited_temp_rows = temp_rows.split(",");

            int index_point_name = Arrays.asList(splited_temp_rows).indexOf("Point_name");
            int index_easting = Arrays.asList(splited_temp_rows).indexOf("Easting");
            int index_northing = Arrays.asList(splited_temp_rows).indexOf("Northing");
            int index_elevation = Arrays.asList(splited_temp_rows).indexOf("Elevation");
//            int index_prefix = Arrays.asList(splited_temp_rows).indexOf("prefix");
            int index_prefix = Arrays.asList(splited_temp_rows).indexOf("Prefix");
//            int index_zone = Arrays.asList(splited_temp_rows).indexOf("zone");
            int index_zone = Arrays.asList(splited_temp_rows).indexOf("Zone");

            while (scanner.hasNextLine()) {
                temp_rows = scanner.nextLine();
                splited_temp_rows = temp_rows.split(",");

                ArrayList<String> temp_data = new ArrayList<String>();
                temp_data.add(splited_temp_rows[index_point_name]);
                temp_data.add(splited_temp_rows[index_easting]);
                temp_data.add(splited_temp_rows[index_northing]);
                temp_data.add(splited_temp_rows[index_elevation]);
                temp_data.add(splited_temp_rows[index_prefix]);
                temp_data.add(splited_temp_rows[index_zone]);

                String prefix = splited_temp_rows[index_prefix];

                point_list.add(temp_data);
                if (prefix.startsWith("Ln") || prefix.startsWith("Pl") || prefix.startsWith("Pg") || prefix.startsWith("Sq") || prefix.startsWith("1Cr") || prefix.startsWith("2Cr") || prefix.startsWith("Ar")) {
                    shape_list.add(temp_data);
                } else {
                }

            }
            time3=System.currentTimeMillis();
            Step1();
            time4=System.currentTimeMillis();
            Step2();
            time5=System.currentTimeMillis();
            Step3();
            time6=System.currentTimeMillis();

            Log.d(TAG, "total_time_in_Creating Activity...: "+(time2-time1));
            Log.d(TAG, "total_time_in_file_reading...: "+(time3-time2));
            Log.d(TAG, "total_time_in_STEP1...: "+(time4-time3));
            Log.d(TAG, "total_time_in_STEP2...: "+(time5-time4));
            Log.d(TAG, "total_time_in_STEP3...: "+(time6-time5));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        return filepath;

    }

    /// reading the Text file
    public String readText(String input) {
        File file = new File(input);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

////////////////////////////////////// Write file data //////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Savetotext(mText);
                } else {
                    Toast.makeText(getBaseContext(), "Storage Permiison Request", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    public void Savetotext(String s) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis());

        try {

            String tempPartA="";
            try{
                InputStream inputStream= getAssets().open("part_a.txt");
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                tempPartA = new String(buffer);
            }catch(Exception e){
                Log.d(TAG, "onActivityResult: ");
            }
            textA.delete(0,textA.length());
            textA.append(tempPartA);
            textA.append("\n");

            String tempPartC="";
            try{
                InputStream inputStream= getAssets().open("part_c.txt");
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                tempPartC = new String(buffer);
            }catch(Exception e){
                Log.d(TAG, "onActivityResult: ");
            }
            textC.delete(0,textC.length());
            textC.append(tempPartC);
            textC.append("\n");

            String tempPartE="";
            try{
                InputStream inputStream= getAssets().open("part_e.txt");
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                tempPartE = new String(buffer);
            }catch(Exception e){
                Log.d(TAG, "onActivityResult: ");
            }
            textE.delete(0,textE.length());
            textE.append(tempPartE);
            textE.append("\n");
//            File path = Environment.getExternalStorageDirectory();
            File path = new File("/storage/emulated/0/Android/data/com.apogee.surveydemo/files/projects/");

            File dir = new File(path, "ra");

            if(!dir.exists())
            {
                dir.mkdir();
            }
            String filename = "MyFile_" + timeStamp + ".dxf";

            File file = new File(dir, filename);
            Log.d(TAG, "onActivityResult  MyFile_: "+file.getAbsolutePath());
            FileWriter writer = new FileWriter(file.getAbsolutePath());

            BufferedWriter fw = new BufferedWriter(writer);
//            fw.write(String.valueOf(sb));

            fw.append(String.valueOf(textA));
            fw.append(String.valueOf(textB));
            fw.append(String.valueOf(textC));
            fw.append(String.valueOf(textD));
            fw.append(String.valueOf(textD1));
            fw.append(String.valueOf(textE));
            fw.close();
            //display file saved message
            Toast.makeText(getBaseContext(), filename + "Saved \n" + path,
                    Toast.LENGTH_SHORT).show();
            Log.i("Save", "File saved successfully!" + path);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "Error");
        }

    }
////////////////////////////////////////////////////////////////
    public void Step1() {
        Log.d(TAG, "point_list: "+point_list.size()+"======="+point_list.get(0));
       // ArrayList<Double> temp_point_coord = new ArrayList<>();
        Log.d("timeCheck-", String.valueOf(System.currentTimeMillis()));
        for (int k = 0; k < point_list.size(); k++) {
            point_data.put(String.valueOf(point_list.get(k).get(0)),  Arrays.asList(Double.parseDouble(point_list.get(k).get(1)),Double.parseDouble(point_list.get(k).get(2)), Double.parseDouble(point_list.get(k).get(3))));
            arrayListeasting.add(Double.parseDouble(point_list.get(k).get(1)));
            arrayListnorthing.add(Double.parseDouble(point_list.get(k).get(2)));
        }
        Log.d("timeCheck--", String.valueOf(System.currentTimeMillis()));

//////////////////////////// *** This is only for printing //////////////////////////////////
//            for ( Map.Entry<String, ArrayList<csvmodel>> entry : point_data.entrySet()) {
//                String key = entry.getKey();
//                ArrayList<csvmodel> tab = entry.getValue();
//                minX = Collections.min(arrayListeasting);
//
//                for (int i=0; i<tab.size(); i++) {
//                    Log.d("Point_data--", key +","+ tab.get(i).Point_name +","+ tab.get(i).Northing +","+ tab.get(i).Easting);
//                }
//
//                break;
//            }
////////////////////////////////////////////////////////////////
        for (int k = 0; k < shape_list.size(); k++) {
//                Log.i("Point_list",""+String.valueOf(point_list.get(k)));
            HashMap<String, HashMap<String, ArrayList<String>>> shape_map = new HashMap<String, HashMap<String, ArrayList<String>>>();
            HashMap<String, ArrayList<String>> point_map = new HashMap<String, ArrayList<String>>();
            HashMap<String, ArrayList<String>> name_map = new HashMap<String, ArrayList<String>>();

            String point_name = String.valueOf(shape_list.get(k).get(0));
            Double easting = Double.parseDouble(shape_list.get(k).get(1));
            Double northing = Double.parseDouble(shape_list.get(k).get(2));
            Double elevation = Double.parseDouble(shape_list.get(k).get(3));
            String prefix = String.valueOf(shape_list.get(k).get(4));
            String[] prefix_splitted = prefix.split("_");
            String prefix_start = prefix_splitted[0].substring(0, prefix_splitted[0].length() - 1);

            ArrayList<String> point_name_array = new ArrayList<String>();

            ArrayList<String> point_coords_array = new ArrayList<String>();
            point_coords_array.add(String.valueOf(easting));
            point_coords_array.add(String.valueOf(northing));
            point_coords_array.add(String.valueOf(elevation));

            point_name_array.add(point_name);


            switch (prefix_start) {
                case "Ln":
                    if (String.valueOf(prefix_splitted[1]).equals("st"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else if (String.valueOf(prefix_splitted[1]).equals("en"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else {
                        Log.i(" LINE SHAPE ERROR", "Invalid LINE Prefix End type" + String.valueOf(prefix_splitted[1]));
                        break;
                    }
                    name_map.put(point_name, point_name_array);
                    shape_map.put("point_coords", point_map);
                    shape_map.put("name", name_map);
                    if (main.containsKey(String.valueOf(prefix_splitted[0])))
                        main.get(String.valueOf(prefix_splitted[0])).get("point_coords").put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else
                        main.put(String.valueOf(prefix_splitted[0]), shape_map);
                    break;

                case "Pl":
                    point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    name_map.put(point_name, point_name_array);
                    shape_map.put("point_coords", point_map);
                    shape_map.put("name", name_map);
                    if (main.containsKey(String.valueOf(prefix_splitted[0])))
                        main.get(String.valueOf(prefix_splitted[0])).get("point_coords").put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else
                        main.put(String.valueOf(prefix_splitted[0]), shape_map);
                    break;

                case "Pg":
                    point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    name_map.put(point_name, point_name_array);
                    shape_map.put("point_coords", point_map);
                    shape_map.put("name", name_map);
                    if (main.containsKey(String.valueOf(prefix_splitted[0])))
                        main.get(String.valueOf(prefix_splitted[0])).get("point_coords").put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else
                        main.put(String.valueOf(prefix_splitted[0]), shape_map);
                    break;


                case "Sq":
                    if (String.valueOf(prefix_splitted[1]).equals("t"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else if (String.valueOf(prefix_splitted[1]).equals("b"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else {
                        Log.i(" SQUARE SHAPE ERROR", "Invalid SQUARE Prefix End type" + String.valueOf(prefix_splitted[1]));
                        break;
                    }
                    name_map.put(point_name, point_name_array);
                    shape_map.put("point_coords", point_map);
                    shape_map.put("name", name_map);
                    if (main.containsKey(String.valueOf(prefix_splitted[0])))
                        main.get(String.valueOf(prefix_splitted[0])).get("point_coords").put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else
                        main.put(String.valueOf(prefix_splitted[0]), shape_map);
                    break;

                case "1Cr":
                    if (String.valueOf(prefix_splitted[1]).equals("c"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else if (String.valueOf(prefix_splitted[1]).equals("r"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else {
                        Log.i(" 1 CIRCLE SHAPE ERROR", "Invalid CIRCLE Prefix End type" + String.valueOf(prefix_splitted[1]));
                        break;
                    }
                    name_map.put(point_name, point_name_array);
                    shape_map.put("point_coords", point_map);
                    shape_map.put("name", name_map);
                    if (main.containsKey(String.valueOf(prefix_splitted[0])))
                        main.get(String.valueOf(prefix_splitted[0])).get("point_coords").put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else
                        main.put(String.valueOf(prefix_splitted[0]), shape_map);
                    break;

                case "2Cr":
                    if (String.valueOf(prefix_splitted[1]).equals("1"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else if (String.valueOf(prefix_splitted[1]).equals("2"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else if (String.valueOf(prefix_splitted[1]).equals("3"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else {
                        Log.i(" 2 CIRCLE SHAPE ERROR", "Invalid CIRCLE Prefix End type" + String.valueOf(prefix_splitted[1]));
                        break;
                    }
                    name_map.put(point_name, point_name_array);
                    shape_map.put("point_coords", point_map);
                    shape_map.put("name", name_map);
                    if (main.containsKey(String.valueOf(prefix_splitted[0])))
                        main.get(String.valueOf(prefix_splitted[0])).get("point_coords").put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else
                        main.put(String.valueOf(prefix_splitted[0]), shape_map);
                    break;


                case "Ar":
                    if (String.valueOf(prefix_splitted[1]).equals("st"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else if (String.valueOf(prefix_splitted[1]).equals("m"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else if (String.valueOf(prefix_splitted[1]).equals("en"))
                        point_map.put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else {
                        Log.i("ARC SHAPE ERROR", "Invalid ARC Prefix End type" + String.valueOf(prefix_splitted[1]));
                        break;
                    }
                    name_map.put(point_name, point_name_array);
                    shape_map.put("point_coords", point_map);
                    shape_map.put("name", name_map);

                    if (main.containsKey(String.valueOf(prefix_splitted[0])))
                        main.get(String.valueOf(prefix_splitted[0])).get("point_coords").put(String.valueOf(prefix_splitted[1]), point_coords_array);
                    else
                        main.put(String.valueOf(prefix_splitted[0]), shape_map);
                    break;
            }

        }


    }

    ////////////////////////////////
    public void Step2() {
//            System.out.println("Key: "+ main.get("Pg1").get("point_coords").get("1"));
        for (Map.Entry keyValue : main.entrySet()) {
            String key = String.valueOf(keyValue.getKey());
//            Log.i("key -->> ", key);
            if (key.startsWith("Sq"))
                if (main.get(key).get("point_coords").size() == 2) {
                    ArrayList<String> top_point = main.get(key).get("point_coords").get("t");
                    double top_point_X = Double.parseDouble(top_point.get(0));
                    double top_point_Y = Double.parseDouble(top_point.get(1));
                    ArrayList<String> bottom_point = main.get(key).get("point_coords").get("b");
                    double bottom_point_X = Double.parseDouble(bottom_point.get(0));
                    double bottom_point_Y = Double.parseDouble(bottom_point.get(1));

                    double diagonal_len = Math.sqrt(Math.pow((top_point_X - bottom_point_X), 2) + Math.pow((top_point_Y - bottom_point_Y), 2));
                    double radius = diagonal_len / 1.41421;

                    double mid_point_distance = diagonal_len / 2;
                    double height = (Math.sqrt(Math.pow(radius, 2)) - Math.pow(mid_point_distance, 2));

                    double temp_mid_point_X = (bottom_point_X + mid_point_distance * (top_point_X - bottom_point_X)) / diagonal_len;
                    double temp_mid_point_Y = (bottom_point_Y + mid_point_distance * (top_point_Y - bottom_point_Y)) / diagonal_len;

                    double x3 = temp_mid_point_X + height * (top_point_Y - bottom_point_Y) / diagonal_len;
                    double y3 = temp_mid_point_Y - height * (top_point_X - bottom_point_X) / diagonal_len;
                    double x4 = temp_mid_point_X - height * (top_point_Y - bottom_point_Y) / diagonal_len;
                    double y4 = temp_mid_point_Y + height * (top_point_X - bottom_point_X) / diagonal_len;

                    ArrayList<String> temp_topPoints = new ArrayList<>();
                    ArrayList<String> temp_bottomPoints = new ArrayList<>();
                    ArrayList<String> temp_x3 = new ArrayList<>();
                    ArrayList<String> temp_x4 = new ArrayList<>();

                    temp_topPoints.add(String.format("%.10f", top_point_X));
                    temp_topPoints.add(String.format("%.10f", top_point_Y));
                    temp_bottomPoints.add(String.format("%.10f", bottom_point_X));
                    temp_bottomPoints.add(String.format("%.10f", bottom_point_Y));
                    temp_x3.add(String.format("%.10f", x3));
                    temp_x3.add(String.format("%.10f", y3));
                    temp_x4.add(String.format("%.10f", x4));
                    temp_x4.add(String.format("%.10f", y4));

                    main.get(key).get("point_coords").remove("t");
                    main.get(key).get("point_coords").remove("b");
                    main.get(key).get("point_coords").put("1", temp_topPoints);
                    main.get(key).get("point_coords").put("2", temp_bottomPoints);
                    main.get(key).get("point_coords").put("3", temp_x3);
                    main.get(key).get("point_coords").put("4", temp_x4);

                } else
                    System.out.println("InSuffecient Points in Square GEOM...");

            else if (key.startsWith("1Cr"))
                if (main.get(key).get("point_coords").size() == 2) {
                    ArrayList<String> start_point = main.get(key).get("point_coords").get("c");
                    double start_point_0 = Double.parseDouble(start_point.get(0));
                    double start_point_1 = Double.parseDouble(start_point.get(1));
                    ArrayList<String> radius_point = main.get(key).get("point_coords").get("r");
                    double radius_point_0 = Double.parseDouble(radius_point.get(0));
                    double radius_point_1 = Double.parseDouble(radius_point.get(1));

                    double radius = Math.sqrt(Math.pow((start_point_0 - radius_point_0), 2) + Math.pow((start_point_1 - radius_point_1), 2));

                    ArrayList<String> temp_center_point = new ArrayList<>();
                    ArrayList<String> temp_radius = new ArrayList<>();

                    temp_center_point.add(String.format("%.10f", start_point_0));
                    temp_center_point.add(String.format("%.10f", start_point_1));
                    temp_radius.add(String.format("%.10f", radius));

                    main.get(key).get("point_coords").remove("c");
                    main.get(key).get("point_coords").remove("r");
                    main.get(key).get("point_coords").put("c", temp_center_point);
                    main.get(key).get("point_coords").put("rd", temp_radius);
                } else
                    System.out.println("InSuffecient Points in Circle GEOM...");

            else if (key.startsWith("2Cr"))
                if (main.get(key).get("point_coords").size() == 3) {
                    ArrayList<String> start_point = main.get(key).get("point_coords").get("1");
                    double start_point_0 = Double.parseDouble(start_point.get(0));
                    double start_point_1 = Double.parseDouble(start_point.get(1));
                    ArrayList<String> mid_point = main.get(key).get("point_coords").get("2");
                    double mid_point_0 = Double.parseDouble(mid_point.get(0));
                    double mid_point_1 = Double.parseDouble(mid_point.get(1));
                    ArrayList<String> end_point = main.get(key).get("point_coords").get("3");
                    double end_point_0 = Double.parseDouble(end_point.get(0));
                    double end_point_1 = Double.parseDouble(end_point.get(1));

                    double f = (((Math.pow(start_point_0, 2) - Math.pow(end_point_0, 2)) * (start_point_0 - mid_point_0) + (Math.pow(start_point_1, 2) - Math.pow(end_point_1, 2) * (start_point_0 - mid_point_0) + Math.pow(mid_point_0, 2) - Math.pow(start_point_0, 2)) * (start_point_0 - end_point_0) + (Math.pow(mid_point_1, 2) - Math.pow(start_point_1, 2)) * (start_point_0 - end_point_0))
                            / 2 * ((end_point_1 - start_point_1) * (start_point_0 - mid_point_0) - (mid_point_1 - start_point_1) * (start_point_0 - end_point_0)));

                    double g = (((Math.pow(start_point_0, 2) - Math.pow(end_point_0, 2)) * (start_point_1 - mid_point_1) + (Math.pow(start_point_1, 2) - Math.pow(end_point_1, 2) * (start_point_1 - mid_point_1) + Math.pow(mid_point_0, 2) - Math.pow(start_point_0, 2)) * (start_point_1 - end_point_1) + (Math.pow(mid_point_1, 2) - Math.pow(start_point_1, 2)) * (start_point_1 - end_point_1))
                            / 2 * ((end_point_0 - start_point_0) * (start_point_1 - mid_point_1) - (mid_point_0 - start_point_0) * (start_point_1 - end_point_1)));

                    double c = (-Math.pow(start_point_0, 2) - Math.pow(start_point_1, 2) - 2 * g * start_point_0 - 2 * f * start_point_1);

                    double h, k;
                    h = -g;
                    k = -f;

                    double radius = Math.sqrt(Math.pow(h, 2) + Math.pow(k, 2) - c);

                    ArrayList<String> temp_center_point = new ArrayList<>();
                    ArrayList<String> temp_radius = new ArrayList<>();

                    temp_center_point.add(String.format("%.10f", h));
                    temp_center_point.add(String.format("%.10f", k));
                    temp_radius.add(String.format("%.10f", radius));

                    main.get(key).get("point_coords").remove("1");
                    main.get(key).get("point_coords").remove("2");
                    main.get(key).get("point_coords").remove("3");
                    main.get(key).get("point_coords").put("c", temp_center_point);
                    main.get(key).get("point_coords").put("rd", temp_radius);

                } else
                    System.out.println("InSuffecient Points in Circle GEOM...");

            else if (key.startsWith("Ar"))
                if (main.get(key).get("point_coords").size() == 3) {
                    ArrayList<String> start_point = main.get(key).get("point_coords").get("st");
                    double start_point_0 = Double.parseDouble(start_point.get(0));
                    double start_point_1 = Double.parseDouble(start_point.get(1));
                    ArrayList<String> mid_point = main.get(key).get("point_coords").get("m");
                    double mid_point_0 = Double.parseDouble(mid_point.get(0));
                    double mid_point_1 = Double.parseDouble(mid_point.get(1));
                    ArrayList<String> end_point = main.get(key).get("point_coords").get("en");
                    double end_point_0 = Double.parseDouble(end_point.get(0));
                    double end_point_1 = Double.parseDouble(end_point.get(1));

                    double f = (((Math.pow(start_point_0, 2) - Math.pow(end_point_0, 2)) * (start_point_0 - mid_point_0) + (Math.pow(start_point_1, 2) - Math.pow(end_point_1, 2) * (start_point_0 - mid_point_0) + Math.pow(mid_point_0, 2) - Math.pow(start_point_0, 2)) * (start_point_0 - end_point_0) + (Math.pow(mid_point_1, 2) - Math.pow(start_point_1, 2)) * (start_point_0 - end_point_0))
                            / 2 * ((end_point_1 - start_point_1) * (start_point_0 - mid_point_0) - (mid_point_1 - start_point_1) * (start_point_0 - end_point_0)));

                    double g = (((Math.pow(start_point_0, 2) - Math.pow(end_point_0, 2)) * (start_point_1 - mid_point_1) + (Math.pow(start_point_1, 2) - Math.pow(end_point_1, 2) * (start_point_1 - mid_point_1) + Math.pow(mid_point_0, 2) - Math.pow(start_point_0, 2)) * (start_point_1 - end_point_1) + (Math.pow(mid_point_1, 2) - Math.pow(start_point_1, 2)) * (start_point_1 - end_point_1))
                            / 2 * ((end_point_0 - start_point_0) * (start_point_1 - mid_point_1) - (mid_point_0 - start_point_0) * (start_point_1 - end_point_1)));

                    double c = (-Math.pow(start_point_0, 2) - Math.pow(start_point_1, 2) - 2 * g * start_point_0 - 2 * f * start_point_1);

                    double h, k;
                    h = -g;
                    k = -f;

                    double radius = Math.sqrt(Math.pow(h, 2) + Math.pow(k, 2) - c);

                    double st_angle = Math.toDegrees(Math.atan2((end_point_1 - k), (end_point_0 - h)));
                    double en_angle = Math.toDegrees(Math.atan2((start_point_1 - k), (start_point_0 - h)));
                    st_angle = st_angle + Math.ceil(-st_angle / 360) * 360;
                    en_angle = en_angle + Math.ceil(-en_angle / 360) * 360;
                    double stAngle = Math.min(st_angle, en_angle);
                    double enAngle = Math.max(st_angle, en_angle);

                    ArrayList<String> temp_center_point = new ArrayList<>();
                    ArrayList<String> temp_radius = new ArrayList<>();
                    ArrayList<String> temp_start_angle = new ArrayList<>();
                    ArrayList<String> temp_end_angle = new ArrayList<>();

                    temp_center_point.add(String.format("%.10f", h));
                    temp_center_point.add(String.format("%.10f", k));
                    temp_radius.add(String.format("%.10f", radius));
                    temp_start_angle.add(String.format("%.10f", radius));
                    temp_end_angle.add(String.format("%.10f", radius));

                    main.get(key).get("point_coords").remove("st");
                    main.get(key).get("point_coords").remove("m");
                    main.get(key).get("point_coords").remove("en");
                    main.get(key).get("point_coords").put("c", temp_center_point);
                    main.get(key).get("point_coords").put("rd", temp_radius);
                    main.get(key).get("point_coords").put("sa", temp_start_angle);
                    main.get(key).get("point_coords").put("en", temp_end_angle);
                } else
                    System.out.println("InSuffecient Points in Arc GEOM...");

        }

//            System.out.println("Retreived entry set is : " + main.entrySet());
//        if (SDK_INT >= Build.VERSION_CODES.N) {
//            main.forEach(
//                    (key, value)
//                            -> System.out.println(key + " = " + value));
    }

    ////////////////////////////////
    public void Step3() {
        int page_margin = 4;
        int hex_value = 80;

        double TEXT_HEIGHT = 0.1;
        int POINT_SIZE = 0;
        double HEIGHT_BOX_WIDTH = 4.0;
        double NUMBER_BOX_WIDTH = 2.0;
        double POINT_HEIGHT_BOX_X_OFFSET = 2 * 0.2;
        double POINT_HEIGHT_BOX_Y_OFFSET = 0.0;
        double POINT_NUMBER_BOX_X_OFFSET = 2 * 0.2;
        double POINT_NUMBER_BOX_Y_OFFSET = 0.0;

//        Log.d("csvmodelArrayList", String.valueOf(csvmodelArrayList));
        Log.d("arrayListeasting", String.valueOf(arrayListeasting));
        Log.d("arrayListnorthing", String.valueOf(arrayListnorthing));

        minX = Collections.min(arrayListeasting);
        maxX = Collections.max(arrayListeasting);
        minY = Collections.min(arrayListnorthing);
        maxY = Collections.max(arrayListnorthing);

        textB.append("  9\n" +
                "$EXTMIN \n" +
                " 10 \n" +
                minX + "\n" +
                " 20\n" +
                minY + "\n" +
                " 30 \n" +
                "0 \n" +
                "  9 \n" +
                "$EXTMAX \n" +
                " 10\n" +
                maxX + "\n" +
                " 20 \n" +
                maxY + "\n" +
                " 30 \n" +
                "0 \n");


        for (LinkedHashMap.Entry keyValue : point_data.entrySet()) {
            String key = String.valueOf(keyValue.getKey());
            ArrayList<Double> coords = new ArrayList();
            coords.add(point_data.get(key).get(0));
            coords.add(point_data.get(key).get(1));
            coords.add(point_data.get(key).get(2));

//            Log.d("Northing",""+String.valueOf(coords.get(0)));
//            Log.d("Easting",""+String.valueOf(coords.get(1)));
//            Log.d("Easting",""+String.valueOf(coords.get(2)));

            textD.append("  0\n" +
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
                    coords.get(0) + "\n" +
                    " 20\n" +
                    coords.get(1) + "\n" +
                    " 30\n" +
                    coords.get(2) + "\n");

            hex_value++;
            textD.append("  0\n" +
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
                    (coords.get(0) - POINT_NUMBER_BOX_X_OFFSET) + "\n" +
                    " 20\n" +
                    (coords.get(1) - POINT_NUMBER_BOX_Y_OFFSET) + "\n" +
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
                    "1\n");

            hex_value++;
        }


        for (Map.Entry keyValue : main.entrySet()) {
            String key = String.valueOf(keyValue.getKey());
            String value = String.valueOf(keyValue.getValue());

            if (key.startsWith("Ln"))
                if (main.get(key).get("point_coords").size() == 2) {
                    textD1.append("  0\n" +
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
                            main.get(key).get("point_coords").get("st").get(0) + "\n" +
                            "  20\n" +
                            main.get(key).get("point_coords").get("st").get(1) + "\n" +
                            "  30\n" +
                            "0.0\n" +
                            "  11\n" +
                            main.get(key).get("point_coords").get("en").get(0) + "\n" +
                            "  21\n" +
                            main.get(key).get("point_coords").get("en").get(1) + "\n" +
                            "  31\n" +
                            "0.0\n");

                    hex_value++;
                }

            if (key.startsWith("Pl"))
                if (main.get(key).get("point_coords").size() > 2) {
                    textD1.append("  0\n"+
                            "POLYLINE\n"+
                            "  5\n"+
                            (Integer.toHexString(hex_value)) + "\n" +
                            "  100\n"+
                            "AcDbEntity\n"+
                            "  8\n"+
                            "0\n"+
                            "  100\n"+
                            "AcDb3dPolyline\n"+
                            "  66\n"+
                            "1\n"+
                            "  10\n"+
                            "0.0\n"+
                            "  20\n"+
                            "0.0\n"+
                            "  30\n"+
                            "0.0\n"+
                            "  39\n"+
                            "8.0\n"+
                            "  70\n"+
                            "0\n");

                    hex_value++;


                    for (int i = 0; i <= main.get(key).get("point_coords").size(); i++) {
                          if(main.get(key).get("point_coords").containsKey(String.valueOf(i))) {
                              textD1.append("  0\n" +
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
                                      main.get(key).get("point_coords").get(String.valueOf(i)).get(0) + "\n" +
                                      "  20\n" +
                                      main.get(key).get("point_coords").get(String.valueOf(i)).get(1) + "\n" +
                                      "  30\n" +
                                      "0.0\n" +
                                      "  70\n" +
                                      "32\n");

                              hex_value++;
                          }
                    }

                    textD1.append("  0\n"+
                            "SEQEND\n"+
                            "  5\n"+
                            (Integer.toHexString(hex_value)) + "\n" +
                            "300\n"+
                            (Integer.toHexString(hex_value+1)) + "\n" +
                            "  100\n"+
                            "AcDbEntity\n"+
                            "  8\n"+
                            "0\n");

                    hex_value++;

                }

            if (key.startsWith("Pg"))
                if (main.get(key).get("point_coords").size() > 3) {
                    textD1.append("  0\n"+
                            "POLYLINE\n"+
                            "  5\n"+
                            (Integer.toHexString(hex_value)) + "\n" +
                            "  100\n"+
                            "AcDbEntity\n"+
                            "  8\n"+
                            "0\n"+
                            "  100\n"+
                            "AcDb3dPolyline\n"+
                            "  66\n"+
                            "1\n"+
                            "  10\n"+
                            "0.0\n"+
                            "  20\n"+
                            "0.0\n"+
                            "  30\n"+
                            "0.0\n"+
                            "  39\n"+
                            "8.0\n"+
                            "  70\n"+
                            "1\n");

                    hex_value++;

                    for (int i = 0; i <= main.get(key).get("point_coords").size(); i++) {
                          if(main.get(key).get("point_coords").containsKey(String.valueOf(i))) {
                              textD1.append("  0\n" +
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
                                      main.get(key).get("point_coords").get(String.valueOf(i)).get(0) + "\n" +
                                      "  20\n" +
                                      main.get(key).get("point_coords").get(String.valueOf(i)).get(1) + "\n" +
                                      "  30\n" +
                                      "0.0\n" +
                                      "  70\n" +
                                      "32\n");

                              hex_value++;
                          }
                    }

                    textD1.append("  0\n"+
                            "SEQEND\n"+
                            "  5\n"+
                            (Integer.toHexString(hex_value)) + "\n" +
                            "300\n"+
                            (Integer.toHexString(hex_value+1)) + "\n" +
                            "  100\n"+
                            "AcDbEntity\n"+
                            "  8\n"+
                            "0\n");

                    hex_value++;

                }

            if (key.startsWith("1Cr") || key.startsWith("2Cr"))
                if (main.get(key).get("point_coords").size() == 2) {
                    textD1.append("  0\n" +
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
                            main.get(key).get("point_coords").get("c").get(0) + "\n" +
                            "  20\n" +
                            main.get(key).get("point_coords").get("c").get(1) + "\n" +
                            "  30\n" +
                            "0.0\n" +
                            "  40\n" +
                            main.get(key).get("point_coords").get("rd").get(0) + "\n");

                    hex_value++;
                }

            if (key.startsWith("Ar"))
                if (main.get(key).get("point_coords").size() == 4) {
                    textD1.append("  0\n" +
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
                            main.get(key).get("point_coords").get("c").get(0) + "\n" +
                            "  20\n" +
                            main.get(key).get("point_coords").get("c").get(1) + "\n" +
                            "  30\n" +
                            "0.0\n" +
                            "  40\n" +
                            main.get(key).get("point_coords").get("rd").get(0) + "\n" +
                            "  100\n" +
                            "AcDbArc\n" +
                            "  50\n" +
                            main.get(key).get("point_coords").get("sa").get(0) + "\n" +
                            "  51\n" +
                            main.get(key).get("point_coords").get("ea").get(0) + "\n");

                    hex_value++;
                }
        }
    }

/// Runtime RequestPermission
    public void requestpermission() {
        //request permission for Read
        if (SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }
        //request permission for Write
        if (SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }

//        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
//        return permission1 == PackageManager.PERMISSION_GRANTED &&  permission2 == PackageManager.PERMISSION_GRANTED;
    }
}