package com.github.centralpark;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

class GarageFinder
{
    private static JSONObject buildings;
    private static ArrayList<Garage> garage_list;

    public static void findGarages(Context context, String buildingID)
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
            Log.d("Webscrape error","Could not webscrape UCF Parking Data!");
        }

        // Get the building and garage walking durations information from the JSON data
        JSONObject destination = getBuilding(buildingID);
        JSONArray destinationGarageList = getDestinationGarageList(destination);



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
        ArrayList<Garage> garages = new ArrayList<>();
        String inputLine;
        String name = "Garage Something";
        URL url = new URL(address);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

        while ((inputLine = reader.readLine()) != null)
        {

            if (inputLine.contains("<td class=\"dxgv\">"))
            {
                // Get available capacity from UCF website
                String start = "<td class=\"dxgv\">";
                String end = "</td>";
                int startIndex = inputLine.indexOf(start) + start.length();
                int endIndex = inputLine.indexOf(end);
                name = inputLine.substring(startIndex, endIndex);
            }
            else if (inputLine.contains("<td class=\"dxgv\" style=\"border-bottom-width:0px;\">"))
            {
                // Get garage name from UCF website
                String start = "<td class=\"dxgv\" style=\"border-bottom-width:0px;\">";
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
                garages.add(new Garage(name, available, capacity));
            }
        }

        garage_list = garages;
    }
}

