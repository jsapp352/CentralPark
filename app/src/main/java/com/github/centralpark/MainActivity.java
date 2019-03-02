package com.github.centralpark;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GarageFinder.findGarages(this);
        JSONObject hec_building = GarageFinder.getBuilding("116");
    }
}
