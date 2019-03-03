package com.github.centralpark;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

class GarageFinder
{
    static Garage findClosestAvailableGarage(JSONArray array)
    {
        if (array == null)
            return null;

        int length = array.length();

        for (int i = 0; i < length; i++)
        {
            String garage_name = array.optJSONObject(i).optString("name");

            Log.d("Debug", "Checking garage: " + garage_name);

            Garage garage = Campus.getGarage(garage_name);

            if (garage == null)
                Log.d("Debug", "garage_hashmap is null....");

            if (garage.hasAvailableSpaces())
            {
                garage.setWalkingDuration(array.optJSONObject(i).optInt("walk_duration"));
                return garage;
            }
        }

        return null;
    }


}

