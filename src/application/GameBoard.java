package application;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;

public class GameBoard extends Application {

    private static final int GRID_SIZE = 5; // 5x6 grid for Wordle
    private static final int MAX_TRIES = 6;
    private int currentRow = 0;
    private int currentCol = 0;
    private String answerWord; // Store the answer word
    private Word wordGenerator; // Word generator
    private StringBuilder currentWord;
    private Map<Character, Button> keyboardButtons = new HashMap<>();
    private int count; 
    GridPane letterGrid = new GridPane();
    Player player;
    Stats playerStats; 
    Text playerNameDisplay; 
    

    @Override
    public void start(Stage primaryStage) {
        try {
            wordGenerator = new Word(); // Initialize Word generator
            answerWord = wordGenerator.getRandomWord(); // Get a random word
            //answerWord = "study"; // to show loss case 
            
            
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
        letterGrid.setAlignment(Pos.TOP_CENTER);
        letterGrid.setHgap(2);
        letterGrid.setVgap(2);
        

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
        enterKey.setMinSize(50, 50); // Increased width to make the ENTER key more visible
        enterKey.setOnAction(e -> handleEnter(letterGrid));
        enterKeyRow.getChildren().add(enterKey);
        
       

        // Add ZXCVBNM row
        HBox zxcvbnmRow = new HBox(5);
        zxcvbnmRow.getChildren().add(enterKeyRow); 
        zxcvbnmRow.setAlignment(Pos.CENTER);
        String zxcvbnm = "ZXCVBNM";
        for (char c : zxcvbnm.toCharArray()) {
            Button key = new Button(String.valueOf(c));
            key.setMinSize(50, 50);
            key.setOnAction(e -> handleKeyPress(c, letterGrid));
            zxcvbnmRow.getChildren().add(key);
            keyboardButtons.put(c, key);
        }

        // Add backspace key
        Button backspaceKey = new Button("⌫");
        backspaceKey.setMinSize(50, 50);
        backspaceKey.setOnAction(e -> handleBackspace(letterGrid));
        zxcvbnmRow.getChildren().add(backspaceKey);

        // Add the entire row to the keyboard
        keyboard.getChildren().add(zxcvbnmRow);

        // Create an empty row for formatting and design
        HBox emptyRow = new HBox();
        emptyRow.setMinHeight(200); // Set minimum height to maintain consistent spacing
        keyboard.getChildren().add(emptyRow);

        keyboard.setAlignment(Pos.CENTER);

        // Keyboard input handling
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKeyboardInput(event, letterGrid));
        


        root.getChildren().addAll(title, letterGrid, keyboard);
        Scene scene = new Scene(root, 800, 1400);
        primaryStage.setScene(scene);
        primaryStage.show();

    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Player Information: ");
    dialog.setHeaderText("Welcome to Harsh's Wordle!");
    dialog.setContentText("Please enter your username:");
    dialog.initStyle(StageStyle.UTILITY);
    dialog.getDialogPane().setStyle("-fx-font-family: 'Comic Sans MS'; -fx-font-size: 14px;");
    
    Optional<String> result = dialog.showAndWait();
    player = new Player("");
    playerStats = new Stats("",0,0,0);
    String name = result.orElse("");
    if (!name.isEmpty()) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        player.setUserName(name); // Convert name to uppercase and space out letters
        playerNameDisplay = new Text("\n\n\n\n\n\n\n\n\n\n\n\n Player: " + player.getUserName() + "\n" 
                + " Wins: " + playerStats.getWins() + "\n" 
                + " Loss: " + playerStats.getLoss() + "\n" 
                + " Average Guesses: " + playerStats.getAverageGuesses());
        playerNameDisplay.setStyle("-fx-font-size: 15px; -fx-font-family: 'Courier New'; -fx-fill: #FFFF00; -fx-letter-spacing: 2px;"); // Use Orbitron font and space out letters
        HBox playerNameBox = new HBox(playerNameDisplay);
        playerNameBox.setAlignment(Pos.TOP_LEFT);
        root.getChildren().add(0, playerNameBox); // Add at the top of the root container

    }

//    winsDisplay = new Text(" Wins: " + playerStats.getWins());
//    winsDisplay.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-fill: #FFFF00; -fx-letter-spacing: 2px;");
//    HBox winsBox = new HBox(winsDisplay);
//    winsBox.setAlignment(Pos.TOP_LEFT);
//    root.getChildren().add(1, winsBox); // Add below the player's name
//
//    lossesDisplay = new Text(" Loss: " + playerStats.getLoss());
//    lossesDisplay.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-fill: #FFFF00; -fx-letter-spacing: 2px;");
//    HBox lossesBox = new HBox(lossesDisplay);
//    lossesBox.setAlignment(Pos.TOP_LEFT);
//    root.getChildren().add(2, lossesBox); // Add below the wins
//    
//    guessDisplay = new Text(" Average Guesses: " + playerStats.getAverageGuesses());
//    guessDisplay.setStyle("-fx-font-size: 14px; -fx-font-family: 'Courier New'; -fx-fill: #FFFF00; -fx-letter-spacing: 2px;");
//    HBox guessBox = new HBox(guessDisplay);
//    guessBox.setAlignment(Pos.TOP_LEFT);
//    root.getChildren().add(3, guessBox); // Add below the wins

    // Add a reset button
    Button resetButton = new Button("  Reset  ");
    resetButton.setOnAction(e -> resetBoard(letterGrid));
    resetButton.setStyle("-fx-background-color: #FF4500; -fx-font-size: 16px; -fx-padding: 14px;"); // Set the button color to red and increase font size and padding
    HBox resetButtonBox = new HBox(resetButton);
    resetButtonBox.setAlignment(Pos.CENTER_RIGHT);
    root.getChildren().add(1,resetButtonBox); // Add below the losses
//    
    
   
}
 

    private void resetBoard(GridPane grid) 
    {
        // Remove all children from the grid
        letterGrid.getChildren().clear();

        // Recreate the grid with new cells
        for (int row = 0; row < MAX_TRIES; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Label cell = new Label();
                cell.setMinSize(60, 60);
                cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: white; -fx-alignment: center; -fx-font-size: 30px;");
                letterGrid.add(cell, col, row);
            }
        }
    // Reset current row and column to start positions
    currentRow = 0;
    currentCol = 0;
    // Enable all keyboard buttons and reset their styles
    for (Button key : keyboardButtons.values()) 
    {
        key.setDisable(false);
        key.setStyle("-fx-background-color: white; -fx-border-color: black;");
    }
    answerWord = wordGenerator.getRandomWord();
	}
    

    private void handleKeyPress(char c, GridPane grid) 
    {
        if (currentCol < GRID_SIZE) {
            Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + currentCol);
            cell.setText(String.valueOf(c));
            currentCol++;
        }
    }

    private void handleEnter(GridPane grid) 
    {
        if (currentCol == GRID_SIZE) 
        {
            
        	currentWord = new StringBuilder();
            
            for (int col = 0; col < GRID_SIZE; col++) 
            {
                Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + col);
                currentWord.append(cell.getText());
            }
            
            if(!wordGenerator.checkIfInList(currentWord.toString()))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Word");
                alert.setHeaderText(null);
                alert.setContentText("Not in word list.");
                alert.showAndWait();
                return; // Prevent further processing when the word is not valid
            }
            
            List<String> result = wordGenerator.checkWordOrder(currentWord.toString(), answerWord);
            
            Set<Character> usedChars = new HashSet<>();
            for (int col = 0; col < GRID_SIZE; col++) 
            {
                Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + col);
                char guessChar = currentWord.charAt(col);
                char resultChar = result.get(col).charAt(0);
                if (resultChar == guessChar) 
                {
                    cell.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-border-width: 2; -fx-alignment: center; -fx-font-size: 30px;");
                    usedChars.add(guessChar);
                } 
                else if (wordGenerator.checkInWord(guessChar, answerWord) && !usedChars.contains(guessChar)) 
                {
                    cell.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 2; -fx-alignment: center; -fx-font-size: 30px;");
                    usedChars.add(guessChar);
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
            // Re-check disabled keys and unlock them if the letter is still in the word
            for (Map.Entry<Character, Button> entry : keyboardButtons.entrySet()) {
                char keyChar = entry.getKey();
                Button keyButton = entry.getValue();
                if (keyButton.isDisabled() && wordGenerator.checkInWord(keyChar, answerWord)) {
                    keyButton.setDisable(false);
                    keyButton.setStyle("-fx-background-color: lightgrey; -fx-border-color: black;");
                }
            }
            }
            if (currentWord.toString().equalsIgnoreCase(answerWord)) 
                {
            		count += 1; 
            		playerStats.addWins();
                    playerStats.setAverageGuesses(count);
                    updateScoreDisplay();
                    Alert winAlert = new Alert(null);
                    winAlert.setTitle("Congratulations!");
                    winAlert.setHeaderText("Fantastic! You've won!");
                    winAlert.setContentText("You've guessed the word correctly! Celebrate your victory!");
                    winAlert.initStyle(StageStyle.UTILITY);
                    winAlert.getDialogPane().setStyle("-fx-background-color: linear-gradient(to right, #00b09b, #96c93d); -fx-font-family: 'Comic Sans MS'; -fx-font-size: 16px; -fx-text-fill: #ffffff;");
                    ButtonType resetButton = new ButtonType("Play Again");
                    winAlert.getButtonTypes().setAll(resetButton);
                    winAlert.setX(440); // Set X position
                    winAlert.setY(620); // Set Y position
                    Optional<ButtonType> resultWin = winAlert.showAndWait();
                    if (resultWin.isPresent() && resultWin.get() == resetButton) {
                        resetBoard(grid); // Fetch a new word
                    }
                    return; // Exit the method to end the game immediately after a win
                }
            else if (currentRow < MAX_TRIES - 1) 
            {
                currentRow++;
                currentCol = 0;
                count += 1;
            } 
            else 
                {
            		count += 1; 
                    playerStats.addLoss();
                    updateScoreDisplay();
                    Alert lossAlert = new Alert(AlertType.ERROR);
                    lossAlert.setTitle("All Attempts Over");
                    lossAlert.setHeaderText("You Lost, the correct word was: ");
                    lossAlert.setX(450); // Set X position
                    lossAlert.setY(900); // Set Y position
                    lossAlert.setContentText("\t\t\t" + answerWord.toUpperCase());
                    lossAlert.initStyle(StageStyle.UTILITY);
                    lossAlert.getDialogPane().setStyle("-fx-background-color: green; -fx-font-family: 'Comic Sans MS'; -fx-font-size: 18px; -fx-text-fill: #ffffff;");
                    ButtonType tryAgainButton = new ButtonType("Try Again!");
                    
                    lossAlert.getButtonTypes().setAll(tryAgainButton);
                    lossAlert.showAndWait();
                    resetBoard(grid);
                    answerWord = wordGenerator.getRandomWord(); // Fetch a new word
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
                        if (keyButton.isDisabled()) {
                            // If the button is disabled, do nothing and return
                            return;
                        }
                    }
                    handleKeyPress(inputChar, grid);
                    break;
                }
        }
        // Re-check disabled keys and unlock them if the letter is still in the word
        for (Map.Entry<Character, Button> entry : keyboardButtons.entrySet()) {
            char keyChar = entry.getKey();
            Button keyButton = entry.getValue();
            if (keyButton.isDisabled() && wordGenerator.checkInWord(keyChar, answerWord)) {
                keyButton.setDisable(false);
                keyButton.setStyle("-fx-background-color: lightgrey; -fx-border-color: black;");
            }
        }
        
        event.consume(); // Consume the event to prevent default handling
    }

    private void updateScoreDisplay() 
    {
           playerNameDisplay.setText("\n\n\n\n\n\n\n\n\n\n\n\n Player: " + player.getUserName() + "\n" 
                   + " Wins: " + playerStats.getWins() + "\n" 
                   + " Loss: " + playerStats.getLoss() + "\n" 
                   + " Average Guesses: " + playerStats.getAverageGuesses());
           playerNameDisplay.setStyle("-fx-font-size: 15px; -fx-font-family: 'Courier New'; -fx-fill: #FFFF00; -fx-letter-spacing: 2px;"); // Use Orbitron font and space out letters

    }
  
    public static void main(String[] args) {
        launch(args);
    }

}
