package application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Word extends GameBoard {
    private List<String> words;

    public Word() 
    {
        try {
            // Load words from file into a list
            words = Files.readAllLines(Paths.get("/Users/harshdave/eclipse-workspace/Wordle/src/application/wordle-words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load words from file.");
        }
    }

    public String getRandomWord() 
    {
        if (words == null || words.isEmpty()) {
            return null; // or throw an exception if you prefer
        }
        Random random = new Random();
        String word = words.get(random.nextInt(words.size()));
        System.out.println(word);
        return word;
    }
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
    public boolean checkIfInList(String guess)
    {
        try {
            words = Files.readAllLines(Paths.get("/Users/harshdave/eclipse-workspace/Wordle/src/application/wordle-words.txt"));
            for (String word : words) {
                if (word.equalsIgnoreCase(guess)) 
                {
                	return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load words from file.");
        }
        return false;
    }
}

