package be.kuleuven.elcontador10.background.database;

public enum DatabaseURL {
    INSTANCE;

    // misc
    String getTranType =  "https://studev.groept.be/api/a20sd505/getTransactionTypes"; // table: TransactionType || columns: al
    String getRoles =  "https://studev.groept.be/api/a20sd505/getStakeholderRoles"; // table: Role || columns: All
    String checkLogIn = "https://studev.groept.be/api/a20sd505/LogIn/";

    // stakeholders
    String URL_findStakeholderUsername = "https://studev.groept.be/api/a20sd505/findStakeholderUsername/";
    String getStakeHolders = "https://studev.groept.be/api/a20sd505/getStakeholders"; // table: Stakeholders || Columns: idStakeholders, firstName, LastName, Role, deleted
    String getStakeholder = "https://studev.groept.be/api/a20sd505/getStakeholder/"; // single stakeholder
    String deleteStakeholder = "https://studev.groept.be/api/a20sd505/deleteStakeholder/";
    String addStakeholder = "https://studev.groept.be/api/a20sd505/addStakeholder/";
    String editStakeholder = "https://studev.groept.be/api/a20sd505/editStakeholder/";
}
