package com.example.helpme.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpme.R;
import com.example.helpme.UI.Dao.DaoCase;
import com.example.helpme.UI.Dao.DaoHelper;
import com.example.helpme.UI.Dao.DaoOrder;
import com.example.helpme.UI.Model.Case;
import com.example.helpme.UI.Model.Helper;
import com.example.helpme.UI.Model.Order;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeHelperAdapter extends RecyclerView.Adapter<HomeHelperAdapter.ViewHolder> {

    ArrayList<Case> caseArrayList = new ArrayList<>();
    Context context;

    DaoHelper daoHelper = new DaoHelper();

    String helperID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private static final int EARTH_RADIUS = 6371; // Radius of the earth in kilometers


    public HomeHelperAdapter(ArrayList<Case> caseArrayList, Context context) {
        this.caseArrayList = caseArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public HomeHelperAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_helper_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHelperAdapter.ViewHolder holder, int position) {

        Case currentCase = caseArrayList.get(position);


        // Calculate the distance between user and helper

        daoHelper.get().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Helper helper = data.getValue(Helper.class);
                    if (helper.getKey().equals(helperID)) {
                        HashMap<String, Double> helperLocationMap = helper.getLocationMap();

                        if (helperLocationMap != null) {
                            double latHelper = helperLocationMap.get("latitude");
                            double lngHelper = helperLocationMap.get("longitude");
                            LatLng helperLocation = new LatLng(latHelper, lngHelper);

                            DaoCase daoCase = new DaoCase();
                            daoCase.get().addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        Case aCase = data.getValue(Case.class);
                                        if (aCase != null && aCase.getCaseKey() != null ) {
                                            HashMap<String, Double> caseLocationMap = aCase.getUserLocation();
                                            if (caseLocationMap != null) {
                                                double latCase = caseLocationMap.get("latitude");
                                                double lngCase = caseLocationMap.get("longitude");
                                                LatLng caseLocation = new LatLng(latCase, lngCase);

                                                double distance = calculateDistance(helperLocation, caseLocation);

                                                holder.txtDistance.setText(String.format("%.2f km", distance));
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle errors
                                }
                            });

                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // Set the user name who needs help
        holder.userNameNeedsHelp.setText(currentCase.getUserName());

        // Handle button clicks (if needed)
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                caseArrayList.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle accept button click
                daoHelper.get().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Helper currentHelper = data.getValue(Helper.class);
                            if (currentHelper.getKey().equals(helperID)) {

                                DaoCase daoCase = new DaoCase();


                                HashMap<String, Object> updatedCase = new HashMap<>();
                                updatedCase.put("helperKey", helperID);
                                updatedCase.put("helperName", currentHelper.getName());
                                updatedCase.put("helperExpType", currentHelper.getExperienceType());
                                updatedCase.put("helperPhoneNumber", currentHelper.getPhoneNumber());
                                updatedCase.put("accept", true);



                                daoCase.update(currentCase.getCaseKey(), updatedCase);


                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


    }

    @Override
    public int getItemCount() {
        return caseArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameNeedsHelp;
        ImageView btnCancel, btnAccept;
        TextView txtDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameNeedsHelp = itemView.findViewById(R.id.txt_name_who_needs_help_HelperHome);
            btnCancel = itemView.findViewById(R.id.btnCancel_home_item);
            btnAccept = itemView.findViewById(R.id.btnAccept_home_item);
            txtDistance = itemView.findViewById(R.id.txt_user_distance_HelperHome);
        }
    }

    // Calculates distance between two LatLng points using Haversine formula
    public static double calculateDistance(LatLng startPoint, LatLng endPoint) {

        if (startPoint == null || endPoint == null) {
            return 0; // Or handle it in a way that makes sense for your application
        }
        double lat1 = Math.toRadians(startPoint.latitude);
        double lon1 = Math.toRadians(startPoint.longitude);
        double lat2 = Math.toRadians(endPoint.latitude);
        double lon2 = Math.toRadians(endPoint.longitude);

        double dLon = lon2 - lon1;
        double dLat = lat2 - lat1;

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // Distance in kilometers
    }
}
