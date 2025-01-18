package com.android.example.bingoroadtripfinland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class NewGameSettingsActivity extends AppCompatActivity {

    private Spinner gridSizeSpnnr;
    private Spinner areaSpnnr;

    private ArrayList<TextInputEditText> teamNameNpts;

    private CheckBox allowSameTaskChckbx;

    private ArrayList<Task> tasks;

    private RandomString rsGenerator;
    private JsonReader jsonReader;

    /**
     * Activity for setting up new game, with settings for grid size, game area, allowing same tasks
     * being completed by multiple teams and team names
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);

        rsGenerator = new RandomString();
        jsonReader = new JsonReader(NewGameSettingsActivity.this);

        setButtons();
        setSpinners();
        setInputs();
        setCheckBoxes();

        tasks = new ArrayList<>();
        tasks = jsonReader.getAllTasks();
    }

    /**
     * Gets buttons from view and sets them on onClickListeners
     */
    private void setButtons() {

        // return button is used to go back to previous view/activity
        Button returnBtn = findViewById(R.id.returnJoinBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Start button is used to start the game after choosing settings
        Button startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get unique random gameId and other settings
                String gameId = rsGenerator.generateRandomGameId(7);
                int gridSize = countMinTasks(gridSizeSpnnr.getSelectedItem().toString());
                String areaName = areaSpnnr.getSelectedItem().toString();
                Boolean multipleCompletions = allowSameTaskChckbx.isChecked();
                ArrayList<String> teamNames = new ArrayList<>();
                for (TextInputEditText in : teamNameNpts) {
                    if (!in.getText().toString().isEmpty()) {
                        teamNames.add(in.getText().toString());
                    }
                }
                ArrayList<String> taskOptions = new ArrayList<>();
                for (Task t: tasks) {
                    if (t.getAreas().contains(areaName)) {
                        taskOptions.add(t.getName());
                    }
                }

                // Check that settings are valid and create new intent
                if (teamNames.size() >= 2 && !areaName.isEmpty()) {

                    Game newGame = new Game(gameId, areaName, gridSize, multipleCompletions,
                            taskOptions, teamNames);
                    Intent gameIntent = new Intent(NewGameSettingsActivity.this,
                            GameMainActivity.class);
                    gameIntent.putExtra("game", (Parcelable) newGame);
                    NewGameSettingsActivity.this.startActivity(gameIntent);
                    finish();

                } else {
                    Toast.makeText(NewGameSettingsActivity.this,
                            "Valitse alue ja vähintään 2 joukkue nimeä",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Gets spinners from view and sets gridSize on listener to control value options of area spinner
     * as all areas are not available for bigger grid sizes
     */
    private void setSpinners() {

        areaSpnnr = findViewById(R.id.areaSpnnr);

        gridSizeSpnnr = findViewById(R.id.gridSizeSpnnr);
        // Add grid size options
        String[] gridSizes = new String[] {"3X3", "4X4", "5X5"};
        ArrayAdapter<String> sizesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, gridSizes);
        sizesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gridSizeSpnnr.setAdapter(sizesAdapter);

        // Listen for changes
        gridSizeSpnnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Area options are read from file, sorted based on whether or not there are enough
                // tasks for specific area and added to spinner
                ArrayMap<String, Integer> areaCounts = countAreaTasks();

                ArrayList<String> areas = new ArrayList<>();
                int minTasks = countMinTasks(gridSizeSpnnr.getSelectedItem().toString());

                for (String key : areaCounts.keySet()) {
                    if (areaCounts.get(key) >= minTasks) {
                        areas.add(key);
                    }
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(NewGameSettingsActivity.this,
                        android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpnnr.setAdapter(areasAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                areaSpnnr.setAdapter(null);
            }
        });
    }

    /**
     * Gets team name text inputs from view and adds them to ArrayList
     */
    private void setInputs() {

        // Input fields used to choose team names
        teamNameNpts = new ArrayList<>();
        TextInputEditText t1 = findViewById(R.id.name1Npt);
        TextInputEditText t2 = findViewById(R.id.name2Npt);
        TextInputEditText t3 = findViewById(R.id.name3Npt);
        TextInputEditText t4 = findViewById(R.id.name4Npt);

        teamNameNpts.add(t1);
        teamNameNpts.add(t2);
        teamNameNpts.add(t3);
        teamNameNpts.add(t4);
    }

    /**
     * Gets checkbox from view
     */
    private void setCheckBoxes() {
        // Check box for choosing whether or not multiple teams can complete the same task
        allowSameTaskChckbx = findViewById(R.id.sameTaskChckbx);
    }

    /**
     * Counts tasks that are available on each area
     * @return ArrayMap with area name as key and amount of tasks as value
     */
    private ArrayMap<String, Integer> countAreaTasks() {
        ArrayMap<String, Integer> res = new ArrayMap<>();

        // Initialize with all place names
        ArrayList<String> allAreas = new ArrayList<>(Arrays.asList("KOTKA", "TAMPERE", "ROVANIEMI", "PORVOO", "KYMENLAAKSO", "LAPPI", "PIRKANMAA", "UUSIMAA", "SUOMI"));
        for (String areaName : allAreas) {
            res.put(areaName, 0);
        }

        int areaCount;

        for (Task t : tasks) {
            ArrayList<String> taskAreas = t.getAreas();

            for (String a : taskAreas) {

                if (a.equals("ALL")) {
                    for (String key : res.keySet()) {
                        areaCount = res.get(key) + 1;
                        res.put(key, areaCount);
                    }
                }
                else if (res.containsKey(a)) {
                    areaCount = res.get(a) + 1;
                    res.put(a, areaCount);
                }
            }
        }
        return res;
    }

    /**
     * Chooses minimum amount of tasks based on user grid size choice
     * @param gridSize String with users choice from gridSizeSpinner
     * @return int amount of tasks
     */
    private int countMinTasks(String gridSize) {
        if (gridSize.equals("3X3")) {
            return 9;
        } else if (gridSize.equals("4X4")) {
            return 16;
        } else {
            return 25;
        }
    }
}