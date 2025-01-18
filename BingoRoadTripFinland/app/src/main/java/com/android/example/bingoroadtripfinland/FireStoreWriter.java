package com.android.example.bingoroadtripfinland;

import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FireStoreWriter {

    FirebaseFirestore mFireStore;
    private final CollectionReference gameReference;

    /**
     * Class for adding entries to firestore
     */
    public FireStoreWriter() {
        mFireStore = FirebaseFirestore.getInstance();
        gameReference = mFireStore.collection("test_games");
    }

    /**
     * Adds game to firestore
     * @param game Game to be added
     */
    public void addGame(Game game) {
        gameReference.add(game);
    }

    /**
     * Checks whether game exists and deletes it if found
     * @param game Game that is to be deleted
     */
    public void updateCompletedTasks(Game game) {
        Query query = gameReference.whereEqualTo("id", game.getId());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        gameReference.document(doc.getId()).update("teamsCompletedTasks",
                                game.getTeamsCompletedTasks());
                    }
                }
            }
        });
    }
}
