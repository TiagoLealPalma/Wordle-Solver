package Solver;

public class Square {
    public enum Value {GRAY ,YELLOW, GREEN};

    private Value value;
    private String letter;
    private boolean interactable;

    public Square(boolean interactable) {
        this.interactable = interactable;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }
}
