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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * the GameBoard class is the main game interface for the Wordle 
 * handles and updates of the game's UI components(grid, title, stats, reset button)
 */
public class GameBoard extends Application 
{

    // 5x6 grid for Wordle
    private static final int GRID_SIZE = 5; 
    private static final int MAX_TRIES = 6;
    GridPane letterGrid = new GridPane(); 
    
    // where the player is currently guessing 
    private int currentRow = 0; 
    private int currentCol = 0;

    private String answerWord; // Store the answer word
    
    private StringBuilder currentWord; // current guess using StringBuilder
    private Map<Character, Button> keyboardButtons = new HashMap<>(); //way to track the keyboard, helps detect if it has been grey'd out or not 
    private int count; // # of guesses per round 
    
    
    //Objects initalized here so I can use through all methods 
    Word wordGenerator; 
    Player player;
    Stats playerStats; 
    Text playerNameDisplay; 
    
    /**
     * starts the primary stage of the application
     * @param primaryStage The primary stage for this application, this is where the application scene can be set
     */
    @Override
    public void start(Stage primaryStage) 
    {
        try {
            wordGenerator = new Word(); // Initialize Word generator
            answerWord = wordGenerator.getRandomWord(); // Get a random word 
            
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            System.out.println("Error");
            return; 
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
        for (int row = 0; row < MAX_TRIES; row++) 
        {
            for (int col = 0; col < GRID_SIZE; col++) 
            {
                Label cell = new Label();
                cell.setMinSize(60, 60);
                cell.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: white; -fx-alignment: center; -fx-font-size: 30px;");
                letterGrid.add(cell, col, row);
            }
        }

        // Keyboard setup, Set the first two rows of the keyboard and their action events
        String[] rows = {"QWERTYUIOP", "ASDFGHJKL"};
        VBox keyboard = new VBox(6);
        for (int i = 0; i < rows.length; i++) 
        {
            HBox keyRow = new HBox(5);
            keyRow.setAlignment(Pos.CENTER);
            for (char c : rows[i].toCharArray()) 
            {
                Button key = new Button(String.valueOf(c));
                key.setMinSize(50, 50);
                key.setOnAction(e -> handleKeyPress(c, letterGrid));
                keyRow.getChildren().add(key);
                keyboardButtons.put(c, key);
            }
            keyboard.getChildren().add(keyRow);
        }

        // Add ENTER key 
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

        // Add the last row to the keyboard
        keyboard.getChildren().add(zxcvbnmRow);

        // Create an empty row for formatting and design, create some space at the bottom so the last row can be properly seen
        HBox emptyRow = new HBox();
        emptyRow.setMinHeight(200); 
        keyboard.getChildren().add(emptyRow);

        keyboard.setAlignment(Pos.CENTER);
        root.addEventFilter(KeyEvent.KEY_PRESSED, event -> handleKeyboardInput(event, letterGrid));
        
        //add all the buttons and boxes 
        root.getChildren().addAll(title, letterGrid, keyboard);
        Scene scene = new Scene(root, 800, 1400);
        primaryStage.setScene(scene);
        primaryStage.show();

    // Prompt for user name
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

    // Read from the log file, if the player has recently played, load in their stats 
    File logFile = new File("src/log.txt");
    if (logFile.exists()) {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) 
        {
            String storedName = reader.readLine();
            if (storedName != null && storedName.equalsIgnoreCase(name)) 
            {
                int wins = Integer.parseInt(reader.readLine());
                int loss = Integer.parseInt(reader.readLine());
                int guesses = Integer.parseInt(reader.readLine());
                playerStats = new Stats(name, wins, loss, guesses);
            }
        } 
        catch (IOException | NumberFormatException e) 
        {
            System.err.println("Error");
        }
    }
    // set default name 
    if (name.isEmpty()) 
    {
        name = "Player";
    }
    // set up the display for the players name, wins, loss, and average guesses 
    name = name.substring(0, 1).toUpperCase() + name.substring(1);
    player.setUserName(name); // Set formatted name to player
    playerNameDisplay = new Text("\n\n\n\n\n\n\n\n\n\n\n\n Player: " + player.getUserName() + "\n" 
            + " Wins: " + playerStats.getWins() + "\n" 
            + " Losses: " + playerStats.getLoss() + "\n" 
            + " Average Guesses: " + playerStats.getAverageGuesses());
    playerNameDisplay.setStyle("-fx-font-size: 15px; -fx-font-family: 'Courier New'; -fx-fill: #FFFF00; -fx-letter-spacing: 2px;");
    HBox playerNameBox = new HBox(playerNameDisplay);
    playerNameBox.setAlignment(Pos.TOP_LEFT);
    root.getChildren().add(0, playerNameBox); // Add at the top of the root container

    // Add a reset button
    Button resetButton = new Button("  Reset  ");
    resetButton.setOnAction(e -> resetBoard());
    resetButton.setStyle("-fx-background-color: #FF4500; -fx-font-size: 16px; -fx-padding: 14px;"); // Set the button color to red and increase font size and padding
    HBox resetButtonBox = new HBox(resetButton);
    resetButtonBox.setAlignment(Pos.CENTER_RIGHT);
    root.getChildren().add(1,resetButtonBox); // Add below the losses
}
 

    /**
     * resets the game board to its initial state, clearing all entered letters and resetting the grid.
     */
    private void resetBoard() 
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
    // Enable all keyboard buttons and reset their styles(un-grey them)
    for (Button key : keyboardButtons.values()) 
    {
        key.setDisable(false);
        key.setStyle("-fx-background-color: white; -fx-border-color: black;");
    }
    answerWord = wordGenerator.getRandomWord();
	}
    

    /**
     * Handles key press events for the on screen keybaord in the game
     * @param c The character corresponding to the pressed key
     * @param grid The grid pane that represents the user's inputs
     */
    private void handleKeyPress(char c, GridPane grid) 
    {
        if (currentCol < GRID_SIZE) {
            Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + currentCol);
            cell.setText(String.valueOf(c));
            currentCol++;
        }
    }

    /**
     * Handles the 'ENTER' key press, which submits the current word attempt and processes it.
     * @param grid The grid pane that represents the letter grid in the UI.
     */
    private void handleEnter(GridPane grid) 
    {
        if (currentCol == GRID_SIZE) 
        {   
            // build the current word from the grid
            currentWord = new StringBuilder();
            
            // get the current word from the grid
            for (int col = 0; col < GRID_SIZE; col++) 
            {
                Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + col);
                currentWord.append(cell.getText());
            }
            
            // Check if the formed word is in the valid word list
            if(!wordGenerator.checkIfInList(currentWord.toString()))
            {
                // Show alert if the word is not valid
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Word");
                alert.setHeaderText(null);
                alert.setContentText("Not in word list.");
                alert.showAndWait();
                return; // stop the program here 
            }
            
            // get the [_, _, _, _, _] format of the word, based on how many are right/wrong
            List<String> result = wordGenerator.checkWordOrder(currentWord.toString(), answerWord);
            
            // Set to track the used characters for coloring the keyboard
            Set<Character> usedChars = new HashSet<>();
            for (int col = 0; col < GRID_SIZE; col++) 
            {
                Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + col);
                char guessChar = currentWord.charAt(col);
                char resultChar = result.get(col).charAt(0);
                
                // Correct character in the correct position
                if (resultChar == guessChar) 
                {
                    cell.setStyle("-fx-background-color: green; -fx-border-color: black; -fx-border-width: 2; -fx-alignment: center; -fx-font-size: 30px;");
                    usedChars.add(guessChar);
                } 
                // Correct character but in the wrong position
                else if (wordGenerator.checkInWord(guessChar, answerWord) && !usedChars.contains(guessChar)) 
                {
                    cell.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 2; -fx-alignment: center; -fx-font-size: 30px;");
                    usedChars.add(guessChar);
                } 
                // Incorrect character
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
            // Re-check disabled keys and unlock them if the letter is still in the word
            for (Map.Entry<Character, Button> entry : keyboardButtons.entrySet()) 
            {
                char keyChar = entry.getKey();
                Button keyButton = entry.getValue();
                if (keyButton.isDisabled() && wordGenerator.checkInWord(keyChar, answerWord)) 
                {
                    keyButton.setDisable(false);
                    keyButton.setStyle("-fx-background-color: lightgrey; -fx-border-color: black;");
                }
            }
            
            // Check if the guessed word matches the answer word
            if (currentWord.toString().equalsIgnoreCase(answerWord)) 
            {
                // Increment win count and update player stats
                count += 1; 
                playerStats.addWins();
                playerStats.setAverageGuesses(count);
                updateScoreDisplay();
                
                // winning alert 
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
                if (resultWin.isPresent() && resultWin.get() == resetButton)
                 {
                    resetBoard(); // get a new word and reset the board
                }
                return; // stop program
            }
            // Move to the next row if not the last attempt
            else if (currentRow < MAX_TRIES - 1) 
            {
                currentRow++;
                currentCol = 0;
                count += 1;
            } 
            // Handle the case when all attempts are over
            else 
            {
                // Increment loss count and update display
                count += 1; 
                playerStats.addLoss();
                updateScoreDisplay();
                
                // loss alert 
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
                resetBoard();
                answerWord = wordGenerator.getRandomWord(); // get a new word
            }
        }
    }
    

    /**
     * handles the backspace key press, removing the last entered letter from the current word attempt.
     */
    private void handleBackspace(GridPane grid) 
    {
        if (currentCol > 0) 
        {
            currentCol--;
            Label cell = (Label) grid.getChildren().get(currentRow * GRID_SIZE + currentCol);
            cell.setText("");
        }
    }

    /**
     * Handles keyboard input from the user, mapping physical keyboard presses to actions in the game.
     * @param event The key event triggered by pressing a key.
     * @param grid The grid pane that represents the letter grid in the UI.
     */
    private void handleKeyboardInput(KeyEvent event, GridPane grid) {
        switch (event.getCode()) {
            case ENTER:
                handleEnter(grid);
                break;
            case BACK_SPACE:
                handleBackspace(grid);
                break;
            default:
                if (event.getText().matches("[a-zA-Z]")) 
                {
                    char inputChar = event.getText().toUpperCase().charAt(0);
                    if (keyboardButtons.containsKey(inputChar)) {
                        Button keyButton = keyboardButtons.get(inputChar);
                        if (keyButton.isDisabled()) 
                        {
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
    }

    /**
     * Updates the display of the player's score and statistics.
     */
    private void updateScoreDisplay() 
    {
           playerNameDisplay.setText("\n\n\n\n\n\n\n\n\n\n\n\n Player: " + player.getUserName() + "\n" 
                   + " Wins: " + playerStats.getWins() + "\n" 
                   + " Losses: " + playerStats.getLoss() + "\n" 
                   + " Average Guesses: " + playerStats.getAverageGuesses());
           playerNameDisplay.setStyle("-fx-font-size: 15px; -fx-font-family: 'Courier New'; -fx-fill: #FFFF00; -fx-letter-spacing: 2px;"); 

    }
  
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() 
    {
        // Create a log file with player stats
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/log.txt"))) {
            writer.write(player.getUserName() + "\n");
            writer.write(playerStats.getWins() + "\n");
            writer.write(playerStats.getLoss() + "\n");
            writer.write(playerStats.getAverageGuesses() + "\n");
        } catch (IOException e) {
            System.err.println("Error");
    }
}  

}