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
import java.util.Iterator;

class Campus
{
    private static JSONObject buildings;
    private static HashMap<String, Garage> garage_hashmap;

    // Parse the building-data JSON file and store it as a member variable
    static void populateBuildingData(Context context, String filename) throws Exception
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

    static Garage getGarage(String name)
    {
        return garage_hashmap.get(name);
    }

    static JSONObject getBuilding(String buildingID)
    {
        return buildings.optJSONObject(buildingID);
    }

    static JSONArray getDestinationGarageList(JSONObject building)
    {
        return building.optJSONArray("garage_walk_durations");
    }

    static void populateGarageData(String address) throws Exception
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

    static HashMap<String, String> getBuildingIDMap()
    {
        HashMap<String, String> map = new HashMap<>();
        Iterator<String> keyIterator = buildings.keys();

        while (keyIterator.hasNext())
        {
            JSONObject building = buildings.optJSONObject(keyIterator.next());
            map.put(building.optString("name"), building.optString("id"));
        }

        return map;

    }
}
