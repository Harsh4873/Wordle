package application;

public class Stats extends Player
{	
    private int wins;
    private int losses;
    private int averageGuesses;

	public Stats(String userName, int wins, int losses, int averageGuesses) 
    {
		super(userName);
        this.wins = wins;
        this.losses = losses;
        this.averageGuesses = averageGuesses;
		// TODO Auto-generated constructor stub
	}
	public int getWins() {
        return wins;
    }

    public void addWins() {
        this.wins += 1;
    }

    public int getLoss() {
        return losses;
    }

    public void addLoss() {
        this.losses += 1;
    }
    
    public int getAverageGuesses() 
    {
        return averageGuesses;
    }

    public void setAverageGuesses(int guess) 
    {
        averageGuesses = (guess+this.averageGuesses)/(this.getWins()+this.getLoss());
    }
    	

}
