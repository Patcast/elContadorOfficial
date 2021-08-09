package be.kuleuven.elcontador10.background.model;

public class StakeHolder {
    private String id;
    private String name;
    private String role;
    private boolean deleted;
    private String email;
    private int phoneNumber;
    private String idOfGlobalAccount;
    private boolean authorized;

    public StakeHolder(String name, String role, boolean deleted, String email, int phoneNumber, String idOfGlobalAccount, boolean authorized) {
        this.name = name;
        this.role = role;
        this.deleted = deleted;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.idOfGlobalAccount = idOfGlobalAccount;
        this.authorized = authorized;
    }

    public StakeHolder() {
    }
    public String getIdOfGlobalAccount() {
        return idOfGlobalAccount;
    }

    public boolean isAuthorized() {
        return authorized;
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
