/**
 * Created by koltsova on 28/06/17.*/
package sample;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dice {
    private ArrayList<Integer> list = new ArrayList<>();
    private int numberOfFaces;
    private ArrayList rollingData = new ArrayList<>();
    private int TypeOfDice;


    void setTypeOfDice(int typeOfDice) {
        TypeOfDice = typeOfDice;
    }


    int getTypeOfDice() {
        return TypeOfDice;
    }


    void setNumberOfFaces(int numberOfFaces) {
        this.numberOfFaces = numberOfFaces;
    }


    int getDiceNumber() {
        int diceNumber = new Random().nextInt(this.numberOfFaces) + 1;
        return diceNumber;
    }


    void addDiceNumber(int number) {
        list.add(number);
    }



    ArrayList listReturn() {
        return this.list;
    }

    void frequency() {
        for(int index = 0; index <= numberOfFaces; index++) {
            rollingData.add(index,Collections.frequency(list,index));
        }
    }



    ArrayList rollingDatalist() {
        return this.rollingData;
    }

    void removeList() {
        list.clear();
    }

    void removeRollingData() {
        rollingData.clear();
    }


    public double calculateAverage() {
        if (list == null) {
            return 0;
        }
        double sum = 0;
        for (Integer mark : list) {
            sum += mark;
        }
        return sum / list.size();
    }
}

