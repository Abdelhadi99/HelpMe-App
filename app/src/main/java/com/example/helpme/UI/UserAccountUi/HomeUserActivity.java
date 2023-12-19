package com.example.helpme.UI.UserAccountUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpme.R;
import com.example.helpme.UI.ChooseAccountActivity;
import com.example.helpme.UI.Dao.DaoCase;
import com.example.helpme.UI.Dao.DaoOrder;
import com.example.helpme.UI.Dao.DaoUser;
import com.example.helpme.UI.Model.Case;
import com.example.helpme.UI.Model.Order;
import com.example.helpme.UI.Model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


// there is another Class to implements : , LocationListener // but when you want to start work on Map

public class HomeUserActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private MapView mapView;

    private boolean initialZoomSet = false;


    private ImageView btnOpenNavView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private LatLng selectedLocation;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;

    private ProgressBar progressBar;

    private String latitude;
    private String longitude;

    private EditText edtLocation;

    private Button btnCurrentLocation, btnSearch;

    private DaoUser daoUser;
    private DaoCase daoCase;
    private FirebaseAuth auth;
    private String currentUserID;

    private HashMap<String, Double> locationMap;

    private boolean isAccept = false;
    private boolean isCompleted = false;

    private boolean isThereACase;

    /////////////////////
    // these variables are just for the case when you deal with the helper

    private boolean helperAccept;
    private boolean helperCompleted;
    private static final int CALL_PERMISSION_REQUEST_CODE = 123; // You can use any integer value
    private Dialog searchingDialog;
    private Dialog acceptDialog;
    private Dialog completedDialog;

    private String CASE_KEY;


    ////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);
        initData();


        btnOpenNavView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.profile:
                        startActivity(new Intent(HomeUserActivity.this, ProfileUserActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.logout:

                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeUserActivity.this);
                        builder.setTitle("Confirm Logout !").
                                setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                firebaseAuth.signOut();
                                Toast.makeText(HomeUserActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(HomeUserActivity.this, ChooseAccountActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        //Creating dialog box
                        AlertDialog dialog = builder.create();
                        dialog.show();


                        break;

                    case R.id.orders:
                        startActivity(new Intent(HomeUserActivity.this, OrdersUserActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);


                }

                return true;
            }
        });


        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                googleMap.clear();
                if (checkLocationPermission()) {
                    getCurrentLocation();
                }
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {


            String userKey, userName, carType, carColor;


            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                daoUser.get().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            User user = data.getValue(User.class);
                            if (user.getKey().equals(currentUserID)) {
                                userKey = currentUserID;
                                userName = user.getName();
                                carType = user.getCarType();
                                carColor = user.getCarColor();

                                Case case1 = new Case("case" + currentUserID, userKey, userName, carType, carColor, locationMap, isAccept, isCompleted);
                                daoCase.add(case1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressBar.setVisibility(View.GONE);

                                        searchingDialog.show();


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HomeUserActivity.this, "We can not search now, try again.", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });


                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HomeUserActivity.this, "Failed Search.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        daoCase.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isThereACase = false;

                String helperName = "";
                String helperExpType = "";
                int helperPhoneNum = 0;


                for (DataSnapshot data : snapshot.getChildren()) {
                    boolean isData = data != null;
                    if (isData) {
                        Case case1 = data.getValue(Case.class);

                        if (case1.getCaseKey() != null){
                        if (case1 != null && case1.getCaseKey().equals("case" + currentUserID)) {
                            isThereACase = true;
                            CASE_KEY = case1.getCaseKey();
                            helperAccept = case1.isAccept();
                            helperCompleted = case1.isCompleted();
                            helperName = case1.getHelperName();
                            helperExpType = case1.getHelperExpType();
                            String phone = case1.getHelperPhoneNumber();
                            if (phone != null && !phone.isEmpty()) {
                                helperPhoneNum = Integer.parseInt(case1.getHelperPhoneNumber());
                            }


                            break;  // Exit the loop if a matching case is found
                        }
                    }

                    }else{
                        Toast.makeText(HomeUserActivity.this, "no cases.", Toast.LENGTH_SHORT).show();
                    }
                }

                // Now 'isThereACase' holds whether there is a matching case or not

                // Move your logic here or call another method with the logic
                handleIsThereACase(isThereACase);
                handleIsHelperAccept(helperAccept, helperPhoneNum, helperName, helperExpType);
                handleIfCaseCompleted(helperCompleted, helperName, helperExpType);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    public void initData() {
        acceptDialog = new Dialog(HomeUserActivity.this);
        acceptDialog.setContentView(R.layout.item_home_user_if_accept);

        completedDialog = new Dialog(HomeUserActivity.this);
        completedDialog.setContentView(R.layout.item_home_user_when_completed);
        completedDialog.setCancelable(false);

        searchingDialog = new Dialog(HomeUserActivity.this);
        searchingDialog.setContentView(R.layout.item_looking_for_helper);

        locationMap = new HashMap<>();
        edtLocation = findViewById(R.id.edt_enterLocation_userHome);
        daoCase = new DaoCase();
        daoUser = new DaoUser();
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();

        btnCurrentLocation = findViewById(R.id.btnCurrentUserLocation);
        btnSearch = findViewById(R.id.btnSearchForHelper);

        progressBar = findViewById(R.id.progressBarHomeUser);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        btnOpenNavView = findViewById(R.id.btnOpenMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Set your preferred map settings if needed
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

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
            locationMap.put("latitude", latLng.latitude);
            locationMap.put("longitude", latLng.longitude);

            edtLocation.setText("Latitude: " + latLng.latitude + "\nLongitude: " + latLng.longitude);
        });


    }

//    public void onSelectLocationClick(View view) {
//        if (selectedLocation != null) {
//            double latitude = selectedLocation.latitude;
//            double longitude = selectedLocation.longitude;
//
//            // Save latitude and longitude to your data structure or database
//            edtLocation.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
//        } else {
//            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
//        }
//    }


    //to check if the GPS is On or not in the user device
    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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

        Task<Location> locationResult = fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null);
        locationResult.addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                edtLocation.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                LatLng currentLocation = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                selectedLocation = currentLocation;
                locationMap.put("latitude", latitude);
                locationMap.put("longitude", longitude);
                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    // Handle the result of the permission request
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

    // ...


    @Override
    public void onBackPressed() {
        if (drawerLayout.isOpen()) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            return;
        }
    }

    // Add a method to handle the logic of is there a case or not
    private void handleIsThereACase(boolean isThereACase) {
        if (!isFinishing()) { // Check if the activity is not finishing
            if (isThereACase) {
                searchingDialog.show();
            } else {
                searchingDialog.dismiss();
            }
        }
    }


    // handle the dialog that show up when the case is completed
    private void handleIfCaseCompleted(boolean isHelperCompleted, String helperName, String helperExpType) {
        if (!isFinishing()) { // Check if the activity is not finishing

            if (isHelperCompleted) {
                acceptDialog.dismiss();
                completedDialog.show();


                TextView txtHelperName = completedDialog.findViewById(R.id.txtHelperNameWhenCompleted);
                txtHelperName.setText(helperName);

                TextView txtHelperExpType = completedDialog.findViewById(R.id.txtHelperExpTypeWhenCompleted);
                txtHelperExpType.setText(helperExpType);


                TextView btnDone = completedDialog.findViewById(R.id.btnDoneOnUserHomeWhenCompleted);
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        RatingBar ratingBar = completedDialog.findViewById(R.id.ratingBarWhenCompleted);
                        float rating = ratingBar.getRating();

                        Order order = new Order(helperName, helperExpType, rating);
                        DaoOrder daoOrder = new DaoOrder();

                        daoOrder.add(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                daoCase.remove(CASE_KEY).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(HomeUserActivity.this, "I hope you're well and everything is fine.", Toast.LENGTH_LONG).show();
                                        completedDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HomeUserActivity.this, "try to Done again", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeUserActivity.this, "try to Done again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                });

            }
        }
    }


    // handle the dialog that show up when the helper accept the case
    private void handleIsHelperAccept(boolean isHelperAccept, int phoneNumber, String helperName, String helperExpType) {
        if (!isFinishing()) { // Check if the activity is not finishing

            if (isHelperAccept) {
                searchingDialog.dismiss();
                acceptDialog.show();

                TextView txtHelperName = acceptDialog.findViewById(R.id.txtNameOfHelperOnUserHome);
                txtHelperName.setText(helperName);

                TextView txtHelperCarType = acceptDialog.findViewById(R.id.txtExpTypeOnUserHome);
                txtHelperCarType.setText(helperExpType);

                ImageView btnCancel = acceptDialog.findViewById(R.id.btnCancelDialogAccepted);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        daoCase.remove("case" + currentUserID).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                acceptDialog.dismiss();

                                Toast.makeText(HomeUserActivity.this, "Case Canceled and Deleted", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });

                ImageView btnCall = acceptDialog.findViewById(R.id.btnCallTheHelper);
                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));

                        // Check for CALL_PHONE permission before making the call
                        if (ActivityCompat.checkSelfPermission(HomeUserActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(dialIntent);
                        } else {
                            // Request the permission if not granted
                            ActivityCompat.requestPermissions(HomeUserActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
                        }
                    }
                });

            }
        }
    }

}