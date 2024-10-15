package Solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class WordleSolver extends Thread {
    private class Letter{
        private char letter;
        private double occurrence;
        public Letter(char letter, int occurrence) {
            this.letter = letter;
            this.occurrence = occurrence;
        }

        public char getLetter() {
            return letter;
        }
        public double getOccurrence() {
            return occurrence;
        }
        public void setOccurrence(double occurrence) {
            this.occurrence = occurrence;
        }
        public void incrementOccurrence() {
            occurrence++;
        }
    }
    private ArrayList<Square> squares;
    private ArrayList<String> dataBase;
    private final char[] alphabet;
    private ArrayList<Letter> letters;
    private int currentAttempt = 0;
    private int lastAttempt = 0;
    private boolean solved = false;

    public WordleSolver(ArrayList<Square> squares) throws FileNotFoundException {
        this.squares = squares;
        dataBase = new ArrayList<>();


        // Initiate Word DataBase
        File file = new File("src/Solver/wordle.csv");
        Scanner s = new Scanner(file);
        s.nextLine();
        while(s.hasNextLine()) {
            dataBase.add(s.nextLine().substring(0,5));
        }
        s.close();

        // Learn alphabet ;)
        alphabet = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t',
                                                                                        'u','v','w','x','y','z'};
        // Save alphabet in a Data Structure (Letter:(Char letter, int occurrence))
        letters = new ArrayList<>();
        for (char letter : alphabet) {
            letters.add(new Letter(letter, 0));
        }

        // Calculate the occurrences of each letter for our word database
        for(Letter l : letters){
            for(String str : dataBase){
                if(str.indexOf(l.letter) != -1){
                    l.incrementOccurrence();
                }
            }
            l.setOccurrence(l.getOccurrence()/dataBase.size()); // Saves result in percentage
        }
    }

    @Override
    public void run() {
        solve();
    }

    public synchronized void solve(){
        addToBoard(getFirstWord());
        while(!solved){
            while(currentAttempt == lastAttempt) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            lastAttempt = currentAttempt;
            System.out.println(getWord());
            addToBoard(getWord());
        }





    }

    // Front-Back logic
    private void addToBoard(String word){
        int index = 0;
        for (int i = (currentAttempt*5) + 0; i < (currentAttempt*5) + 5; i++) {
            squares.get(i).setLetter(String.valueOf(word.charAt(index)));
            index++;
        }
    }


    // Solving logic

    // Calculate what is the summed occurrence of all the letters in the same word, since we have no other information.
    // Note: Take out words that repeated letters
    private String getFirstWord(){
        String firstword = "";
        double value = -1.0;
        for(String str : dataBase){
            if(value < summedOccurence(str, false)){
                firstword = str;
                value = summedOccurence(str, false);
            }
        }
        return firstword;
    }

    private String getWord(){
        // Get lasts word values, returned by the user.
        Square[] lastWord = new Square[5];
        for (int i = 0; i < 5; i++) {
            lastWord[i] = squares.get((currentAttempt-1)*5 + i);
        }

        boolean skip = false;
        ArrayList<String> newDataBase = new ArrayList<>();
        // Run through array handling each letter value
        for(String str : dataBase){
            for (int pos = 0; pos < 5; pos++) {
                char letter = lastWord[pos].getLetter().charAt(0);

                if (lastWord[pos].getValue() == Square.Value.GREEN) {
                    if (str.charAt(pos) != letter) {
                        skip = true;
                        break;
                    }
                }

                if (lastWord[pos].getValue() == Square.Value.YELLOW) {
                    if (str.indexOf(letter) == -1 || str.charAt(pos) == letter) {

                        skip = true;
                        break;
                    }
                }

                if (lastWord[pos].getValue() == Square.Value.GRAY) {
                    if (str.indexOf(letter) != -1) {
                        skip = true;
                        break;
                    }
                }
            }
            if(!skip) newDataBase.add(str);
            else skip = false;
        }

        dataBase = newDataBase;
        String newWord = null;
        boolean allowRepeatedWords = false;
        while(newWord == null){
            double value = -1.0;

            for(String str : dataBase) {
                if (value < summedOccurence(str, allowRepeatedWords)) {
                    newWord = str;
                    value = summedOccurence(str, allowRepeatedWords);
                }
            }
            allowRepeatedWords = true;
        }
        return newWord;
    }

    private double summedOccurence(String word, boolean allowRepeatedLetters){
        if(word.length() != 5) throw new IllegalArgumentException();

        char[] array = word.toCharArray();
        double occurrence = 0.0;

        for (int i = 0; i < 5; i++) {
            if(word.indexOf(array[i]) != i && !allowRepeatedLetters){
                return -1.0;
            }
            occurrence += letters.get(array[i]-'a').getOccurrence();
        }
        return occurrence;
    }

    public synchronized void notifyInput(){
        notifyAll();
    }

    public int getCurrentAttempt(){
        return currentAttempt;
    }

    public void incrementCurrentAttempt(){
        currentAttempt++;
    }



























    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        ArrayList<Square> squares = new ArrayList<>();
        WordleSolver solver = new WordleSolver(squares);

        sleep(500);
        solver.solve();

    }
}
