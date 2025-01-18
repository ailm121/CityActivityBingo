package com.android.example.bingoroadtripfinland;

import static java.lang.Math.sqrt;

import java.util.ArrayList;
import java.util.List;

public class BingoChecker {

    ArrayList<List<String>> bingos;

    /**
     * Class for checking if team has managed bingo
     * @param tasks List<String> List of tasks in game
     */
    public BingoChecker(List<String> tasks) {
        bingos = new ArrayList<>();

        // Get possible bingo rows
        for (int i = 0; i < tasks.size(); i += sqrt(tasks.size())) {
            List<String> bingoRow = tasks.subList(i, i + (int) sqrt(tasks.size()));
            bingos.add(bingoRow);
        }

        ArrayList<List<String>> rowBingos = (ArrayList<List<String>>) bingos.clone();

        // Get possible bingo columns
        for (int j = 0; j < sqrt(tasks.size()); j++) {
            List<String> bingoColumn = new ArrayList<>();
            for (List<String> b : rowBingos) {
                bingoColumn.add(b.get(j));
            }
            bingos.add(bingoColumn);
        }
    }

    /**
     * Check if team has gotten bingo by comparing teams completed tasks to each bingo possibility
     * @param completedTasks ArrayList<String> List of completed tasks by some team
     * @return boolean if bingo was found
     */
    public boolean hasBingo( ArrayList<String> completedTasks) {

        // Check if teams completed tasks contain all required tasks of some bingo row or column
        for (List<String> bingo : bingos) {
            if (completedTasks != null) {
                if (completedTasks.containsAll(bingo)) {
                    return true;
                }
            }
        }
        return false;
    }
}
