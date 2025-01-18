package com.android.example.bingoroadtripfinland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import android.Manifest;
import android.widget.Toast;

public class TaskDetailsActivity extends AppCompatActivity {

    private MapView mMapView = null;
    Location mLoc = null;
    LocationManager locationManager;

    private JsonReader jsonReader;

    private final int REQUEST_PERMISSION_REQUEST_CODE = 101;

    /**
     * Activity for showing task information including name, instructions and description, address
     * and location on map. Teams can also set task as completed from here.
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        jsonReader = new JsonReader(TaskDetailsActivity.this);
        getGuiElements();
    }

    /**
     * Sets gui elements to screen
     */
    private void getGuiElements() {
        // buttons for returning to previous and marking task as completed
        Button returnTaskBtn = findViewById(R.id.returnTaskBtn);
        Button setCompletedBtn = findViewById(R.id.setCompletedBtn);

        // TextViews for information
        TextView taskNameTxtvw = findViewById(R.id.taskNameTxtvw);
        TextView taskInfoTxtvw = findViewById(R.id.taskInfoTxtVw);
        TextView taskAddressTxtvw = findViewById(R.id.taskAddressTxtVw);

        // Button for returning to grid view
        returnTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Button for marking the task as completed
        setCompletedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent markCompletedIntent = new Intent();
                markCompletedIntent.putExtra("taskName", taskNameTxtvw.getText().toString());
                markCompletedIntent.putExtra("completed", true);
                setResult(Activity.RESULT_OK, markCompletedIntent);
                finish();
            }
        });

        Intent intent = getIntent();
        String taskName = intent.getStringExtra("name");
        ArrayList<Task> allTasks = jsonReader.getAllTasks();

        for (Task t : allTasks) {
            if (t.getName().equals(taskName)) {
                taskInfoTxtvw.setText(t.getDescription());
                // Some tasks don't have addresses
                if (t.getAddress() != null) {
                    taskAddressTxtvw.setText(t.getAddress());
                }
                // Some tasks don't have specific locations
                if (t.getGpsLocation() != null) {
                    float lat = (float) t.getGpsLocation().getLatitude();
                    float lon = (float) t.getGpsLocation().getLongitude();
                    GeoPoint loc = new GeoPoint(lat, lon);
                    setMapView(loc);
                }
            }
        }
        taskNameTxtvw.setText(taskName);
    }

    /**
     * Sets up map view with task location if applicable and users current location
     * @param taskLocation GeoPoint of the tasks location
     */
    private void setMapView(GeoPoint taskLocation) {
        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // MapView
        mMapView = findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        MapController mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(15);

        // Request necessary permissions
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });

        showOwnLocation();

        if (taskLocation != null) {
            String str1 = "" + taskLocation.getLatitude();
            String str2 = "" + taskLocation.getLongitude();
            ArrayList<OverlayItem> taskMarkers = new ArrayList<>();
            taskMarkers.add(new OverlayItem(str1, str2, taskLocation));

            ItemizedIconOverlay<OverlayItem> mMarkerItemizedIconOverlay = new ItemizedIconOverlay<>(
                    TaskDetailsActivity.this, taskMarkers, null);
            mMapView.getOverlays().add(mMarkerItemizedIconOverlay);

            mMapView.getController().setCenter(taskLocation);
        }
    }

    /**
     * Sets users current location to same mapView as tasks location
     */
    private void showOwnLocation() {
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(
                TaskDetailsActivity.this), mMapView);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(mLocationOverlay);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Puhelimen sijaintia ei voida määrittää," +
                    " koska GPS on pois päältä", Toast.LENGTH_SHORT).show();
        }

        assignLocationListener();
        mMapView.getController().setCenter(mLocationOverlay.getMyLocation());
    }

    /**
     * Listens for location changes that are caused by users moving around
     */
    private void assignLocationListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Listen for GPS changes
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location loc) {
                mLoc = loc;
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(TaskDetailsActivity.this, "Sijainnin käyttölupa evätty",
                    Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000, 10, locationListener);
    }

    /**
     * Checks and requests permissions if they are denied
     * @param permissions List of required permission
     */
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
                    REQUEST_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Listens for results from asking permissions
     */
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
                    REQUEST_PERMISSION_REQUEST_CODE);
        }
    }
}