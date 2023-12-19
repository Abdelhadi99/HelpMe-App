package com.example.helpme.UI.Dao;

import com.example.helpme.UI.Model.Case;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DaoCase {
    DatabaseReference databaseReference;

    public DaoCase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Case.class.getSimpleName());
    }

    public Task<Void> add(Case case1){

        return databaseReference.child("case"+case1.getUserKey()).setValue(case1);
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


