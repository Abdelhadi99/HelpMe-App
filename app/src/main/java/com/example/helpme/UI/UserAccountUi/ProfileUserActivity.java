package com.example.helpme.UI.UserAccountUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpme.R;
import com.example.helpme.UI.ChooseAccountActivity;
import com.example.helpme.UI.Dao.DaoUser;
import com.example.helpme.UI.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileUserActivity extends AppCompatActivity {


    private ImageView btnOpenNavView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView btnEditProfile;

    private EditText edtName, edtEmail, edtCarType, edtCarColor, edtCarModel, edtPhoneNumber;

    private DaoUser daoUser;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private String currentUserID;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        initData();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        currentUserID = firebaseUser.getUid();
        daoUser = new DaoUser();

        setUserData();


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
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.logout:

                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUserActivity.this);
                        builder.setTitle("Confirm Logout !").
                                setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                firebaseAuth.signOut();
                                Toast.makeText(ProfileUserActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ProfileUserActivity.this, ChooseAccountActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        //Creating dialog box
                        AlertDialog dialog  = builder.create();
                        dialog.show();


                        break;

                    case R.id.orders:
                        startActivity(new Intent(ProfileUserActivity.this, OrdersUserActivity.class));


                }

                return true;
            }
        });

        // Edit Button
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String carType = edtCarType.getText().toString();
                String carColor = edtCarColor.getText().toString();
                String carModel = edtCarModel.getText().toString();
                String phoneNumber = edtPhoneNumber.getText().toString();

                User user = new User(currentUserID,name,email,carType,carColor,carModel,phoneNumber);

                daoUser.add(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileUserActivity.this, "Edited successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileUserActivity.this, "Failed edit", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void initData() {
        btnEditProfile = findViewById(R.id.btnEditProfileUser);

        btnOpenNavView = findViewById(R.id.btnOpenMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        edtName = findViewById(R.id.edt_name_profile_user);
        edtEmail = findViewById(R.id.edt_email_profile_user);
        edtCarType = findViewById(R.id.edt_carType_profile_user);
        edtCarColor = findViewById(R.id.edt_carColor_profile_user);
        edtCarModel = findViewById(R.id.edt_carModel_profile_user);
        edtPhoneNumber = findViewById(R.id.edt_phoneNumber_profile_user);

    }

    private void setUserData() {
        daoUser.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    user = dataSnapshot.getValue(User.class);

                    if (user.getKey().equals(currentUserID)) {

                        edtName.setText(user.getName());
                        edtEmail.setText(user.getEmail());
                        edtCarType.setText(user.getCarType());
                        edtCarColor.setText(user.getCarColor());
                        edtCarModel.setText(user.getCarModel());
                        edtPhoneNumber.setText(user.getPhoneNumber());


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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