package com.example.helpme.UI.HelperAccountUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.helpme.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class ChoseYourLocationHelperActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private Button buttonSelectLocation,buttonCurrentLocation;
    private LatLng selectedLocation;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;

    private ProgressBar progressBar;

    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_your_location_helper);

        progressBar = findViewById(R.id.progressBarHelperLocation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        mapView = findViewById(R.id.mapViewSelectHelperLocation);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        buttonSelectLocation = findViewById(R.id.btnSelectHelperLocation);
        buttonCurrentLocation = findViewById(R.id.btnCurrentHelperLocation);

        buttonCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                googleMap.clear();
                if (checkLocationPermission()) {
                    getCurrentLocation();
                }
            }
        });

        buttonSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectLocationClick(view);

                Intent intent1 = getIntent();
                if (intent1.hasExtra("fromProfile")){

                    if (selectedLocation != null) {
                        latitude = String.valueOf(selectedLocation.latitude);
                        longitude = String.valueOf(selectedLocation.longitude);
                        Intent intent = new Intent(ChoseYourLocationHelperActivity.this,ProfileHelperActivity.class);
                        intent.putExtra("latitudeP",latitude);
                        intent.putExtra("longitude",longitude);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ChoseYourLocationHelperActivity.this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    if (selectedLocation != null) {
                        latitude = String.valueOf(selectedLocation.latitude);
                        longitude = String.valueOf(selectedLocation.longitude);
                        Intent intent = new Intent(ChoseYourLocationHelperActivity.this,SignupHelperActivity.class);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ChoseYourLocationHelperActivity.this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
                    }
                }





            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Set your preferred map settings if needed
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);

        // Add a default marker or move the camera to a default location
        LatLng defaultLocation = new LatLng(0, 0);
        googleMap.addMarker(new MarkerOptions().position(defaultLocation).title("Default Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));

        // Set a marker click listener if needed
        googleMap.setOnMarkerClickListener(marker -> {
            // Handle marker click
            return false;
        });

        // Set a map click listener
        googleMap.setOnMapClickListener(latLng -> {
            // Handle map click
            googleMap.clear(); // Clear existing markers
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedLocation = latLng;

        });
    }

    public void onSelectLocationClick(View view) {
        if (selectedLocation != null) {
             latitude = String.valueOf(selectedLocation.latitude);
             longitude = String.valueOf(selectedLocation.longitude);

            // Save latitude and longitude to your data structure or database
            Toast.makeText(this, "Selected Location: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }



    private void getCurrentLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        if (!isGpsEnabled()) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        Task<Location> locationResult = fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,null);
        locationResult.addOnSuccessListener(this, location -> {
            if (location != null) {
                 latitude = String.valueOf(location.getLatitude());
                 longitude = String.valueOf(location.getLongitude());
//                edtLocation.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                LatLng currentLocation = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                selectedLocation = currentLocation;
                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    //to check if the GPS is On or not in the user device
    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    }






    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}