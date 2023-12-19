package com.example.helpme.UI.Dao;

import com.example.helpme.UI.Model.Helper;
import com.example.helpme.UI.Model.Order;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DaoOrder {
    DatabaseReference databaseReference;


    public DaoOrder() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Order.class.getSimpleName());
    }

    public Task<Void> add(Order order){

        return databaseReference.child("order of "+order.getHelperName()).setValue(order);
    }


    public Task<Void>update(String key, HashMap<String,Object> hashMap){

        return databaseReference.child(key).updateChildren(hashMap);

    }

    public Task<Void>remove(String key){


        return databaseReference.child(key).removeValue();
    }
    public Query get(){

        return databaseReference.orderByValue();
    }


}


