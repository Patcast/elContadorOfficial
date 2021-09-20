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
    private final MutableLiveData<ArrayList<ScheduledTransaction>> data = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLate = new MutableLiveData<>(),
            isFuture = new MutableLiveData<>(), isCompleted = new MutableLiveData<>();

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

    public MutableLiveData<ArrayList<ScheduledTransaction>> getData() {
        return data;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setData(ArrayList<ScheduledTransaction> data) {
        ArrayList<ScheduledTransaction> late, future, completed;

        late = data.stream()
                .filter(e -> e.getAmountPaid() < e.getTotalAmount())
                .filter(e -> e.getDueDate().getSeconds() < Timestamp.now().getSeconds()) // due date smaller than now
                .collect(Collectors.toCollection(ArrayList::new));

        future = data.stream()
                .filter(e -> e.getAmountPaid() < e.getTotalAmount())
                .filter(e -> e.getDueDate().getSeconds() > Timestamp.now().getSeconds()) // due date larger than now
                .collect(Collectors.toCollection(ArrayList::new));

        completed = data.stream()
                .filter(e -> e.getAmountPaid() >= e.getTotalAmount())
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<ScheduledTransaction> combined = new ArrayList<>();
        combined.addAll(late);
        combined.addAll(future);
        combined.addAll(completed);

        this.data.setValue(combined);
    }
}
