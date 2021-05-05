package be.kuleuven.elcontador10.background;

public class Transaction {
    private boolean cashIn;
    private double amount;
    private String person;
    private int idType;
    private String txtComments;




    ///Constructor use to make object to create submit URL
    public Transaction(boolean cashIn, double amount, String person,int idTypeTrans, String txtComments) {
        this.cashIn = cashIn;
        this.amount = amount;
        this.person = person;
        this.idType = idTypeTrans;
        this.txtComments = txtComments;
    }

    public boolean isCashIn() {
        return cashIn;
    }

    public double getAmount() {
        return amount;
    }

    public String getPerson() {
        return person;
    }

    public int getIdType() {
        return idType;
    }

    public String getTxtComments() {
        return txtComments;
    }
}



