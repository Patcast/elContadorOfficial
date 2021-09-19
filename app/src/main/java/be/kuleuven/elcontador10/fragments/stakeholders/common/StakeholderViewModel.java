package be.kuleuven.elcontador10.fragments.stakeholders.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StakeholderViewModel extends ViewModel {
    private final MutableLiveData <Boolean> fabClicked = new MutableLiveData<>();

    public LiveData<Boolean> getFabClicked() { return fabClicked; }

    public void setFabClicked(boolean value) {
        fabClicked.setValue(value);
    }
}
