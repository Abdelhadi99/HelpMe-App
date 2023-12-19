package com.example.helpme.UI.UserAccountUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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
import com.example.helpme.UI.Dao.DaoUser;
import com.example.helpme.UI.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupUserActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtCarType, edtCarColor, edtCarModel, edtPhoneNumber;

    private TextView btnSignupUser;

    private FirebaseAuth auth;
    private DaoUser daoUser;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);
        initData();
        auth = FirebaseAuth.getInstance();
        daoUser = new DaoUser();

        btnSignupUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                String carType = edtCarType.getText().toString();
                String carColor = edtCarColor.getText().toString();
                String carModel = edtCarModel.getText().toString();
                String phoneNumber = edtPhoneNumber.getText().toString();

                if (edtNotEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);

                    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            // here i update the useer info and send an email to verfy his email and when
                            // the email is done i will upload his info to the firebase database

                            FirebaseUser user = auth.getCurrentUser();
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(userProfileChangeRequest);

                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    User user1 = new User(user.getUid(), name, email, password, carType, carColor, carModel, phoneNumber);

                                    daoUser.add(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(SignupUserActivity.this, "SignedUp Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupUserActivity.this, LoginUserActivity.class);
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
                                    Toast.makeText(SignupUserActivity.this, "Email was not sent " + e, Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupUserActivity.this, "Failed to signup " + e, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });


                }
            }
        });
    }


    private void initData() {
        btnSignupUser = findViewById(R.id.btnSignupUser);
        edtName = findViewById(R.id.edt_name_signup_user);
        edtEmail = findViewById(R.id.edt_email_signup_user);
        edtPassword = findViewById(R.id.edt_password_signup_user);
        edtCarType = findViewById(R.id.edt_carType_signup_user);
        edtCarColor = findViewById(R.id.edt_carColor_signup_user);
        edtCarModel = findViewById(R.id.edt_carModel_signup_user);
        edtPhoneNumber = findViewById(R.id.edt_phoneNumber_signup_user);
        progressBar = findViewById(R.id.progressBarUserSignup);

    }

    private boolean edtNotEmpty() {


        if (!edtName.getText().toString().isEmpty()) {

            if (!edtEmail.getText().toString().isEmpty()) {

                if (Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()) {

                    if (!edtPassword.getText().toString().isEmpty()) {

                        if (edtPassword.getText().toString().length() >= 6) {

                            if (!edtCarType.getText().toString().isEmpty()) {

                                if (!edtCarColor.getText().toString().isEmpty()) {

                                    if (!edtCarModel.getText().toString().isEmpty()) {

                                        if (!edtPhoneNumber.getText().toString().isEmpty()) {

                                            return true;

                                        } else {
                                            Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                                            return false;
                                        }

                                    } else {
                                        Toast.makeText(this, "Enter your Car Model", Toast.LENGTH_SHORT).show();
                                        return false;
                                    }


                                } else {
                                    Toast.makeText(this, "Enter your Car Color", Toast.LENGTH_SHORT).show();
                                    return false;
                                }


                            } else {
                                Toast.makeText(this, "Enter your Car Type", Toast.LENGTH_SHORT).show();
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