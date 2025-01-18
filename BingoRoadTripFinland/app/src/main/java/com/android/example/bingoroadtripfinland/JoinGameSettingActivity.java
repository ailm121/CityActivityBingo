package com.android.example.bingoroadtripfinland;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class JoinGameSettingActivity extends AppCompatActivity {

    private TextInputEditText gameIdNpt;

    private ArrayList<String> games;
    FireStoreReader fsReader;

    /**
     * Activity for entering game id and joining the game
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game_setting);

        games = new ArrayList<>();
        fsReader = new FireStoreReader();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getGuiElements();
                getGames();
            }
        }, 3000);
    }

    /**
     * Get Gui elements and sets listeners
     */
    private void getGuiElements() {
        Button returnBtn = findViewById(R.id.returnJoinBtn);
        gameIdNpt = findViewById(R.id.gameIdNpt);
        Button joinIdBtn = findViewById(R.id.joinIdBtn);

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        joinIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check game id validity
                String id = Objects.requireNonNull(gameIdNpt.getText()).toString();
                if (isIdValid(id)) {
                    // If id is valid start new intent
                    Intent joinTeamGameIntent = new Intent(JoinGameSettingActivity.this,
                            JoinGameTeamActivity.class);
                    joinTeamGameIntent.putExtra("id", id);

                    JoinGameSettingActivity.this.startActivity(joinTeamGameIntent);
                    finish();
                } else {
                    // Inform user that id was not found
                    Toast.makeText(JoinGameSettingActivity.this,
                            "Peli tunnusta " + id + " ei löytynyt",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Checks that game has been created with id
     * @param id String game id to test
     * @return boolean true if id is valid, else false
     */
    private boolean isIdValid(String id) {
        if (id.isEmpty()) {
            return false;
        }
        return games.contains(id);
    }

    /**
     * Gets all game ids from fireStoreReader
     */
    private void getGames() {
        QuerySnapshot qs = fsReader.getFireStoreEntries();

        System.out.println("getGames");
        for (QueryDocumentSnapshot doc : qs) {
            games.add(doc.getString("id"));
        }
    }
}