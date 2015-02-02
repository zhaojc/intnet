package models;

/**
 * Created by christoffer.gunning on 2015-01-29.
 */
public class GuessData {
    int goal;
    int nrOfGuesses;

    public GuessData(int goal) {
        this.goal = goal;
        this.nrOfGuesses = 0;
    }

    public int guess(int guess) {
        nrOfGuesses++;
        return guess - goal;
    }

    public int getNrOfGuesses() {
        return nrOfGuesses;
    }
}
