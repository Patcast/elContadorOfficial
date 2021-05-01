package be.kuleuven.elcontador10.background;

import android.content.res.Resources;

import be.kuleuven.elcontador10.R;

public class Transaction {
    private boolean cashIn;
    private double amount;
    private String person;
    private String category;
    private String subCategory;
    private String txtComments;

//    private  String[] rentArray = new String[]{"Other","Rent","Deposit","Maintenance","Reimbursement"};
//    final String[] SalaryArray = new String[]{"Other","Weekly","Fortnightly","Commission"};
//    final String[] purchaseArray = new String[]{"Other","Construction Materials","Supplies","Rubbish"};
//    final String[] toiletsArray = new String[]{"Other","Entrance Fee","Paper","Towels"};
//    final String[] DepositArray = new String[]{"Other","Cash from company","Cash for company"};

    public Transaction(boolean cashIn, double amount, String person,String category, String subCategory, String txtComments){
        this.cashIn = cashIn;
        this.amount=amount;
        this.person= person;
        this.category= category;
        this.subCategory= subCategory;
        this.txtComments= txtComments;
    }

    public static String[] chooseSubCat(String category){
        switch (category){
            case  "Rent":
                return Resources.getSystem().getStringArray(R.array.rent_subcategory_items); //String[]{"Other","Rent","Deposit","Maintenance","Reimbursement"};
            case  "Salary":
                return Resources.getSystem().getStringArray(R.array.salary_subcategory_items); //new String[]{"Other","Weekly","Fortnightly","Commission"};
            case  "Toilets":
                return Resources.getSystem().getStringArray(R.array.toilets_subcategory_items); // new String[]{"Other","Entrance Fee","Paper","Towels"};
            case  "Purchases":
                return Resources.getSystem().getStringArray(R.array.purchases_subcategory_items); // new String[]{"Other","Construction Materials","Supplies","Rubbish"};
            case  "Deposits":
                return Resources.getSystem().getStringArray(R.array.deposits_subcategory_items); // new String[]{"Other","Cash from company","Cash for company"};
            default:
                return null;
        }
    }

}
