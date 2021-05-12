package be.kuleuven.elcontador10.model;

public class StakeHolder {
    int id;
    String name;
    String familyName;
    String role;
    boolean deleted;

    public StakeHolder(int id, String name, String familyName, String role, boolean deleted) {
        this.id = id;
        this.name = name;
        this.familyName = familyName;
        this.role = role;
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getFamilyName() {
        return familyName;
    }
    public String getRole() {
        return role;
    }
    public String getFullNameId(){return ("-"+id+"-"+" "+name+" "+familyName);}
    public boolean isDeleted() {
        return deleted;
    }

}
