package be.kuleuven.elcontador10.fragments.stakeholders.contracts;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

public class SubContractViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<ScheduledTransaction>> filtered = new MutableLiveData<>();
    private ArrayList<ScheduledTransaction> late, future, completed;

    // filters with default value
    private final MutableLiveData<Boolean> isLate = new MutableLiveData<>(true),
            isFuture = new MutableLiveData<>(true), isCompleted = new MutableLiveData<>(false);

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

    public MutableLiveData<ArrayList<ScheduledTransaction>> getFiltered() {
        return filtered;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setRaw(ArrayList<ScheduledTransaction> transactions) {
        late = transactions.stream()
                .filter(e -> Math.abs(e.getAmountPaid()) < Math.abs(e.getTotalAmount()))
                .filter(e -> e.getDueDate().getSeconds() < Timestamp.now().getSeconds()) // due date smaller than now
                .collect(Collectors.toCollection(ArrayList::new));

        future = transactions.stream()
                .filter(e -> Math.abs(e.getAmountPaid()) < Math.abs(e.getTotalAmount()))
                .filter(e -> e.getDueDate().getSeconds() > Timestamp.now().getSeconds()) // due date larger than now
                .collect(Collectors.toCollection(ArrayList::new));

        completed = transactions.stream()
                .filter(e -> Math.abs(e.getAmountPaid()) >= Math.abs(e.getTotalAmount()))
                .collect(Collectors.toCollection(ArrayList::new));

        setFiltered();
    }

    public void setFiltered() {
        ArrayList<ScheduledTransaction> combined = new ArrayList<>();

        if (isLate.getValue()) combined.addAll(late);

        if (isFuture.getValue()) combined.addAll(future);

        if (isCompleted.getValue()) combined.addAll(completed);

        this.filtered.setValue(combined);
    }
}
