package sample;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class TableElement {
    private final IntegerProperty score;
    private final IntegerProperty cases;

    /**
     * Constructor to turn input int values into IntegerProperty values
     * @param score the score of the dice.
     * @param cases number of duplicate cases.
     */
    public TableElement(int score, int cases) {
        this.score = new SimpleIntegerProperty(score);
        this.cases = new SimpleIntegerProperty(cases);
    }

    public int getScore() {
        return score.get();
    }

    public int getCases() {
        return cases.get();
    }
}
