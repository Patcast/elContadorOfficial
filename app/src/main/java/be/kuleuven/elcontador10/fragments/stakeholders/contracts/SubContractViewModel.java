package be.kuleuven.elcontador10.fragments.stakeholders.contracts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

public class SubContractViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<ScheduledTransaction>> filtered = new MutableLiveData<>();
    private ArrayList<ScheduledTransaction> late, future, completed, ignored;

    // filters with default value
    private final MutableLiveData<Boolean> isLate = new MutableLiveData<>(true),
            isFuture = new MutableLiveData<>(true), isCompleted = new MutableLiveData<>(false),
            isIgnored = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getIsLate() {
        return isLate;
    }

    public void setIsLate(boolean isLate) {
        this.isLate.setValue(isLate);
    }

    public MutableLiveData<Boolean> getIsFuture() {
        return isFuture;
    }

    public void setIsFuture(boolean isFuture) {
        this.isFuture.setValue(isFuture);
    }

    public MutableLiveData<Boolean> getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted.setValue(isCompleted);
    }

    public MutableLiveData<Boolean> getIsIgnored() { return isIgnored; }

    public void setIsIgnored(boolean isIgnored) { this.isIgnored.setValue(isIgnored);}

    public MutableLiveData<ArrayList<ScheduledTransaction>> getFiltered() {
        return filtered;
    }

    public void setRaw(ArrayList<ScheduledTransaction> transactions) {
        late = new ArrayList<>();
        future = new ArrayList<>();
        completed = new ArrayList<>();
        ignored = new ArrayList<>();

        for (ScheduledTransaction transaction : transactions) {
            if (transaction.getStatus() != null)
                switch (transaction.getStatus()) {
                    case LATE:
                        late.add(transaction);
                        break;
                    case FUTURE:
                        future.add(transaction);
                        break;
                    case COMPLETED:
                        completed.add(transaction);
                        break;
                    case IGNORED:
                        ignored.add(transaction);
                        break;
                }
        }

        setFiltered();
    }

    public void setFiltered() {
        ArrayList<ScheduledTransaction> combined = new ArrayList<>();

        assert isLate.getValue() != null;
        assert isFuture.getValue() != null;
        assert isCompleted.getValue() != null;
        assert isIgnored.getValue() != null;

        if (isLate.getValue()) combined.addAll(late);

        if (isFuture.getValue()) combined.addAll(future);

        if (isCompleted.getValue()) combined.addAll(completed);

        if (isIgnored.getValue()) combined.addAll(ignored);

        this.filtered.setValue(combined);
    }
}
