package com.android.example.bingoroadtripfinland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartScreenActivity extends AppCompatActivity {

    /**
     * Activity for choosing to create new game or to join an old game
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        Button newGameBtn = findViewById(R.id.newGameBtn);
        Button joinGameBtn = findViewById(R.id.joinGameBtn);

        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newGameIntent = new Intent(StartScreenActivity.this,
                        NewGameSettingsActivity.class);
                StartScreenActivity.this.startActivity(newGameIntent);
            }
        });

        joinGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinGameIntent = new Intent(StartScreenActivity.this,
                        JoinGameSettingActivity.class);
                StartScreenActivity.this.startActivity(joinGameIntent);
            }
        });
    }
}