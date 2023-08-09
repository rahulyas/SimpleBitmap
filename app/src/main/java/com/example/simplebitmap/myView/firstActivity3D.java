package com.example.simplebitmap.myView;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.simplebitmap.R;
import com.example.simplebitmap.Utils;

import java.util.ArrayList;
import java.util.List;

import io.github.jdiemke.triangulation.Triangle2D;

public class firstActivity3D extends AppCompatActivity {
    private final int PICK_TEXT = 101;
    List<Float> new_finallist = new ArrayList<>();
    List<String> list = new ArrayList<>();
    Uri fileuri;
    static List<Triangle2D> triangleList = null;
    static List<Float> list1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_activity3_d);

        Button button2 = (Button) findViewById(R.id.button1);
        button2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_TEXT);
        });

        Button button3 = (Button) findViewById(R.id.button2);
        button3.setOnClickListener(view -> {
            Intent intent = new Intent(firstActivity3D.this,Activity3D.class);
            startActivity(intent);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.getData();
            String path = new Utils().getFilePath(fileuri, firstActivity3D.this);
            String[] splitData = path.split("\\.");
            String splitDataPath = splitData[1];

            if (splitData.length > 0) {
                if (splitDataPath.contains("xml")) {
                    new Utils().readText(new Utils().getFilePath(fileuri));
                    ArrayList<Double> doubleList = new ArrayList<>();
                    doubleList = new Utils().readNorthingEasting();
                    for (Double doubleValue : doubleList) {
                        new_finallist.add(doubleValue.floatValue());
                    }
                } else if (splitDataPath.contains("txt")) {
                    list = new Utils().readAnyfile(fileuri,firstActivity3D.this);
                    // Convert ArrayList<Double> to List<Float>
                    ArrayList<Double> doubleList = new ArrayList<>();
                    doubleList = new Utils().SplitDataList(list);
                    for (Double doubleValue : doubleList) {
                        new_finallist.add(doubleValue.floatValue());
                    }
                }
            }
            triangleList = Delaunay1.doDelaunayFromGit(new_finallist);
            list1 = Delaunay1.doEdge(triangleList,new_finallist);
            Log.d(TAG, "onActivityResult: new_finallist == "+new_finallist);
            Log.d(TAG, "onActivityResult: triangleList == "+triangleList);
            Log.d(TAG, "onActivityResult: list1 == "+list1);
        }
    }

}