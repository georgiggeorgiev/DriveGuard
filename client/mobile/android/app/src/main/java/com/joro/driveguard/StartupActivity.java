package com.joro.driveguard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class StartupActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

//        SharedPreferences preferences = getSharedPreferences("DATA", Context.MODE_PRIVATE);
//        boolean saved = preferences.getBoolean("APIKeySaved", false);
//        if (!saved)
//        {
//            // Go to login
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        } else
//        {
//            Constants.userFirstName = preferences.getString("userFirstName", null);
//            Constants.userPhoneNumber = preferences.getString("userPhoneNumber", null);
//            Constants.APIKey = preferences.getString("APIKey", null);
//
//            // Continue with activity
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        // TODO Always go to login for testing
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
