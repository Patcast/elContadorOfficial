package be.kuleuven.elcontador10.fragments.transactions.NewTransaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import be.kuleuven.elcontador10.background.model.EmojiCategory;
import be.kuleuven.elcontador10.fragments.transactions.Categories.ViewModelCategory;
import be.kuleuven.elcontador10.background.model.ImageFireBase;
import be.kuleuven.elcontador10.background.model.Interfaces.ViewModelCamaraInterface;
import be.kuleuven.elcontador10.background.model.Property;
import be.kuleuven.elcontador10.background.model.StakeHolder;

public class ViewModel_NewTransaction extends ViewModelCategory implements ViewModelCamaraInterface {
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
        chosenProperty.setValue(null);
    }

    //ChosenProperty
    private final MutableLiveData<Property> chosenProperty = new MutableLiveData<>();
    public LiveData<Property> getChosenProperty() {
        return chosenProperty;
    }
    public void selectProperty(Property input_property){
        chosenProperty.setValue(input_property);
    }
    public void resetChosenProperty(){
        chosenProperty.setValue(null);
    }

    //ChosenImage
    private final MutableLiveData<ImageFireBase> chosenImage = new MutableLiveData<>();
    @Override
    public LiveData<ImageFireBase> getChosenImage() {
        return chosenImage;
    }
    @Override
    public void selectImage(ImageFireBase imageInput){
        chosenImage.setValue(imageInput);
    }
    @Override
    public void resetImage(){ chosenImage.setValue(null); }

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

}
