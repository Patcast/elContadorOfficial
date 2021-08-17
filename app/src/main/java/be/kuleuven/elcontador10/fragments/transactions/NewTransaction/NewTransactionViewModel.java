package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.StakeHolder;

public class NewTransactionViewModel extends ViewModel {
    private final MutableLiveData<StakeHolder> chosenStakeholder = new MutableLiveData<>();
    public LiveData<StakeHolder> getChosenStakeholder() {
        return chosenStakeholder;
    }
    public void selectStakeholder(StakeHolder stakeholder){
        chosenStakeholder.setValue(stakeholder);
    }
    public void reset(){
        chosenStakeholder.setValue(null);
    }


    private final MutableLiveData<EmojiCategory> chosenCategory = new MutableLiveData<>();
    public LiveData<EmojiCategory> getChosenCategory() {
        return chosenCategory;
    }
    public void selectCategory(EmojiCategory categoryInput){
        chosenCategory.setValue(categoryInput);
    }
    public void resetCategory(){
        chosenCategory.setValue(null);
    }


}
