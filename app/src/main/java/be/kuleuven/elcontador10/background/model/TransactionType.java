package be.kuleuven.elcontador10.background.model;

public class TransactionType {

    private String category;
    private String subCategory;


    public TransactionType(String category, String subCategory) {
        this.category = category;
        this.subCategory = subCategory;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

}
