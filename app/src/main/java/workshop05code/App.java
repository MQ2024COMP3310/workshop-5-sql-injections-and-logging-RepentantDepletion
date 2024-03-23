package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.util.logging.FileHandler;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());

    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
            if (wordleDatabaseConnection.isValidInput(line)) {
                // Log valid words at CONFIG level
                logger.config("Valid word read from file: " + line);
                wordleDatabaseConnection.addValidWord(0, line);
            } else {
                // Log invalid words at SEVERE level
                logger.severe("Invalid word read from file: " + line);
            }
        }
    } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to load data.txt", e);
    }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            while (!guess.equals("q")) {
                if (wordleDatabaseConnection.isValidInput(guess)) {
                    System.out.println("You've guessed '" + guess + "'.");
                    if (wordleDatabaseConnection.isValidWord(guess)) {
                        System.out.println("Success! It is in the list.\n");
                        logger.log(Level.INFO, "Correct Input");
                    } else {
                        System.out.println("Sorry. This word is NOT in the list.\n");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a 4-letter word consisting only of lowercase letters a-z.");
                    logger.log(Level.INFO, "Invalid guess");
                }

                System.out.print("Enter a 4 letter word for a guess or q to quit: ");
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            logger.log(Level.WARNING, "An error occurred during input", e);
        }

    }
}