package be.kuleuven.elcontador10.fragments.transactions.scheduledTransaction;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import be.kuleuven.elcontador10.background.model.StakeHolder;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

// may be used for future where you can change which transaction to execute
public class ExecuteScheduledViewModel extends ViewModel {
    private final MutableLiveData<StakeHolder> chosenStakeholder = new MutableLiveData<>(null);
    private final MutableLiveData<ScheduledTransaction> chosenTransaction = new MutableLiveData<>(null);

    public MutableLiveData<StakeHolder> getChosenStakeholder() {
        return chosenStakeholder;
    }

    public MutableLiveData<ScheduledTransaction> getChosenTransaction() {
        return chosenTransaction;
    }

    public void setChosenStakeholder(StakeHolder stakeholder) {
        chosenStakeholder.setValue(stakeholder);
    }

    public void setChosenTransaction(ScheduledTransaction scheduledTransaction) {
        chosenTransaction.setValue(scheduledTransaction);
    }
}
