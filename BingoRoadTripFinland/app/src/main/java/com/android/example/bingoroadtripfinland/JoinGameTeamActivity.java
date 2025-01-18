package com.android.example.bingoroadtripfinland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class JoinGameTeamActivity extends AppCompatActivity {

    private Spinner teamChoiceSpnnr;
    private FireStoreReader fsReader;

    private ArrayList<String> teams;

    /**
     * Activity for choosing team in an already created game
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game_team);

        fsReader = new FireStoreReader();
        getGuiElements();
    }

    /**
     * gets gui elements and sets them on listeners
     */
    private void getGuiElements() {
        teamChoiceSpnnr = findViewById(R.id.teamSnnpr);
        Button returnTeamsBtn = findViewById(R.id.returnTaskBtn);
        Button chooseTeamBtn = findViewById(R.id.chooseTeamBtn);

        // Set team options to spinner
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                teams =  getTeams(getIntent().getStringExtra("id"));
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_spinner_dropdown_item, teams);
                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                teamChoiceSpnnr.setAdapter(adapter);
            }
        }, 1000);   //3 seconds

        // Return to previous activity
        returnTeamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chooseTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teamChoiceSpnnr.getSelectedItem() != null) {

                    // Get chosen team and continue to gameMainActivity
                    String gameId = getIntent().getStringExtra("id");
                    Intent startJoinedGameIntent = new Intent(JoinGameTeamActivity.this,
                            GameMainActivity.class);
                    startJoinedGameIntent.putExtra("team", teamChoiceSpnnr.getSelectedItem().toString());
                    startJoinedGameIntent.putExtra("id", gameId);

                    JoinGameTeamActivity.this.startActivity(startJoinedGameIntent);
                    finish();
                }
            }
        });
    }

    /**
     * Gets teams for game with id
     * @param id String game id
     * @return ArrayList of team names
     */
    private ArrayList<String> getTeams(String id) {
        QuerySnapshot qs = fsReader.getFireStoreEntries();
        ArrayList<String> teams = new ArrayList<>();

        for (QueryDocumentSnapshot doc : qs) {
            if (Objects.equals(doc.getString("id"), id)) {
                teams = (ArrayList<String>) doc.getData().get("teams");
            }
        }
        return teams;
    }
}