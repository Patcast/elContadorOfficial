package be.kuleuven.elcontador10.background.database;

public enum DataBaseURL {
    INSTANCE;
    String getTranType =  "https://studev.groept.be/api/a20sd505/getTransactionTypes"; // table: TransactionType || columns: al
    String getStakeHolder = "https://studev.groept.be/api/a20sd505/getStakeholders"; // table: Stakeholders || Columns: idStakeholders, firstName, LastName, Role, deleted
    String getRoles =  "https://studev.groept.be/api/a20sd505/getStakeholderRoles"; // table: Role || columns: All
    String checkLogIn = "https://studev.groept.be/api/a20sd505/LogIn/";
    String URL_findStakeholderUsername = "https://studev.groept.be/api/a20sd505/findStakeholderUsername/";
}
