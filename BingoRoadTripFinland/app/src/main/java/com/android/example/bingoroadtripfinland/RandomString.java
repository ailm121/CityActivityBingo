package com.android.example.bingoroadtripfinland;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.annotation.Nullable;

// Class for creating random strings
public class RandomString {

    private FireStoreReader mReader;

    /**
     * Class for generating unique random strings
     */
    public RandomString() {
        mReader = new FireStoreReader();
    }

    /**
     * Generates a random string of given size. Checks that string is unique in gameIds
     * @param size length of Id
     * @return String random character sequence
     */
    public String generateRandomGameId(int size) {

        // Possible characters
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789";

        StringBuilder sb = new StringBuilder(size);
        ArrayList<String> oldIds = getOldIds();

        while (oldIds.contains(sb.toString()) || sb.length() == 0) {
            sb.setLength(0);
            // Choose random characters
            for (int i = 0; i < size; i++) {
                int index = (int) (characters.length() * Math.random());
                sb.append(characters.charAt(index));
            }
        }
        return sb.toString();
    }

    /**
     * Gets game ids that are already in use
     * @return ArrayList of used Ids
     */
    private ArrayList<String> getOldIds() {

        QuerySnapshot fireStoreEntries = mReader.getFireStoreEntries();
        ArrayList<String> oldIds = new ArrayList<>();

        for (DocumentSnapshot snapshot : fireStoreEntries) {
            String id = snapshot.getString("id");
            oldIds.add(id);
        }

        return oldIds;
    }
}
