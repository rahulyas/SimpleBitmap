package com.example.simplebitmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OSMActivity extends AppCompatActivity {
    private MapView map = null;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    ArrayList<Double> Latitude = new ArrayList<Double>(Arrays.asList(28.619846805765697, 28.616662582003524, 28.62405736412823, 28.616975106635802));
    ArrayList<Double> Longitude = new ArrayList<Double>(Arrays.asList(77.3808249441672, 77.40679880836608, 77.3377475561817, 77.3467168634806));
    private Marker previousMarker;
    private Polyline line;
    double selectlatitude=0.0;
    double selectlongitude=0.0;
    TextView textView;
    Button liveLocationButton;
    List<IGeoPoint> points = new ArrayList<>();
    List<GeoPoint> trianglePoints = new ArrayList<>(3);
    List<GeoPoint> rectanglePoints = new ArrayList<>(4);
    Button drawrectangleButton;
    Button drawTriangleButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osmactivity);
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET
        });
        liveLocationButton = findViewById(R.id.live_location_button);
        textView = findViewById(R.id.textView);
        drawrectangleButton = findViewById(R.id.drawRectangleButton);
        drawTriangleButton = findViewById(R.id.drawTriangleButton);

        map = findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        map.getController().setZoom(15.0);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        // lat long Grid overlay
//        LatLonGridlineOverlay2 overlay2 = new LatLonGridlineOverlay2();
//        map.getOverlays().add(overlay2);

        //Scalebar
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBar = new ScaleBarOverlay(map);
        mScaleBar.setCentred(true);
        //play around with these values to get the Location on Screen in the right placefor your app
        mScaleBar.setScaleBarOffset(metrics.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBar);

        map.setOnTouchListener(new View.OnTouchListener() {
            private List<GeoPoint> polylinePoints = new ArrayList<>();
            private Polyline polyline;
            private GestureDetector gestureDetector = new GestureDetector(OSMActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    // Get the coordinates of the double tap event
                    GeoPoint tappedPoint = (GeoPoint) map.getProjection().fromPixels((int) e.getX(), (int) e.getY());
                    rectanglePoints.add(tappedPoint);
                    trianglePoints.add(tappedPoint);
                    // Create a marker at the tapped coordinates
                    Marker marker = new Marker(map);
                    marker.setPosition(tappedPoint);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle(tappedPoint.toString());
                    map.getOverlays().add(marker);

                    // Refresh the map to update the display
                    map.invalidate();
                    return true;
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    // Clear existing polyline
                    if (polyline != null) {
                        map.getOverlayManager().remove(polyline);
                    }

                    // Clear previous polyline points
                    polylinePoints.clear();
                    return true;
                }

/*                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    // Get the current scroll position
                    GeoPoint currentPoint = (GeoPoint) map.getProjection().fromPixels((int) e2.getX(), (int) e2.getY());

                    // Add the current point to the polyline points list
                    polylinePoints.add(currentPoint);

                    // Create a new polyline with the updated points
                    polyline = new Polyline();
                    polyline.setPoints(polylinePoints);
                    polyline.setColor(Color.BLUE);
                    polyline.setWidth(5.0f);

                    // Add the polyline to the map's overlay manager
                    map.getOverlayManager().add(polyline);

                    // Refresh the map to update the display
                    map.invalidate();
                    return false;
                }*/
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        liveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                livelocation();
            }
        });

        drawrectangleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDrawRectangleButtonClick();
            }
        });

        drawTriangleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDrawTriangleButtonClick();
            }
        });

        for(int i =0 ; i< Latitude.size() ; i++) {
            points.add(new LabelledGeoPoint(Latitude.get(i), Longitude.get(i)
                    , "Point #" + i));
        }

        Log.d("points", ""+points);

        for(int j=0; j < points.size(); j++) {
            Marker startMarker = new Marker(map);
            startMarker.setPosition((GeoPoint) points.get(j));
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            startMarker.setIcon(getResources().getDrawable(R.drawable.ic_baseline_add_24));
            startMarker.setTitle(""+j);
            startMarker.showInfoWindow();
            startMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    GeoPoint point = marker.getPosition();
                    selectlatitude = point.getLatitude();
                    selectlongitude = point.getLongitude();
                    drawLinOnOSM();
                    Toast.makeText(OSMActivity.this, "Clicked point: " + selectlatitude + ", " + selectlongitude, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            map.getOverlays().add(startMarker);
            map.getController().setCenter(points.get(j));
        }

/*    // Draw a line between the Two point
    // Create two GeoPoints for the start and end points
        GeoPoint startPoint = new GeoPoint(40.748817, -73.985428); // New York City
        GeoPoint endPoint = new GeoPoint(37.7749, -122.4194); // San Francisco

    // Create a list of GeoPoints to hold the start and end points
        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(startPoint);
        geoPoints.add(endPoint);

    // Create a Polyline object and set its points
        Polyline line = new Polyline();
        line.setPoints(geoPoints);

    // Add the Polyline to the MapView
        map.getOverlayManager().add(line);*/

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void livelocation(){
        MapController mapController = (MapController) map.getController();
        mapController.setZoom(30);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(OSMActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OSMActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mapController.animateTo(new GeoPoint(location.getLatitude(), location.getLongitude()));
        Drawable markerIcon = ContextCompat.getDrawable(OSMActivity.this, R.drawable.marker_icon);
        Marker currentMarker = new Marker(map);
        currentMarker.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
        currentMarker.setIcon(markerIcon);
        currentMarker.setTitle("Current Location");
        map.getOverlays().add(currentMarker);

    }

    private void drawLinOnOSM() {
        if (previousMarker != null && line != null) {
            map.getOverlays().remove(previousMarker);
            map.getOverlayManager().remove(line);
        }
        Location liveLocation = new Location("");
        liveLocation.setLatitude(28.619846805765697); // we can replace the current location with the new location
        liveLocation.setLongitude(77.3808249441672);
        Location selectedLocation = new Location("");
        selectedLocation.setLatitude(selectlatitude);
        selectedLocation.setLongitude(selectlongitude);
        float distance = liveLocation.distanceTo(selectedLocation); // Calculate the distance in meters

        float bearing = liveLocation.bearingTo(selectedLocation);
        String direction;
        if (bearing >= 315 && bearing < 45) {
            direction = "North";
        } else if (bearing >= 45 && bearing < 135) {
            direction = "East";
        } else if (bearing >= 135 && bearing < 225) {
            direction = "South";
        } else {
            direction = "West";
        }

        // Determine the relative direction
        String direction2;
        if (bearing >= -45 && bearing < 45) {
            direction2 = "right";
        } else if (bearing >= 45 && bearing < 135) {
            direction2 = "top";
        } else if (bearing >= 135 && bearing < -135) {
            direction2 = "left";
        } else {
            direction2 = "bottom";
        }
        // Print the direction and distance
        Toast.makeText(getApplicationContext(),"direction= "+bearing+"="+direction+"="+direction2+"\n"+"Distance= "+distance, Toast.LENGTH_SHORT).show();
        previousMarker = new Marker(map);
        previousMarker.setPosition(new GeoPoint(selectlatitude, selectlongitude));
        previousMarker.setTitle(String.valueOf(new GeoPoint(selectlatitude, selectlongitude)));
        map.getOverlays().add(previousMarker);

        // In this case we draw line between live locations to select locations
        line = new Polyline();
        line.setColor(Color.RED);
        line.setWidth(5f);
        line.setPoints(Arrays.asList(
                new GeoPoint(liveLocation.getLatitude(), liveLocation.getLongitude()),
                new GeoPoint(selectlatitude, selectlongitude)
        ));
        map.getOverlayManager().add(line);
    }

    public void onDrawRectangleButtonClick() {
        // Draw the Rectangle when four points are selected
        if (rectanglePoints.size() == 4) {
            drawRectangle();
            Toast.makeText(this, "RectangleClicked", Toast.LENGTH_SHORT).show();
        } else if(rectanglePoints.size() > 4){
            rectanglePoints.clear();
        }
        else{
            Toast.makeText(this, "Check the list size=="+rectanglePoints.size(), Toast.LENGTH_SHORT).show();
        }
    }

    private void drawRectangle() {
        Polygon polygon = new Polygon(map);
//        polygon.getFillPaint().setColor(Color.argb(50, 255, 0, 0)); // Set fill color
        polygon.setFillColor(Color.argb(100, 0, 0, 255));
        polygon.setStrokeColor(Color.BLUE);
        polygon.setStrokeWidth(1.0f);

        // Add triangle points to the polygon
        for (GeoPoint point : rectanglePoints) {
            polygon.addPoint(point);
        }

        // Calculate the lengths of the sides
        double sideA = rectanglePoints.get(0).distanceToAsDouble(rectanglePoints.get(1));
        double sideB = rectanglePoints.get(1).distanceToAsDouble(rectanglePoints.get(2));
        double sideC = rectanglePoints.get(2).distanceToAsDouble(rectanglePoints.get(3));
        double sideD = rectanglePoints.get(3).distanceToAsDouble(rectanglePoints.get(0));

        // Calculate the area
        double area = sideA * sideB;  // base * height

        // Calculate the perimeter
        double perimeter = sideA + sideB + sideC + sideD;

        // Print the calculated area and perimeter
        System.out.println("Area: " + area);
        System.out.println("Perimeter: " + perimeter);

        // Add polygon overlay to the MapView
        map.getOverlays().add(polygon);
        rectanglePoints.clear();
        // Refresh the MapView to update the display
        map.invalidate();
    }

    public void onDrawTriangleButtonClick() {
        // Draw the triangle when three points are selected
        if (trianglePoints.size() == 3) {
            drawTriangle();
            Toast.makeText(this, "TriangleClicked", Toast.LENGTH_SHORT).show();
        } else if(trianglePoints.size() > 3)
        {
            trianglePoints.clear();
        }
        else {
            Toast.makeText(this, "Check the list size==" + trianglePoints.size(), Toast.LENGTH_SHORT).show();
        }

    }

    private void drawTriangle() {
        Polygon polygon = new Polygon(map);
//        polygon.getF41
//        \][opit5e432.
//        illPaint().setColor(Color.argb(100, 255, 0, 0)); // Set fill color
        polygon.setFillColor(Color.argb(100, 255, 0, 0));
        polygon.setStrokeColor(Color.RED);
        polygon.setStrokeWidth(1.0f);
        // Add triangle points to the polygon
        for (GeoPoint point : trianglePoints) {
            polygon.addPoint(point);
        }

        // Calculate the lengths of the sides
        double sideA = trianglePoints.get(0).distanceToAsDouble(trianglePoints.get(1));
        double sideB = trianglePoints.get(1).distanceToAsDouble(trianglePoints.get(2));
        double sideC = trianglePoints.get(2).distanceToAsDouble(trianglePoints.get(0));

        // Calculate the semi-perimeter
        double semiPerimeter = (sideA + sideB + sideC) / 2.0;

        // Calculate the area using Heron's formula
        double area = Math.sqrt(semiPerimeter * (semiPerimeter - sideA) * (semiPerimeter - sideB) * (semiPerimeter - sideC));

        // Calculate the perimeter
        double perimeter = sideA + sideB + sideC;

        // Print the calculated area and perimeter
        Log.d("Triangle", "Area: " + area);
        Log.d("Triangle", "Perimeter: " + perimeter);
        Toast.makeText(this, "Area:="+area+"\n"+"Perimeter:="+perimeter, Toast.LENGTH_SHORT).show();
        // Add polygon overlay to the MapView
        map.getOverlays().add(polygon);
        trianglePoints.clear();
        // Refresh the MapView to update the display
        map.invalidate();
    }



}