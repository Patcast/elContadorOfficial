package be.kuleuven.elcontador10.background.model;

public class StakeHolder {
    private String id;
    private String name;
    private String role;
    private boolean deleted;
    private String email;
    private int phoneNumber;



    public StakeHolder(String id, String name, String role, boolean deleted, String email, int phoneNumber) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.deleted = deleted;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public StakeHolder(String name, String role, boolean deleted, String email, int phoneNumber) {
        this.name = name;
        this.role = role;
        this.deleted = deleted;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public StakeHolder() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getEmail() {
        return email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }
    public void setId(String id) {
        this.id = id;
    }
}
