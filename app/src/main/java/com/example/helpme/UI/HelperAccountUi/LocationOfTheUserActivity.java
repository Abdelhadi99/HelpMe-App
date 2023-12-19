package com.example.helpme.UI.HelperAccountUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.helpme.R;
import com.example.helpme.UI.Dao.DaoCase;
import com.example.helpme.UI.Model.Case;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LocationOfTheUserActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private LocationManager locationManager;

    private HashMap<String , Double> userLocation = new HashMap<>();

    double latitude;
    double longitude;

    ProgressBar progressBar;
    DaoCase daoCase;
    FirebaseAuth auth;

    String currentHelperId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_of_the_user);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        auth = FirebaseAuth.getInstance();
        currentHelperId = auth.getUid();

        daoCase = new DaoCase();
        progressBar =  findViewById(R.id.progressBarUserLocationOnHelper);
        progressBar.setVisibility(View.VISIBLE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // You can customize and work with the GoogleMap instance here.
        // Add markers, set initial position, etc

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);


        daoCase.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()){
                    Case case1 = data.getValue(Case.class);
                    if (case1 != null){

                        if (case1.getHelperKey() != null) {
                            if (case1.getHelperKey().equals(currentHelperId)) {

                                if (case1.getUserLocation() != null) {
                                    latitude = case1.getUserLocation().get("latitude");
                                    longitude = case1.getUserLocation().get("longitude");
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(LocationOfTheUserActivity.this, "No location", Toast.LENGTH_SHORT).show();
                                }


                                googleMap.clear();
                                LatLng latLng = new LatLng(latitude, longitude);
                                googleMap.addMarker(new MarkerOptions().position(latLng).title("User Location").draggable(false));
                            }
                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }



    public void back(View view) {
        onBackPressed();
    }
}