package com.example.simplebitmap;

import static android.os.Build.VERSION.SDK_INT;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ortiz.touchview.OnTouchImageViewListener;
import com.ortiz.touchview.TouchImageView;

import org.beyka.tiffbitmapfactory.TiffBitmapFactory;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RasterImageActivity extends AppCompatActivity {
    Bitmap bitmap;
    Display currentdisplay;
    float dw = 0;
    float dh = 0;
    float centerX = 0;
    float centerY = 0;
    int minX = 0;
    int maxX = 0;
    int minY = 0;
    int maxY = 0;
    String filename="";
    double differenceX = 0.0;
    double differenceY = 0.0;
    double differenceXY = 0.0;
    double meanofX = 0.0;
    double meanofY = 0.0;
    double scaleXY = 0.0;
    int diffX = 0;
    int diffY = 0;
    float deltaX = 0f;
    float deltaY = 0f;
    float curSpanX = 0f;
    float prevSpanX = 0f;
    float curSpanY = 0f;
    float prevSpanY = 0f;
    boolean isScroll = false;
    boolean isScaleSetFirstTime = true;
    Canvas canvas;
    Paint paint,paint1;
    TouchImageView imageView;
    ArrayList<Integer> listx = new ArrayList<Integer>(Arrays.asList(400,800,400,800,700));
    ArrayList<Integer> listy = new ArrayList<Integer>(Arrays.asList(800,800,400,400,700));
    /*    ArrayList<Integer> listx = new ArrayList<Integer>(Arrays.asList(123585, 123585, 357015, 357015));
        ArrayList<Integer> listy = new ArrayList<Integer>(Arrays.asList(3474015, 3236685, 3236685, 3474015));*/
    ArrayList<Integer> pixelofX = new ArrayList<Integer>();
    ArrayList<Integer> pixelofY = new ArrayList<Integer>();
    AppCompatButton selectImageButton;
    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    private String imagePath = "";
    Bitmap bmp;
    int firstcenterX;
    int secondcenterY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raster_image);
        onRequestpermission();
        createDisplay();
    }
    public void createDisplay() {
        currentdisplay = getWindowManager().getDefaultDisplay();
        dw = currentdisplay.getWidth();
        dh = currentdisplay.getHeight();
        centerX = (float) currentdisplay.getWidth() / 2;
        centerY = (float) currentdisplay.getHeight() / 2;
        bitmap = Bitmap.createBitmap((int) dw, (int) dh, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        imageView = findViewById(R.id.newbitmap);
        selectImageButton = findViewById(R.id.load);
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, 101);
            }
        });
        paint();
        imageView.setOnTouchImageViewListener(new OnTouchImageViewListener() {
            @Override
            public void onMove(float x, float y, float scaleFactor) {
                if (listx.size() > 0 && listy.size() > 0) {
                    if (x < dw && y < dh) {
                        float changeSpanX = (float) ((curSpanX - prevSpanX));
                        float deltaSlop = (float) ((changeSpanX / dw) * scaleXY);
                        scaleXY = deltaSlop + scaleXY;
                        getPixel();
                        draw();
                        isScroll = true;
                        isScaleSetFirstTime = false;
                    }
                }

            }

            @Override
            public void onScaleBegin(float x, float y, float scaleFactor) {

            }

            @Override
            public void onScaleEnd(float x, float y, float scaleFactor) {
            }

            @Override
            public void onDrag(float deltax, float deltay) {
                if (listx.size() > 0) {
                    deltaX += deltax/scaleXY;
                    deltaY += -deltay/scaleXY;
                    getPixel();
                    draw();
                    isScaleSetFirstTime = false;
                }
            }

            @Override
            public void onSpan(float currX, float currY, float prevX, float prevY) {
                curSpanX = currX;
                curSpanY = currY;
                prevSpanX = prevX;
                prevSpanY = prevY;

                Log.d(TAG, "curSpanX: " + curSpanX);
                Log.d(TAG, "curSpanY: " + curSpanY);
                Log.d(TAG, "prevSpanX: " + prevSpanX);
                Log.d(TAG, "prevSpanY: " + prevSpanY);
            }

            @Override
            public void onSingleTap(float x, float y) {
            }
        });
        CalculatePointplot();

    }
    public void paint() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint1 = new Paint();
        paint1.setColor(Color.GREEN);
        paint1.setStyle(Paint.Style.FILL);
    }
    public void CalculatePointplot() {
        minX = Collections.min(listx);
        maxX = Collections.max(listx);
        minY = Collections.min(listy);
        maxY = Collections.max(listy);
        differenceX = maxX - minX;
        differenceY = maxY - minY;
        differenceXY = Math.max(differenceX, differenceY);
        meanofX = (minX + maxX) / 2;
        meanofY = (minY + maxY) / 2;
        scaleXY = (dw / differenceXY);
        getPixel();
    }
    public void getPixel() {
        double plotX;
        double plotY;

        ArrayList<Integer> plotValueX = new ArrayList<Integer>(listx);
        ArrayList<Integer> plotValueY = new ArrayList<Integer>(listy);

        if(plotValueX.size() > 1){

            for(int i = 0; i < plotValueX.size(); i++) {
                double value = (plotValueX.get(i) + diffX) + deltaX;
                plotValueX.set(i, (int) value);
            }

            for(int i = 0; i < plotValueY.size(); i++) {
                double value = (plotValueY.get(i) + diffY) + deltaY;
                plotValueY.set(i, (int) value);
            }

            pixelofX.clear();
            pixelofY.clear();

            for(int i = 0; i < plotValueX.size(); i++) {
                if (plotValueX.get(i) > meanofX) {
                    plotX = (dw / 2) + ((Math.abs(meanofX - plotValueX.get(i) )) * scaleXY);
                } else {
                    plotX = (dw / 2) - ((Math.abs(meanofX - plotValueX.get(i) )) * scaleXY);
                }
                pixelofX.add((int) plotX);
            }

            for(int i = 0; i < plotValueY.size(); i++) {
                if (plotValueY.get(i) > meanofY) {
                    plotY = (dh / 2) + ((Math.abs(meanofY - plotValueY.get(i) )) * scaleXY);
                } else {
                    plotY = (dh / 2) - ((Math.abs(meanofY - plotValueY.get(i) )) * scaleXY);
                }
                plotY = dh - plotY;
                pixelofY.add((int) plotY);
            }
        }
//        calculatethecenter();
    }
    public void draw(){
        bitmap = Bitmap.createBitmap((int) dw, (int) dh, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawBitmap(bmp, null, new RectF(pixelofX.get(0), pixelofY.get(0), pixelofX.get(3), pixelofY.get(3)), null);
        for(int j=0; j<pixelofX.size() && j<pixelofY.size() ; j++){
            canvas.drawText(""+(j+1),pixelofX.get(j).floatValue(),pixelofY.get(j).floatValue(),paint);
            canvas.drawCircle(pixelofX.get(j).floatValue(),pixelofY.get(j).floatValue(),25,paint);
            imageView.setImageBitmap(bitmap);
        }
    }
//    public void calculatethecenter(){
//        int x = pixelofX.get(0);
//        int y = pixelofY.get(0);
//        int x1 = pixelofX.get(1);
//        int y1 = pixelofY.get(1);
//        firstcenterX =  x+x1/2;
//        secondcenterY = y+y1/2;
//        Log.d("TAG", "calculatethecenterX => "+firstcenterX);
//        Log.d("TAG", "calculatethecenterY => "+secondcenterY);
//
//    }
    public void onRequestpermission(){
        if(SDK_INT>= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} , PERMISSION_REQUEST_STORAGE);
        }
    }

    @SuppressLint("Range")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            Uri pickedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            // here we check image type/
            String ext = "";
            int i = imagePath.lastIndexOf('.');
            if (i > 0 && i < imagePath.length() - 1) {
                ext = imagePath.substring(i + 1).toLowerCase();
            }
            if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif"))
            {
                bmp =  BitmapFactory.decodeFile(imagePath);
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                getCoordinatesFromExif(inputStream);
                draw();
                if (imagePath != null) {
                    ExifInterface exifInterface = null;
                    try {
                        exifInterface = new ExifInterface(imagePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Read or write EXIF tags as per your requirement
                    String exif = "Exif: " + imagePath;
                    exif += "\n IMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                    exif += "\n IMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                    exif += "\n DATETIME: " + exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                    exif += "\n TAG_MAKE: " + exifInterface.getAttribute(ExifInterface.TAG_MAKE);
                    exif += "\n TAG_MODEL: " + exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                    exif += "\n TAG_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                    exif += "\n TAG_WHITE_BALANCE: " + exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
                    exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                    exif += "\n TAG_FLASH: " + exifInterface.getAttribute(ExifInterface.TAG_FLASH);
                    exif += "\n TAG_STRIP_OFFSETS: " + exifInterface.getAttribute(ExifInterface.TAG_STRIP_OFFSETS);
                    exif += "\n TAG_BITS_PER_SAMPLE: " + exifInterface.getAttribute(ExifInterface.TAG_BITS_PER_SAMPLE);
                    exif += "\n TAG_COMPRESSED_BITS_PER_PIXEL: " + exifInterface.getAttribute(ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL);
                    exif += "\n TAG_DIGITAL_ZOOM_RATIO: " + exifInterface.getAttribute(ExifInterface.TAG_DIGITAL_ZOOM_RATIO);
                    exif += "\n TAG_FOCAL_PLANE_X_RESOLUTION: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION);
                    exif += "\n TAG_FOCAL_PLANE_Y_RESOLUTION: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION);
                    exif += "\n TAG_PIXEL_X_DIMENSION: " + exifInterface.getAttribute(ExifInterface.TAG_PIXEL_X_DIMENSION);
                    exif += "\n TAG_PIXEL_Y_DIMENSION: " + exifInterface.getAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION);
                    exif += "\n TAG_SAMPLES_PER_PIXEL: " + exifInterface.getAttribute(ExifInterface.TAG_SAMPLES_PER_PIXEL);
                    exif += "\n TAG_RW2_SENSOR_TOP_BORDER: " + exifInterface.getAttribute(ExifInterface.TAG_RW2_SENSOR_TOP_BORDER);
                    exif += "\n TAG_RW2_SENSOR_BOTTOM_BORDER: " + exifInterface.getAttribute(ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER);
                    exif += "\n TAG_RW2_SENSOR_LEFT_BORDER: " + exifInterface.getAttribute(ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER);
                    exif += "\n TAG_RW2_SENSOR_RIGHT_BORDER: " + exifInterface.getAttribute(ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER);
                    exif += "\n TAG_THUMBNAIL_IMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH);
                    exif += "\n TAG_THUMBNAIL_IMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH);
                    exif += "\n TAG_THUMBNAIL_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_THUMBNAIL_ORIENTATION);
                    exif += "\n TAG_ORF_THUMBNAIL_IMAGE: " + exifInterface.getAttribute(ExifInterface.TAG_ORF_THUMBNAIL_IMAGE);
                    exif += "\n STREAM_TYPE_FULL_IMAGE_DATA: " + exifInterface.getAttribute(String.valueOf(ExifInterface.STREAM_TYPE_FULL_IMAGE_DATA));
                    exif += "\nGPS related:";
                    exif += "\n TAG_GPS_DATESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
                    exif += "\n TAG_GPS_TIMESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
                    exif += "\n TAG_GPS_LATITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    exif += "\n TAG_GPS_LATITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                    exif += "\n TAG_GPS_LONGITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    exif += "\n TAG_GPS_LONGITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                    exif += "\n TAG_GPS_PROCESSING_METHOD: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
                    Toast.makeText(getApplicationContext(), exif, Toast.LENGTH_LONG).show();
                    Log.i("Result", exif);
                }else{
                    Log.e("Error", "Error");
                }
            }else if(ext.equals("tiff") || ext.contains("tif")){
                try {
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
                    bmp = TiffBitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFd());
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());

                    if (inputStream != null) {
                        ExifInterface exifInterface = new ExifInterface(inputStream);
                        // Read or write EXIF tags as per your requirement
                        String exif = "--Exif:--" + inputStream;
                        exif += "\n IMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                        exif += "\n IMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                        exif += "\n DATETIME: " + exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                        exif += "\n TAG_MAKE: " + exifInterface.getAttribute(ExifInterface.TAG_MAKE);
                        exif += "\n TAG_MODEL: " + exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                        exif += "\n TAG_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                        exif += "\n TAG_WHITE_BALANCE: " + exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
                        exif += "\n TAG_FOCAL_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                        exif += "\n TAG_FLASH: " + exifInterface.getAttribute(ExifInterface.TAG_FLASH);
                        exif += "\n TAG_STRIP_OFFSETS: " + exifInterface.getAttribute(ExifInterface.TAG_STRIP_OFFSETS);
                        exif += "\n TAG_BITS_PER_SAMPLE: " + exifInterface.getAttribute(ExifInterface.TAG_BITS_PER_SAMPLE);
                        exif += "\n TAG_COMPRESSED_BITS_PER_PIXEL: " + exifInterface.getAttribute(ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL);
                        exif += "\n TAG_DIGITAL_ZOOM_RATIO: " + exifInterface.getAttribute(ExifInterface.TAG_DIGITAL_ZOOM_RATIO);
                        exif += "\n TAG_FOCAL_PLANE_X_RESOLUTION: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION);
                        exif += "\n TAG_FOCAL_PLANE_Y_RESOLUTION: " + exifInterface.getAttribute(ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION);
                        exif += "\n TAG_PIXEL_X_DIMENSION: " + exifInterface.getAttribute(ExifInterface.TAG_PIXEL_X_DIMENSION);
                        exif += "\n TAG_PIXEL_Y_DIMENSION: " + exifInterface.getAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION);
                        exif += "\n TAG_SAMPLES_PER_PIXEL: " + exifInterface.getAttribute(ExifInterface.TAG_SAMPLES_PER_PIXEL);
                        exif += "\n TAG_RW2_SENSOR_TOP_BORDER: " + exifInterface.getAttribute(ExifInterface.TAG_RW2_SENSOR_TOP_BORDER);
                        exif += "\n TAG_RW2_SENSOR_BOTTOM_BORDER: " + exifInterface.getAttribute(ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER);
                        exif += "\n TAG_RW2_SENSOR_LEFT_BORDER: " + exifInterface.getAttribute(ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER);
                        exif += "\n TAG_RW2_SENSOR_RIGHT_BORDER: " + exifInterface.getAttribute(ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER);
                        exif += "\n TAG_THUMBNAIL_IMAGE_LENGTH: " + exifInterface.getAttribute(ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH);
                        exif += "\n TAG_THUMBNAIL_IMAGE_WIDTH: " + exifInterface.getAttribute(ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH);
                        exif += "\n TAG_THUMBNAIL_ORIENTATION: " + exifInterface.getAttribute(ExifInterface.TAG_THUMBNAIL_ORIENTATION);
                        exif += "\n TAG_ORF_THUMBNAIL_IMAGE: " + exifInterface.getAttribute(ExifInterface.TAG_ORF_THUMBNAIL_IMAGE);
                        exif += "\n STREAM_TYPE_FULL_IMAGE_DATA: " + exifInterface.getAttribute(String.valueOf(ExifInterface.STREAM_TYPE_FULL_IMAGE_DATA));
                        exif += "\n --GPS related:--";
                        exif += "\n TAG_GPS_DATESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
                        exif += "\n TAG_GPS_TIMESTAMP: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
                        exif += "\n TAG_GPS_LATITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                        exif += "\n TAG_GPS_LATITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                        exif += "\n TAG_GPS_LONGITUDE: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                        exif += "\n TAG_GPS_LONGITUDE_REF: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                        exif += "\n TAG_GPS_PROCESSING_METHOD: " + exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
                        Toast.makeText(getApplicationContext(), exif, Toast.LENGTH_LONG).show();
                        Log.i("Result", exif);


                    }else{
                        Log.e("Error", "Error");
                    }
                    draw();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getCoordinatesFromExif(InputStream inputStream) {
        try {
            // Create an ExifInterface object for the TIFF image file
            ExifInterface exif = new ExifInterface(inputStream);

            // Get the GPS latitude and longitude using the ExifInterface tag constants
            double latitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_LATITUDE, 0.0);
            double longitude = exif.getAttributeDouble(ExifInterface.TAG_GPS_LONGITUDE, 0.0);

            // Check the latitude reference (North or South) and apply the sign accordingly
            String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            if (latitudeRef != null && latitudeRef.equals("S")) {
                latitude = -latitude;
            }

            // Check the longitude reference (East or West) and apply the sign accordingly
            String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            if (longitudeRef != null && longitudeRef.equals("W")) {
                longitude = -longitude;
            }

            // Do something with the coordinates, for example, display them in a TextView or log them to the console
            Log.d("TAG", "Latitude: " + latitude);
            Log.d("TAG", "Longitude: " + longitude);
        } catch (IOException e) {
            // Handle the error
            e.printStackTrace();
        }
    }
}