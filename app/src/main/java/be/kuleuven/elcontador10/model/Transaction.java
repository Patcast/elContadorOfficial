package be.kuleuven.elcontador10.model;

public class Transaction {
    private boolean cashIn;
    private double amount;
    private int idUser;

    private String stakeHolder;
    private int idType;
    private String txtComments;


    private final String idOfBudget = "8";




    ///Constructor use to make object to create submit URL
    public Transaction(boolean cashIn, double amount, int idUser, String stake, int idTypeTrans, String txtComments) {
        this.amount = amount;
        this.cashIn = cashIn;
        this.idUser = idUser;
        this.stakeHolder = stake;
        this.idType = idTypeTrans;
        this.txtComments = txtComments;
    }

    public double getAmount() {
        double fixAmount;
        if(cashIn){
            if (amount>0){fixAmount = amount;}
            else{fixAmount = -1*amount;}
        }
        else{ //this represents cash out.
            if (amount>0){fixAmount = -1*amount;}
            else{fixAmount = amount;}
        }
        return fixAmount;
    }

    public int getIdUser() {
        return idUser;
    }

    public String  getIdStakeholder() {

        if(stakeHolder.isEmpty()){
            return "0";
        }
        else {

            return  stakeHolder.split("-")[1];
        }
    }

    public String getIdType() {
        return String.valueOf(idType);
    }

    public String getTxtComments() {
        return txtComments;
    }

}



