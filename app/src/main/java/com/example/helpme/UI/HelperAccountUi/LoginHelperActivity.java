package com.example.helpme.UI.HelperAccountUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.helpme.UI.UserAccountUi.HomeUserActivity;
import com.example.helpme.UI.UserAccountUi.LoginUserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginHelperActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private TextView btnLogin;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DaoHelper daoHelper = new DaoHelper();

    private ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {

            daoHelper.get().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Helper helper = data.getValue(Helper.class);
                        if (helper != null) {
                            progressBar.setVisibility(View.VISIBLE);

                            if (helper.getEmail().equals(auth.getCurrentUser().getEmail())) {


                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                if (firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    Intent intent = new Intent(LoginHelperActivity.this, HomeHelperActivity.class);
                                    startActivity(intent);
                                    progressBar.setVisibility(View.GONE);
                                    finish();

                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    showSnackBar();
                                }


                                break;
                            } else {
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        } else {
            Toast.makeText(LoginHelperActivity.this, "You can Login OR SignUp", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_helper);
        initData();

        Intent intent = getIntent();

        if ("new".equals(intent.getStringExtra("sign"))) {

            showSnackBar();
            progressBar.setVisibility(View.GONE);

        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email = edtEmail.getText().toString().toLowerCase();
                String password = edtPassword.getText().toString().toLowerCase();

                if (edtNotEmpty()) {

                    progressBar.setVisibility(View.VISIBLE);
                    //////////////////////////////////////////
                    auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = authResult.getUser();
                            String name = user.getDisplayName();


                            daoHelper.get().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        Helper helper = data.getValue(Helper.class);
                                        if (helper.getEmail().equals(auth.getCurrentUser().getEmail())) {


                                            if (auth.getCurrentUser().isEmailVerified()) {


                                                Intent intent = new Intent(LoginHelperActivity.this, HomeHelperActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(LoginHelperActivity.this, "welcome " + name, Toast.LENGTH_LONG).show();
                                                finish();
                                            } else {
                                                showSnackBar();
                                                progressBar.setVisibility(View.GONE);
                                            }


                                            break;
                                        } else {
                                            Toast.makeText(LoginHelperActivity.this, "No user has this email.", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle specific error scenarios


                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginHelperActivity.this, "Failed to log in. An internal error occurred.", Toast.LENGTH_SHORT).show();
                        }


                    });
                    ///////////////////////////////////////////////


                }


            }
        });

    }

    public void btnGoToSignUpUser(View view) {
        Intent intent = new Intent(LoginHelperActivity.this, SignupHelperActivity.class);
        startActivity(intent);
    }

    private void initData() {
        edtEmail = findViewById(R.id.edt_email_login_helper);
        edtPassword = findViewById(R.id.edt_password_login_helper);
        btnLogin = findViewById(R.id.btnLoginHelper);

        progressBar = findViewById(R.id.progressBarLoginHelper);
    }

    private boolean edtNotEmpty() {
        if (!edtEmail.getText().toString().isEmpty()) {

            if (!edtPassword.getText().toString().isEmpty()) {

                return true;

            } else {
                Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    public void btnGoToSignUpHelper(View view) {
        startActivity(new Intent(LoginHelperActivity.this, SignupHelperActivity.class));
    }


    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.loginHelperLayout), "We sent a Verification message to your email.\nPlease verify your email.", Snackbar.LENGTH_INDEFINITE).
                setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).setBackgroundTint(getResources().getColor(R.color.yellow)).setTextColor(getResources().getColor(R.color.black))
                .setActionTextColor(getResources().getColor(R.color.black)).setTextMaxLines(5);
        snackbar.show();
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


    public void btnForgetPassHelper(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_forget_password);
        dialog.show();
        EditText editText = dialog.findViewById(R.id.edt_email_forgetPass_user);
        TextView btnReset = dialog.findViewById(R.id.btnResetForgetPass);
        TextView btnCancel = dialog.findViewById(R.id.btnCancelForgetPass);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = editText.getText().toString();
                if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(LoginHelperActivity.this, "Enter your registered email !", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginHelperActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(LoginHelperActivity.this, "Unable to send, failed ! ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }


}