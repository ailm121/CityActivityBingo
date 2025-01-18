package com.android.example.bingoroadtripfinland;

import static java.lang.Math.sqrt;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameMainActivity extends AppCompatActivity {

    private Game game;
    private ArrayMap<String, Team> teams;

    private TableLayout gridContainer;
    private ArrayMap<String, Button> taskButtons;
    private TextView winnerTxtvw;

    private FireStoreWriter fsWriter;
    private FireStoreReader fsReader;
    private BingoChecker bChecker;

    /**
     * Activity for main view of the game. View contains the bingo grid.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        fsWriter = new FireStoreWriter();
        fsReader = new FireStoreReader();

        Intent intent = getIntent();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                createGame(intent);
                getGuiElements();
                setCompletedTasksColors();
                bChecker = new BingoChecker(game.getTasks());
                checkBingos(null);
            }
        }, 1000);
        setFireStoreChangeListener();
    }

    /**
     * Starts game based on intent extras
     * @param intent Intent that was used to start the GameMainActivity
     */
    private void createGame(Intent intent) {

        if (intent.getExtras().size() == 1) {
            // Game was started through new game button
            // Game
            game = (Game) intent.getParcelableExtra("game");
            fsWriter.addGame(game);

            // Teams
            teams = new ArrayMap<>();
            int teamCounter = 0;
            for (String teamName : game.getTeams()) {
                boolean thisUser = false;
                if (teamCounter == 0) {
                    thisUser = true;
                }
                Team team = new Team(teamName, teamCounter, thisUser);
                teamCounter++;
                teams.put(teamName, team);
            }

        } else {
            // Game was started through join game button
            setGameDetails(getIntent().getStringExtra("id"),
                    getIntent().getStringExtra("team"));
        }

    }

    /**
     * Retrieves GUI elements and sets listeners
     */
    private void getGuiElements() {
        gridContainer = findViewById(R.id.gridContainer);
        TextView teamsTxtVw = findViewById(R.id.teamsTxtVw);
        TextView gameIdTxtVw = findViewById(R.id.gameIdTxtVw);

        // Button for going back to starting view
        Button quitBtn = findViewById(R.id.quitBtn);
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        createGrid();

        // all team names
        StringBuilder teamsText = new StringBuilder();
        for (Team t : teams.values()) {
            teamsText.append("<font color=").append(t.getColor()).append(">").append(t.getName()).append("</font> ");
        }
        teamsTxtVw.setText(Html.fromHtml(teamsText.toString(), Html.FROM_HTML_MODE_LEGACY));

        // game id
        gameIdTxtVw.setText("Peli tunnus: " + game.getId());

        // textView for announcing an bingo
        winnerTxtvw = findViewById(R.id.winnerTxtVw);
        winnerTxtvw.setText("");
    }

    /**
     * Makes grid of buttons to tableLayout with games tasks and gridSize
     */
    private void createGrid() {
        this.taskButtons = new ArrayMap<>();
        Button[][] taskButtons = new Button[5][5];
        int taskIndex = 0;

        // Add buttons with task names to create a grid for bingo
        for (int row = 0; row < sqrt(game.getGridSize()); row++) {

            TableRow currentRow = new TableRow(GameMainActivity.this);
            currentRow.setGravity(Gravity.CENTER);

            for (int btn = 0; btn < sqrt(game.getGridSize()); btn++) {

                Button currentBtn = new Button(GameMainActivity.this);
                currentBtn.setWidth((int) (960/sqrt(game.getGridSize())));
                currentBtn.setHeight((int) (960/sqrt(game.getGridSize())));
                currentBtn.setText(game.getTasks().get(taskIndex));

                // when task is clicked it shows more details in an other activity
                currentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent taskDetailsIntent = new Intent(GameMainActivity.this,
                                TaskDetailsActivity.class);
                        taskDetailsIntent.putExtra("name", currentBtn.getText());
                        startActivityForResult(taskDetailsIntent, 101);
                    }
                });

                taskButtons[row][btn] = currentBtn;
                currentRow.addView(currentBtn);

                this.taskButtons.put(currentBtn.getText().toString(), currentBtn);
                taskIndex++;
            }
            // Add button row to tableLayout
            gridContainer.addView(currentRow);
        }
    }

    /**
     * Gets game details from fire store and sets up a game based on them
     * @param id String game id
     * @param team String team name of this user
     */
    private void setGameDetails(String id, String team) {
        game = fsReader.getFireStoreGame(id);

        int teamCounter = 0;
        boolean thisUser = false;

        this.teams = new ArrayMap<>();

        for (String teamName : game.getTeams()) {
            thisUser = teamName.equals(team);
            Team newTeam = new Team(teamName, teamCounter, thisUser);
            teamCounter++;
            this.teams.put(teamName, newTeam);
        }
    }

    /**
     * Sets completed tasks buttons colors visible using custom drawables
     */
    private void setCompletedTasksColors() {

        HashMap<String, ArrayList<String>> teamsTasks = game.getTeamsCompletedTasks();
        HashMap<String, ArrayList<String>> taskColors = new HashMap<>();

        // Get required colors for each task
        for (String key : teamsTasks.keySet()) {
            for (String task : teamsTasks.get(key)) {
                if (taskColors.containsKey(task)) {
                    taskColors.get(task).add(teams.get(key).getColor());
                } else {
                    ArrayList<String> newColor = new ArrayList<>();
                    newColor.add(teams.get(key).getColor());
                    taskColors.put(task, newColor);
                }
            }
        }
        // Set button backgrounds with custom drawables
        for (String taskName : taskColors.keySet()) {
            taskButtons.get(taskName).setBackground(new CustomDrawable(taskColors.get(taskName)));
        }
    }

    /**
     * Operations to mark task completed by team that is currently active on device
     * @param taskName String name of completed task
     */
    private void markTaskCompleted(String taskName) {

        // Identify active team
        Team completingTeam = null;
        for (String tn : teams.keySet()) {
            if (teams.get(tn).getThisTeam()) {
                completingTeam = teams.get(tn);
                break;
            }
        }

        // Check fireStore updates
        game.setCompletedTasks(fsReader.getFireStoreGameCompletedTasks(game.getId()));
        setCompletedTasksColors();
        checkBingos(null);

        // If same task is not allowed to be completed more than once and other team has already
        // completed it make toast to inform users and return
        if (!game.isSameTaskAllowed()) {
            for (ArrayList<String> completed : game.getTeamsCompletedTasks().values()) {
                if (completed.contains(taskName)) {
                    Toast.makeText(GameMainActivity.this,
                            "Tehtävät voidaan suorittaa vain kerran",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        // If team has already completed task do nothing
        if ( game.getTeamsCompletedTasks().get(completingTeam.getName()) != null) {
            if (game.getTeamsCompletedTasks().get(completingTeam.getName()).contains(taskName)) {
                return;
            }
        }

        ArrayList<String> teamsCompleted = new ArrayList<>();

        if (game.getTeamsCompletedTasks() != null) {
            for (String tm : game.getTeamsCompletedTasks().keySet()) {
                if (game.getTeamsCompletedTasks().get(tm).contains(taskName)) {
                    teamsCompleted.add(tm);
                }
            }
        }
        // Add completed task for team in Game class and update FireStore
        game.addCompletedTask(completingTeam.getName(), taskName);
        fsWriter.updateCompletedTasks(game);

        // Update View
        setCompletedTasksColors();
        checkBingos(completingTeam);
    }

    /**
     * Checks if there are any bingos on the grid
     * @param team Team that just added a new completed task, if null check is asked because
     *             FireStore was updated
     */
    private void checkBingos(Team team) {

        // Possible bingo from Fire Store updates
        if (team == null) {
            // check bingos for all teams and set textView with text "BINGO!" in black
            for (Team t : teams.values()) {
                if (bChecker.hasBingo(game.getTeamsCompletedTasks().get(t.getName()))) {
                    winnerTxtvw.setTextColor(Color.BLACK);
                    winnerTxtvw.setText("BINGO!");
                    break;
                }
            }

        } else if (bChecker.hasBingo(game.getTeamsCompletedTasks().get(team.getName()))) {
            // check bingo for specific team and set textView with text "BINGO!" in the teams color
            if (team.getColor().equals("RED")) {
                winnerTxtvw.setTextColor(Color.RED);

            } else if (team.getColor().equals("BLUE")) {
                winnerTxtvw.setTextColor(Color.BLUE);

            } else if (team.getColor().equals("GREEN")) {
                winnerTxtvw.setTextColor(Color.GREEN);

            } else if (team.getColor().equals("MAGENTA")) {
                winnerTxtvw.setTextColor(Color.MAGENTA);
            }
            winnerTxtvw.setText("BINGO!");
        }
    }

    /**
     * If user returns from viewing task details via mark completed button, the task is marked completed
     * @param requestCode int resultCode
     * @param resultCode int resultCode
     * @param data Intent with extras from TaskDetailsActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data.getExtras().size() == 2) {
            String taskName = data.getStringExtra("taskName");
            boolean markCompleted = data.getBooleanExtra("completed", true);
            if (markCompleted) {
                markTaskCompleted(taskName);
            }
        }
    }

    /**
     * Checks FireStoreReader for changes every 5 seconds
     */
    private void setFireStoreChangeListener () {
        Runnable taskUpdatesRunnable = new Runnable() {
            public void run() {
                if (fsReader != null && game != null) {
                    if (fsReader.getFireStoreGameCompletedTasks(game.getId()) != null) {
                        HashMap<String, ArrayList<String>> newMap = fsReader
                                .getFireStoreGameCompletedTasks(game.getId());

                        // Only things that should be changing during a game are completed tasks
                        if (!newMap.equals(game.getTeamsCompletedTasks())) {
                            game.setCompletedTasks(newMap);
                            setCompletedTasksColors();
                            checkBingos(null);
                        }
                    }
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(taskUpdatesRunnable, 0, 5, TimeUnit.SECONDS);
    }
}