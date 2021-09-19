package be.kuleuven.elcontador10.fragments.transactions.AllTransactions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

import be.kuleuven.elcontador10.background.model.StakeHolder;

public class ViewModel_AllTransactions extends ViewModel {
    //ChosenTypesOfTransactions

    private final MutableLiveData<HashMap<String,Boolean>> chosenTypesOfTransactions = new MutableLiveData<>();
    public LiveData<HashMap<String,Boolean>> getChosenTypesOfTransactions() {
        return chosenTypesOfTransactions;
    }
    public void setTypesOfTransactions(HashMap<String,Boolean> selectedTypes){
        chosenTypesOfTransactions.setValue(selectedTypes);
    }
    public void reset(){
        chosenTypesOfTransactions.setValue(null);
    }

}
