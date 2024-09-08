package application;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * It is a child class of GameBoard
 * It is responsible for getting a new random word, checking the order of the word in the Wordle, and verifying if a guess is right or if it in the word list
 */
public class Word extends GameBoard 
{
    private List<String> words; // List to hold words loaded from a file

    /**
     * Constructor for Word class.
     * It loads in the list of words from the wordle-words.txt file into the List of Strings
     */
    public Word() 
    {
        try 
        {
            words = Files.readAllLines(Paths.get("src/application/wordle-words.txt"));
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            System.out.println("Error");
        }
    }

    /**
     * Gets a random word from the list of words
     * @return a random word from the list
     */
    public String getRandomWord() 
    {
        Random random = new Random();
        String word = words.get(random.nextInt(words.size()));
        return word;
    }

    /**
     * Compares the guessed word with the actual word and returns a list with the correct characters and _ for the others
     * @param guess The guessed word
     * @param actual The correct word
     * @return A list of characters and blanks based on the comparison of both words
     */
    public List<String> checkWordOrder(String guess, String actual) 
    {
        guess = guess.toUpperCase(); 
        actual = actual.toUpperCase(); 
        List<String> result = new ArrayList<>();
        for(int i = 0; i < guess.length(); i++)
        {
            if(guess.charAt(i) == actual.charAt(i))
            {
                result.add(String.valueOf(guess.charAt(i)));
            }
            else
            {
                result.add("_");
            }
        }
        return result; 
    }

    /**
     * Checks if a specific character in the guess word is present in the actual word
     * @param a The character to check
     * @param actual The actual word
     * @return true if the character is in the word, false otherwise
     */
    public boolean checkInWord(char a, String actual) 
    {
        a = Character.toUpperCase(a); 
    	actual = actual.toUpperCase(); 
    	for(int i = 0; i < actual.length(); i++)
        {
            if(a == actual.charAt(i))
            {
            	return true; 
            }
        }
    	return false; 
    }

    /**
     * verify if the guessed word is in the list of valid words
     * @param guess The word to verify
     * @return true if the word is in the list, false otherwise
     */
    public boolean checkIfInList(String guess)
    {
        for (String word : words) 
        {
            if (word.equalsIgnoreCase(guess)) 
            {
                return true;
            }
        }
        return false;
    }
}

