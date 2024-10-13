package Solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class WordleSolver {
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

    public void solve(){
        getFirstWord();
        // get the squares to correspond next unoccupied squares to receive the solved word
    }


    private String getFirstWord(){
        // Calculate what is the summed occurrence of all the letters in the same word, since we have no other information.
        // Note: Take out words that repeated letters
        String firstword = "faile";
        double value = -1.0;
        for(String str : dataBase){
            if(value < summedOccurence(str)){
                firstword = str;
                value = summedOccurence(str);
            }
        }

        return firstword;
    }

    private double summedOccurence(String word){
        if(word.length() != 5) throw new IllegalArgumentException();

        char[] array = word.toCharArray();
        double occurrence = 0.0;

        for (int i = 0; i < 5; i++) {
            if(word.indexOf(array[i]) != i){
                return -1.0;
            }
            occurrence += letters.get(array[i]-'a').getOccurrence();
        }
        return occurrence;
    }




























    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<Square> squares = new ArrayList<>();
        WordleSolver solver = new WordleSolver(squares);


        System.out.println(solver.getFirstWord());

    }
}
