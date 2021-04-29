package be.kuleuven.elcontador10.background;

public class Transaction {
    private boolean cashIn;
    private double amount;
    private String person;
    private String category;
    private String subCategory;
    private String txtComments;

    public Transaction(boolean cashIn, double amount, String person,String category, String subCategory, String txtComments){
        this.cashIn = cashIn;
        this.amount=amount;
        this.person= person;
        this.category= category;
        this.subCategory= subCategory;
        this.txtComments= txtComments;
    }




}
