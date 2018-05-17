/**
 * @Author Koltsova Anna-Maria
 * @since 30-06-2017
 * @version 2
 * Сontroller for MVC GUI. All of the core processes happen here.
 */

package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {
    private String newline = "\n";
    private String colon = ":";
    private String type = "Type of Dice: ";
    private String face = " faces";
    private int score;
    private int flag = 0;
    private int delayTime = 0;
    private boolean running = false;
    private int userChoice;
    private OutputStream writer = null;
    private int data;
    private int rollCounter=0;
    private boolean reset=false;
    private int countingRolls=0;
    @FXML
    private Button sixFace;
    @FXML
    private Button twelveFace;
    @FXML
    private Button twentyFace;
    @FXML
    private Label labelChoice;
    @FXML
    private ImageView displayChoice;
    @FXML
    private Slider timeSlider;
    @FXML
    private Label timeDisplay;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button resetButton;
    @FXML
    private BarChart<String, Integer> barChart;
    @FXML
    private TableView<TableElement> table;
    @FXML
    private TableColumn<TableElement, Integer> scoreColumn;
    @FXML
    private TableColumn<TableElement, Integer> casesColumn;
    private ObservableList<TableElement> caseTables = FXCollections.observableArrayList();
    @FXML
    private LineChart<Integer, Double> lineChart;
    @FXML
    private NumberAxis rollsLineChart;
    @FXML
    private NumberAxis averageLineChart;
    private Dice dice = new Dice();

    /**
     * This method is used for Six Face dice button and actions uppon pressing.
     * The program will recive data for number of faces.
     */
    public void handleSixFace() {
        if (!running) {
            dice.setNumberOfFaces(6);
            labelChoice.setText("You have chosen 6 faces");
            userChoice = 6;
        }
    }

    /**
     * This method is used for 12 Face dice option. The program will recieve data for number of faces.
     */

    public void handleTwelveFace() {
        if (!running) {
            dice.setNumberOfFaces(12);
            labelChoice.setText("You have chosen 12 faces");
            userChoice = 12;
        }
    }

    /**
     * This method is used for 20 Face dice option. The program will recive data for number of faces.
     */

    public void handleTwentyFace() {
        if (!running) {
            dice.setNumberOfFaces(20);
            labelChoice.setText("You have chosen 20 faces");
            userChoice = 20;
        }
    }

    /**
     * This method is to handle slider to set the time.The variable Timer will then be transferred to the method handlestartButton
     * to affect the sleep time of the thread.
     */
    public void handleSliderTimer(){
        timeDisplay.setText(String.valueOf((int)timeSlider.getValue()) +" miliseconds");
        delayTime = (int)timeSlider.getValue();}

    /**
     * In this method main actions happen. When start button is pressed the program
     * will create a thread. Platfrom thead is used as a shield to protect.
     * Program will create an average line chart. Write position to position log and all sorted data for the table to rolls
     * log and all of the data to all_rolls log. Table log is used for table update. Start button removes all the previous data from arraylists.
     */
    public void handleStartButton(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    running = true; //tell the program that the thread is running
                    countingRolls =0;
                    dice.removeList();
                    dice.removeRollingData();
                    dice.setTypeOfDice(userChoice);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {//start anther protective thread
                            startButton.setText("Running...");
                        }
                    });


                    Platform.runLater(() -> barChart.getData().clear());
                    XYChart.Series<Integer,Double> seriesLineChart = new XYChart.Series<>();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            lineChart.getData().addAll(seriesLineChart);
                        }
                    });
                    dice.frequency();
                    XYChart.Series<String, Integer> series = new XYChart.Series<>();
                    for(int index = 0; index < userChoice; index++){
                        String name = String.valueOf(index+1);
                        series.getData().addAll(new XYChart.Data<>(name, 0));
                    }
                    Platform.runLater((new Runnable() {
                        @Override
                        public void run() { barChart.getData().addAll(series);}
                    }));
                    while (true) {
                        if(flag == 0) {//flag to separeate while and if loop for pause button
                            //keep generating number
                            String name = String.valueOf(score);
                            File file = new File("/Users/coltsoff/IdeaProjects/Anna/src/"+name+".png");
                            Image image = new Image(file.toURI().toString());
                            displayChoice.setImage(image);
                            countingRolls++;
                            score = dice.getDiceNumber();
                            dice.addDiceNumber(score);
                            System.out.println(dice.listReturn().get(rollCounter));
                            Platform.runLater((new Runnable() {
                                @Override
                                public void run() {
                                    seriesLineChart.getData().add(new XYChart.Data<>(countingRolls, dice.calculateAverage()));
                                }
                            }));
                            try {
                                Thread.sleep(delayTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while(flag == 1) {
                            try {
                                Thread.sleep(10); //sleep until the flag is 0 which means resume
                                if(reset){
                                    reset = false;
                                    break;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        dice.removeRollingData();
                        dice.frequency();//count duplicate cases

                        try {
                            writer = new FileOutputStream("Table.log",false);//over-write everytime
                            for(int index = 1; index <= dice.rollingDatalist().size() - 1; index++) {
                                String in = String.valueOf(index);
                                String data = String.valueOf((int) dice.rollingDatalist().get(index));
                                writer.write(in.getBytes());
                                writer.write(colon.getBytes());
                                writer.write(data.getBytes());
                                writer.write(newline.getBytes());
                            }
                            Platform.runLater((new Runnable() {
                                @Override
                                public void run() { updateTable();}
                            }));
                            Platform.runLater((new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        int column = 0;
                                        FileInputStream readLog = new FileInputStream("Table.log");
                                        BufferedReader readFile = new BufferedReader(new InputStreamReader(readLog));
                                        while (true) {
                                            String read = readFile.readLine();
                                            if (read == null) {
                                                break;
                                            }
                                            Integer data = Integer.parseInt(read.substring(read.indexOf(':') + 1, read.length()));
                                            if (series.getData().get(column).getYValue().intValue() != data) {
                                                series.getData().get(column).setYValue(data);
                                            }
                                            column++;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } }));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(reset) {

                            reset = false;
                            break;
                        }
                    }

                    try {
                        int lineCount = 0, commentsCount = 0;
                        Scanner input = new Scanner(new File("Rolls.log"));
                        while (input.hasNextLine()) {
                            String data = input.nextLine();
                            if (data.startsWith("//")) commentsCount++;
                            lineCount++;
                        }
                        System.out.println("Line Count: " + lineCount + "\t Comments Count: " + commentsCount);
                        writer = new FileOutputStream("Positions.log",false); //over-write every times it runs
                        writer.write(String.valueOf(lineCount).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        writer = new FileOutputStream("Rolls.log",true);
                        writer.write(newline.getBytes());
                        writer.write(type.getBytes());
                        String numberOfFaces = String.valueOf(dice.getTypeOfDice());
                        writer.write(numberOfFaces.getBytes());
                        writer.write(face.getBytes());
                        for(int index = 1; index <= dice.rollingDatalist().size() - 1; index++) {
                            String data = String.valueOf((int) dice.rollingDatalist().get(index));
                            String in = String.valueOf(index);
                            writer.write(newline.getBytes());
                            writer.write(in.getBytes());
                            writer.write(colon.getBytes());
                            writer.write(data.getBytes());
                        }
                        writer.write(newline.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        writer = new FileOutputStream("All_rolls.log",true);
                        writer.write(newline.getBytes());
                        writer.write(type.getBytes());
                        String numberOfFaces = String.valueOf(dice.getTypeOfDice());
                        writer.write(numberOfFaces.getBytes());
                        writer.write(face.getBytes());
                        for(int index = 1; index <= dice.listReturn().size() - 1; index++) {
                            String data = String.valueOf((int) dice.listReturn().get(index));
                            String in = String.valueOf(index);
                            writer.write(newline.getBytes());
                            writer.write(in.getBytes());
                            writer.write(colon.getBytes());
                            writer.write(data.getBytes());
                        }
                        writer.write(newline.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for(int index = 0; index < dice.rollingDatalist().size() - 1; index++) {
                        System.out.print("'");
                        System.out.print(index + 1);
                        System.out.println("': " + dice.rollingDatalist().get(index + 1));
                    }
                    Platform.runLater((new Runnable() {
                        @Override
                        public void run() {
                            drawBarChart();
                            createTable();
                            startButton.setText("Start");
                        }}));
                    running = false; //tell the program that the thread stopped
                }
            }
        });
        if(!running) {//prevent creating another thread
            startButton.setText("Running...");
            clearLineChart();
            thread.start();
            startButton.setText("Start");
        }
    }
    /**
     *This method will act uppon user pressing Pause button.Flag controlls threads.  The Lable text will change.
     * This method is responsible for the variable with controlls threads.
     */

    public void handlePauseButton() {
        if (running) {
            flag++;
            if (flag == 1) {
                pauseButton.setText("Resume");
            }
            if (flag == 2) {
                pauseButton.setText("Pause");
                flag = 0;
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {//someknid of constructor
        File file = new File("/Users/coltsoff/IdeaProjects/Anna/src/sample/Button 6 Faces.png");
        Image image = new Image(file.toURI().toString());
        sixFace.setGraphic(new ImageView(image));
        file = new File("/Users/coltsoff/IdeaProjects/Anna/src/sample/Button 12 Faces.png");
        image = new Image(file.toURI().toString());
        twelveFace.setGraphic(new ImageView(image));
        file = new File("/Users/coltsoff/IdeaProjects/Anna/src/sample/Button 20 Faces.png");
        image = new Image(file.toURI().toString());
        twentyFace.setGraphic(new ImageView(image));
        timeDisplay.setText(String.valueOf((int)timeSlider.getValue()) +" miliseconds");
        dice.setNumberOfFaces(6);
        labelChoice.setText("Default choice is 6 faces");
        labelChoice.setAlignment(Pos.CENTER);
        userChoice = 6;
        delayTime = (int)timeSlider.getValue();
        barChart.setAnimated(false);
        rollsLineChart.setAutoRanging(false);
        rollsLineChart.setLowerBound(0);
        rollsLineChart.setUpperBound(100);
        averageLineChart.setLowerBound(0);
        averageLineChart.setUpperBound(20);
        lineChart.setCreateSymbols(false);
        drawBarChart();
        createTable();
    }



    /**
     * This method is to draw the BarChar by reading through log file.
     */

    public void drawBarChart() {
        barChart.getData().clear();
        try {
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            FileInputStream readLog = new FileInputStream("Positions.log");
            BufferedReader reader = new BufferedReader(new InputStreamReader(readLog));
            int position = Integer.parseInt(reader.readLine()); //get the latest position
            System.out.println(position);
            readLog = new FileInputStream("Rolls.log");
            BufferedReader readLogFile = new BufferedReader(new InputStreamReader(readLog));
            for (int index = 0; index < position + 2; index++) { //loop through the previous log doing nothing
                readLogFile.readLine();
            }
            while (true) { //start processing data
                String read = readLogFile.readLine();
                if (read == null) {
                    break;
                } else {
                    System.out.println(read);
                    String name = read.substring(0, read.indexOf(':'));
                    data = Integer.parseInt(read.substring(read.indexOf(':') + 1, read.length()));//returns integer values from the file
                    series.getData().addAll(new XYChart.Data<>(name, data));
                }
            }
            series.setName("Duplicate cases");
            barChart.setLegendSide(Side.TOP);
            barChart.getData().addAll(series);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is to create a table. data is read through log file. The integers are turned to table
     * property value added to an observable list and then displayed.
     */

    private void createTable(){
        table.getItems().clear();
        int index = 1;
        try {
            FileInputStream readLog = new FileInputStream("Positions.log");
            BufferedReader reader = new BufferedReader(new InputStreamReader(readLog));
            int position = Integer.parseInt(reader.readLine());
            System.out.println(position);
            readLog = new FileInputStream("Rolls.log");
            BufferedReader readLogFile = new BufferedReader(new InputStreamReader(readLog));
            for(int pos = 0; pos < position + 2; pos++) {
                readLogFile.readLine();
            }
            while (true) {
                String read = readLogFile.readLine();
                if(read == null) {
                    break;
                } else {
                    System.out.println(read);
                    Integer data = Integer.parseInt(read.substring(read.indexOf(':') + 1,read.length()));
                    caseTables.add(new TableElement(index, data));
                }
                index++;
            }
            scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
            scoreColumn.setStyle("-fx-alignment: CENTER;");
            casesColumn.setCellValueFactory(new PropertyValueFactory<>("cases"));
            casesColumn.setStyle("-fx-alignment: CENTER;");
            table.setItems(null);
            table.setItems(caseTables);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to react to a user action of pressing the reset button which will reset the charts terminate completely the previous rolls.
     * Only works when thread is running.
     */

    public void handleReset() {
        if(running){
            reset = true;

        }
    }

    /**
     * This method will update the Table by using a log file line by line.
     */

    private void updateTable(){
        table.getItems().clear();
        try {
            int index = 1;
            FileInputStream readLog = new FileInputStream("Table.log");
            BufferedReader readFile = new BufferedReader(new InputStreamReader(readLog));
            while (true) {
                String read = readFile.readLine();
                if(read == null) {
                    break;
                }
                System.out.println(read);
                Integer data = Integer.parseInt(read.substring(read.indexOf(':') + 1,read.length()));
                caseTables.add(new TableElement(index, data));
                index++;
            }
            scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
            scoreColumn.setStyle("-fx-alignment: CENTER;");
            casesColumn.setCellValueFactory(new PropertyValueFactory<>("cases"));
            casesColumn.setStyle("-fx-alignment: CENTER;");
            table.setItems(null);
            table.setItems(caseTables);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will remove all data from the lineChart.
     */
    private void clearLineChart() {
        lineChart.getData().clear();
    }
}
