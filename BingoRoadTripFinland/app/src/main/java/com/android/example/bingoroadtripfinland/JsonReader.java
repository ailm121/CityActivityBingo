package com.android.example.bingoroadtripfinland;

import android.content.Context;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class JsonReader {

    private Context context;
    private ArrayList<Task> allTasks;

    public JsonReader(Context context) {
        this.context = context;
        allTasks = new ArrayList<>();
        setAllTasks();
    }

    /**
     * Reads tasks JSON file
     * @return String JSON string with file contents
     */
    private String readTasks() {
        String jsonString;
        try {
            InputStream is = context.getAssets().open("tasks.json");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }

    /**
     * Parses JSON formatted string from readTasks() creates Task objects based on it
     * and adds tasks to ArrayList
     */
    private void setAllTasks() {
        String jsonFileString = readTasks();

        try {
            JSONArray jsonArray = new JSONArray(jsonFileString);
            String name;
            String description;
            Location location;
            String address;
            ArrayList<String> areas = new ArrayList<>();

            Double lat;
            Double lon;
            JSONArray areasTemp = new JSONArray();

            // Get JSON attributes and create Task
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = (JSONObject) jsonArray.get(i);

                name = obj.getString("name");
                description = obj.getString("description");

                lat = (Double) obj.getJSONArray("gpsLocation").get(0);
                lon = (Double) obj.getJSONArray("gpsLocation").get(1);
                location = new Location("");
                location.setLatitude(lat);
                location.setLongitude(lon);

                address = obj.getString("address");

                areasTemp = obj.getJSONArray("areas");
                for (int j=0;j<areasTemp.length();j++){
                    areas.add(areasTemp.getString(j));
                }
                Task t = new Task(name, description, location, address, (ArrayList<String>) areas.clone());
                allTasks.add(t);
                areas.clear();
            }

        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
        }
    }

    public ArrayList<Task> getAllTasks() { return allTasks; }
}
