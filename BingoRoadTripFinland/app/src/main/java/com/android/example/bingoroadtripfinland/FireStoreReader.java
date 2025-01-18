package com.android.example.bingoroadtripfinland;

import android.util.ArrayMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class FireStoreReader {

    FirebaseFirestore mFireStore;
    private QuerySnapshot snapshots;

    /**
     * Class for fetching firestore entries
     */
    public FireStoreReader () {
        mFireStore = FirebaseFirestore.getInstance();
        changeInquiry();
    }

    /**
     * returns most resent firestore querysnapshot
     * @return QuerySnapshot of firestore collection
     */
    public QuerySnapshot getFireStoreEntries () { return snapshots; }

    public HashMap<String, ArrayList<String>> getFireStoreGameCompletedTasks(String gameId) {
        if (snapshots == null) {
            return null;
        }
        for (QueryDocumentSnapshot doc : snapshots) {
            if (Objects.equals(doc.getString("id"), gameId)) {
                return (HashMap<String, ArrayList<String>>) doc.getData().get("teamsCompletedTasks");
            }
        }
        return null;
    }

    /**
     * returns Game read from Fire Store based on given id
     * @param gameId String id of wanted game
     * @return Game new game based on Fire Store info
     */
    public Game getFireStoreGame(String gameId) {
        String id = "";
        String area = "";
        int gridSize = 9;
        boolean allowSameTask = true;
        List<String> tasks = new ArrayList<>();
        List<String> teams = new ArrayList<>();
        HashMap<String, ArrayList<String>> completedTasks = new HashMap<>();

        // find game details from fireStore
        for (QueryDocumentSnapshot doc : snapshots) {
            if (doc.getString("id").equals(gameId)) {
                id = doc.getString("id");
                area = doc.getString("area");
                gridSize = doc.getLong("gridSize").intValue();
                allowSameTask = doc.getBoolean("sameTaskAllowed");
                tasks = (ArrayList<String>) doc.getData().get("tasks");
                teams = (ArrayList<String>) doc.getData().get("teams");
                completedTasks = (HashMap<String, ArrayList<String>>) doc.getData().get("teamsCompletedTasks");
            }
        }
        if (completedTasks == null) {
            completedTasks = new HashMap<>();
        }

        return new Game(gameId, area, gridSize, allowSameTask, tasks, teams, completedTasks);
    }


    /**
     * Updates snapshots when there are changes in Fire Store
     */
    private void changeInquiry() {
        mFireStore.collection("test_games").addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            snapshots = queryDocumentSnapshots;
                        }
                    }
                });
    }
}
