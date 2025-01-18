package com.android.example.bingoroadtripfinland;

import android.location.Location;

import java.util.ArrayList;

public class Task {

    private String name;
    private String description;
    private Location gpsLocation;
    private String address;

    private ArrayList<String> areas;

    /**
     * Class for representing a task
     * @param name string task name
     * @param description string task description and instructions
     * @param location Location task gps location
     * @param address String task address
     * @param areas ArrayList<String> areas this task is available
     */
    public Task (String name, String description, Location location, String address,
                 ArrayList<String> areas) {
        this.name = name;
        this.description = description;
        this.gpsLocation = location;
        this.address = address;
        this.areas = areas;
    }

    public String getName() {return name;}
    public String getDescription() {return description;}
    public Location getGpsLocation() {return gpsLocation;}
    public String getAddress() {return address;}
    public ArrayList<String> getAreas() {return this.areas;}
}
