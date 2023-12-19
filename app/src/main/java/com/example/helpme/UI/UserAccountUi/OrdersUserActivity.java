package com.example.helpme.UI.UserAccountUi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpme.R;
import com.example.helpme.UI.Adapters.OrderAdapter;
import com.example.helpme.UI.ChooseAccountActivity;
import com.example.helpme.UI.Dao.DaoOrder;
import com.example.helpme.UI.HelperAccountUi.HomeHelperActivity;
import com.example.helpme.UI.Model.Order;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrdersUserActivity extends AppCompatActivity {

    private ImageView btnOpenNavView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private RecyclerView recView;
    private OrderAdapter adapter;
    private ArrayList<Order> orderArrayList;

    private TextView txtNoOrders;

    DaoOrder daoOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_user);
        intiData();
        daoOrder = new DaoOrder();

        adapter = new OrderAdapter(OrdersUserActivity.this,orderArrayList);

        daoOrder.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean isThereOrders = false;

                for (DataSnapshot data : snapshot.getChildren()){
                    Order order = data.getValue(Order.class);
                    orderArrayList.add(order);
                   if (order != null){
                       isThereOrders = true;
                   }
                }


                // handle if there is orders or not
                handleIsThereOrders(isThereOrders);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




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
                        startActivity(new Intent(OrdersUserActivity.this, ProfileUserActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.logout:

                        AlertDialog.Builder builder = new AlertDialog.Builder(OrdersUserActivity.this);
                        builder.setTitle("Confirm Logout !").
                                setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                firebaseAuth.signOut();
                                Toast.makeText(OrdersUserActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(OrdersUserActivity.this, ChooseAccountActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
                        drawerLayout.closeDrawer(GravityCompat.START);

                }

                return true;
            }
        });


    }

    private void handleIsThereOrders(boolean isThereOrders) {
        if (isThereOrders) {
            txtNoOrders.setVisibility(View.GONE);
            recView.setVisibility(View.VISIBLE);
            recView.setAdapter(adapter);
            recView.setLayoutManager(new LinearLayoutManager(OrdersUserActivity.this));
            adapter.notifyDataSetChanged();
        } else {
            recView.setVisibility(View.GONE);
            txtNoOrders.setVisibility(View.VISIBLE);
        }
    }

    void intiData() {
        btnOpenNavView = findViewById(R.id.btnOpenMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        recView = findViewById(R.id.recView_orders);
        orderArrayList = new ArrayList<>();
        txtNoOrders =  findViewById(R.id.txtNoOrders);

    }
}