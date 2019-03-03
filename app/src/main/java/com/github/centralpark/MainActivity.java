package com.github.centralpark;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Allow HTTP request for webscrape
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final TextView resultTextView = (TextView) findViewById(R.id.Result);

        String garageDataURL = "http://secure.parking.ucf.edu/GarageCount/";
        String building_data_filename = "building_data.json";

        // Create a JSON object containing building information and walking-duration times from
        // each garage to each building
        try
        {
            Campus.populateBuildingData(this, building_data_filename);
        }
        catch (Exception e)
        {
            Log.d("JSON error", "Couldn't import JSON data.");
        }

        // Create a list of parking garages that contains current availability data for each garage
        try
        {
            Campus.populateGarageData(garageDataURL);
        }
        catch (Exception e)
        {
            Log.d("Webscrape error","Could not webscrape UCF Parking Data!", e);
        }

        HashMap<String, String> buildingIDMap = Campus.getBuildingIDMap();
        Object [] buildingNameObjects = buildingIDMap.keySet().toArray();
        String [] buildingNames = Arrays.copyOf(buildingNameObjects,
                buildingNameObjects.length, String[].class);

        // Code adapted from: https://www.journaldev.com/9574/android-autocompletetextview-example-tutorial
        //Creating the instance of ArrayAdapter containing list of fruit names
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item, buildingNames);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.destinatioinInput);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

        // Get the building and garage walking durations information from the JSON data
        JSONObject destination = Campus.getBuilding("116");

        String destinationName = destination.optString("name");

        JSONArray destinationGarageList = Campus.getDestinationGarageList(destination);

        Garage garage = GarageFinder.findClosestAvailableGarage(destinationGarageList);

        int minutes = garage.walkingDuration / 60;
        int seconds = garage.walkingDuration % 60;

        Log.d("Output", "Destination: " + destinationName);
        Log.d("Output", "Garage " + garage.name);
        Log.d("Output", minutes + "m " + seconds + "s walking time");
        Log.d("Output", garage.available + " out of " + garage.total + " spaces available.");

        resultTextView.setText("Destination: " + destinationName +
                               "\nGarage " + garage.name +
                               "\n" + minutes + "m " + seconds + "s walking time\n" +
                               garage.available + " out of " + garage.total + " spaces available.");
    }
}
