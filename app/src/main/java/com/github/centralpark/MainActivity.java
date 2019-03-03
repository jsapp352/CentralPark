package com.github.centralpark;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Allow HTTP request for webscrape
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Garage closestGarage = GarageFinder.findGarages(this, "116");

        Log.d("Output", "Garage " + closestGarage.name);
        Log.d("output", closestGarage.walkingDuration + " seconds walking time");
    }
}
