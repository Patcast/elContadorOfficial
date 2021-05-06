package be.kuleuven.elcontador10.background;

public class Transaction {
    private boolean cashIn;
    private double amount;
    private int idStakeholder;
    private int idType;
    private String txtComments;


    private final String idOfBudget = "8";




    ///Constructor use to make object to create submit URL
    public Transaction(boolean cashIn, double amount, int idStake,int idTypeTrans, String txtComments) {
        this.cashIn = cashIn;
        this.amount = amount;
        this.idStakeholder = idStake;
        this.idType = idTypeTrans;
        this.txtComments = txtComments;
    }

    public double getAmount() {
        return amount;
    }

    public int getIdStakeholder() {
        return idStakeholder;
    }

    public String getIdType() {
        return String.valueOf(idType);
    }

    public String getTxtComments() {
        return txtComments;
    }

    public String getStakePays() {
        if(cashIn){ return String.valueOf(idStakeholder); }
        return idOfBudget;
    }

    public String getStakeReceives() {
        if(cashIn){ return String.valueOf(idOfBudget); }
        return String.valueOf(idStakeholder);
    }
}



