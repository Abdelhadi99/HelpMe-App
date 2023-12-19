package com.example.helpme.UI.Dao;

import android.util.Log;

import com.example.helpme.UI.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DaoUser {
    DatabaseReference databaseReference;

    public DaoUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(User.class.getSimpleName());
    }

    public Task<Void> add(User user){

        return databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(user);
    }


    public Task<Void>update(String key, HashMap<String,Object>hashMap){

        return databaseReference.child(key).updateChildren(hashMap);

    }

    public Task<Void>remove(String key){


        return databaseReference.child(key).removeValue();
    }
    public Query get(){

        return databaseReference.orderByValue();
    }


}
