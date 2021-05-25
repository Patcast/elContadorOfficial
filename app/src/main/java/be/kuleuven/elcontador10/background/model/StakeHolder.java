package be.kuleuven.elcontador10.background.model;

public class StakeHolder {
    private int id;
    private String name;
    private String familyName;
    private String role;
    private boolean deleted;

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
    public boolean isDeleted() { return deleted; }
    public String  getIdStakeholder(String stakeHolder) {
        if(stakeHolder.isEmpty()){
            return "0";
        }
        else { return  stakeHolder.split("-")[1]; }
    }

}
