package com.joro.driveguard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        boolean saved = preferences.getBoolean("APIKeySaved", false);
        if (saved)
        {
            Constants.APIKey = preferences.getString("APIKey", null);
        } else
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
