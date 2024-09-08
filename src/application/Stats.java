package application;

/**
 * Represents the statistics of a player in the game
 * Child of the Player class
 */
public class Stats extends Player
{	
    private int wins; // Number of wins the player has
    private int losses; // Number of losses the player has
    private int averageGuesses; // Average number of guesses per game

    /**
     * @param userName The username of the player
     * @param wins The number of wins
     * @param losses The number of losses
     * @param averageGuesses The average number of guesses
     */
	public Stats(String userName, int wins, int losses, int averageGuesses) 
    {
		super(userName);
        this.wins = wins;
        this.losses = losses;
        this.averageGuesses = averageGuesses;
	}

    /**
     * Get the number of wins the player has
     * @return The number of wins
     */
	public int getWins() {
        return wins;
    }

    /**
     * add a win to the total wins the current player has
     */
    public void addWins() 
    {
        this.wins += 1;
    }

    /**
     * Get the number of losses the player has
     * @return The number of losses
     */
    public int getLoss() {
        return losses;
    }

    /**
     * add a loss to the total losses the current player has
     */
    public void addLoss() 
    {
        this.losses += 1;
    }
    
    /**
     * Get the average number of guesses per game
     * @return The average number of guesses
     */
    public int getAverageGuesses() 
    {
        return averageGuesses;
    }

    /**
     * Update the average number of guesses based on the total of all games and past and new guesses
     * @param guess The number of guesses in the most recent game
     */
    public void setAverageGuesses(int guess) 
    {
        int totalGames = this.getWins() + this.getLoss();
        if (totalGames > 0) 
        {
            averageGuesses = ((averageGuesses * (totalGames - 1)) + guess) / totalGames;
        } 
        else 
        {
            averageGuesses = guess;
        }
        // Ensure averageGuesses is within the range 1 to 6
        if (averageGuesses < 1) {
            averageGuesses = 1;
        } else if (averageGuesses > 6) {
            averageGuesses = 6;
        }
    }
}
