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
    private static JSONObject buildings;
    private static HashMap<String, Garage> garage_hashmap;
    static String destinationName;

    public static Garage findGarages(Context context, String buildingID)
    {
        String garageDataURL = "http://secure.parking.ucf.edu/GarageCount/";
        String building_data_filename = "building_data.json";

        // Create a JSON object containing building information and walking-duration times from
        // each garage to each building
        try
        {
            populateBuildingData(context, building_data_filename);
        }
        catch (Exception e)
        {
            Log.d("JSON error", "Couldn't import JSON data.");
        }

        // Create a list of parking garages that contains current availability data for each garage
        try
        {
            populateGarageData(garageDataURL);
        }
        catch (Exception e)
        {
            Log.d("Webscrape error","Could not webscrape UCF Parking Data!", e);
        }

        // Get the building and garage walking durations information from the JSON data
        JSONObject destination = getBuilding(buildingID);

        destinationName = destination.optString("name");



        JSONArray destinationGarageList = getDestinationGarageList(destination);

        if (destinationGarageList == null)
            return null;

        return findClosestAvailableGarage(destinationGarageList);
    }

    static Garage findClosestAvailableGarage(JSONArray array)
    {
        int length = array.length();

        for (int i = 0; i < length; i++)
        {
            String garage_name = array.optJSONObject(i).optString("name");

            Log.d("Debug", "Checking garage: " + garage_name);

            Garage garage = garage_hashmap.get(garage_name);

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

    // Parse the building-data JSON file and store it as a member variable
    private static void populateBuildingData(Context context, String filename) throws Exception
    {
        String jsonString = null;

        InputStream in = context.getAssets().open("building_data.json");
        int size = in.available();
        byte[] buffer = new byte[size];
        in.read(buffer);
        in.close();
        jsonString = new String(buffer, "UTF-8");

        buildings = new JSONObject(jsonString);
    }

    static JSONObject getBuilding(String buildingID)
    {
        return buildings.optJSONObject(buildingID);
    }

    static JSONArray getDestinationGarageList(JSONObject building)
    {
        return building.optJSONArray("garage_walk_durations");
    }

    private static void populateGarageData(String address) throws Exception
    {
        HashMap<String, Garage> garages = new HashMap<>();
        String inputLine;
        String name = "Garage Something";
        URL url = new URL(address);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

        while ((inputLine = reader.readLine()) != null)
        {

            if (inputLine.contains("<td class=\"dxgv\">Garage "))
            {
                // Get garage name from UCF website
                String start = "<td class=\"dxgv\">Garage ";
                String end = "</td>";
                int startIndex = inputLine.indexOf(start) + start.length();
                int endIndex = inputLine.indexOf(end);
                name = inputLine.substring(startIndex, endIndex);
            }
            else if (inputLine.contains("<td class=\"dxgv\" style=\"border-bottom-width:0px;\">Garage "))
            {
                // Get garage name from UCF website
                String start = "<td class=\"dxgv\" style=\"border-bottom-width:0px;\">Garage ";
                String end = "</td>";
                int startIndex = inputLine.indexOf(start) + start.length();
                int endIndex = inputLine.indexOf(end);
                name = inputLine.substring(startIndex, endIndex);
            }
            else if (inputLine.contains("<strong>"))
            {
                // Get available capacity from UCF website
                String start = "<strong>";
                String end = "</strong>";
                int startIndex = inputLine.indexOf(start) + start.length();
                int endIndex = inputLine.indexOf(end);
                int available = Integer.parseInt(inputLine.substring(startIndex, endIndex));

                // Get total capacity from UCF website
                start = "/";
                startIndex = inputLine.lastIndexOf(start) + start.length();
                int capacity = Integer.parseInt(inputLine.substring(startIndex));

                // Add to the list of garages
                garages.put(name, new Garage(name, available, capacity));

                Log.d("debug", "garage name  --"+name+"--");
            }
        }

        garage_hashmap = garages;
    }
}

