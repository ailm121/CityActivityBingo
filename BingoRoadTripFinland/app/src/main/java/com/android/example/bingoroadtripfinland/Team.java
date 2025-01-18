package com.android.example.bingoroadtripfinland;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

public class Team {

    private final String name;
    private final String color;
    private final boolean thisTeam;

    private static final ImmutableList<String> ALL_COLORS = ImmutableList.of(
            "RED", "MAGENTA", "GREEN", "BLUE");

    /**
     * Class for representing a team
     * @param name String teams name
     * @param color int representing team color
     * @param thisTeam boolean for indicating whether user belongs to this team
     */
    public Team(String name, int color, boolean thisTeam) {
        this.name = name;
        this.color = ALL_COLORS.get(color);
        this.thisTeam = thisTeam;
    }

    public String getName() { return name; }
    public String getColor() { return color; }
    public boolean getThisTeam() { return thisTeam; }
}
