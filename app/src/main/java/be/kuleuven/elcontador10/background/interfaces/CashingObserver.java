package be.kuleuven.elcontador10.background.interfaces;

import java.util.List;

import be.kuleuven.elcontador10.model.StakeHolder;
import be.kuleuven.elcontador10.model.TransactionType;

public interface CashingObserver {
    void notifyRoles( List <String> roles);
    void notifyCategories( List <TransactionType>  transTypes);
    void notifyStakeHolders(List<StakeHolder> stakeHolders );
}
