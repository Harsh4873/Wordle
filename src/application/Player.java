package application;

/**
 * Represents a user in the game. 
 */
public class Player extends GameBoard
{
    /**
     * The username of the player
     * String
     */
    private String userName;

    /**
     * Constructs a new Player with a specific username
     * They must set it, otherwise default will be "Player"
     * @param userName The username to be assigned to this player
     */
    public Player(String userName) 
    {
        this.userName = userName;
    }

    /**
     * Gets the username of the player
     * @return The current username of the player
     */
    public String getUserName() 
    {
        return userName;
    }

    /**
     * Sets the username of the player
     * @param userName The new username to be set for the player
     */
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    
}
