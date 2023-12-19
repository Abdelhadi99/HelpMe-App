package com.example.helpme.UI.Dao;

import com.example.helpme.UI.Model.Helper;
import com.example.helpme.UI.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DaoHelper {
    DatabaseReference databaseReference;

    public DaoHelper() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Helper.class.getSimpleName());
    }

    public Task<Void> add(Helper helper){

        return databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(helper);
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


