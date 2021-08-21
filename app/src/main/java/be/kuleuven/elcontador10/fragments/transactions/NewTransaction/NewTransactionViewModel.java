package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.model.StakeHolder;

public class NewTransactionViewModel extends ViewModel {
    //ChosenStakeholder
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

    //ChosenCategory
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

    //ChosenCategory
    private final MutableLiveData<ImageFireBase> chosenImage = new MutableLiveData<>();
    public LiveData<ImageFireBase> getChosenImage() {
        return chosenImage;
    }
    public void selectImage(ImageFireBase imageInput){
        chosenImage.setValue(imageInput);
    }
    public void resetImage(){ chosenImage.setValue(null); }

}
