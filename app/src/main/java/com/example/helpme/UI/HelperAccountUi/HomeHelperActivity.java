package com.example.helpme.UI.HelperAccountUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpme.R;
import com.example.helpme.UI.Adapters.HomeHelperAdapter;
import com.example.helpme.UI.ChooseAccountActivity;
import com.example.helpme.UI.Dao.DaoCase;
import com.example.helpme.UI.Model.Case;
import com.example.helpme.UI.UserAccountUi.HomeUserActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeHelperActivity extends AppCompatActivity {
    ArrayList<Case> caseArrayList = new ArrayList<>();
    HomeHelperAdapter adapter;
    RecyclerView recView;
    DaoCase daoCase;

    TextView txtNoCases;
    Dialog dialogIfAccept;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_helper);

        dialogIfAccept = new Dialog(HomeHelperActivity.this);
        dialogIfAccept.setCancelable(false);

        recView = findViewById(R.id.recView_homeHelper);
        daoCase = new DaoCase();
        txtNoCases = findViewById(R.id.txtNoCases);
        adapter = new HomeHelperAdapter(caseArrayList, HomeHelperActivity.this);


        daoCase.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isThereACase = false;

                String userName = "";
                String userCarType = "";
                String userCarColor = "";
                String caseKey = "";

                boolean Accept = false;
                boolean Complete = false;

                Case case1 = null;

                for (DataSnapshot data : snapshot.getChildren()) {
                     case1 = data.getValue(Case.class);
                    caseArrayList.add(case1);


                    if (case1 != null ) {
                        isThereACase = true;

                        // data i need if the helper accept the case
                        userName = case1.getUserName();
                        userCarType = case1.getCarType();
                        userCarColor = case1.getCarColor();
                        caseKey = case1.getCaseKey();
                        Accept = case1.isAccept();
                        Complete = case1.isCompleted();


                        if (Accept) {
                            // show the Dialog if the case Accepted

                            caseArrayList.remove(case1);
                            adapter.notifyDataSetChanged();

                        }


//                        adapter.notifyDataSetChanged();
                        break;  // Exit the loop if a matching case is found
                    }

                }

                // Now 'isThereACase' holds whether there is a matching case or not

                // Move your logic here or call another method with the logic


                handleIsThereACase(isThereACase);


                handleIfHelperClickAccept(case1,Accept,Complete, caseKey, userName, userCarType, userCarColor, true);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });


        ///////////////////////////////////////////////


    }

    private void handleIsThereACase(boolean isThereACase) {
        if (!isFinishing()) { // Check if the activity is not finishing
        if (isThereACase) {
            txtNoCases.setVisibility(View.GONE);
            recView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            recView.setAdapter(adapter);
            recView.setLayoutManager(new LinearLayoutManager(HomeHelperActivity.this));
        } else {
            recView.setVisibility(View.GONE);
            txtNoCases.setVisibility(View.VISIBLE);
        }

    }}

    private void handleIfHelperClickAccept(Case case1,boolean Accept,boolean Complete, String caseKey, String userName, String userCarType, String userCarColor, boolean isCompleted) {
        if (!isFinishing()) { // Check if the activity is not finishing

            if(case1 != null) {

                if (Accept && !Complete) {
                    dialogIfAccept.setContentView(R.layout.item_home_helper_if_accept);
                    dialogIfAccept.show();
                    TextView txtUserName = dialogIfAccept.findViewById(R.id.txt_name_who_needs_help_HelperHome_ifAccept);
                    txtUserName.setText(userName);
                    TextView txtUserCarColor = dialogIfAccept.findViewById(R.id.txt_carColor_who_needs_help_HelperHome_ifAccept);
                    txtUserCarColor.setText(userCarColor);
                    TextView txtUserCarType = dialogIfAccept.findViewById(R.id.txt_carName_who_needs_help_HelperHome_ifAccept);
                    txtUserCarType.setText(userCarType);


                    LinearLayout btnLocation = dialogIfAccept.findViewById(R.id.btnGetLocation_helperIfAccept);

                    btnLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HomeHelperActivity.this, LocationOfTheUserActivity.class);
                            startActivity(intent);
                        }
                    });


                    LinearLayout btnCompleted = dialogIfAccept.findViewById(R.id.btnCompleted_helperIfAccept);
                    btnCompleted.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            HashMap<String, Object> updateCase = new HashMap<>();
                            updateCase.put("completed", isCompleted);
                            daoCase.update(caseKey, updateCase).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialogIfAccept.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HomeHelperActivity.this, "Failed Complete, try again.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                } else if (Accept && Complete) {
                    dialogIfAccept.dismiss();
                    caseArrayList.remove(case1);
                    adapter.notifyDataSetChanged();
                }
            }


        }

    }

    public void btnGoToProfileHelper(View view) {
        startActivity(new Intent(HomeHelperActivity.this, ProfileHelperActivity.class));
    }

    public void btnSignOutHelper(View view) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout !").
                setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Toast.makeText(HomeHelperActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeHelperActivity.this, ChooseAccountActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
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

    }
}