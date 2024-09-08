package application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;

public class Board extends Application {

    private static final int GRID_SIZE = 5; // 5x6 grid for Wordle
    private static final int MAX_TRIES = 6;
    private int currentRow = 0;
    private int currentCol = 0;
    private String answerWord; // Store the answer word
    private Word wordGenerator; // Word generator
    private Map<Character, Button> keyboardButtons = new HashMap<>();


    @Override
    public void start(Stage primaryStage) {
        try {
            wordGenerator = new Word(); // Initialize Word generator
            answerWord = wordGenerator.getRandomWord(); // Get a random word
            System.out.println(answerWord);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to initialize the word generator or fetch a word.");
            return; // Exit if we can't load a word
        }

        primaryStage.setTitle("Wordle!"); 

        // Main layout container
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #000000;");


        // Title
        Text title = new Text("Wordle");
        title.setStyle("-fx-font-size: 60px; -fx-font-weight: bold; -fx-font-family: 'Comic Sans MS'; -fx-fill: #2A9D8F;");

        // Grid for the letters
        GridPane letterGrid = new GridPane();
        letterGrid.setAlignment(Pos.CENTER);
        letterGrid.setHgap(5);
        letterGrid.setVgap(5);

        // Create grid cells
        for (int row = 0; row < MAX_TRIES; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Label cell = new Label();
                cell.setMinSize(60, 60);
                cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: white; -fx-alignment: center; -fx-font-size: 30px;");
                letterGrid.add(cell, col, row);
            }
        }

        // Keyboard
        String[] rows = {"QWERTYUIOP", "ASDFGHJKL"};
        VBox keyboard = new VBox(6);
        for (int i = 0; i < rows.length; i++) {
            HBox keyRow = new HBox(5);
            keyRow.setAlignment(Pos.CENTER);
            for (char c : rows[i].toCharArray()) {
                Button key = new Button(String.valueOf(c));
                key.setMinSize(50, 50);
                key.setOnAction(e -> handleKeyPress(c, letterGrid));
                keyRow.getChildren().add(key);
                keyboardButtons.put(c, key);
            }
            keyboard.getChildren().add(keyRow);
        }

        // Add ENTER key in the middle
        HBox enterKeyRow = new HBox(5);
        enterKeyRow.setAlignment(Pos.CENTER);
        Button enterKey = new Button("ENTER");
        enterKey.setMinSize(50, 50);
        enterKey.setOnAction(e -> handleEnter(letterGrid));
        enterKeyRow.getChildren().add(enterKey);

        // Add ZXCVBNM row
        HBox zxcvbnmRow = new HBox(5);
        zxcvbnmRow.setAlignment(Pos.CENTER);
        String zxcvbnm = "ZXCVBNM";
        zxcvbnmRow.getChildren().add(enterKeyRow);
        for (char c : zxcvbnm.toCharArray()) 
        {
            Button key = new Button(String.valueOf(c));
            key.setMinSize(50, 50);
            key.setOnAction(e -> handleKeyPress(c, letterGrid));
            zxcvbnmRow.getChildren().add(key);
            keyboardButtons.put(c, key);
        }
        // Add backspace key
        HBox backspaceKeyRow = new HBox(5);
        backspaceKeyRow.setAlignment(Pos.CENTER);
        Button backspaceKey = new Button("⌫");
        backspaceKey.setMinSize(50, 50);
        backspaceKey.setOnAction(e -> handleBackspace(letterGrid));
        backspaceKeyRow.getChildren().add(backspaceKey);
        
        zxcvbnmRow.getChildren().add(backspaceKeyRow);
        keyboard.getChildren().add(zxcvbnmRow);

        // Keyboard input handling
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKeyboardInput(event, letterGrid));

        root.getChildren().addAll(title, letterGrid, keyboard);
        Scene scene = new Scene(root, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleKeyPress(char c, GridPane grid) {
        if (currentCol < GRID_SIZE) {
            Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + currentCol);
            cell.setText(String.valueOf(c));
            currentCol++;
        }
    }

    private void handleEnter(GridPane grid) 
    {
        

        if (currentCol == GRID_SIZE) {
            StringBuilder currentWord = new StringBuilder();
            
            for (int col = 0; col < GRID_SIZE; col++) 
            {
                Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + col);
                currentWord.append(cell.getText());
            }
            
            if(!wordGenerator.checkIfInList(currentWord.toString()))
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid Word");
                alert.setHeaderText(null);
                alert.setContentText("Not in word list.");
                alert.showAndWait();
                return; // Prevent further processing when the word is not valid
            }
            
            List<String> result = wordGenerator.checkWordOrder(currentWord.toString(), answerWord);
            System.out.println(result);
            
            for (int col = 0; col < GRID_SIZE; col++) 
            {
                Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + col);
                char guessChar = currentWord.charAt(col);
                char resultChar = result.get(col).charAt(0);
                if (resultChar == guessChar) 
                {
                    cell.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-border-width: 2; -fx-alignment: center; -fx-font-size: 30px;");
                } 
                else if (wordGenerator.checkInWord(guessChar, answerWord)) 
                {
                    cell.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 2; -fx-alignment: center; -fx-font-size: 30px;");
                } 
                else 
                {
                    cell.setStyle("-fx-background-color: grey; -fx-border-color: black; -fx-border-width: 2; -fx-alignment: center; -fx-font-size: 30px;");
                    if (keyboardButtons.containsKey(guessChar)) {
                        Button keyButton = keyboardButtons.get(guessChar);
                        if (keyButton != null) {
                            keyButton.setStyle("-fx-background-color: grey; -fx-border-color: black;");
                            keyButton.setDisable(true); // Disable the button
                        }
                    }
                }
            }
            
            if (currentWord.toString().equalsIgnoreCase(answerWord)) 
            {
                System.out.println("Correct!");
                // Additional logic for game completion
            } else {
                System.out.println("Try again!");
                if (currentRow < MAX_TRIES - 1) {
                    currentRow++;
                    currentCol = 0;
                } else {
                    System.out.println("Game Over! The correct word was: " + answerWord);
                }
            }
        }
    }

    private void handleBackspace(GridPane grid) {
        if (currentCol > 0) {
            currentCol--;
            Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + currentCol);
            cell.setText("");
        }
    }

    private void handleKeyboardInput(KeyEvent event, GridPane grid) {
        switch (event.getCode()) {
            case ENTER:
                handleEnter(grid);
                break;
            case BACK_SPACE:
                handleBackspace(grid);
                break;
            default:
                if (event.getText().matches("[a-zA-Z]")) {
                    char inputChar = event.getText().toUpperCase().charAt(0);
                    if (keyboardButtons.containsKey(inputChar)) {
                        Button keyButton = keyboardButtons.get(inputChar);
                        if (keyButton.getStyle().contains("grey")) {
                            // If the button is greyed out, do nothing and return
                            return;
                        }
                    }
                    handleKeyPress(inputChar, grid);
                }
                break;
        }
        event.consume(); // Consume the event to prevent default handling
    }

    public static void main(String[] args) {
        launch(args);
    }

}