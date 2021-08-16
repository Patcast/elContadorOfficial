package be.kuleuven.elcontador10.background.model;

public class NumbersFormat {
    String editedNumber;
    boolean isPositive;
    public NumbersFormat(long rawNumber) {
        isPositive= rawNumber > 0;

    }
}
