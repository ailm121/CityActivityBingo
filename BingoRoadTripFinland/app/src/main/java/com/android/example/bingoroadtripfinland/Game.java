package com.android.example.bingoroadtripfinland;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game implements Parcelable {

    private String id;
    private String area;
    private int gridSize;
    private boolean allowSameTask;
    private List<String> tasks;
    private List<String> teams;
    private HashMap<String, ArrayList<String>> teamsCompletedTasks;

    /**
     * creates new game, used when game is started from settings view
     * @param id string gameId, unique
     * @param area String game area
     * @param gridSize int size of bingo grid
     * @param taskOptions ArrayList<Task> all available tasks on area
     * @param teams ArrayList<String> team names
     */
    public Game (String id, String area, int gridSize, boolean allowSameTask, List<String> taskOptions,
                 List<String> teams) {
        this.id = id;
        this.area = area;
        this.gridSize = gridSize;
        this.allowSameTask = allowSameTask;
        this.teams = teams;
        teamsCompletedTasks = new HashMap<>();

        this.tasks = new ArrayList<>();
        chooseTasks(taskOptions);
    }

    /**
     * creates new game, used when game is started from join game
     * @param id string gameId, unique
     * @param area String game area
     * @param gridSize int size of bingo grid
     * @param tasks ArrayList<Task> tasks for game
     * @param teams ArrayList<String> team names
     * @param teamsCompletedTasks ArrayMap<String, ArrayList<String>> teamNames as id and their
     *                            completed task names as value
     */
    public Game (String id, String area, int gridSize, boolean allowSameTask, List<String> tasks,
                 List<String> teams, HashMap<String, ArrayList<String>> teamsCompletedTasks) {
        this.id = id;
        this.area = area;
        this.gridSize = gridSize;
        this.allowSameTask = allowSameTask;
        this.teams = teams;
        this.tasks = tasks;
        if (teamsCompletedTasks == null) {
            this.teamsCompletedTasks = new HashMap<String, ArrayList<String>>();
        } else {
            this.teamsCompletedTasks = teamsCompletedTasks;
        }
    }

    /**
     * Chooses random tasks for bingo grid
     * @param taskOptions list of available tasks on game area
     */
    private void chooseTasks(List<String> taskOptions){

        Random randomGenerator = new Random();

        while (tasks.size() != gridSize) {
            // Choose random tasks from the available ones on the area
            int index = randomGenerator.nextInt(taskOptions.size() - 1);
            String task = taskOptions.get(index);
            // no duplicate tasks
            if (!tasks.contains(task)) {
                tasks.add(task);
            }
        }
    }

    /**
     * Adds task to teams completed tasks and checks if this completes a row or column
     * @param team name of team that has completed a task
     * @param task name of completed task
     */
    public void addCompletedTask(String team, String task) {
        ArrayList<String> taskList = new ArrayList<>();
        if (teamsCompletedTasks != null) {
            if (teamsCompletedTasks.containsKey(team)) {
                taskList = teamsCompletedTasks.get(team);
            }
        }
        taskList.add(task);
        teamsCompletedTasks.put(team, taskList);
    }

    /**
     * Sets completed tasks for game
     * @param newCompletedTasks Map of new completed tasks
     */
    public void setCompletedTasks(HashMap<String, ArrayList<String>> newCompletedTasks) {
        this.teamsCompletedTasks = newCompletedTasks;
    }

    public String getId () { return id; }
    public String getArea () { return area; }
    public int getGridSize () { return gridSize; }
    public boolean isSameTaskAllowed () {return allowSameTask; }
    public List<String> getTeams () { return teams; }
    public List<String> getTasks () { return tasks; }
    public HashMap<String, ArrayList<String>> getTeamsCompletedTasks () {
        return teamsCompletedTasks; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(area);
        parcel.writeInt(gridSize);
        parcel.writeBoolean(allowSameTask);
        parcel.writeStringList(tasks);
        parcel.writeStringList(teams);
        parcel.writeMap(teamsCompletedTasks);
    }

    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    private Game (Parcel data) {
        tasks = new ArrayList<>();
        teams = new ArrayList<>();
        id = data.readString();
        area = data.readString();
        gridSize = data.readInt();
        allowSameTask = data.readBoolean();
        data.readStringList(tasks);
        data.readStringList(teams);
        teamsCompletedTasks = new HashMap<>();
    }
}
