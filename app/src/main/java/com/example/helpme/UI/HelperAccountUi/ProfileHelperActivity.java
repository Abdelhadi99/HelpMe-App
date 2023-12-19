package com.example.helpme.UI.HelperAccountUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpme.R;
import com.example.helpme.UI.Dao.DaoHelper;
import com.example.helpme.UI.Dao.DaoUser;
import com.example.helpme.UI.Model.Helper;
import com.example.helpme.UI.Model.User;
import com.example.helpme.UI.UserAccountUi.ProfileUserActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileHelperActivity extends AppCompatActivity {

    private EditText edtLocation, edtName, edtEmail, edtExperienceType, edtExperienceLevel, edtPhoneNumber;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;

    private ProgressBar progressBar;
    private HashMap<String, Double> locationMap;


    private String latitude;
    private String longitude;

    private TextView btnEditProfileHelper;

    private DaoHelper daoHelper;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private String currentUserID;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_helper);
        initData();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        currentUserID = firebaseUser.getUid();
        daoHelper = new DaoHelper();

        setUserData();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);





        edtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(ProfileHelperActivity.this);
                dialog.setContentView(R.layout.item_select_helper_location);
                dialog.show();

                TextView btnCurrentLocation = dialog.findViewById(R.id.btnCurrentLocation);
                TextView btnSelectLocation = dialog.findViewById(R.id.btnSelectLocation);

                btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        progressBar.setVisibility(View.VISIBLE);

                        if (checkLocationPermission()) {
                            getCurrentLocation();
                        }
                    }
                });

                btnSelectLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Intent intent1 = new Intent(ProfileHelperActivity.this, ChoseYourLocationHelperActivity.class);
                        intent1.putExtra("fromProfile", "profile");
                        startActivity(intent1);
                    }
                });

            }
        });

//        if (!latitude.isEmpty() && !longitude.isEmpty())

        btnEditProfileHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String expType = edtExperienceType.getText().toString();
                String expLevel = edtExperienceLevel.getText().toString();
                String phoneNumber = edtPhoneNumber.getText().toString();

                Helper helper = new Helper(currentUserID, name, email, expType, expLevel, phoneNumber, locationMap);
                daoHelper.add(helper).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileHelperActivity.this, "Edited successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileHelperActivity.this, "Failed edit", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


    public void initData() {
        locationMap = new HashMap<>();

        edtName = findViewById(R.id.edt_name_profile_helper);
        edtEmail = findViewById(R.id.edt_email_profile_helper);

        edtExperienceLevel = findViewById(R.id.edt_levelOfExperience_profile_helper);
        edtExperienceType = findViewById(R.id.edt_typeExperience_profile_helper);
        edtPhoneNumber = findViewById(R.id.edt_phoneNumber_profile_helper);
        edtLocation = findViewById(R.id.edt_location_profile_helper);

        btnEditProfileHelper = findViewById(R.id.btnEditProfileHelper);
        edtLocation = findViewById(R.id.edt_location_profile_helper);
        progressBar = findViewById(R.id.progressBarHelperProfile);
    }


    private void setUserData() {
        daoHelper.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    helper = dataSnapshot.getValue(Helper.class);

                    if (helper.getKey().equals(currentUserID)) {

                        edtName.setText(helper.getName());
                        edtEmail.setText(helper.getEmail());
                        edtExperienceType.setText(helper.getExperienceType());
                        edtExperienceLevel.setText(helper.getExperienceLevel());
                        edtPhoneNumber.setText(helper.getPhoneNumber());

                        locationMap = helper.getLocationMap();


                        // to get the location from ChoseLocation activity

                        Intent intent = getIntent();
                        if (intent.hasExtra("latitudeP")) {
                            latitude = intent.getStringExtra("latitudeP");
                            longitude = intent.getStringExtra("longitude");

                            locationMap.put("latitude", Double.valueOf(latitude));
                            locationMap.put("longitude", Double.valueOf(longitude));

                            edtLocation.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                        } else {
                            latitude = String.valueOf(locationMap.get("latitude"));
                            longitude = String.valueOf(locationMap.get("longitude"));

                            edtLocation.setText("Latitude: " + latitude + "\nLongitude: " + longitude);

                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());

                locationMap.put("latitude",location.getLatitude());
                locationMap.put("longitude",location.getLongitude());

                edtLocation.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
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


    //Press on any place in screen to cancel the keyboard
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {

                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }


}