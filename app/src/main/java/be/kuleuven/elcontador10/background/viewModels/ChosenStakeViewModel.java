package be.kuleuven.elcontador10.background.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import be.kuleuven.elcontador10.background.model.StakeHolder;

public class ChosenStakeViewModel extends ViewModel {
    private final MutableLiveData<StakeHolder> chosenStakeholder = new MutableLiveData<>();

    public LiveData<StakeHolder> getChosenStakeholder() {
        return chosenStakeholder;
    }
    public void select (StakeHolder stakeholder){
        chosenStakeholder.setValue(stakeholder);
    }
    public void reset(){
        chosenStakeholder.setValue(null);
    }


}
