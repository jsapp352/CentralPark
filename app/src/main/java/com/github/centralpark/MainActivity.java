package com.github.centralpark;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

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

        Garage garage = GarageFinder.findGarages(this, "116");

        int minutes = garage.walkingDuration / 60;
        int seconds = garage.walkingDuration % 60;

        Log.d("Output", "Destination: " + GarageFinder.destinationName);
        Log.d("Output", "Garage " + garage.name);
        Log.d("Output", minutes + "m " + seconds + "s walking time");
        Log.d("Output", garage.available + " out of " + garage.total + " spaces available.");

        resultTextView.setText("Destination: " + GarageFinder.destinationName +
                               "\nGarage " + garage.name +
                               "\n" + minutes + "m " + seconds + "s walking time\n" +
                               garage.available + " out of " + garage.total + " spaces available.");
    }
}
