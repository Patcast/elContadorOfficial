package be.kuleuven.elcontador10.background.database;

public enum Frequency {
    ONE_TIME(0),
    WEEKLY(1),
    MONTHLY(2),
    QUARTERLY(3),
    YEARLY(4);

    private final int label;

    Frequency(int label) {
        this.label = label;
    }

    public int get() {
        return label;
    }
}
