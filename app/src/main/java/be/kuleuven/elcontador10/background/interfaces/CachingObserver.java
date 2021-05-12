package be.kuleuven.elcontador10.background.interfaces;

import java.util.List;

import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.TransactionType;

public interface CachingObserver {
    void notifyRoles( List <String> roles);
    void notifyCategories( List <TransactionType>  transTypes);
    void notifyStakeHolders(List<StakeHolder> stakeHolders );
}
