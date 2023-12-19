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
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpme.R;
import com.example.helpme.UI.Dao.DaoHelper;
import com.example.helpme.UI.Model.Helper;
import com.example.helpme.UI.Model.User;
import com.example.helpme.UI.UserAccountUi.LoginUserActivity;
import com.example.helpme.UI.UserAccountUi.SignupUserActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;

public class SignupHelperActivity extends AppCompatActivity {

    private TextView btnSignupHelper;
    private EditText edtLocation,edtName,edtEmail,edtPassword,edtExperienceType,edtExperienceLevel,edtPhoneNumber;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private DaoHelper daoHelper;

    private HashMap<String,Double> locationMap;

    private String latitude;
    private String longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_helper);
       initData();
       auth = FirebaseAuth.getInstance();
       daoHelper = new DaoHelper();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // to get the location from ChoseLocation activity

        Intent intent = getIntent();
        if (intent.hasExtra("latitude")){
             latitude = intent.getStringExtra("latitude");
             longitude = intent.getStringExtra("longitude");
            edtLocation.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
        }else{
            edtLocation.setText("");
        }


        edtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(SignupHelperActivity.this);
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
                        startActivity(new Intent(SignupHelperActivity.this,ChoseYourLocationHelperActivity.class));
                        finish();
                    }
                });






            }
        });


        btnSignupHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start to upload data to firebase



                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                String expType = edtExperienceType.getText().toString();
                String expLevel = edtExperienceLevel.getText().toString();
                String phoneNumber = edtPhoneNumber.getText().toString();


                if (edtNotEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);

                    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            // here i update the Helper info and send an email to verify his email and when
                            // the email is done i will upload his info to the firebase database

                            FirebaseUser user = auth.getCurrentUser();
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(userProfileChangeRequest);

                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Helper helper = new Helper(user.getUid(), name, email, password, expType, expLevel, phoneNumber,locationMap);

                                    daoHelper.add(helper).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(SignupHelperActivity.this, "SignedUp Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupHelperActivity.this, LoginHelperActivity.class);
                                            intent.putExtra("sign", "new");
                                            startActivity(intent);
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupHelperActivity.this, "Email was not sent " + e, Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupHelperActivity.this, "Failed to signup " + e, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });


                }
            }
        });




        //end of onCreate
    }

    public void initData(){
        locationMap = new HashMap<>();

        edtName = findViewById(R.id.edt_name_signup_helper);
        edtEmail = findViewById(R.id.edt_email_signup_helper);
        edtPassword = findViewById(R.id.edt_password_signup_helper);
        edtExperienceLevel = findViewById(R.id.edt_levelOfExperience_signup_helper);
        edtExperienceType = findViewById(R.id.edt_typeExperience_signup_helper);
        edtPhoneNumber = findViewById(R.id.edt_phoneNumber_signup_helper);
        btnSignupHelper = findViewById(R.id.btnSignupHelper);
        edtLocation = findViewById(R.id.edt_location_signup_helper);
        progressBar = findViewById(R.id.progressBarHelper);
    }


    private boolean edtNotEmpty() {


        if (!edtName.getText().toString().isEmpty()) {

            if (!edtEmail.getText().toString().isEmpty()) {

                if (Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {

                    if (!edtPassword.getText().toString().isEmpty()) {

                        if (edtPassword.getText().toString().length() >= 6) {

                            if (!edtExperienceType.getText().toString().isEmpty()) {

                                if (!edtExperienceLevel.getText().toString().isEmpty()) {

                                    if (!edtLocation.getText().toString().isEmpty()) {

                                        locationMap.put("latitude", Double.valueOf(latitude));
                                        locationMap.put("longitude", Double.valueOf(longitude));

                                        if (!edtPhoneNumber.getText().toString().isEmpty()) {

                                            return true;

                                        } else {
                                            Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                                            return false;
                                        }

                                    } else {
                                        Toast.makeText(this, "Enter your Location", Toast.LENGTH_SHORT).show();
                                        return false;
                                    }


                                } else {
                                    Toast.makeText(this, "Enter your Experience level", Toast.LENGTH_SHORT).show();
                                    return false;
                                }


                            } else {
                                Toast.makeText(this, "Enter your Experience Type", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                        } else {
                            Toast.makeText(this, "Password should be at least 6 numbers", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                    } else {
                        Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }else{
                    Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }
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

                edtLocation.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
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



}