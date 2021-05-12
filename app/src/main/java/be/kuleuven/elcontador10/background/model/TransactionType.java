package be.kuleuven.elcontador10.background.model;

public class TransactionType {
    int id;
    String category;
    String subCategory;

    public TransactionType(int id ,String cat,String sub) {
        category =cat;
        subCategory = sub;
        this.id = id;
    }
    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public int getId() {
        return id;
    }
}
