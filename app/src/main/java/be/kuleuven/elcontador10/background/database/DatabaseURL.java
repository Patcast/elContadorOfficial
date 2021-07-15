package be.kuleuven.elcontador10.background.database;

public enum DatabaseURL {
    INSTANCE;

    //// FireBase
    private  String globalAccountId;
    private String chosenAccountId;
    private String logInUserId;


    // misc
    String getTranType =  "https://studev.groept.be/api/a20sd505/getTransactionTypes"; // table: TransactionType || columns: al
    String getRoles =  "https://studev.groept.be/api/a20sd505/getStakeholderRoles"; // table: Role || columns: All
    String checkLogIn = "https://studev.groept.be/api/a20sd505/LogIn/";
    String getBudget = "https://studev.groept.be/api/a20sd505/getBudget";

    // settings
    String changePassword = "https://studev.groept.be/api/a20sd505/changePassword/";
    String getNonRegistered = "https://studev.groept.be/api/a20sd505/getNonRegisteredStakeholders";
    String checkUsernameList = "https://studev.groept.be/api/a20sd505/checkUsernameList/";
    String registerUsername = "https://studev.groept.be/api/a20sd505/registerUsername/";
    String updateUsername = "https://studev.groept.be/api/a20sd505/updateUsername/";

    // stakeholders
    String URL_findStakeholderUsername = "https://studev.groept.be/api/a20sd505/findStakeholderUsername/";
    String getStakeHolders = "https://studev.groept.be/api/a20sd505/getStakeholders"; // table: Stakeholders || Columns: idStakeholders, firstName, LastName, Role, deleted
    String getStakeholder = "https://studev.groept.be/api/a20sd505/getStakeholder/"; // single stakeholder
    String deleteStakeholder = "https://studev.groept.be/api/a20sd505/deleteStakeholder/";
    String addStakeholder = "https://studev.groept.be/api/a20sd505/addStakeholder/";
    String editStakeholder = "https://studev.groept.be/api/a20sd505/editStakeholder/";

    // transactions
    String homepageTransaction = "https://studev.groept.be/api/a20sd505/getHomepageTransactions";
    String getTransactions = "https://studev.groept.be/api/a20sd505/getTransactions"; // all
    String getTransaction = "https://studev.groept.be/api/a20sd505/getTransaction/"; // single
    String deleteTransaction = "https://studev.groept.be/api/a20sd505/deleteTransaction/";
    String addTransaction = "https://studev.groept.be/api/a20sd505/postNewTransaction/";

}
