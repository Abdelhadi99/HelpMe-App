package com.example.helpme.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.helpme.R;
import com.example.helpme.UI.HelperAccountUi.LoginHelperActivity;
import com.example.helpme.UI.UserAccountUi.LoginUserActivity;

public class ChooseAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosse_account);
    }

    public void btnGoToUserAccount(View view) {
        Intent intent = new Intent(ChooseAccountActivity.this, LoginUserActivity.class);
        startActivity(intent);

    }

    public void btnGoToHelperAccount(View view) {
        startActivity(new Intent(ChooseAccountActivity.this, LoginHelperActivity.class));

    }
}